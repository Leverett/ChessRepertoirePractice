package com.leverett.chessrepertoirepractice.utils

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