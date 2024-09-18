package com.kbds.unit.project.collections

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.kbds.unit.project.R
import com.kbds.unit.project.collections.adapter.CollectionAdapter
import com.kbds.unit.project.collections.model.CollectionItem
import com.kbds.unit.project.database.AppDatabase
import com.kbds.unit.project.database.model.RequestItem
import com.kbds.unit.project.databinding.AlertBoxForCollectionBinding
import com.kbds.unit.project.databinding.FragmentCollectionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CollectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CollectionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentCollectionBinding // 현 Fragment
    private lateinit var dialogViewBinding: AlertBoxForCollectionBinding // AlertBox
    private var size: Int? = 0

    private var collectionAdapter: CollectionAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCollectionBinding.bind(view)


        // New Collection Popup
        binding.txtNewCollection.setOnClickListener {
            createCollection()
        }
        // New Collection Popup
        binding.imgNewCollection.setOnClickListener {
            createCollection()
        }
        collectionAdapter = CollectionAdapter()

        binding.collectionRecyclerView.apply {
            adapter = collectionAdapter
            layoutManager = LinearLayoutManager(context)
        }


        CoroutineScope(Dispatchers.IO).launch {
            val getCollection = context?.let { AppDatabase.getInstance(it)?.collectionDao()?.getAll() }
            withContext(Dispatchers.Main) {
                collectionAdapter!!.submitList(getCollection)
            }
            if(getCollection?.isNotEmpty() == true || !getCollection.isNullOrEmpty()){
                size = getCollection?.size // size 가지고 넣어주기
            }

        }
    }

    private fun createCollection() {
        dialogViewBinding = AlertBoxForCollectionBinding.inflate(layoutInflater)
        // 팝업 열기
        val alertDialogBox = context?.let {
            AlertDialog.Builder(it)
                .setTitle("CREATE A NEW COLLECTION")
                .setView(dialogViewBinding.root)
                .create()
        }

        alertDialogBox?.show()
        Log.e("CollectionFragment", size.toString())
        // 취소 버튼 클릭
        dialogViewBinding.alertBtnCancel.setOnClickListener {
            alertDialogBox?.dismiss()
        }
        // 생성 버튼 클릭
        dialogViewBinding.alertBtnCreate.setOnClickListener {
            makeCollection(alertDialogBox)
        }
    }

    private fun makeCollection(alertDialogBox: AlertDialog?) {
        var collectionName = dialogViewBinding.alertEditTextCollection.text
        CoroutineScope(Dispatchers.IO).launch {
            val getCollection = context?.let { AppDatabase.getInstance(it)?.collectionDao()?.getAll() }
            size = getCollection?.size // 현재 컬렉션의 크기로 정의
            // 처음에는 빈 값으로 리스트 넣기 (RequestItem)
            val collection = CollectionItem (
                id = (size).toString().plus("_collection"),
                title = collectionName.toString(),
                requestCount = 0
            )

            val requests = mutableListOf<RequestItem>()
            context?.let { AppDatabase?.getInstance(it)?.collectionDao()?.insertCollectionWithRequests(
                collection, requests
            ) }

            Log.d("CollectionFragment_UID", size.toString())

            val reCollection = context?.let { AppDatabase.getInstance(it)?.collectionDao()?.getAll() }
            // Insert하고나서 다시 조회해서 넣어주기
            withContext(Dispatchers.Main) {
                collectionAdapter?.submitList(reCollection)
            }
        }
        // 1. 이미 데이터베이스에 있는지 확인하기 -> 없어야 만들 수 있고 있으면 안된단 문구 날리기
        // collectionAdapter?.submitList()
        // collectionAdapter?.submitList(mutableListOf(CollectionItem("","","",1)))
        alertDialogBox?.dismiss()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CollectionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CollectionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}