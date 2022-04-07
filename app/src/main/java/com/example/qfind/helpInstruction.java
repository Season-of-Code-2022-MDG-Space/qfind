package com.example.qfind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class helpInstruction extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_instruction);
        textView =findViewById(R.id.instruction_text);

        String instruction = "Hi,\n\nHere are some instruction to help you utilize the app\n\n1. Using camera icon you" +
                " can click photo and extract text (NOT VERY ACCURATE :-|)\n\n2.Using the COPY icon, you will be able to copy" +
                " anything that appears on the Scrollable screen" +
                "in your clip board.\n\n3. Using the ADD PDF button you will be lead to another screen where you can add the path of " +
                "pdf you want to perform operation on.\n\n4. Using SEE PDF button you can see all the PDF whose path you have added.\n\n" +
                "5. In general Path of a pdf looks like: \n/storage/emulated/0/Download/UNIT-VI-MOTIVATION-1.pdf"+"\n\n6. CTRL F" +
                " will search for exact word in the pdf and highlight if that word is present(better to serach word)\n\n" +
                "7. The SEARCH button will search for the for something similar you might have entered(Better for sentences), not always accurate" +
                "\n\nLastly don't forget to give permission to manage file in Storage permission\nYou can ignore the warning\n\n" +
                "GitHub Repo link : https://github.com/Season-of-Code-2022-MDG-Space/qfind/tree/branch1"+"\n\nExcel in exams!! \n;-)";
        textView.setText(instruction);

    }
}