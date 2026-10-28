package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.receiver.DailyGitaNotificationReceiver
import com.example.ui.GitaViewModel
import com.example.ui.NavTab
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.CommentaryScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GitaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleNotificationIntent(intent)

        setContent {
            val appTheme by viewModel.appTheme.collectAsState()
            MyApplicationTheme(themeMode = appTheme) {
                MainContent(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val verseId = intent?.getStringExtra(DailyGitaNotificationReceiver.EXTRA_VERSE_ID)
        if (!verseId.isNullOrEmpty()) {
            viewModel.selectVerseById(verseId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(viewModel: GitaViewModel) {
    val currentTab by viewModel.currentNavTab.collectAsState()
    val appTheme by viewModel.appTheme.collectAsState()
    var showThemeMenu by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    // Handle back press: if not on Home/Daily, navigate back to Daily
    BackHandler(enabled = currentTab != NavTab.DAILY) {
        viewModel.setNavTab(NavTab.DAILY)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentTab) {
                            NavTab.DAILY -> "Gita Daily"
                            NavTab.EXPLORE -> "Search & Explore"
                            NavTab.COMMENTARY -> "Verse Commentary"
                            NavTab.SAVED -> "My Bookmarks"
                            NavTab.SETTINGS -> "Settings & Developer"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                actions = {
                    // Quick Theme Switcher in TopAppBar
                    Box {
                        androidx.compose.material3.IconButton(
                            onClick = { showThemeMenu = true },
                            modifier = Modifier.testTag("top_bar_theme_btn")
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = when (appTheme) {
                                    com.example.ui.theme.AppThemeMode.LIGHT -> androidx.compose.material.icons.Icons.Default.LightMode
                                    com.example.ui.theme.AppThemeMode.DARK -> androidx.compose.material.icons.Icons.Default.DarkMode
                                    com.example.ui.theme.AppThemeMode.SPIRITUAL_PAPER -> androidx.compose.material.icons.Icons.Default.AutoStories
                                },
                                contentDescription = "Change Theme",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        androidx.compose.material3.DropdownMenu(
                            expanded = showThemeMenu,
                            onDismissRequest = { showThemeMenu = false }
                        ) {
                            com.example.ui.theme.AppThemeMode.values().forEach { mode ->
                                androidx.compose.material3.DropdownMenuItem(
                                    text = {
                                        Row(
                                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                                Text(
                                                    text = when (mode) {
                                                        com.example.ui.theme.AppThemeMode.LIGHT -> "☀️ "
                                                        com.example.ui.theme.AppThemeMode.DARK -> "🌙 "
                                                        com.example.ui.theme.AppThemeMode.SPIRITUAL_PAPER -> "📜 "
                                                    }
                                                )
                                                Text(
                                                    text = mode.displayName,
                                                    fontWeight = if (appTheme == mode) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                            if (appTheme == mode) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "✓",
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        viewModel.setAppTheme(mode)
                                        showThemeMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("app_top_bar")
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("app_bottom_nav")
            ) {
                // 1. Daily Tab
                NavigationBarItem(
                    selected = currentTab == NavTab.DAILY,
                    onClick = { viewModel.setNavTab(NavTab.DAILY) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavTab.DAILY) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                            contentDescription = "Daily Teaching"
                        )
                    },
                    label = { Text("Daily", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_daily")
                )

                // 2. Explore Tab
                NavigationBarItem(
                    selected = currentTab == NavTab.EXPLORE,
                    onClick = { viewModel.setNavTab(NavTab.EXPLORE) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavTab.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore,
                            contentDescription = "Explore"
                        )
                    },
                    label = { Text("Explore", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_explore")
                )

                // 3. Commentary Tab
                NavigationBarItem(
                    selected = currentTab == NavTab.COMMENTARY,
                    onClick = { viewModel.setNavTab(NavTab.COMMENTARY) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavTab.COMMENTARY) Icons.Filled.AutoStories else Icons.Outlined.AutoStories,
                            contentDescription = "Commentary"
                        )
                    },
                    label = { Text("Commentary", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_commentary")
                )

                // 4. Saved Tab
                NavigationBarItem(
                    selected = currentTab == NavTab.SAVED,
                    onClick = { viewModel.setNavTab(NavTab.SAVED) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavTab.SAVED) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Saved"
                        )
                    },
                    label = { Text("Saved", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_saved")
                )

                // 5. Settings Tab
                NavigationBarItem(
                    selected = currentTab == NavTab.SETTINGS,
                    onClick = { viewModel.setNavTab(NavTab.SETTINGS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    label = { Text("Settings", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_item_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavTab.DAILY -> HomeScreen(viewModel = viewModel)
                NavTab.EXPLORE -> ExploreScreen(viewModel = viewModel)
                NavTab.COMMENTARY -> CommentaryScreen(viewModel = viewModel)
                NavTab.SAVED -> BookmarksScreen(viewModel = viewModel)
                NavTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
