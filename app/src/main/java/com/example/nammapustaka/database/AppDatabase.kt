package com.example.nammapustaka.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nammapustaka.database.dao.BookDao
import com.example.nammapustaka.database.dao.LeaderboardDao
import com.example.nammapustaka.database.dao.ReservationDao
import com.example.nammapustaka.database.dao.ReviewDao
import com.example.nammapustaka.database.dao.TransactionDao
import com.example.nammapustaka.database.dao.UserDao
import com.example.nammapustaka.database.entity.BookEntity
import com.example.nammapustaka.database.entity.LeaderboardEntity
import com.example.nammapustaka.database.entity.ReservationEntity
import com.example.nammapustaka.database.entity.ReviewEntity
import com.example.nammapustaka.database.entity.TransactionEntity
import com.example.nammapustaka.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        BookEntity::class,
        TransactionEntity::class,
        ReviewEntity::class,
        LeaderboardEntity::class,
        ReservationEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao
    abstract fun transactionDao(): TransactionDao
    abstract fun reviewDao(): ReviewDao
    abstract fun leaderboardDao(): LeaderboardDao
    abstract fun reservationDao(): ReservationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nammapustaka_smart.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
