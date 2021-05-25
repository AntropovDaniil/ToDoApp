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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.adapters.TaskListAdapter
import com.example.todoapp.databinding.FragmentTaskListBinding
import com.example.todoapp.enums.SortMode
import com.example.todoapp.viewModels.TaskViewModel


class TaskListFragment : Fragment(), androidx.appcompat.widget.SearchView.OnQueryTextListener{

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TaskViewModel
    private val adapter = TaskListAdapter()

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
            R.id.sort_by_name -> {
                viewModel.sortMode = SortMode.SORT_BY_NAME
            }
            R.id.sort_by_time -> {
                viewModel.sortMode = SortMode.SORT_BY_TIME
            }
            R.id.sort_by_priority -> {
                viewModel.sortMode = SortMode.SORT_BY_PRIORITY
            }
        }
        adapter.sortTaskList(viewModel.sortMode)

        return super.onOptionsItemSelected(item)
    }


    private fun initRecyclerView(){
        binding.taskListRv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.taskListRv.adapter = adapter

        viewModel.readAllTasks.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                binding.emptyListBackground.visibility = View.GONE
            }
            adapter.setData(it)
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
}