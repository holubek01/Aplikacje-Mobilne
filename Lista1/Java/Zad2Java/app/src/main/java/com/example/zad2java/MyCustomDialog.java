package com.example.zad2java;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyCustomDialog extends DialogFragment {
    private int countAttempts;

    public MyCustomDialog(int countAttempts)
    {
        this.countAttempts=countAttempts;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_win, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView name = view.findViewById(R.id.info);
        System.out.println("cjuj");
        if (countAttempts == 1)
        {
            name.setText("Gratulacje, udało ci się zgadnąć\n liczbę w 1 próbie!");
        }
        else
        {
            System.out.println("siemasiema");
            name.setText("Gratulacje, udało ci się zgadnąć\n liczbę w "+ countAttempts +" próbach!");

        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        //getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
