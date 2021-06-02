package com.example.todoapp.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.databinding.FragmentTaskDetailBinding
import com.example.todoapp.enums.TaskPriority
import com.example.todoapp.viewModels.TaskViewModel
import java.util.*

class TaskDetailFragment : Fragment() {

    private val args by navArgs<TaskDetailFragmentArgs>()

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        setArgs()
        setUpdateButtonListener()

        // add menu with delete function
        setHasOptionsMenu(true)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setArgs(){
        binding.taskName.setText(args.updatedTask.taskName)
        binding.taskDescription.setText(args.updatedTask.taskDescription)
        binding.taskDate.text = args.updatedTask.taskDate
        binding.taskTime.text = args.updatedTask.taskTime

        when(args.updatedTask.taskPriority){
            TaskPriority.LOW_PRIORITY -> binding.lowPriorityRb.isChecked = true
            TaskPriority.MEDIUM_PRIORITY -> binding.mediumPriorityRb.isChecked = true
            else -> binding.highPriorityRb.isChecked = true
        }

        if (args.updatedTask.taskRemindFlag)
            binding.taskRemindSwitch.isChecked = true

        setDataChangeListener()
    }

    private fun setDataChangeListener(){
        val calendar = Calendar.getInstance()
        binding.taskDate.setOnClickListener {
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)
            if (binding.taskDate.text.isNotEmpty()){
                year = args.updatedTask.taskDate.substringAfterLast(".").toInt()
                month = args.updatedTask.taskDate.substringAfter(".").substringBefore(".").toInt()
                day = args.updatedTask.taskDate.substringBefore(".").toInt()
            }
            val datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener {
                    view, year, month, dayOfMonth ->
                binding.taskDate.setText(viewModel.setTaskDate(year, month, dayOfMonth))
            }, year, month, day)
            datePicker.show()
        }

        binding.taskTime.setOnClickListener {
            var hour = calendar.get(Calendar.HOUR_OF_DAY)
            var minute = calendar.get(Calendar.MINUTE)
            if (binding.taskTime.text.isNotEmpty()) {
                hour = args.updatedTask.taskTime.substringBefore(":").toInt()
                minute = args.updatedTask.taskTime.substringAfter(":").toInt()
            }

            val timePicker = TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener {
                    view, hourOfDay, minute ->
                binding.taskTime.setText(viewModel.setTaskTime(hourOfDay, minute))
            }, hour, minute, true)
            timePicker.show()
        }
    }

    private fun setUpdateButtonListener(){
        binding.taskUpdateBtn.setOnClickListener {
            if (checkInputData()) {
                updateTask()
                findNavController().navigate(R.id.navigateToTaskListFragmentFromDetail)
            }
            else {
                binding.taskName.error = "Invalid Data"
                binding.taskName.requestFocus()
            }
        }
    }

    private fun checkInputData(): Boolean{
        return !(TextUtils.isEmpty(binding.taskName.text))
    }

    private fun updateTask(){
        val taskPriority = when(binding.taskPriorityGroup.checkedRadioButtonId){
            R.id.high_priority_rb -> TaskPriority.HIGH_PRIORITY
            R.id.medium_priority_rb -> TaskPriority.MEDIUM_PRIORITY
            else -> TaskPriority.LOW_PRIORITY
        }
        val updatedTask = TaskEntity(args.updatedTask.taskId,
            binding.taskName.text.toString(),
            binding.taskDescription.text.toString(),
            binding.taskDate.text.toString(),
            binding.taskTime.text.toString(),
            taskPriority,
            binding.taskRemindSwitch.isChecked
        )
        viewModel.updateTask(updatedTask)

        Toast.makeText(requireContext(), "Task updated", Toast.LENGTH_SHORT).show()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.task_detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_menu){
            deleteTask()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteTask(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes", {_, _ ->
            viewModel.deleteTask(args.updatedTask)
            Toast.makeText(requireContext(), "Task deleted", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.navigateToTaskListFragmentFromDetail)
        })
        builder.setNegativeButton("No", {_, _ ->
        })
        builder.setTitle("Task deletion ")
        builder.setMessage("Delete this task?")
        builder.create().show()
    }


}