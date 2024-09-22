package com.kbds.unit.project.api

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import androidx.core.view.size
import com.kbds.unit.project.R
import com.kbds.unit.project.databinding.FragmentApiBinding
import com.kbds.unit.project.service.Network

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ApiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ApiFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentApiBinding
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
        return inflater.inflate(R.layout.fragment_api, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentApiBinding.bind(view)

        initViews()
    }

    private fun initViews() {
        val itemList = listOf("GET", "POST", "PUT", "DELETE")
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
        val sharedPreferences =
            binding.root.context.getSharedPreferences("request", Context.MODE_PRIVATE)

        val collectionId = sharedPreferences.getInt("COLLECTION_ID", 0)
        val type = sharedPreferences.getString("TYPE", "GET")
        val title = sharedPreferences.getString("TITLE", "")
        val url = sharedPreferences.getString("URL", "")

        // 값 분배하고 삭제하기
        sharedPreferences.edit().remove("COLLECTION_ID")
        sharedPreferences.edit().remove("TYPE")
        sharedPreferences.edit().remove("TITLE")
        sharedPreferences.edit().remove("URL")
        sharedPreferences.edit().apply()

        val spinner = binding.apiSpinner

        Log.e("APIFragment", "CollectionID: $collectionId , Type: $type , Title: $title, URL: $url")


        binding.apiSpinner.apply {
            adapter = spinnerAdapter
        }

        binding.apiReqTabLayout.addTab(
            binding.apiReqTabLayout.newTab().setText("Params")
        )
        binding.apiReqTabLayout.addTab(
            binding.apiReqTabLayout.newTab().setText("Headers")
        )
        binding.apiReqTabLayout.addTab(
            binding.apiReqTabLayout.newTab().setText("Body")
        )

        binding.apiTitle.text = title
        val defaultSelectionIndex = itemList.indexOf(type)
        spinner.setSelection(defaultSelectionIndex)

        // 선택된 항목 처리
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = itemList[position]
                Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무것도 선택되지 않았을 때 처리
            }
        }

        val apiEditText = binding.apiUrlEditText
        val btnSend = binding.apiBtnSend
        val apiResponse = binding.apiResponseTextView

        btnSend.setOnClickListener {
            sendRequestForApi(apiEditText.text.toString())
        }

        // 체크박스 선택시 Row추가
        binding.apiCheckBox1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addItemRow(binding.apiTableLayout, 1)
            } else {
                removeItemRow(binding.apiTableLayout, 2)
            }
        }
    }


    private fun sendRequestForApi(url: String) {
        val apiTableLayout = binding.apiTableLayout
        for (i in 1 until apiTableLayout.childCount) {
            // Row
            val row = apiTableLayout.getChildAt(i)
            if (row is TableRow) {
                // tableRow row 순회
                for (j in 1 until row.childCount) {
                    var view = row.getChildAt(j)
                    Log.e("TEST", view.id.toString())
                }
            }
        }
        //  Network.getInstance().getService()
    }

    private fun addItemRow(tableLayout: TableLayout, position: Int) {
        val newRow = TableRow(binding.root.context)
        val newCheckBox = CheckBox(binding.root.context).apply {
            buttonTintList =
                ContextCompat.getColorStateList(binding.root.context, R.color.checkbox_status)
            (position + 1).also { id = it } // id값
            setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    Log.e("checkButton Clicked", "${buttonView.id} is checked")
                    addItemRow(tableLayout, position + 1)
                } else {
                    Log.e("checkButton Clicked", "${buttonView.id} is unchecked")
                    removeItemRow(tableLayout, (position + 1))
                }
            }
        }

        val keyEditText = EditText(binding.root.context).apply {
            gravity = Gravity.CENTER
            id = (position + 10)  // 11, 21, 31, 41, ... 새로 생긴 Key의 아이디값
        }
        val valueEditText = EditText(binding.root.context).apply {
            gravity = Gravity.CENTER
            id = (position + 11) // 12, 22, 32, 42, ...
        }

        val layoutParams1 = TableRow.LayoutParams(
            0,
            150,
        ).apply {
            weight = 1f
            marginStart = 15
        }

        val layoutParams2 = TableRow.LayoutParams(
            0,
            150,
        ).apply {
            weight = 2f
        }
        val layoutParams3 = TableRow.LayoutParams(
            0,
            150,
        ).apply {
            weight = 5f
        }

        newCheckBox.layoutParams = layoutParams1
        keyEditText.layoutParams = layoutParams2
        valueEditText.layoutParams = layoutParams3

        newRow.addView(newCheckBox)
        newRow.addView(keyEditText)
        newRow.addView(valueEditText)


        tableLayout.addView(newRow)

        // 아래 길이도 늘리기
        val apiResponseTextView = binding.apiResponseTextView
        val apiResponseHeight = apiResponseTextView.height
        val apiResponseParam = binding.apiResponseTextView.layoutParams
        apiResponseParam.height += 150
        binding.apiResponseTextView.layoutParams = apiResponseParam

        Log.e("APIResponseHeight", apiResponseHeight.toString())
        Log.e("APIResponseHeight2", binding.apiResponseTextView.height.toString())
    }

    private fun removeItemRow(tableLayout: TableLayout, position: Int) {
        // 행이 2 이상일 때만 제거하기
        if (position >= 2) {
            tableLayout.removeViewAt(position)
        }

        // 아래 길이도 늘리기
        val apiResponseTextView = binding.apiResponseTextView
        val apiResponseHeight = apiResponseTextView.height
        val apiResponseParam = binding.apiResponseTextView.layoutParams
        apiResponseParam.height -= 150
        binding.apiResponseTextView.layoutParams = apiResponseParam

        Log.e("APIResponseHeight", apiResponseHeight.toString())
        Log.e("APIResponseHeight2", binding.apiResponseTextView.height.toString())
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ApiFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ApiFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}