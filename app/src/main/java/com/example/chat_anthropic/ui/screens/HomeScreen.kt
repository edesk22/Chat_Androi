package com.example.chat_anthropic.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_anthropic.R
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeScreenViewModel = viewModel(),
    anthropicUiState: AnthropicUiState,

){
    when (anthropicUiState) {
//        is AnthropicUiState.Loading -> LoadingScreen(
//            homeViewModel = homeViewModel,
//            modifier = modifier
//        )
        is AnthropicUiState.Success -> ChatScreen(
            homeViewModel = homeViewModel,
            respuesta = anthropicUiState.respuesta,
            modifier = modifier
        )
        is AnthropicUiState.Error -> ErrorScreen(
            homeViewModel = homeViewModel,
            errorMessage ="Error ${anthropicUiState.message}",
            modifier = modifier
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    homeViewModel: HomeScreenViewModel,
    respuesta: List<ChatMessage>,
    modifier: Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Chat Anthropic") },
                navigationIcon = {
                    IconButton(onClick = { /* Acción de retroceso */ }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            ChatInputBar(
                inputText = homeViewModel.inputText,
                onInputChange = { homeViewModel.updateInputText(it)},
                onSendClick = { homeViewModel.sendMessage() }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            reverseLayout = true
        ) {
            itemsIndexed(
                items = respuesta.asReversed(),
                key = { _, message -> message.timestamp }
            ) { _, message ->
                ChatMessageItem(message)
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val alignment = if (message.role == "user") Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Card(
            modifier = Modifier.padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (message.role == "user") "Tú" else "Asistente",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                RichText {
                    Markdown(content = message.content)
                }
            }
        }
    }
}

/**
 * The home screen displaying the loading message.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingScreen(
    homeViewModel: HomeScreenViewModel,
    modifier: Modifier
){
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Chat Anthropic") },
                navigationIcon = {
                    IconButton(onClick = { /* Acción de retroceso */ }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            ChatInputBar(
                inputText = homeViewModel.inputText,
                onInputChange = { homeViewModel.updateInputText(it)},
                onSendClick = { homeViewModel.sendMessage() }
            )
        }
    ) { innerPadding ->
        Image(
            modifier = modifier
                .size(200.dp)
                .fillMaxSize()
                .padding(innerPadding),
            painter = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.loading)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorScreen(
    errorMessage: String,
    homeViewModel: HomeScreenViewModel,
    modifier: Modifier
){
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Chat Anthropic") },
                navigationIcon = {
                    IconButton(onClick = { /* Acción de retroceso */ }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            ChatInputBar(
                inputText = homeViewModel.inputText,
                onInputChange = { homeViewModel.updateInputText(it)},
                onSendClick = { homeViewModel.sendMessage() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .size(200.dp)
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
            )
            Text(text = errorMessage, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        TextField(
            value = inputText,
            onValueChange = onInputChange,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onSendClick) {
            Icon(Icons.Filled.Send, contentDescription = "Enviar")
        }
    }
}