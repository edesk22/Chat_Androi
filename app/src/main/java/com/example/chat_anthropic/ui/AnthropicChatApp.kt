package com.example.chat_anthropic.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.chat_anthropic.ui.screens.HomeScreenViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chat_anthropic.ui.screens.HomeScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AnthropicChatApp(
    modifier: Modifier
) {
    val anthropicViewModel: HomeScreenViewModel = viewModel()
    HomeScreen(
        anthropicUiState= anthropicViewModel.anthropicUiState,
        modifier = modifier
    )
}