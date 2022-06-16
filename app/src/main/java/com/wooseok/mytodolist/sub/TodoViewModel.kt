package com.wooseok.mytodolist.sub

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.*

class TodoViewModel(context: Context) : ViewModel() {

    private val todoDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "todo")
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    private val todoDao = todoDatabase.todoDao()
    private val todos = todoDao.getAllWriteTimeOrder()
    val mutableLiveData = MutableLiveData<MutableList<Todo>>(todos)
    var isTimeOrder: Boolean = false

    // 전체 리스트 get
    fun getList(isTimeOrder: Boolean): MutableList<Todo> {
        return if (isTimeOrder) getAllTimeOrder() else getAllWriteTimeOrder()
    }

    // 등록시간 순서
    fun getAllWriteTimeOrder() : MutableList<Todo> {
        return todoDao.getAllWriteTimeOrder()
    }

    // 날짜 순서
    fun getAllTimeOrder() : MutableList<Todo> {
        return todoDao.getAllTimeOrder()
    }

    // 키워드로 검색
    fun getTodosByText(text: String) : MutableList<Todo> {
        return todoDao.getTodosByText(text)
    }

    fun insert(todo: Todo) {
        todoDao.insert(todo)
    }

    fun update(todo: Todo) {
        todoDao.update(todo)
    }

    fun delete(todo: Todo) {
        todoDao.delete(todo)
    }

    fun deleteAll(todo: Array<Todo>){
        todoDao.deleteAll(*todo)
    }



}