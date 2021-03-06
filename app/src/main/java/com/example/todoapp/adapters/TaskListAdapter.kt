package com.example.todoapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.database.TaskDatabase
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.repository.TaskRepository
import com.example.todoapp.databinding.TaskItemLayoutBinding
import com.example.todoapp.enums.SortMode
import com.example.todoapp.enums.TaskPriority
import com.example.todoapp.ui.TaskListFragmentDirections
import com.example.todoapp.utils.taskDateCompare
import com.example.todoapp.utils.taskTimeCompareMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class TaskListAdapter @Inject constructor(private val repository: TaskRepository)
    : RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder>() {

    private var _rvBinding: TaskItemLayoutBinding? = null
    private val rvBinding: TaskItemLayoutBinding get() = _rvBinding!!

    var taskList = emptyList<TaskEntity>()
    var adapterSortMode = SortMode.SORT_BY_ID

    inner class TaskListViewHolder(val binding: TaskItemLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskListAdapter.TaskListViewHolder {
        _rvBinding = TaskItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TaskListViewHolder(rvBinding)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: TaskListAdapter.TaskListViewHolder, position: Int) {
        holder.binding.taskTitle.text = taskList[position].taskName

        if (taskList[position].taskDescription.isEmpty())
            holder.binding.taskDescription.visibility = View.GONE
        else
            holder.binding.taskDescription.text = taskList[position].taskDescription

        when(taskList[position].taskPriority){
            TaskPriority.LOW_PRIORITY -> holder.binding.taskPriority.setBackgroundColor(Color.GREEN)
            TaskPriority.MEDIUM_PRIORITY -> holder.binding.taskPriority.setBackgroundColor(Color.YELLOW)
            TaskPriority.HIGH_PRIORITY -> holder.binding.taskPriority.setBackgroundColor(Color.RED)
        }

        if (taskList[position].taskRemindFlag){
            holder.binding.taskRemindIcon.visibility = View.VISIBLE
        }
        else holder.binding.taskRemindIcon.visibility = View.GONE

        setTaskStatus(holder, taskList[position])

        holder.binding.taskStatusMarker.setOnClickListener {
            changeTaskStatus(taskList[position])
        }

        holder.binding.taskId.setOnClickListener {
            val action = TaskListFragmentDirections.navigateToTaskDetailFragment(taskList[position])
            holder.itemView.findNavController().navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return taskList.size
    }


    fun setData(tasks: List<TaskEntity>){
        taskList = tasks
        sortTaskList()
    }

    private fun setTaskStatus(holder: TaskListAdapter.TaskListViewHolder, task: TaskEntity) {
        if (!task.taskDoneFlag) {
            holder.binding.taskStatusMarker.setImageResource(R.drawable.undone_circle)
            holder.binding.taskTitle.setPaintFlags(0)
        } else if (task.taskDoneFlag) {
            holder.binding.taskStatusMarker.setImageResource(R.drawable.done_circle)
            holder.binding.taskTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeTaskStatus(task: TaskEntity){
        task.taskDoneFlag = !task.taskDoneFlag
        updateTask(task)
        sortTaskList()
    }

    private fun updateTask(task: TaskEntity){
        CoroutineScope(Dispatchers.IO).launch{
            repository.updateTask(task)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSortMode(sortMode: SortMode){
        adapterSortMode = sortMode
        sortTaskList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sortTaskList(){
        when(adapterSortMode){
            SortMode.SORT_BY_ID -> {
                taskList = taskList.sortedBy{it.taskId}.sortedBy { it.taskDoneFlag }
            }
            SortMode.SORT_BY_NAME -> {
                taskList = taskList.sortedBy {it.taskName}.sortedBy { it.taskDoneFlag }
            }
            SortMode.SORT_BY_PRIORITY -> {
                taskList = taskList.sortedByDescending { it.taskPriority }.sortedBy { it.taskDoneFlag }
            }
            SortMode.SORT_BY_TIME -> {
                taskList = taskDateCompare(taskList).sortedBy { it.taskDoneFlag }
            }
        }
        notifyDataSetChanged()
    }

}