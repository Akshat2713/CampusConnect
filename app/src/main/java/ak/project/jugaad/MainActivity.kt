package ak.project.jugaad

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Initialize One Tap Client and Request
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.server_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        // Show the splash screen
        setContent {
            SplashScreen()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            setContent {
                WelcomeScreen { signInWithGoogle() }
            }
        } else {
            val intent = Intent(this, LandingPage::class.java)
            intent.putExtra("entryNumber",user.email!!.substringBefore('@'))
            startActivity(intent)
            finish()
        }
    }

    private fun signInWithGoogle() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender,
                        RC_GOOGLE_SIGN_IN,
                        null,
                        0,
                        0,
                        0
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error in signing in", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this, "Error in signing in", Toast.LENGTH_LONG).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            try {
                val credential: SignInCredential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                val email = credential.id
                if (idToken != null) {
                    if (email.endsWith("@smvdu.ac.in")) {
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "signInWithCredential:success")
                                    val user = auth.currentUser
                                    storeUserData(user)
                                    val intent = Intent(this, RegistrationPageOne::class.java)
                                    startActivity(intent)
//                                    finish()
                                } else {
                                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Please use your @smvdu.ac.in email.", Toast.LENGTH_LONG).show()
                        auth.signOut()
                    }
                } else {
                    Log.d(TAG, "No ID token!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
//                Toast.makeText(this, "Error in signing in", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun storeUserData(user: FirebaseUser?) {
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val userData = mapOf(
                "name" to (user.displayName ?: "No Name"),
                "email" to user.email,
                "uid" to user.uid
            )
            db.collection("users").document(user.uid)
                .set(userData)
                .addOnSuccessListener {
                    Log.d(TAG, "User data successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error writing user data", e)
                }
        }
    }

    companion object {
        const val RC_GOOGLE_SIGN_IN = 1001
        const val TAG = "GoogleSignIn"
    }
}

@Composable
fun SplashScreen() {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1500) // Animation for 3 seconds
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.smvdu_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(160.dp)
                .scale(scale),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun WelcomeScreen(onGoogleSignInClick: () -> Unit) {
    val context = LocalContext.current

    var scaleState by remember { mutableStateOf(0f) }
    val scale by animateFloatAsState(
        targetValue = scaleState,
        animationSpec = tween(durationMillis = 1500) // 1.5-second animation
    )

    LaunchedEffect(key1 = true) {
        scaleState = 1f // Scales the image to normal size
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.smvdu_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(160.dp)
                    .scale(scale), // Apply scale modifier here
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Welcome",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onGoogleSignInClick,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .height(50.dp)
                    .width(280.dp),
                shape = CircleShape,
                elevation = ButtonDefaults.buttonElevation(8.dp),
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Sign in with Google", color = Color.Black, fontSize = 16.sp)
                }
            }
        }
    }
}
@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}
