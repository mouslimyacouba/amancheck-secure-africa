package com.example.amancheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.amancheck.ui.screens.AlertsScreen
import com.example.amancheck.ui.screens.AnalyzeScreen
import com.example.amancheck.ui.screens.EducationScreen
import com.example.amancheck.ui.screens.HomeScreen
import com.example.amancheck.ui.screens.ReportScreen
import com.example.amancheck.ui.theme.AmanCheckTheme
import com.example.amancheck.ui.viewmodel.MainViewModel
import com.example.amancheck.ui.viewmodel.MainViewModelFactory

enum class AppTab {
    VERIFY,
    REPORT,
    ANALYZE,
    ALERTS,
    EDUCATION
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as AmanCheckApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AmanCheckTheme {
                AmanCheckMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AmanCheckMainApp(viewModel: MainViewModel) {
    var currentTab by remember { mutableStateOf(AppTab.VERIFY) }
    val t by viewModel.translation.collectAsState()
    val alerts by viewModel.alerts.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Verify Tab
                NavigationBarItem(
                    selected = currentTab == AppTab.VERIFY,
                    onClick = { currentTab = AppTab.VERIFY },
                    icon = {
                        Icon(imageVector = Icons.Default.Security, contentDescription = t.verify)
                    },
                    label = { Text(t.verify, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_verify")
                )

                // Report Tab
                NavigationBarItem(
                    selected = currentTab == AppTab.REPORT,
                    onClick = { currentTab = AppTab.REPORT },
                    icon = {
                        Icon(imageVector = Icons.Default.ReportProblem, contentDescription = t.report)
                    },
                    label = { Text(t.report, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.error,
                        selectedTextColor = MaterialTheme.colorScheme.error,
                        indicatorColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_report")
                )

                // Analyze Tab
                NavigationBarItem(
                    selected = currentTab == AppTab.ANALYZE,
                    onClick = { currentTab = AppTab.ANALYZE },
                    icon = {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = t.analyze)
                    },
                    label = { Text(t.analyze, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_analyze")
                )

                // Alerts Tab
                NavigationBarItem(
                    selected = currentTab == AppTab.ALERTS,
                    onClick = { currentTab = AppTab.ALERTS },
                    icon = {
                        if (alerts.isNotEmpty()) {
                            BadgedBox(
                                badge = {
                                    Badge { Text("${alerts.size}") }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Notifications, contentDescription = t.alerts)
                            }
                        } else {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = t.alerts)
                        }
                    },
                    label = { Text(t.alerts, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_alerts")
                )

                // Education Tab
                NavigationBarItem(
                    selected = currentTab == AppTab.EDUCATION,
                    onClick = { currentTab = AppTab.EDUCATION },
                    icon = {
                        Icon(imageVector = Icons.Default.Book, contentDescription = t.education)
                    },
                    label = { Text(t.education, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_education")
                )
            }
        }
    ) { innerPadding ->
        when (currentTab) {
            AppTab.VERIFY -> HomeScreen(
                viewModel = viewModel,
                onNavigateToReport = { currentTab = AppTab.REPORT },
                onNavigateToAnalyze = { currentTab = AppTab.ANALYZE },
                onNavigateToEducation = { currentTab = AppTab.EDUCATION },
                modifier = Modifier.padding(innerPadding)
            )
            AppTab.REPORT -> ReportScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            AppTab.ANALYZE -> AnalyzeScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            AppTab.ALERTS -> AlertsScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
            AppTab.EDUCATION -> EducationScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
