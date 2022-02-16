package com.leverett.chessrepertoirepractice.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.lines.Book
import com.leverett.repertoire.chess.lines.LineTree
import com.leverett.repertoire.chess.pgn.*
import com.leverett.repertoire.chess.settings.Configuration
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception
import java.lang.RuntimeException

private const val REPERTOIRE_DIR_NAME = "Repertoire"
const val CONFIGURATIONS_FILE_NAME = "Configurations.json"
private const val CURRENT_CONFIGURATION_FILE_NAME = "CurrentConfiguration.txt"
private const val ACCOUNT_INFO_FILE_NAME = "AccountInfo.txt"

private const val TEMP_DIR_NAME = "temp"

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
                if (text.isNullOrBlank()) {
                    repertoireFile.delete()
                } else {
                    val lineTree =
                        if (repertoireFile.name.contains("book")) parseAnnotatedPgnToBook(text)
                        else parseAnnotatedPgnToChapter(text, null)
                    if (lineTree != null) {
                        repertoireManager.repertoire.lineTrees.add(lineTree)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    loadConfigurations(context)
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

fun loadConfigurations(context: Context) {
    val configurationsFile = configurationsFile(context)
    if (configurationsFile.exists()) {
        try {
            val reader = FileReader(configurationsFile)
            val type = object : TypeToken<MutableMap<String, Configuration>>() {}.type
            val repertoireManager = RepertoireManager
            repertoireManager.configurations = gson.fromJson(reader.readText(), type)
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun configurationsFile(context: Context): File {
    return File(context.filesDir, CONFIGURATIONS_FILE_NAME)
}

fun setupLichessAccountInfo(context: Context) {
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

fun storeTempoPgnFile(context: Context, configuration: Configuration): File {
    val color = if (configuration.color) {"white"} else {"black"}
    val name = "${configuration.name}_$color.pgn"
    val pgn = makeRepertoirePgnForConfiguration(configuration)
    val tempDir = File(context.filesDir, TEMP_DIR_NAME)
    if (!tempDir.exists()) {
        tempDir.mkdir()
    }
    val file = File(tempDir, name)
    file.writeText(pgn)
    return file
}

private fun lineTreeFileName(lineTree: LineTree): String {
    val append = if (lineTree is Book) "_book.pgn" else "_chapter.pgn"
    return lineTree.hashCode().toString() + append
}