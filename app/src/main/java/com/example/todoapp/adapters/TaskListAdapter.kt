package com.example.todoapp.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.TaskListFragmentDirections
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.databinding.TaskItemLayoutBinding
import com.example.todoapp.enums.SortMode
import com.example.todoapp.enums.TaskPriority
import java.util.*

class TaskListAdapter(): RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder>() {

    private var _rvBinding: TaskItemLayoutBinding? = null
    private val rvBinding: TaskItemLayoutBinding get() = _rvBinding!!

    private var taskList = emptyList<TaskEntity>()

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
        holder.binding.taskDescription.text = taskList[position].taskDescription
        when(taskList[position].taskPriority){
            TaskPriority.LOW_PRIORITY -> holder.binding.taskPriority.setBackgroundColor(Color.GREEN)
            TaskPriority.MEDIUM_PRIORITY -> holder.binding.taskPriority.setBackgroundColor(Color.YELLOW)
            TaskPriority.HIGH_PRIORITY -> holder.binding.taskPriority.setBackgroundColor(Color.RED)
        }

        if (taskList[position].taskRemindFlag){
            holder.binding.taskRemindIcon.visibility = View.VISIBLE
        }

        holder.binding.taskStatusMarker.setOnClickListener {
            setTaskStatus(taskList[position])
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
        notifyDataSetChanged()
    }

    private fun setTaskStatus(task: TaskEntity) {
        if (!task.taskDoneFlag) {
            task.taskDoneFlag = true
            rvBinding.taskStatusMarker.setImageResource(R.drawable.done_circle)
            rvBinding.taskTitle.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG)
        } else if (task.taskDoneFlag) {
            task.taskDoneFlag = false
            rvBinding.taskStatusMarker.setImageResource(R.drawable.undone_circle)
            rvBinding.taskTitle.setPaintFlags(0)
        }
    }

    fun sortTaskList(sortMode: SortMode){
        when(sortMode){
            SortMode.SORT_NO_MODE -> taskList.sortedBy { it.taskId }
            SortMode.SORT_BY_NAME -> {
                taskList = taskList.sortedBy {it.taskName}
            }
            SortMode.SORT_BY_PRIORITY -> {
                taskList = taskList.sortedByDescending { it.taskPriority }
            }
            SortMode.SORT_BY_TIME -> {
                taskList = taskList.sortedBy { taskTimeCompareMap(it.taskDate, it.taskTime) }
            }
        }
        notifyDataSetChanged()
    }

    private fun taskTimeCompareMap(date: String, time: String): Date{
        var result = Date()
        if (date.isNotEmpty() && time.isNotEmpty()){
            result = Date(
                date.substringAfterLast(".").toInt(),
                date.substringAfter(".").substringBefore(".").toInt(),
                date.substringBefore(".").toInt(),
                time.substringBefore(":").toInt(),
                time.substringAfter(":").toInt()
            )
        }
        else if (date.isNotEmpty() && time.isEmpty()){
            result = Date(
                date.substringAfterLast(".").toInt(),
                date.substringAfter(".").substringBefore(".").toInt(),
                date.substringBefore(".").toInt()
            )
        }
        return result
    }
}