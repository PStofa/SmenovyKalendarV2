package com.example.smenovykalendarv2;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class CustomFragment extends DialogFragment{
    private MainActivity.Cell cell;
    private MainActivity.Handler handler;
    int type;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View fragmentView = inflater.inflate(R.layout.fragment, null);
        Button Ulozit = (Button) fragmentView.findViewById(R.id.Ulozit);
        Button Zrusit = (Button) fragmentView.findViewById(R.id.Zrusit);
        Spinner Type = (Spinner) fragmentView.findViewById(R.id.type);
        TextView name = (TextView) fragmentView.findViewById(R.id.name);
        name.setText(cell.nameLong);
        Type.setSelection(0);
        final EditText Poznamka = (EditText) fragmentView.findViewById(R.id.Poznamka);
        final ImageView TypePic = (ImageView) fragmentView.findViewById(R.id.type_pic);
        if(cell.text.equals("")) {
            Poznamka.setText(cell.text);
        }
        switch (cell.type){
            case 0: Type.setSelection(0); break;
            case 1: Type.setSelection(0); break;
            case 2: Type.setSelection(1); break;
            case 3: Type.setSelection(2); break;
        }
        builder.setView(fragmentView);
        Type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position){
                    case 0:TypePic.setImageResource(R.drawable.nardabacka_50dp);type = 1;break;
                    case 1:TypePic.setImageResource(R.drawable.skolenie_50dp);type = 2;break;
                    case 2:TypePic.setImageResource(R.drawable.poznamka_50dp);type = 3;break;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        Zrusit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Ulozit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cell.type = type;
                cell.text = Poznamka.getText().toString();
                handler.save();
                dismiss();
            }
        });
        return builder.create();
    }
    public void send(MainActivity.Cell cell, MainActivity.Handler handler ){
        this.cell = cell;
        this.handler = handler;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }

        int dialogWidth = getResources().getDimensionPixelSize(R.dimen.dialog_width);
        int dialogHeight = getResources().getDimensionPixelSize(R.dimen.dialog_height);

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    }
