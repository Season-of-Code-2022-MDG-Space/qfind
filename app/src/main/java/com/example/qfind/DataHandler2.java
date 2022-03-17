package com.example.qfind;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHandler2 extends SQLiteOpenHelper {
    private static final String db = "myDb";
    private static final int version = 1;


    public DataHandler2(Context context) {
        super(context, db, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_WORD_TABLE = "CREATE TABLE NON_NOUNS (_id INTEGER PRIMARY KEY AUTOINCREMENT, WORD TEXT)";
        sqLiteDatabase.execSQL(CREATE_WORD_TABLE);

        String verbs = "accept add admit alert allow amuse appreciate arrange attend attract avoid bathe battle belong bolt book borrow bow branch bury calculate camp care claim clip close coach collect compare concentrate contain crash cross curve cycle dam dance discover end enjoy entertain escape exercise fax help move park pass pause phone place point poke polish pray print puncture relax repair replace report return search serve settle shelter sign signal start stay steer stop store support surround telephone tour trade train transport travel trip try turn use visit wait walk wander want waste watch weigh need";
        String[] verbsArray = verbs.split(" ");

        String prepositions1 ="about above across after along among around at before behind below beside between beyond but by down during for from in inside near of on opposite outside over through to towards under up via with within without";
        String[] prepositions1Array = prepositions1.split(" ");

        String preposition2 = "near for with from into during including against among throughout towards upon of to in for on by about like through over before between after since under along following across behind beyond plus except up out around down off above near at";
        String[] preposition2Array = preposition2.split(" ");

        String pronoun = "i we they me you us them it his her ours theirs this that these those who whom which what whose some all any many few each another anything everything nothing none other others several something most enough little more both either neither one much such";
        String[] pronounArray = pronoun.split("");

        String determiner = "the a an this that these those my yours his her its our their a few a little much many any enough some most one all both half either neither each every other another such what rather quite";
        String determinerArray[] = determiner.split(" ");

        String conjunction = "but and that or as if when than because while where when wherever after so though since until whether before although nor like once unless now till for yet";
        String[] conjunctionArray = conjunction.split(" ");


        for (int i =0 ; i< determinerArray.length;i++) {
          //  insertData(s2, sqLiteDatabase);
            ContentValues values = new ContentValues();
            values.put("PATH", determinerArray[i]);
        sqLiteDatabase.insert("NON_NOUNS",null, values);
        }
        for (String s1 : pronounArray) {
            insertData(s1, sqLiteDatabase);
        }
        for (String element : verbsArray) {
            insertData(element, sqLiteDatabase);
        }
        for (String item : prepositions1Array) {
            insertData(item, sqLiteDatabase);
        }
        for (String value : preposition2Array) {
            insertData(value, sqLiteDatabase);
        }
        for (String s : conjunctionArray) {
            insertData(s, sqLiteDatabase);
        }

    }

    public void insertData(String word, SQLiteDatabase database) {

        ContentValues values = new ContentValues();
        values.put("WORD", word);
        database.insert("NON_NOUNS", null, values);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //DROP THE TABLE IF EXIST ALREADY
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "NON_NOUNS");
        //CREATE A NEW TABLE
        onCreate(sqLiteDatabase);

    }
}
