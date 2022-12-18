package com.ilazar.myapp

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ilazar.myapp.auth.LoginScreen
import com.ilazar.myapp.core.data.remote.Api
import com.ilazar.myapp.core.ui.UserPreferencesViewModel
import com.ilazar.myapp.todo.ui.StudentScreen
import com.ilazar.myapp.todo.ui.students.StudentsScreen

val studentsRoute = "students"
val authRoute = "auth"

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
