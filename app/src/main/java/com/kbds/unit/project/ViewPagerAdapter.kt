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

    fun updateFragmentData(position: Int, data: ChildReqItem) {
        val fragment = fragmentList[position] as? ApiFragment
        fragment?.arguments = Bundle().apply {
            putInt("COLLECTION_ID", data.collectionId)
            putString("TYPE", data.type)
            putString("TITLE", data.title)
            putString("URL", data.url ?: "")
        }
        notifyItemChanged(position)
    }
    fun getViewPager(): ViewPager2 {
        return mainViewPager
    }

    fun setDataToFragment(position: Int, bundle: Bundle) {
        fragmentList[position].arguments = bundle
        Log.d("haams123123", "bundle: ${bundle.toString()}  fragment : ${fragmentList[position]}")
    }
}