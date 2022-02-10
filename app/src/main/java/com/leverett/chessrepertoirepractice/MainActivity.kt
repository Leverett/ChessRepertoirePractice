package com.leverett.chessrepertoirepractice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.leverett.chessrepertoirepractice.utils.makeAccountInfoPopup
import com.leverett.chessrepertoirepractice.utils.setupAccountInfo
import com.leverett.chessrepertoirepractice.utils.setupRepertoireManager
import com.leverett.rules.chess.representation.log


class MainActivity : AppCompatActivity() {

    var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            var task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                account =
                    task.getResult(ApiException::class.java)
                // Signed in successfully, show authenticated UI.
            } catch (e: ApiException) {
                log("signIn", "failure")
            }
        }
    }

    private lateinit var account: GoogleSignInAccount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setupRepertoireManager(applicationContext)
        setupAccountInfo(applicationContext)
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
        signIn()
    }

    private fun signIn() {

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(DriveScopes.DRIVE))
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }
}