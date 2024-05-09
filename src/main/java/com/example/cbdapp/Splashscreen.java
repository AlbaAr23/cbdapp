package com.example.cbdapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@SuppressLint("CustomSplashScreen")
public class Splashscreen extends Activity implements AdapterView.OnItemSelectedListener {
    private static int SPLASH_TIMER = 3000;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_splash);
//get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinner1);
//create a list of items for the spinner.
        String[] items = getLanguages();
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        /**/
    }
    private String[] getLanguages(){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://apertium.org/apy/listPairs")
                .method("GET", null)
                .build();
        ArrayList<String> idiomas=new ArrayList();
        try {
            Response response = client.newCall(request).execute();
            String bodyStr = response.body().string();
            Log.d("ADebugTag","successful"+bodyStr);
            JSONObject root = new JSONObject(bodyStr);
            JSONArray array= root.getJSONArray("responseData");
            for(int i=0;i<array.length();i++){
                String fuente=((JSONObject)(array.get(i))).get("sourceLanguage").toString();

                if(fuente.equals("spa")){
                    String target=((JSONObject)(array.get(i))).get("targetLanguage").toString();
                    if(target.length()<4){
                   idiomas.add(target );
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        for(String language:idiomas){
            Log.d("ADebugTag","spa-"+language);
        }
        return this.getnombres(idiomas.toArray(new String[0]));
    }
    private String[] getnombres(String[] codigos){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://apertium.org/apy/listLanguageNames?locale=es&languages="+String.join("+",codigos))
                .method("GET", null)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String bodyStr = response.body().string();
            Log.d("ADebugTag","successful"+bodyStr);
            JSONObject root = new JSONObject(bodyStr);
            for(int i=0;i<codigos.length;i++) {
                codigos[i]= root.get(codigos[i]).toString().toUpperCase();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return codigos;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splashscreen.this,WebActivity.class);
                startActivity(i);
                finish();
            }
        },SPLASH_TIMER);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}