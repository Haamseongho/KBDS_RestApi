package com.kbds.unit.project.collections.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kbds.unit.project.collections.model.ChildReqItem
import com.kbds.unit.project.databinding.AlertBoxChildMenuBinding
import com.kbds.unit.project.databinding.ChildRequestItemBinding

class ChildReqAdapter : ListAdapter<ChildReqItem, ChildReqAdapter.ViewHolder>(diff) {

    inner class ViewHolder(private val binding: ChildRequestItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChildReqItem) {
            val currentPos = adapterPosition

            // childMenu
            val dialogView =
                AlertBoxChildMenuBinding.inflate(LayoutInflater.from(binding.root.context))
            val dialog = binding.root.context.let {
                AlertDialog.Builder(it).setView(dialogView.root).create()
            }

            binding.childReqTitle.text = item.title
            binding.childReqType.text = item.type
            binding.childReqImage.setOnClickListener {
                // menu child popup open
                dialog.show()
            }

            dialogView.childReqRename.setOnClickListener {
                dialog.dismiss()
            }

            dialogView.childReqDelete.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<ChildReqItem>() {
            override fun areItemsTheSame(oldItem: ChildReqItem, newItem: ChildReqItem): Boolean {
                return oldItem.id == newItem.id // id 값 계속 변경해야함
            }

            override fun areContentsTheSame(oldItem: ChildReqItem, newItem: ChildReqItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChildRequestItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}