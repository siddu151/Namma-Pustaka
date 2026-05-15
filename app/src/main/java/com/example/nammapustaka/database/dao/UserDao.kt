package com.example.nammapustaka.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nammapustaka.database.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'STUDENT' ORDER BY name ASC")
    fun observeStudents(): LiveData<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users WHERE role = 'STUDENT'")
    fun observeStudentCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM users WHERE role = 'STUDENT'")
    suspend fun countStudents(): Int

    @Query("SELECT * FROM users WHERE role = 'STUDENT' ORDER BY name")
    suspend fun listStudents(): List<UserEntity>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countAll(): Int
}
