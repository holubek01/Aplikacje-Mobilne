package com.example.minesweeper

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.hardware.display.DisplayManagerCompat
import androidx.fragment.app.DialogFragment
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var linearLayout: LinearLayout
    private lateinit var fieldList : Array<Array<FieldBoard>>
    private lateinit var minesTextView : TextView
    private lateinit var timer : Chronometer

    private var isPlay = false
    private val boardSize = 9
    private val startMinesCount = 10
    
    private var minesLeft=10
    private var nonMinesCount = 0


    enum class GameStatus{WON, LOST}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayout = findViewById(R.id.innerLinearLayout)
        minesTextView = findViewById(R.id.MineCount)
        timer = findViewById(R.id.Time)

        fieldList = Array(boardSize+1) { Array(boardSize+1) { FieldBoard(Button(this))} }
        minesTextView.text="Miny:$startMinesCount"

        initializeBoard()
        setMines()
        setListeners()


        timer.base = SystemClock.elapsedRealtime()
        timer.start()
        isPlay = true
    }

    private fun setListeners() {
        for(i in 1..boardSize)
        {
            for(j in 1..boardSize)
            {
                fieldList[i][j].button.setOnClickListener {
                    mainLogic(i, j)
                }

                fieldList[i][j].button.setOnLongClickListener{
                    setFlagged(fieldList[i][j])
                    true
                }
            }
        }
    }


    private fun checkEndOfGame()
    {
        if (nonMinesCount==boardSize*boardSize-startMinesCount && minesLeft==0)
        {
            endOfGame(GameStatus.WON)
        }
    }



    private fun setFlagged(button : FieldBoard) {

        if (!button.isFlagged && button.isEnabled)
        {
            button.isFlagged = true
            button.button.background = ResourcesCompat.getDrawable(resources, R.drawable.flag, null)
            minesLeft--

            minesTextView.text = "Miny:$minesLeft"
        }

    }



    private fun removeFlag(button:FieldBoard)
    {
        //jesli jest oflagowany a na niego klikniemy to staje sie nieodkryty, natomiast liczba min wzrasta
        button.isFlagged = false
        button.button.background = ResourcesCompat.getDrawable(resources, R.drawable.hiddenfield, null)
        minesLeft++

        minesTextView.text = "Miny:$minesLeft"
    }


    private fun revealFieldWithCount(button: FieldBoard)
    {
        button.isEnabled = false
        val path= "field${button.count}"

        button.button.background =
            ResourcesCompat.getDrawable(resources, this.resources.getIdentifier(path, "drawable", this.packageName),null)

        nonMinesCount++


    }


    private fun revealEmptyField(button: FieldBoard, row:Int, col:Int)
    {
        button.button.background = ResourcesCompat.getDrawable(resources, R.drawable.field0, null)
        button.isEnabled = false
        nonMinesCount++

        mainLogic(row-1, col)
        mainLogic(row+1, col)
        mainLogic(row, col-1)
        mainLogic(row, col+1)
    }




    @SuppressLint("SetTextI18n")
    private fun mainLogic(row: Int, col: Int) {

        //wygrana następuje przez odkrycie wszystkich pól bez min i oznaczenie
        //flagami pozostałych

        checkEndOfGame()
        if (row !in 1..boardSize || col !in 1..boardSize) return

        val button = fieldList[row][col]

        if (button.isEnabled) {

            if(button.isFlagged) removeFlag(button)
            else if (button.isMine) endOfGame(GameStatus.LOST)
            else if (button.count > 0) revealFieldWithCount(button)
            else if (button.count == 0) revealEmptyField(button, row, col)

            checkEndOfGame()
        }
    }

    private fun endOfGame(result: GameStatus) {
        timer.stop()
        disableAll()

        /*
        val minesList = fieldList.flatten()
            .filter { it.isMine }
            .map { it.button }

         */

        val minesList = mutableListOf<Button>()
        val notMines = mutableListOf<FieldBoard>()

        fieldList.flatten().forEach { button ->
            if (button.isMine) {minesList.add(button.button)}
            else {notMines.add(button)}}


        minesList.forEach {
            it.background = ResourcesCompat.getDrawable(resources, R.drawable.mine, null)
        }

        notMines.forEach {
            val path = "field${it.count}"
            it.button.background = ResourcesCompat.getDrawable(
                resources, resources.getIdentifier(path, "drawable", packageName), null
            )
        }

        class MyCustomDialog: DialogFragment() {


            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View {

                dialog?.window?.attributes!!.windowAnimations = R.style.DialogAnimation

                dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val rootView : View = inflater.inflate(R.layout.dialog_win, container, false)
                rootView.findViewById<Button>(R.id.exit_btn).setOnClickListener{ exitProcess(-1) }
                rootView.findViewById<Button>(R.id.restart_btn).setOnClickListener{
                    dismiss()
                    restartGame(rootView)
                }

                val name = rootView.findViewById<TextView>(R.id.info)



                if (result == GameStatus.LOST) {
                    rootView.findViewById<TextView>(R.id.title).text = "Porażka!"
                    rootView.findViewById<ImageView>(R.id.imageView2).setImageResource(R.drawable.sad)
                    name.text = "Niestety przegrałeś, może spróbujesz jeszcze raz?"
                }

                return rootView
            }

            override fun onStart() {
                super.onStart()


                val defaultDisplay = DisplayManagerCompat.getInstance(this@MainActivity).getDisplay(Display.DEFAULT_DISPLAY)
                val displayContext = createDisplayContext(defaultDisplay!!)
                val width = displayContext.resources.displayMetrics.widthPixels
                val height = displayContext.resources.displayMetrics.heightPixels

                dialog?.window
                    ?.setLayout((width*0.9).toInt(), (height*0.8).toInt())
            }
        }


        val dialog  = MyCustomDialog()
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.show(supportFragmentManager, "CustomDialog")
        }, 1500)


    }


    private fun disableAll() {
        fieldList.forEach { row ->
            row.forEach { fieldBoard ->
                fieldBoard.isEnabled = false
            }
        }
    }




    private fun setNeighbours(randRow:Int, randCol:Int)
    {
        for (i in randRow - 1..randRow + 1) {
            for (j in randCol - 1..randCol + 1) {
                if (i in 1..9 && j in 1..9)
                {
                    fieldList[i][j].count++
                }
            }
        }
    }

    private fun setMines() {
        for (i in 0 until startMinesCount) {
            var randRow: Int
            var randCol: Int

            //losuj nowe liczby dopóki nie natrafisz na pole, które nie jest miną
            do {
                randRow = (1..boardSize).random()
                randCol = (1..boardSize).random()
            } while (fieldList[randRow][randCol].isMine)

            fieldList[randRow][randCol].isMine = true


            setNeighbours(randRow, randCol)

        }
    }

    private fun initializeBoard() {
        val defaultDisplay = DisplayManagerCompat.getInstance(this).getDisplay(Display.DEFAULT_DISPLAY)
        val displayContext = createDisplayContext(defaultDisplay!!)
        val width = displayContext.resources.displayMetrics.widthPixels
        

        val paramsRow = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0
        ).apply { weight = 1.0F }


        val paramsField = LinearLayout.LayoutParams(
            width/(boardSize+1),
            width/(boardSize+1)
        ).apply { weight = 1.0F }


        for (i in 1..boardSize){
            val horizontalLinearLayout = LinearLayout(this).apply { layoutParams = paramsRow }


            for (j in 1..boardSize)
            {
                val button = Button(this).apply { background = ResourcesCompat.getDrawable(resources, R.drawable.hiddenfield, null) }
                button.layoutParams = paramsField
                horizontalLinearLayout.addView(button)
                val field = FieldBoard(button)
                fieldList[i][j] = field
            }
            linearLayout.addView(horizontalLinearLayout)
        }
    }

    fun restartGame(view:View) {
        minesLeft= startMinesCount
        nonMinesCount = 0

        minesTextView.text = "miny:${startMinesCount}"

        fieldList.forEach { row ->
            row.forEach { fieldBoard ->
                fieldBoard.count = 0
                fieldBoard.isEnabled = true
                fieldBoard.isMine = false
                fieldBoard.isFlagged = false
                fieldBoard.button.background = ResourcesCompat.getDrawable(resources, R.drawable.hiddenfield, null)
            }
        }

        setMines()
        timer.base = SystemClock.elapsedRealtime()
        timer.start()
    }
}


class FieldBoard(
    var button: Button,
    var count: Int = 0,
    var isMine: Boolean = false,
    var isFlagged: Boolean = false,
    var isEnabled: Boolean = true
)




/*
// gdy w środku planszy

if (randRow in 2..8 && randCol >= 2 && randCol <= 8) {
    for (i in randRow - 1..randRow + 1) {
        for (j in randCol - 1..randCol + 1) {
            fieldList[i][j].count++
        }
    }
}


// gdy w rogach planszy
else if (randRow == 1 && randCol == 1) {
    fieldList[2][2].count++
    fieldList[1][2].count++
    fieldList[2][1].count++
} else if (randRow == 1 && randCol == boardSize) {
    fieldList[2][boardSize - 1].count++
    fieldList[1][boardSize - 1].count++
    fieldList[2][boardSize].count++
} else if (randRow == boardSize && randCol == 1) {
    fieldList[boardSize - 1][2].count++
    fieldList[boardSize - 1][1].count++
    fieldList[boardSize][2].count++
} else if (randRow == boardSize && randCol == boardSize) {
    fieldList[boardSize - 1][boardSize - 1].count++
    fieldList[boardSize - 1][boardSize].count++
    fieldList[boardSize][boardSize - 1].count++
}

//gdy w 1 kolumnie
else if (randCol==1)
{

    fieldList[randRow-1][1].count++
    fieldList[randRow-1][2].count++
    fieldList[randRow][2].count++
    fieldList[randRow+1][2].count++
    fieldList[randRow+1][1].count++
}

//gdy w 1 wierszu
else if (randRow==1)
{
    fieldList[1][randCol-1].count++
    fieldList[1][randCol+1].count++
    fieldList[2][randCol-1].count++
    fieldList[2][randCol].count++
    fieldList[2][randCol+1].count++
}

//gdy w ostatniej kolumnie
else if (randCol == 9)
{
    fieldList[randRow - 1][boardSize-1].count++
    fieldList[randRow - 1][boardSize].count++
    fieldList[randRow][boardSize-1].count++
    fieldList[randRow + 1][boardSize].count++
    fieldList[randRow + 1][boardSize-1].count++
}

//gdy w ostatnim wierszu
else if (randRow==boardSize)
{
    fieldList[boardSize][randCol-1].count++
    fieldList[boardSize][randCol+1].count++
    fieldList[boardSize-1][randCol-1].count++
    fieldList[boardSize-1][randCol].count++
    fieldList[boardSize-1][randCol+1].count++
}

     */