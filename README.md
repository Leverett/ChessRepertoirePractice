# ChessRepertoirePractice

## Motivation
This is a personal project with the goal of creating a satisfying, customizable drilling system for Chess openings. I found that despite diligently reviewing each completed game, it was hard for me to remember missed optimal moves during common opening lines with my preferred systems. My idea to help improve this was to create a system where I could add sets of moves and branches which a program could them make back at me with some randomization, so that I could rapidly build up recall for common positions. This also created an opportunity for me to polish up very old skills (Android app development) and learn new ones (e.g. Kotlin).

## Goal
The primary activity I was aiming for is a configurable "Practice Mode". At its most basic, it would use a store of lines that I had built using PGN (with some customization), allow me to make moves, validate whether each move is "correct" based on the stored repertoire, and provide an opponent's next move based off of the known options. There are numerous customization options to determine what is "correct" for my own moves and which moves the opponent will make based on my goals for that drilling session.

The key principle to this system is that it is not intended for pure, rote memorization of lines. The aim is to simultaneously train the knowlege of why and how certian moves are correct rather than just being able to quickly make the correct choice. This principle drives all of the numerous idiosyncratic design choices that I made in how this app functions.

## How to Use

There are 3 Activities available. The Practice Mode is the primary purpose of this app. The contents of the app are structured to allow importing from Lichess Studies.

* To get started, you need to create a study in Lichess. I have an example study here: https://lichess.org/study/1KJCPTuo
* You can export the study using the Sharing icon below the board and Downloading the Study PGN. Importing the file directly (or using the Lichess link) is a planned feature. I currently save the PGN text to some online document.
* Open the app and select "Configure Repertoire". Use the "Load PGN" button at the bottom to copy/pase the PGN into the dialog window and select OK.
* There are additional settings that can be adjusted, as explained below.
* Back out of the Configuration screen and choose "Practice Mode". This opens the main experience for the app. You may have to switch the perspective color (the switch at the top right) depending on the Studies you are using.
* You can now make moves and have the opponent make moves as described above, and in more detail below.


### Sandbox Mode

This is just a simple mode that allows the user to play both sides of a game. The top-left has a "reset" button that will return the board to the starting position. The top-right has a switch that will change the board perspective. Between them is a space where the algebraic notation of the moves played up until that point is displayed.

Below that is the board. Pieces are currently not draggable, but pieces of the active color can be selected and then moved to a legal square.

In the bottom left is a "Board Settings" button, which allows the user to choose the board colors and piece styles. There are currently only two coloration settings and one piece style, so this is mostly a standin for future additions.

The bottom right has two arrow buttons which will undo or redo the move just played or undone.

### Configure Repertoire

#### The Repertoire List

The top section contains a list of the LineTrees that the user has added. There are two types that can be added: Chapters and Books. Books correspond to a full Lichess Study, and show up as expandable list items on the screen. The expanded Books contain Chapters. Chapters correspond to Lichess Chapters within those studies.  It is also possible to have standalone Chapters in the list that are not part of a Book.

Each Book or Chapter can be added to the "active" repertoire by checking the checkbox on the list item. Only LineTrees that are checked will be used in the Practice Mode.

#### Move Options

Below this list is a set of "Move Options". There are settings for each the Opponent and the Player, and these settings correspond to which types of moves are allowed for the player, and which will be played by the opponent.

For players and opponents:
* Best: If any move in the position is labeled "best", only a move labeled "best" will be correct. Otherwise, any non-mistake is valid.
* Theory: Similar to "best" and "preferred", but calculated afterwards.

For players only:
* Preferred: Similar to "best" and "theory".
* Gambit: This will create an indicator when there is a gambit line available for a position, and that line will be considered valid. This is for when the player wants to specifically practice gambit lines.

For opponents only:
* Gambit: If this is false, it will filter out gambit lines from the pool of valid options.
* Mistakes: This means that mistakes will be part of the possible options that the opponent can make. Each time the opponent moves, a button will appear. If the move was a mistake, the player has to push the button before making their next move. The intention is to practice recognizing when mainline mistakes are made, in addition to know how to proceed.

#### The Bottom Buttons

* Load PGN: This button opens a dialog into which a Lichess Study can be copied into.
* Export PGN: This will copy the entire repertoire (each Book separated by 3 newlines) into the Clipboard, so that it can be saved externally if desired.
* Load Configuration: This will set the active repertoire and move settings to some previously saved combination that can be selected from a popup list.
* Save Configuration: This opens a dialog that allows the user to save the current selected repertoire and move settings to a label that can be loaded in later.

### Practice Mode

This has all of the options available in the Sandbox Mode. There is an additional set of buttons right beneath the board. Below that is a space where text will be displayed depending on the latest user action.

Player's Turn:
* Show Description: This will show the description of the current position, as stored in the active repertoire. It is not displayed by default in order to encourage the player to recall the position.
* Edit Description: This button currently does nothing. See the Future Work section.
* Show Options: This will display all of the valid move options currently available.

Opponent's Turn:
* Opponent Move: This will make the opponent's next move, randomly, from the pool of availabe moves according to that active repertoire and move settings. The opponent's moves are not made automatically in order to allow the player to consider the position before it happens, as well as let the play choose for the opponent, in case they want to follow a specific line.
* Edit Description: This button currently does nothing. See the Future Work section.
* Show Options: This will display all of the valid move options currently available.

When the player makes a move, if the move was valid, the description space will display possible alternatives. When the opponent makes a move, the description section will be blank unless the player hits the Show Description button.

If the player makes a move that is not found in the active repertoire, the description will say "Unknown move", progress will be locked, and another set of options will be available.
* Undo Move
* Add to Repertoire: Intended to allow expanding the repertoire on-the-fly. This button currently does nothing. See the Future Work section.
* Search Repertoire:  Intended to see if the move is present in the full repertoire, instead of just the actively selected repertoire. This button currently does nothing. See the Future Work section.

If the player makes a mistake, the description will indicate it and progress will be locked. The only button will be an undo button.

If the "mistakes" option is active for the opponent, then after each opponent move, a Green "M" indicator will appear next to the options. This must be pressed if the opponent made a mistake before the player moves again. This is to help the player consciously recognize known mistakes.

If the "gambit" option is active for the player, then whenever a gambit line becomes an option, a Red "G" indicator will appear next to the options. This is simply to notify the player, it is not interactive.

At the bottom, in addition to the Sandbox Mode buttons, there are "Move Settings" and "Configure Repertoire" buttons, which allow the player to adjust the options also available in the "Configure Repertoire" Mode.


## Design

I broke down the app into 3 modules. The first is a standalone, backend Chess Rules program (the gamerules package) which could theoretically be recycled for some other Chess application. The second is the Repertoire module, which contains all of the logic for constructing the repertoire structure that the app uses to run the drills. This module was also created to be standalone, so that it could potentially be exported and reused for another application on a different platform. The final layer is the app itself, which is built to contain only the UI elements and UI logic.

### The Rules Engine

I considered simply importing one of the many available chess rules programs available on Github and elsewhere, but thought it would be interesting to build my own. While I did spend some time browsing existing implementations (and found bitboards fascinating, but an exercise for another time as the Rules component of this app is not the primary motivation) this implementation was created fresh from whole cloth by myself. The only limitation is that I have not included any move-counting or repetition rules. Ultimately the purpose of this is openings practice where such things are irrelevant, and I can tack them on later fairly easily if necessary.

Because I found bitboards fascinating, I tried to structure the module so that an alternate implementation using them could relatively easily be added in the future. This would require either making the Position class into an interface or implementing a translation process between the more human-accessible representation and the bitboard representation. The rules could then be reimplemented into the RulesEngine interface.

(As an aside, while working on this step I also happened to look into other Chess-like games such as Shogi, and found that this engine should be fairly easily expandable into those areas. As I would like to learn Shogi at some point in the future, I kept this possibility in mind as I built the abstractions being used)

### The Repertoire

This is the more unique component. There are several goals here.

* Create a tree-like structure of moves to link between unique positions.
  * Have these trees be combinable ad hoc so that different lines or sets of lines can be used for any particular drill.
  * Link between transpositions, so even lines in different trees can automatically overlap
* Make a system of categorizing these moves which can be used to determine correctness based on the play-settings
* Make a system of play-settings that can be used to customize the drill process

#### Line Moves

The basic unit of this module is a LineMove. This unit contains a before and after position, the move object connecting them, and a details object that can be used for the practice activity.

```
LineMove
  chapter: Chapter           // The portion of the study that the move belongs to
  previousPosition: Position // The board state the move starts with
  nextPosition: Position     // The board state after the move
  move: Move                 // The move object connecting the board states
  moveDetails: MoveDetails   // The object containing the practice-mode details of the move
```

The move details are extremely subjective. They connect directly to the play settings and interact with them in order to create the Practice Activity experience using strict logic, but the way that they do this is based on my own preference for how the activity should run.

Any combination of flags is valid, though the behavior for some of them (e.g. both `mistake` and `best` being true) will probably cause weird behavior.
  
```
MoveDetails
  best: Boolean      // If this move is considered clearly the best option (or one of the clear best options) in the position
  theory: Boolean    // If this move is a, or the, standard move in the position. Distinguished from "best" mostly when alternatives aren't necessarily inaccuracies or mistakes
  preferred: Boolean // The move that the user specifically wants to play in the situation, generally out of many reasonable options
  gambit: Boolean    // Whether this move initiates a "gambit" line
  mistake: Boolean   // Whether a move is a mistake. The intention is to help the user recognize and exploit mistakes the opponent makes which might not be obvious
```

These connect to the PlaySettings for the practice session.

#### Play Settings
The play settings are effectively split into two components; guiding the opponent's moves and validating the player's moves.

For players and opponents:
* Best: If any move in the position is labeled "best", only a move labeled "best" will be correct. Otherwise, any non-mistake is valid.
* Theory: Similar to "best" and "preferred", but calculated afterwards.

For players only:
* Preferred: Similar to "best" and "theory".
* Gambit: This will create an indicator when there is a gambit line available for a position, and that line will be considered valid. This is for when the player wants to specifically practice gambit lines.

For opponents only:
* Gambit: If this is false, it will filter out gambit lines from the pool of valid options.
* Mistakes: This means that mistakes will be part of the possible options that the opponent can make. Each time the opponent moves, a button will appear. If the move was a mistake, the player has to push the button before making their next move. The intention is to practice recognizing when mainline mistakes are made, in addition to know how to proceed.

#### Repertoire (sub)sets

The LineMove units are organized into LineTree objects. Each LineTree can take a Position and provide all of the LineMoves available for it, or any equivalent transpositions (i.e. ignoring the turn). There are several different classes of LineTrees

* LineTreeSet: This is a LineTree class that can contain any number of other LineTrees (specifically Chapters and Books) and act as its own LineTree combining all of them.
* Chapter: This is the basic LineTree unit. It is analogous to a Lichess Study Chapter.
* Book: This is a LineTreeSet specification that is analogous to a Lichess Study. It must contain at least one Chapter, and can only contain Chapters.
* Repertoire: This is a LineTreeSet that contains Books and Chapters. It exists in the RepertoireManager Singleton and is intended to contain all of the LineTrees that have been added by the user to their repertoire.

The app uses a singleton instance called the RepertoireManager which will control all of the PracticeMode configurations. There are several fields that are actively used by the player in Practice Mode.

```
RepertoireManager
  repertoire: Repertoire        // This member contains all of the LineTrees added by the player
  activeRepertoire: LineTreeSet // This is an independently customizable subset of the full repertoire, containing all of the LineTrees the player wants to be using for a given session
  playSettings: PlaySettings    // The play settings for the practice session
  configurations: Map           // This is a way to store combinations of activeRepertoires and playSettings that the user wants to reuse without having to set up each time.
```

### The App

The app is best explained in the how-to-use section.

## Future Work

### Board Graphics
* Add more styles
* Highlight previous-move square
* Enable showing legal moves
* Dragging pieces
* Border on promotion dialog
* Border on board for mates
* Indication of game-over
* Current material imbalance
* Coordinates on edge squares
* Sounds
* Export FEN option

### Practice Mode Changes
* Automove opponent option
* Change “Move Settings” and “Configure Repertoire” buttons to just “Load Configuration”
* Use space from above to add a button for loading a position from PGN/FEN
* Implement Editing Description
* Add an end-of-line indication
* Adjust how making mistakes works to move past them and see how they play out
* Adjust unknown moves in order to allow for reaching transpositions not explicitly mapped out

### Repertoire Activity Changes
* Delete Study confirmation
* Indent child options in list
* Add default perspective to Configurations
* Import PGN directly from .pgn file, or from Lichess study links
	
### Chess Logic
* Repetition
* Move counting

### Core Upgrades
* Add move weights for practice mode
* Perhaps upgrade parsing to read the first option as the “preferred” move
* Accessibility
* Exploration/Build Mode
* Online Storage
* Incorporate Chess Engine(s)

### Backend stuff
* Add error handling
* Test cases
* Make the practice play button layout dynamic rather than swapping them out
