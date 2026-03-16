package edu.oregonstate.cs492.assignmentfinal.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import edu.oregonstate.cs492.assignmentfinal.R

class gameResultFragment : Fragment(R.layout.game_result_fragment) {

    private val gameViewModel: GameDetailsViewModel by activityViewModels()

    private val tag = "GameResultFragment"

    private var clueList: List<String> = emptyList()
    private var clueIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // all components in UI
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

        val mode = arguments?.getString("mode") ?: "daily" // daily / endless

        // Guess result
        outcomeTV.text = when (result) {
            GuessResult.CORRECT -> "CORRECT!"
            GuessResult.INCORRECT -> "INCORRECT"
            else -> ""
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

        // Hint Arrows
        backArrow.setOnClickListener {
            // Implement later
        }

        forwardArrow.setOnClickListener {
            // Implement later
        }

        visitButton.setOnClickListener {
            // Implement later
        }

        nextButton.setOnClickListener {
            if (mode == "daily") {
                findNavController().navigate(R.id.home_page)
            }
            else {
                findNavController().navigate(R.id.go_to_next_endless)
            }
        }
    }
}
