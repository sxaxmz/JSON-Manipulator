package stackstagingcom.firstwebpage3_com.websiteranking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import java.io.InputStream;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.Locale;
import java.util.Set;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private RecyclerView myRV;
    private RecyclerView.Adapter myRVA;
    private RecyclerView.LayoutManager myRVLM;

    ArrayList<items> items;

    private OkHttpClient client;

    private TextView dtStart;
    private TextView dtEnd;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatePickerDialog.OnDateSetListener dateSetListener2;
    private DatePickerDialog.OnDateSetListener dateSetListener3;

    String startDate = "";
    String endDate = "";

    Button btnFilter, btnAll, btnChart, btnAdd;

    TextView txtVisitors, txtSiteName, txtItemCount, etVisitDate;

    String[] siteName;
    float[] visitors;

    ArrayList<String> allDates = new ArrayList();

    FloatingActionButton fab;

    EditText etSiteName, etVisitors;

    AlertDialog.Builder adb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);

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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }
        });

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
                startDate = year + "-" + (month+1) + "-" + day;
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
                endDate = year + "-" + (month+1) + "-" + day;
                dtEnd.setText(endDate);
            }
        };


        getJSON();



        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getJSON();
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startDate == "" || endDate == "") {
                    Toast.makeText(MainActivity.this, "Please select a date!", Toast.LENGTH_SHORT).show();
                } else {
                    filterJSON(startDate, endDate);
                }
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
                //need modification
                if ( (siteName != null) && (visitors != null) ) {
                    Toast.makeText(MainActivity.this, "Chart is not working at the moment!", Toast.LENGTH_SHORT).show();
                } else {
                    viewChart();
                }
            }
        });

        //JSONtoArray();

    }

    public void addData () {

        Log.d("MainActivity", "** addData function **");

        adb = new AlertDialog.Builder(MainActivity.this);
        View adbView = getLayoutInflater().inflate(R.layout.write_dialog, null);

        etSiteName = adbView.findViewById(R.id.etSiteName);
        etVisitDate = adbView.findViewById(R.id.etVisitDate);
        etVisitors = adbView.findViewById(R.id.etVisitors);
        btnAdd = adbView.findViewById(R.id.btnAdd);

        etVisitDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_DeviceDefault_Dialog,
                        dateSetListener3,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener3 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                startDate = year + "-" + (month+1) + "-" + day;
                etVisitDate.setText(startDate);
            }
        };

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etSiteName.getText().toString().isEmpty() && !etVisitDate.getText().toString().isEmpty() && !etVisitors.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "The Butotn Works!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        adb.setView(adbView);
        AlertDialog ad = adb.create();
        ad.show();
    }

    public void getJSON () {

        Log.d("MainActivity", "** getJSON function **");
        items.clear();
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

            txtItemCount.setText(String.valueOf(items.size()));
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

        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void filterJSON (String startDate, String endDate) {

        Log.d("MainActivity", "** filterJSON function **");
            String json;

            getDaysBetween(startDate, endDate);

            String eachDate;
            String checkData = "";

            try {
                InputStream is = getAssets().open("websiteRanking.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                json = new String(buffer, "UTF-8");
                JSONArray jsonArray = new JSONArray(json);

                items.clear();
                    for (int j = 0; j < allDates.size(); j++) {
                        eachDate = String.valueOf(allDates.get(j));

                        //Log.d("MainActivity", "allDates.get(j) result --> "+allDates.get(j));
                        //Log.d("MainActivity", "items from JSON --> " + items.get(j)+ " item --> "+ j);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                        if (jsonObject.getString("visit_date").equals(eachDate)) {
                            String siteId = jsonObject.getString("id_website");
                            String siteName = jsonObject.getString("website_name");
                            String visitDate = jsonObject.getString("visit_date");
                            String visitors = jsonObject.getString("total_visits");


                            items newItem = new items(siteName, siteId, visitDate, visitors);
                            items.add(newItem);
                            Log.d("MainActivity ->", "within the date " + eachDate + " --> " + siteName + " " + siteId + " " + visitDate + " " + visitors);

                            checkData = "UPDATED!";
                        }

                        if (items.size() == 0){
                            checkData = "NO DATA!";
                        }
                    }
                }

                myRVA.notifyDataSetChanged();

                Toast.makeText(MainActivity.this, checkData, Toast.LENGTH_SHORT).show();

                txtItemCount.setText(String.valueOf(items.size()));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    public void getDaysBetween ( String startDate, String endDate){

        Log.d("MainActivity", "** getDaysBetween function **");

            try {

                allDates.clear();

                SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date dateStart = myFormat.parse(startDate);
                Date dateEnd = myFormat.parse(endDate);


                Calendar c1 = DateToCalendar(dateStart);
                Calendar c2 = DateToCalendar(dateEnd);

                while (!areEqualDate(c1, c2)) {
                    allDates.add(myFormat.format(c1.getTime()));
                    //System.out.println (c1.getTime());
                    c1.add(Calendar.DAY_OF_YEAR, 1);

                    Log.d("MainActivity", "dateBetween --> " + String.valueOf(c1.getTime()));
                }
                allDates.add(myFormat.format(c2.getTime()));

                Log.d("MainActivity", "The 2 dates --> " + startDate + " <--> " + endDate);
                Log.d("MainActivity", "The 2 dates with format --> " + dateStart + " <--> " + dateEnd);
                Log.d("MainActivity", "allDates list --> " + String.valueOf(allDates));

            } catch (ParseException e) {
                e.printStackTrace();
            }
    }

    private static boolean areEqualDate(Calendar c1, Calendar c2) {
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) return false;
        if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH)) return false;
        if (c1.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR)) return false;
        return true;
    }

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
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

    public void viewChart () {
        Log.d("MainActivity", "*** ViewChart ***");
        new chartActivity(siteName, visitors);
        Intent intent = new Intent(MainActivity.this, chartActivity.class);
        startActivity(intent);
        //chartActivity(siteName, visitors);
    }

}
