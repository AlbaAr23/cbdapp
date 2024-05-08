package com.example.cbdapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Inicializar el ListView
        listView = findViewById(R.id.listView);

        // Crear un array de datos de ejemplo
        String[] datos = {"Elemento 1", "Elemento 2", "Elemento 3", "Elemento 4", "Elemento 5"};

        // Crear un ArrayAdapter para el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);

        // Establecer el adaptador en el ListView
        listView.setAdapter(adapter);

    }



    }
