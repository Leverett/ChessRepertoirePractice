package com.leverett.chessrepertoirepractice.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.lines.Book
import com.leverett.repertoire.chess.lines.LineTree
import com.leverett.repertoire.chess.pgn.getFullRepertoire
import com.leverett.repertoire.chess.pgn.makeLineTreeText
import com.leverett.repertoire.chess.pgn.parseAnnotatedPgnToBook
import com.leverett.repertoire.chess.pgn.parseAnnotatedPgnToChapter
import com.leverett.repertoire.chess.settings.Configuration
import com.leverett.repertoire.chess.settings.PlaySettings
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedInputStream
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

private const val REPERTOIRE_DIR_NAME = "Repertoire"
private const val CONFIGURATIONS_FILE_NAME = "Configurations.json"
private const val CURRENT_CONFIGURATION_FILE_NAME = "CurrentConfiguration.txt"
private const val ACCOUNT_INFO_FILE_NAME = "AccountInfo.txt"

private const val NULL_VALUE = "null"

val gson = Gson()
val repertoireManager = RepertoireManager
val accountInfo = LichessAccountInfo


fun syncRepertoire(context: Context) = runBlocking {
    launch {
        val pgn = getStudies(accountInfo.accountName!!, accountInfo.apiToken!!)
        val books = getFullRepertoire(pgn)
        repertoireManager.syncRepertoire(books)
        storeRepertoire(context)
    }
}

private fun getStudies(accountName: String, apiToken: String, retry: Boolean = false): String {
    val url = URL("https://lichess.org/study/by/$accountName/export.pgn")
    val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
    urlConnection.setRequestProperty("Authorization", "Bearer $apiToken")
    urlConnection.setRequestProperty("Content-Type", "application/x-chess-pgn")
    val inputStream = BufferedInputStream(urlConnection.inputStream)
    val pgnBuilder = StringBuilder()
    var c: Int
    while (inputStream.read().also { c = it } != -1) {
        pgnBuilder.append(c.toChar())
    }
    urlConnection.disconnect()
    val pgn = pgnBuilder.toString()
    if (!retry && pgn.isBlank()) {
        return getStudies(accountName, apiToken, true)
    }
    return pgn
}

fun storeRepertoire(context: Context) {
    try {
        val repertoireDir = File(context.filesDir, REPERTOIRE_DIR_NAME)
        if (!repertoireDir.exists()) {
            repertoireDir.mkdir()
        }
        for (lineTree in repertoireManager.repertoire.lineTrees) {
            val file = File(repertoireDir, lineTreeFileName(lineTree))
            file.createNewFile()

            val writer = FileWriter(file)
            val text = makeLineTreeText(lineTree)
            writer.write(text)
            writer.flush()
            writer.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    storeConfigurations(context)
}

fun storeConfigurations(context: Context) {
    try {
        val repertoireManager = RepertoireManager
        val configurationsFile = File(context.filesDir, CONFIGURATIONS_FILE_NAME)
        val configurationsWriter = FileWriter(configurationsFile)
        configurationsWriter.write(gson.toJson(repertoireManager.configurations))
        configurationsWriter.flush()
        configurationsWriter.close()

        val configurationFile = File(context.filesDir, CURRENT_CONFIGURATION_FILE_NAME)
        val configurationWriter = FileWriter(configurationFile)
        configurationWriter.write(repertoireManager.currentConfigurationName)
        configurationWriter.flush()
        configurationWriter.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun storeLichessAccountInfo(context: Context) {
    try {
        val accountInfo = LichessAccountInfo
        val accountName = accountInfo.accountName
        val apiToken = accountInfo.apiToken
        val accountInfoFile = File(context.filesDir, ACCOUNT_INFO_FILE_NAME)

        if (accountInfo.notSet) {
            accountInfoFile.delete()
        } else {
            val accountInfoWriter = FileWriter(accountInfoFile)
            accountInfoWriter.write(
                if (!accountName.isNullOrBlank()) {
                    accountName
                } else {
                    NULL_VALUE
                }
            )
            accountInfoWriter.write("\n")
            accountInfoWriter.write(
                if (!apiToken.isNullOrBlank()) {
                    apiToken
                } else {
                    NULL_VALUE
                }
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun deleteLineTreeFile(context: Context, lineTree: LineTree) {
    try {
        val repertoireDir = File(context.filesDir, REPERTOIRE_DIR_NAME)
        val file = File(repertoireDir, lineTreeFileName(lineTree))
        file.delete()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun setupRepertoireManager(context: Context) {
    val repertoireManager = RepertoireManager
    val repertoireDir = File(context.filesDir, REPERTOIRE_DIR_NAME)
    if (!repertoireDir.exists()) repertoireDir.mkdir()
    val files = repertoireDir.listFiles()
    if (files != null && files.isEmpty()) {
        Thread{ syncRepertoire(context) }.start()
    } else if (repertoireDir.listFiles() != null){
        for (repertoireFile in repertoireDir.listFiles()!!) {
            try {
                val reader = FileReader(repertoireFile)
                val text = reader.readText()
                reader.close()
                val lineTree =
                    if (repertoireFile.name.contains("book")) parseAnnotatedPgnToBook(text)
                    else parseAnnotatedPgnToChapter(text, null)
                if (lineTree != null) {
                    repertoireManager.repertoire.lineTrees.add(lineTree)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    val configurationsFile = File(context.filesDir, CONFIGURATIONS_FILE_NAME)
    if (configurationsFile.exists()) {
        try {
            val reader = FileReader(configurationsFile)
            val type = object : TypeToken<MutableMap<String, Configuration>>() {}.type
            repertoireManager.configurations = gson.fromJson(reader.readText(), type)
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    val currentConfigurationFile = File(context.filesDir, CURRENT_CONFIGURATION_FILE_NAME)
    if (currentConfigurationFile.exists()) {
        try {
            val reader = FileReader(currentConfigurationFile)
            val configurationName = reader.readText()
            repertoireManager.loadConfiguration(configurationName)
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun setupAccountInfo(context: Context) {
    val accountInfo = LichessAccountInfo
    val accountInfoFile = File(context.filesDir, ACCOUNT_INFO_FILE_NAME)
    if (accountInfoFile.exists()) {
        try {
            val reader = FileReader(accountInfoFile)
            val accountInfoText = reader.readText()
            val accountInfoTokens = accountInfoText.split("\n")
            if (accountInfoTokens.size != 2) {
                throw RuntimeException("Invalid account info in file: $accountInfoText")
            }
            val accountName = accountInfoTokens[0]
            if (accountName.isNotBlank() && accountName != NULL_VALUE) {
                accountInfo.accountName = accountName
            }
            val apiToken = accountInfoTokens[1]
            if (apiToken.isNotBlank() && apiToken != NULL_VALUE) {
                accountInfo.apiToken = apiToken
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun lineTreeFileName(lineTree: LineTree): String {
    val append = if (lineTree is Book) "_book.pgn" else "_chapter.pgn"
    return lineTree.hashCode().toString() + append
}