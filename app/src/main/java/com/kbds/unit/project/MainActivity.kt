package com.kbds.unit.project

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.kbds.unit.project.api.ApiFragment
import com.kbds.unit.project.collections.CollectionFragment
import com.kbds.unit.project.databinding.ActivityMainBinding
import com.kbds.unit.project.history.HistoryFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var viewPagerAdapter: ViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
    }

    private fun initViews() {

        ViewPagerAdapter.fragmentList.add(CollectionFragment())
        ViewPagerAdapter.fragmentList.add(ApiFragment())
        ViewPagerAdapter.fragmentList.add(HistoryFragment())
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
        val sharedPreferences = binding.root.context.getSharedPreferences("request", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

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
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("MainActivity","HIHIH")
    }
}