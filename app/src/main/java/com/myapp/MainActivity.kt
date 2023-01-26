package com.myapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.myapp.core.TAG
import com.myapp.ui.theme.MyAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Log.d(TAG, "onCreate")
            MyApp {
                MyAppNavHost(this)
//                MyNetworkStatus()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            (application as MyApplication).container.studentRepository.openWsClient()
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            (application as MyApplication).container.studentRepository.closeWsClient()
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    Log.d("MyApp", "recompose")
    MyAppTheme {
        Surface {
            content()
        }
    }
}

