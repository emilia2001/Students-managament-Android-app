package com.ilazar.myapp.todo.ui.map

import android.app.Application
import android.util.Log
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key.Companion.D
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MyLocation() {
    val myNewtworkStatusViewModel = viewModel<MyLocationViewModel>(
        factory = MyLocationViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )
    val location = myNewtworkStatusViewModel.uiState
    Log.d("Location", location.toString())
    if (location != null) {
        MyMap(location.latitude, location.longitude)
    } else {
        LinearProgressIndicator()
    }
}
