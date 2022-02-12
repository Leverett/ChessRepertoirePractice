package com.leverett.chessrepertoirepractice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.services.drive.DriveScopes
import com.leverett.chessrepertoirepractice.utils.*
import com.leverett.rules.chess.representation.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File


class MainActivity : AppCompatActivity() {

    private var signInResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Signed in successfully, show authenticated UI.
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                account =
                    task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                log("signIn", "failure")
            }
            setupDriveInfo(this)
        }
    }

    private lateinit var account: GoogleSignInAccount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setupRepertoireManager(applicationContext)
        setupLichessAccountInfo(applicationContext)
    }

    fun sandboxModeButton(view: View) {
        val intent = Intent(this, SandboxActivity::class.java)
        startActivity(intent)
    }

    fun accountInfoButton(view: View) {
        makeAccountInfoPopup(applicationContext, layoutInflater, view)
    }

    fun repertoireModeButton(view: View) {
        val intent = Intent(this, RepertoireActivity::class.java)
        startActivity(intent)
    }

    fun practiceModeButton(view: View) {
        val intent = Intent(this, PracticeActivity::class.java)
        startActivity(intent)
    }

    fun signInButton(view: View) {
        signIn(this, signInResultLauncher)
//        setupDriveInfo(this)
    }

    fun testButton(view: View) {
        setupDriveInfo(this)
        val files: MutableList<File> = mutableListOf()
        writeLocalTempFiles(this.applicationContext, files)
        for (file in files) {
            log("testButton", file.name)
        }
    }
}