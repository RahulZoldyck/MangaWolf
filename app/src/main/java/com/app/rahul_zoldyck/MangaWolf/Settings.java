package com.app.rahul_zoldyck.MangaWolf;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

public class Settings extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        NumberPicker nm=(NumberPicker)findViewById(R.id.numberPicker);
        nm.setMinValue(1);
        nm.setMaxValue(48);
        nm.setOnValueChangedListener(
                new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        SharedPreferences.Editor pref=getSharedPreferences(OpenerActivity.SHARETAG+"min", Context.MODE_PRIVATE).edit();
                        pref.putInt("time", newVal);
                        pref.apply();
                        Toast.makeText(Settings.this,"Success fully updated the preferred update time",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        Switch sw=(Switch)findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            SharedPreferences.Editor pref=getSharedPreferences(OpenerActivity.SHARETAG+"download", Context.MODE_PRIVATE).edit();
                            pref.putBoolean("download", isChecked);
                            pref.apply();
                            Toast.makeText(Settings.this,"Success fully updated the preferred Download method",Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


}