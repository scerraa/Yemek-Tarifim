package com.example.giris;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Tarifler extends AppCompatActivity {

    ListView listView;
    ArrayList<String> isimler;
    ArrayList<Integer> idler;
    ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarifler);

        listView = findViewById(R.id.liste);
        isimler = new ArrayList<>();
        idler = new ArrayList<>();

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,isimler);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Tarifler.this,TarifEkle.class);
                intent.putExtra("id",idler.get(position));
                intent.putExtra("info","old");
                startActivity(intent);
            }
        });

        getir();

    }

    public void getir(){

        try{
        SQLiteDatabase DB = this.openOrCreateDatabase("YemekTarifi",MODE_PRIVATE,null);
        Cursor cursor = DB.rawQuery("SELECT * FROM tarifler",null);
        int nameIndex = cursor.getColumnIndex("ad");
        int idIndex = cursor.getColumnIndex("id");
        while (cursor.moveToNext()){
            isimler.add(cursor.getString(nameIndex));
            idler.add(cursor.getInt(idIndex));

        }
        arrayAdapter.notifyDataSetChanged();
        cursor.close();
    }
    catch (Exception e){
            e.printStackTrace();
    }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_recipe,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== R.id.tarifekle){
            Intent intent = new Intent(Tarifler.this,TarifEkle.class);
            intent.putExtra("info","new");
            startActivity(intent);

        }
        else if(item.getItemId()==R.id.sorun){

            Intent intent = new Intent(Tarifler.this,SorunBildir.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}
