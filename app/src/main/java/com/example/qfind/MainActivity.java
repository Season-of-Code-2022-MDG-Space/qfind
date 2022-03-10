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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.lang.reflect.Field;


public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button camera_button;
    Button select_file_button, addes_file_button;
    static final int REQUEST_IMAGE_CAPTURE=1;

    // creating variables for
    // button and text view.
    private Button extractPDFBtn;
    private TextView extractedTV;
    private Button listBtn;
    String path;
    private Button add_pdf_button;
    ActivityResultLauncher<String> r_launcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        extractedTV = findViewById(R.id.idPDFTV);
        extractPDFBtn = findViewById(R.id.idBtnExtract);
        listBtn = findViewById(R.id.listBtn);
        add_pdf_button=findViewById(R.id.add_pdf);

        r_launcher= registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                //  final String   path = result.getPath();
                path = uri.getPath();
                Toast.makeText(MainActivity.this, "File Selected: " + path, Toast.LENGTH_LONG).show();
            }
        });

        add_pdf_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r_launcher.launch("application/pdf");
            }
        });

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listRaw();
            }
        });

        extractPDFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                extractPDF();
            }
        });
    }

    private void extractPDF() {
            try {

                String extractedText = "";

               PdfReader reader = new PdfReader("/storage/emulated/0/Download/7366a7f1ec0912231deb90e1e086d0eb.pdf");
               PdfReader reader2 = new PdfReader("res/raw/pdf.pdf");



                int n = reader.getNumberOfPages();
                int n2 = reader2.getNumberOfPages();

                for (int i = 0; i < n; i++) {
                    extractedText = extractedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";

                }
                for (int i = 0; i < n2; i++) {
                    extractedText = extractedText + PdfTextExtractor.getTextFromPage(reader2, i + 1).trim() + "\n";

                }

                Toast.makeText(MainActivity.this,"now",Toast.LENGTH_SHORT).show();


                extractedTV.setText(extractedText);


                reader.close();
            } catch (Exception e) {

                extractedTV.setText("Error found is : \n" + e);
            }
        }



    //this code shows the list of pdfs in the res/raw folder
    //it will be removed in future

    public void listRaw() {
        String str= "";
        Field[] fields = R.raw.class.getFields();
        for(int count = 0; count < fields.length; count++) {

            str = str + "\n"+ fields[count].getName();
            extractedTV.setText(str);
        }
    }
}