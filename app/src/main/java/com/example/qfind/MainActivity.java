package com.example.qfind;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.util.ArrayList;
import java.util.HashMap;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    EditText proeditText;

    TextView extractedTV;

    Button extractPDFBtn;
    Button search_btn;
    Button add_pdf_button;
    Button seepdf;
    Button pro_search_btn;

    String redText = "";
    String paths_list="";
    String the_search_text;
    ImageView right_res;
    ImageView left_res;

    ArrayList<String> path = new ArrayList<>();
    ArrayList<Integer> indexes;
    Integer ResultIndex =0;

    HashMap<String, Integer> hashMap;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        extractedTV = findViewById(R.id.idPDFTV);
        extractPDFBtn = findViewById(R.id.idBtnExtract);

        add_pdf_button = findViewById(R.id.add_pdf);
        editText = findViewById(R.id.the_q_bar);
        //proeditText = findViewById(R.id.the_pro_search);
        search_btn = findViewById(R.id.search);
        seepdf = findViewById(R.id.showpdf);
        pro_search_btn = findViewById(R.id.pro_search);

        right_res =findViewById(R.id.right_result);
        left_res =findViewById(R.id.left_result);
        right_res.setVisibility(View.GONE);
        left_res.setVisibility(View.GONE);

        DataHandler handler = new DataHandler(this);
        SQLiteDatabase db = handler.getReadableDatabase();

        DataHandler2 dataHandler2 = new DataHandler2(MainActivity.this);
        SQLiteDatabase word_database = dataHandler2.getWritableDatabase();

        right_res.setClickable(true);
        left_res.setClickable(true);
        right_res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ResultIndex<indexes.size()){
                    ResultIndex++;
Log.e(TAG,"right");
searchTheText(the_search_text,ResultIndex);
Log.e(TAG, "searching doen");
                }
            }
        });

        left_res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ResultIndex>0){
                    ResultIndex--;
                    Log.e(TAG,"left");
                    searchTheText(the_search_text,ResultIndex);
                    Log.e(TAG, "searching doen");
                }
            }
        });


        pro_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String st= proeditText.getText().toString();
                proeditText.getText().clear();


                String stArray[] = st.split(" ");

                transfer_the_databasePath_hashmap(word_database);

                ArrayList<Integer> thenum = new ArrayList<>();

                boolean bi[] = new boolean[stArray.length];

                for (int i=0; i<stArray.length; i++){
                    bi[i]=tellIfItsThere(stArray[i]);
                    thenum.add(i);
                }

                String[] ToBeSearchedArray =new String[stArray.length];

                for (int k=0; k<stArray.length; k++){
                    if(tellIfItsThere(stArray[k])){ToBeSearchedArray[k]=stArray[k];}
                }
            }
        });

        ;
        extractPDFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "The task started now");


                transfer_the_databasePath_toArrayList(path, db);
                String strv=readText();

                extractedTV.setText(strv );
                Log.e(TAG, "The reading and putting task completed now");
            }
        });

        ArrayList<String> paths=   transfer_the_databasePath_toArrayList(path, db);
        paths_list="";
        for(int i=0; i< paths.size(); i++){
            paths_list = paths_list + paths.get(i);
        }
        seepdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractedTV.setText(" ");




                extractedTV.setText(paths_list);

            }
        });


        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                the_search_text = editText.getText().toString();
                editText.getText().clear();
                Log.e(TAG, "loging doen");
                transfer_the_databasePath_toArrayList(path, db);
                Log.e(TAG, "database doen");
                readText();
                Log.e(TAG, "reading doen");

                searchTheText(the_search_text,ResultIndex);
                Log.e(TAG, "indexOf initiated");


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

    //printing the saved pdfs and in process converting the elements of the database into Arraylist
    private ArrayList<String> transfer_the_databasePath_toArrayList(ArrayList<String> path, SQLiteDatabase db) {
        Cursor cr = db.rawQuery("SELECT PATH FROM FILE_PATHS", new String[]{});


        if(cr != null){
            if(cr.moveToFirst()) {
                int i=1;
                do {

                    path.add(cr.getString(0));


                }while (cr.moveToNext());
                cr.close();
            }
        }
        return path;
    }

    //put the element of the database to the hashmap
    private void transfer_the_databasePath_hashmap(SQLiteDatabase db) {
        hashMap = new HashMap<>();
        String str ="";
        Cursor cr = db.rawQuery("SELECT WORD FROM NON_NOUNS", new String[]{});

        if(cr != null){
            if(cr.moveToFirst()) {
                int i=1;
                do {
                    hashMap.put(cr.getString(0), 1);

                }

                while (cr.moveToNext());
                cr.close();
            }

        }
    }

    private boolean tellIfItsThere(String str){

        return hashMap.containsKey(str);

    }

    //code to open the pdf add activity
    private void StartAddActivity() {
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);

    }

    //code to read the saved pdf
    private String readText(){
        for(int i=0; i< path.size();i++) {
            try {


                //the the pdf for add path
                PdfReader reader = new PdfReader(path.get(i).toString());

                int n = reader.getNumberOfPages();

                for (int k = 0; k < n; k++) {
                    redText = redText + PdfTextExtractor.getTextFromPage(reader, k + 1).trim() + "\n\n\n";

                }

            } catch (Exception e) {
                extractedTV.setText("Error found is : \n" + e);
            }
        }
        return redText;
    }

    //it search the text in the pdf text
    public void searchTheText(String str, Integer Result_Index) {

         indexes = new ArrayList<Integer>();
        int totalChr = redText.length();




        for (int index = redText.indexOf(str);
             index >= 0;
             index = redText.indexOf(str, index + 1)) {
            indexes.add(index);
        }


        right_res.setVisibility(View.VISIBLE);
        left_res.setVisibility(View.VISIBLE);

            if (indexes.get(Result_Index) > 0) {
                if (totalChr > 2000) {
                    if (indexes.get(Result_Index) > 1000) {


                        String result = redText.substring(indexes.get(Result_Index) - 1000, indexes.get(Result_Index)) + " ||@@@|| "
                                + redText.substring(indexes.get(Result_Index), indexes.get(Result_Index) + str.length()) + " ||@@@|| "
                                + redText.substring(indexes.get(Result_Index) + str.length(), totalChr);

                        //formatted text
                        String Highlighted_text = "<span style='background-color:yellow'>" + str + "</span>";
                        //modify result
                        String highlighted_result = result.replaceAll(str, Highlighted_text);

                        extractedTV.setText(Html.fromHtml(highlighted_result, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        extractedTV.setText(redText.substring(0, indexes.get(Result_Index) + 1000));
                    }
                }
                else extractedTV.setText(redText.substring(0, totalChr));
            }
            else extractedTV.setText("Nothing Similar Found");


    }

}


