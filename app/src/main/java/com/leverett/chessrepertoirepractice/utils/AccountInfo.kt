package com.leverett.chessrepertoirepractice.utils

object AccountInfo {
    var accountName: String? = null
    var apiToken: String? = null

    val incompleteInfo: Boolean
        get() = accountName.isNullOrBlank() || apiToken.isNullOrBlank()
    val notSet: Boolean
        get() = accountName.isNullOrBlank() && apiToken.isNullOrBlank()
}