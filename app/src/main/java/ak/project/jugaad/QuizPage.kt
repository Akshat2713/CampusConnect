package ak.project.jugaad

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class Question(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

class QuizPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val beginnerQuestionList = listOf(
            Question(
                question = "What is the primary language used for Android development?",
                options = listOf("Python", "Java", "Ruby", "Swift"),
                correctAnswerIndex = 1
            ),
            Question(
                question = "What file contains the application’s metadata, such as its name, permissions, and components?",
                options = listOf("build.gradle", "MainActivity.java", "AndroidManifest.xml", "strings.xml"),
                correctAnswerIndex = 2
            ),
            Question(
                question = "What is the default layout used when creating a new Android project in Android Studio?",
                options = listOf("RelativeLayout", "LinearLayout", "FrameLayout", "ConstraintLayout"),
                correctAnswerIndex = 3
            ),
            Question(
                question = "What method is used to start a new activity in Android?",
                options = listOf("startIntent()", "startActivity()", "startNewActivity()", "launchActivity()"),
                correctAnswerIndex = 1
            ),
            Question(
                question = "In Android, which component is used to display a scrollable list of items?",
                options = listOf("TextView", "RecyclerView", "ImageView", "Button"),
                correctAnswerIndex = 1
            ),
            Question(
                question = "Which lifecycle method is called when an activity is first created?",
                options = listOf("onStart()", "onResume()", "onCreate()", "onDestroy()"),
                correctAnswerIndex = 2
            ),
            Question(
                question = "What is the purpose of the Gradle build system in Android development?",
                options = listOf("To manage project files", "To compile and build the app", "To handle user interface design", "To test the app"),
                correctAnswerIndex = 1
            ),
            Question(
                question = "Which XML attribute is used to make a view invisible but still take up space in the layout?",
                options = listOf("android:visibility=\"gone\"", "android:visibility=\"hidden\"", "android:visibility=\"invisible\"", "android:visibility=\"collapse\""),
                correctAnswerIndex = 2
            ),
            Question(
                question = "What is the role of the R file in an Android project?",
                options = listOf("It is used to store string resources.", "It is used to reference application resources.", "It contains the app's main logic.", "It handles network operations."),
                correctAnswerIndex = 1
            ),
            Question(
                question = "In Android Studio, which tool is used to preview and design your app’s UI?",
                options = listOf("APK Analyzer", "AVD Manager", "Layout Editor", "Logcat"),
                correctAnswerIndex = 2
            )
        )


        val intermediateQuestionList = listOf(
            Question(
                question = "What is the primary difference between Activity and Fragment in Android?",
                options = listOf("An Activity can host multiple Fragments.", "A Fragment can run independently of an Activity.", "Fragments do not have a lifecycle.", "Activities do not support user interactions."),
                correctAnswerIndex = 0
            ),
            Question(
                question = "Which Jetpack library is used for implementing MVVM architecture in Android apps?",
                options = listOf("Navigation", "WorkManager", "LiveData", "DataStore"),
                correctAnswerIndex = 2
            ),
            Question(
                question = "What is the purpose of the ViewModel class in Android?",
                options = listOf("To handle navigation between activities", "To manage UI-related data in a lifecycle-conscious way", "To create custom views", "To manage database operations"),
                correctAnswerIndex = 1
            ),
            Question(
                question = "What does the Room library in Android Jetpack provide?",
                options = listOf("A caching mechanism for network requests", "An abstraction layer over SQLite for database management", "A mechanism to manage UI state", "A library for real-time communication"),
                correctAnswerIndex = 1
            ),
            Question(
                question = "Which class is used to execute tasks asynchronously in Android?",
                options = listOf("Handler", "Service", "AsyncTask", "ExecutorService"),
                correctAnswerIndex = 3
            ),
            Question(
                question = "How can you ensure a RecyclerView maintains a fixed size?",
                options = listOf("Set the android:fixedSize attribute to true.", "Call setHasFixedSize(true) on the RecyclerView.", "Use a LinearLayoutManager.", "Enable NestedScrolling for the RecyclerView."),
                correctAnswerIndex = 1
            ),
            Question(
                question = "Which method is used to check for permissions in Android at runtime?",
                options = listOf("checkSelfPermission()", "checkPermission()", "requestPermission()", "requestPermissions()"),
                correctAnswerIndex = 0
            ),
            Question(
                question = "How do you define a custom attribute for a custom view in Android?",
                options = listOf("Declare it in the AndroidManifest.xml.", "Add it to a custom style file.", "Define it in a res/values/attrs.xml file.", "Add it directly in the custom view's Java class."),
                correctAnswerIndex = 2
            ),
            Question(
                question = "In Jetpack Compose, which composable function is used to create a scrollable list?",
                options = listOf("LazyColumn", "ScrollView", "Column", "LazyRow"),
                correctAnswerIndex = 0
            ),
            Question(
                question = "What is the main advantage of using WorkManager over AsyncTask or Thread?",
                options = listOf("It supports periodic tasks and can persist through app restarts.", "It has better UI integration.", "It is faster than other threading mechanisms.", "It does not need AndroidManifest configuration."),
                correctAnswerIndex = 0
            )
        )


        val advancedQuestionList = listOf(
            Question(
                question = "Which of the following is true about Android’s ViewModel and its lifecycle behavior?",
                options = listOf(
                    "ViewModel is tied to the activity and gets destroyed when the activity is destroyed.",
                    "ViewModel survives configuration changes like rotation but is destroyed when the app process is killed.",
                    "ViewModel is tied to a fragment and gets destroyed when the fragment is destroyed.",
                    "ViewModel persists across app process restarts, even if the system kills the app process."
                ),
                correctAnswerIndex = 1
            ),
            Question(
                question = "What is the primary benefit of using LiveData in an Android app?",
                options = listOf(
                    "It provides lifecycle-aware data that automatically cleans up when a lifecycle owner is destroyed.",
                    "It is used for offline caching of data from a remote server.",
                    "It stores data persistently across sessions even when the app is closed.",
                    "It directly manages UI updates by itself without any interaction with ViewModels."
                ),
                correctAnswerIndex = 0
            ),
            Question(
                question = "Which of the following best describes the purpose of the WorkManager in Android?",
                options = listOf(
                    "It is used to perform background tasks that require a long-running operation and can be canceled by the user.",
                    "It is a replacement for AsyncTask for performing background operations without blocking the UI thread.",
                    "It is used to schedule background tasks that need to be guaranteed to run, even if the app exits or the device restarts.",
                    "It is designed to handle network-intensive operations while ensuring the app stays connected during background work."
                ),
                correctAnswerIndex = 2
            ),
            Question(
                question = "When using Room for database operations, which of the following is true about @Transaction annotation?",
                options = listOf(
                    "It is used to ensure that all database operations within the annotated method execute as part of a single transaction.",
                    "It automatically ensures the rollback of a transaction in case of an exception, without needing explicit handling.",
                    "It is only needed when using foreign keys in the database schema.",
                    "It is not supported for LiveData queries but works only with plain SQL queries."
                ),
                correctAnswerIndex = 0
            ),
            Question(
                question = "Which of the following describes the correct use of onSaveInstanceState() and onRestoreInstanceState() methods in Android?",
                options = listOf(
                    "onSaveInstanceState() is used to save the state of the app, and onRestoreInstanceState() is called automatically to restore the state during activity recreation.",
                    "onSaveInstanceState() is only used for saving data in the background, and onRestoreInstanceState() is used for restoring UI state only after configuration changes.",
                    "onSaveInstanceState() is not needed for activities that don’t use fragments, and onRestoreInstanceState() is never called for those.",
                    "onSaveInstanceState() must always be paired with onStop() for proper state management."
                ),
                correctAnswerIndex = 0
            ),
            Question(
                question = "In Kotlin, which of the following is correct when using Coroutines with Android's ViewModel to fetch data from a repository?",
                options = listOf(
                    "You should launch a coroutine in the ViewModel using viewModelScope.launch to ensure that it is automatically canceled when the ViewModel is destroyed.",
                    "You should always launch coroutines in MainScope to avoid blocking the UI thread.",
                    "viewModelScope should only be used for network requests and not for database operations.",
                    "Coroutines cannot be used in ViewModel because they are not lifecycle-aware."
                ),
                correctAnswerIndex = 0
            ),
            Question(
                question = "What is the purpose of the ContentProvider in Android?",
                options = listOf(
                    "It is used to manage app settings and configurations.",
                    "It acts as an interface for sharing data between different apps in a secure and standardized way.",
                    "It manages persistent storage and caches app data.",
                    "It is primarily used for performing background network operations."
                ),
                correctAnswerIndex = 1
            ),
            Question(
                question = "Which of the following is true about Android's Navigation Component?",
                options = listOf(
                    "It provides an alternative to Fragments for managing app UI navigation.",
                    "It supports deep linking, simplifying navigation between multiple app components.",
                    "It is only compatible with Jetpack Compose and not XML-based UI.",
                    "It removes the need for Activity and Fragment lifecycle management."
                ),
                correctAnswerIndex = 1
            ),
            Question(
                question = "What is the main difference between HandlerThread and AsyncTask in Android?",
                options = listOf(
                    "HandlerThread is used to perform asynchronous tasks on the UI thread, while AsyncTask is used to handle background operations.",
                    "HandlerThread manages its own looper and allows background tasks to run on a separate thread, while AsyncTask runs on a single background thread and provides easier cancellation.",
                    "AsyncTask is deprecated, while HandlerThread is the recommended solution for background tasks.",
                    "HandlerThread executes tasks sequentially, while AsyncTask executes tasks concurrently."
                ),
                correctAnswerIndex = 1
            ),
            Question(
                question = "When should you use ViewBinding over findViewById() in an Android app?",
                options = listOf(
                    "findViewById() is always better because it is more performance-efficient than ViewBinding.",
                    "ViewBinding is preferred when you need to access views in a fragment or activity, as it ensures null-safety and eliminates boilerplate code.",
                    "findViewById() should always be used in RecyclerView adapters for better flexibility.",
                    "ViewBinding is only suitable for small applications but not recommended for larger projects."
                ),
                correctAnswerIndex = 1
            )
        )


        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                QuizScreen(beginnerQuestionList, intermediateQuestionList, advancedQuestionList)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun QuizScreen(

        beginnerQuestionList: List<Question>,
        intermediateQuestionList: List<Question>,
        advancedQuestionList: List<Question>
    ) {
        var context= LocalContext.current
        var selectedField by remember { mutableStateOf<String?>(null) } // No field selected initially
        var selectedDifficulty by remember { mutableStateOf<String?>(null) } // No difficulty selected initially

        var currentQuestionList by remember { mutableStateOf<List<Question>?>(null) } // Question list to display
        var timerValue by remember { mutableStateOf(300) } // Timer (300 seconds)
        var score by remember { mutableStateOf(0) } // Tracks score
        val selectedAnswers = remember { mutableStateListOf<Int?>() }

        LaunchedEffect(selectedDifficulty, selectedField) {
            // Update question list based on selected difficulty and field
            currentQuestionList = when (selectedDifficulty) {
                "Easy" -> beginnerQuestionList
                "Medium" -> intermediateQuestionList
                "Hard" -> advancedQuestionList
                else -> null
            }
            // Reset state when question list changes
            currentQuestionList?.let {
                selectedAnswers.clear()
                selectedAnswers.addAll(List(it.size) { null })
                timerValue = 300 // Reset timer
            }
        }

        // Timer countdown logic
        LaunchedEffect(currentQuestionList) {
            while (timerValue > 0) {
                delay(1000)
                timerValue--
            }
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Quiz", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(Color.White)
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Dropdown for selecting field
                    Text("Select Field:")
                    DropdownMenuWithLabel(
                        options = listOf("Android Development"),
                        selectedOption = selectedField,
                        onOptionSelected = { selectedField = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons for selecting difficulty level
                    Text("Select Difficulty:")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        Button(onClick = { selectedDifficulty = "Easy" }) {
                            Text("Easy")
                        }
                        Button(onClick = { selectedDifficulty = "Medium" }) {
                            Text("Medium")
                        }
                        Button(onClick = { selectedDifficulty = "Hard" }) {
                            Text("Hard")
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // Display Quiz if question list is loaded
                    currentQuestionList?.let { questionList ->
                        Text("Time Remaining: $timerValue seconds", fontSize = 22.sp, color = Color.Black)

                        Spacer(modifier = Modifier.height(66.dp))

                        // Display Questions
                        questionList.forEachIndexed { questionIndex, question ->
                            Text(
                                text = question.question,
                                fontSize = 20.sp
                            )

                            Spacer(modifier = Modifier.height(26.dp))

                            // Display Options with Radio Buttons
                            question.options.forEachIndexed { optionIndex, option ->
                                RadioButtonWithText(
                                    option = option,
                                    isSelected = selectedAnswers[questionIndex] == optionIndex
                                ) {
                                    selectedAnswers[questionIndex] = optionIndex
                                }
                            }

                            Spacer(modifier = Modifier.height(26.dp))
                        }

                        // Submit Button
                        Button(onClick = {
                            // Calculate score
                            score = questionList.count { question ->
                                val selectedAnswer = selectedAnswers[questionList.indexOf(question)]
                                selectedAnswer == question.correctAnswerIndex
                            }
                            Toast.makeText(context,"Your  score is :$score",Toast.LENGTH_LONG).show()
                            println("Quiz Submitted! Score: $score")
                        }) {
                            Text("Submit")
                        }
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DropdownMenuWithLabel(
        options: List<String>,
        selectedOption: String?,
        onOptionSelected: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedOption ?: "Select",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        Modifier.clickable { expanded = true }
                    )
                },
                label = { Text("Select an option") }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false // Close the menu when an option is selected
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun RadioButtonWithText(option: String, isSelected: Boolean, onSelect: () -> Unit) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = option, fontSize = 16.sp)
        }
    }

}
