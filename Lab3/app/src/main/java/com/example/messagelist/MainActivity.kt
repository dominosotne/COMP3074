package com.example.messagelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.messagelist.ui.theme.MessageListTheme

data class Message(
    val author: String,
    val body: String
)

private val sampleMessages = listOf(
    Message("Joe", "Hi!"),
    Message("Jim", "How are you?"),
    Message("Joe", "Test..1..2...3"),
    Message("Joe", "I hate coding!!!"),
    Message("Joe", "Hi!"),
    Message("Jim", "How are you?"),
    Message("Joe", "Test..1..2...3"),
    Message("Joe", "I hate coding!!!"),
    Message("Joe", "Hi!"),
    Message("Jim", "How are you?"),
    Message("Joe", "Test..1..2...3"),
    Message("Joe", "I hate coding!!!")
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MessageListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFFFF7FF)
                ) {
                    Conversation(sampleMessages)
                }
            }
        }
    }
}

@Composable
fun Conversation(messages: List<Message>) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@Composable
fun MessageCard(message: Message) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {

        // Profile picture
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFE8EE)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message.author.first().toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {

            // Author name
            Text(
                text = message.author,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Message bubble
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFFFF9FF),
                shadowElevation = 1.dp
            ) {

                Text(
                    text = message.body,
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 7.dp
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageListPreview() {

    MessageListTheme {
        Conversation(sampleMessages)
    }
}