package com.kbds.unit.project.history.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kbds.unit.project.database.AppDatabase
import com.kbds.unit.project.database.model.HistoryItem
import com.kbds.unit.project.database.model.RequestItem
import com.kbds.unit.project.databinding.AlertBoxForHistoryBinding
import com.kbds.unit.project.databinding.HistoryChildItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChildHistoryAdapter(): ListAdapter<HistoryItem, ChildHistoryAdapter.ViewHolder> (diff){

    inner class ViewHolder(private val binding: HistoryChildItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: HistoryItem){
            binding.childReqTitle2.text = item.title
            binding.childReqType2.text = item.type
            val position = adapterPosition
            val dialogView =
                AlertBoxForHistoryBinding.inflate(LayoutInflater.from(binding.root.context))
            val dialog = AlertDialog.Builder(binding.root.context).apply {
                setTitle("SAVE REQUEST")
                setView(dialogView.root)
            }.create()
            binding.childReqImage2.setOnClickListener {
                dialog.show()
            }

            dialogView.alertBtnSave.setOnClickListener {
                dialog.dismiss()
                val text = dialogView.addRequestType.text.toString()
                val reqItem = currentList[position]
                Log.e("reqItemInChildHistoryAdapter", "TEST : ${reqItem.toString()} text : $text" )
                if(text != ""){
                    saveHistoryToCollection(dialogView.addRequestType.text.toString(), reqItem.type, reqItem.url)
                } else {
                    Toast.makeText(binding.root.context,"컬랙션 이름이 추가되지 않았습니다.", Toast.LENGTH_SHORT).show()
                }
            }


        }

        private fun saveHistoryToCollection(collectionName: String, type: String, url: String) {
            CoroutineScope(Dispatchers.IO).launch {
                // Collection 이름을 찾기 (입력한 값을 가지고 찾기)
                val findCollectionList = AppDatabase.getInstance(binding.root.context)?.collectionDao()
                    ?.findTitleInHistory(findTitle = collectionName) ?: emptyList()
                val findName = findCollectionList.first().title
                val findCId = findCollectionList.first().cId

                // Collection Name이 있기 떄문에 여기에 그대로 request 형태로 Insert하기
                if(findName != ""){
                    /*
                    1. findName, cId 찾기
                    2. findName, cId를 토대로 RequestTB에 넣어주면 CollectionId를 토대로 어차피 조회가 되기 때문에 자동으로 Collection 쪽에 Request가 들어갈 예정
                     */
                    // 히스토리에서 선택한 것으로 타입, url 따서 insert or Update 진행한다.
                    AppDatabase.getInstance(binding.root.context)?.requestDao()
                        ?.insertOrUpdateReqItem(RequestItem(collectionId = findCId, type = type, title = findName, url = url))
                }
                else {
                    AppDatabase.getInstance(binding.root.context)?.requestDao()
                        ?.insertNewCollection(RequestItem(collectionId = findCId, type = type, title = findName, url = url))
                }
            }

        }
    }
    companion object {
        val diff = object: DiffUtil.ItemCallback<HistoryItem>(){
            override fun areItemsTheSame(
                oldItem: HistoryItem,
                newItem: HistoryItem
            ): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(
                oldItem: HistoryItem,
                newItem: HistoryItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(HistoryChildItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyItem = getItem(position)
        holder.bind(historyItem)
    }
}