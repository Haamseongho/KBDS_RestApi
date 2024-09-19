package com.kbds.unit.project.collections.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kbds.unit.project.R
import com.kbds.unit.project.collections.model.ChildReqItem
import com.kbds.unit.project.collections.model.CollectionItem
import com.kbds.unit.project.database.AppDatabase
import com.kbds.unit.project.database.model.RequestItem
import com.kbds.unit.project.databinding.AlertBoxForCollectionBinding
import com.kbds.unit.project.databinding.AlertBoxMenuBinding
import com.kbds.unit.project.databinding.ItemCollectionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class CollectionAdapter() : ListAdapter<CollectionItem, CollectionAdapter.ViewHolder>(diff) {

    private var childReqAdapter: ChildReqAdapter? = null  // Request 추가용 어댑터

    companion object {
        val diff = object : DiffUtil.ItemCallback<CollectionItem>() {
            override fun areItemsTheSame(
                oldItem: CollectionItem,
                newItem: CollectionItem
            ): Boolean {
                return oldItem.id === newItem.id
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

            val position = adapterPosition
            Log.e("CollectionAdapter_position", "Current Position: $position")
            // Collection 안에 Request도 ListAdapter를 통해 붙이기
            childReqAdapter = ChildReqAdapter() // Adapter 관리
            binding.childRecyclerView.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = childReqAdapter
            }
            // 처음엔 보이지 않도록 할 것
            binding.childRecyclerView.visibility = View.INVISIBLE
            binding.childRecyclerView.isVisible = false

            // 처음 바인딩 될 때 request 값 있으면 가져오기
            getRequestData(position)
            binding.txtCollection.text = item.title
            binding.txtRequest.text = item.requestCount.toString().plus("request")

            // 열었을 때 재조회
            binding.imgCollectionArrow1.setOnClickListener {
                binding.imgCollectionArrow1.isVisible = false
                binding.imgCollectionArrow1.visibility = View.INVISIBLE
                binding.imgCollectionArrow2.isVisible = true
                binding.imgCollectionArrow2.visibility = View.VISIBLE
                binding.childRecyclerView.visibility = View.VISIBLE
                binding.childRecyclerView.isVisible = true
                getRequestData(position)
            }

            binding.imgCollectionArrow2.setOnClickListener {
                binding.imgCollectionArrow1.isVisible = true
                binding.imgCollectionArrow1.visibility = View.VISIBLE
                binding.imgCollectionArrow2.isVisible = false
                binding.imgCollectionArrow2.visibility = View.INVISIBLE
                binding.childRecyclerView.visibility = View.INVISIBLE
                binding.childRecyclerView.isVisible = false
            }

            var pos = adapterPosition
            // 선택 팝업 띄우기 (Rename, Add Folder, Add Request, Delete
            binding.imgMenuCollection.setOnClickListener {
                Log.e("CollectionAdapter333", position.toString())
                val dlgBindingView =
                    AlertBoxMenuBinding.inflate(LayoutInflater.from(binding.root.context))
                val dialog = binding.root.context.let {
                    AlertDialog.Builder(it).setView(dlgBindingView.root).create()
                }
                dialog.show()

                // rename // delete // add Request
                val dlgSubBindingView =
                    AlertBoxForCollectionBinding.inflate(LayoutInflater.from(binding.root.context))
                val subDialog = binding.root.context.let {
                    AlertDialog.Builder(it).setView(dlgSubBindingView.root).create()
                }
                // menu -> Collection rename
                dlgBindingView.rename.setOnClickListener {
                    dlgSubBindingView.alertBtnCreate.text = "Edit"
                    dlgSubBindingView.alertBtnCancel.text = "Cancel"
                    dlgSubBindingView.alertEditTextCollection.isVisible = true
                    dlgSubBindingView.alertEditTextCollection.visibility = View.VISIBLE
                    dialog.dismiss() // 기존꺼는 내리기
                    dlgSubBindingView.alertBtnCreate.setOnClickListener {
                        if (dlgSubBindingView.alertEditTextCollection.text.toString() !== "" && dlgSubBindingView.alertEditTextCollection.text.isNotEmpty()) {
                            updateRequest(
                                dlgSubBindingView.alertEditTextCollection.text.toString(),
                                position
                            )
                        } else {
                            Toast.makeText(
                                binding.root.context,
                                "Do not leave it empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        subDialog.dismiss()
                    }

                    dlgSubBindingView.alertBtnCancel.setOnClickListener {
                        subDialog.dismiss()
                    }

                    subDialog.setTitle("Rename")
                    subDialog.show()
                }
                // menu -> Colleciton addRequest
                dlgBindingView.addRequest.setOnClickListener {
                    dlgSubBindingView.alertBtnCreate.text = "Create"
                    dlgSubBindingView.alertBtnCancel.text = "Cancel"
                    dlgSubBindingView.alertEditTextCollection.isVisible = true
                    dlgSubBindingView.alertEditTextCollection.visibility = View.VISIBLE
                    dlgSubBindingView.addRequestType.visibility = View.VISIBLE
                    dlgSubBindingView.addRequestType.isVisible = true
                    dialog.dismiss() // 기존꺼는 내리기
                    dlgSubBindingView.alertBtnCreate.setOnClickListener {
                        if (dlgSubBindingView.alertEditTextCollection.text.toString() !== "" && dlgSubBindingView.alertEditTextCollection.text.isNotEmpty()
                            && dlgSubBindingView.addRequestType.text.toString() !== "" && dlgSubBindingView.addRequestType.text.isNotEmpty()
                        ) {

                            addRequest(
                                dlgSubBindingView.alertEditTextCollection.text.toString(),
                                dlgSubBindingView.addRequestType.text.toString(),
                                position
                            )
                        } else {
                            Toast.makeText(
                                binding.root.context,
                                "Do not leave it empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        subDialog.dismiss()
                    }

                    dlgSubBindingView.alertBtnCancel.setOnClickListener {
                        subDialog.dismiss()
                    }

                    subDialog.setTitle("CREATE A NEW REQUEST")
                    subDialog.show()
                }
                // menu -> Collection delete
                dlgBindingView.delete.setOnClickListener {
                    dlgSubBindingView.alertBtnCreate.text = "Delete"
                    dlgSubBindingView.alertBtnCancel.text = "Cancel"
                    dlgSubBindingView.alertEditTextCollection.isVisible = false
                    dlgSubBindingView.alertEditTextCollection.visibility = View.GONE
                    dlgSubBindingView.alertTxtName.isVisible = false
                    dlgSubBindingView.alertTxtName.visibility = View.GONE
                    dlgSubBindingView.addRequestType.isVisible = false
                    dlgSubBindingView.addRequestType.visibility = View.GONE
                    dialog.dismiss() // 기존꺼는 내리기d
                    subDialog.setTitle("DELETE REQUEST")
                    subDialog.setTitle("Do you really want to delete your own request?")

                    dlgSubBindingView.alertBtnCreate.setOnClickListener {
                        deleteRequest(position)
                        subDialog.dismiss()
                    }

                    dlgSubBindingView.alertBtnCancel.setOnClickListener {
                        subDialog.dismiss()
                    }

                    subDialog.show()
                }

            }

        }


        // Delete -> 선택한 Collection 지우기
        // d이부분 좀 더 해야함
        private fun deleteRequest(position: Int) {
            if (position != RecyclerView.NO_POSITION) {
                val item = currentList[position]
                CoroutineScope(Dispatchers.IO).launch {
                    AppDatabase.getInstance(binding.root.context)?.collectionDao()
                        ?.deleteById(id = item.id)
                    // Collection이 삭제되면 그거에 맞은 키로 비교해서 Request도 제거하기
                    AppDatabase.getInstance(binding.root.context)?.requestDao()
                        ?.deleteByCollectionId(id = item.cId)

                    val collectionList =
                        AppDatabase.getInstance(binding.root.context)?.collectionDao()
                            ?.getAll()

                    withContext(Dispatchers.Main) {
                        submitList(collectionList) {
                            Log.d("currentList:::", currentList.toString())
                        }
                    }
                }
            } else {
                Log.e("CollectionAdapter_error", "$position 변화됨")
            }
        }

        private fun addRequest(curTitle: String, curType: String, position: Int) {
            if (position != RecyclerView.NO_POSITION) {
                val item = currentList[position]
                val cId = item.cId
                val id = item.id

                // 추가된 Request 다시 조회 후 보여주기
                CoroutineScope(Dispatchers.IO).launch {
                    val childRequestItem =
                        AppDatabase.getInstance(binding.root.context)?.collectionDao()
                            ?.getCollectionWithRequests(collectionId = cId)

                    val requestItemList = mutableListOf<ChildReqItem>()

                    // 추가한 것 리스트에 넣어주기
                    requestItemList.add(
                        ChildReqItem(
                            collectionId = cId,
                            type = curType,
                            title = curTitle
                        )
                    )

                    // RequestItem 쪽에다가 추가된 것들만 넣어주기
                    for (itemList in requestItemList) {
                        AppDatabase.getInstance(binding.root.context)?.requestDao()
                            ?.insertOrUpdateReqItem(
                                RequestItem(
                                    collectionId = itemList.collectionId,
                                    type = itemList.type,
                                    title = itemList.title
                                )
                            )
                    }
                    // 추가된 것 먼저 넣어준 다음에 기존꺼를 다시 조회해서 넣어주고 리스트 구성
                    for (reqItemList in childRequestItem!!.requestList) {
                        Log.e(
                            "reqItemList",
                            reqItemList.collectionId.toString().plus("///")
                                .plus(reqItemList.reqId.toString())
                        )
                        requestItemList.add(
                            ChildReqItem(
                                collectionId = reqItemList.collectionId,
                                type = reqItemList.type,
                                title = reqItemList.title
                            )
                        )
                    }
                    // 구성된 리스트로 사이즈 재조정
                    val size = requestItemList.size
                    AppDatabase.getInstance(binding.root.context)?.collectionDao()
                        ?.updateRequestCount(size = size, collectionId = cId)
                    // 사이즈 변경 후 재조회해서 submitList에 넣어주기
                    val reCollection =
                        AppDatabase.getInstance(binding.root.context)?.collectionDao()
                            ?.getAll()

                    // 화면 반영용은 Main 스레드에서 넣어줄 것
                    withContext(Dispatchers.Main) {
                        submitList(reCollection)
                        // cId를 토대로 구한 리스트 이므로 그대로 넣어줘서 갱신하기
                        childReqAdapter!!.submitList(requestItemList)
                    }
                }
            }
        }

        private fun updateRequest(title: String, position: Int) {
            if (position != RecyclerView.NO_POSITION) {
                val item = currentList[position]
                CoroutineScope(Dispatchers.IO).launch {
                    AppDatabase.getInstance(binding.root.context)?.collectionDao()
                        ?.renameCollection(id = item.id, title = title)

                    val collectionList =
                        AppDatabase.getInstance(binding.root.context)?.collectionDao()
                            ?.getAll()

                    withContext(Dispatchers.Main) {
                        submitList(collectionList) {
                            Log.d("currentList:::", currentList.toString())
                        }
                    }
                }
            } else {
                Log.e("CollectionAdapter_error", "$position 변화됨")
            }
        }

        // Collection 안에 Request 가져오기
        private fun getRequestData(position: Int) {
            val item = currentList[position]
            val cId = item.cId
            val id = item.id
            // 추가된 Request 다시 조회 후 보여주기
            CoroutineScope(Dispatchers.IO).launch {
                val childRequestItem =
                    AppDatabase.getInstance(binding.root.context)?.collectionDao()
                        ?.getCollectionWithRequests(collectionId = item.cId)

                if (childRequestItem?.requestList?.size != 0 && childRequestItem?.requestList!!.isNotEmpty()) {
                    val requestItemList = mutableListOf<ChildReqItem>()
                    for (reqItemList in childRequestItem.requestList) {
                        requestItemList.add(
                            ChildReqItem(
                                collectionId = reqItemList.collectionId,
                                type = reqItemList.type,
                                title = reqItemList.title
                            )
                        )
                    }
                    // Main Thread
                    withContext(Dispatchers.Main) {
                        childReqAdapter!!.submitList(requestItemList)
                    }
                }
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