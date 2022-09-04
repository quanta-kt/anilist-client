package com.github.quantakt.anilistclient.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.quantakt.anilistclient.domain.entities.MediaType
import com.github.quantakt.anilistclient.presentation.ui.screens.login.LoginScreen
import com.github.quantakt.anilistclient.presentation.ui.screens.main.home.HomeScreen
import com.github.quantakt.anilistclient.presentation.ui.screens.main.list.ListFilter
import com.github.quantakt.anilistclient.presentation.ui.screens.main.list.ListScreen
import com.github.quantakt.anilistclient.presentation.ui.screens.main.media.MediaDetailsScreen
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")

    object MediaDetails : Screen("media/{mediaId}") {
        fun createRoute(mediaId: Int) = "media/$mediaId"

        const val ARG_MEDIA_ID = "mediaId"

        val arguments = listOf(
            navArgument(ARG_MEDIA_ID) {
                type = NavType.IntType
            }
        )
    }
}

sealed class BottomNavScreen(route: String) {
    val route = "${Screen.Home.route}/$route"

    object Home : BottomNavScreen("home")

    object List : BottomNavScreen("list") {

        // Bottom sheet for filtering media list
        object Filter : Screen("${List.route}/filter/{mediaType}") {
            const val ARG_MEDIA_TYPE = "mediaType"

            val arguments = listOf(
                navArgument(ARG_MEDIA_TYPE) {
                    type = NavType.StringType
                }
            )

            fun createRoute(mediaType: MediaType) = "${List.route}/filter/${mediaType.name}"
        }
    }

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
        addBottomNavScreens(navController)
        addMediaDetails(navController)
    }
}

private fun NavGraphBuilder.addBottomNavScreens(navController: NavHostController) {
    navigation(
        route = Screen.Home.route,
        startDestination = BottomNavScreen.Home.route
    ) {

        composable(BottomNavScreen.Home.route) {
            HomeScreen()
        }

        addListScreen(navController)

        composable(BottomNavScreen.Profile.route) {
            TodoComposable()
        }

        composable(BottomNavScreen.Search.route) {
            TodoComposable()
        }
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
private fun NavGraphBuilder.addListScreen(navController: NavHostController) {
    composable(BottomNavScreen.List.route) {
        ListScreen(
            listViewModel = hiltViewModel(),
            openFilter = { mediaType ->
                navController.navigate(BottomNavScreen.List.Filter.createRoute(mediaType))
            },
            openMedia = { mediaId ->
                navController.navigate(Screen.MediaDetails.createRoute(mediaId))
            },
        )
    }

    bottomSheet(
        route = BottomNavScreen.List.Filter.route,
        arguments = BottomNavScreen.List.Filter.arguments,
    ) { backStackEntry ->

        // Intentionally sharing parent's ViewModel to allow bottom sheet apply filters for it's parent
        val parentBackStackEntry = remember {
            navController.previousBackStackEntry
                ?: error("List Filter destination must not be started without a parent")
        }

        val mediaType = MediaType.valueOf(
            backStackEntry.arguments?.getString(BottomNavScreen.List.Filter.ARG_MEDIA_TYPE)
                ?: error("List.Filter destination requires ${BottomNavScreen.List.Filter.ARG_MEDIA_TYPE} argument")
        )

        BottomSheetContainer {
            ListFilter(
                viewModel = hiltViewModel(parentBackStackEntry),
                mediaType = mediaType,
            )
        }
    }
}

private fun NavGraphBuilder.addMediaDetails(navController: NavHostController) {
    composable(
        route = Screen.MediaDetails.route,
        arguments = Screen.MediaDetails.arguments,
    ) {
        MediaDetailsScreen(
            viewModel = hiltViewModel(),
            openMedia = { mediaId ->
                navController.navigate(Screen.MediaDetails.createRoute(mediaId))
            }
        )
    }
}

@Composable
private fun BottomSheetContainer(
    content: @Composable () -> Unit,
) {
    val insets = WindowInsets.safeDrawing.only(
        sides = WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
    )

    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onSurface
    ) {
        Box(modifier = Modifier.windowInsetsPadding(insets)) {
            content()
        }
    }
}

private fun NavGraphBuilder.addLogin(
    navController: NavHostController,
    onLoginRequest: () -> Unit,
) {
    composable(Screen.Login.route) {
        LoginScreen(onLoginRequest = onLoginRequest)
    }
}

@Composable
private fun TodoComposable() {
    Text("TODO: Not Implemented")
}