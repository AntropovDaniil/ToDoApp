package com.example.todoapp.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.databinding.FragmentTaskDetailBinding
import com.example.todoapp.enums.TaskPriority
import com.example.todoapp.viewModels.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class TaskDetailFragment : Fragment() {

    private val args by navArgs<TaskDetailFragmentArgs>()

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        setArgs()
        setUpdateButtonListener()
        setBackPressedCallback()
        setupSaveTaskDialogFragmentListener()

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
                findNavController().navigateUp()//.navigate(R.id.navigateToTaskListFragmentFromDetail)
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
            findNavController().navigateUp()
        })
        builder.setNegativeButton("No", {_, _ ->
        })
        builder.setTitle("Task deletion ")
        builder.setMessage("Delete this task?")
        builder.create().show()
    }

    private fun isTaskChanged(): Boolean{
        return !(binding.taskName.text.toString() == args.updatedTask.taskName &&
                binding.taskDescription.text.toString() == args.updatedTask.taskDescription &&
                binding.taskDate.text.toString() == args.updatedTask.taskDate &&
                binding.taskTime.text.toString() == args.updatedTask.taskTime &&
                getTaskPriority() == args.updatedTask.taskPriority &&
                binding.taskRemindSwitch.isChecked == args.updatedTask.taskRemindFlag)
    }

    private fun setBackPressedCallback(){
        val backCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (isTaskChanged() && binding.taskName.text.toString().isNotEmpty())
                    showAlertDialog()
                else
                    findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(backCallback)
    }

    private fun getTaskPriority(): TaskPriority{
        when{
            binding.lowPriorityRb.isChecked -> return TaskPriority.LOW_PRIORITY
            binding.mediumPriorityRb.isChecked -> return TaskPriority.MEDIUM_PRIORITY
            else -> return TaskPriority.HIGH_PRIORITY
        }
    }

    private fun showAlertDialog() {
        val dialogFragment = SaveTaskDialogFragment()
        dialogFragment.show(parentFragmentManager, SaveTaskDialogFragment.TAG)
    }

    private fun setupSaveTaskDialogFragmentListener(){
        parentFragmentManager.setFragmentResultListener(SaveTaskDialogFragment.REQUEST_KEY,
            viewLifecycleOwner,
            FragmentResultListener { _, result ->
                val which = result.getInt(SaveTaskDialogFragment.KEY_RESPONSE)
                when(which){
                    DialogInterface.BUTTON_POSITIVE -> {
                        updateTask()
                        findNavController().navigateUp()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> findNavController().navigateUp()
                    DialogInterface.BUTTON_NEUTRAL -> {}
                }
            })
    }


}