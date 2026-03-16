package edu.oregonstate.cs492.assignmentfinal.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.oregonstate.cs492.assignmentfinal.R
import java.io.File
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar
import kotlin.random.Random

class endlessFragment : Fragment(R.layout.endless_game_page) {
    private val gameViewModel: GameDetailsViewModel by viewModels()


    private val tag = "Endless Fragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireContext().getSharedPreferences("endless_game", Context.MODE_PRIVATE)

        val TVName = view.findViewById<TextView>(R.id.endless_name)
        val IVClue = view.findViewById<ImageView>(R.id.image_clue)
        val ACTVInput = view.findViewById<AutoCompleteTextView>(R.id.auto_complete_text)

        val input = requireContext().assets.open("games.txt")
        val lines = input.bufferedReader().readLines()
        val items = lines.map { line ->
            line.split("|")[1]
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        ACTVInput.setAdapter(adapter)
        // Two characters have to be typed
        ACTVInput.threshold = 2

        // Clue Button
        val rightArrow: ImageButton = view.findViewById(R.id.clue_forward_arrow)
        val leftArrow: ImageButton = view.findViewById(R.id.clue_back_arrow)

        rightArrow.setOnClickListener {

        }
        leftArrow.setOnClickListener {

        }

        // Confirm Button
        val confirmButton: Button = view.findViewById(R.id.submit_button)
        // Takes the answer from the text box and the game stored in current_game_endless and compares then wins the game if true
        confirmButton.setOnClickListener {
            val guess = view.findViewById<TextInputLayout>(R.id.game_input)
                .editText?.text.toString().trim()
            gameViewModel.submitGuess(guess)
        }

        // Loads the next game for endless mode
        gameViewModel.gameCompleted.observe(viewLifecycleOwner){ completed ->
            if (completed){
                loadNextGame()
                gameViewModel.resetCompletion()
            }

        }

        // Skip Button

        val skipButton: Button = view.findViewById(R.id.skip_button)

        skipButton.setOnClickListener {

        }

        gameViewModel.game.observe(viewLifecycleOwner) { game ->
            if (game != null) {
                TVName.text = game.name ?: "Error: Unknown Game"
            }
        }

        gameViewModel.screenshots.observe(viewLifecycleOwner) { screenshots ->
            val firstPhotoUrl = screenshots?.photos?.firstOrNull()?.image

            if (firstPhotoUrl != null) {
                Glide.with(this)
                    .load(firstPhotoUrl)
                    // .placeholder(R.drawable.ic_loading_placeholder) // TODO Placeholder image
                    // .error(R.drawable.ic_error_image)               // TODO Error image
                    .into(IVClue)
            }
        }

    }
    override fun onStart() {
        super.onStart()
        loadNextGame()
    }
    fun loadNextGame() {
        try {
            val input = requireContext().assets.open("games.txt")
            val lines = input.bufferedReader().readLines()

            if (lines.isNotEmpty()) {
                val prefs = requireContext().getSharedPreferences("endless_game", Context.MODE_PRIVATE)
                val usedGames = prefs.getStringSet("used_games_endless", mutableSetOf())!!.toMutableSet()

                // Pick a game that hasn't been used yet
                val remaining = lines.filter { it !in usedGames }
                val picked = remaining.random()

                // Save updated used set
                usedGames.add(picked)
                val currentGame = picked.split("|")
                prefs.edit()
                    .putString("current_game_endless", currentGame[1])
                    .putStringSet("used_games_endless", usedGames)
                    .apply()

                val gameObject = picked.split("|")
                Log.d(tag, "Game Chosen: " + gameObject[0])
                gameViewModel.loadGameData(gameObject[0], getString(R.string.rawg_api_key))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}