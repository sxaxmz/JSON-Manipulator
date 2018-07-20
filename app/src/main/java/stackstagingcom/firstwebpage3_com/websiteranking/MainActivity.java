package stackstagingcom.firstwebpage3_com.websiteranking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import java.io.InputStream;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private RecyclerView myRV;
    private RecyclerView.Adapter myRVA;
    private RecyclerView.LayoutManager myRVLM;

    ArrayList<items> items;

    int itemCount = 0;

    private OkHttpClient client;

    private TextView dtStart;
    private TextView dtEnd;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatePickerDialog.OnDateSetListener dateSetListener2;

    String startDate;
    String endDate;

    Button btnFilter;
    Button btnAll;
    Button btnChart;

    TextView txtVisitors;
    TextView txtSiteName;
    TextView txtItemCount;

    String[] siteName;
    float[] visitors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient();

        items = new ArrayList<>();

        myRV = findViewById(R.id.RV);

        dtStart = findViewById(R.id.dtStart);
        dtEnd = findViewById(R.id.dtEnd);

        btnFilter = findViewById(R.id.btnFilter);
        btnAll = findViewById(R.id.btnAll);
        btnChart = findViewById(R.id.btnChart);

        txtVisitors = findViewById(R.id.textVisits);
        txtSiteName = findViewById(R.id.textSiteName);
        txtItemCount = findViewById(R.id.txtItemCount);

        siteName = new String[items.size()];
        visitors = new float[items.size()];

        dtStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_DeviceDefault_Dialog,
                         dateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                startDate = year + "/" + (month+1) + "/" + day;
                dtStart.setText(startDate);
            }
        };

        dtEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_DeviceDefault_Dialog,
                        dateSetListener2,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                endDate = year + "/" + (month+1) + "/" + day;
                dtEnd.setText(endDate);
            }
        };


        getJSON();



        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewNotSortedData();
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    filterJSON(startDate, endDate);
            }
        });

        txtVisitors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortTotalVisits();
            }
        });

        txtSiteName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortWebsitename();
            }
        });

        btnChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewChart();
            }
        });

        JSONtoArray();
    }

    public void getJSON () {
        String json;
        try {
            InputStream is = getAssets().open("websiteRanking.json");
            int size = is.available();
            byte [] buffer= new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer,"UTF-8");
            JSONArray  jsonArray = new JSONArray(json);

            for (int i=0; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ++itemCount;

                String siteId = jsonObject.getString("id_website");
                String siteName = jsonObject.getString("website_name");
                String visitDate = jsonObject.getString("visit_date");
                String visitors = jsonObject.getString("total_visits");


                items newItem = new items( siteName, siteId, visitDate, visitors);

                items.add(newItem);
            }

            myRV.setHasFixedSize(true);
            myRVLM = new LinearLayoutManager(MainActivity.this);
            myRVA = new MyRVA(items);

            myRV.setLayoutManager(myRVLM);
            myRV.setAdapter(myRVA);

            txtItemCount.setText(Integer.toString(itemCount));

        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Test function
    public void JSONtoArray () {

        String json;

        try {
            InputStream is = getAssets().open("websiteRanking.json");
            int size = is.available();
            byte [] buffer= new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer,"UTF-8");
            JSONArray  jsonArray = new JSONArray(json);


            for (int i=0; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                //Get the site names from JSON list
                //Add it to siteNames array
                String websiteName = jsonObject.getString("website_name");
                String totalVisitors = jsonObject.getString("total_visits");

                Log.d("MainActivity", " websiteName --> "+websiteName);
                Log.d("MainActivity", " totalVisits --> "+totalVisitors);

                //Fix the error
                //java.lang.ArrayIndexOutOfBoundsException: length=0; index=0
                siteName [i] = websiteName;
                visitors [i] = Integer.parseInt(totalVisitors);

                for (int j = 0; j<siteName.length;j++){
                    //Get the website names from siteName array
                    //Keep the unique names to be used as indicators in the array
                    Set<String> uniqueString = new HashSet<String>(Arrays.asList(siteName));
                    Log.d("MainActivity", siteName+" <-- comparedTo --> "+uniqueString);

                    //Get the number of visits of the duplicated values and store it
                    //In one unique value column/
                }


                if (jsonObject.getString("visit_date").equals(startDate) || jsonObject.getString("visit_date").equals(endDate) ) {
                    String siteId = jsonObject.getString("id_website");
                    String siteName = jsonObject.getString("website_name");
                    String visitDate = jsonObject.getString("visit_date");
                    String visitors = jsonObject.getString("total_visits");

                    items newItem = new items(siteName, siteId, visitDate, visitors);
                    items.add(newItem);
                    Log.d("MainActivity ->", siteName+" "+ siteId+" "+ visitDate+" "+ visitors);
                }
            }

            new chartActivity(siteName, visitors);

        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void filterJSON (String startDate, String endDate) {

        String json;
        try {
            InputStream is = getAssets().open("websiteRanking.json");
            int size = is.available();
            byte [] buffer= new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer,"UTF-8");
            JSONArray  jsonArray = new JSONArray(json);

            Log.d("MainActivity", startDate+" "+ endDate);

            itemCount = 0;
            for (int i=0; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ++itemCount;

                if (jsonObject.getString("visit_date").equals(startDate) || jsonObject.getString("visit_date").equals(endDate) ) {
                    String siteId = jsonObject.getString("id_website");
                    String siteName = jsonObject.getString("website_name");
                    String visitDate = jsonObject.getString("visit_date");
                    String visitors = jsonObject.getString("total_visits");

                    items newItem = new items(siteName, siteId, visitDate, visitors);
                    items.add(newItem);
                    Log.d("MainActivity ->", siteName+" "+ siteId+" "+ visitDate+" "+ visitors);
                }
            }

           myRVA.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, "Data for "+startDate+" and "+endDate, Toast.LENGTH_SHORT).show();

            txtItemCount.setText(Integer.toString(itemCount));

        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sortTotalVisits () {
        Collections.sort(items, new Comparator<stackstagingcom.firstwebpage3_com.websiteranking.items>() {
            @Override
            public int compare(items items, items t1) {
                return items.getVisiotrs().compareTo(t1.getVisiotrs());
            }
        });

        myRVA.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "The list has been sorted", Toast.LENGTH_SHORT).show();
    }

    public void sortWebsitename () {
        Collections.sort(items, new Comparator<stackstagingcom.firstwebpage3_com.websiteranking.items>() {
            @Override
            public int compare(items items, items t1) {
                return items.getSiteName().compareTo(t1.getSiteName());
            }
        });
        myRVA.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "The list has been sorted Alphabetically", Toast.LENGTH_SHORT).show();
    }

    public void viewNotSortedData (){
        Collections.sort(items, new Comparator<stackstagingcom.firstwebpage3_com.websiteranking.items>() {
            @Override
            public int compare(items items, items t1) {
                return items.getSiteId().compareTo(t1.getSiteId());
            }
        });
        myRVA.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "All data displayed", Toast.LENGTH_SHORT).show();
    }

    public void viewChart () {
        Log.d("MainActivity", "*** ViewChart ***");
        Intent intent = new Intent(MainActivity.this, chartActivity.class);
        startActivity(intent);
        //chartActivity(siteName, visitors);
    }

}
