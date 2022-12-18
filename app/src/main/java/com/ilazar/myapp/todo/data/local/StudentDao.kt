package com.ilazar.myapp.todo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ilazar.myapp.todo.data.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM Students")
    fun getAll(): Flow<List<Student>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(students: List<Student>)

    @Update
    suspend fun update(student: Student): Int

    @Query("DELETE FROM Students WHERE _id = :id")
    suspend fun deleteById(id: String): Int

    @Query("DELETE FROM Students")
    suspend fun deleteAll()
}