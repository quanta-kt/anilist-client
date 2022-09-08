package com.github.quantakt.anilistclient.presentation.ui.screens.main

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.quantakt.anilistclient.R
import com.github.quantakt.anilistclient.domain.entities.AuthState
import com.github.quantakt.anilistclient.presentation.navigation.BottomNavScreen
import com.github.quantakt.anilistclient.presentation.navigation.NavGraph
import com.github.quantakt.anilistclient.presentation.navigation.Screen
import com.github.quantakt.anilistclient.presentation.ui.activities.main.MainActivityViewModel
import com.github.quantakt.anilistclient.presentation.ui.theme.AppTheme
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalMaterialNavigationApi::class)
@Composable
fun App(onLoginRequest: () -> Unit) {
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
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
        ModalBottomSheetLayout(
            bottomSheetNavigator = bottomSheetNavigator,
            sheetBackgroundColor = MaterialTheme.colorScheme.surface,
            sheetContentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Scaffold(
                content = { sourcePaddingValues ->

                    val layoutDirection = LocalLayoutDirection.current
                    // Discard top insets
                    val paddingValues = PaddingValues(
                        top = 0.dp,
                        bottom = sourcePaddingValues.calculateBottomPadding(),
                        start = sourcePaddingValues.calculateStartPadding(layoutDirection),
                        end = sourcePaddingValues.calculateEndPadding(layoutDirection),
                    )

                    NavGraph(
                        modifier = Modifier
                            .padding(paddingValues)
                            .consumedWindowInsets(paddingValues)
                            .fillMaxSize(),
                        navController = navController,
                        onLoginRequest = onLoginRequest,
                    )
                },
                bottomBar = {
                    NavBar(
                        navController = navController
                    )
                },
            )
        }
    }
}

@Composable
private fun NavBar(
    modifier: Modifier = Modifier,
    navController: NavController,
) {

    val selectedNavItem = navController.selectedNavItem()

    // Show bottom nav only when relevant
    AnimatedVisibility(
        visible = selectedNavItem != null,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
    ) {
        NavigationBar(modifier = modifier) {
            navItems.forEach { item ->
                NavBarItem(item = item, selected = selectedNavItem == item) {
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
private fun RowScope.NavBarItem(
    item: NavItem,
    selected: Boolean,
    onClick: () -> Unit = { /* no-op */ },
) {
    NavigationBarItem(
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

private val navItems = listOf(
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
private val routeToScreen = navItems.associateBy { it.screen.route }

@Stable
@Composable
private fun NavController.selectedNavItem(): NavItem? {

    // Recompose every time current destination changes
    val backStackEntry by currentBackStackEntryAsState()

    val selectedNavItem = remember(backStackEntry) {
        // Find the last destination in the back stack which is mapped to a bottom nav item
        val destination = backQueue.findLast { it.destination.route in routeToScreen }?.destination
        routeToScreen[destination?.route]
    }

    return selectedNavItem
}