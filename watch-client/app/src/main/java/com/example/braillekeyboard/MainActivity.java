package com.example.braillekeyboard;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.wear.input.WearableButtons;

import com.example.braillekeyboard.BrailleMapping;

import com.example.braillekeyboard.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {

    private TextView mTextView;
    private ActivityMainBinding binding;

    // store the user input
    public static String answer = "";
    public static String sentence = "";
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
        if (!alphabet.equals(KEY_NOT_FOUND)) {
            Log.v("Current Alphabet ", alphabet);
            sentence+=alphabet;
        }
    }

    public void onButtonSpace(View view){
        sentence+=" ";
    }
    
    public void onButtonSentence(View view){
        Log.v("Current Sentence ", sentence);

        // SEND POST REQUEST
        try {
            URL url = new URL("https://balajimt.pythonanywhere.com/addnotepublic");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json");

            String data = String.format("{\"username\":\"elle_admin\",\n\"licensekey\": \"SEUSSGEISEL\",\n \"note\": \"%s\"\n} ",sentence);
            Log.v("Data is ", data);

            byte[] out = data.getBytes(StandardCharsets.UTF_8);

            //some errors HERE
            OutputStream stream = http.getOutputStream();
            stream.write(out);

            System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
            http.disconnect();
        } catch(IOException ex){
            Log.v("Unsuccessful", "IO Exception");
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

//    @Override (Considered physical buttons but hard to test on the emulator
//// Activity
//    public boolean onKeyDown(int keyCode, KeyEvent event){
//        if (event.getRepeatCount() == 0) {
//            if (keyCode == KeyEvent.KEYCODE_STEM_1) {
//                // Do stuff
//                return true;
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}