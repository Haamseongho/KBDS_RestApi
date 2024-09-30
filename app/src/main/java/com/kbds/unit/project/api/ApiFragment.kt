package com.kbds.unit.project.api

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
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
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.kbds.unit.project.R
import com.kbds.unit.project.database.AppDatabase
import com.kbds.unit.project.database.model.HistoryItem
import com.kbds.unit.project.databinding.FragmentApiBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    var prevTitle = ""

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
        Log.e("Haams704_view", "CollectionId : ${arguments?.getInt("COLLECTION_ID", -1)}" +
                "Type : ${arguments?.getString("TYPE")} Title: ${arguments?.getString("TITLE")} URL : ${arguments?.getString("URL")}")

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

        val collectionId = sharedPreferences.getInt("COLLECTION_ID", -1)
        val title = sharedPreferences.getString("TITLE", "")

        prevTitle = title ?: ""
        binding.apiTitle.setText(title)
        val type = sharedPreferences.getString("TYPE", "GET")

        val url = sharedPreferences.getString("URL", "")
        val defaultSelectionIndex = itemList.indexOf(type)

        binding.apiUrlEditText.setText(url)

        spinner.setSelection(defaultSelectionIndex)
        Log.e(
            "APIFragment22",
            "CollectionID: $collectionId , Type: $type , Title: $title, URL: $url"
        )
    }

    @SuppressLint("CommitPrefEdits")
    private fun initViews() {
        // 처음 초기에만 발생
        isFirstOpen = false

        Log.e("Haams704", "CollectionId : ${arguments?.getInt("COLLECTION_ID", -1)}" +
                "Type : ${arguments?.getString("TYPE")} Title: ${arguments?.getString("TITLE")} URL : ${arguments?.getString("URL")}")

        val itemList = listOf("GET", "POST", "PUT", "DELETE")
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
        val sharedPreferences =
            binding.root.context.getSharedPreferences("request", Context.MODE_PRIVATE)

        val collectionId = sharedPreferences.getInt("COLLECTION_ID", -1)
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
        // 이전꺼 가지고있기
        prevTitle = title ?: ""


        binding.apiTitle.setText(title)
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
        binding.apiReqBodyEditText.isSingleLine = false
        binding.apiReqBodyEditText.maxLines = Integer.MAX_VALUE
        binding.apiReqBodyEditText.isVerticalScrollBarEnabled = true
        binding.apiReqBodyEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i("apiBody", "beforeTextChanged")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.i("apiBody", "onTextChanged")
            }

            // 변화에 따라 입력수 * 라인길이 = 증가
            override fun afterTextChanged(s: Editable?) {
                val lineCount = binding.apiReqBodyEditText.lineCount
                if (lineCount > 0) {
                    binding.apiReqBodyEditText.layoutParams.height =
                        binding.apiReqBodyEditText.lineHeight * lineCount
                }
            }

        })
        // 제약 조건 적용 후 뷰의 가시성 설정
        if (selectedTabPosition == 0 || selectedTabPosition == 1) {
            binding.apiTableLayout.visibility = View.VISIBLE
            binding.apiTableLayout.isVisible = true
            binding.apiReqBodyEditText.isVisible = false
            binding.apiReqBodyEditText.visibility = View.GONE
        } else {
            binding.apiTableLayout.visibility = View.INVISIBLE
            binding.apiTableLayout.isVisible = false
            binding.apiReqBodyEditText.isVisible = true
            binding.apiReqBodyEditText.visibility = View.VISIBLE
        }
        val apiEditText = binding.apiUrlEditText
        val btnSend = binding.apiBtnSend

        // tab 이벤트
        apiReqTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0 || tab?.position == 1) {
                    binding.apiTableLayout.visibility = View.VISIBLE
                    binding.apiTableLayout.isVisible = true
                    binding.apiReqBodyEditText.isVisible = false
                    binding.apiReqBodyEditText.visibility = View.GONE
                } else {
                    binding.apiTableLayout.visibility = View.INVISIBLE
                    binding.apiTableLayout.isVisible = false
                    binding.apiReqBodyEditText.isVisible = true
                    binding.apiReqBodyEditText.visibility = View.VISIBLE
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
                if ((apiEditText.text.toString() != "") && (apiEditText.text.toString()
                        .contains("http") || (apiEditText.text.toString().contains("https")))
                ) {
                    // 요청 보내기
                    sendRequestForApi(
                        apiEditText.text.toString(),
                        selectedItem,
                        collectionId,
                        binding.apiTitle.text.toString() ?: ""
                    )
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "http or https로 시작하는 url 경로 입력해주세요",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            // 같다면 선택한 값
            else {
                if ((apiEditText.text.toString() != "") && (apiEditText.text.toString()
                        .contains("http") || (apiEditText.text.toString().contains("https")))
                ) {
                    // 요청 보내기
                    sendRequestForApi(
                        apiEditText.text.toString(),
                        type,
                        collectionId,
                        binding.apiTitle.text.toString().toString() ?: ""
                    )
                } else {
                    Toast.makeText(
                        binding.root.context,
                        "http or https로 시작하는 url 경로 입력해주세요",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

    // api 넘기고 type 넘기고 테이블에 있는 값들로 해서 Params, Headers, Body 처리 가능(구분하기)
    private fun sendRequestForApi(url: String, type: String, collectionId: Int, title: String?) {
        val selectedTab = binding.apiReqTabLayout.selectedTabPosition
        Log.e("selectedTab", selectedTab.toString())

        val headerMap = mutableMapOf<String, String>() // header
        val paramsMap = mutableMapOf<String, String>() // params
        // body
        if (selectedTab == 2) {
            val inputBody = binding.apiReqBodyEditText.text.toString()
            // 여기에는 RequestBody 넣어줘야함
            val mediaType = MediaType.parse("application/json")
            try {
                val jsonObject =
                    JSONObject(inputBody.trim()) // input이 JSON 형태인지 체크하고 아니면 예외가 발생합니다.
                val requestBody = RequestBody.create(mediaType, jsonObject.toString())
                makeRequest(
                    url = url,
                    method = type,
                    headers = headerMap,
                    queryParams = paramsMap,
                    body = requestBody,
                    collectionId = collectionId,
                    title = title ?: ""
                )
            } catch (e: JSONException) {
                Toast.makeText(
                    binding.root.context,
                    "Body 입력 데이터는 반드시 JSON 형태여야 합니다.",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("ApiFragment", e.message.toString())
            }

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
            makeRequest(
                url,
                type,
                headers = headerMap,
                queryParams = paramsMap,
                collectionId = collectionId,
                title = title ?: ""
            )
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
        Log.e("Haams704", "CollectionId : ${arguments?.getInt("COLLECTION_ID", -1)}" +
                "Type : ${arguments?.getString("TYPE")} Title: ${arguments?.getString("TITLE")} URL : ${arguments?.getString("URL")}")

        if (!isFirstOpen) {
            initResumeData()  // 다시 UI 구성
        } else {
            val sharedPreferences =
                binding.root.context.getSharedPreferences("request", Context.MODE_PRIVATE)
            // 값 분배하고 삭제하기
            sharedPreferences.edit().clear().apply()
        }
    }

    private fun makeRequest(
        url: String,
        method: String,
        headers: Map<String, String>?,
        queryParams: Map<String, String>?,
        body: RequestBody? = null,
        collectionId: Int,
        title: String?
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
        // 앱 죽는것 방지
        try {
            val request = requestBuilder.build()
            CoroutineScope(Dispatchers.IO).launch {
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("ApiFragment_apiSend", e.toString())
                        e.printStackTrace()
                    }

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            Log.d("ApiFragment_apiResponseBody", response.toString())

                            val responseBody = response.toString()
                            val cookies = response.headers("Set-Cookie")
                            responseBody.plus("\n\n\n")
                            responseBody.plus("[Cookies]\n")

                            for (cookie in cookies) {
                                responseBody.plus("Cookie : $cookie \n")
                            }

                            responseBody.plus("[Headers]\n")
                            responseBody.plus(response.headers()).plus("\n")

                            val prettyJson = formatJsonString(responseBody)

                            activity?.runOnUiThread {
                                binding.apiResponseTextView.text = prettyJson
                            }

                            updateRequest(url, method, collectionId, title)
                            val nowTime = LocalDateTime.now()
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val formattedDate = nowTime.format(formatter)
                            insertHistoryItem(
                                collectionId,
                                formattedDate,
                                title,
                                method,
                                url,
                                queryParams,
                                headers,
                                body
                            )
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    // History Table 저장
    private fun insertHistoryItem(
        collectionId: Int,
        formattedDate: String?,
        title: String?,
        method: String,
        url: String,
        queryParams: Map<String, String>?,
        headers: Map<String, String>?,
        body: RequestBody?
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            val reqId = AppDatabase.getInstance(binding.root.context)?.requestDao()
                ?.getReqIdByTitleAndCid(afterTitle = title ?: "", cId = collectionId)
            Log.e("REQID", reqId.toString())
            val paramJsonString = Gson().toJson(queryParams)
            val headerJsonString = Gson().toJson(headers)
            AppDatabase.getInstance(binding.root.context)?.historyDao()
                ?.insertHistory(
                    HistoryItem(
                        reqId = reqId!!,
                        collectionId = collectionId,
                        date = formattedDate!!,
                        title = title ?: "No_title",
                        type = method,
                        url = url,
                        params = paramJsonString,
                        headers = headerJsonString,
                        body = body.toString()
                    )
                )

        }
    }

    // Request Update
    private fun updateRequest(url: String, method: String, collectionId: Int, title: String?) {
        var cTitle = title
        // 빈 값이면 Url로 넣어서 insert or update 해주기
        if (cTitle == "" || cTitle.isNullOrEmpty()) {
            cTitle = url
        }
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getInstance(binding.root.context)?.requestDao()
                ?.updateReqItemUrl(afterTitle = cTitle, url = url, prevTitle = prevTitle)

            withContext(Dispatchers.Main) {
                Toast.makeText(binding.root.context, "히스토리에 저장되었습니다.", Toast.LENGTH_SHORT).show()
            }
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