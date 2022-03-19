package com.example.qfind;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.odml.image.MlImage;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class MainActivity extends AppCompatActivity {

    private final int STORAGE_PERMISSION_CODE = 1;
    private  static final int request_camera_code=1;

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
    ImageView image_to_text;

    ArrayList<Integer> theFinalIndexes;
    ArrayList<Integer> indexes;
    Integer ResultIndex =0;
    Integer ResultIndex2 =0;

    HashMap<String, Integer> hashMap;
    Integer image_token=0;
String theCameraCapturedResult = "";
    ActivityResultLauncher<Intent> launcher;


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.activity_main);

        extractedTV = findViewById(R.id.idPDFTV);
        seeAllTxtBtn = findViewById(R.id.idBtnExtract);
        image_to_text = findViewById(R.id.image_to_text);
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

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestStoragePermission();
        }


        DataHandler handler = new DataHandler(this);
        SQLiteDatabase db = handler.getReadableDatabase();

        //text Recognition from camera part start
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);


        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA}, request_camera_code);
        }
        launcher  = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK && result.getData()  != null){
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    InputImage image = InputImage.fromBitmap(bitmap, 0);

//                    // [START run_detector]
        Task<Text> resultee =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {


                                theCameraCapturedResult = visionText.getText();

                                extractedTV.setText(theCameraCapturedResult);
                                editText.setText(theCameraCapturedResult);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Something went wrong!!", Toast.LENGTH_LONG).show();
                                    }
                                });
                }
            }
        });
        image_to_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                launcher.launch(camera);


            }
        });

        //text recognition form camera ends


        DataHandler2 dataHandler2 = new DataHandler2(this);
        SQLiteDatabase word_database = dataHandler2.getWritableDatabase();


        right_res.setClickable(true);
        left_res.setClickable(true);
        piro_right_res.setClickable(true);
        piro_left_res.setClickable(true);

        piro_right_res.setOnClickListener(view -> {
            if(ResultIndex2< (theFinalIndexes.size())){
            ResultIndex2++;

            searchTextProStyle(piro_search_text, ResultIndex2);
            }
        });

        piro_left_res.setOnClickListener(view -> {
            if(ResultIndex2>0){
                ResultIndex2--;

                searchTextProStyle(piro_search_text, ResultIndex2);
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

            piro_right_res.setVisibility(View.GONE);
            piro_left_res.setVisibility(View.GONE);


            redText = readText(transfer_the_databasePath_toArrayList(db));

            the_search_text = editText.getText().toString();

            editText.getText().clear();

            searchTheText(the_search_text,ResultIndex );

        });

        pro_search_btn.setOnClickListener(view -> {


            right_res.setVisibility(View.GONE);
            left_res.setVisibility(View.GONE);

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

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        getMenuInflater().inflate(R.menu.actionbar1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.copy_txt:
                ClipboardManager clipboardManager = (ClipboardManager)  getSystemService(Context.CLIPBOARD_SERVICE);
                if(extractedTV.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this,"nothing to copied", Toast.LENGTH_SHORT).show(); }
                    else {
                    ClipData clipData = ClipData.newPlainText("textview txt", extractedTV.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(MainActivity.this, "Copied", Toast.LENGTH_SHORT).show();
                }
        }

        return super.onOptionsItemSelected(item);
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

        theFinalIndexes= new ArrayList<>();
        for(int k=1; k<=theNUmvber.size()/2; k++){
            theFinalIndexes.add(theNUmvber.get(2*k-1));
        }


        piro_right_res.setVisibility(View.VISIBLE);
        piro_left_res.setVisibility(View.VISIBLE);

        StringBuilder stringBuilder =  new StringBuilder();
        int theIndex = 0;


        if(ResultIndex2 >=0 && ResultIndex2<theFinalIndexes.size() ){
              theIndex = theFinalIndexes.get(ResultIndex2);
        }


        if(theIndex<100){
            for(int i =0; i<200; i++){
                stringBuilder.append(" ").append(allTheTxtInArray[i]).append(" ");
            }
        }else {

            if(theIndex+100>allTheTxtInArray.length){

                for(int k= theIndex-100; k<allTheTxtInArray.length; k++){

                    stringBuilder.append(" ").append(allTheTxtInArray[k]).append(" ");

                }

            }

            else {
                    for (int k=theIndex-100; k<theIndex+100; k++){
                        stringBuilder.append(" ").append(allTheTxtInArray[k]).append(" ");
                    }
            }
        }



        //formatted text
        String Highlighted_text = "<span style='background-color:yellow'>" + allTheTxtInArray[ResultIndex2] + "</span>";
        //modify result
        String highlighted_result = stringBuilder.toString().replaceAll(allTheTxtInArray[ResultIndex2], Highlighted_text);
        extractedTV.setText(Html.fromHtml(highlighted_result, Html.FROM_HTML_MODE_LEGACY));
    }
}


