package com.ashmit.notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ashmit.notes.Firebase.FirebaseAuthHelper
import com.ashmit.notes.databinding.ActivitySignUpScreenBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignUpScreen : AppCompatActivity() {
    private val binding: ActivitySignUpScreenBinding by lazy {
        ActivitySignUpScreenBinding.inflate(layoutInflater)
    }

    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var repeatedPwd: String
    private val context= this@SignUpScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            // Initializing FirebaseAuth
            auth = FirebaseAuth.getInstance()
            insets
        }

        //setting the clickable link / string
        binding.txtViewLogin.text = ClickableStringBuilder().clickableText("Already have an account , Login?" , 24) {
            startActivity(Intent(this, LoginScreen::class.java))
        }
        binding.txtViewLogin.movementMethod = android.text.method.LinkMovementMethod.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        // Google sign-in button
        googleSignInClient = GoogleSignIn.getClient(context, gso)
        binding.googleSignUpBtn.setOnClickListener {
            val signInClient = googleSignInClient.signInIntent
            launcher.launch(signInClient)
        }

        binding.signUpBtn.setOnClickListener{
            getSignUpInputs()
            if(checkCredentials()){
                FirebaseAuthHelper(this).createUser(email , password, {
                    intentPassing(this, MainActivity::class.java)
                } , {
                    Toast.makeText(this, "verification email bhejne mai dikkat aa rhi h ", Toast.LENGTH_SHORT).show()
                })
            }
        }

    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Successful", Toast.LENGTH_LONG).show()
                        intentPassing( context , MainActivity::class.java)
                    } else {
                        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { exception ->
                    Log.d("FAILED", exception.message.toString())
                }
            } else {
                Log.d("SIGN-IN-FAILED", "Google sign-in task failed: ${task.exception?.message}")
                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
            }
        } else {
            Log.d("SIGN-IN-FAILED", "Result code: ${result.resultCode}")
            Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
        }
    }

    // Getting data from the user
    private fun getSignUpInputs() {
        email = binding.emailId.text.toString()
        password = binding.passwordSignUp.text.toString()
        username = binding.nameSignUp.text.toString()
        repeatedPwd = binding.repeatPwdSignUp.text.toString()
    }

    // This function is used to pass intent
    private fun intentPassing(context: Context, targetActivity: Class<out AppCompatActivity>) {
        startActivity(Intent(context, targetActivity))
    }

    // Checking the user credentials
    private fun checkCredentials(): Boolean {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || repeatedPwd.isEmpty()) {
            Toast.makeText(this, "Please Enter All the Details", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != repeatedPwd) {
            Toast.makeText(this, "Repeat password Must be Same", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun sendVerificationEmail(){
        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener{
            task->
            if(task.isSuccessful){
                Toast.makeText(
                    this, "Verification Email Sent", Toast.LENGTH_SHORT
                ).show()
            }else{
                Toast.makeText(
                    this, "Failed to Send Email", Toast.LENGTH_SHORT
                ).show()
            }
        }?.addOnFailureListener{
            Toast.makeText(
                this, "Failed to Send Email", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun refreshUser(callBack : ()-> Unit){
        auth.currentUser?.reload()
            ?.addOnCompleteListener{
                task->
            if(task.isSuccessful){
                callBack()
            }else{
                Toast.makeText(this, "Failed to refresh user data", Toast.LENGTH_SHORT).show()
                Log.e("User Refresh Failed", task.exception.toString())
            }
        }
    }


}

