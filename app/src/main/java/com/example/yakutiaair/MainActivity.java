package com.example.yakutiaair;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Document doc;
    private Document doc_weather;
    private String day;
    private Thread secThread;
    private Runnable runnable;
    private ListView listView;
    private CustomArrayAdapter adapter;
    private List<ListItemClass> arrayList;
    private String p = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        adapter = new CustomArrayAdapter(this, R.layout.list_item_1, arrayList, getLayoutInflater());
        listView.setAdapter(adapter);
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                Today();
                getWeb(day, p);
            }
        };
        secThread = new Thread(runnable);
        secThread.start();
    }

    private void Today() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);

        String m1, d1;

        int d = cal.get(Calendar.DAY_OF_MONTH);
        if (d < 10) {
            d1 = "0" + d;
        } else {
            d1 = "" + d;
        }

        int m = cal.get(Calendar.MONTH) + 1;
        if (m < 10) {
            m1 = "0" + m;
        } else {
            m1 = "" + m;
        }

        int y = cal.get(Calendar.YEAR);
        day = y + "-" + m1 + "-" + d1;
    }

    private void Yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        String m1, d1;

        int d = cal.get(Calendar.DAY_OF_MONTH);
        if (d < 10) {
            d1 = "0" + d;
        } else {
            d1 = "" + d;
        }

        int m = cal.get(Calendar.MONTH) + 1;
        if (m < 10) {
            m1 = "0" + m;
        } else {
            m1 = "" + m;
        }

        int y = cal.get(Calendar.YEAR);
        day = y + "-" + m1 + "-" + d1;
    }

    private void Tomorrow() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, +1);

        String m1, d1;

        int d = cal.get(Calendar.DAY_OF_MONTH);
        if (d < 10) {
            d1 = "0" + d;
        } else {
            d1 = "" + d;
        }

        int m = cal.get(Calendar.MONTH) + 1;
        if (m < 10) {
            m1 = "0" + m;
        } else {
            m1 = "" + m;
        }

        int y = cal.get(Calendar.YEAR);
        day = y + "-" + m1 + "-" + d1;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private  void getWeb(String d,String p)
    {
        try{
            doc_weather = Jsoup.connect("https://yandex.ru/pogoda/yakutsk?lat=62.028103&lon=129.732663").get();
            doc = Jsoup.connect("https://nasamolete.net/raspisanie-samoletov-yakutsk/"+p+d+"/").get();
            Elements tables = doc.select("div[class=aeroplane-row]");
            Elements weather = doc_weather.select("div[class=temp fact__temp fact__temp_size_s]");
            Elements weather_info = doc_weather.select("div[class=link__condition day-anchor i-bem]");
            Elements weather_wind = doc_weather.select("span[class=wind-speed]");
            Log.d("MyLog","Table: " + doc.baseUri());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    TextView pogoda = findViewById(R.id.textView9);
                    pogoda.setText("Погода в Якутске:"+weather.get(0).text()+" "+weather_info.get(0).text()+" "+weather_wind.get(0).text()+"м/с");
                }
            });

            for(int i = 0;i < tables.size();i++)
            {
                ListItemClass items = new ListItemClass();
                if(p=="")
                    items.setData_1(tables.get(i).children().get(0).children().get(1).children().get(2).text().substring(6));

                else if(p=="prilet/")
                    items.setData_1(tables.get(i).children().get(0).children().get(1).children().get(0).text().substring(6));

                items.setData_2(tables.get(i).children().get(0).children().get(0).children().get(0).text());
                arrayList.add(items);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void button_yesterday_click(View view) {
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                Yesterday();
                getWeb(day,p);
            }
        };
        secThread = new Thread(runnable);
        secThread.start();
    }
    public void button_today_click(View view) {
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                Today();
                getWeb(day,p);
            }
        };
        secThread = new Thread(runnable);
        secThread.start();
    }

    public void button_tomorrow_click(View view) {
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                Tomorrow();
                getWeb(day,p);
            }
        };
        secThread = new Thread(runnable);
        secThread.start();
    }

    public void button_prilet_click(View view) {
        p = "prilet/";
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                getWeb(day,p);
            }
        };
        secThread = new Thread(runnable);
        secThread.start();
    }

    public void button_vilet_click(View view) {
        p = "";
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                getWeb(day,p);
            }
        };
        secThread = new Thread(runnable);
        secThread.start();
    }
}