package com.example.lab1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends Activity{
    private List<Element> elements = new ArrayList();

    ListView elementsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        // начальная инициализация списка
        setInitialData();
        // получаем элемент ListView
        elementsList = (ListView) findViewById(R.id.elementsList);
        // создаем адаптер
        ElementAdapter elementAdapter = new ElementAdapter(this, R.layout.list_item, elements);
        // устанавливаем адаптер
        elementsList.setAdapter(elementAdapter);
        // слушатель выбора в списке
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                // получаем выбранный пункт
                Element selectedState = (Element)parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Был выбран пункт " + selectedState.getStr(),
                        Toast.LENGTH_SHORT).show();
            }
        };
        elementsList.setOnItemClickListener(itemListener);
    }

    private void setInitialData(){
        for(int i = 327190; i < 1000001; i++) elements.add(new Element(i));
    }
}
