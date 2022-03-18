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
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class MainActivity extends AppCompatActivity {

    EditText editText;

    TextView extractedTV;

    Button seeAllTxtBtn;
    Button search_btn;
    Button add_pdf_button;
    Button seepdf;
    Button pro_search_btn;

    String redText = "";
    String paths_list="";
    String the_search_text;
    String piro_search_text;
    ImageView right_res;
    ImageView left_res;
    ImageView piro_right_res;
    ImageView piro_left_res;


    ArrayList<Integer> indexes;
    Integer ResultIndex =0;
    Integer ResultIndex2 =0;

    HashMap<String, Integer> hashMap;
    Integer image_token=0;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        extractedTV = findViewById(R.id.idPDFTV);
      seeAllTxtBtn = findViewById(R.id.idBtnExtract);

        add_pdf_button = findViewById(R.id.add_pdf);
        editText = findViewById(R.id.the_q_bar);
        search_btn = findViewById(R.id.search);
        seepdf = findViewById(R.id.showpdf);
        pro_search_btn = findViewById(R.id.pro_search);

        right_res =findViewById(R.id.right_result);
        left_res =findViewById(R.id.left_result);
        right_res.setVisibility(View.GONE);
        left_res.setVisibility(View.GONE);

        piro_right_res =findViewById(R.id.piro_right_result);
        piro_left_res =findViewById(R.id.piro_left_result);
        piro_right_res.setVisibility(View.GONE);
        piro_left_res.setVisibility(View.GONE);

        DataHandler handler = new DataHandler(this);
        SQLiteDatabase db = handler.getReadableDatabase();


        DataHandler2 dataHandler2 = new DataHandler2(this);
        SQLiteDatabase word_database = dataHandler2.getWritableDatabase();


        right_res.setClickable(true);
        left_res.setClickable(true);
        piro_right_res.setClickable(true);
        piro_left_res.setClickable(true);

        piro_right_res.setOnClickListener(view -> {
            if(ResultIndex2< (indexes).size()){

            }
        });

        right_res.setOnClickListener(view -> {
            if(ResultIndex<indexes.size()){
                ResultIndex++;

                Log.e(TAG,"right");
               searchTheText(the_search_text,ResultIndex);
                Log.e(TAG, "searching doen");
            }
        });

        left_res.setOnClickListener(view -> {
            if(ResultIndex>0){
                ResultIndex--;
                Log.e(TAG,"left");
                searchTheText(the_search_text,ResultIndex);
                Log.e(TAG, "searching doen");
            }
        });
        //to search ctrl way
        search_btn.setOnClickListener(view -> {


            redText = readText(transfer_the_databasePath_toArrayList(db));

            the_search_text = editText.getText().toString();

            editText.getText().clear();

            searchTheText(the_search_text,ResultIndex );

        });

        pro_search_btn.setOnClickListener(view -> {

            redText =readText(transfer_the_databasePath_toArrayList(db));

            transfer_the_databasePath_hashmap(word_database);

            piro_search_text= editText.getText().toString();

            editText.getText().clear();

            searchTextProStyle(piro_search_text,ResultIndex2);

        });

        //done, no bugs
        //used to see all txt in the pdf
        seeAllTxtBtn.setOnClickListener(v -> {

            Log.e(TAG, "The task started now");
            String prose ="Processing...";
            extractedTV.setText(prose);
            extractedTV.setText(readText(transfer_the_databasePath_toArrayList(db)));

            Log.e(TAG, "The reading and putting task completed now");
        });


        //done, no bugs
        //used to see the list of all the saved pdfs
        seepdf.setOnClickListener(view -> {
            ArrayList<String> paths=   transfer_the_databasePath_toArrayList(db);
            paths_list="";

            for(int i=0; i< paths.size(); i++){

                String[] pathsWay =paths.get(i).split("/");

                paths_list = paths_list + pathsWay[pathsWay.length-1]+"\n\n\n\n";
            }

           extractedTV.setText(paths_list);

        });



        //to go to new activity
        add_pdf_button.setOnClickListener(view -> {

            StartAddActivity();

            //  r_launcher.launch("application/pdf");
        });



    }


    //printing the saved pdfs and in process converting the elements of the database into Arraylist
    private ArrayList<String> transfer_the_databasePath_toArrayList(SQLiteDatabase db) {
        ArrayList<String> path = new ArrayList<>();
        Cursor cr = db.rawQuery("SELECT PATH FROM FILE_PATHS", new String[]{});

        if(cr != null){
            if(cr.moveToFirst()) {
                do {

                    path.add(cr.getString(0));


                }while (cr.moveToNext());
                cr.close();
            }
        }
        extractedTV.setText(path.toString());
        return path;
    }

    //put the element of the database to the hashmap
    private void transfer_the_databasePath_hashmap(SQLiteDatabase db) {
        hashMap = new HashMap<>();
        Cursor cr = db.rawQuery("SELECT WORD FROM NON_NOUNS", new String[]{});
        Log.e(TAG,"databaese opened");
        if(cr != null){
            if(cr.moveToFirst()) {
                int i=1;
                do {
                    hashMap.put(cr.getString(0), 1);
//str = str +cr.getString(0);
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
    private String readText(ArrayList<String> path){
        String redText="";

        Log.e(TAG, "readtext called");
        for(int i=0; i< path.size();i++) {
            try {


                //the the pdf for add path
                PdfReader reader = new PdfReader(path.get(i));

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
        //when nothing is found

        if (indexes.size() == 0) {
            extractedTV.setText("Nothing found");
            return;
        }


        right_res.setVisibility(View.VISIBLE);
        left_res.setVisibility(View.VISIBLE);
        if (indexes.size() > Result_Index) {
            if (indexes.get(ResultIndex) > 0) {
                if (totalChr > 2000) {
                    if (indexes.get(Result_Index) > 1000) {


                        String resultee = redText.substring(indexes.get(Result_Index) - 1000, indexes.get(Result_Index)) + " ||@@@|| "
                                + redText.substring(indexes.get(Result_Index), indexes.get(Result_Index) + str.length()) + " ||@@@|| "
                                + redText.substring(indexes.get(Result_Index) + str.length(), totalChr);

                        //formatted text
                        String Highlighted_text = "<span style='background-color:yellow'>" + str + "</span>";
                        //modify result
                        String highlighted_result = resultee.replaceAll(str, Highlighted_text);

                        extractedTV.setText(Html.fromHtml(highlighted_result, Html.FROM_HTML_MODE_LEGACY));
                    } else extractedTV.setText(redText.substring(0, indexes.get(Result_Index) + 1000));

                } else extractedTV.setText(redText.substring(0, totalChr));
            }
        }else extractedTV.setText("Nothing else ahead");
    }

    public  void searchTextProStyle(String st, Integer Result_Index){

        String allTheTxtInArray[] = redText.split(" ");

        String  theResult = FuzzySearch.extractTop(st, Arrays.asList(allTheTxtInArray),6).toString();

        extractedTV.setText(theResult);


        theResult = theResult.replaceAll("[^0-9]", " ");    // return the the string in form "  213 1234 1234 32 123"
        ArrayList<Integer> theNUmvber = new ArrayList<>();
        for(int k=0 ; k<theResult.length(); k++){
            String stree = "";
            if ((int)theResult.charAt(k)!=32){
            for(int i=k; (int)theResult.charAt(i)!=32;i++){
                stree = stree + theResult.charAt(i);
                k++;
            }
            theNUmvber.add(Integer.parseInt(stree));
            }
        }
        right_res.setVisibility(View.VISIBLE);
        left_res.setVisibility(View.VISIBLE);




    }
}


