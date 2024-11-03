package com.example.submission_1_fundamental_android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.submission_1_fundamental_android.adapter.FinishedAdapter
import com.example.submission_1_fundamental_android.data.local.EventEntity
import com.example.submission_1_fundamental_android.databinding.FragmentFinishedBinding
import com.example.submission_1_fundamental_android.utils.Result
import com.example.submission_1_fundamental_android.viewModel.FactoryViewModel
import com.example.submission_1_fundamental_android.viewModel.MainViewModel

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private lateinit var adapter: FinishedAdapter
    private lateinit var viewModel : MainViewModel
    private var progressBarCallback: ProgressBarCallback? = null
    private var noInternetCallback: NoInternetCallback? = null
    private var swipeRefreshLayout: SwipeRefreshLayout?=null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        val root: View = binding.root
        
        val factory : FactoryViewModel = FactoryViewModel.getInstance(requireContext())
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]

        swipeRefreshLayout= binding.swipeRefreshLayout
        
        adapter = FinishedAdapter(FinishedAdapter.VIEW_TYPE_FINISHED)
        binding.rvFinished.layoutManager = LinearLayoutManager(context)
        binding.rvFinished.adapter = adapter
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Finished Event"

        swipeRefreshLayout?.setOnRefreshListener {
            viewModel.getFinishedEvent().observe(viewLifecycleOwner){ event ->
                handleResult(event, adapter)
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFinishedEvent().observe(viewLifecycleOwner){ event ->
            handleResult(event, adapter)
        }
        
    }

    private fun handleResult(result: Result<List<EventEntity>>, adapter: FinishedAdapter) {
        when (result) {
            is Result.Loading -> {
                showLoading(isLoading = true, isInternet = false)
                swipeRefreshLayout?.isRefreshing = true
            }
            is Result.Success -> {
                showLoading(isLoading = false,isInternet = false)
                swipeRefreshLayout?.isRefreshing = false
                adapter.submitList(result.data)
            }
            is Result.Error -> {
                showLoading(isLoading = false,isInternet = true)
                swipeRefreshLayout?.isRefreshing = false
                Toast.makeText(context, "Failed Fetching data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean,isInternet: Boolean) {
        progressBarCallback?.showProgressBar(isLoading)
        noInternetCallback?.showNoInternet(isInternet)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}