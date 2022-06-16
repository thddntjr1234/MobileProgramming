package com.wooseok.mytodolist

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.wooseok.mytodolist.sub.Todo
import com.wooseok.mytodolist.sub.TodoViewModel
import com.wooseok.mytodolist.sub.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var todayAdapter : MyAdapter
    lateinit var viewModel : TodoViewModel
    lateinit var todoList: MutableLiveData<MutableList<Todo>>
    lateinit var time: String
    lateinit var date: String

    companion object {
        const val RC_GO_TO_DETAIL = 1004
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //뷰모델 프로바이더로부터 뷰모델을 받는다
        viewModel = ViewModelProvider(this, ViewModelProviderFactory(this.application))
            .get(TodoViewModel::class.java)

        //리사이클러뷰에 추가할 아이템을 받는다
        todoList = viewModel.mutableLiveData
        todoList.observe(this, Observer {
            todayAdapter.itemList = it
            todayAdapter.notifyDataSetChanged() // 리사이클러뷰의 변동을 알리는 메소드
        })



        todayAdapter = MyAdapter(this, mutableListOf<Todo>(), viewModel, ::goToDetail, ::setList)

        //리사이클러뷰에 레이아웃 매니저를 등록
        today_list.adapter = todayAdapter
        today_list.layoutManager = LinearLayoutManager(this)


        // 버튼 클릭시 todo객체의 생성자에 입력한 텍스트를 넣어 객체 선언
        todo_add.setOnClickListener {
            if (todo_input.text.toString() != "") {
                val todo = Todo(todo_input.text.toString())
                // 뷰모델에서 정의한 service
                viewModel.insert(todo)
                setList()
                todo_input.setText("")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        val menuItem = menu?.findItem(R.id.menu_search)
        val searchView = menuItem?.actionView  as SearchView

        // 검색 기능
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색어 입력 버튼을 눌렀을 때 키워드가 존재한다면
                if (query != "" && query != null) {
                    // 쿼리를 날려 검색어에 맞는 데이터를 가져옴
                    todoList.value = viewModel.getTodosByText(query)
                } else {
                    setList()
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // 검색 닫기를 누르면 이전 TodoList로 복귀
       searchView.setOnCloseListener {
            setList()
            false
        }
        return super.onCreateOptionsMenu(menu)
    }

    // 우상단 메뉴 이벤트를 처리
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            // 등록일 기준 정렬 메소드 호출
            R.id.menu_sort_register -> {
                viewModel.isTimeOrder = false

            }
            // 날짜 기준 정렬 메소드 호출
            R.id.menu_sort_date -> {
                viewModel.isTimeOrder = true
            }
            // 완료한 목록 삭제
            R.id.menu_delete_done -> {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setMessage("완료된 할 일 목록을 전체 지우시겠습니까?")
                    .setNegativeButton("취소", null)
                    .setPositiveButton("확인") { _, _ ->
                        for (todo in todayAdapter.itemList) {
                            if (todo.isDone) {
                                viewModel.delete(todo)
                            }
                        }
                        setList()
                    }
                    .show()
            }
        }
        setList()
        return false
    }

    // RecyclerView의 item을 눌릴 때 상세페이지로 들어가는 기능을 담당하는 함수로 어댑터의 인자로 사용한다
    fun goToDetail(todo: Todo, position: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("todo", todo)
        intent.putExtra("data", bundle)
        intent.putExtra("position", position)
        startActivityForResult(intent, RC_GO_TO_DETAIL)
    }

    // DeatilActivity에서 돌아왔을 때 처리하는 메소드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GO_TO_DETAIL && resultCode == Activity.RESULT_OK) {

            val bundle = data?.getBundleExtra("data")
            val todo = bundle?.getSerializable("todo") as Todo
            viewModel.update(todo)
            setList()
        }
    }

    // 화면을 다시 돌리기 위해서 viewModel 내에 있는 LiveData의 값을 변경하면
    // 값이 변경되면서 옵저버가 실행되어 UI가 변경됨
    fun setList() {
        todoList.value = viewModel.getList(viewModel.isTimeOrder)
    }

}