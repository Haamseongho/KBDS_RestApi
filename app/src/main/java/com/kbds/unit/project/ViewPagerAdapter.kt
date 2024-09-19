package com.kbds.unit.project

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class ViewPagerAdapter(private val mActivity: MainActivity, private val mainViewPager: ViewPager2) :
    FragmentStateAdapter(mActivity) {


    companion object {
        val fragmentList: ArrayList<Fragment> = ArrayList()
    }

    override fun getItemCount(): Int {
        return 3 // Fragment 갯수
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun getViewPager(): ViewPager2 {
        return mainViewPager
    }
}