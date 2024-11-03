package com.example.submission_1_fundamental_android.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.submission_1_fundamental_android.utils.AppInternet
import com.example.submission_1_fundamental_android.R
import com.example.submission_1_fundamental_android.adapter.UpcomingAdapter
import com.example.submission_1_fundamental_android.data.remote.response.ListEventsItem
import com.example.submission_1_fundamental_android.databinding.ActivityMainBinding
import com.example.submission_1_fundamental_android.utils.Result
import com.example.submission_1_fundamental_android.viewModel.FactoryViewModel
import com.example.submission_1_fundamental_android.viewModel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), ProgressBarCallback, NoInternetCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var ivNoInternet: ConstraintLayout
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: UpcomingAdapter
    private lateinit var recyclerView : RecyclerView
    private var isRecyclerViewVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent("com.android.systemui.fsgesture")
        sendBroadcast(intent, "android.permission.SOME_PROTECTED_BROADCAST_PERMISSION")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView = binding.recyclerViewMain

        ivNoInternet = binding.noInternetLayout
        ivNoInternet.visibility = View.GONE

       recyclerView.visibility = View.GONE

        if (savedInstanceState != null) {
            isRecyclerViewVisible = savedInstanceState.getBoolean("isRecyclerViewVisible", false)
           recyclerView.visibility = if (isRecyclerViewVisible) View.VISIBLE else View.GONE
        }

        (applicationContext as AppInternet).isConnected.observe(this) { isConnected ->
            ivNoInternet.visibility = View.VISIBLE
            updateInternetStatusUI(isConnected)
        }
        val factory : FactoryViewModel = FactoryViewModel.getInstance(this)
        viewModel = ViewModelProvider(this,factory)[MainViewModel::class.java]

        observeViewModel()

        adapter = UpcomingAdapter()

        setupRecycler()
        supportActionBar?.show()

        progressBar = binding.progressBar
        setSelectedNavItem(R.id.navigation_home)
        isRecyclerViewVisible = false
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.getThemeSetting().observe(this@MainActivity){
            setDarkMode(it)
        }

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun setSelectedNavItem(nav:Int){
        binding.navView.selectedItemId = nav
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setOnItemSelectedListener { menuItem ->

            when(menuItem.itemId){
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_upcoming -> {
                    navController.navigate(R.id.navigation_upcoming)
                    true
                }
                R.id.navigation_finished -> {
                    navController.navigate(R.id.navigation_finished)
                    true
                }
                R.id.navigation_favorite -> {
                    navController.navigate(R.id.navigation_favorite)
                    true
                }
                R.id.navigation_setting -> {
                    navController.navigate(R.id.navigation_setting)
                    true
                }
                else -> false
            }
        }
    }

    fun getRecyclerView(): RecyclerView {
        return recyclerView
    }

    private val requestPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Notifications permission rejected", Toast.LENGTH_SHORT).show()
            }
        }

    private fun setupRecycler(){
        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = adapter
            adapter.submitList(emptyList())
        }
    }

    private fun setDarkMode(isDarkMode : Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        progressBar.visibility = View.GONE
    }


    private fun observeViewModel(){
        viewModel.searchResult.observe(this) { result ->
           handleEventResult(result)
        }
    }

    private fun handleEventResult(result: Result<List<ListEventsItem>>) {
        when (result) {
            is Result.Success -> {
                showLoading(false)
                if (result.data.isEmpty()) {
                    recyclerView.visibility = View.GONE
                } else {

                    recyclerView.visibility = View.VISIBLE
                    adapter.submitList(result.data)
                }
            }
            is Result.Error -> {
                recyclerView.visibility = View.GONE
                Toast.makeText(this, "Failed fetching data", Toast.LENGTH_SHORT).show()
            }
            is Result.Loading -> {
                showLoading(true)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
       recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val search = R.menu.menu_search

        menuInflater.inflate(search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotBlank()) {
                        viewModel.searchEvents(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotBlank()) {
                        viewModel.searchEvents(it)
                    }
                }
                return true
            }
        })

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {

                recyclerView.visibility = View.GONE
                isRecyclerViewVisible = false
            } else {

                recyclerView.visibility = View.VISIBLE
                isRecyclerViewVisible = true
            }
        }
        recyclerView.visibility = View.GONE

        return true
    }









































































    private fun updateInternetStatusUI(isInternetAvailable: Boolean) {
        ivNoInternet.visibility  = if(isInternetAvailable) View.GONE else View.VISIBLE

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean("isRecyclerViewVisible", isRecyclerViewVisible)
    }

    override fun showProgressBar(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE

    }

    override fun showNoInternet(show: Boolean) {
        ivNoInternet.visibility = View.VISIBLE
    }


}