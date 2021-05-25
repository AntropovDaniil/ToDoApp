package com.example.todoapp.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.TaskListFragmentDirections
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.databinding.TaskItemLayoutBinding
import com.example.todoapp.models.SortMode
import com.example.todoapp.models.Task
import com.example.todoapp.models.TaskPriority

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
            "low_priority" -> holder.binding.taskPriority.setBackgroundColor(Color.GREEN) //.setColorFilter(Color.GREEN)
            "medium_priority" -> holder.binding.taskPriority.setBackgroundColor(Color.YELLOW)
            "high_priority" -> holder.binding.taskPriority.setBackgroundColor(Color.RED)
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
            SortMode.SORT_BY_NAME -> {
                taskList = taskList.sortedBy {
                    it.taskName
                }
            }
        }
        notifyDataSetChanged()
    }
}