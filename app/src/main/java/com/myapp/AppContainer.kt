package com.myapp.core

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import com.myapp.MyAppDatabase
import com.myapp.auth.data.AuthRepository
import com.myapp.auth.data.remote.AuthDataSource
import com.myapp.core.data.UserPreferencesRepository
import com.myapp.core.data.remote.Api
import com.myapp.todo.data.StudentRepository
import com.myapp.todo.data.remote.StudentService
import com.myapp.todo.data.remote.StudentWsClient

val Context.userPreferencesDataStore by preferencesDataStore(
    name = "user_preferences"
)

class AppContainer(val context: Context) {
    init {
        Log.d(TAG, "init")
    }

    private val studentService: StudentService = Api.retrofit.create(StudentService::class.java)
    private val studentWsClient: StudentWsClient = StudentWsClient(Api.okHttpClient)
    private val authDataSource: AuthDataSource = AuthDataSource()

    private val database: MyAppDatabase by lazy { MyAppDatabase.getDatabase(context) }

    val studentRepository: StudentRepository by lazy {
        StudentRepository(studentService, studentWsClient, database.studentDao())
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(authDataSource)
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.userPreferencesDataStore)
    }
}
