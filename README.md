# Final Project Proposal \- GameFound™

## Team 11

# Team Members

1. Joshua Knowles
2. Christopher Dutton
3. James Osborn


# Description

Our app is going to be a “Guess the Game” app called GameFound™, which has the user guessing a video game based on various clues with information such as, release dates, genres, user ratings, game achievement titles, developers, publishers, and screenshots. When a player plays the game, they will be given clues 1 at a time and are allowed a guess at what game they think it is after each clue. If they guess the game correctly, then they have won, and the app tells them they were correct. If they guess incorrectly, it gives them the next clue, and they are allowed to guess again. If they run out of clues, the game says they have lost, and tells them what the game was.   
There will be two game modes in our app. The first is daily, which is a game chosen at random each day, but is always the same if it is still the same day. This gives users a chance to compete against their friends and see who can complete the daily in the fewest clues. The second mode is endless, which picks a random game each time it is played. This allows the user to continue using the app after having completed the daily guess.

# API

## Links

[https://rawg.io/apidocs](https://rawg.io/apidocs)  
[https://api.rawg.io/docs/](https://api.rawg.io/docs/)

## 

## 

## List of Games:

[https://api.rawg.io/docs/\#operation/games\_list](https://api.rawg.io/docs/#operation/games_list)  
**https://api.rawg.io/api/games**   
Retrieving the list of games will be a one-time call to retrieve an initial list of games that are possible to show up. This list of more than 800,000 games will be filtered to create a list of \~10,000 games that could be guessed. They will likely be filtered by release date in order to get newer games in the list, as well as possibly using popularity parameters to use more well-known games.

## Details of Game:

[https://api.rawg.io/docs/\#operation/games\_read](https://api.rawg.io/docs/#operation/games_read)   
**https://api.rawg.io/api/games/{id}**  
When a game is set up to be played, this api will be called in order to retrieve the various information on the game that will be used as clues.

## Screenshots of Game:

[https://api.rawg.io/docs/\#operation/games\_screenshots\_list](https://api.rawg.io/docs/#operation/games_screenshots_list)   
**https://api.rawg.io/api/games/{game\_pk}/screenshots**  
When a game is set up to be played, this api will be called in order to retrieve screenshots of the game that will be used as clues.

## Game Achievements:

[https://api.rawg.io/docs/\#operation/games\_achievements\_read](https://api.rawg.io/docs/#operation/games_achievements_read)  
**https://api.rawg.io/api/games/{id}/achievements**  
When a game is set up to be played, this api will be (maybe) called in order to retrieve achievement titles that may be used as clues.

## Store Page Links

[https://api.rawg.io/docs/\#operation/games\_stores\_list](https://api.rawg.io/docs/#operation/games_stores_list)   
**https://api.rawg.io/api/games/{game\_pk}/stores**  
After a game is complete (or when the game is set up, depending on which is easier), this api will be called to retrieve the links to the store pages (i.e., Steam) that will implicitly open another app.

# UI

GameFound™ is a quiz app where users can identify video games based on progressively revealed clues that are either visual or knowledge-based. The app will support two gameplay modes(Daily and Endless) a settings screen, and a post-game results page that has the ability to take you to the page for the game either on Steam or on Chrome.   
The UI itself is organized around a small set of focused activities with a simple forward/back navigation and minimal clutter.

Activities

* Main Page
* Daily
* Endless
* Settings

Navigation Flow

* MainActivity
  - DailyGameActivity
  - ResultActivity
  - BacktoMain(MainActivity)
* MainActivity
  - EndlessGameActivity
  - ResultActivity
  - Next or BacktoMain
* MainActivity
  - SettingsActivity
  - BacktoMain

Major Non-Navigation Interactions

* Clue Navigation
  * Left/Right Arrows for cycling through clues
  * More images revealed
* Guess Submission
  * Text input field with a button
  * Dropdown Selection
* EndlessMode Skip functionality
  * Skips to next game
* Sound Effects
  * Correct guess
  * Close Call
* Theme Switching
  * Dark Mode
* External Link
  * Visit the game page through an external link

# Mocks

Figma Mocks for Guess the Game: [https://www.figma.com/design/8mjOnNMej7Vr8ygFj3QT7y/Guess-the-Game?node-id=0-1\&t=PxpvqaRsquEgadsj-1](https://www.figma.com/design/8mjOnNMej7Vr8ygFj3QT7y/Guess-the-Game?node-id=0-1&t=PxpvqaRsquEgadsj-1)

# 

# Division of Labor

	All team members will be responsible for developing and reviewing code for the project, with specific responsibilities listed below.

* Joshua Knowles
  * API and ViewModel linkage
  * Filter game data (900k games down to \~10k)
  * Make the game guess dropdown filter based on game data
* Christopher Dutton
  * Activity lifecycle methods and ViewModel architecture
  * Implement screen timeout
  * Add a scoring system functionality for endless mode
* James Osborn
  * UI implementation and storing user experience preferences
  * Add media playback for buttons
  * Link the game page button to an external app