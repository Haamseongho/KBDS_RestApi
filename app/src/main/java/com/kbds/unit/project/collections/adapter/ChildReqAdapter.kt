package com.kbds.unit.project.collections.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.kbds.unit.project.R
import com.kbds.unit.project.api.ApiFragment
import com.kbds.unit.project.collections.CollectionFragment
import com.kbds.unit.project.collections.model.ChildReqItem
import com.kbds.unit.project.database.AppDatabase
import com.kbds.unit.project.databinding.ActivityMainBinding
import com.kbds.unit.project.databinding.AlertBoxChildMenuBinding
import com.kbds.unit.project.databinding.AlertBoxForCollectionBinding
import com.kbds.unit.project.databinding.ChildRequestItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChildReqAdapter(
    private val listener: ChildReqAdapterListener
) : ListAdapter<ChildReqItem, ChildReqAdapter.ViewHolder>(diff) {

    inner class ViewHolder(private val binding: ChildRequestItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChildReqItem) {
            Log.e("position!!!!!", adapterPosition.toString())
            Log.e("ChildReqID", item.reqId.toString().plus("clicked"))
            val item2 = currentList[adapterPosition]
            Log.e("items@@@@@@", item2.toString())
            binding.root.setOnClickListener {
                listener.onChildItemClicked(item)
            }

            binding.childReqTitle.setOnClickListener {
                listener.onChildItemClicked(item)
            }
            binding.childReqType.setOnClickListener {
                listener.onChildItemClicked(item)
            }
            // childMenu
            val dialogView =
                AlertBoxChildMenuBinding.inflate(LayoutInflater.from(binding.root.context))
            val dialog = binding.root.context.let {
                AlertDialog.Builder(it).setView(dialogView.root).create()
            }

            binding.childReqTitle.text = item.title
            binding.childReqType.text = item.type
            if (binding.childReqType.text.toString().contains("GET")) {
                binding.childReqType.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.type_get
                    )
                ) // R.color.type_get
            } else if (binding.childReqType.text.toString().contains("POST")) {
                binding.childReqType.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.type_post
                    )
                ) // R.color.type_post
            } else if (binding.childReqType.text.toString().contains("PUT")) {
                binding.childReqType.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.type_put
                    )
                ) // R.color.type_put
            } else if (binding.childReqType.text.toString().contains("DELETE")) {
                binding.childReqType.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.type_delete
                    )
                ) // R.color.type_delete
            } else {
                binding.childReqType.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.type_else
                    )
                ) // R.color.type_else
            }

            binding.childReqImage.setOnClickListener {
                // menu child popup open
                dialog.show()
            }

            dialogView.childReqRename.setOnClickListener {
                // DB 수정(RequestItem)
                renameReqTitleByPopup()
                dialog.dismiss()
            }

            dialogView.childReqDelete.setOnClickListener {
                deleteReqData()
                dialog.dismiss()
            }
        }

        private fun renameReqTitleByPopup() {
            // rename // delete // add Request
            val dlgSubBindingView =
                AlertBoxForCollectionBinding.inflate(LayoutInflater.from(binding.root.context))
            val subDialog = binding.root.context.let {
                AlertDialog.Builder(it).setView(dlgSubBindingView.root).create()
            }
            // menu -> Collection rename

            dlgSubBindingView.alertBtnCreate.setOnClickListener {
                if (dlgSubBindingView.alertEditTextCollection.text.toString() == "" || dlgSubBindingView.alertEditTextCollection.text.isNullOrEmpty()) {
                    Toast.makeText(binding.root.context, "변경할 이름을 입력해주세요", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    renameReqTitle(dlgSubBindingView.alertEditTextCollection.text.toString())
                }
                subDialog.dismiss()
            }

            dlgSubBindingView.alertBtnCancel.setOnClickListener {
                subDialog.dismiss()
            }

            subDialog.show()
        }

        private fun renameReqTitle(renameTextView: String) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val item = currentList[position]
                CoroutineScope(Dispatchers.IO).launch {
                    // ID로 해야하는데 좀 꼬이는 게 있어서 타이틀로 우선 대체
                    AppDatabase.getInstance(context = binding.root.context)?.requestDao()
                        ?.updateReqTitleByReqTitle(renameTextView, item.title)
                    val childReqItemList = mutableListOf<ChildReqItem>()
                    val reqItemLists =
                        AppDatabase.getInstance(context = binding.root.context)?.requestDao()
                            ?.getAll()

                    for (reqItem in reqItemLists!!) {
                        childReqItemList.add(
                            ChildReqItem(
                                reqItem.reqId,
                                reqItem.collectionId,
                                reqItem.type,
                                reqItem.title
                            )
                        )
                    }
                    withContext(Dispatchers.Main) {
                        submitList(childReqItemList)
                    }
                }
            }

        }

        private fun deleteReqData() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val item = currentList[position]
                Log.e(
                    "ChildReqAdapter!!!",
                    position.toString().plus("///").plus(item.reqId).plus("??").plus(item.title)
                )
                CoroutineScope(Dispatchers.IO).launch {
                    // reqId는 유니크한 아이디이기에 이걸로 찾아서 삭제할 것
                    // id가 계속 잘 안먹혀서 타이틀로 우선 교체
                    AppDatabase.getInstance(context = binding.root.context)?.requestDao()
                        ?.deleteByReqTitle(item.title)
                    // 삭제 먼저하고 재조회한 다음 전체 데이터 리스트에 다시 넣고 정리해서 submitList에 담아 변경내용 관리

                    // 선택한 reqId의 CollectionId를 가지고 Collection & Request 값 반환받기
                    // 위에서 이미 하나 지워졌기에 collectionId에 엮인 reqId는 1:(n-1) 관계가 될 것 입니다.
                    val requestItemList =
                        AppDatabase.getInstance(binding.root.context)?.collectionDao()
                            ?.getCollectionWithRequests(collectionId = item.collectionId)
                    // 위에서 구한 값으로 Request 갯수를 구해서 CollectionTB에 업데이트를 진행합니다.
                    // collectionDao에서는 cId가 즉, collectionId가 유니크한 값
                    AppDatabase.getInstance(binding.root.context)?.collectionDao()
                        ?.updateRequestCount(
                            requestItemList?.requestList?.size ?: 0,
                            item.collectionId
                        )

                    // 업데이트 한 것을 가지고 다시 조회하기!
                    // cId는 유니크한 값입니다 (in collectionTB)
                    val reCollection =
                        AppDatabase.getInstance(binding.root.context)?.collectionDao()
                            ?.getCollectionWithRequests(collectionId = item.collectionId)
                    Log.e("ChildReqAdapter_List", requestItemList.toString())
                    Log.e("ChildReqAdapter_Size", reCollection?.requestList?.size.toString())
                    val reCollectionList =
                        AppDatabase.getInstance(binding.root.context)?.collectionDao()
                            ?.getAll()

                    val childReqItemSubList = mutableListOf<ChildReqItem>()
                    for (childReqItem in reCollection?.requestList ?: mutableListOf()) {
                        childReqItemSubList.add(
                            ChildReqItem(
                                collectionId = childReqItem.collectionId,
                                type = childReqItem.type,
                                title = childReqItem.title
                            )
                        )
                    }
                    withContext(Dispatchers.Main) {
                        submitList(childReqItemSubList)
                        // collectionAdapter.submitList(reCollectionList)
                    }
                }
            }

        }
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<ChildReqItem>() {
            override fun areItemsTheSame(oldItem: ChildReqItem, newItem: ChildReqItem): Boolean {
                return oldItem.reqId == newItem.reqId // id 값 계속 변경해야함
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
        Log.e("ChildAdapter_Position", position.toString());
        holder.bind(currentList[position])
    }
}

interface ChildReqAdapterListener {
    fun onChildItemClicked(data: ChildReqItem)
}