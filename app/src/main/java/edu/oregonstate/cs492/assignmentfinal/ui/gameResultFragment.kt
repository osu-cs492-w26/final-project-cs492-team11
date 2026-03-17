package edu.oregonstate.cs492.assignmentfinal.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import edu.oregonstate.cs492.assignmentfinal.R
import edu.oregonstate.cs492.assignmentfinal.data.Game
import edu.oregonstate.cs492.assignmentfinal.data.GameScreenshots
import kotlinx.coroutines.launch

class gameResultFragment : Fragment(R.layout.game_result_fragment) {

    private val gameViewModel: GameDetailsViewModel by activityViewModels()

    private val tag = "GameResultFragment"

    private var gameReady = false
    private var storedGame: Game? = null
    private var screenshotsReady = false
    private var storedScreenshots: GameScreenshots? = null

    private lateinit var TVClue: TextView
    private lateinit var IVClue: ImageView

    private var currentHint = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val outcomeTV = view.findViewById<TextView>(R.id.guess_outcome_text)
        val gameNameTV = view.findViewById<TextView>(R.id.game_name)
        val scoreTV = view.findViewById<TextView>(R.id.score_text)

        val clueNumberTV = view.findViewById<TextView>(R.id.clue_number)
        TVClue = view.findViewById<TextView>(R.id.text_clue)
        IVClue = view.findViewById<ImageView>(R.id.image_clue)

        val backArrow = view.findViewById<ImageButton>(R.id.clue_back_arrow)
        val forwardArrow = view.findViewById<ImageButton>(R.id.clue_forward_arrow)

        val visitButton = view.findViewById<Button>(R.id.visit_game_button)
        val nextButton = view.findViewById<Button>(R.id.next_button)

        val result = gameViewModel.guessResult.value
        val game = gameViewModel.game.value
        val screenshots = gameViewModel.screenshots.value
        val mode = arguments?.getString("mode") ?: "daily"

        val url = gameViewModel.game.value?.website
        // only works if there is a url
        visitButton.isEnabled = !url.isNullOrBlank()

        outcomeTV.text = when (result) {
            GuessResult.CORRECT -> getString(R.string.correct_guess_text)
            GuessResult.INCORRECT -> getString(R.string.incorrect_guess_text)
            else -> ""
        }

        // Nav button text
        if (mode == "daily") {
            nextButton.text = getString(R.string.daily_game_done_button)
        } else {
            if (result == GuessResult.CORRECT) {
                nextButton.text = getString(R.string.endless_next_button)
            } else {
                nextButton.text = getString(R.string.daily_game_done_button)
            }
        }


        // Game name
        gameNameTV.text = game?.name ?: "Unknown Game"

        // Score, endless mode only
        if (mode == "endless") {
            scoreTV.text = getString(R.string.score_text, gameViewModel.score)
        } else {
            scoreTV.visibility = View.GONE
        }

        fun updateClue() {
            clueNumberTV.text = getString(R.string.clue_number, currentHint, gameViewModel.maxHints)

            viewLifecycleOwner.lifecycleScope.launch {
                updateHintShown()
            }
        }

        backArrow.setOnClickListener {
            if (currentHint > 1) {
                currentHint -= 1
                updateClue()
            }
        }

        forwardArrow.setOnClickListener {
            if (currentHint < gameViewModel.maxHints) {
                currentHint += 1
                updateClue()
            }
        }

        updateClue()

        visitButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }


        nextButton.setOnClickListener {
            // Daily, just lead to home
            if (mode == "daily") {
                findNavController().navigate(R.id.home_page)
            }
            else {
                // Endless if correct
                if (result == GuessResult.CORRECT) {
                    findNavController().navigate(R.id.go_to_next_endless)
                }
                // Endless if incorrect
                else {
                    findNavController().navigate(R.id.home_page)
                }
            }
        }

        gameViewModel.game.observe(viewLifecycleOwner) { game ->
            gameReady = game != null
            if (gameReady) {
                storedGame = game
            }
        }

        gameViewModel.screenshots.observe(viewLifecycleOwner) { screenshots ->
            screenshotsReady = screenshots != null
            if (screenshotsReady) {
                storedScreenshots = screenshots
            }
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

