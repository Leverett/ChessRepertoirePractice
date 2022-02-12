package com.leverett.chessrepertoirepractice.utils

import java.io.BufferedInputStream
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL


fun getStudies(accountName: String, apiToken: String, retry: Boolean = false): String {
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

object LichessAccountInfo {
    var accountName: String? = "CircleBreaker"
    var apiToken: String? = "lip_jmKrmN5dH54qH1nYP73r"
//    var accountName: String? = null
//    var apiToken: String? = null

    val incompleteInfo: Boolean
        get() = accountName.isNullOrBlank() || apiToken.isNullOrBlank()
    val notSet: Boolean
        get() = accountName.isNullOrBlank() && apiToken.isNullOrBlank()
}