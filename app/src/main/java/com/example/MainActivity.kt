package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.notifications.StudyHubNotificationManager
import com.example.ui.StudyHubViewModel
import com.example.ui.navigation.StudyHubApp

class MainActivity : ComponentActivity() {
    private val viewModel: StudyHubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialTargetRoute = intent?.getStringExtra(StudyHubNotificationManager.EXTRA_TARGET_ROUTE)

        viewModel.initializeNotificationsOnLaunch()

        setContent {
            StudyHubApp(
                viewModel = viewModel,
                initialTargetRoute = initialTargetRoute
            )
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val route = intent.getStringExtra(StudyHubNotificationManager.EXTRA_TARGET_ROUTE)
        if (!route.isNullOrEmpty()) {
            viewModel.onNotificationRouteReceived(route)
        }
    }
}
