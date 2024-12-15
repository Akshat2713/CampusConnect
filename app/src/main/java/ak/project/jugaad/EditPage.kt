package ak.project.jugaad

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class EditPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val entryNumber = intent.getStringExtra("entryNumber")
        setContent {
            EditPageUI(entryNumber = entryNumber)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPageUI(entryNumber: String?) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Firebase Firestore instance
    val db = remember { FirebaseFirestore.getInstance() }

    // State variables
    var skillsList by remember { mutableStateOf(mutableListOf(SkillField())) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var linkedinLink by remember { mutableStateOf(TextFieldValue("")) }
    var githubLink by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }

    // Fetch user data
    LaunchedEffect(entryNumber) {
        isLoading = true
        db.collection("users").document(entryNumber!!)
            .get()
            .addOnSuccessListener { document ->
                document?.let {
                    phoneNumber = TextFieldValue(it.getString("phoneNumber") ?: "")
                    linkedinLink = TextFieldValue(it.getString("linkedin") ?: "")
                    githubLink = TextFieldValue(it.getString("github") ?: "")

                    val skillsField = it.get("skills")
                    Log.d("Firestore", "Skills Field: $skillsField")

                    skillsList = when (skillsField) {
                        is List<*> -> skillsField.filterIsInstance<Map<String, String>>().map { skill ->
                            SkillField(
                                skill = skill["skill"] ?: "",
                                proficiency = skill["proficiency"] ?: "",
                                experience = skill["experience"] ?: ""
                            )
                        }.toMutableList()
                        else -> mutableListOf(SkillField())
                    }
                    Log.d("Firestore", "Skills List: $skillsList")
                }
                isLoading = false
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error fetching data: ${it.message}", Toast.LENGTH_LONG).show()
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.smvdu_logo),
                    contentDescription = "SMVDU Logo",
                    modifier = Modifier.size(50.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Edit Profile", style = MaterialTheme.typography.titleLarge)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Skills", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Log.d("UI", "Skills List in UI: $skillsList") // Log the skills list before rendering

            skillsList.forEachIndexed { index, skillField ->
                SkillFieldInput2(
                    skillField = skillField,
                    skillOptions = listOf(
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
                ,
                    proficiencyOptions = listOf("Beginner", "Intermediate", "Advanced", "Expert"),
                    experienceOptions = listOf("0-1 years", "1-3 years", "3-5 years", "5+ years"),
                    onRemove = {
                        skillsList = skillsList.toMutableList().apply {
                            removeAt(index)
                        }
                    },
                    showRemoveButton = true
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            StyledButton(

                onClick = {
                    skillsList = skillsList.toMutableList().apply {
                        add(SkillField())
                    }
                },
                text = "Add Skill"
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = linkedinLink,
                onValueChange = { linkedinLink = it },
                label = { Text("LinkedIn Profile Link") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = githubLink,
                onValueChange = { githubLink = it },
                label = { Text("GitHub Profile Link") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                StyledButton(
                    onClick = {
                        val updatedData = hashMapOf(
                            "phoneNumber" to phoneNumber.text,
                            "linkedin" to linkedinLink.text,
                            "github" to githubLink.text,
                            "skills" to skillsList.map {
                                mapOf(
                                    "skill" to it.skill,
                                    "proficiency" to it.proficiency,
                                    "experience" to it.experience
                                )
                            }
                        )

                        isLoading = true
                        db.collection("users").document(entryNumber!!)
                            .set(updatedData, SetOptions.merge())
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Profile updated successfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent = Intent(context, LandingPage::class.java)
                                intent.putExtra("entryNumber", entryNumber)
                                context.startActivity(intent)
                                (context as Activity).finish()
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(context, "Error updating data: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    },
                    text = "Save Changes",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading
                )
            }
        }
    }
}
@Composable
fun SkillFieldInput2(
    skillField: SkillField,
    skillOptions: List<String>,
    proficiencyOptions: List<String>,
    experienceOptions: List<String>,
    onRemove: () -> Unit,
    showRemoveButton: Boolean
) {
    Column {
        DropdownMenuField2(
            options = skillOptions,
            label = "Skill",
            selectedValue = skillField.skill, // Fetched value here
            onValueChange = { newValue -> skillField.skill = newValue },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenuField2(
            options = proficiencyOptions,
            label = "Proficiency",
            selectedValue = skillField.proficiency, // Fetched value here
            onValueChange = { newValue -> skillField.proficiency = newValue },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenuField2(
            options = experienceOptions,
            label = "Experience",
            selectedValue = skillField.experience, // Fetched value here
            onValueChange = { newValue -> skillField.experience = newValue },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (showRemoveButton) {
            Button(colors = ButtonDefaults.buttonColors(Color(0xFFFA2A2A)),
                onClick = onRemove,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Remove Skill")
            }
        }
    }
}


@Composable
fun DropdownMenuField2(
    options: List<String>,
    label: String,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(text = selectedValue.ifEmpty { "Select $label" }) // Default text if no value
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(option) // Update the parent state
                        expanded = false
                    },
                    text = { Text(text = option) }
                )
            }
        }
    }
}


@Preview
@Composable
fun EditPageUIPreview() {
    EditPageUI("22bcs011")
}
