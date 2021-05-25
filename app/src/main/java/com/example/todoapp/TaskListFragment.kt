package com.example.todoapp

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.adapters.TaskListAdapter
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.databinding.FragmentTaskListBinding
import com.example.todoapp.models.SortMode
import com.example.todoapp.models.Task
import com.example.todoapp.viewModels.TaskViewModel


class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TaskViewModel
    private val adapter = TaskListAdapter()

    //private var sortMode = SortMode.SORT_NO_MODE


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
        val view = binding.root

        setFabAppearance()
        initNavigation()

        initRecyclerView()

        setHasOptionsMenu(true)


        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_menu -> deleteAllTasks()
            R.id.sort_by_name -> {
                adapter.sortTaskList(SortMode.SORT_BY_NAME)
            }
            R.id.sort_by_time -> adapter.sortTaskList(SortMode.SORT_BY_TIME)
            R.id.sort_by_priority -> adapter.sortTaskList(SortMode.SORT_BY_PRIORITY)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView(){
        binding.taskListRv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.taskListRv.adapter = adapter

        viewModel.readAllTasks.observe(viewLifecycleOwner, Observer {
            adapter.setData(it) //it.sortedBy { it.taskName }
        })
    }

    private fun setFabAppearance(){
        binding.fab.setColorFilter(Color.WHITE)
    }

    private fun initNavigation(){
        binding.fab.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.navigateToTaskCreateFragment)
        }

    }

    private fun deleteAllTasks(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes", {_, _ ->
            viewModel.deleteAllTasks()
            Toast.makeText(requireContext(), "List cleared", Toast.LENGTH_SHORT).show()
        })

        builder.setNegativeButton("No", {_, _ ->

        })
        builder.setTitle("List clear")
        builder.setMessage("Clear all list?")
        builder.create().show()
    }
}