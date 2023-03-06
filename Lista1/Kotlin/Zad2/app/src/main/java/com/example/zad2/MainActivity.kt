package com.example.zad2

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import kotlin.random.Random
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private var randomNumber = 0;
    private var countAttempts = 0;
    lateinit var answer : TextView
    lateinit var attempts : TextView
    lateinit var guess : TextInputEditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        randomNumber = Random.nextInt(100);

        guess = findViewById<TextInputEditText>(R.id.guessField)
        answer = findViewById<TextView>(R.id.textView)
        attempts = findViewById<TextView>(R.id.attempts)
    }


    fun reset()
    {
        randomNumber = Random.nextInt(100);
        countAttempts=0;
        answer.text = "Pomyślałem liczbę z zakresu 1-100. Zgadnij jaką"
        attempts.text = attempts.text.substring(0, attempts.length()-1) + countAttempts

    }

    fun guess(view: View) {
        countAttempts++
        attempts.text = attempts.text.substring(0, attempts.length()-1) + countAttempts

        if (guess.text.toString() != "")
        {
            var atmp = guess.text.toString().toInt()

            if (atmp > randomNumber)
            {
                answer.text = "Za dużo!"
            }
            else if (atmp < randomNumber)
            {
                answer.text = "Za mało!"
            }
            else
            {
                class MyCustomDialog: DialogFragment() {


                    override fun onCreateView(
                        inflater: LayoutInflater,
                        container: ViewGroup?,
                        savedInstanceState: Bundle?
                    ): View? {

                        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        var rootView : View = inflater.inflate(R.layout.dialog_win, container, false)
                        rootView.findViewById<Button>(R.id.exit_btn).setOnClickListener{ exitProcess(-1) }
                        rootView.findViewById<Button>(R.id.restart_btn).setOnClickListener{
                            dismiss()
                            reset()
                        }


                        val name = rootView.findViewById<TextView>(R.id.info)
                        if (countAttempts == 1)
                        {
                            name.text = "Gratulacje, udało ci się zgadnąć\n liczbę w $countAttempts próbie!"
                        }
                        else
                        {
                            name.text = "Gratulacje, udało ci się zgadnąć\n liczbę w $countAttempts próbach!"

                        }


                        return rootView
                    }

                    override fun onStart() {
                        super.onStart()
                            //getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corners);
                        //dialog?.window?.setLayout(900, 850)
                        dialog?.getWindow()
                            ?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                }

                val dialog  = MyCustomDialog()
                dialog.show(supportFragmentManager, "CustomDialog")
            }
        }
        else
        {
            Toast.makeText(this, "Wpisz liczbę zanim klikniesz przycisk", Toast.LENGTH_SHORT).show()
            countAttempts--;
            attempts.text = attempts.text.substring(0, attempts.length()-1) + countAttempts

        }

        guess.setText("")
    }
}