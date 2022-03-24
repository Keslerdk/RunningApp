package com.example.runningapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.runningapp.databinding.RunItemBinding
import com.example.runningapp.db.Run
import com.example.runningapp.other.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*

class RunsRecyclerViewAdapter(private val onCLick: (Int) -> Unit) :
    RecyclerView.Adapter<RunsRecyclerViewAdapter.RunsRecyclerViewHolder>() {

    inner class RunsRecyclerViewHolder(private val binding: RunItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(id: Int, timeStamp: Long, distanceInMeters: Int, timeInMillis: Long, avgSpeed: Float) {
            val dateFormat = SimpleDateFormat("dd.MM.yy hh:mm a", Locale.getDefault())
            binding.date.text = dateFormat.format(timeStamp)
            binding.distanceTitle.text = distanceInMeters.toString()
            binding.timeTitle.text = TrackingUtility.getFormattedStopWatchTime(timeInMillis)
            binding.speedTitle.text = avgSpeed.toString()

            binding.runCard.setOnClickListener { onCLick(id) }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitData(data: List<Run>) = differ.submitList(data)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunsRecyclerViewHolder {
        val view = RunItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RunsRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RunsRecyclerViewHolder, position: Int) {
        val curItem = differ.currentList[position]

        curItem.id?.let {
            holder.bind(
                it,
                curItem.timestamp,
                curItem.distanceInMeters,
                curItem.timeInMillis,
                curItem.avgSpeedKIH
            )
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}