package com.example.smenovykalendarv2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class FragmentShow extends DialogFragment {
    MainActivity.Cell cell;
    private MainActivity.Handler handler;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View fragmentView = inflater.inflate(R.layout.fragment_show, null);
        builder.setView(fragmentView);
        Button ok = (Button) fragmentView.findViewById(R.id.ok);
        Button zmazat = (Button) fragmentView.findViewById(R.id.zmazat);
        Button upravit = (Button) fragmentView.findViewById(R.id.upravit);
        TextView poznamka = (TextView) fragmentView.findViewById(R.id.poznamka_show);
        poznamka.setMovementMethod(new ScrollingMovementMethod());
        TextView type = (TextView) fragmentView.findViewById(R.id.type_show);
        ImageView typePic = (ImageView) fragmentView.findViewById(R.id.type_pic_show);
        TextView name = (TextView) fragmentView.findViewById(R.id.name);
        name.setText(cell.nameLong);
        poznamka.setText(cell.text);
        switch (cell.type){
            case 0: break;
            case 1: type.setText("Vložená");typePic.setImageResource(R.drawable.nardabacka_50dp);break;
            case 2: type.setText("Školenie");typePic.setImageResource(R.drawable.skolenie_50dp);break;
            case 3: type.setText("Iné");typePic.setImageResource(R.drawable.poznamka_50dp);break;
        }
        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        upravit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.openEdit();
                dismiss();
            }
        });
        zmazat.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cell.type = 0;
                cell.text = null;
                handler.delete();
                dismiss();
            }
        });

        return builder.create();
    }
    public void send(MainActivity.Cell cell, MainActivity.Handler handler){
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
    @Override
    public void onDetach(){
        super.onDetach();
    }
}
