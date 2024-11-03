package com.example.submission_1_fundamental_android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.submission_1_fundamental_android.adapter.FinishedAdapter
import com.example.submission_1_fundamental_android.data.local.EventEntity
import com.example.submission_1_fundamental_android.databinding.FragmentFavoriteBinding
import com.example.submission_1_fundamental_android.viewModel.FactoryViewModel
import com.example.submission_1_fundamental_android.viewModel.MainViewModel

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private lateinit var adapter: FinishedAdapter
    private lateinit var viewModel : MainViewModel
    private var progressBarCallback: ProgressBarCallback? = null
    private var noInternetCallback: NoInternetCallback? = null
    private var swipeRefreshLayout: SwipeRefreshLayout?=null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Favorite"

        val factory : FactoryViewModel = FactoryViewModel.getInstance(requireContext())
        viewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]

        adapter = FinishedAdapter(FinishedAdapter.VIEW_TYPE_FINISHED)
        binding.rvFinished.layoutManager = LinearLayoutManager(context)
        binding.rvFinished.adapter = adapter

        swipeRefreshLayout= binding.swipeRefreshLayout

        swipeRefreshLayout?.setOnRefreshListener {
            viewModel.getFavorite().observe(viewLifecycleOwner){ event ->
                handleResult(event, adapter)
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFavorite().observe(viewLifecycleOwner){ event ->
            handleResult(event, adapter)
        }
    }

    private fun handleResult(result: List<EventEntity>, adapter: FinishedAdapter) {
        swipeRefreshLayout?.isRefreshing = false

        adapter.submitList(result)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}