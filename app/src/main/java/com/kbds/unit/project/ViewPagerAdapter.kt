package com.kbds.unit.project

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.kbds.unit.project.api.ApiFragment
import com.kbds.unit.project.collections.CollectionFragment
import com.kbds.unit.project.collections.model.ChildReqItem
import com.kbds.unit.project.history.HistoryFragment

class ViewPagerAdapter(private val mActivity: MainActivity, private val mainViewPager: ViewPager2) :
    FragmentStateAdapter(mActivity) {

    companion object {
        val fragmentList = listOf(
            CollectionFragment(),
            ApiFragment(),
            HistoryFragment()
        )
    }

    override fun getItemCount(): Int {
        return fragmentList.size // Fragment 갯수
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }


    fun setDataToFragment(position: Int, bundle: Bundle) {
        val fragment = fragmentList[position]
        fragment.arguments = bundle
        if (fragment is ApiFragment) {
            fragment.updateData(bundle)  // ApiFragment에 데이터를 업데이트하는 메서드 추가
        }
    }
}