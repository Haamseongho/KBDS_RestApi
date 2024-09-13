package com.kbds.unit.project.collections.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kbds.unit.project.collections.model.CollectionItem
import com.kbds.unit.project.databinding.ItemCollectionBinding

class CollectionAdapter : ListAdapter<CollectionItem, CollectionAdapter.ViewHolder>(diff) {
    companion object {
        val diff = object : DiffUtil.ItemCallback<CollectionItem>() {
            override fun areItemsTheSame(
                oldItem: CollectionItem,
                newItem: CollectionItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CollectionItem,
                newItem: CollectionItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class ViewHolder(private val binding: ItemCollectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CollectionItem) {
            binding.txtCollection.text = item.title
            binding.txtRequest.text = item.requestCount
            binding.imgCollectionArrow1.setOnClickListener {
                binding.imgCollectionArrow1.isVisible = false
                binding.imgCollectionArrow1.visibility = View.INVISIBLE
                binding.imgCollectionArrow2.isVisible = true
                binding.imgCollectionArrow2.visibility = View.VISIBLE
            }

            binding.imgCollectionArrow2.setOnClickListener {
                binding.imgCollectionArrow1.isVisible = true
                binding.imgCollectionArrow1.visibility = View.VISIBLE
                binding.imgCollectionArrow2.isVisible = false
                binding.imgCollectionArrow2.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCollectionBinding.inflate(
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