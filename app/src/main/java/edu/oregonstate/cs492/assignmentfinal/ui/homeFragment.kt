package edu.oregonstate.cs492.assignmentfinal.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import edu.oregonstate.cs492.assignmentfinal.R

class homeFragment : Fragment(R.layout.home_page) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dailyBtn: Button = view.findViewById(R.id.daily_navigation)
        val endBtn: Button = view.findViewById(R.id.endless_navigation)
        val settingsBtn: Button = view.findViewById(R.id.settings_navigation)

        dailyBtn.setOnClickListener {
            findNavController().navigate(R.id.navigate_to_daily)
        }
        endBtn.setOnClickListener {
            findNavController().navigate(R.id.navigate_to_endless)
        }
        settingsBtn.setOnClickListener {
            findNavController().navigate(R.id.navigate_to_settings)
        }
    }
}