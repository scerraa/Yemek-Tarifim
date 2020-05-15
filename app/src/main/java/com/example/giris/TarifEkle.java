package com.example.giris;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TarifEkle extends AppCompatActivity {
    Bitmap selectedimage;
    ImageView imageView;
    EditText yemek,malzeme,tarif,ad;
    Button kaydet;
    TextView masterr,infoo;
    SQLiteDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarif_ekle);
        DB = this.openOrCreateDatabase("YemekTarifi",MODE_PRIVATE,null);
        imageView = findViewById(R.id.resimekle);
        yemek = findViewById(R.id.yemek);
        malzeme = findViewById(R.id.malzeme);
        tarif= findViewById(R.id.tarif);
        ad = findViewById(R.id.ad);
        kaydet=findViewById(R.id.kaydet);
        masterr = findViewById(R.id.master);
        infoo = findViewById(R.id.info);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if(info.matches("new"))
        {
            yemek.setText("");
            malzeme.setText("");
            tarif.setText("");
            ad.setText("");
            kaydet.setVisibility(View.VISIBLE);
            masterr.setVisibility(View.INVISIBLE);
            infoo.setVisibility(View.INVISIBLE);
            Bitmap selectimage = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.web);
            imageView.setImageBitmap(selectimage);
        }
        else
        {
            int id=intent.getIntExtra("id",1);
            kaydet.setVisibility(View.INVISIBLE);
            ad.setVisibility(View.INVISIBLE);

            try{

                Cursor cursor = DB.rawQuery("SELECT * FROM tarifler WHERE id=?",new String[]{String.valueOf(id)});
                int nameIndex = cursor.getColumnIndex("ad");
                int malzemeIdx = cursor.getColumnIndex("malzeme");
                int taridIdx = cursor.getColumnIndex("tarif");
                int isimIdx = cursor.getColumnIndex("isim");
                int image = cursor.getColumnIndex("resim");
                while (cursor.moveToNext()){
                    yemek.setText(cursor.getString(nameIndex));
                    malzeme.setText(cursor.getString(malzemeIdx));
                    tarif.setText(cursor.getString(taridIdx));
                    malzeme.setText(cursor.getString(malzemeIdx));
                    masterr.setText(cursor.getString(isimIdx));
                    byte[] bytes = cursor.getBlob(image);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imageView.setImageBitmap(bitmap);


                }
                cursor.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }


        }
    }
    public  void resimsec(View view){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        else{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==2 && resultCode==RESULT_OK && data !=null){
            Uri image =data.getData();
            try {
                if(Build.VERSION.SDK_INT >= 28 ){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),image);
                    selectedimage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedimage);
                }
                else{
                selectedimage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
                imageView.setImageBitmap(selectedimage);}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void kaydet(View view){
        String yemekAdi = yemek.getText().toString();
        String malzemeler = malzeme.getText().toString();
        String tarifT = tarif.getText().toString();
        String isim = ad.getText().toString();
        Bitmap resim = kucult(selectedimage,300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resim.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] bytearray = outputStream.toByteArray();

        try{
            DB = this.openOrCreateDatabase("YemekTarifi",MODE_PRIVATE,null);
            DB.execSQL("CREATE TABLE IF NOT EXISTS tarifler (id INTEGER PRIMARY KEY,ad VARCHAR,malzeme VARCHAR,tarif VARCHAR,isim VARCHAR, resim BLOB )");

            String sorgu = "INSERT INTO tarifler(ad,malzeme,tarif,isim,resim) VALUES (?,?,?,?,?)";
            SQLiteStatement sqLiteStatement = DB.compileStatement(sorgu);
            sqLiteStatement.bindString(1,yemekAdi);
            sqLiteStatement.bindString(2,malzemeler);
            sqLiteStatement.bindString(3,tarifT);
            sqLiteStatement.bindString(4,isim);
            sqLiteStatement.bindBlob(5,bytearray);
            sqLiteStatement.execute();



        }
        catch (Exception e)
        {e.printStackTrace();}
        Intent intent = new Intent(TarifEkle.this,Tarifler.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        //finish();
    }
    public Bitmap kucult(Bitmap image, int maxSize){
        int width = image.getWidth();
        int height = image.getHeight();
        float bolum= (float)width/ (float)height;
        if(bolum>1)
        {
            width=maxSize;
            height = (int)(width / bolum);
        }
        else{
            height = maxSize;
            width = (int)(height * bolum);
        }
        return Bitmap.createScaledBitmap(image,width,height,true);
    }
}