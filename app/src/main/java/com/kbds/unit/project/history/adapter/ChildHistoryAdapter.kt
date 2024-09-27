package com.kbds.unit.project.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kbds.unit.project.databinding.AlertBoxForHistoryBinding
import com.kbds.unit.project.databinding.HistoryChildItemBinding
import com.kbds.unit.project.history.model.ChildHistoryItem

class ChildHistoryAdapter: ListAdapter<ChildHistoryItem, ChildHistoryAdapter.ViewHolder> (diff){

    inner class ViewHolder(private val binding: HistoryChildItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: ChildHistoryItem){
            binding.childReqTitle2.text = item.title
            binding.childReqType2.text = item.type
            val dialogView =
                AlertBoxForHistoryBinding.inflate(LayoutInflater.from(binding.root.context))
            val dialog = AlertDialog.Builder(binding.root.context).apply {
                setTitle("SAVE REQUEST")
                setView(dialogView.root)
            }.create()
            binding.childReqImage2.setOnClickListener {
                dialog.show()
            }
        }
    }
    companion object {
        val diff = object: DiffUtil.ItemCallback<ChildHistoryItem>(){
            override fun areItemsTheSame(
                oldItem: ChildHistoryItem,
                newItem: ChildHistoryItem
            ): Boolean {
                return oldItem.hId === newItem.hId
            }

            override fun areContentsTheSame(
                oldItem: ChildHistoryItem,
                newItem: ChildHistoryItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(HistoryChildItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}