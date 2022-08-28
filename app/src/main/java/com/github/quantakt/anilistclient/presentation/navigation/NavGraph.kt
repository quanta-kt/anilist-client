package com.github.quantakt.anilistclient.presentation.navigation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.github.quantakt.anilistclient.presentation.ui.screens.login.LoginScreen
import com.github.quantakt.anilistclient.presentation.ui.screens.main.home.HomeScreen
import com.github.quantakt.anilistclient.presentation.ui.screens.main.list.ListScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login: Screen("login")
}

sealed class BottomNavScreen(route: String) {
    val route = "${Screen.Home.route}/$route"

    object Home : BottomNavScreen("home")
    object List : BottomNavScreen("list")
    object Profile : BottomNavScreen("profile")
    object Search : BottomNavScreen("search")
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onLoginRequest: () -> Unit,
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = Screen.Home.route
    ) {
        addLogin(navController, onLoginRequest)
        addBottomNavScreens()
    }
}

private fun NavGraphBuilder.addBottomNavScreens() {
    navigation(
        route = Screen.Home.route,
        startDestination = BottomNavScreen.Home.route
    ) {
        composable(BottomNavScreen.Home.route) {
            HomeScreen()
        }
        composable(BottomNavScreen.List.route) {
            ListScreen()
        }
        composable(BottomNavScreen.Profile.route) {
            TodoComposable()
        }
        composable(BottomNavScreen.Search.route) {
            TodoComposable()
        }
    }
}

private fun NavGraphBuilder.addLogin(
    navController: NavHostController,
    onLoginRequest: () -> Unit
) {
    composable(Screen.Login.route) {
        LoginScreen(onLoginRequest = onLoginRequest)
    }
}

@Composable
private fun TodoComposable() {
    Text("TODO: Not Implemented")
}