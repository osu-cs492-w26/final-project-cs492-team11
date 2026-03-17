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
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import edu.oregonstate.cs492.assignmentfinal.R
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import edu.oregonstate.cs492.assignmentfinal.data.Game
import edu.oregonstate.cs492.assignmentfinal.data.GameScreenshots
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import kotlin.random.Random


class dailyFragment : Fragment(R.layout.daily_game_fragment) {

    private val gameViewModel: GameDetailsViewModel by activityViewModels()

    private val tag = "DailyFragment"
    private var gameReady = false
    private var storedGame: Game? = null
    private var screenshotsReady = false
    private var storedScreenshots: GameScreenshots? = null

    private lateinit var loadingErrorTV: TextView
    private lateinit var loadingIndicator: CircularProgressIndicator

    private lateinit var TVClue: TextView
    private lateinit var IVClue: ImageView

    private var currentHint = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Preferences for game
        val prefs = requireContext().getSharedPreferences("daily_game", Context.MODE_PRIVATE)
        val mainContent = view.findViewById<View>(R.id.main_content)

        TVClue = view.findViewById<TextView>(R.id.text_clue)
        IVClue = view.findViewById<ImageView>(R.id.image_clue)
        // AutoComplete Text View Input
        val ACTVInput = view.findViewById<AutoCompleteTextView>(R.id.auto_complete_text)
        val confirmButton: Button = view.findViewById(R.id.submit_button)

        val TVDailyName = view.findViewById<TextView>(R.id.daily_name)

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

        TVDailyName.text = getString(R.string.daily_puzzle_title, getDailyNumber())

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
        // Takes the answer from the text box and the game stored in last_game and compares then wins the game if true
        confirmButton.setOnClickListener {
            val guess = view.findViewById<TextInputLayout>(R.id.game_input)
                .editText?.text.toString().trim()
            gameViewModel.submitGuess(guess)
        }

        gameViewModel.game.observe(viewLifecycleOwner) { game ->
            gameReady = game != null
            if (gameReady) {
                storedGame = game
            }
        }

        gameViewModel.gameCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed) {
                val bundle = Bundle().apply {
                    putString("mode", "daily")
                }
                findNavController().navigate(R.id.action_daily_to_result, bundle)
            }
        }

        gameViewModel.screenshots.observe(viewLifecycleOwner) { screenshots ->
            screenshotsReady = screenshots != null
            if (screenshotsReady) {
                storedScreenshots = screenshots
            }
        }

        // Updates the current hint to be newest one when guessing
        gameViewModel.hintIndex.observe(viewLifecycleOwner) { index ->
            currentHint = index
            updateClueNumber()
        }

        // Loading
        gameViewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                loadingIndicator.visibility = View.VISIBLE
                loadingErrorTV.visibility = View.INVISIBLE
                mainContent.visibility = View.INVISIBLE
            } else {
                loadingIndicator.visibility = View.INVISIBLE
                mainContent.visibility = View.VISIBLE
            }
        }

        // Error
        gameViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                loadingErrorTV.text = getString(R.string.loading_error, error.message)
                loadingErrorTV.visibility = View.VISIBLE
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.home_page)
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

    // Defaults puzzle 1 to March 11th, 2026
    fun getDailyNumber(): Int {
        val start = LocalDate.of(2026, 3, 11)
        val today = LocalDate.now()
        return ChronoUnit.DAYS.between(start, today).toInt() + 1
    }

    private fun updateClueNumber() {
        val TVClueNumber = view?.findViewById<TextView>(R.id.clue_number) ?: return

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