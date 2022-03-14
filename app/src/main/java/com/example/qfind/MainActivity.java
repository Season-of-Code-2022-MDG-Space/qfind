package com.example.qfind;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    EditText editText;

    TextView extractedTV;

    Button extractPDFBtn;
    Button search_btn;
    Button add_pdf_button;
    Button seepdf;

    String redText="";
    ArrayList<String> path = new ArrayList<>();;
    String the_search_text;

    


    //ActivityResultLauncher<String> r_launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        extractedTV = findViewById(R.id.idPDFTV);
        extractPDFBtn = findViewById(R.id.idBtnExtract);

        add_pdf_button=findViewById(R.id.add_pdf);
        editText= findViewById(R.id.the_q_bar);
        search_btn= findViewById(R.id.search);
        seepdf =findViewById(R.id.showpdf);

        DataHandler handler = new DataHandler(this);
        SQLiteDatabase db = handler.getReadableDatabase();


        extractPDFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                transfer_the_databasePath_toArrayList(path, db);
                readText();
            }
        });


        seepdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                transfer_the_databasePath_toArrayList(path, db);


    }
});


        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                transfer_the_databasePath_toArrayList(path, db);
                readText();
                the_search_text = editText.getText().toString();

                searchTheText(the_search_text);


            }
        });


        add_pdf_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                StartAddActivity();

                //  r_launcher.launch("application/pdf");
            }
        });

    }

    private void transfer_the_databasePath_toArrayList(ArrayList<String> path, SQLiteDatabase db) {



        Cursor cr = db.rawQuery("SELECT PATH FROM FILE_PATHS", new String[]{});

        String string ="";

        if(cr != null){
            if(cr.moveToFirst()) {
                int i=1;
                do {

                    path.add(cr.getString(0));


                }while (cr.moveToNext());
                cr.close();
            }
            String paths_list="";
            for(int i=0; i< path.size(); i++){
                paths_list = paths_list + path.get(i);
            }

            extractedTV.setText(paths_list);
        }

    }




    //code to open the pdf add activity
    private void StartAddActivity() {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);

    }



//code to read the saved pdf
    private void readText(){
      for(int i=0; i< path.size();i++) {
          try {


              //the the pdf for add path
              PdfReader reader = new PdfReader(path.get(i).toString());

              int n = reader.getNumberOfPages();

              for (int k = 0; k < n; k++) {
                  redText = redText + PdfTextExtractor.getTextFromPage(reader, k + 1).trim() +"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+ "\n";

              }
              extractedTV.setText(redText);
          } catch (Exception e) {
              extractedTV.setText("Error found is : \n" + e);
          }
      }
      }








    public void searchTheText(String str) {


        ArrayList<Integer> indexes = new ArrayList<Integer>();
        int totalChr = redText.length();


        for (int index = redText.indexOf(str);
             index >= 0;
             index = redText.indexOf(str, index + 1)) {
            indexes.add(index);
        }


        for (int k = 0; k < indexes.size(); k++) {

            if (indexes.get(k) > 0) {
                if (totalChr > 2000) {
                    if (indexes.get(k) > 1000) {


                        String result = redText.substring(indexes.get(k) - 1000, indexes.get(k)) + " ||@@@|| " + redText.substring(indexes.get(k), indexes.get(k) + str.length()) + " ||@@@|| "
                                + redText.substring(indexes.get(k) + str.length(), totalChr);

                        //formatted text
                        String Highlighted_text = "<span style='background-color:yellow'>" + str + "</span>";
                        //modify result
                        String highlighted_result = result.replaceAll(str, Highlighted_text);

                        extractedTV.setText(Html.fromHtml(highlighted_result, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        extractedTV.setText(redText.substring(0, indexes.get(k) + 1000));
                    }
                } else extractedTV.setText(redText.substring(0, totalChr));
            } else extractedTV.setText("Nothing Similar Found");

        }
    }


    }


