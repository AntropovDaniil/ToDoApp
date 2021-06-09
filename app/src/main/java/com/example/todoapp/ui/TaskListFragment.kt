package com.example.todoapp.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.R
import com.example.todoapp.adapters.TaskListAdapter
import com.example.todoapp.databinding.FragmentTaskListBinding
import com.example.todoapp.enums.SortMode
import com.example.todoapp.viewModels.TaskViewModel
import com.example.todoapp.worker.TaskWorker
import java.text.SimpleDateFormat
import java.util.*


class TaskListFragment : Fragment(), androidx.appcompat.widget.SearchView.OnQueryTextListener{

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels()
    private val adapter = TaskListAdapter()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskListBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        setFabAppearance()
        initNavigation()
        initRecyclerView()
        setSortMode()

        setHasOptionsMenu(true)

        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)

        val search = menu.findItem(R.id.search_menu)
        val searchView = search?.actionView as? androidx.appcompat.widget.SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_menu -> {
                deleteAllTasks()
                binding.emptyListBackground.visibility = View.VISIBLE
            }
            R.id.sort_by_creating -> {
                viewModel.sortMode = SortMode.SORT_NO_MODE
                viewModel.saveSortMode(SortMode.SORT_NO_MODE)
            }
            R.id.sort_by_name -> {
                viewModel.sortMode = SortMode.SORT_BY_NAME
                viewModel.saveSortMode(SortMode.SORT_BY_NAME)
            }
            R.id.sort_by_time -> {
                viewModel.sortMode = SortMode.SORT_BY_TIME
                viewModel.saveSortMode(SortMode.SORT_BY_TIME)
            }
            R.id.sort_by_priority -> {
                viewModel.sortMode = SortMode.SORT_BY_PRIORITY
                viewModel.saveSortMode(SortMode.SORT_BY_PRIORITY)
            }
            R.id.grid_list -> {
                binding.taskListRv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                viewModel.saveLayoutType("GRID")
            }
            R.id.linear_list -> {
                binding.taskListRv.layoutManager = LinearLayoutManager(context)
                viewModel.saveLayoutType("LINEAR")
            }
        }
        adapter.sortTaskList(viewModel.sortMode)

        return super.onOptionsItemSelected(item)
    }


    private fun initRecyclerView(){
        viewModel.readLayoutType.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                when(it){
                    "LINEAR" -> binding.taskListRv.layoutManager = LinearLayoutManager(context)
                    else -> binding.taskListRv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                }
            }
        })
        binding.taskListRv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.taskListRv.adapter = adapter

        viewModel.readAllTasks.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                binding.emptyListBackground.visibility = View.GONE
            }
            adapter.setData(it)
            adapter.sortTaskList(viewModel.sortMode)
        })

        initSwipeListener()
    }

    private fun setSortMode() {
        viewModel.readSortMode.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                when(it){
                    "SORT_NO_MODE" -> viewModel.sortMode = SortMode.SORT_NO_MODE
                    "SORT_BY_TIME" -> viewModel.sortMode = SortMode.SORT_BY_TIME
                    "SORT_BY_PRIORITY" -> viewModel.sortMode = SortMode.SORT_BY_PRIORITY
                    "SORT_BY_NAME" -> viewModel.sortMode = SortMode.SORT_BY_NAME
                }
            }
            adapter.sortTaskList(viewModel.sortMode)
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
        builder.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteAllTasks()
            Toast.makeText(requireContext(), "List cleared", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No") { _, _ ->}

        builder.setTitle("Clear List")
        builder.setMessage("Delete all tasks?")
        builder.create().show()
    }

    private fun searchTask(query: String){
        val searchQuery = "%$query%"
        viewModel.searchTask(searchQuery).observe(this, Observer { list ->
            list.let {
                adapter.setData(it)
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null){
            searchTask(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null){
            searchTask(query)
        }
        return true
    }

    private fun initSwipeListener(){
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                viewModel.deleteTask(adapter.taskList[position])
                adapter.notifyItemRemoved(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.taskListRv)
    }
}