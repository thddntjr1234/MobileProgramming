package com.wooseok.mytodolist

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.wooseok.mytodolist.sub.Todo
import kotlinx.android.synthetic.main.activity_detail.*
import java.util.*

class DetailActivity : AppCompatActivity() {
    lateinit var todo: Todo

    lateinit var time: String
    lateinit var date: String

    lateinit var bundle : Bundle

    var timeFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        bundle = intent.getBundleExtra("data")!!

        bundle?.apply {
            todo = getSerializable("todo") as Todo
            detail_todo_input.setText(todo.text)
        }

        // 오늘 년도, 월, 일을 가져오고 시간과 분은 디폴트값 설정
        val calendar = Calendar.getInstance()
        val year = todo.year ?: calendar.get(Calendar.YEAR)
        val month = todo.month ?: calendar.get(Calendar.MONTH)
        val day = todo.day ?: calendar.get(Calendar.DAY_OF_MONTH)
        val hour = todo.hour ?: 9
        val minute = todo.minute ?: 0
        date =  "${year}.${month+1}.$day"
        time = String.format("%02d:%02d",hour,minute)

        detail_todo_date.text = date
        detail_todo_time.text = time

        // 날짜 다이얼로그중 년도 월 일자에 해당
        val datePickerListener =
            DatePickerDialog.OnDateSetListener { view, _year, _month, dayOfMonth ->
                date =  "${_year}.${_month+1}.$dayOfMonth" // 데이터가 보이는 포맷
                detail_todo_date.text = date
                timeFlag = true // 날짜를 변경했다는 표시
                todo.year = _year
                todo.month = _month
                todo.day = dayOfMonth
                todo.date = date
            }

        val datePickerDialog = DatePickerDialog(this, datePickerListener,year,month,day)
        val datePicker = datePickerDialog.datePicker
        //다이얼로그에서 이미 지난 날짜는 표현되지 않도록 선택 가능한 최소 날짜를 현재 시간 기준으로 지정한다.
        datePicker.minDate = System.currentTimeMillis()

        //설정한 다이얼로그를 보여줌
        detail_todo_date.setOnClickListener {
            datePickerDialog.show()
        }

        //날짜 다이얼로그중 시간 분에 해당
        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val strHour = String.format("%02d",hourOfDay)
                val strMin = String.format("%02d",minute)

                time = "$strHour:$strMin" // 데이터가 보이는 포맷
                detail_todo_time.text = time
                timeFlag = true
                todo.hour = hourOfDay
                todo.minute = minute
                todo.time = time
            }


        val timePickerDialog = TimePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog, timePickerListener, 9,0,true)

        detail_todo_time.setOnClickListener {
            timePickerDialog.show()
        }


        // 확인버튼을 눌렀을때
        detail_confirm_btn.setOnClickListener(detailBtnListener)
        // 취소 버튼 눌렀을 때
        detail_cancel_btn.setOnClickListener(detailBtnListener)

    }

    val detailBtnListener = View.OnClickListener { view->

        when (view) {
            // 확인 버튼 클릭시
            detail_confirm_btn -> {
                // 날짜, 시간을 변경한적이 있는 경우
                if (timeFlag) {
                    todo.time = time
                    todo.date = "기한 : " + date
                }
                // 할일
                todo.text = detail_todo_input.text.toString()

                bundle.putSerializable("todo",todo)
                intent.putExtra("data",bundle)
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
            //취소 버튼 클릭시
            detail_cancel_btn -> {
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }
        }

    }
}