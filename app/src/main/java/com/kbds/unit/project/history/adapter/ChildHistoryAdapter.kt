package com.kbds.unit.project.history.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kbds.unit.project.collections.model.CollectionItem
import com.kbds.unit.project.database.AppDatabase
import com.kbds.unit.project.database.model.HistoryItem
import com.kbds.unit.project.database.model.RequestItem
import com.kbds.unit.project.databinding.AlertBoxForHistoryBinding
import com.kbds.unit.project.databinding.HistoryChildItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChildHistoryAdapter() : ListAdapter<HistoryItem, ChildHistoryAdapter.ViewHolder>(diff) {

    inner class ViewHolder(private val binding: HistoryChildItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryItem) {
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
                val collectionText = dialogView.addCollectionType.text.toString()
                val reqItem = currentList[position]

                if (collectionText != "") {
                    saveHistoryToCollection(
                        dialogView.addCollectionType.text.toString(),
                        reqItem.title,
                        reqItem.type,
                        reqItem.url
                    )
                } else {
                    Toast.makeText(binding.root.context, "컬랙션 이름이 추가되지 않았습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            dialogView.alertBtnCancel.setOnClickListener {
                dialog.dismiss()
            }

        }

        private fun saveHistoryToCollection(
            collectionTitle: String,
            requestTitle: String,
            type: String,
            url: String
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                // Collection 이름을 찾기 (입력한 값을 가지고 찾기)
                val findCollectionList =
                    AppDatabase.getInstance(binding.root.context)?.collectionDao()
                        ?.findTitleInHistory(findTitle = collectionTitle) ?: emptyList()

                var findName = ""
                var findCId = -1
                if (findCollectionList.isNotEmpty()) {
                    findName = findCollectionList.first().title
                    findCId = findCollectionList.first().cId
                }
                Log.e("findItem", "findName $findName  findCId $findCId")

                // 입력한 컬랙션 이름을 보았을때 테이블에 존재하는 경우라면 해당 CId를 토대로 RequestTB만 update를 진행하면 됨, reqId는 추가로만 넣어주면 됨
                if (findCId != -1) {
                    AppDatabase.getInstance(binding.root.context)?.requestDao()
                        ?.insertOrUpdateReqItem(
                            RequestItem(
                                collectionId = findCId,
                                type = type,
                                title = requestTitle,
                                url = url
                            )
                        )
                }
                // 그러나 컬랙션 테이블에 없는 경우라면 컬랙션 테이블에 추가를 해주고 requestTB에도 추가해야함
                else {
                    var size = 0
                    val getCollection =
                        AppDatabase.getInstance(binding.root.context)?.collectionDao()?.getAll()
                            ?: mutableListOf()
                    size = getCollection.size // 현재 컬렉션의 크기로 정의
                    // 컬랙션 테이블에 없으니까 입력한 것으로 넣어주기
                    val collections = CollectionItem(
                        id = (size).toString().plus("_collection"),
                        title = collectionTitle,
                        requestCount = 0
                    )

                    val requests = mutableListOf<RequestItem>()
                    requests.add(
                        RequestItem(
                            collectionId = -1,
                            type = type,
                            title = requestTitle,
                            url = url
                        )
                    )
                    AppDatabase.getInstance(binding.root.context)?.collectionDao()
                        ?.insertCollectionWithRequests(collections, requests)

                }
            }
        }
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<HistoryItem>() {
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
        return ViewHolder(
            HistoryChildItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyItem = getItem(position)
        holder.bind(historyItem)
    }
}