package com.leverett.chessrepertoirepractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.leverett.chessrepertoirepractice.ui.views.LoadConfigurationAdapter
import com.leverett.chessrepertoirepractice.ui.views.PlaySettingButton
import com.leverett.chessrepertoirepractice.ui.views.RepertoireListAdapter
import com.leverett.repertoire.chess.RepertoireManager
import com.leverett.repertoire.chess.pgn.parseAnnotatedPgnToBook
import com.leverett.repertoire.chess.settings.PlaySettings

class RepertoireActivity : AppCompatActivity() {

    private val repertoireManager = RepertoireManager
    private lateinit var repertoireView: ExpandableListView
    private val repertoireViewAdapter = RepertoireListAdapter(true)
    private val playSettings: PlaySettings
        get() = repertoireManager.playSettings

    private lateinit var playerBestOptionView: PlaySettingButton
    private lateinit var playerTheoryOptionView: PlaySettingButton
    private lateinit var playerGambitsOptionView: PlaySettingButton
    private lateinit var playerPreferredOptionView: PlaySettingButton
    private lateinit var opponentBestOptionView: PlaySettingButton
    private lateinit var opponentTheoryOptionView: PlaySettingButton
    private lateinit var opponentGambitsOptionView: PlaySettingButton
    private lateinit var opponentMistakesOptionView: PlaySettingButton
    private val playOptionViews: List<PlaySettingButton>
        get() {
            return listOf(
                playerBestOptionView,
                playerTheoryOptionView,
                playerGambitsOptionView,
                playerPreferredOptionView,
                opponentBestOptionView,
                opponentTheoryOptionView,
                opponentGambitsOptionView,
                opponentMistakesOptionView
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repertoire)
        repertoireView = findViewById(R.id.repertoire_list_view)
        repertoireView.setAdapter(repertoireViewAdapter)

        playerBestOptionView = findViewById(R.id.player_best)
        playerTheoryOptionView = findViewById(R.id.player_theory)
        playerGambitsOptionView = findViewById(R.id.player_gambits)
        playerPreferredOptionView = findViewById(R.id.player_preferred)
        opponentBestOptionView = findViewById(R.id.opponent_best)
        opponentTheoryOptionView = findViewById(R.id.opponent_theory)
        opponentGambitsOptionView = findViewById(R.id.opponent_gambit)
        opponentMistakesOptionView = findViewById(R.id.opponent_mistakes)

        for (view in playOptionViews) {
            view.setOnClickListener { view -> togglePlayOption(view as PlaySettingButton) }
        }
        refreshPlayOptionButtonColors()
    }

    fun loadPgnButton(view: View) {
        val popupView = layoutInflater.inflate(R.layout.load_pgn_popup, null) as ConstraintLayout
        val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(repertoireView, Gravity.CENTER, 0, -100)
        popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
//            val pgn = popupView.findViewById<TextInputEditText>(R.id.pgn_input).text.toString()
            val pgn = if (repertoireManager.repertoire.lineTrees.isEmpty()) testString else testString2
            val book = parseAnnotatedPgnToBook(pgn)
            repertoireManager.addToRepertoire(book)
            repertoireViewAdapter.notifyDataSetChanged()
            popupWindow.dismiss()
        }
    }

    fun exportPgnButton(view: View) {

    }

    fun saveConfigurationButton(view: View) {
        val popupView = layoutInflater.inflate(R.layout.save_configuration_popup, null) as ConstraintLayout
        val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(repertoireView, Gravity.CENTER, 0, 0)
        popupView.findViewById<Button>(R.id.ok_button).setOnClickListener {
            val configName = popupView.findViewById<TextInputEditText>(R.id.configuration_name_input).text.toString()
            repertoireManager.saveConfiguration(configName)
            popupWindow.dismiss()
        }
    }

    fun loadConfigurationButton(view: View) {
        if (repertoireManager.configurations.isNotEmpty()) {
            val popupView = layoutInflater.inflate(R.layout.load_configuration_popup, null) as ConstraintLayout
            val popupWindow = PopupWindow(popupView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true)
            popupWindow.showAtLocation(repertoireView, Gravity.CENTER, 0, 0)
            popupView.findViewById<RecyclerView>(R.id.configuration_options).adapter = LoadConfigurationAdapter ({popupWindow.dismiss()}, {repertoireViewAdapter.refreshListViewChecks()})
            popupView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
                popupWindow.dismiss()
            }
        }
    }

    private fun togglePlayOption(view: PlaySettingButton) {
        when (view) {
            playerBestOptionView -> playSettings.playerBest = !playSettings.playerBest
            playerTheoryOptionView -> playSettings.playerTheory = !playSettings.playerTheory
            playerGambitsOptionView -> playSettings.playerGambits = !playSettings.playerGambits
            playerPreferredOptionView -> playSettings.playerPreferred = !playSettings.playerPreferred
            opponentBestOptionView -> playSettings.opponentBest = !playSettings.opponentBest
            opponentTheoryOptionView -> playSettings.opponentTheory = !playSettings.opponentTheory
            opponentGambitsOptionView -> playSettings.opponentGambits = !playSettings.opponentGambits
            opponentMistakesOptionView -> playSettings.opponentMistakes = !playSettings.opponentMistakes
        }
        refreshPlayOptionButtonColors()
    }

    private fun refreshPlayOptionButtonColors() {
        for (view in playOptionViews) {
            when (view) {
                playerBestOptionView -> playerBestOptionView.active = playSettings.playerBest
                playerTheoryOptionView -> playerTheoryOptionView.active = playSettings.playerTheory
                playerGambitsOptionView -> playerGambitsOptionView.active = playSettings.playerGambits
                playerPreferredOptionView -> playerPreferredOptionView.active = playSettings.playerPreferred
                opponentBestOptionView -> opponentBestOptionView.active = playSettings.opponentBest
                opponentTheoryOptionView -> opponentTheoryOptionView.active = playSettings.opponentTheory
                opponentGambitsOptionView -> opponentGambitsOptionView.active = playSettings.opponentGambits
                opponentMistakesOptionView -> opponentMistakesOptionView.active = playSettings.opponentMistakes
            }
            view.updateColor()
        }
    }

    private val testString = "[Event \"Stafford Gambit: Stafford Gambittt\"]\n" +
            "[Site \"https://lichess.org/study/97YHM989/6H5I8LuB\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.04.23\"]\n" +
            "[UTCTime \"14:01:18\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"C42\"]\n" +
            "[Opening \"Russian Game: Stafford Gambit\"]\n" +
            "[Annotator \"https://lichess.org/@/SpookyCrystals\"]\n" +
            "\n" +
            "1. e4 e5 2. Nf3 Nf6 3. Nxe5 Nc6 4. Nxc6 dxc6 5. d3 (5. e5 Ne4 6. d3 (6. d4 Qh4 7. g3 Nxg3 8. hxg3 (8. fxg3 Qe4+ { [%cal Ge4h1] } 9. Qe2 Qxh1) 8... Qxh1) 6... Bc5 7. dxe4 Bxf2+ 8. Kxf2 (8. Ke2 Bg4+ { [%cal Gg4d1] }) 8... Qxd1)  (5. Nc3 Bc5 6. d3 (6. Bc4 Ng4 { [%cal Gg4f2,Gc5f2] } 7. O-O (7. Qf3 { [%cal Rg4f2,Rf3f7] } 7... Ne5 { [%cal Ge5f3,Ge5c4] } 8. Qe2 Qh4 { [%cal Be2f2,Be2c4,Gh4f2,Gc5f2,Ge5c4] } 9. g3 Qh3 { [%cal Gc8g4] }) 7... Qh4 { [%cal Gh4h2,Gh4f2] } 8. h3 Nxf2 { [%cal Gf2d1] } 9. Qf3 \$22 Nxh3+ { [%cal Gc5g1,Gh3g1] } 10. Kh2 (10. Kh1 Nf2+ 11. Kg1 Qh1#) 10... Nf2+ { [%cal Gh4h2] } 11. Qh3 Bd6+ 12. e5 Bxe5+ 13. Kg1 Nxh3+ { [%cal Gh3g1,Gh3f2] } 14. Kh1 Nf2+ 15. Kg1 Bh2# { [%csl Gh1] })  (6. Be2 { [%csl Gg4][%cal Ge2g4,Gc8g4,Gf6g4] } 6... h5 7. h3 Qd4 { [%csl Gf2][%cal Gd4f2] } 8. O-O Ng4 { [%cal Gg4f2,Gd4f2,Gc5f2] } 9. hxg4 (9. Qe1 Qe5 { [%csl Gh2][%cal Ge5h2] }) 9... hxg4 { [%csl Ge5][%cal Gd4e5,Ge5h2,Gc5g1] } 10. g3 (10. Bxg4 Qe5 11. Bh3 \$22 Rxh3 12. gxh3 Qg3+ 13. Kh1 Qxh3+ 14. Kg1 Bd4 15. Nd5 Bg4 16. Nf4 Qg3+ 17. Kh1 Qxf4 18. d3 Bf3+ 19. Qxf3 Qxf3+ 20. Kg1 O-O-O 21. Bf4 Qxf4 22. Kg2 Qg4+ 23. Kh1 Rh8#) 10... Qe5 { [%cal Ge5g3] } 11. Kg2 Bxf2 12. Kxf2 (12. Rxf2 Qh5 { [%cal Gh5h3] } 13. Rf4 { Mate in 27, but where? } 13... Qh2+ 14. Kf1) 12... Rh2+ 13. Ke3 Qxg3+ 14. Kd4 Be6 { [%csl Gd8][%cal Ge8b8,Gd8d4] }) 6... Ng4 { [%cal Gg4f2,Gc5f2] } 7. Be3 { Best move as white but still losing material } 7... Nxe3 { [%cal Ge3f1,Ge3d1] } 8. fxe3 Bxe3 { [%cal Ge3g1,Ge3c1] })  (5. Qe2 Bc5 6. e5 Ng4 7. f3 Bf2+ 8. Kd1 Ne3+ { [%cal Gd8d1] }) 5... Bc5 6. Be2 (6. Bg5 Nxe4 7. Bxd8 \$22 (7. dxe4 Bxf2+ 8. Kxf2 Qxd1)  (7. Be3 Bxe3 8. fxe3 Qh4+ 9. g3 Nxg3 10. hxg3 Qxh1)  (7. Qe2 Qxg5 8. Qxe4+ Kd8 { [%csl Ge8][%cal Gh8e8,Ge8e1] } 9. Be2 Qc1+ 10. Bd1 Re8) 7... Bxf2+ 8. Ke2 Bg4#)  (6. h3 Bxf2+ 7. Kxf2 Nxe4+ { [%cal Gd3e4,Gd8d1] } 8. Kg1 { Force a draw } { [%cal Gd8d4,Gd4e5,Ge5d4,Gg1h2,Gh2g1] } (8. Kf3 O-O 9. Kxe4))  (6. c3 h5 7. Be2 Ng4 8. d4 Qh4 \$140 { same position as another line }) 6... h5 7. c3 (7. O-O Ng4 8. h3 Qd6 9. hxg4 \$22 (9. g3 { [%cal Gc5g1] } 9... Qxg3+) 9... hxg4) 7... Ng4 8. d4 Qh4 9. Bxg4 Bxg4 10. Qd3 O-O-O { [%cal Gd8d3] } 11. Qg3 Qe7 12. dxc5 Rd1# *\n" +
            "\n" +
            "\n" +
            "[Event \"Stafford Gambit: Stafford Gambit Rejected\"]\n" +
            "[Site \"https://lichess.org/study/97YHM989/2cLyWcuE\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.04.23\"]\n" +
            "[UTCTime \"15:30:49\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"C42\"]\n" +
            "[Opening \"Russian Game\"]\n" +
            "[Annotator \"https://lichess.org/@/SpookyCrystals\"]\n" +
            "\n" +
            "1. e4 e5 2. Nf3 Nf6 3. d3 (3. Nxe5 Nc6 4. Nf3) 3... Bc5 4. Nxe5 Nc6 5. Nf3 d5 6. e5 Ng4 7. d4 Nxd4 8. Nxd4 Nxf2 9. Kxf2 Qh4+ 10. g3 Bxd4+ { [%cal Gc1e3] } 11. Be3 { Only move } 11... Bxe3+ 12. Kxe3 Qe4+ { Do not take rook! } { [%csl Bh1,Rb5][%cal Be4h1,Rf1b5,Rd1h1] } 13. Kf2 O-O *\n" +
            "\n" +
            "\n" +
            "[Event \"Stafford Gambit: Against Stafford as White\"]\n" +
            "[Site \"https://lichess.org/study/97YHM989/B9JB7rro\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.04.23\"]\n" +
            "[UTCTime \"15:40:02\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"C42\"]\n" +
            "[Opening \"Russian Game: Stafford Gambit\"]\n" +
            "[Annotator \"https://lichess.org/@/SpookyCrystals\"]\n" +
            "\n" +
            "1. e4 e5 2. Nf3 Nf6 3. Nxe5 (3. Bc4 Nxe4 { Stafford as White } 4. Nc3 Nxc3 5. dxc3 f6 { [%cal Gf3h4] } (5... d6 6. Ng5 { [%cal Gg5f7,Gc4f7] } (6. Nxe5 Qe7 (6... dxe5 7. Bxf7+ Kxf7 8. Qxd8)) 6... Be6 7. Bxe6 fxe6 8. Qf3 { [%csl Gf7][%cal Gf3f7,Gg5f7,Gf3b7] } 8... Be7 (8... Qe7 9. Qxb7) 9. Qf7+ Kd7 10. Qxe6+ { [%csl Gg5] } 10... Ke8 11. Nf7 Qd7 12. Qxd7+ Kxd7 13. Nxh8)  (5... Nc6 6. Ng5 Qe7 7. Bxf7+ { [%csl Gf7][%cal Gg5f7,Ge7f7] } 7... Kd8 8. Ne6+ { [%csl Gd7][%cal Gd1d8] } 8... Qxe6 9. Bxe6) 6. Nh4 g6 7. f4 Qe7 8. f5 { [%cal Gf5g6,Gh4g6] } 8... Qg7 9. fxg6 hxg6 10. Qg4 { [%cal Gg4d7] }) 3... Nc6 { Accepting Stafford } 4. Nxc6 (4. Nc3 { Refutes Stafford -> Halloween Gambit } 4... Nxe5 (4... Bc5 5. Nxc6 dxc6 { Stafford...again }) 5. d4 Ng6 6. e5 Ng8 7. Bc4) 4... dxc6 5. d3 Bc5 6. Be2 h5 (6... Ng4 7. Bxg4 Qh4 8. g3 Qxg4 9. Qxg4 Bxg4) 7. c3 { Hafu Variation } *\n" +
            "\n" +
            "\n" +
            "[Event \"Stafford Gambit: Interesting Stafford Variations\"]\n" +
            "[Site \"https://lichess.org/study/97YHM989/yCFdm3pU\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.04.24\"]\n" +
            "[UTCTime \"02:36:10\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"C42\"]\n" +
            "[Opening \"Russian Game: Stafford Gambit\"]\n" +
            "[Annotator \"https://lichess.org/@/SpookyCrystals\"]\n" +
            "\n" +
            "1. e4 e5 2. Nf3 Nf6 3. Nxe5 Nc6 4. Nxc6 dxc6 5. d3 Bc5 6. f3 Nh5 7. g3 f5 8. Nc3 O-O 9. Qe2 f4 10. g4 Qh4+ 11. Kd1 Ng3 { [%csl Ge2,Gh1,Gh2][%cal Gg3e2,Gg3h1,Gh4h1] } 12. Qe1 { [%cal Ge1h4] } 12... Qh6 { Rook trapped (but not very good) } { [%csl Gg1,Gh1][%cal Gc5g1,Gg3h1,Gh6h1] } 13. Qxg3 { [%cal Gc1h6] } 13... fxg3 14. Bxh6 gxh6 15. Ke2 *\n" +
            "\n" +
            "\n" +
            "[Event \"Stafford Gambit: Chapter 6\"]\n" +
            "[Site \"https://lichess.org/study/97YHM989/U4dTg5jK\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.05.15\"]\n" +
            "[UTCTime \"22:39:26\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"?\"]\n" +
            "[Opening \"?\"]\n" +
            "[Annotator \"https://lichess.org/@/SpookyCrystals\"]\n" +
            "\n" +
            "*\n" +
            "\n" +
            "\n" +
            "[Event \"Stafford Gambit: Beautiful game by Eric Rosen\"]\n" +
            "[Site \"https://lichess.org/study/97YHM989/lyM76BUr\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.06.27\"]\n" +
            "[UTCTime \"19:10:28\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"C42\"]\n" +
            "[Opening \"Russian Game: Stafford Gambit\"]\n" +
            "[Annotator \"https://lichess.org/@/SpookyCrystals\"]\n" +
            "\n" +
            "1. e4 e5 2. Nf3 Nf6 3. Nxe5 Nc6 4. Nxc6 dxc6 5. Nc3 Bc5 6. Be2 h5 7. O-O Ng4 8. h3 { Game begins here } 8... Qd4 9. hxg4 hxg4 10. Bxg4 Qe5 { [%csl Gh2][%cal Ge5h2,Gc5g1] } 11. Re1 Qh2+ 12. Kf1 Qh1+ 13. Ke2 Bxg4+ 14. f3 Qxg2+ 15. Kd3 Bxf3 16. Ne2 Rh3 { [%cal Gf3e2,Be2d3,Bh3d3] } 17. Kc4 Bxe2+ 18. Rxe2 b5+ 19. Kxc5 Qg5+ 20. e5 Qe7+ 21. Kd4 { [%cal Ge7b4] } 21... f5 { [%csl Ge4][%cal Gf5e4] } (21... Qb4+ { Eric wanted something elegant - did not play this }) 22. exf6 O-O-O# { [%csl Gc4,Gd5,Gc5][%cal Gd8d3,Ge7e3,Gh3b3,Ge7c5,Gc6d5,Gb5c4] } *\n" +
            "\n" +
            "\n"

    private val testString2 = "[Event \"Test Study: Chapter 1\"]\n" +
            "[Site \"https://lichess.org/study/DGjt4lwU/32qGstHX\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.07.25\"]\n" +
            "[UTCTime \"03:15:10\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"D20\"]\n" +
            "[Opening \"Queen's Gambit Accepted: Old Variation\"]\n" +
            "[Annotator \"https://lichess.org/@/CircleBreaker\"]\n" +
            "\n" +
            "1. d4 \$THEORY {just a comment} d5 {comment for a hint} 2. c4 dxc4 \$MISTAKE 3. e3 (3. e4 \$MISTAKE) 3... b5 4. Qf3 *\n" +
            "\n" +
            "\n" +
            "[Event \"Test Study: Chapter 2\"]\n" +
            "[Site \"https://lichess.org/study/DGjt4lwU/C8nP4WlO\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.07.25\"]\n" +
            "[UTCTime \"03:33:53\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"D30\"]\n" +
            "[Opening \"Queen's Gambit Declined\"]\n" +
            "[Annotator \"https://lichess.org/@/CircleBreaker\"]\n" +
            "\n" +
            "1. d4 d5 2. c4 e6 *\n" +
            "\n" +
            "\n" +
            "[Event \"Test Study: Chapter 3\"]\n" +
            "[Site \"https://lichess.org/study/DGjt4lwU/C8nP4WlO\"]\n" +
            "[Result \"*\"]\n" +
            "[UTCDate \"2021.07.25\"]\n" +
            "[UTCTime \"03:33:53\"]\n" +
            "[Variant \"Standard\"]\n" +
            "[ECO \"D30\"]\n" +
            "[Opening \"Queen's Gambit Declined\"]\n" +
            "[Annotator \"https://lichess.org/@/CircleBreaker\"]\n" +
            "\n" +
            "1. e4 \$THEORY d5 2. c4 e6 *"
}