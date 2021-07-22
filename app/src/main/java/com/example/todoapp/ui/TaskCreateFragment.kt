package com.example.todoapp.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.databinding.FragmentTaskCreateBinding
//import com.example.todoapp.models.Task
import com.example.todoapp.enums.TaskPriority
import com.example.todoapp.viewModels.TaskViewModel
import com.example.todoapp.viewModels.TaskViewModelFactory
import kotlinx.coroutines.runBlocking
import java.util.*

class TaskCreateFragment : Fragment() {

    private var _binding: FragmentTaskCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels()
    private lateinit var viewModelFactory: TaskViewModelFactory

    private var isTaskSaved: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskCreateBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModelFactory = TaskViewModelFactory(requireActivity().application)

        setCreateButtonListener()
        setDataChangeListener()
        setBackPressedCallback()
        setupSaveTaskDialogFragmentListener()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setDataChangeListener(){
        binding.taskDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener {
                    view, year, month, dayOfMonth ->
                binding.taskDate.setText(viewModel.setTaskDate(year, month, dayOfMonth))
            }, year, month, day)
            datePicker.show()
        }

        binding.taskTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener {
                    view, hourOfDay, minute ->
                binding.taskTime.setText(viewModel.setTaskTime(hourOfDay, minute))
            }, hour, minute, true)
            timePicker.show()
        }
    }

    private fun setCreateButtonListener(){
        binding.taskCreateBtn.setOnClickListener {
            if (checkInputData()) {
                createTask()
                findNavController().navigateUp()
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

    private fun createTask(){
        val taskPriority = when(binding.taskPriorityGroup.checkedRadioButtonId){
            R.id.high_priority_rb -> TaskPriority.HIGH_PRIORITY
            R.id.medium_priority_rb -> TaskPriority.MEDIUM_PRIORITY
            else -> TaskPriority.LOW_PRIORITY
        }
        val task = TaskEntity(0,
            binding.taskName.text.toString(),
            binding.taskDescription.text.toString(),
            binding.taskDate.text.toString(),
            binding.taskTime.text.toString(),
            taskPriority,
            binding.taskRemindSwitch.isChecked
        )
        viewModel.addTask(task)

        Toast.makeText(requireContext(), "Task created", Toast.LENGTH_SHORT).show()
        isTaskSaved = true
    }


    private fun setBackPressedCallback(){
        val backCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (isTaskSaved == false && binding.taskName.text.toString().isNotEmpty())
                    showAlertDialog()
                else
                    findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(backCallback)
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
                        createTask()
                        findNavController().navigateUp()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> findNavController().navigateUp()
                    DialogInterface.BUTTON_NEUTRAL -> {}
                }
            })
    }
}