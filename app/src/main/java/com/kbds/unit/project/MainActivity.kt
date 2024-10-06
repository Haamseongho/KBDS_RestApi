package com.kbds.unit.project

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kbds.unit.project.api.ApiFragment
import com.kbds.unit.project.collections.CollectionFragment
import com.kbds.unit.project.collections.model.ChildReqItem
import com.kbds.unit.project.databinding.ActivityMainBinding
import com.kbds.unit.project.history.HistoryFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var viewPagerAdapter: ViewPagerAdapter? = null
    var bundle: Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewPagerAdapter = ViewPagerAdapter(this, binding.mainViewPager)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
    }

    private fun initViews() {

        binding.mainTabLayout.addTab(
            binding.mainTabLayout.newTab().setIcon(R.drawable.baseline_account_circle_24)
                .setText(R.string.main_tab_1)
        )
        binding.mainTabLayout.addTab(
            binding.mainTabLayout.newTab().setIcon(R.drawable.baseline_account_circle_24)
                .setText(R.string.main_tab_2)
        )
        binding.mainTabLayout.addTab(
            binding.mainTabLayout.newTab().setIcon(R.drawable.baseline_account_circle_24)
                .setText(R.string.main_tab_3)
        )


        viewPagerAdapter = ViewPagerAdapter(this, binding.mainViewPager)



        binding.mainViewPager.adapter = viewPagerAdapter


        TabLayoutMediator(
            binding.mainTabLayout,
            binding.mainViewPager,
        ) { tab, position ->
            binding.mainViewPager.currentItem = tab.position
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.baseline_account_circle_24)
                    tab.text = getString(R.string.main_tab_1)
                }

                1 -> {
                    tab.setIcon(R.drawable.baseline_account_circle_24)
                    tab.text = getString(R.string.main_tab_2)

                }

                2 -> {
                    tab.setIcon(R.drawable.baseline_account_circle_24)
                    tab.text = getString(R.string.main_tab_3)

                }
            }
        }.attach()

        binding.mainTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    // 선택된 탭의 위치로 ViewPager 이동
                    binding.mainViewPager.setCurrentItem(it.position, true)
                    bundle?.let { it1 -> viewPagerAdapter?.setDataToFragment(it.position, it1) }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 데이터 전달
        sendDataToFragment(null, 0)
    }



    fun sendDataToFragment(data: ChildReqItem?, position: Int) {
        Log.d("haams_data", data.toString())

        if (data != null) {
            bundle = Bundle().apply {
                putInt("COLLECTION_ID", data.collectionId)
                putString("TYPE", data.type)
                putString("TITLE", data.title)
                putString("URL", data.url ?: "")
            }
            Log.d("haams_data22", bundle.toString())

            // 이미 생성된 프래그먼트에 데이터를 전달
            viewPagerAdapter!!.setDataToFragment(position, bundle!!)
        }
    }


    override fun onRestart() {
        super.onRestart()
        Log.e("MainActivity","HIHIH")
    }

    override fun onResume() {
        super.onResume()
        Log.e("MainActivity","HIHIH")
    }
}