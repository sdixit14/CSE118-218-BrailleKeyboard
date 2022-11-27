package com.example.braillekeyboard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.wear.input.WearableButtons;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.braillekeyboard.BrailleMapping;

import com.example.braillekeyboard.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
        Context context = getApplicationContext();
        CharSequence text = sentence;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onButtonSpace(View view){
        sentence+=" ";

        Context context = getApplicationContext();
        CharSequence text = sentence;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onButtonSentence(View view){
        Log.v("Current Sentence ", sentence);
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);

        final String URL = "https://balajimt.pythonanywhere.com/addnotepublic";
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", "elle_admin");
        params.put("licensekey", "SEUSSGEISEL");
        params.put("note", sentence);

        Context context = getApplicationContext();
        CharSequence text = "Sending: " + sentence;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        JsonObjectRequest request_json = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println(response);
                            Context context = getApplicationContext();
                            CharSequence text = response.getString("message");
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } catch (Exception e) {
                            Context context = getApplicationContext();
                            CharSequence text = e.getMessage();
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        // add the request object to the queue to be executed
        MyRequestQueue.add(request_json);
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