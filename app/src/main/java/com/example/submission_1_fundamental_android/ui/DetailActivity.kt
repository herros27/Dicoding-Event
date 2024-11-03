package com.example.submission_1_fundamental_android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.submission_1_fundamental_android.utils.AppInternet
import com.example.submission_1_fundamental_android.R
import com.example.submission_1_fundamental_android.data.local.EventEntity
import com.example.submission_1_fundamental_android.databinding.ActivityDetailBinding
import com.example.submission_1_fundamental_android.utils.Result
import com.example.submission_1_fundamental_android.viewModel.FactoryViewModel
import com.example.submission_1_fundamental_android.viewModel.MainViewModel

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivNoInternet.visibility = View.VISIBLE
        (applicationContext as AppInternet).isConnected.observe(this) { isConnected ->
            updateInternetStatusUI(isConnected)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val factory : FactoryViewModel = FactoryViewModel.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setupInitialUI()
        setupViewModel()

        handleIncomingData()

    }
    
    private fun setupViewModel() {
        val factory: FactoryViewModel = FactoryViewModel.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun setupInitialUI() {
        binding.ivNoInternet.visibility = View.VISIBLE
        (applicationContext as AppInternet).isConnected.observe(this) { isConnected ->
            updateInternetStatusUI(isConnected)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun handleIncomingData() {
        val event = intent.getParcelableExtra<EventEntity>(EXTRA_EVENT)

        if (event != null) {
            
            showLoading(false)
            updateUI(event)
        } else {
            
            val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)
            if (eventId != -1) {
                
                showLoading(true)
                viewModel.getEventByIdSync(eventId)
                observeEventById()
            } else {
                
                showLoading(false)
                Toast.makeText(this, "Failed to get event data", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun observeEventById() {
        viewModel.events.observe(this) { state ->
            when (state) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    updateUI(state.data)
                }
                is Result.Error -> {
                    showLoading(false)
                    finish()
                }
            }
        }
    }

    private fun updateInternetStatusUI(isInternetAvailable: Boolean) {
        if (!isInternetAvailable) {
            binding.noInternetLayout.visibility = View.VISIBLE
            binding.btnOpenLink.visibility = View.GONE
        } else {
            binding.noInternetLayout.visibility = View.GONE
            binding.btnOpenLink.visibility = View.VISIBLE

        }
    }

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    private fun updateUI(detail: EventEntity) {
        binding.apply {
            changeFavorite(detail.isFavorite)
            tvEventName.text = detail.name
            tvOrganizer.text = detail.ownerName
            tvEventTime.text = detail.beginTime
            tvRemainingQuota.text = "Sisa Kuota: ${detail.quota - detail.registrants}"
            val description = detail.description
            val withoutImage = deleteImageWeb(description)
            tvDescription .text = Html.fromHtml(withoutImage, Html.FROM_HTML_MODE_LEGACY)

            Glide.with(this@DetailActivity)
                .load(detail.imageLogo)
                .into(imageLogo)

            Glide.with(this@DetailActivity)
                .load(detail.mediaCover)
                .into(mediaCover)

            btnOpenLink.setOnClickListener {
                openLink(detail.link)
            }

            btnFavorite.setOnClickListener {
                detail.isFavorite = !detail.isFavorite
                changeFavorite(detail.isFavorite)
                if(detail.isFavorite){
                    viewModel.addFavoriteEvent(detail)
                    Toast.makeText(this@DetailActivity, "Berhasil menambahkan ke favorite",Toast.LENGTH_SHORT).show()
                }else{
                    viewModel.removeFavoriteEvent(detail)
                    Toast.makeText(this@DetailActivity, "Berhasil menghapus dari favorite",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteImageWeb(description: String):String{
        val startIndex = description.indexOf("\"<p>")
        val lastIndex = description.indexOf("\"</p>")
        val htmlContent =if(startIndex != -1 && lastIndex != -1){
            description.substring(startIndex+1,lastIndex+10)
        } else {
            description
        }

        return htmlContent.replace(("<p><img.*?/><p>").toRegex(), "")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                binding.noInternetLayout.visibility = View.GONE
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun changeFavorite(isFavorite: Boolean){
        binding.btnFavorite.setImageResource(
            if (isFavorite){
                R.drawable.ic_favorite1
            }else{
                R.drawable.ic_favorite
            }
        )
    }

    companion object {
        const val EXTRA_EVENT = "EXTRA_EVENT"
        const val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"
    }
}
