package edu.oregonstate.cs492.assignmentfinal.ui

import GameDetailsViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.oregonstate.cs492.assignmentfinal.R
import java.io.File
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import java.util.Calendar
import kotlin.random.Random


class dailyFragment : Fragment(R.layout.daily_game_fragment) {

    private val gameViewModel: GameDetailsViewModel by viewModels()

    private val tag = "DailyFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val TVName = view.findViewById<TextView>(R.id.daily_name)
        val IVClue = view.findViewById<ImageView>(R.id.image_clue)
        // AutoComplete Text View Input
        val ACTVInput = view.findViewById<AutoCompleteTextView>(R.id.auto_complete_text)

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


        // TODO: Loading/Error Functionality

    }

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
                }else{
                    val usedGames = prefs.getStringSet("used_game", mutableSetOf())!!.toMutableSet()
                    if (usedGames.size >= lines.size){
                        usedGames.clear()
                    }else{
                        val remainingLines = lines.filter { it !in usedGames }
                        val seed = seedDaily()
                        val picked = remainingLines.random(Random(seed))

                        usedGames.add(picked)
                        prefs.edit()
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