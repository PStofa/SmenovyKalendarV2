package com.example.smenovykalendarv2;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    SQLiteDatabase mydb;
    Calendar cal = Calendar.getInstance();
    ArrayList<Cell> cellArray = new ArrayList<Cell>();
    String sMesiac;
    int smena;
    String sNadrabacky;
    final String [][][] nadrabacky = {
            {{"2","3","2","3","3","2","2","2","2","2","2","2"},
                    {"3","2","3","2","2","3","2","2","3","2","3","2"},
                    {"2","2","3","3","2","3","2","2","3","2","2","2"},
                    {"3","2","2","3","2","2","3","2","2","3","2","3"},
                    {"2","3","2","2","3","2","2","3","2","2","2","2"}},
            {{"3","2","3","2","2","2","3","2","2","3","2","3"},
                    {"2","3","2","2","3","2","2","3","2","2","2","2"},
                    {"3","3","2","2","2","3","2","2","3","2","2","3"},
                    {"3","2","2","3","2","3","2","2","3","2","2","2"},
                    {"2","2","2","3","3","2","2","3","2","2","2","2"}},
            {{"3","2","2","2","2","3","2","2","3","2","2","3"},
                    {"2","2","2","3","3","2","2","2","2","3","2","2"},
                    {"2","3","2","2","3","2","2","3","2","2","2","2"},
                    {"3","2","2","2","2","3","2","2","3","2","3","3"},
                    {"3","2","2","3","2","2","3","2","2","3","2","3"}}
    };
    final String[] smeny = {"R","R","P","N","N","V","V","V","R","R","P","P","N","V","V","V","V","R","P","P","N","N","V","V","V"};
    final String[] sviatky = {"0101","0106","0501","0508","0705","0829","0901","0915","1101","1117","1224","1225","1226"};
    final String[] velkanoc = {"20190419","20190422","20200410","20200413","20210402","20210405","20220415","20220418","20230407","20230410","20240329","20240401","20250418","20250421","20260403","20260406"};
    int nSmena;
    AnimatorSet popup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mydb = openOrCreateDatabase("poznamky",MODE_PRIVATE,null);
        mydb.execSQL("CREATE TABLE IF NOT EXISTS Poznamky(Date TEXT,Type INTEGER,Pozn TEXT);");
        mPrefs = getSharedPreferences("Smeny", MODE_PRIVATE);
        mEditor = mPrefs.edit();
        smena = mPrefs.getInt("smena",0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button edit = (Button) findViewById(R.id.edit_button);
        final Button next = (Button) findViewById(R.id.next);
        final Button prev = (Button) findViewById(R.id.prev);
        final TextView mesiac = (TextView) findViewById(R.id.mesiac);
        final TextView nadrabacky = (TextView) findViewById(R.id.vlozene);
        final TextView rok = (TextView) findViewById(R.id.rok);
        CheckFields();
        mesiac.setText(sMesiac);
        nadrabacky.setText(sNadrabacky);
        Refresh();
        final CellAdapter mCellAdapter = new CellAdapter(this, cellArray);
        GridView grid = (GridView) findViewById(R.id.grid);
        grid.setAdapter(mCellAdapter);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setSelection(smena);
        final Handler handler = new Handler(edit, mCellAdapter);
        popup = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.popup);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                handler.selected(position);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch(position) {
                    case 0:
                        nSmena = 11;
                        smena = 0;
                        break;
                    case 1:
                        nSmena = 16;
                        smena = 1;
                        break;
                    case 2:
                        nSmena = 6;
                        smena = 2;
                        break;
                    case 3:
                        nSmena = 21;
                        smena = 3;
                        break;
                    case 4:
                        nSmena = 1;
                        smena = 4;
                        break;
                }
                Refresh();
                CheckFields();
                nadrabacky.setText(sNadrabacky);
                mCellAdapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                cal.add(Calendar.MONTH, 1);
                CheckFields();
                mesiac.setText(sMesiac);
                nadrabacky.setText(sNadrabacky);
                rok.setText(Integer.toString(cal.get(Calendar.YEAR)));
                Refresh();
                prev.setAlpha(1f);
                handler.reset();
            }
        });
        prev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(cal.get(Calendar.MONTH) == 0 && cal.get(Calendar.YEAR) == 2017) {
                }else{
                    cal.add(Calendar.MONTH, -1);
                    CheckFields();
                    mesiac.setText(sMesiac);
                    nadrabacky.setText(sNadrabacky);
                    rok.setText(Integer.toString(cal.get(Calendar.YEAR)));
                    Refresh();
                    if (cal.get(Calendar.MONTH) == 0 && cal.get(Calendar.YEAR) == 2017){
                        prev.setAlpha(0.5f);
                    }
                    handler.reset();
                }
            }
        });
    }
    public class Cell{
        public String smena;
        public String date;
        public boolean notMonth;
        public int isWeekend;
        public boolean isToday;
        public boolean isSviatok;
        public int type;
        public String text;
        public String name;
        public boolean selected;
        public String nameLong;

        public Cell(String smena, String date, boolean notMonth, int isWeekend, boolean isToday, boolean isSviatok, String name, int type, String text, String nameLong){
            this.smena = smena;
            this.date = date;
            this.notMonth = notMonth;
            this.isWeekend = isWeekend;
            this.isToday = isToday;
            this.isSviatok = isSviatok;
            this.type = type;
            this.text = text;
            this.name = name;
            this.nameLong = nameLong;

        }
    }
    public class CellAdapter extends ArrayAdapter<Cell> {

        public CellAdapter(Context context, ArrayList<Cell> cells){
            super(context, 0,cells);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Cell cell = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_layout, parent, false);
            }
            TextView cellSmena = (TextView) convertView.findViewById(R.id.cellSmena);
            TextView cellDate = (TextView) convertView.findViewById(R.id.cellDate);
            ImageView type = (ImageView) convertView.findViewById(R.id.type_tag);
            cellSmena.setText(cell.smena);
            cellDate.setText(cell.date);
            if(cell.notMonth){
                convertView.setAlpha(0.2f);
            }else{
                convertView.setAlpha(1f);
            }
            switch(cell.isWeekend){
                case 0: convertView.setBackgroundResource(R.drawable.round_corners);break;
                case 1: convertView.setBackgroundResource(R.drawable.weekend); break;
                case 2: convertView.setBackgroundResource(R.drawable.free_weekend); break;
                case 3: convertView.setBackgroundResource(R.drawable.free_day); break;
            }
            if(cell.isSviatok){
                convertView.setBackgroundResource(R.drawable.cell_sviatok);
            }
            if (cell.isToday){
                ((GradientDrawable)convertView.getBackground()).setStroke(7,Color.RED);
            }else{
                if(Objects.equals(cell.smena, "")) {
                    ((GradientDrawable) convertView.getBackground()).setStroke(5, Color.TRANSPARENT);
                }else{
                    ((GradientDrawable) convertView.getBackground()).setStroke(5, Color.BLACK);
                }
            }
            switch(cell.type){
                case 0: type.setVisibility(View.INVISIBLE);break;
                case 1: type.setImageResource(R.drawable.nadrabacka_15dp);type.setVisibility(View.VISIBLE);break;
                case 2: type.setImageResource(R.drawable.skolenie_15dp);type.setVisibility(View.VISIBLE);break;
                case 3: type.setImageResource(R.drawable.poznamka_15dp);type.setVisibility(View.VISIBLE);break;
            }
            if(cell.selected){
                ((GradientDrawable) convertView.getBackground()).setStroke(7, Color.BLUE, 12, 8);
            }
            return convertView;
        }
    }
    private void Refresh(){
        int cMonth = cal.get(Calendar.MONTH);
        int cYear = cal.get(Calendar.YEAR);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int den;
        if(cal.get(Calendar.DAY_OF_WEEK) == 1){
            den = 6;
        }else if(cal.get(Calendar.DAY_OF_WEEK) == 2){
            den = 0;
        }else {
            den = cal.get(Calendar.DAY_OF_WEEK) - 2;
        }
        cal.add(Calendar.DATE, -den);
        cellArray.clear();
        for(int i = 1; i < 43; i++){
            long days = TimeUnit.MILLISECONDS.toDays(cal.getTimeInMillis()-1483228800000L)+1;
            int pos = ((int)days - nSmena + 25) % 25;
            String smena;
            boolean isMonth = true;
            int isWeekend = 0;
            boolean isToday = false;
            boolean isSviatok = false;
            int type = 0;
            String text = "";
            Date date = cal.getTime();
            Date today = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String name = sdf.format(date);
            Log.i("date:",""+sdf.format(date));
            if (Objects.equals(smeny[pos],"V")){
                smena = "";
            }else{
                smena = smeny[pos];
            }
            if(cal.get(Calendar.MONTH) == cMonth){
                isMonth = false;
            }
            if(cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 7){
                if(Objects.equals(smeny[pos],"V")){
                    isWeekend = 2;
                }else{
                    isWeekend = 1;
                }
            }else if(Objects.equals(smeny[pos],"V")){
                isWeekend = 3;
            }
            if(name.equals(sdf.format(today))){
                isToday = true;
            }
            if(Arrays.asList(sviatky).contains(name.substring(4)) || Arrays.asList(velkanoc).contains(name)){
                isSviatok = true;
            }
            Cursor c = mydb.rawQuery("SELECT * FROM Poznamky WHERE date='"+name+"'",null);
            if(c.moveToFirst())
            {
                type = Integer.parseInt(c.getString(1));
                text = c.getString(2);
            }
            c.close();
            String nameLong = Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) + " " + sMesiac + " " + Integer.toString(cal.get(Calendar.YEAR));
            Cell a = new Cell(smena, Integer.toString(cal.get(Calendar.DAY_OF_MONTH)), isMonth, isWeekend, isToday, isSviatok, name, type, text, nameLong);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            cellArray.add(a);
        }
        cal.set(Calendar.MONTH, cMonth);
        cal.set(Calendar.YEAR, cYear);
    }
    private void CheckFields(){
        switch(cal.get(Calendar.MONTH)){
            case 0: sMesiac = "Január"; break;
            case 1: sMesiac = "Február"; break;
            case 2: sMesiac = "Marec"; break;
            case 3: sMesiac = "Apríl"; break;
            case 4: sMesiac = "Máj"; break;
            case 5: sMesiac = "Jún"; break;
            case 6: sMesiac = "Júl"; break;
            case 7: sMesiac = "August"; break;
            case 8: sMesiac = "September"; break;
            case 9: sMesiac = "Október"; break;
            case 10: sMesiac = "November"; break;
            case 11: sMesiac = "December"; break;
        }
        int year = cal.get(Calendar.YEAR) - 2017;
        if(year > 1){
            sNadrabacky = "?";
        }else{
            sNadrabacky = "Vložené : " + nadrabacky[year][smena][cal.get(Calendar.MONTH)];
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        mEditor.putInt("smena",smena);
        mEditor.commit();
    }
    public class Handler{
        Cell newCell;
        Cell lastCell;
        Button edit;
        CellAdapter adapter;
        Handler handler = this;
        public Handler(Button edit, final CellAdapter adapter){
            this.edit = edit;
            this.adapter = adapter;
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(newCell != null) {
                        if (newCell.type == 0) {
                            openEdit();
                        } else {
                            FragmentShow fragment = new FragmentShow();
                            fragment.send(newCell,handler);
                            showEditFrag(fragment);
                        }
                    }
                }
            });
        }
        public void selected(int pos){
            if(!cellArray.get(pos).notMonth) {
                newCell = cellArray.get(pos);
                if(newCell != lastCell) {
                    newCell.selected = true;
                    if (lastCell != null) {
                        lastCell.selected = false;
                    }
                    lastCell = newCell;
                    adapter.notifyDataSetChanged();
                    if (newCell.type == 0) {
                        edit.setBackgroundResource(R.drawable.handler_button_add);
                    } else {
                        edit.setBackgroundResource(R.drawable.handler_button_show);
                    }
                }else{
                    reset();
                }
            }
        }
        public void reset(){
            if(newCell != null) {
                newCell.selected = false;
                newCell = null;
            }
            if(lastCell != null){
                lastCell.selected = false;
                lastCell = null;
            }
            edit.setBackgroundResource(R.drawable.handler_button_blank);
            adapter.notifyDataSetChanged();
        }

        public void openEdit() {
            CustomFragment fragment = new CustomFragment();
            fragment.send(newCell,handler);
            fragment.show(getFragmentManager(), "123");
        }
        public void delete(){
            mydb.execSQL("DELETE FROM Poznamky WHERE Date ='"+ newCell.name+"'");
            reset();
        }
        public void save(){
            mydb.execSQL("DELETE FROM Poznamky WHERE Date ='"+ newCell.name+"'");
            mydb.execSQL("INSERT INTO Poznamky (Date, Type, Pozn) VALUES ('"+newCell.name+"',"+newCell.type+",'"+newCell.text+"')");
            reset();
        }
    }
    public void showEditFrag(FragmentShow fragment){
        fragment.show(getFragmentManager(), "123");
    }
}
