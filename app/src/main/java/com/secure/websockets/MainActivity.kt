package com.secure.websockets

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.secure.websockets.ui.theme.WebSocketsTheme
import com.secure.websockets.utils.WebSocketManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val webSocketManager = WebSocketManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var text by rememberSaveable {
                mutableStateOf("")
            }
            val state by webSocketManager.messages.collectAsState()
//            val state = listOf<String>()
            val coroutineScope = rememberCoroutineScope()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                var roomId by remember {
                    mutableStateOf("")
                }
                TextField(
                    value = roomId,
                    onValueChange = {
                        roomId = it
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = {
                        Text(text = "Room Id")
                    }
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        coroutineScope.launch {
                            val result = webSocketManager.connect(roomId)
                            if (result) {
                                Toast.makeText(this@MainActivity, "Connected", Toast.LENGTH_SHORT)
                                    .show()
                                webSocketManager.listenForMessages()
                            }
                        }
                    }
                ) {
                    Text(text = "Connect")
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(vertical = 10.dp)
                ) {
                    items(state) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                colorResource(id = R.color.white)
                            ),
                            shape = RoundedCornerShape(4.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextField(
                        value = text, onValueChange = {
                            text = it
                        },
                        modifier = Modifier
                            .fillMaxWidth(1f),
                        placeholder = {
                            Text(text = "Message")
                        }
                    )
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                webSocketManager.sendMessage(text)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(text = "Send")
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WebSocketsTheme {
        Greeting("Android")
    }
}