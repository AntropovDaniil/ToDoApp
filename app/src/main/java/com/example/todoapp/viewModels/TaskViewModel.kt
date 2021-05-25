package com.example.todoapp.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.example.todoapp.database.TaskDatabase
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.database.TaskRepository
import com.example.todoapp.enums.SortMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application): AndroidViewModel(application) {

    var readAllTasks: LiveData<List<TaskEntity>>
    private val repository: TaskRepository

    var sortMode = SortMode.SORT_NO_MODE

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

    fun searchTask(query: String): LiveData<List<TaskEntity>>{
        return repository.searchTask(query).asLiveData()
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