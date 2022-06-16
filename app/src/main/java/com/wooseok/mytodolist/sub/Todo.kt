package com.wooseok.mytodolist.sub

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//room 데이터베이스의 데이터 항목
@Entity
data class Todo(var text: String?, var isDone: Boolean = false) : Serializable {
    // 등록시간 기준으로 정렬시 사용(데이터베이스에 INSERT될 때 시스템 시간이 입력됨)
    @PrimaryKey
    var registerTime : Long = System.currentTimeMillis()

    // 날짜 기준으로 정렬시 사용
    @ColumnInfo
    var time: String? = null
    @ColumnInfo
    var date: String? = null
    @ColumnInfo
    var dateLong: Long? = null


    @ColumnInfo
    var year: Int? = null
    @ColumnInfo
    var month: Int? = null
    @ColumnInfo
    var day: Int? = null
    @ColumnInfo
    var hour: Int? = null
    @ColumnInfo
    var minute: Int? = null

}
