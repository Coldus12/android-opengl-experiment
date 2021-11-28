package com.example.fall

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class CharacterPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment = when(position){
        0 -> ChoosePlayerFragment("Jason Bourne", "Pistol", 0)
        1 -> ChoosePlayerFragment("Justin", "Shotgun", 1)
        else -> ChoosePlayerFragment("Jason Bourne", "Pistol", 0)
    }

    override fun getCount() : Int = NUM_PAGES

    companion object {
        const val NUM_PAGES = 2
    }
}