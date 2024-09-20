package com.kbds.unit.project.api

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TableRow
import android.widget.Toast
import com.kbds.unit.project.R
import com.kbds.unit.project.databinding.FragmentApiBinding

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
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
        val collectionId = arguments?.getString("COLLECTION_ID", "")
        val type = arguments?.getString("TYPE", "GET")
        val title = arguments?.getString("TITLE", "")
        val url = arguments?.getString("URL", "")

        val spinner = binding.apiSpinner

        Log.e("APIFragment", "CollectionID: $collectionId , Type: $type , Title: $title, URL: $url")


        binding.apiSpinner.apply {
            adapter =  spinnerAdapter
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
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = itemList[position]
                Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무것도 선택되지 않았을 때 처리
            }
        }

        val apiEditText = binding.apiUrlEditText
        val btnSend = binding.apiBtnSend
        val apiResponse = binding.apiResponseTextView

//        binding.apiCheckBox1?.setOnCheckedChangeListener {
//
//        }
    }

    private fun addItemRow(itemName: String) {
        val tableRow = TableRow(requireContext())
        val checkBox = CheckBox(requireContext()).apply {
            text = itemName

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