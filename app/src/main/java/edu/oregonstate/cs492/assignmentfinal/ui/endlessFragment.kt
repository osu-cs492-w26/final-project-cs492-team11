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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import edu.oregonstate.cs492.assignmentfinal.R
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import edu.oregonstate.cs492.assignmentfinal.data.Game
import edu.oregonstate.cs492.assignmentfinal.data.GameScreenshots
import kotlinx.coroutines.launch

class endlessFragment : Fragment(R.layout.endless_game_page) {

    private val gameViewModel: GameDetailsViewModel by activityViewModels()

    private val tag = "Endless Fragment"

    private var currentHint = 1

    private var gameReady = false
    private var storedGame: Game? = null
    private var screenshotsReady = false
    private var storedScreenshots: GameScreenshots? = null

    private lateinit var loadingErrorTV: TextView
    private lateinit var loadingIndicator: View

    private lateinit var TVClue: TextView

    private lateinit var TVClueNumber: TextView
    private lateinit var IVClue: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireContext().getSharedPreferences("endless_game", Context.MODE_PRIVATE)

        TVClue = view.findViewById<TextView>(R.id.text_clue)
        IVClue = view.findViewById<ImageView>(R.id.image_clue)
        val ACTVInput = view.findViewById<AutoCompleteTextView>(R.id.auto_complete_text)
        TVClueNumber = view.findViewById<TextView>(R.id.clue_number)
        val TVScore = view.findViewById<TextView>(R.id.score_text)

        val TVPuzzleName = view.findViewById<TextView>(R.id.endless_name)

        loadingErrorTV = view.findViewById(R.id.tv_loading_error)
        loadingIndicator = view.findViewById(R.id.loading_indicator)

        val input = requireContext().assets.open("games.txt")
        val lines = input.bufferedReader().readLines()
        val items = lines.map { line -> line.split("|")[1] }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        ACTVInput.setAdapter(adapter)
        // Two characters have to be typed
        ACTVInput.threshold = 2

        // Clue Button
        val rightArrow: ImageButton = view.findViewById(R.id.clue_forward_arrow)
        val leftArrow: ImageButton = view.findViewById(R.id.clue_back_arrow)

        TVScore.text = getString(R.string.score_text, gameViewModel.score)

        rightArrow.setOnClickListener {
            val maxUnlocked = gameViewModel.hintIndex.value ?: 1
            if (currentHint < maxUnlocked) {
                currentHint += 1
            }
            updateClueNumber()
        }

        leftArrow.setOnClickListener {
            if (currentHint > 1) {
                currentHint -= 1
            }
            updateClueNumber()
        }

        // Confirm Button
        val confirmButton: Button = view.findViewById(R.id.submit_button)
        // Takes the answer from the text box and the game stored in current_game_endless and compares then wins the game if true
        confirmButton.setOnClickListener {
            val guess = view.findViewById<TextInputLayout>(R.id.game_input)
                .editText?.text.toString().trim()
            gameViewModel.submitGuess(guess)
        }

        gameViewModel.gameCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                val bundle = Bundle().apply {
                    putString("mode", "endless")
                }
                findNavController().navigate(R.id.action_endless_to_result, bundle)
            }
        }

        val skipButton: Button = view.findViewById(R.id.skip_button)

        skipButton.setOnClickListener {
            findNavController().navigate(R.id.endless_page)
        }

        gameViewModel.game.observe(viewLifecycleOwner) { game ->
            gameReady = game != null
            if (gameReady) {
                storedGame = game
                TVPuzzleName.text = getString(R.string.endless_puzzle_title, gameViewModel.puzzleNum)
            }
        }

        gameViewModel.screenshots.observe(viewLifecycleOwner) { screenshots ->
            screenshotsReady = screenshots != null
            if (screenshotsReady) {
                storedScreenshots = screenshots
            }
        }

        gameViewModel.hintIndex.observe(viewLifecycleOwner) { index ->
            currentHint = index
            updateClueNumber()
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

    override fun onStart() {
        super.onStart()
        currentHint = 1
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

    private fun updateClueNumber() {
        TVClueNumber.text = getString(
            R.string.clue_number,
            currentHint,
            gameViewModel.maxHints
        )

        viewLifecycleOwner.lifecycleScope.launch {
            updateHintShown()
        }
    }

    suspend private fun updateHintShown() {
        if (gameReady && screenshotsReady) {
            // Image:
            storedScreenshots?.count?.let {
                if (it > currentHint-1) {
                    val url = storedScreenshots?.photos?.getOrNull(currentHint-1)?.image
                    Glide.with(this)
                        .load(url)
                        // .placeholder(R.drawable.ic_loading_placeholder) // TODO Placeholder image
                        // .error(R.drawable.ic_error_image)               // TODO Error image
                        .into(IVClue)
                }
            }

            // Text
            TVClue.text = when (currentHint) {
                1-> ""
                2-> "Metacritic: " + storedGame?.metacritic.toString()
                3-> "ESRB Rating: " + (storedGame?.esrbRating?.name ?: "Not Available")
                4-> "Developer: " + (storedGame?.developers?.getOrNull(0)?.name ?: "Not Available")
                5-> "Publisher: " + (storedGame?.developers?.getOrNull(0)?.name ?: "Not Available")
                else -> "This should never happen"
            }
        } else {
            kotlinx.coroutines.delay(50)
            updateHintShown()
        }
    }
}