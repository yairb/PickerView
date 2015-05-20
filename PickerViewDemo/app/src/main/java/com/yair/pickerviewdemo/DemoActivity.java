package com.yair.pickerviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.yair.pickerview.PickerView;

import java.util.ArrayList;


public class DemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);


        PickerView picker1 = (PickerView)findViewById(R.id.pickerView1);

        ArrayList<String> items = new ArrayList<>();
        for (int i = 1; i <= 100; i++){
            items.add("" + i);
        }

        picker1.setList(items);

//        PickerView picker2 = (PickerView)findViewById(R.id.pickerView2);
//
//        ArrayList<String> items2 = new ArrayList<>();
//        for (int i = 100; i <= 200; i++){
//            items2.add("" + i);
//        }
//
//        picker2.setList(items2);

        PickerView picker3 = (PickerView)findViewById(R.id.pickerView3);

        ArrayList<String> items3 = new ArrayList<>();
        for (int i = 200; i <= 300; i++){
            items3.add("" + i);
        }

        picker3.setList(items3);
    }

}
