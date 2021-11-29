package com.example.fall.player_picker

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

// Character pager adapter
//--------------------------------------------------------------------------------------------------
/** !!If a new playertype is added this class should be modified as well!!
 *  Standard pager adapter for the different playertypes.
 *  Should a new playertype be added the value of NUM_PAGES should be modified, and a new
 *  new "ChoosePlayerFragment" should be placed inside the getItem function with the appropiate
 *  data.
 * */
class CharacterPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment = when(position){
        0 -> ChoosePlayerFragment("Denzel", "Pistol", 0)
        1 -> ChoosePlayerFragment("Justin", "Shotgun", 1)
        else -> ChoosePlayerFragment("Denzel", "Pistol", 0)
    }

    override fun getCount() : Int = NUM_PAGES

    companion object {
        const val NUM_PAGES = 2
    }
}