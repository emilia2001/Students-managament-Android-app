package com.myapp

import android.Manifest
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.myapp.auth.LoginScreen
import com.myapp.core.data.remote.Api
import com.myapp.core.ui.UserPreferencesViewModel
import com.myapp.todo.ui.StudentScreen
import com.myapp.todo.ui.map.MyLocation
import com.myapp.todo.ui.students.StudentsScreen
import com.myapp.util.Permissions
import com.myapp.util.createNotificationChannel

val studentsRoute = "students"
val authRoute = "auth"
val mapRoute = "map"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MyAppNavHost(mainActivity: MainActivity) {
    val navController = rememberNavController()
    val onCloseStudent = {
        Log.d("MyAppNavHost", "navigate back to list")
        navController.popBackStack()
    }
    val userPreferencesViewModel =
        viewModel<UserPreferencesViewModel>(factory = UserPreferencesViewModel.Factory)
    val userPreferencesUiState = userPreferencesViewModel.uiState
    val myAppViewModel = viewModel<MyAppViewModel>(factory = MyAppViewModel.Factory)

    val context = LocalContext.current
    val channelId = "MyTestChannel"
    val notificationId = 0

    NavHost(
        navController = navController,
        startDestination = authRoute
    ) {
        composable(studentsRoute) {
            StudentsScreen(
                onStudentClick = { studentId ->
                    Log.d("MyAppNavHost", "navigate to student $studentId")
                    navController.navigate("$studentsRoute/$studentId")
                },
                onAddStudent = {
                    Log.d("MyAppNavHost", "navigate to new student")
                    navController.navigate("$studentsRoute-new")
                },
                onMapClick = {
                    Log.d("MyAppNavHost", "navigate to map page")
                    navController.navigate(mapRoute)
                },
                onLogout = {
                    Log.d("MyAppNavHost", "logout")
                    myAppViewModel.logout()
                    Api.tokenInterceptor.token = null
                    navController.navigate(authRoute) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(
            route = "$studentsRoute/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        )
        {
            StudentScreen(
                studentId = it.arguments?.getString("id"),
                onClose = { onCloseStudent() },
                context = mainActivity
            )
        }
        composable(route = "$studentsRoute-new")
        {
            StudentScreen(
                studentId = null,
                onClose = { onCloseStudent() },
                context = mainActivity
            )
        }
        composable(route = mapRoute)
        {
            Permissions(
                permissions = listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                rationaleText = "Please allow app to use location (coarse or fine)",
                dismissedText = "O noes! No location provider allowed!"
            ) {
                MyLocation()
            }
        }
        composable(route = authRoute)
        {
            LoginScreen(
                onClose = {
                    Log.d("MyAppNavHost", "navigate to list")
                    navController.navigate(studentsRoute)
                }
            )
        }
    }

    LaunchedEffect(userPreferencesUiState.token) {
        createNotificationChannel(channelId, context)
        if (userPreferencesUiState.token.isNotEmpty()) {
            Log.d("MyAppNavHost", "Lauched effect navigate to students")
            Api.tokenInterceptor.token = userPreferencesUiState.token
            myAppViewModel.setToken(userPreferencesUiState.token)
            navController.navigate(studentsRoute) {
                popUpTo(0)
            }
        }
    }
}
