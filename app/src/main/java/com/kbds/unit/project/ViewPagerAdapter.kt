package com.kbds.unit.project

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kbds.unit.project.collections.CollectionFragment

class ViewPagerAdapter(private val mActivity: MainActivity) : FragmentStateAdapter(mActivity) {


    companion object {
        val fragmentList: ArrayList<Fragment> = ArrayList()
    }

    override fun getItemCount(): Int {
        return 3 // Fragment 갯수
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}