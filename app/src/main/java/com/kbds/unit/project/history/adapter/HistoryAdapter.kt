package com.kbds.unit.project.history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kbds.unit.project.databinding.HistoryItemBinding
import com.kbds.unit.project.history.model.HistoryItemInFragment

class HistoryAdapter : ListAdapter<HistoryItemInFragment, HistoryAdapter.ViewHolder>(diff) {
    private val childHistoryAdapter = ChildHistoryAdapter()
    inner class ViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryItemInFragment) {

            binding.txtHistoryDate.text = item.date
            binding.imgHistoryArrow1.setOnClickListener {
                binding.imgHistoryArrow2.visibility = View.VISIBLE
                binding.imgHistoryArrow2.isVisible = true
                binding.imgHistoryArrow1.isVisible = false
                binding.imgHistoryArrow1.visibility = View.INVISIBLE
            }
            binding.imgHistoryArrow2.setOnClickListener {
                binding.imgHistoryArrow1.visibility = View.VISIBLE
                binding.imgHistoryArrow1.isVisible = true
                binding.imgHistoryArrow2.isVisible = false
                binding.imgHistoryArrow2.visibility = View.INVISIBLE
            }

            binding.childRecyclerView2.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = childHistoryAdapter
            }

        }
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<HistoryItemInFragment>() {
            override fun areItemsTheSame(oldItem: HistoryItemInFragment, newItem: HistoryItemInFragment): Boolean {
                return oldItem.hId === newItem.hId
            }

            override fun areContentsTheSame(oldItem: HistoryItemInFragment, newItem: HistoryItemInFragment): Boolean {
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
        holder.bind(currentList[position])
    }
}