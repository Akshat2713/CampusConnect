package ak.project.jugaad

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.ktx.auth
//import com.google.firebase.Firebase
import kotlinx.coroutines.launch
//import androidx.compose.runtime.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.regex.Pattern


class LandingPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val entryNumber = intent.getStringExtra("entryNumber")

        setContent {
            LandingScreen(entryNumber)
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun LandingScreen(entryNumber: String?) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    // State to hold student profiles and filters
    var studentProfiles by remember { mutableStateOf(listOf<StudentProfile>()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchExpanded by remember { mutableStateOf(false) }
    var selectedSkill by remember { mutableStateOf("") }
    var selectedExperience by remember { mutableStateOf("") }
    var selectedProficiency by remember { mutableStateOf("") }
    var entryNumberQuery by remember { mutableStateOf("") }
    var docId by remember { mutableStateOf<String?>(null) }

    // Dropdown options
    val skillOptions = listOf(
        "Android Development",
        "Web Development",
        "Data Science",
        "Design",
        "Machine Learning",
        "Artificial Intelligence",
        "Cloud Computing",
        "Blockchain",
        "DevOps",
        "Cybersecurity",
        "Game Development",
        "Internet of Things (IoT)",
        "Database Management",
        "UI/UX Design",
        "Embedded Systems",
        "Robotics",
        "Augmented Reality (AR)",
        "Virtual Reality (VR)",
        "Big Data Analytics",
        "Mobile App Testing"
    )

    val proficiencyOptions= listOf("Beginner", "Intermediate", "Advanced", "Expert")
    val experienceOptions = listOf("0-1 years", "1-3 years", "3-5 years", "5+ years")

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val entryNumberPattern = Pattern.compile("\\d{2}[a-zA-Z]{3}\\d{3}")

            val profiles = firestore.collection("users")
                .get()
                .await()
                .mapNotNull { doc ->
                    Log.d("Firestore", "Document ID: ${doc.id}, Data: ${doc.data}")
                    docId = doc.id
                    if (entryNumberPattern.matcher(docId!!).matches() && docId != entryNumber) {
                        val skillsList = doc.get("skills") as? List<*>
                        val skillsString = skillsList?.joinToString(separator = "; ") { skill ->
                            if (skill is Map<*, *>) {
                                "Skill: ${skill["skill"]}, Experience: ${skill["experience"]}, Proficiency: ${skill["proficiency"]}"
                            } else {
                                skill.toString()
                            }
                        } ?: ""
                        StudentProfile(
                            name = doc.getString("name") ?: "",
                            entryNumber = doc.getString("entryNumber") ?: "",
                            skills = skillsString
                        )
                    } else {
                        null
                    }
                }

            studentProfiles = profiles
            Log.d("Firestore", "Profiles fetched: $studentProfiles")
            isLoading = false
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching profiles", e)
            isLoading = false
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(240.dp)
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Menu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DrawerItem("My Profile") {
                        Log.d("Firestore", "clicked $entryNumber")
                        val intent = Intent(context, ProfilePage::class.java)
                        intent.putExtra("entryNumber", entryNumber)  // Use docId
                        context.startActivity(intent)
                    }
                    DrawerItem("Chat") {
                        context.startActivity(Intent(context, ChatActivity::class.java))
                    }
                    DrawerItem("Test Yourself") {
                        context.startActivity(Intent(context, QuizPage::class.java))
                    }
                    DrawerItem("Edit Profile") {
                        val intent = Intent(context, EditPage::class.java)
                        intent.putExtra("entryNumber", entryNumber)  // Use docId
                        context.startActivity(intent)
                    }
                    DrawerItem("About") {
                        context.startActivity(Intent(context, LinksPage::class.java))
                    }
                    DrawerItem("Logout") {
                        // Handle Logout
                         Firebase.auth.signOut()
                         context.startActivity(Intent(context, MainActivity::class.java))
                    }
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.smvdu_logo),
                                    contentDescription = "SMVDU Logo",
                                    modifier = Modifier.size(60.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Campus Connect", fontWeight = FontWeight.Bold)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu Icon",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Gray
                                )
                            }
                        }
                    )
                },
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(paddingValues)
                    ) {
                        // Search bar with expanding filters
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = entryNumberQuery,
                                        onValueChange = { entryNumberQuery = it },
                                        placeholder = { Text("Search students...") },
                                        modifier = Modifier.weight(1f),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = Color.Gray,
                                            unfocusedBorderColor = Color.LightGray
                                        ),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(onClick = { searchExpanded = !searchExpanded }) {
                                        Text("Filters")
                                    }
                                }
                                if (searchExpanded) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    DropdownField(
                                        label = "Skill",
                                        options = skillOptions,
                                        selectedOption = selectedSkill,
                                        onOptionSelected = { selectedSkill = it }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    DropdownField(
                                        label = "Experience",
                                        options = experienceOptions,
                                        selectedOption = selectedExperience,
                                        onOptionSelected = { selectedExperience = it }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    DropdownField(
                                        label = "Proficiency",
                                        options = proficiencyOptions,
                                        selectedOption = selectedProficiency,
                                        onOptionSelected = { selectedProficiency = it }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = {
                                            isLoading = true
                                            scope.launch {
                                                searchProfiles(
                                                    firestore,
                                                    selectedSkill,
                                                    selectedExperience,
                                                    selectedProficiency,
                                                    entryNumberQuery
                                                ) { profiles ->
                                                    studentProfiles = profiles
                                                    isLoading = false
                                                }
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Search")
                                    }
                                }
                            }
                        }

                        // Show loading or profiles
                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                for (profile in studentProfiles) {
                                    ProfileCard(
                                        name = profile.name,
                                        entryNumber = profile.entryNumber,
                                        skills = profile.skills
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    onOptionSelected("") // Clear selection
                    expanded = false
                },
                text = { Text("None") } // Option to deselect
            )
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    text = { Text(option) }
                )
            }
        }
    }
}


suspend fun searchProfiles(
    firestore: FirebaseFirestore,
    skill: String,
    experience: String,
    proficiency: String,
    entryNumber: String,
    onResult: (List<StudentProfile>) -> Unit
) {
    try {
        val results = mutableSetOf<StudentProfile>()

        // Fetch all documents and filter in memory
        val querySnapshot = firestore.collection("users").get().await()

        for (doc in querySnapshot) {
            // Log the skills field to verify its structure
            val skillsField = doc.get("skills")
            Log.d("Firestore", "Skills Field: $skillsField")

            // Handle different types of skillsField
            val skillsList = when (skillsField) {
                is List<*> -> skillsField.filterIsInstance<Map<String, String>>()
                else -> emptyList()
            }
            Log.d("Firestore", "Skills List: $skillsList")

            // Check if the document matches the provided filters
            val matchesSkill = skill.isEmpty() || skillsList.any { it["skill"] == skill }
            val matchesExperience = experience.isEmpty() || skillsList.any { it["experience"] == experience }
            val matchesProficiency = proficiency.isEmpty() || skillsList.any { it["proficiency"] == proficiency }
            val matchesEntryNumber = entryNumber.isEmpty() || doc.getString("entryNumber") == entryNumber

            Log.d("Firestore", "matchesSkill: $matchesSkill, matchesExperience: $matchesExperience, matchesProficiency: $matchesProficiency, matchesEntryNumber: $matchesEntryNumber")

            if (matchesSkill && matchesExperience && matchesProficiency && matchesEntryNumber) {
                val skillsString = skillsList.joinToString(separator = "; ") { map ->
                    map.entries.joinToString { "${it.key}: ${it.value}" }
                }
                results.add(
                    StudentProfile(
                        name = doc.getString("name") ?: "",
                        entryNumber = doc.getString("entryNumber") ?: "",
                        skills = skillsString
                    )
                )
            }
        }

        // Pass the filtered results
        onResult(results.toList())
    } catch (e: Exception) {
        Log.e("Firestore", "Error during search", e)
        onResult(emptyList()) // Return an empty list on failure
    }
}




@Composable
fun DrawerItem(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick, modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text, fontSize = 20.sp, color = Color.Gray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCard(name: String, entryNumber: String, skills: String) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {
            Log.d("Firestore", "clicked $entryNumber")

            val intent = Intent(context, ProfilePage::class.java)
            intent.putExtra("entryNumber", entryNumber) // Pass entryNumber to RegistrationPageTwo
            context.startActivity(intent)
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = entryNumber, fontSize = 14.sp, color = Color.Gray)

            // Parse skills and limit to a maximum of two
            val skillsList = skills.split(";").map { it.trim() }.take(2)
            for (skill in skillsList) {
                Text(text = skill, fontSize = 14.sp, color = Color.DarkGray)
            }
        }
    }
}

data class StudentProfile(
    val name: String = "",
    val entryNumber: String = "",
    val skills: String = ""
)

@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    LandingScreen("22bcs011")
}
