package com.kbds.unit.project.history.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kbds.unit.project.database.AppDatabase
import com.kbds.unit.project.database.model.HistoryItem
import com.kbds.unit.project.databinding.HistoryItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryAdapter : ListAdapter<Pair<String, List<HistoryItem>>, HistoryAdapter.ViewHolder>(diff) {

    inner class ViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(date: String, historyList: List<HistoryItem>) {
            val position = adapterPosition
            binding.txtHistoryDate.text = date
            binding.imgHistoryArrow1.setOnClickListener {
                binding.imgHistoryArrow2.visibility = View.VISIBLE
                binding.imgHistoryArrow2.isVisible = true
                binding.imgHistoryArrow1.isVisible = false
                binding.imgHistoryArrow1.visibility = View.INVISIBLE
            //    refreshData(position)
            }
            binding.imgHistoryArrow2.setOnClickListener {
                binding.imgHistoryArrow1.visibility = View.VISIBLE
                binding.imgHistoryArrow1.isVisible = true
                binding.imgHistoryArrow2.isVisible = false
                binding.imgHistoryArrow2.visibility = View.INVISIBLE
            }

            val childHistoryAdapter = ChildHistoryAdapter()
            val linearLayoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            binding.childRecyclerView2.apply {
                layoutManager = linearLayoutManager
                adapter = childHistoryAdapter
                setHasFixedSize(false)  // 가변 크기로 설정
            }
            childHistoryAdapter.submitList(historyList)
        }

        private fun refreshData(position: Int) {
            if (position != RecyclerView.NO_POSITION) {

            }
        }
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<Pair<String, List<HistoryItem>>>() {
            override fun areItemsTheSame(
                oldItem: Pair<String, List<HistoryItem>>,
                newItem: Pair<String, List<HistoryItem>>
            ): Boolean {
                return oldItem.first == newItem.first
            }

            override fun areContentsTheSame(
                oldItem: Pair<String, List<HistoryItem>>,
                newItem: Pair<String, List<HistoryItem>>
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HistoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (date, historyList) = getItem(position)
        holder.bind(date, historyList)
    }

}

