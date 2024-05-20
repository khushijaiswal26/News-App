package com.khushijaiswal.newsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> titlesList;

    public void onGetLatestNewsClick(View view){

        DownloadNewsTask downloadNewsTask=new DownloadNewsTask();

        downloadNewsTask.execute("https://newsdata.io/api/1/news?apikey=pub_4186614041ec28acc85f49a3050921cc6ff18&language=en");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titlesList=new ArrayList<>();
    }

    public class DownloadNewsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = " ";
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();

                while (data != -1) {
                    char ch = (char) data;
                    result += ch;
                    data = inputStreamReader.read();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);

            //Log.i("result",s);

            //JSON parsing
            try {
                JSONObject jsonObject = new JSONObject(s);
                String status = jsonObject.getString("status");
                String totalResults = jsonObject.getString("totalResults");
                String results = jsonObject.getString("results");

                Log.i("result status",status);
                Log.i("result total",totalResults );
                //Log.i("result results",results);

                JSONArray resultArray= new JSONArray(results);
                for(int i=0;i<resultArray.length();i++){
                    JSONObject jsonPart = resultArray.getJSONObject(i);

                    titlesList.add(jsonPart.getString("title"));

                    Log.i("result title",jsonPart.getString("title"));
                    Log.i("result image",jsonPart.getString("image_url"));
                }

                callListActivity();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void callListActivity() {
            Intent intent= new Intent(MainActivity.this,NewsListActivity.class);
            intent.putExtra("data",titlesList);
            startActivity(intent);
        }
    }
}