package ak.project.jugaad

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import androidx.compose.foundation.shape.RoundedCornerShape


data class UserProfile(
    val name: String = "",
    val email: String = "",
    val entryNumber: String = "",
    val branch: String = "",
    val batch: String = "",
    val phoneNumber: String = "",
    val linkedin: String = "",
    val github: String = "",
    val skills: List<Map<String, String>> = emptyList()
)

class ProfilePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val entryNumber = intent.getStringExtra("entryNumber")
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                ProfileScreen(entryNumber)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(entryNumber: String?) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isExpanded by remember { mutableStateOf(false) }

    val currentUser = auth.currentUser

    if (currentUser != null) {
        LaunchedEffect(Unit) {
            try {
                firestore.collection("users")
                    .document(entryNumber!!)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            userProfile = documentSnapshot.toObject<UserProfile>()
                            isLoading = false
                        } else {
                            errorMessage = "User not found"
                            isLoading = false
                        }
                    }
                    .addOnFailureListener { exception ->
                        errorMessage = exception.message
                        isLoading = false
                    }
            } catch (e: Exception) {
                errorMessage = e.message
                isLoading = false
            }
        }
    } else {
        errorMessage = "No user logged in"
        isLoading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.White),
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF0F0F0))
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileImage()
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        CircularProgressIndicator()
                    } else if (errorMessage != null) {
                        Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
                    } else if (userProfile != null) {
                        ProfileHeader(userProfile)
                        Spacer(modifier = Modifier.height(24.dp))

                        ProfileDetailsSection(userProfile)
                        Spacer(modifier = Modifier.height(24.dp))

                        SkillsSection(userProfile, isExpanded, onToggleExpand = { isExpanded = !isExpanded })
                        Spacer(modifier = Modifier.height(24.dp))

                        LinksSection(userProfile)
                    }
                }
            }
        }
    )
}

@Composable
fun ProfileImage() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_person_24),
            contentDescription = "Profile Picture",
            tint = Color.White,
            modifier = Modifier.size(80.dp)
        )
    }
}

@Composable
fun ProfileHeader(userProfile: UserProfile?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(userProfile?.name ?: "Name not available", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(userProfile?.branch ?: "Branch not available", fontSize = 16.sp, color = Color.Gray)
    }
}

@Composable
fun ProfileDetailsSection(userProfile: UserProfile?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        ProfileField(label = "Email", value = userProfile?.email ?: "Not available")
        ProfileField(label = "Entry No", value = userProfile?.entryNumber ?: "Not available")
        ProfileField(label = "Branch", value = userProfile?.branch ?: "Not available")
        ProfileField(label = "Batch", value = userProfile?.batch ?: "Not available")
        ProfileField(label = "Phone", value = userProfile?.phoneNumber ?: "Not available")
    }
}

@Composable
fun SkillsSection(userProfile: UserProfile?, isExpanded: Boolean, onToggleExpand: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(text = "Skills", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        if (userProfile?.skills.isNullOrEmpty()) {
            Text(text = "No skills available", fontSize = 16.sp, color = Color.Gray)
        } else {
            val skillsToShow = if (isExpanded) userProfile!!.skills else userProfile!!.skills.take(3)
            skillsToShow.forEach { skill ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = skill["skill"] ?: "Skill not available",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Proficiency: ${skill["proficiency"] ?: "Not specified"}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Experience: ${skill["experience"] ?: "Not specified"}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (!isExpanded && userProfile.skills.size > 3) {
                Text(
                    text = "Show more...",
                    color = Color.Blue,
                    modifier = Modifier.clickable { onToggleExpand() }
                )
            }
        }
    }
}

@Composable
fun LinksSection(userProfile: UserProfile?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        ProfileField(label = "LinkedIn", value = userProfile?.linkedin ?: "Not available")
        ProfileField(label = "GitHub", value = userProfile?.github ?: "Not available")
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.Black)
    }
}


@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen("22bcs011")
}
