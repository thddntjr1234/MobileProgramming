package com.wooseok.mytodolist

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.wooseok.mytodolist.sub.Todo
import com.wooseok.mytodolist.sub.TodoViewModel
import kotlinx.android.synthetic.main.item_todo.view.*

class MyAdapter(val context: Context,
                var itemList: MutableList<Todo>,
                val viewModel: TodoViewModel,
                val goToDetailListener : (Todo, Int) -> Unit,
                val setList: () -> Unit
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_todo,parent,false)

        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // itemList에서 현재 포지션에 해당하는 아이템을 todo객체에 담음
        val todo = itemList[position]
        holder.todoText.text = todo.text

        
        // todo객체의 isDone(boolean)을 ischecked에 연결
        holder.todoIsDone.isChecked = todo.isDone
        
        // todo가 체크된 상태라면 todo_text를 완료 처리하는 스타일을 지정해줌
        if (todo.isDone) {
            holder.todoText.apply {
                setTextColor(Color.GRAY)
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                setTypeface(null, Typeface.ITALIC)
            }
        } else {
            // 완료상태가 아니라면 스타일 복구
            holder.todoText.apply {
                setTextColor(Color.BLACK)
                paintFlags = 0
                setTypeface(null, Typeface.NORMAL)
            }
        }
        
        // CheckBox인 todoIsdone이 체크되었을 때
        holder.todoIsDone.apply {
            setOnClickListener {
                //변경이 완료되었으므로 다시 todoList를 받고
                // liveData.value에 넣어주는 setList메서드를 실행하여 적용한다
                todo.isDone = this.isChecked
                viewModel.update(todo)
                setList()
            }
        }

        //원하는 todo를 클릭하면 text를 감싸는 todoInfo이 클릭됨, 리스너 실행하여 DetailActivity 실행
        holder.todoInfo.setOnClickListener {
            goToDetailListener(todo, position)
        }

        // DeatilActivity에서 시간을 설정한적이 있는 경우 todo의 날짜와 시간이 표시됨.
        // 설정하지 않으면 원 상태 유지
        if (todo.time != null && todo.date != null) {
            holder.todoTime.apply {
                text = "${todo.date} ${todo.time}"
                visibility = View.VISIBLE
            }
        } else {
            holder.todoTime.apply {
                text = ""
                visibility = View.GONE
            }
        }

        //x버튼 클릭시 리스너, 삭제 확인후 삭제루틴 진행
        holder.todoDelete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)
                .setMessage("정말 삭제하시겠습니까?")
                .setPositiveButton("삭제") {str, dialogInterface ->
                    val todo = itemList[position]
                    //뷰모델 livedata 변경 위해 삭제한 아이템에 대해 viewmodel에서 지우고 변경 알림
                    viewModel.delete(todo)
                    setList()
                }
                .setNegativeButton("취소",null)
            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val todoInfo = itemView.todo_info
        val todoText = itemView.todo_text
        val todoTime = itemView.todo_time
        val todoDelete = itemView.todo_delete
        val todoIsDone: CheckBox = itemView.todo_done
    }
}