package com.github.quantakt.anilistclient.presentation.ui.screens.main

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.quantakt.anilistclient.R
import com.github.quantakt.anilistclient.base.AuthState
import com.github.quantakt.anilistclient.presentation.navigation.BottomNavScreen
import com.github.quantakt.anilistclient.presentation.navigation.NavGraph
import com.github.quantakt.anilistclient.presentation.navigation.Screen
import com.github.quantakt.anilistclient.presentation.ui.activities.main.MainActivityViewModel
import com.github.quantakt.anilistclient.presentation.ui.theme.AppTheme
import com.google.accompanist.insets.ui.BottomNavigation
import com.google.accompanist.insets.ui.Scaffold
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun App(onLoginRequest: () -> Unit) {
    val navController = rememberNavController()
    val viewModel: MainActivityViewModel = viewModel()

    // Observe auth state and navigate to login screen when no logged in user is set
    LaunchedEffect(Unit) {
        viewModel.authState.filterNotNull().collectLatest {
            val isLoggedIn = it is AuthState.LoggedIn

            val destination = if (isLoggedIn) {
                Screen.Home.route
            } else {
                Screen.Login.route
            }

            navController.navigate(destination) {
                launchSingleTop = true
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        }
    }

    AppTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            color = MaterialTheme.colors.background
        ) {
            Scaffold(
                modifier = Modifier.statusBarsPadding(),
                content = { paddingValues ->
                    NavGraph(
                        modifier = Modifier.padding(paddingValues),
                        navController = navController,
                        onLoginRequest = onLoginRequest
                    )
                },
                bottomBar = {
                    BottomBar(
                        navController = navController,
                        contentPadding = WindowInsets.navigationBars
                            .only(WindowInsetsSides.Bottom)
                            .asPaddingValues()
                    )
                },
            )
        }
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navController: NavController,
) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val selectedNavItem = backStackEntry?.findScreen()

    // Show bottom nav only when relevant
    if (selectedNavItem != null) {
        BottomNavigation(
            modifier = modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.surface,
            contentPadding = contentPadding,
        ) {
            bottomNavItems.forEach { item ->
                BottomBarItem(item = item, selected = selectedNavItem == item) {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.BottomBarItem(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit = { /* no-op */ },
) {
    BottomNavigationItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = item.icon,
                contentDescription = null
            )
        },
        label = {
            Text(stringResource(item.labelRes))
        }
    )
}

private class NavItem(
    val screen: BottomNavScreen,
    val icon: ImageVector,
    @StringRes val labelRes: Int,
)

private val bottomNavItems = listOf(
    NavItem(
        screen = BottomNavScreen.Home,
        icon = Icons.Default.Home,
        labelRes = R.string.title_home
    ),
    NavItem(
        screen = BottomNavScreen.List,
        icon = Icons.Default.List,
        labelRes = R.string.title_list
    ),
    NavItem(
        screen = BottomNavScreen.Search,
        icon = Icons.Default.Search,
        labelRes = R.string.title_search
    ),
    NavItem(
        screen = BottomNavScreen.Profile,
        icon = Icons.Default.Person,
        labelRes = R.string.title_profile
    ),
)

// mapping of routes to nav items
private val routeToScreen = bottomNavItems.associateBy { it.screen.route }

@Stable
@Composable
private fun NavBackStackEntry.findScreen(): NavItem? {
    val route = destination.hierarchy.find { it.route in routeToScreen }?.route
    return routeToScreen[route]
}