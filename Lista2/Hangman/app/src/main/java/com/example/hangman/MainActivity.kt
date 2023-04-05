package com.example.hangman

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.hardware.display.DisplayManagerCompat
import androidx.fragment.app.DialogFragment
import java.util.*
import kotlin.random.Random
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    private lateinit var words: Array<String>
    private lateinit var word : TextView
    private lateinit var image: ImageView
    private lateinit var lettersLayout : LinearLayout
    private var keyboard : MutableList<Button> = mutableListOf()
    private lateinit var correctWord : String
    private var imgNr : Int = 0
    private var numberOfIncorrectLetters = 0
    private val maxNumberOfIncorrectLetters = 12

    enum class GamegameStatus{WON, LOST, ACTIVE}
    var gameStatus = GamegameStatus.ACTIVE

    private val hangmanImages = listOf(
        R.drawable.hangman_0, R.drawable.hangman_1, R.drawable.hangman_2,
        R.drawable.hangman_3, R.drawable.hangman_4, R.drawable.hangman_5,
        R.drawable.hangman_6, R.drawable.hangman_7, R.drawable.hangman_8,
        R.drawable.hangman_9, R.drawable.hangman_10, R.drawable.hangman_11,R.drawable.hangman_12
    )

    private val correctLetters : MutableSet<Char> = mutableSetOf()
    private val letters : MutableMap<Int, Char> = mutableMapOf(1 to 'A',2 to 'Ą',3 to 'B',4 to 'C',5 to 'Ć',
        6 to 'D', 7 to 'E',8 to 'Ę', 9 to 'F',10 to 'G',11 to 'H',
        12 to 'I',13 to 'J',14 to 'K',15 to 'L',16 to 'Ł',17 to 'M',18 to 'N',19 to 'Ń',
        20 to 'O',21 to 'Ó', 22 to 'P',23 to 'Q', 24 to 'R',25 to 'S',26 to 'Ś',27 to 'T',28 to 'U', 29 to 'V',
        30 to 'W',31 to 'X', 32 to 'Y',33 to 'Z', 34 to 'Ż', 35 to 'Ź')
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lettersLayout = findViewById(R.id.lettersLayout)
        words = resources.getStringArray(R.array.words_array)
        word = findViewById<TextView>(R.id.word)
        image = findViewById<ImageView>(R.id.hangmanImage)

        initializeLetters()
        chooseWord()
        setListeners()


    }

    private fun chooseWord() {
        correctWord = words[Random.nextInt(0, words.size)].uppercase(Locale.ROOT)
        word.text = ""

        for (i in correctWord.indices)
        {
            word.append("_")
        }

    }

    private fun setListeners() {
        letters.forEach { (key, value) ->
            keyboard[key - 1].setOnClickListener {
                useThatLetter(keyboard[key - 1])
            }
        }

    }

    private fun useThatLetter(letter: Button) {
        val ch = letter.text[0]

        if (correctWord.contains(ch))
        {
            correctLetters.add(ch)
            letter.setBackgroundColorById(R.color.green)
            var index = correctWord.indexOf(ch)
            while (index != -1) {
                val tmp = StringBuilder(word.text.toString()).also { it.setCharAt(index, correctWord[index]) }
                word.text = tmp.toString()
                index = correctWord.indexOf(ch, index + 1)

            }
        }
        else{
            numberOfIncorrectLetters++
            letter.setBackgroundColorById(R.color.red)

            imgNr++

            if (imgNr <= maxNumberOfIncorrectLetters) {
                image.setImageResource(hangmanImages[imgNr])
            }
        }

        letter.isEnabled = false
        checkEndOfGame()
    }

    private fun checkEndOfGame() {
        if (numberOfIncorrectLetters == maxNumberOfIncorrectLetters)
        {
            gameStatus = GamegameStatus.LOST
            disableAll()

            val textSpan = SpannableString(correctWord)

            for (letter in correctWord.toCharArray())
            {
                if (letter !in correctLetters) textSpan.setSpan(ForegroundColorSpan(Color.RED),correctWord.indexOf(letter) , correctWord.indexOf(letter)+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            }
            word.text = textSpan
        }
        else if (word.text.toString() == correctWord)
        {
            val textSpan = SpannableString(correctWord)
            textSpan.setSpan(ForegroundColorSpan(Color.GREEN),0 , correctWord.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            word.text = textSpan
            gameStatus = GamegameStatus.WON

            disableAll()
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
                    reset(rootView)
                }

                val name = rootView.findViewById<TextView>(R.id.info)


                if (gameStatus == GamegameStatus.LOST) {
                    rootView.findViewById<TextView>(R.id.title).text = "Porażka!"
                    rootView.findViewById<ImageView>(R.id.imageView2).setImageResource(R.drawable.sad)
                    name.text = "Przegrałeś! Poprawne słowo to: $correctWord"
                }

                return rootView
            }

            override fun onStart() {
                super.onStart()
                val defaultDisplay = DisplayManagerCompat.getInstance(this@MainActivity).getDisplay(
                    Display.DEFAULT_DISPLAY)
                val displayContext = createDisplayContext(defaultDisplay!!)
                val width = displayContext.resources.displayMetrics.widthPixels
                val height = displayContext.resources.displayMetrics.heightPixels
                
                dialog?.window
                    ?.setLayout((width*0.9).toInt(), (height*0.8).toInt())

            }
        }


        if (gameStatus != GamegameStatus.ACTIVE)
        {
            val dialog  = MyCustomDialog()
            Handler(Looper.getMainLooper()).postDelayed({
                dialog.show(supportFragmentManager, "CustomDialog")
            }, 1100)
        }


    }

    private fun disableAll() {
        for(item in letters)
        {
            keyboard[item.key-1].isEnabled = false
        }
    }


    private fun initializeLetters() {
        val columns = 5
        val rows = 7

        val paramsRow = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1.0F
        }
        val paramsCol = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        ).apply {
            weight = 1.0F
            setMargins(5,5,5,5)
        }


        var counter = 1
        for (i in 0 until columns){
            val horizontalLinearLayout = LinearLayout(this)
            horizontalLinearLayout.layoutParams = paramsRow

            for (j in 0 until rows)
            {
                val button = Button(this)
                button.setBackgroundColorById(R.color.purple_500)


                button.id = counter
                button.layoutParams = paramsCol
                horizontalLinearLayout.addView(button)
                button.text = letters[counter].toString()
                button.textSize
                button.maxLines = 1
                button.isEnabled = true

                button.setTextColor(ContextCompat.getColor(this, R.color.white))
                button.setAutoSizeTextTypeUniformWithConfiguration(10,40,2  , 2)
                counter++

                keyboard.add(button)
            }

            lettersLayout.addView(horizontalLinearLayout)
        }
    }

    fun reset(view: View) {
        gameStatus = GamegameStatus.ACTIVE
        resetKeyboard()
        chooseWord()
        image.setImageResource(R.drawable.hangman_0)
        numberOfIncorrectLetters = 0
        imgNr = 0
        setListeners()
    }

    private fun resetKeyboard() {
        for (item in keyboard)
        {
            item.isEnabled = true
            item.setBackgroundColorById(R.color.purple_500)
        }
    }


    fun Button.setBackgroundColorById(id: Int) {
        setBackgroundColor(ContextCompat.getColor(context, id))
    }
}