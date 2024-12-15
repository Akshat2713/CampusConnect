package ak.project.jugaad

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* // For layout components
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class RegistrationPageOne : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegistrationPage()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationPage() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // State to hold the user's data
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var entryNumber by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }
    var batch by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }  // Added state for phone number

    // Fetch user data from Firestore
    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val fullName = document.getString("name") ?: ""
                        name = fullName.substringBeforeLast(' ')
                        email = document.getString("email") ?: ""
                        entryNumber = extractEntryNumber(email) // Extract Entry Number
                        branch = extractBranch(email)
                        batch = extractBatch(email)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()) // Allows scrolling for smaller devices
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.smvdu_logo),
            contentDescription = "SMVDU Logo",
            modifier = Modifier
                .size(125.dp)
                .align(Alignment.CenterHorizontally),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Campus Connect",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp, bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Name") },
                modifier = Modifier
                    .weight(1f) // Take up remaining space
                    .padding(end = 8.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.baseline_person_24),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = entryNumber,
            onValueChange = { entryNumber = it },
            label = { Text(text = "Entry Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = branch,
            onValueChange = { branch = it },
            label = { Text(text = "Branch") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = batch,
            onValueChange = { batch = it },
            label = { Text(text = "Batch") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phone Number Input Field
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text(text = "Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Confirm and Next Button
        Button(
            onClick = {
                // Handle saving to Firestore
                val currentUser = auth.currentUser    ///// yahan dikkat hai
                if (currentUser != null) {
                    db.collection("users").document(currentUser.toString()).delete()

                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "entryNumber" to entryNumber,
                        "branch" to branch,
                        "batch" to batch,
                        "phoneNumber" to phoneNumber
                    )
                    db.collection("users").document(entryNumber).set(userData, SetOptions.merge())
                        .addOnSuccessListener {
                            Toast.makeText(context, "Data saved successfully", Toast.LENGTH_SHORT).show()
                            // Navigate to next page
                            val intent = Intent(context, RegistrationPageTwo::class.java)
                            intent.putExtra("entryNumber", entryNumber) // Pass uid to RegistrationPageTwo
                            context.startActivity(intent)
                            (context as Activity).finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to save data", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Failed to authenticate user", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .wrapContentSize()
                .height(50.dp),
            elevation = ButtonDefaults.buttonElevation(4.dp),
            colors = ButtonDefaults.buttonColors(Color.White)
        ) {
            Text(text = "Confirm and Next", fontSize = 16.sp, color = Color.DarkGray)
        }
    }
}

// Function to extract Entry Number from email
fun extractEntryNumber(email: String): String {
    return email.substringBefore('@')
}

// Function to extract Branch from email and map it to full name
fun extractBranch(email: String): String {
    val branchAbbreviation = email.substring(2, 5)
    return when (branchAbbreviation) {
        "bcs" -> "Bachelor in Computer Science and Engineering"
        "bba" -> "Bachelor in Business Administration"
        "bme" -> "Bachelor in Mechanical Engineering"
        "bee" -> "Bachelor in Electrical Engineering"
        "bec" -> "Bachelor in Electronics Engineering"
        "bce" -> "Bachelor in Civil Engineering"
        else -> "Unknown Branch"
    }
}

fun extractBatch(email: String): String {
    val batchAbbreviation = email.substring(0, 2)
    return when (batchAbbreviation) {
        "21" -> "2021-25"
        "22" -> "2022-26"
        "23" -> "2023-27"
        "24" -> "2024-28"
        else -> "Unknown Batch"
    }
}

// Preview of the Registration Page
@Preview(showBackground = true)
@Composable
fun PreviewRegistrationPage() {
    RegistrationPage()
}
