package com.example.braillekeyboard;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.braillekeyboard.BrailleMapping;

import com.example.braillekeyboard.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    private TextView mTextView;
    private ActivityMainBinding binding;

    // store the user input
    public static String answer = "";
    public static String KEY_NOT_FOUND = "KEY_NOT_FOUND";
    public static BrailleMapping brailleMapping = new BrailleMapping();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    public void onButtonConfirm(View view){
        String alphabet = checkAnswer(answer);
        if (!alphabet.equals(KEY_NOT_FOUND)){
            Log.v("Current Alphabet",alphabet);
        }
    }

    public void onButtonClick1(View view){
        answer+="1";
        Log.v("Init","clicked1");
    }
    public void onButtonClick2(View view){
        Log.v("Init","clicked2");
        answer+="2";
    }
    public void onButtonClick3(View view){
        Log.v("Init","clicked3");
        answer+="3";
    }
    public void onButtonClick4(View view){
        Log.v("Init","clicked4");
        answer+="4";
    }
    public void onButtonClick5(View view){
        Log.v("Init","clicked5");
        answer+="5";
    }
    public void onButtonClick6(View view){
        Log.v("Init","clicked6");
        answer+="6";
    }

    public static String checkAnswer(String ans){
        String currentAlphabet = brailleMapping.getAlphabet(answer);
        if (currentAlphabet.equals(KEY_NOT_FOUND)){
            return KEY_NOT_FOUND;
        }
        else{
            answer = "";
            return currentAlphabet;
        }
    }

}