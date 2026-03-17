package edu.oregonstate.cs492.assignmentfinal.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import edu.oregonstate.cs492.assignmentfinal.R

class gameResultFragment : Fragment(R.layout.game_result_fragment) {

    private val gameViewModel: GameDetailsViewModel by activityViewModels()

    private val tag = "GameResultFragment"

    private var currentHint = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val outcomeTV = view.findViewById<TextView>(R.id.guess_outcome_text)
        val gameNameTV = view.findViewById<TextView>(R.id.game_name)
        val scoreTV = view.findViewById<TextView>(R.id.score_text)

        val clueNumberTV = view.findViewById<TextView>(R.id.clue_number)
        val textClueTV = view.findViewById<TextView>(R.id.text_clue)
        val imageClueIV = view.findViewById<ImageView>(R.id.image_clue)

        val backArrow = view.findViewById<ImageButton>(R.id.clue_back_arrow)
        val forwardArrow = view.findViewById<ImageButton>(R.id.clue_forward_arrow)

        val visitButton = view.findViewById<Button>(R.id.visit_game_button)
        val nextButton = view.findViewById<Button>(R.id.next_button)

        val result = gameViewModel.guessResult.value
        val game = gameViewModel.game.value
        val screenshots = gameViewModel.screenshots.value
        val mode = arguments?.getString("mode") ?: "daily"

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
            val score = arguments?.getInt("score") ?: 0
            scoreTV.text = "Score: $score"
        } else {
            scoreTV.visibility = View.GONE
        }

        fun updateClue() {
            clueNumberTV.text = getString(R.string.clue_number, currentHint, gameViewModel.maxHints)
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
            // Implement later
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
    }
}

