package com.example.submission_1_fundamental_android.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submission_1_fundamental_android.data.local.EventEntity
import com.example.submission_1_fundamental_android.databinding.FinishedViewBinding
import com.example.submission_1_fundamental_android.databinding.UpcomingViewBinding
import com.example.submission_1_fundamental_android.ui.DetailActivity

class FinishedAdapter(private val typeView: Int?
) : ListAdapter<EventEntity, RecyclerView.ViewHolder>(
    DIFF_CALLBACK) {

    inner class FinishedViewHolder(private val binding: FinishedViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventEntity) {
            binding.tvJudul.text = event.name

            binding.tvOverView.text = event.summary

            Glide.with(itemView.context)
                .load(event.imageLogo)
                .into(binding.ivContent)

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra("EXTRA_EVENT", event)
                binding.root.context.startActivity(intent)
            }
        }
    }

    inner class UpcomingViewHolder(private val binding: UpcomingViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventEntity) {
            binding.tvJudul.text = event.name

            Glide.with(itemView.context)
                .load(event.imageLogo)
                .into(binding.ivContent)

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra("EXTRA_EVENT", event)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(typeView){
            VIEW_TYPE_FINISHED -> {
                val binding = FinishedViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FinishedViewHolder(binding)
            }
            VIEW_TYPE_UPCOMING -> {
                val binding = UpcomingViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UpcomingViewHolder(binding)
            }
            else ->{
                val binding = UpcomingViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UpcomingViewHolder(binding)
            }

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = getItem(position)
        when(holder){
            is UpcomingViewHolder -> holder.bind(event)
            is FinishedViewHolder -> {
                holder.bind(event)
            }
        }
    }

    companion object {
        const val VIEW_TYPE_UPCOMING = 1
        const val VIEW_TYPE_FINISHED = 2
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(
                oldItem: EventEntity,
                newItem: EventEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
