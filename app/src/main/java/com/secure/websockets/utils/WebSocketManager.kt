package com.secure.websockets.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class WebSocketManager {

    private val client = HttpClient(CIO) {
        install(WebSockets)
    }
    private var webSocketsSession: WebSocketSession? = null

    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages = _messages.asStateFlow()

    suspend fun connect(roomId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                webSocketsSession = client.webSocketSession {
                    url("ws://192.168.1.9:8000/ws/${roomId}")
                }
                _messages.update {
                    emptyList()
                }
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    suspend fun sendMessage(msg: String) {
        withContext(Dispatchers.IO) {
            webSocketsSession?.send(msg)
        }
    }

    suspend fun listenForMessages() {
        withContext(Dispatchers.IO) {
            webSocketsSession?.incoming?.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val message = frame.readText()
                    val msgs = messages.value.toMutableList()
                    msgs.add(message)
                    _messages.update {
                        msgs
                    }
                }
            }
        }
    }

}