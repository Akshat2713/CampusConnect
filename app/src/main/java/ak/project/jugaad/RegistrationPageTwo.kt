package ak.project.jugaad

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class RegistrationPageTwo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve entry number from intent
        val entryNumber = intent.getStringExtra("entryNumber")
        setContent {
            RegistrationPageTwoUI(entryNumber)
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationPageTwoUI(entryNumber:String?) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Firebase Firestore instance
    val db = remember { FirebaseFirestore.getInstance() }
//    val auth = FirebaseAuth.getInstance()
//    val currentUser = auth.currentUser

    // State variables
    var skillsList by remember { mutableStateOf(mutableListOf(SkillField())) }
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
        "Cyber Security",
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

    val proficiencyOptions = listOf("Beginner", "Intermediate", "Advanced", "Expert")
    val experienceOptions = listOf("0-1 years", "1-3 years", "3-5 years", "5+ years")

    var linkedinLink by remember { mutableStateOf(TextFieldValue("")) }
    var githubLink by remember { mutableStateOf(TextFieldValue("")) }

    var isLoading by remember { mutableStateOf(false) }

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
            Text(text = "Skills", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            skillsList.forEachIndexed { index, skillField ->
                SkillFieldInput(
                    skillField = skillField,
                    skillOptions = skillOptions,
                    proficiencyOptions = proficiencyOptions,
                    experienceOptions = experienceOptions,
                    onRemove = {
                        skillsList = skillsList.toMutableList().apply {
                            removeAt(index)
                        }
                    },
                    showRemoveButton = (index == skillsList.lastIndex)
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
                        if (!Patterns.WEB_URL.matcher(linkedinLink.text).matches() ||
                            !Patterns.WEB_URL.matcher(githubLink.text).matches()
                        ) {
                            Toast.makeText(
                                context,
                                "Please enter valid URLs for LinkedIn and GitHub!",
                                Toast.LENGTH_LONG
                            ).show()
                            return@StyledButton
                        }

                        if (skillsList.isEmpty() || skillsList.any { it.skill.isEmpty() || it.proficiency.isEmpty() || it.experience.isEmpty() }) {
                            Toast.makeText(
                                context,
                                "Please complete all skill fields before saving!",
                                Toast.LENGTH_LONG
                            ).show()
                            return@StyledButton
                        }

                        val data = hashMapOf(
                            "skills" to skillsList.map {
                                mapOf(
                                    "skill" to it.skill,
                                    "proficiency" to it.proficiency,
                                    "experience" to it.experience
                                )
                            },
                            "linkedin" to linkedinLink.text,
                            "github" to githubLink.text
                        )

                        isLoading = true
                        db.collection("users").document(entryNumber!!)
                            .update(data,) // Use update to merge data with existing document
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Registration Data Saved Successfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent = Intent(context, LandingPage::class.java)
                                intent.putExtra("entryNumber", entryNumber) // Pass entryNumber to RegistrationPageTwo
                                context.startActivity(intent)
                                (context as Activity).finish()

                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Error saving data: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    },
                    text = "Save",
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
fun DropdownMenuField(
    options: List<String>,
    label: String,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(selectedValue) }
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(text = selectedOption)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selectedOption = option
                        onValueChange(option)
                        expanded = false
                    },
                    text = { Text(text = option) }
                )
            }
        }
    }
}

@Composable
fun StyledButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        elevation = ButtonDefaults.buttonElevation(4.dp),
        colors = ButtonDefaults.buttonColors(Color(0xFFD1E231)),
        enabled = enabled
    ) {
        Text(text = text, fontSize = 16.sp, color = Color.DarkGray)
    }
}

@Composable
fun SkillFieldInput(
    skillField: SkillField,
    skillOptions: List<String>,
    proficiencyOptions: List<String>,
    experienceOptions: List<String>,
    onRemove: () -> Unit,
    showRemoveButton: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DropdownMenuField(
                options = skillOptions,
                label = "Skill",
                selectedValue = skillField.skill,
                onValueChange = { skillField.skill = it },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            DropdownMenuField(
                options = proficiencyOptions,
                label = "Proficiency",
                selectedValue = skillField.proficiency,
                onValueChange = { skillField.proficiency = it },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            DropdownMenuField(
                options = experienceOptions,
                label = "Experience",
                selectedValue = skillField.experience,
                onValueChange = { skillField.experience = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (showRemoveButton) {
            Button(
                onClick = onRemove,
                colors = ButtonDefaults.buttonColors(Color.Red),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Remove")
            }
        }
    }
}

data class SkillField(
    var skill: String = "",
    var proficiency: String = "",
    var experience: String = ""
)

@Preview
@Composable
fun RegistrationPageTwoPrev(){
    RegistrationPageTwoUI("22bcs011")
}