package com.example.todoapp.viewModels

import android.app.Application
import android.app.DatePickerDialog
import android.util.Log
import androidx.lifecycle.*
import com.example.todoapp.database.TaskDatabase
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.database.TaskRepository
import com.example.todoapp.mappers.TaskMapper
import com.example.todoapp.models.SortMode
import com.example.todoapp.models.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import java.util.*

class TaskViewModel(application: Application): AndroidViewModel(application) {

    var readAllTasks: LiveData<List<TaskEntity>>
    private val repository: TaskRepository

    init {
        val taskDao = TaskDatabase.getDatabase(application.applicationContext).taskDao()
        repository = TaskRepository(taskDao)
        readAllTasks = repository.readAllTasks
    }

    fun addTask(task: TaskEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(task)
        }
    }

    fun updateTask(task: TaskEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: TaskEntity){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteTask(task)
        }
    }

    fun deleteAllTasks(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAllTasks()
        }
    }

    fun setTaskDate(year: Int, month: Int, day: Int): String{
        if (month.toString().length == 1) {
            return "$day.0$month.$year"
        } else return "$day.$month.$year"
    }

    fun setTaskTime(hour: Int, minute: Int): String{
        if (minute.toString().length == 1) {
            return "$hour:0$minute"
        } else return "$hour:$minute"
    }

}