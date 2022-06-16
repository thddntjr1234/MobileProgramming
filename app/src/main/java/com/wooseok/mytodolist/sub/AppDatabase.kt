package com.wooseok.mytodolist.sub

import androidx.room.Database
import androidx.room.RoomDatabase

//데이터베이스를 보유할 AppDatabase 클래스를 정의함. 데이터에 대한 기본 액세스 포인트 역할.
@Database(entities = [Todo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}