package com.kbds.unit.project.history

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.kbds.unit.project.R
import com.kbds.unit.project.database.AppDatabase
import com.kbds.unit.project.database.model.HistoryItem
import com.kbds.unit.project.databinding.FragmentHistoryBinding
import com.kbds.unit.project.history.adapter.HistoryAdapter
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
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHistoryBinding
    private val historyAdapter = HistoryAdapter()
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
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHistoryBinding.bind(view)
        initViews()
    }

    private fun initViews() {
        val historyRecyclerView = binding.historyRecyclerView
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        historyRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = historyAdapter
        }

        // 날짜로 submitList 처리
        CoroutineScope(Dispatchers.IO).launch {
            val historyList =
                context?.let { AppDatabase.getInstance(it)?.historyDao()?.getAllHistoryData() }
            val groupedData = historyList?.groupBy { it.date }
            // testList를 사용하여 HistoryItem 추가
            withContext(Dispatchers.Main) {
                groupedData?.let { historyAdapter.submitList(it.toList()) }
            }
        }

        binding.txtHistoryClear.setOnClickListener {
            isReallyDeleteAllHistory()
        }
    }

    private fun resumeData(){
        // 날짜로 submitList 처리
        CoroutineScope(Dispatchers.IO).launch {
            val historyList =
                context?.let { AppDatabase.getInstance(it)?.historyDao()?.getAllHistoryData() }
            val groupedData = historyList?.groupBy { it.date }
            // testList를 사용하여 HistoryItem 추가
            withContext(Dispatchers.Main) {
                groupedData?.let { historyAdapter.submitList(it.toList()) }
            }
        }
    }

    private fun isReallyDeleteAllHistory() {
        val dlg = AlertDialog.Builder(context).apply {
            setTitle("히스토리 삭제")
            setMessage("히스토리를 정말 모두 삭제할 것인가요? 삭제하실거면 YES, 아니면 NO를 눌러주세요")
            setNegativeButton("NO", null)
            setPositiveButton("YES") { dialog, _ ->
                removeAllHistory()
                dialog.dismiss()
            }
        }.create()

        dlg.show()
    }
    // 재시작했을때 재조회
    private fun removeAllHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            context?.let { it1 -> AppDatabase.getInstance(it1)?.historyDao()?.deleteAllHistory() }
            withContext(Dispatchers.Main) {
                historyAdapter.submitList(mutableListOf())
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        resumeData()
    }
}