package com.kbds.unit.project.api

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.room.util.query
import com.google.android.material.tabs.TabLayout
import com.kbds.unit.project.R
import com.kbds.unit.project.databinding.FragmentApiBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

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
    private var isFirstOpen: Boolean = true
    val client = OkHttpClient()
    var selectedItem = ""

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

    private fun initResumeData() {
        val itemList = listOf("GET", "POST", "PUT", "DELETE")
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
        val spinner = binding.apiSpinner
        binding.apiSpinner.apply {
            adapter = spinnerAdapter
        }
        val sharedPreferences =
            binding.root.context.getSharedPreferences("request", Context.MODE_PRIVATE)

        val title = sharedPreferences.getString("TITLE", "")
        binding.apiTitle.text = title
        val type = sharedPreferences.getString("TYPE", "GET")

        val defaultSelectionIndex = itemList.indexOf(type)
        spinner.setSelection(defaultSelectionIndex)
    }

    @SuppressLint("CommitPrefEdits")
    private fun initViews() {
        // 처음 초기에만 발생
        isFirstOpen = false

        val itemList = listOf("GET", "POST", "PUT", "DELETE")
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
        val sharedPreferences =
            binding.root.context.getSharedPreferences("request", Context.MODE_PRIVATE)

        val collectionId = sharedPreferences.getInt("COLLECTION_ID", 0)
        val type = sharedPreferences.getString("TYPE", "GET")
        val title = sharedPreferences.getString("TITLE", "")
        val url = sharedPreferences.getString("URL", "")

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
                selectedItem = itemList[position]
                Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무것도 선택되지 않았을 때 처리
            }
        }
        val apiReqTabLayout = binding.apiReqTabLayout
        val selectedTabPosition = apiReqTabLayout.selectedTabPosition

        // constraintLayout
        val apiConstraintLayout = binding.apiConstraintLayout

        if (selectedTabPosition == 0 || selectedTabPosition == 1) {
            binding.apiTableLayout.visibility = View.VISIBLE
            binding.apiTableLayout.isVisible = true
            switchPositionByTab(apiConstraintLayout, selectedTabPosition)
        } else {
            binding.apiTableLayout.visibility = View.GONE
            binding.apiTableLayout.isVisible = false
            switchPositionByTab(apiConstraintLayout, selectedTabPosition)
        }

        val apiEditText = binding.apiUrlEditText
        val btnSend = binding.apiBtnSend
        val apiResponse = binding.apiResponseTextView

        // tab 이벤트
        apiReqTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0 || tab?.position == 1) {
                    binding.apiTableLayout.visibility = View.VISIBLE
                    binding.apiTableLayout.isVisible = true
                } else {
                    binding.apiTableLayout.visibility = View.GONE
                    binding.apiTableLayout.isVisible = false
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.e("ApiFragment_TabUnSelected", "${tab?.position} 선택되지 않았습니다.")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.e("ApiFragment_TabReSelected", "${tab?.position} 다시 선택되었습니다.")
            }

        })
        btnSend.setOnClickListener {
            // 같지 않다면 선택한 값
            if (type?.toString() !== selectedItem) {
                sendRequestForApi(apiEditText.text.toString(), selectedItem)
            }
            // 같다면 선택한 값
            else {
                sendRequestForApi(apiEditText.text.toString(), type)
            }

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

    private fun switchPositionByTab(apiConstraintLayout: ConstraintLayout, selectedTabPosition: Int) {

        // 제약 조건 적용 후 뷰의 가시성 설정
        if (selectedTabPosition == 0 || selectedTabPosition == 1) {
            binding.apiTableLayout.visibility = View.VISIBLE
            binding.apiReqBodyEditText.visibility = View.GONE
        } else {
            binding.apiTableLayout.visibility = View.GONE
            binding.apiReqBodyEditText.visibility = View.VISIBLE
        }
        val constraintSet = ConstraintSet() // 제약조건 세트 관리
        constraintSet.clone(apiConstraintLayout)
        if(selectedTabPosition == 0 || selectedTabPosition == 1){
            constraintSet.clear(R.id.apiResponseLabel, ConstraintSet.TOP) // Top에 대한 제약조건 삭제
            constraintSet.connect(
                R.id.apiResponseLabel,
                ConstraintSet.TOP,
                R.id.apiTableLayout,
                ConstraintSet.BOTTOM
            )
        } else {
            constraintSet.clear(R.id.apiResponseLabel, ConstraintSet.TOP) // Top에 대한 제약조건 삭제
            constraintSet.connect(
                R.id.apiResponseLabel,
                ConstraintSet.TOP,
                R.id.apiReqBodyEditText,
                ConstraintSet.BOTTOM
            )
        }
        // 적용
        constraintSet.applyTo(apiConstraintLayout)


    }

    // api 넘기고 type 넘기고 테이블에 있는 값들로 해서 Params, Headers, Body 처리 가능(구분하기)
    private fun sendRequestForApi(url: String, type: String) {
        val selectedTab = binding.apiReqTabLayout.selectedTabPosition
        Log.e("selectedTab", selectedTab.toString())

        val headerMap = mutableMapOf<String, String>() // header
        val paramsMap = mutableMapOf<String, String>() // params
        // body
        if (selectedTab == 2) {
            val body = ""
            makeRequest(url = url, method = type, headers = headerMap, queryParams = paramsMap)
        }
        // Params & Headers
        else {
            val apiTableLayout = binding.apiTableLayout
            val key1 = binding.apiKey1?.text.toString() ?: ""
            val value1 = binding.apiValue1?.text.toString() ?: ""
            val keyList = mutableListOf<String>()
            val valueList = mutableListOf<String>()
            val kvList = mutableListOf<String>()
            for (i in 1 until apiTableLayout.childCount) {
                // Row
                val row = apiTableLayout.getChildAt(i)
                if (row is TableRow) {
                    // tableRow row 순회
                    // 행 순회할 때마다 키벨류 뽑아서 넣기
                    var key2 = ""
                    var value2 = ""
                    if (key1 != "" && value1 != "") {
                        paramsMap[key1] = value1
                        headerMap[key1] = value1
                    }
                    for (j in 1 until row.childCount) {
                        val view = row.getChildAt(j)
                        if (view is EditText) {
                            val editor = view as EditText
                            kvList.add(editor.text.toString()) // 0, 2, 4, ... Key // 1, 3, 5, 7, ... Value
                        }
                    }

                }
            }
            for (k in 0..kvList.size - 1) {
                if (kvList[k] != "") {
                    if (k % 2 == 0) {
                        keyList.add(kvList[k]) // Key만 넣기
                    } else {
                        valueList.add(kvList[k]) // Value만 넣기
                    }
                }
            }
            for (index in 0..keyList.size - 1) {
                paramsMap[keyList[index]] = valueList[index]
                headerMap[keyList[index]] = valueList[index]
            }

            paramsMap.remove("null")
            headerMap.remove("null")
            makeRequest(url, type, headers = headerMap, queryParams = paramsMap)
        }
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
            id = (position + 1)
        }
        val valueEditText = EditText(binding.root.context).apply {
            gravity = Gravity.CENTER
            id = (position + 2)
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

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (!isFirstOpen) {
            initResumeData()  // 다시 UI 구성
        } else {
            val sharedPreferences =
                binding.root.context.getSharedPreferences("request", Context.MODE_PRIVATE)
            // 값 분배하고 삭제하기
            sharedPreferences.edit().clear().apply()
        }
    }

    fun makeRequest(
        url: String,
        method: String,
        headers: Map<String, String>?,
        queryParams: Map<String, String>?,
        body: RequestBody? = null
    ) {
        Log.d("makeRequest", "start!!")
        val currentTab = binding.apiReqTabLayout.selectedTabPosition
        Log.d("TAB POSITION", currentTab.toString())

        val urlBuilder = HttpUrl.parse(url)?.newBuilder()
        // Query Parameter가 있을때 수행

        // 최종은 urlBuilder에서 url Http로 파싱한것 + 파라미터 추가한 값
        val finalUrl = urlBuilder?.build().toString()
        // parameter 추가한 RequestBuilder
        val requestBuilder = Request.Builder()
            .url(finalUrl)
        // 0 --> Params
        if (currentTab == 0) {
            queryParams?.forEach { (key, value) ->
                urlBuilder?.addQueryParameter(key, value)
            }
        }
        // 1 -> Headers
        else if (currentTab == 1) {
            headers?.forEach { (key, value) ->
                requestBuilder.addHeader(key, value)
            }
        }
        // 2 --> body
        else {
            Log.e("ApiFragment_TabLayout", "Body 구현")
        }
        // 해더 넘긴 것 추가하기
        // Header 추가한 RequestBuilder
        Log.e(
            "TEST",
            "method: $method url: $url headers: ${headers.toString()} queryParams: ${queryParams.toString()}"
        )
        when (method) {
            "GET" -> requestBuilder.get()
            "POST" -> requestBuilder.post(body ?: RequestBody.create(null, ""))
            "PUT" -> {
                requestBuilder.put(body ?: RequestBody.create(null, ""))
            }

            "DELETE" -> {
                requestBuilder.delete(body ?: RequestBody.create(null, ""))
            }
        }
        val request = requestBuilder.build()
        CoroutineScope(Dispatchers.IO).launch {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ApiFragment_apiSend", e.toString())
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d("ApiFragment_apiResponseBody", response.toString())
                        val responseBody = response.toString()
                        val prettyJson = formatJsonString(responseBody)

                        activity?.runOnUiThread {
                            binding.apiResponseTextView.text = prettyJson
                        }
                    }
                }
            })

        }
    }

    fun formatJsonString(jsonString: String?): String {
        return try {
            if (jsonString?.trim()?.startsWith("{") == true) {
                val jsonObject = JSONObject(jsonString)
                jsonObject.toString(4)
            } else if (jsonString?.trim()?.startsWith("[") == true) {
                val jsonArray = JSONArray(jsonString)
                jsonArray.toString(4)
            } else {
                jsonString ?: "Invalid JSON"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Error parsing JSON ${e.message}"
        }
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