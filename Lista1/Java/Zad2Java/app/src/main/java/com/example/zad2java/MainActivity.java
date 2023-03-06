package com.example.zad2java;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private int randomNumber = 0;
    private int countAttempts = 0;
    private TextView answer;
    private TextView attempts;
    private TextInputEditText guess;
    private Random random;
    private MyCustomDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        random = new Random();

        randomNumber = random.nextInt(100);

        guess = findViewById(R.id.guessField);
        answer = findViewById(R.id.textView);
        attempts = findViewById(R.id.attempts);
    }

    public void restart(View view)
    {
        dialog.dismiss();
        randomNumber = random.nextInt(100);
        countAttempts=0;
        answer.setText("Pomyślałem liczbę z zakresu 1-100. Zgadnij jaką");
        attempts.setText(attempts.getText().subSequence(0, attempts.length()-1).toString() + countAttempts);

    }

    public void exit()
    {
        System.exit(0);
    }



    public void guess(View view) {
        countAttempts++;
        attempts.setText(attempts.getText().subSequence(0, attempts.length()-1).toString() + countAttempts);

        if (!Objects.requireNonNull(guess.getText()).toString().equals(""))
        {
            int atmp = Integer.parseInt(guess.getText().toString());

            if (atmp > randomNumber)
            {
                answer.setText("Za dużo!");
            }
            else if (atmp < randomNumber)
            {
                answer.setText("Za mało!");
            }
            else
            {
                    dialog = new MyCustomDialog(countAttempts);
                    dialog.show(getSupportFragmentManager(), "CustomDialog");
            }

            guess.setText("");
    }
        else
        {
            Toast.makeText(this, "Wpisz liczbę przed naciśnięciem przycisku", Toast.LENGTH_LONG).show();
            countAttempts--;
            attempts.setText(attempts.getText().subSequence(0, attempts.length()-1).toString() + countAttempts);
        }
}


}