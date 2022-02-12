package com.leverett.chessrepertoirepractice.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.pgn.makeRepertoirePgnForConfiguration
import com.leverett.repertoire.chess.settings.Configuration
import com.leverett.rules.chess.representation.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.FileOutputStream
import kotlin.coroutines.CoroutineContext

private const val DRIVE_SPACE = "drive"
private const val DRIVE_FIELDS = "nextPageToken, files(id, name, parents)"

private const val APP_FOLDER_NAME = "RepertoirePractice"
private const val TEMPO_FOLDER_NAME = "TempoPGNs"
private const val CONFIG_FILE_NAME = "configuration.json"


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

fun setupDriveInfo(activity: Activity) {
    val driveInfo = DriveInfo
    driveInfo.googleAccount = GoogleSignIn.getLastSignedInAccount(activity)
    driveInfo.googleAccount.let { googleAccount ->
        val credential = GoogleAccountCredential.usingOAuth2(
            activity, listOf(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE)
        )
        credential.selectedAccount = googleAccount!!.account!!
        val drive: Drive = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).build()
        CoroutineScope(Dispatchers.IO).launch {
            var pageToken: String?
            do {
                val result = drive.files().list().apply {
                    spaces = DRIVE_SPACE
                    fields = DRIVE_FIELDS
                    pageToken = this.pageToken
                }.execute()
                result.files.forEach { file ->
//                    log("FILE", ("name=${file.name} id=${file.id} parents = ${file.parents}"))
                    when (file.name) {
                        APP_FOLDER_NAME -> driveInfo.appFolderId = file.id
                        TEMPO_FOLDER_NAME -> driveInfo.tempoFolderId = file.id
                        CONFIG_FILE_NAME -> driveInfo.configurationFileId = file.id
                        else -> {}
                    }
                }
            } while (pageToken != null)
        }
    }
}


fun writeLocalTempFiles(context: Context, fileList: MutableList<java.io.File>) = runBlocking {
    val repertoireManager = RepertoireManager
    repertoireManager.configurations.values
        .forEach{ launch {
            fileList.add(storeTempPgnFile(context, it))
        }}
}


object DriveInfo {
    var googleAccount: GoogleSignInAccount? = null
    val ready: Boolean
        get() = googleAccount != null &&
                appFolderId != null &&
                tempoFolderId != null
    var appFolderId: String? = null
    var tempoFolderId: String? = null
    var configurationFileId: String? = null

}