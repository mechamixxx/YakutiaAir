package com.example.yakutiaair;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Document doc;
    private Thread secThread;
    private Runnable runnable;
    private ListView listView;
    private CustomArrayAdapter adapter;
    private List<ListItemClass> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init()
    {
        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        adapter = new CustomArrayAdapter(this,R.layout.list_item_1,arrayList,getLayoutInflater());
        listView.setAdapter(adapter);
        runnable = new Runnable(){
            @Override
            public void run(){
                getWeb();
            }
        };
        secThread = new Thread(runnable);
        secThread.start();
    }
    private  void getWeb()
    {
        try{
            //doc = Jsoup.connect("https://aviakompaniya.info/raspisanie/nordwind-airlines").post();
            /*doc = Jsoup.connect("https://www.aviasales.ru/airlines/yakutia-air").data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .post();*/
            /*doc = Jsoup.connect("https://rasp.yandex.ru/station/9600424/").get();*/
            doc = Jsoup.connect("https://nasamolete.net/raspisanie-samoletov-yakutsk/2022-05-25/").get();
            Elements tables = doc.select("div[class=aeroplane-row]");
            Element table = tables.get(0);
            Elements blocks = table.children();
            Element block = blocks.get(0);
            Elements block_elements = block.children();

            //Log.d("MyLog","Table: " + tables.get(0).children().get(0).children().get(0).children().get(0).text());//Рейс и Маршрут
            Log.d("MyLog","Table: " + tables);//Время Вылета

            for(int i = 0;i < tables.size();i++)
            {
                ListItemClass items = new ListItemClass();
                items.setData_1(tables.get(i).children().get(0).children().get(1).children().get(2).text());
                items.setData_2(tables.get(i).children().get(0).children().get(0).children().get(0).text());
                //items.setData_3(table.children().get(i).child(2).text());
                //items.setData_4(table.children().get(i).child(3).text());
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
}