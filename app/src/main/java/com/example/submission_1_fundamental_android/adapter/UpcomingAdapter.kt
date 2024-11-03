package com.example.submission_1_fundamental_android.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submission_1_fundamental_android.data.remote.response.ListEventsItem
import com.example.submission_1_fundamental_android.databinding.UpcomingViewBinding
import com.example.submission_1_fundamental_android.ui.DetailActivity

class UpcomingAdapter :
    ListAdapter<ListEventsItem, UpcomingAdapter.MainViewHolder>(DIFF_CALLBACK) {

    inner class MainViewHolder(private val binding: UpcomingViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: ListEventsItem) {
            binding.tvJudul.text = event.name
            Glide.with(itemView.context)
                .load(event.mediaCover)
                .into(binding.ivContent)

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra("EXTRA_EVENT_ID", event.id)
                binding.root.context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = UpcomingViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
