package com.example.todoapp.viewModels

import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.repository.TaskRepository
import com.example.todoapp.enums.SortMode
import com.example.todoapp.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val repository: TaskRepository,
                                        private val dataStoreRepository: DataStoreRepository,
                                        application: Application): AndroidViewModel(application) {

    var readAllTasks: LiveData<List<TaskEntity>>
    private var taskList: List<TaskEntity>

    val readSortMode = dataStoreRepository.readSortMode().asLiveData()
    val readLayoutType = dataStoreRepository.readLayoutType().asLiveData()

    //private val workManager: WorkManager = WorkManager.getInstance(application)

    var sortMode = SortMode.SORT_BY_ID


    init {
        readAllTasks = repository.readAllTasks
        taskList = readAllTasks.value ?: emptyList()
        //launchWorker()
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
            return "$day.0${month+1}.$year"
        } else return "$day.${month+1}.$year"
    }

    fun setTaskTime(hour: Int, minute: Int): String{
        if (minute.toString().length == 1) {
            return "$hour:0$minute"
        } else return "$hour:$minute"
    }

//    private fun launchWorker(){
//        workManager
//            .enqueue(buildWorkerRequest())
//    }

//    private fun buildWorkerRequest() : WorkRequest{
//        val constraints = Constraints.Builder()
//            .setRequiresBatteryNotLow(true)
//            .build()
//
//        val taskWorkerRequest: WorkRequest = PeriodicWorkRequestBuilder<TaskWorker>(12, TimeUnit.HOURS, 1, TimeUnit.HOURS)
//            .addTag("TaskWorker")
//            .setInitialDelay(10, TimeUnit.SECONDS)
//            .setConstraints(constraints)
//            .build()
//
//        return taskWorkerRequest
//    }

    fun saveSortMode(sortMode: SortMode){
        viewModelScope.launch(Dispatchers.IO) {
            when (sortMode) {
                SortMode.SORT_BY_ID -> dataStoreRepository.saveSortMode("SORT_BY_ID")
                SortMode.SORT_BY_TIME -> dataStoreRepository.saveSortMode("SORT_BY_TIME")
                SortMode.SORT_BY_PRIORITY -> dataStoreRepository.saveSortMode("SORT_BY_PRIORITY")
                SortMode.SORT_BY_NAME -> dataStoreRepository.saveSortMode("SORT_BY_NAME")
            }
        }
    }

    fun saveLayoutType(layoutType: String){
        viewModelScope.launch(Dispatchers.IO){
            when(layoutType){
                "LINEAR" -> dataStoreRepository.saveLayoutType("LINEAR")
                else -> dataStoreRepository.saveLayoutType("GRID")
            }
        }
    }





}