package com.myapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.myapp.todo.data.Student
import com.myapp.todo.data.local.StudentDao

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE students "
                    + "ADD COLUMN address TEXT"
        )
    }
}

@Database(entities = arrayOf(Student::class), version = 2)
abstract class MyAppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: MyAppDatabase? = null

        fun getDatabase(context: Context): MyAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    MyAppDatabase::class.java,
                    "app_database"
                )
//                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
