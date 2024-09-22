package com.kbds.unit.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SharedViewModel : ViewModel() {
    val dataToSend = MutableLiveData<String>()
}

class MyFragment : Fragment() {
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // 데이터 관찰
        viewModel.dataToSend.observe(viewLifecycleOwner) { data ->

        }

        return inflater.inflate(R.layout.fragment_api, container, false)
    }
}


