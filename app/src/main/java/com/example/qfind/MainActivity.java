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

import java.lang.reflect.Field;


public class MainActivity extends AppCompatActivity {
    EditText editText;

    TextView extractedTV;

    Button extractPDFBtn;
    Button search_btn;
    Button listBtn;
    Button add_pdf_button;
    String redText;
    String path;
    String the_search_text;

    ActivityResultLauncher<String> r_launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        extractedTV = findViewById(R.id.idPDFTV);
        extractPDFBtn = findViewById(R.id.idBtnExtract);
        listBtn = findViewById(R.id.listBtn);
        add_pdf_button=findViewById(R.id.add_pdf);
        editText= findViewById(R.id.the_q_bar);
        search_btn= findViewById(R.id.search);


        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readText();
                the_search_text = editText.getText().toString();

                searchTheText(the_search_text);


            }
        });

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
                readText();
            //    extractPDF();
            }
        });
    }


    private void readText(){
        try{


            //making the reader
            PdfReader reader2 = new PdfReader("res/raw/pdf.pdf");
            PdfReader reader = new PdfReader("/storage/emulated/0/Download/7366a7f1ec0912231deb90e1e086d0eb.pdf");

            int n = reader.getNumberOfPages();
            int n2 = reader2.getNumberOfPages();

            for (int i = 0; i < n2; i++) {
                redText  =redText+(PdfTextExtractor.getTextFromPage(reader2, i + 1).trim())+("\n\n");

            }
            Toast.makeText(MainActivity.this,"Reading Done",Toast.LENGTH_SHORT).show();


            for (int i = 0; i < n; i++) {
                if(PdfTextExtractor.getTextFromPage(reader, i + 1).trim().contains(the_search_text)){
                    redText=redText+(PdfTextExtractor.getTextFromPage(reader, i + 1).trim())+("\n");
                }
            }


        }catch(Exception e){
            extractedTV.setText("Error found is : \n" + e);
        }
    }

    public void searchTheText(String str){

        int i = redText.indexOf(str);
        int totalChr = redText.length();
    //  extractedTV.setText(Integer.toString(totalChr));


        if (totalChr>2000){
            if(i>1000) {


                String result = redText.substring(i - 1000,i)+" ||@@@|| "+redText.substring(i,i+str.length())+" ||@@@|| "
                        + redText.substring(i+str.length(),totalChr);

                //formatted text
                String Highlighted_text= "<span style='background-color:purple'>"+str+"</span>";
                //modify result
                String highlighted_result = result.replaceAll(str,Highlighted_text);

                extractedTV.setText(Html.fromHtml(highlighted_result,Html.FROM_HTML_MODE_LEGACY));
            }
            else {extractedTV.setText(redText.substring(0,i+1000));}
        }
        else extractedTV.setText(redText.substring(0,totalChr));




        /*
        if(i>500)
        {String ss= redText.substring(i-500,i+500);
           extractedTV.setText(ss);
        }
        else {
            String ss = redText.substring(0, i+500);
        }

//        else
//            extractedTV.setText("string not found");
*/
    }


    //method to searching and pasting the text from the PDF

    /*private void searchTheText(String the_search_text) {
        try {

            String pages_Containing_searched_text ="" ;

          //  PdfReader reader = new PdfReader("/storage/emulated/0/Download/7366a7f1ec0912231deb90e1e086d0eb.pdf");
            PdfReader reader2 = new PdfReader("res/raw/pdf.pdf");

         //   int n = reader.getNumberOfPages();
            int n2 = reader2.getNumberOfPages();

            for (int i = 0; i < n; i++) {
                if(PdfTextExtractor.getTextFromPage(reader, i + 1).trim().contains(the_search_text)){
                    pages_Containing_searched_text.append(PdfTextExtractor.getTextFromPage(reader, i + 1).trim()).append("\n");
                }
            }

            for (int i = 0; i < n2; i++) {

                if(PdfTextExtractor.getTextFromPage(reader2, i + 1).trim().contains(the_search_text)){
                    pages_Containing_searched_text = pages_Containing_searched_text + (PdfTextExtractor.getTextFromPage(reader2, i + 1).trim())+"\n";

                }
                else pages_Containing_searched_text = "found nothing!!!";
            }

            Toast.makeText(MainActivity.this,"Reading Done",Toast.LENGTH_SHORT).show();


            extractedTV.setText(pages_Containing_searched_text.toString());
        }
        catch (Exception e){
            extractedTV.setText("Error found is : \n" + e);
        }
    }*/






/*    private void extractPDF() {
            try {



                StringBuilder extractedText = new StringBuilder();


               //PdfReader reader = new PdfReader("/storage/emulated/0/Download/7366a7f1ec0912231deb90e1e086d0eb.pdf");
               PdfReader reader2 = new PdfReader("res/raw/pdf.pdf");



              //  int n = reader.getNumberOfPages();
                int n2 = reader2.getNumberOfPages();
                //firstPDF
                /*for (int i = 0; i < n; i++) {
                    extractedText.append(PdfTextExtractor.getTextFromPage(reader, i + 1).trim()).append("\n");

                }*/
                //secondPDF
             /*   for (int i = 0; i < n2; i++) {
                    extractedText.append(PdfTextExtractor.getTextFromPage(reader2, i + 1).trim()).append("\n");

                }

                Toast.makeText(MainActivity.this,"now",Toast.LENGTH_SHORT).show();
                boolean b;
                    //   b = extractedText.contains("")
              //  if()
                extractedTV.setText(extractedText.toString());


            //    reader.close();
                reader2.close();
            } catch (Exception e) {

                extractedTV.setText("Error found is : \n" + e);
            }
        }*/



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