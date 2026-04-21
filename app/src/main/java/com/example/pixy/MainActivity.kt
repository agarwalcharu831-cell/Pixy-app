package com.example.pixy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.pixy.navigation.AppNavGraph
import com.example.pixy.ui.theme.PixyTheme
import com.example.pixy.viewmodel.IssueViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val issueViewModel: IssueViewModel = viewModel()
            val navController = rememberNavController()

            PixyTheme(themeMode = issueViewModel.themeMode) {
                AppNavGraph(
                    navController = navController,
                    issueViewModel = issueViewModel
                )
            }
        }
    }
}