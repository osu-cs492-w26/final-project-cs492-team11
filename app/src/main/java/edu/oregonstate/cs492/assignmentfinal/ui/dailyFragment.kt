package edu.oregonstate.cs492.assignmentfinal.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.oregonstate.cs492.assignmentfinal.R
import java.io.File
import androidx.fragment.app.viewModels



class dailyFragment : Fragment(R.layout.daily_game_fragment) {

    private val viewModel: SingleGameViewModel by viewModels()

    private val tag = "DailyFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val TVName = view.findViewById<TextView>(R.id.daily_name)

        viewModel.game.observe(viewLifecycleOwner) { game ->
            if (game != null) {
                TVName.text = game.name ?: "Error: Unknown Game"
            }
        }

        // TODO: Error observation
    }

    override fun onStart() {
        super.onStart()

        try {
            val input = requireContext().assets.open("games.txt")
            val lines = input.bufferedReader().readLines()


            if (lines.isNotEmpty()) {
                val randomObject = lines.random()
                val gameObject = randomObject.split("|")

                Log.d(tag, "Game Chosen: " + gameObject[0])

                viewModel.loadSingleGame(
                    gameObject[0],
                    getString(R.string.rawg_api_key)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace() // Handle the case where the asset is missing
        }

    }
}