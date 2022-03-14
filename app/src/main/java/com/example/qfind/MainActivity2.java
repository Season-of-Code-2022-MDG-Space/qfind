package com.example.qfind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {
EditText editText;
TextView textView;
Button save_path;
Button remove_path;
Button delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        editText = findViewById(R.id.edit_text);
        save_path = findViewById(R.id.save_path);
        remove_path = findViewById(R.id.remove_path);
        delete = findViewById(R.id.delete);
        DataHandler handler = new DataHandler(MainActivity2.this);
        SQLiteDatabase db = handler.getWritableDatabase();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.clearDatabase(db,"FILE_PATH");
                updateTheTasks();
            }
        });



        save_path.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {



        String str = editText.getText().toString();
        ContentValues values = new ContentValues();
        values.put("PATH", str);
        db.insert("FILE_PATHS",null, values);
        updateTheTasks();

    }
});
        remove_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String str = editText.getText().toString();

                ContentValues values = new ContentValues();
                values.put("PATH", "NO PDF IN THIS");

              //  db.delete("FILE_PATHS", "id = ?",new String[]{(str)});
                db.update("FILE_PATHS",values,"id =?",new String[]{str});
                updateTheTasks();

            }
        });

        }


    public void updateTheTasks(){

        DataHandler handler = new DataHandler(MainActivity2.this);
        SQLiteDatabase db = handler.getWritableDatabase();

        Cursor cr = db.rawQuery("SELECT PATH FROM FILE_PATHS", new String[]{});

        String string ="";

        if(cr != null){
            if(cr.moveToFirst()) {
                int i=1;
                do {

                    String str = cr.getString(0);
                    string = string+i+". "+str+"\n";
                    i++;
                }while (cr.moveToNext());
                cr.close();
            }

        }
        textView= findViewById(R.id.textView3);
        textView.setText(string);
    }

}
    //    textView = findViewById(R.id.textView2);



 /*       DataHandler handler = new DataHandler(this);
        SQLiteDatabase database = handler.getWritableDatabase();
        Cursor cr = database.rawQuery("SELECT PATH FROM FILE_PATHS", new String[]{});

        StringBuilder sb = new StringBuilder();
        if (cr != null) {
            cr.moveToFirst();
            do {

                sb.append(getString(0));


            } while (cr.moveToNext());

            Objects.requireNonNull(cr).close();
          //  textView.setText(sb.toString());


        }

            //      SharedPreferences sharedPreferences = getSharedPreferences("path", Context.MODE_PRIVATE);

            /*textView.setText(sb.toString());
            save_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //         save();
                }
            });*/





/*

       // show_path = findViewById(R.id.show_path);
        show_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //        load();
            }
        });
    }


}
*/


   /* public void save() {
        String text = mEditText.getText().toString();
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());

            mEditText.getText().clear();
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
                    Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void load() {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            mEditText.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
