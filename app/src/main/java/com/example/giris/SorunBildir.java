package com.example.giris;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SorunBildir extends AppCompatActivity {

    EditText konu,detay;
    Button gonder;
    SQLiteDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorun_bildir);
        konu = findViewById(R.id.konu);
        detay = findViewById(R.id.detay);
        gonder = findViewById(R.id.gonder);
        DB = this.openOrCreateDatabase("YemekTarifi",MODE_PRIVATE,null);
    }
    public void gonder(View view){
        String konuu = konu.getText().toString();
        String detayy= detay.getText().toString();

        try{
            DB = this.openOrCreateDatabase("YemekTarifi",MODE_PRIVATE,null);
            DB.execSQL("CREATE TABLE IF NOT EXISTS sikayet (id INTEGER PRIMARY KEY,konu VARCHAR,detay VARCHAR)");

            String sorgu = "INSERT INTO sikayet(konu,detay) VALUES (?,?)";
            SQLiteStatement sqLiteStatement = DB.compileStatement(sorgu);
            sqLiteStatement.bindString(1,konuu);
            sqLiteStatement.bindString(2,detayy);
            sqLiteStatement.execute();
            Toast.makeText(getApplicationContext(),"İsteğiniz Alınmıştır", Toast.LENGTH_LONG).show();

        }
        catch (Exception e)
        {e.printStackTrace();}

        Intent intent = new Intent(SorunBildir.this,Tarifler.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
