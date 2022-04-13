package com.leverett.chessrepertoirepractice.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.RepertoireManager.DEFAULT_CONFIGURATION_NAMES
import com.leverett.repertoire.chess.lines.Book
import com.leverett.repertoire.chess.lines.Chapter
import com.leverett.repertoire.chess.pgn.makeRepertoirePgnForBook
import com.leverett.repertoire.chess.pgn.makeRepertoirePgnForChapter
import com.leverett.repertoire.chess.pgn.makeRepertoirePgnForConfiguration
import com.leverett.rules.chess.representation.log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.io.FileOutputStream

private const val DRIVE_SPACE = "drive"
private const val DRIVE_FIELDS = "nextPageToken, files(id, name, parents, modifiedTime)"
private const val PLAINTEXT_TYPE = "text/plain"

private const val APP_FOLDER_NAME = "RepertoirePractice"
private const val TEMPO_FOLDER_NAME = "TempoPGNs"
private val driveInfo = DriveInfo


fun signIn(activity: Activity, signInResultLauncher: ActivityResultLauncher<Intent>) = runBlocking {
    val gso = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestScopes(Scope(DriveScopes.DRIVE), Scope(DriveScopes.DRIVE_FILE))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(activity, gso)
    val signInIntent = googleSignInClient.signInIntent
    signInResultLauncher.launch(signInIntent)
}

fun setupDriveInfo(context: Context) = runBlocking {
    driveInfo.googleAccount = GoogleSignIn.getLastSignedInAccount(context)
    driveInfo.googleAccount.let { googleAccount ->
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE)
        )
        credential.selectedAccount = googleAccount!!.account!!
        driveInfo.drive = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).build()

        CoroutineScope(Dispatchers.IO).launch {
            var pageToken: String?
            val configFiles: MutableList<File> = mutableListOf()
            do {
                val result = driveInfo.drive!!.files().list().apply {
                    spaces = DRIVE_SPACE
                    fields = DRIVE_FIELDS
                    pageToken = this.pageToken
                }.execute()
                result.files.forEach { file ->
                    file.modifiedTime
//                    log("FILE", ("name=${file.name} id=${file.id} parents = ${file.parents}"))
                    when (file.name) {
                        APP_FOLDER_NAME -> driveInfo.appFolderId = file.id
                        TEMPO_FOLDER_NAME -> driveInfo.tempoFolderId = file.id
                        CONFIGURATIONS_FILE_NAME -> configFiles.add(file)
                        else -> {}
                    }
                }
            } while (pageToken != null)
            driveInfo.configurationFileId = configFiles.maxByOrNull { it.modifiedTime.value }?.id
        }
    }
}

suspend fun uploadRepertoire(context: Context) {
    if (driveInfo.incomplete) {
        val job = setupDriveInfo(context)
        job.join()
    }
    val tempDir = java.io.File(context.filesDir, TEMP_DIR_NAME)
    if (tempDir.exists()) {
        tempDir.deleteRecursively()
    }
    val tempoDirectory = listOf(driveInfo.tempoFolderId!!)
    writeLocalTempoFiles(context).collect { uploadFile(it, tempoDirectory) }
    val configurationsFile = configurationsFile(context)
    if (!configurationsFile.exists()) {
        storeConfigurations(context)
    }
    uploadFile(configurationsFile, listOf(driveInfo.appFolderId!!))
}

fun writeLocalTempoFiles(context: Context) = flow {
    val repertoireManager = RepertoireManager
    repertoireManager.configurations.values
        .forEach {
            if (!DEFAULT_CONFIGURATION_NAMES.contains(it.name)) {
                val pgn = makeRepertoirePgnForConfiguration(it)
                val tempoFile = makeTempoFile(context, it.name, it.color, pgn)
                emit(tempoFile)
            } else  {
                it.activeRepertoire.forEach { lt ->
                    val lineTree = repertoireManager.repertoire.findLineTreeByName(lt)
                    val pgn = if (lineTree is Book) {
                        makeRepertoirePgnForBook(lineTree, it.color)
                    } else {
                        makeRepertoirePgnForChapter(lineTree as Chapter, it.color)
                    }
                    val tempoFile = makeTempoFile(context, lt, it.color, pgn)
                    emit(tempoFile)
                }
            }
        }
}

fun makeTempoFile(context: Context, name: String, color: Boolean, pgn: String): java.io.File {
    val colorString = if (color) {"white"} else {"black"}
    val safeName = name.replace("/", "-")
    val filename = "${safeName}_$colorString.pgn"
    return storeTempoPgnFile(context, filename, pgn)
}

fun uploadFile(file: java.io.File, parents: List<String>?) {
    CoroutineScope(Dispatchers.IO).launch {
        val gFile = File()
        gFile.name = file.name
        if (!parents.isNullOrEmpty()) {
            gFile.parents = parents
        }
        val fileContent = FileContent(PLAINTEXT_TYPE, file)
        driveInfo.drive!!.files().create(gFile, fileContent).execute()
    }
}

suspend fun downloadConfigurations(context: Context) {
    if (driveInfo.incomplete) {
        val job =
            setupDriveInfo(context)
        job.join()
    }
    if (driveInfo.configurationFileId == null) {
        // TODO: give message
        return
    }
    val localConfigurationsFile = configurationsFile(context)
    val outputStream = FileOutputStream(localConfigurationsFile)
    val downloadJob = CoroutineScope(Dispatchers.IO).launch { driveInfo.drive!!.files().get(driveInfo.configurationFileId).executeMediaAndDownloadTo(outputStream) }
    downloadJob.join()
    outputStream.close()
}


object DriveInfo {
    var googleAccount: GoogleSignInAccount? = null
    var drive: Drive? = null
    val incomplete: Boolean
        get() = googleAccount == null ||
                drive == null ||
                appFolderId == null ||
                tempoFolderId == null
    var appFolderId: String? = null
    var tempoFolderId: String? = null
    var componentFolderId: String? = null
    var configurationFileId: String? = null

}

fun registerSignInResult(activity: ComponentActivity): ActivityResultLauncher<Intent> {
    return activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Signed in successfully, show authenticated UI.
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                driveInfo.googleAccount =
                    task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                log("signIn", "failure")
            }
            setupDriveInfo(activity)
        }
    }
}