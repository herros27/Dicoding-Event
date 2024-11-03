package com.example.submission_1_fundamental_android.ui


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.submission_1_fundamental_android.R
import com.example.submission_1_fundamental_android.adapter.FinishedAdapter
import com.example.submission_1_fundamental_android.data.local.EventEntity
import com.example.submission_1_fundamental_android.databinding.FragmentHomeBinding
import com.example.submission_1_fundamental_android.utils.Result
import com.example.submission_1_fundamental_android.viewModel.FactoryViewModel
import com.example.submission_1_fundamental_android.viewModel.MainViewModel

interface ProgressBarCallback {
    fun showProgressBar(show: Boolean)
}

interface NoInternetCallback {
    fun showNoInternet(show: Boolean)
}
class HomeFragment : Fragment() {

    private lateinit var finishAdapter: FinishedAdapter
    private lateinit var upAdapter: FinishedAdapter
    private lateinit var homeViewModel: MainViewModel
    private var progressBarCallback: ProgressBarCallback? = null
    private var noInternetCallback: NoInternetCallback? = null
    private lateinit var labelFinished : TextView
    private lateinit var labelUpcoming : TextView
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when(context) {
            is ProgressBarCallback -> progressBarCallback = context
            is NoInternetCallback -> noInternetCallback = context
            else -> throw RuntimeException("$context must implement ProgressBarCallback and NoInternetCallback")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        swipeRefreshLayout = binding.swipeRefreshLayout 

        (activity as? AppCompatActivity)?.supportActionBar?.title = "Home"

        val factory : FactoryViewModel = FactoryViewModel.getInstance(requireContext())
        homeViewModel = ViewModelProvider(requireActivity(), factory)[MainViewModel::class.java]

        setupRecyclerView()

        labelFinished = binding.finishedEventsLabel
        labelUpcoming = binding.upcomingEventsLabel
        setupNavMore()

        swipeRefreshLayout.setOnRefreshListener {
            setupRecyclerView()
            homeViewModel.getUpcomingEvent().observe(viewLifecycleOwner){event ->
                handleResult(event,upAdapter)
            }

            homeViewModel.getFinishedEvent().observe(viewLifecycleOwner){event ->
                handleResult(event,finishAdapter)
            }
        }

        swipeRefreshLayout.setOnChildScrollUpCallback { _, _ ->
            binding.scrollView.scrollY > 0
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        
        homeViewModel.getUpcomingEvent().observe(viewLifecycleOwner) { event ->
            handleResult(event, upAdapter)
        }

        homeViewModel.getFinishedEvent().observe(viewLifecycleOwner) { event ->
            handleResult(event, finishAdapter)
        }
    }

    private fun setupNavMore() {
        binding.tvMoreUp.setOnClickListener {
            (activity as MainActivity).setSelectedNavItem(R.id.navigation_upcoming)
        }
        binding.tvMoreFin.setOnClickListener {
            (activity as MainActivity).setSelectedNavItem(R.id.navigation_finished)
        }
    }

    private fun setupRecyclerView() {
        finishAdapter = FinishedAdapter(FinishedAdapter.VIEW_TYPE_FINISHED)
        upAdapter = FinishedAdapter(FinishedAdapter.VIEW_TYPE_UPCOMING)

        binding.upcomingEventsRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.finishedEventsRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)

        binding.upcomingEventsRecycler.adapter = upAdapter
        binding.finishedEventsRecycler.adapter = finishAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getUpcomingEvent().observe(viewLifecycleOwner){event ->
            handleResult(event,upAdapter)
            val recyclerViewMain = (activity as? MainActivity)?.getRecyclerView()
            recyclerViewMain?.visibility = View.GONE
        }

        homeViewModel.getFinishedEvent().observe(viewLifecycleOwner){event ->
            handleResult(event,finishAdapter)
            val recyclerViewMain = (activity as? MainActivity)?.getRecyclerView()
            recyclerViewMain?.visibility = View.GONE
        }
    }

    private fun handleResult(result: Result<List<EventEntity>>, adapter: FinishedAdapter) {
        when (result) {
            is Result.Loading -> {
                showLoading(isLoading = true, isInternet = false)
                swipeRefreshLayout.isRefreshing = true
            }
            is Result.Success -> {
                showLoading(isLoading = false, isInternet = false)
                swipeRefreshLayout.isRefreshing = false
                val recyclerViewMain = (activity as? MainActivity)?.getRecyclerView()
                recyclerViewMain?.visibility = View.GONE
                val limitData = result.data.take(4).shuffled()
                adapter.submitList(limitData)
            }
            is Result.Error -> {
                showLoading(isLoading = false, isInternet = true)
                swipeRefreshLayout.isRefreshing = false 
                Toast.makeText(context, "Failed fetching data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean, isInternet: Boolean) {
        val visibility = if (isLoading) View.GONE else View.VISIBLE

        labelFinished.visibility = visibility
        labelUpcoming.visibility = visibility
        binding.tvMoreUp.visibility = visibility
        binding.tvMoreFin.visibility = visibility
        binding.title.visibility = visibility

        progressBarCallback?.showProgressBar(isLoading)
        noInternetCallback?.showNoInternet(isInternet)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        progressBarCallback = null
        noInternetCallback = null
    }
}

