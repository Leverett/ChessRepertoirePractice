package com.leverett.chessrepertoirepractice.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.lines.Book
import com.leverett.repertoire.chess.lines.LineTree
import com.leverett.repertoire.chess.pgn.makeLineTreeText
import com.leverett.repertoire.chess.pgn.parseAnnotatedPgnToBook
import com.leverett.repertoire.chess.pgn.parseAnnotatedPgnToChapter
import com.leverett.repertoire.chess.settings.PlaySettings
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception

private const val REPERTOIRE_DIR_NAME = "Repertoire"
private const val CONFIGURATIONS_FILE_NAME = "Configurations.json"
private val gson = Gson()

fun setupRepertoireManager(context: Context) {
    val repertoireManager = RepertoireManager
    val repertoireDir = File(context.filesDir, REPERTOIRE_DIR_NAME)
    if (repertoireDir.exists()) {
        try {
            for (repertoireFile in repertoireDir.listFiles()) {
                val reader = FileReader(repertoireFile)
                val text = reader.readText()
                reader.close()
                val lineTree =
                    if (repertoireFile.name.contains("book")) parseAnnotatedPgnToBook(text)
                    else parseAnnotatedPgnToChapter(text, null)
                if (lineTree != null) {
                    repertoireManager.repertoire.lineTrees.add(lineTree)
                }
            }
            repertoireManager.newActiveRepertoire()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    val configurationsFile = File(context.filesDir, CONFIGURATIONS_FILE_NAME)
    if (configurationsFile.exists()) {
        try {
            val reader = FileReader(configurationsFile)
            val type = object : TypeToken<MutableMap<String, Pair<List<String>, PlaySettings>>>() {}.type
            repertoireManager.configurations = gson.fromJson(reader.readText(), type)
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun storeRepertoire(context: Context) {
    try {
        val repertoireManager = RepertoireManager
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
}

fun storeConfigurations(context: Context) {
    try {
        val repertoireManager = RepertoireManager
        val configurationsFile = File(context.filesDir, CONFIGURATIONS_FILE_NAME)
        val writer = FileWriter(configurationsFile)
        writer.write(gson.toJson(repertoireManager.configurations))
        writer.flush()
        writer.close()
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

private fun lineTreeFileName(lineTree: LineTree): String {
    val append = if (lineTree is Book) "_book.pgn" else "_chapter.pgn"
    return lineTree.hashCode().toString() + append
}