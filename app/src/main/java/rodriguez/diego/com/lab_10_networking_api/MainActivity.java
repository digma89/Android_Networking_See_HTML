package rodriguez.diego.com.lab_10_networking_api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //READ HTML
    public void clickButton(View view) {
        try {
            long currentThread = Thread.currentThread().getId();
            System.out.println("I am in clickButton() with thread : " + currentThread);

            //Check connectivity to network
            ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            boolean isNetworkAvail = ni.isAvailable();

            if (isNetworkAvail) {
                if (view.getId() == R.id.readhtml) {
                    EditText editText = (EditText) findViewById(R.id.urlString);
                    String url = editText.getText().toString();
                    new HttpAsyncTask().execute(new URL(url));
                } if (view.getId() == R.id.weather) {
                    String url = "http://api.openweathermap.org/data/2.5/weather?q=";
                    EditText editText = (EditText) findViewById(R.id.urlString);
                    url+= editText.getText().toString();
                    url+="&units=metric&appid=0563c772e97be8ac153b7ec3ee1b9cf7";
                    new JsonAsyncTask().execute(new URL(url));
                }
            } else {
                TextView textView = (TextView) findViewById(R.id.result);
                textView.setText("Not connected to any network");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class HttpAsyncTask extends AsyncTask<URL, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            long currentThread = Thread.currentThread().getId();
            System.out.println("I am in HttpAsyncTask.onPreExecute() with (main) thread : " + currentThread);
        }

        @Override
        protected String doInBackground(URL... params) {

            //int id = android.os.Process.getThreadPriority(android.os.Process.myTid());
            long currentThread = Thread.currentThread().getId();
            System.out.println("I am in HttpAsyncTask.doInBackground() with thread : " + currentThread);

            StringBuilder result = new StringBuilder();
            try {
                //URL url = new URL((String) params[0]);
                System.out.println("before open conn .... ");
                HttpURLConnection httpConn = (HttpURLConnection) params[0].openConnection();
                System.out.println("after open conn .... ");

                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                System.out.println("after connect .... ");

                int response = httpConn.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK) {
                    InputStream in = httpConn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }

                System.out.println("end open conn .... ");

            } catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
            System.out.println("result is : " + result);
            return result.toString();
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            long currentThread = Thread.currentThread().getId();
            System.out.println("I am in HttpAsyncTask.onPostExecute() with (main) thread : " + currentThread);
            TextView textView = (TextView) findViewById(R.id.result);
            textView.setText(o);
        }
    }


    private class JsonAsyncTask extends AsyncTask<URL, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            long currentThread = Thread.currentThread().getId();
            System.out.println("I am in JsonAsyncTask.onPreExecute() with (main) thread : " + currentThread);
        }

        @Override
        protected String doInBackground(URL... params) {

            long currentThread = Thread.currentThread().getId();
            System.out.println("I am in JsonAsyncTask.doInBackground() with thread : " + currentThread);

            String temperature = "Not Available";

            StringBuilder result = new StringBuilder();
            try {
                System.out.println("before open conn .... ");
                HttpURLConnection httpConn = (HttpURLConnection) params[0].openConnection();
                System.out.println("after open conn .... ");

                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                System.out.println("after connect .... ");

                int response = httpConn.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK) {
                    InputStream in = httpConn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }

                JSONObject json = new JSONObject(result.toString());
                //JSONArray jArray = json.getJSONArray("temp");
                JSONObject main = json.getJSONObject("main");
                String temp = main.getString("temp");
                temperature = "Temperature: " + temp;

            } catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
            System.out.println("result is : " + result);
            return temperature;
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            long currentThread = Thread.currentThread().getId();
            System.out.println("I am in JsonAsyncTask.onPostExecute() with (main) thread : " + currentThread);
            TextView textView = (TextView) findViewById(R.id.result);
            textView.setText(o);
        }
    }
}
