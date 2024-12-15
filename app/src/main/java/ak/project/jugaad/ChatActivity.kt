package ak.project.jugaad

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "chat_list") {
        composable("chat_list") {
            ChatAppUI(navController = navController)
        }
        composable("chat_screen/{chatId}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatScreen(chatId = chatId)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppUI(navController: NavController) {
    Scaffold(
        topBar = { ChatTopBar() } // Adding the Top Bar
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Background color for the entire screen
                .padding(paddingValues) // Applying the scaffold's padding
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Enable vertical scrolling
        ) {
            // Dummy data for chat items (You can replace this with Firebase data)
            val chats = listOf(
                ChatData("Akshat Kumar", "Hey! How are you?", R.drawable.baseline_person_24, "chat1"),
                ChatData("Aditya Singh", "Are you coming today?", R.drawable.baseline_person_24, "chat2"),
                ChatData("Abhisek", "Let's catch up soon.", R.drawable.baseline_person_24, "chat3"),
                ChatData("Aryan Jha", "Meeting postponed to 3 PM.", R.drawable.baseline_person_24, "chat4")
            )

            // Display each chat item with a separator
            chats.forEachIndexed { index, chat ->
                ChatItem(chatData = chat) {
                    // Navigate to the in-chat screen
                    navController.navigate("chat_screen/${chat.chatId}")
                }

                // Add a separator between the chat items (except for the last item)
                if (index < chats.size - 1) {
                    Divider(color = Color.Black, thickness = 1.dp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Chat",
                color = Color.White,
                fontSize = 25.sp
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.smvdu_logo),
                contentDescription = "Organization Logo",
                modifier = Modifier
                    .size(60.dp)
                    .padding(4.dp)
            )
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color(0xFF0288D1), // Blue color for the action bar
            titleContentColor = Color.White
        )
    )
}

@Composable
fun ChatItem(chatData: ChatData, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // Navigate to in-chat UI
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image
        Image(
            painter = painterResource(id = chatData.profileImage),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Chat Name and Last Message
        Column {
            Text(
                text = chatData.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = chatData.lastMessage,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

// Data model for chat item
data class ChatData(
    val name: String,
    val lastMessage: String,
    val profileImage: Int,
    val chatId: String // Unique ID for chat
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatId: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF0288D1))
            )
        }
    ) {
        var messages by remember { mutableStateOf<List<Message>>(emptyList()) }

        // Fetching messages from Firebase Realtime Database
        LaunchedEffect(chatId) {
            fetchMessagesFromRealtimeDatabase(chatId) { fetchedMessages ->
                messages = fetchedMessages
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Messages List
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
            }

            // Input Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var textState by remember { mutableStateOf("") }
                TextField(
                    value = textState,
                    onValueChange = { textState = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message...") }
                )
                IconButton(onClick = {
                    sendMessage(chatId, textState)
                    textState = "" // Clear text after sending
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = "Send"
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (message.sender == "self") Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Text(
            text = message.text,
            modifier = Modifier
                .background(
                    if (message.sender == "self") Color(0xFF0288D1) else Color.LightGray,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(8.dp),
            color = if (message.sender == "self") Color.White else Color.Black
        )
    }
}

// Data model for message
data class Message(val text: String, val sender: String)

fun fetchMessagesFromRealtimeDatabase(chatId: String, onMessagesFetched: (List<Message>) -> Unit) {
    val database = FirebaseDatabase.getInstance()
    val messagesRef = database.getReference("chats/$chatId/messages")

    // Listen for changes to the messages
    messagesRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val messagesList = mutableListOf<Message>()
            for (messageSnapshot in snapshot.children) {
                val message = messageSnapshot.getValue(Message::class.java)
                message?.let { messagesList.add(it) }
            }
            onMessagesFetched(messagesList) // Return the fetched messages
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle the error and log it
            Log.e("FirebaseError", "Failed to fetch messages: ${error.message}")
        }
    })
}

fun sendMessage(chatId: String, messageText: String) {
    val database = FirebaseDatabase.getInstance()
    val messagesRef = database.getReference("chats/$chatId/messages")

    // Send message to Firebase Realtime Database
    val newMessage = Message(messageText, sender = "self")
    messagesRef.push().setValue(newMessage).addOnSuccessListener {
        Log.d("Firebase", "Message sent successfully")
    }.addOnFailureListener { exception ->
        Log.e("FirebaseError", "Failed to send message: ${exception.message}")
    }
}
