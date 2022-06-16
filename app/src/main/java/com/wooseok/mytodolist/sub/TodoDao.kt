package com.wooseok.mytodolist.sub

import androidx.room.*

@Dao
interface TodoDao {
    //Room 라이브러리는 엔티티와 dao를 만들어야 함(데이터베이스에 접근하는 객체를 별도로 만들기 위해)

    //등록시간 순서로 order by하여 전체 선택
    @Query("select * from Todo order by registerTime desc")
    fun getAllWriteTimeOrder() : MutableList<Todo>

    //date, time 순서로 order by하여 전체 선택, 둘 다 리스트로 반환(전체를 가져오기 때문)
    //날짜순으로 가져오는 것은 먼저 시간별로 내림차순하고 다시 일자별로 내림차순하여 기한이 많이 남은것이 상단, 기한이 얼마 남지 않은 것이
    //하단으로 위치
    @Query("select * from (select * from todo order by time desc) order by date desc")
    fun getAllTimeOrder() : MutableList<Todo>

    //입력받은 text에 해당하는 데이터를 모두 선택(동일하게 검색 대상의 데이터를 리스트로 반환)
    //날짜별로 가져오는 부분과 동일
    @Query("select * from (select * from todo where text like '%' ||:text || '%' order by time desc) order by date desc")
    fun getTodosByText(text: String) : MutableList<Todo>

    @Insert
    fun insert(todo: Todo)

    @Update
    fun update(todo: Todo)

    @Delete
    fun delete(todo: Todo)

    @Delete
    fun deleteAll(vararg todo: Todo)
}