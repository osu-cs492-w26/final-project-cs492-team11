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
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar
import kotlin.random.Random


class dailyFragment : Fragment(R.layout.daily_game_fragment) {

    private val gameViewModel: GameDetailsViewModel by viewModels()

    private val tag = "DailyFragment"

    private lateinit var loadingErrorTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Preferences for game
        val prefs = requireContext().getSharedPreferences("daily_game", Context.MODE_PRIVATE)

        val TVName = view.findViewById<TextView>(R.id.daily_name)
        val IVClue = view.findViewById<ImageView>(R.id.image_clue)
        // AutoComplete Text View Input
        val ACTVInput = view.findViewById<AutoCompleteTextView>(R.id.auto_complete_text)
        val confirmButton: Button = view.findViewById(R.id.submit_button)

        val rightArrow: ImageButton = view.findViewById(R.id.clue_forward_arrow)
        val leftArrow: ImageButton = view.findViewById(R.id.clue_back_arrow)

        loadingErrorTV = view.findViewById(R.id.tv_loading_error)
        loadingIndicator = view.findViewById(R.id.loading_indicator)

        // Setup AutoComplete
        val input = requireContext().assets.open("games.txt")
        val lines = input.bufferedReader().readLines()
        val items = lines.map { line ->
            line.split("|")[1]
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        ACTVInput.setAdapter(adapter)
        // Two characters have to be typed
        ACTVInput.threshold = 2

        rightArrow.setOnClickListener {

        }
        leftArrow.setOnClickListener {

        }

        // Confirm Button
        // Takes the answer from the text box and the game stored in last_game and compares then wins the game if true
        confirmButton.setOnClickListener {
            val guess = view.findViewById<TextInputLayout>(R.id.game_input)
                .editText?.text.toString().trim()
            gameViewModel.submitGuess(guess)
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

        // looks for changes in the guess result and then takes action
        gameViewModel.guessResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                GuessResult.CORRECT -> {
                    Log.d("DailyFragment", "GuessResult: CORRECT")
                }
                GuessResult.INCORRECT -> {
                    Log.d("DailyFragment", "GuessResult: INCORRECT")
                }
                GuessResult.EMPTY -> {
                    Log.d("DailyFragment", "GuessResult: EMPTY")
                }
                else -> {
                    Log.d("DailyFragment", "GuessResult: null or unknown")
                }
            }
        }

        gameViewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                loadingIndicator.visibility = View.VISIBLE
                loadingErrorTV.visibility = View.INVISIBLE
            } else {
                loadingIndicator.visibility = View.INVISIBLE
            }
        }

        gameViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                loadingErrorTV.text = getString(R.string.loading_error, error.message)
                loadingErrorTV.visibility = View.VISIBLE
            }
        }


    }

    // Seeds the Daily game and initiates a network call for that game
    override fun onStart() {
        super.onStart()

        try {
            val input = requireContext().assets.open("games.txt")
            val lines = input.bufferedReader().readLines()


            if (lines.isNotEmpty()) {
                val prefs = requireContext().getSharedPreferences("daily_game", Context.MODE_PRIVATE)
                val today = java.time.LocalDateTime.now().toString()

                val savedDate = prefs.getString("last_date", null)
                val savedGame = prefs.getString("last_game", null)
                val chosenGame = if (savedDate == today && savedGame != null){
                    savedGame
                } else{
                    val usedGames = prefs.getStringSet("used_game", mutableSetOf())!!.toMutableSet()
                    if (usedGames.size >= lines.size){
                        usedGames.clear()
                    }else{
                        val remainingLines = lines.filter { it !in usedGames }
                        val seed = seedDaily()
                        val picked = remainingLines.random(Random(seed))

                        usedGames.add(picked)
                        val currentGame = picked.split("|")
                        prefs.edit()
                            .putString("current_game", currentGame[1])
                            .putString("last_date", today)
                            .putString("last_game", picked)
                            .putStringSet("used_games", usedGames)
                            .apply()
                        picked
                    }
                }


                val randomGame = chosenGame.toString()
                val gameObject = randomGame.split("|")

                Log.d(tag, "Game Chosen: " + gameObject[0])

                gameViewModel.loadGameData(
                    gameObject[0],
                    getString(R.string.rawg_api_key)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace() // Handle the case where the asset is missing
        }

    }
    fun seedDaily() : Long {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val seed = (year * 10000 + month * 100 + day).toLong()
        return seed
    }
}