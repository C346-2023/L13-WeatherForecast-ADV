package com.myapplicationdev.l13weatherforecastadv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    ArrayList<Weather> alWeather;
    ArrayList<String> selectedCities;
    WeatherAdapter aa;
    Button btnAdd, btnRefresh;
    TextView tv;
    EditText etCity;
    ListView lv;
    AsyncHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) this.findViewById(R.id.button1);
        btnRefresh = (Button) this.findViewById(R.id.btnRefresh);
        etCity = (EditText) findViewById(R.id.etCity);
        tv = (TextView) findViewById(R.id.tv);
        lv = (ListView) this.findViewById(R.id.list);

        client = new AsyncHttpClient();

        alWeather = new ArrayList<Weather>();
        selectedCities = new ArrayList<String>();

        aa = new WeatherAdapter(this, R.layout.row, alWeather);
        lv.setAdapter(aa);

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String selected = etCity.getText().toString();
                selectedCities.add(selected);
                alWeather.add(new Weather(selected, "Please Refresh", -1));
                aa.notifyDataSetChanged();
            }
        });


        getCity();
        registerForContextMenu(lv);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alWeather.clear();
                aa.notifyDataSetChanged();

                for (int i = 0; i < selectedCities.size(); i++){
                    getWeatherInfo(selectedCities.get(i));
                }
            }
        });

    }

    private void getCity(){
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        boolean value = settings.getBoolean("hasrun", false);

        if (value == false){
            selectedCities.add("Bangkok");
            selectedCities.add("Tokyo");
            selectedCities.add("Canberra");
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("hasrun", true);
            editor.commit();
        } else {
            int num = settings.getInt("num", 0);
            for (int i = 0; i < num; i++){
                String city = settings.getString("city" + i, "Singapore");
                selectedCities.add(city);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        alWeather.clear();
        aa.notifyDataSetChanged();

        for (int i = 0; i < selectedCities.size(); i++){
            getWeatherInfo(selectedCities.get(i));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("num", selectedCities.size());
        editor.commit();
        for (int i = 0; i< selectedCities.size(); i++){
            String city = selectedCities.get(i);
            editor = settings.edit();
            editor.putString("city"+i, city);
            editor.commit();
        }
    }

    private void getWeatherInfo(String city) {
        client.get("https://wttr.in/" + city + "?format=j1", new JsonHttpResponseHandler() {
            String area;
            String forecast;
            int temperature;
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray jsonArrItems = response.getJSONArray("current_condition");
                    JSONObject firstObj = jsonArrItems.getJSONObject(0);
                    area = city;
                    forecast = firstObj.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
                    temperature = firstObj.getInt("temp_C");
                    Weather weather = new Weather(area, forecast, temperature);
                    alWeather.add(weather);

                }
                catch(JSONException e){
                }
                //POINT X – Code to display List View
                aa.notifyDataSetChanged();

            }//end onSuccess
        });
    }

}