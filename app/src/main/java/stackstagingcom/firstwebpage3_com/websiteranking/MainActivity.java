package stackstagingcom.firstwebpage3_com.websiteranking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.github.clans.fab.FloatingActionMenu;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
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
import java.util.Objects;
import java.util.Set;
import okhttp3.OkHttpClient;
import android.support.v7.app.ActionBar;

public class MainActivity extends AppCompatActivity {

    //SharedPreference references
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String FIRST_TIME = "firstTime";

    public static int jsonID = 0;


    private String first_time;

    private RecyclerView myRV;
    private RecyclerView.Adapter myRVA;
    private RecyclerView.LayoutManager myRVLM;

    private OkHttpClient client;

    private TextView dtStart;
    private TextView dtEnd;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private DatePickerDialog.OnDateSetListener dateSetListener2;
    private DatePickerDialog.OnDateSetListener dateSetListener3;

    String startDate = "";
    String endDate = "";
    String dateVisit = "";
    String filterName = "";
    String fileName = "websiteRanking.json";
    String id, websiteName, visitDate, numOfVisitors, filePath, downloadDir;

    Button btnFilter, btnAdd;

    TextView txtVisitors, txtSiteName, txtItemCount, etVisitDate, txtVisitDate;

    public static String[] siteName;
    public static float[] visitors;

    ArrayList<items> items;
    ArrayList<String> allDates = new ArrayList();

    com.github.clans.fab.FloatingActionButton fab_add, fab_search, fab_refresh, fab_graph, fab_export, fab_creator;
    FloatingActionMenu fab_menu;

    EditText etSiteName, etVisitors, etFilterName;

    AlertDialog.Builder adb;

    int firstTime = 0;
    int filterType;

    ActionBar actionBar;

    Window window;

    items newItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "** onCreate method **");

        actionBar = getSupportActionBar();
        ColorDrawable cd = new ColorDrawable(getResources().getColor(R.color.appColor));
        actionBar.setBackgroundDrawable(cd);

        //StatusBar
        window  = MainActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.appColor));


        loadData();

        fab_menu = findViewById(R.id.fab_menu);
        fab_add = findViewById(R.id.fab_add);
        fab_search = findViewById(R.id.fab_search);
        fab_refresh = findViewById(R.id.fab_refresh);
        fab_graph = findViewById(R.id.fab_graph);
        fab_export = findViewById(R.id.fab_export);
        fab_creator = findViewById(R.id.fab_creator);

        client = new OkHttpClient();

        items = new ArrayList<>();

        myRV = findViewById(R.id.RV);

        txtVisitors = findViewById(R.id.textVisits);
        txtSiteName = findViewById(R.id.textSiteName);
        txtItemCount = findViewById(R.id.txtItemCount);
        txtVisitDate = findViewById(R.id.txtVisitDate);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
                fab_menu.close(true);
            }
        });

        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterJSONDialog();
                fab_menu.close(true);
            }
        });

        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getJSON();
                fab_menu.close(true);
            }
        });

        fab_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( (siteName != null) && (visitors != null) ) {
                    viewChart();
                    fab_menu.close(true);
                } else {
                    Toast.makeText(MainActivity.this, "Chart is not working at the moment!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fab_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Export is currently not available!", Toast.LENGTH_LONG).show();
                filePath = String.valueOf(getFilesDir());
                downloadDir = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                //exportFile(fileName);
                fab_menu.close(true);
            }
        });

        fab_creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creatorLink();
                fab_menu.close(true);
            }
        });

        fab_menu.setClosedOnTouchOutside(true);

        getJSON();

        if (firstTime == 0){
            writeFile(fileName);
        }

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

        txtVisitDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortVisitDate();
            }
        });

        saveData();
    }


    public items jsonObjects(JSONObject jsonObject) throws JSONException {
        Log.d("MainActivity", "** jsonObject function **");

        String siteId = jsonObject.getString("id_website");
        String siteName = jsonObject.getString("website_name");
        String visitDate = jsonObject.getString("visit_date");
        String visitors = jsonObject.getString("total_visits");

        newItem = new items( siteName, siteId, visitDate, visitors);

        return newItem;
    }


    public String checkNumberValue(int number){
        Log.d("MainActivity", "** checkNumberValue function **");
        String num ="";
        if (number < 10){
            num = 0+String.valueOf(number);
            Log.d("MainActivity", "The Value --> "+ num);
            return num;
        } else {
            num = String.valueOf(number);
            Log.d("MainActivity", "The Value --> "+ num);
            return num;
        }
    }

    public void saveData () {
        Log.d("MainActivity", "** saveData function **");
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String ft = String.valueOf(firstTime);
        editor.putString(FIRST_TIME, ft);
        Log.d("MainActivity", "Data saved --> "+ ft);
        editor.apply();
    }

    public void loadData () {
        Log.d("MainActivity", "** loadData function **");
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        first_time = sp.getString(FIRST_TIME, "0");
        Log.d("MainActivity", "Data Loaded --> "+ first_time);
        firstTime = Integer.parseInt(first_time);
    }

     public void writeFile(String fileName) {
        Log.d("MainActivity", "** CreateFile function **");

        String _id;
        String _websiteName;
        String _visitDate;
        String _numOfVisitors;

         try {
             JSONObject jsonObject = new JSONObject();
             OutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);

             fos.write("[".getBytes());
             fos.write("\n".getBytes());

             for (int i=0; i<items.size();i++){

                  _id = items.get(i).getSiteId();
                  _websiteName = items.get(i).getSiteName();
                  _visitDate = items.get(i).getVisitDate();
                  _numOfVisitors = items.get(i).getVisiotrs();

                     jsonObject.put("id_website", _id);
                     jsonObject.put("website_name", _websiteName);
                     jsonObject.put("visit_date", _visitDate);
                     jsonObject.put("total_visits", _numOfVisitors);

                     if(!(i == 0)) {
                      fos.write(",".getBytes());
                     }

                 fos.write(jsonObject.toString().getBytes());
                 fos.write("\n".getBytes());

                 Log.d("MainActivity", "jsonObject -->"+ jsonObject.toString() +" --> "+i);

                 }
             fos.write("]".getBytes());

             Log.d("MainActivity", "saved to -->"+ getFilesDir()+"/"+fileName);

             fos.close();

         } catch (JSONException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }
         ++firstTime;
     }

     public void filterJSONDialog () {
         Log.d("MainActivity", "** filterJSONDialog function **");

         adb = new AlertDialog.Builder(MainActivity.this);
         View adbView = getLayoutInflater().inflate(R.layout.filter_dialog, null);

         dtStart = adbView.findViewById(R.id.dtStart);
         dtEnd = adbView.findViewById(R.id.dtEnd);
         etFilterName = adbView.findViewById(R.id.etFilterName);
         btnFilter = adbView.findViewById(R.id.btnFilter);

         adb.setView(adbView);
         final AlertDialog ad = adb.create();
         ad.show();

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
                 startDate = year + "-" + checkNumberValue(month+1) + "-" + checkNumberValue(day);
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
                 endDate = year + "-" + checkNumberValue(month+1) + "-" + checkNumberValue(day);
                 dtEnd.setText(endDate);
             }
         };



         btnFilter.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 filterName = etFilterName.getText().toString().trim();
                 Log.d("MainActivity", "filterName --> " + filterName);

                 if (!startDate.equals("") && !endDate.equals("") && !filterName.equals("")) {
                     filterType=3;
                     filterJSON(startDate, endDate, filterName);
                     ad.dismiss();
                 } else {
                     if (startDate.equals("") || endDate.equals("")) {
                         Toast.makeText(MainActivity.this, "Please select a date!", Toast.LENGTH_SHORT).show();
                     } else {
                         filterType = 2;
                         filterJSON(startDate, endDate, filterName);
                         ad.dismiss();
                     }

                     if (filterName.equals("")) {
                         Toast.makeText(MainActivity.this, "Please fill required fields!", Toast.LENGTH_SHORT).show();
                     } else {
                         filterType = 1;
                         filterJSON(startDate, endDate, filterName);
                         ad.dismiss();
                     }

                     //reset search
                     startDate = "";
                     endDate = "";
                     filterName = "";
                 }
             }
         });
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
                dateVisit = year + "-" + checkNumberValue(month+1) + "-" + checkNumberValue(day);
                etVisitDate.setText(dateVisit);
            }
        };


        adb.setView(adbView);
       final AlertDialog ad = adb.create();
        ad.show();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etSiteName.getText().toString().isEmpty() && !etVisitDate.getText().toString().isEmpty() && !etVisitors.getText().toString().isEmpty()) {
                    dataToJSON();
                    ad.dismiss();
                    getJSON();
                } else {
                    Toast.makeText(MainActivity.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void dataToJSON () {
        Log.d("MainActivity", "** dataToJSON function **");

        int newId = jsonID +1;

        Log.d("MainActivity", "Read from local file");
        readLocalFile();

        Log.d("MainActivity", "write to local file");
        try {
            JSONObject jsonObject = new JSONObject();
            OutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);

            fos.write("[".getBytes());
            fos.write("\n".getBytes());

            for (int i=0; i<items.size();i++){

                    //Write the previous items that was in the file
                    id = items.get(i).getSiteId();
                    websiteName = items.get(i).getSiteName();
                    visitDate = items.get(i).getVisitDate();
                    numOfVisitors = items.get(i).getVisiotrs();

                jsonObject.put("id_website", id);
                jsonObject.put("website_name", websiteName);
                jsonObject.put("visit_date", visitDate);
                jsonObject.put("total_visits", numOfVisitors);

                if(!(i == 0)) {
                    fos.write(",".getBytes());
                }

                fos.write(jsonObject.toString().getBytes());
                fos.write("\n".getBytes());


            }

            //Write the new added data
            id = String.valueOf(newId);
            fos.write(",".getBytes());
            websiteName = etSiteName.getText().toString();
            visitDate = etVisitDate.getText().toString();
            numOfVisitors = etVisitors.getText().toString();

            jsonObject.put("id_website", id);
            jsonObject.put("website_name", websiteName);
            jsonObject.put("visit_date", visitDate);
            jsonObject.put("total_visits", numOfVisitors);

            fos.write(jsonObject.toString().getBytes());
            fos.write("\n".getBytes());

            fos.write("]".getBytes());
            fos.close();

            Log.d("MainActivity", "Data has been added!");

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void readLocalFile(){
        try {
            Log.d("MainActivity", "** Local file **");
            items.clear();
            String json;
            FileInputStream fis;
            fis = openFileInput(fileName);
            // InputStreamReader isr = new InputStreamReader(fis);
            // BufferedReader br = new BufferedReader(isr);
            //  StringBuilder sb = new StringBuilder();
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();

            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                jsonObjects(jsonObject);

                items.add(newItem);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void getJSON () {

        Log.d("MainActivity", "** getJSON function **");

        if (firstTime == 0) {
            try {
                items.clear();
                Log.d("MainActivity", "** Assets file **");
                String json;
                InputStream is = getAssets().open("websiteRanking.json");
                int size = is.available();
                byte [] buffer= new byte[size];
                is.read(buffer);
                is.close();

                json = new String(buffer,"UTF-8");
                JSONArray  jsonArray = new JSONArray(json);

                for (int i=0; i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    jsonObjects(jsonObject);

                    items.add(newItem);
                }

            } catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (firstTime == 1) {
            readLocalFile();
        }

        myRV.setHasFixedSize(true);
        myRVLM = new LinearLayoutManager(MainActivity.this);
        myRVA = new MyRVA(items);

        myRV.setLayoutManager(myRVLM);
        myRV.setAdapter(myRVA);

        txtItemCount.setText(String.valueOf(items.size()));

        JSONtoArray();
    }

    public void JSONtoArray () {

        Log.d("MainActivity", "** JSONtoArray function **");

        String[] nameOfSite = new String[items.size()];
        float[] numOfVisits = new float[items.size()];

        siteName = new String[items.size()];


        for (int i = 0; i < items.size(); i++) {

            //Add data to siteNames & visitors array
            String _websiteName = items.get(i).getSiteName();
            String _numOfVisitors = items.get(i).getVisiotrs();

            nameOfSite[i] = _websiteName;
            numOfVisits[i] = Integer.parseInt(_numOfVisitors);

            //Log.d("MainActivity", nameOfSite[i]+" <-- with the --> "+numOfVisits[i]);
        }

        //Keep the unique names to be used as indicators in the array
        Set<String> uniqueString = new HashSet<>(Arrays.asList(nameOfSite));
        siteName = uniqueString.toArray(new String[uniqueString.size()]);
        visitors = new float[uniqueString.size()];


        /** This section will filter the JSON file to give us the list of all the unique values
            with the reference number of the row in the JSON file and count for the unique value quantity **/
        for (int i = 0; i < siteName.length; i++) {
            int count = 0;
            visitors[i] = 0;
            for (int j = 0; j < nameOfSite.length; j++) {
                //Log.d("MainActivity", j + " --> " + nameOfSite[j] + " <--> " + numOfVisits[j]);

                if (nameOfSite[j].equals(siteName[i])) {
                    visitors[i] += numOfVisits[j];
                    ++count;
                }
            }
            //Log.d("MainActivity", siteName[i]+" <--> "+visitors[i]);
        }
        Log.d("MainActivity", "The final arrays --> "+ Arrays.toString(siteName)+" <--> "+Arrays.toString(visitors));
    }

    public void filterJSON (String startDate, String endDate, String filterName) {

        Log.d("MainActivity", "** filterJSON function **");
            String json;
            String checkData = "";
            String eachDate = "";
            items.clear();

            try {
                FileInputStream fis;
                fis = openFileInput(fileName);
                int size = fis.available();
                byte[] buffer = new byte[size];
                fis.read(buffer);
                fis.close();

                json = new String(buffer, "UTF-8");
                JSONArray jsonArray = new JSONArray(json);

                if (filterType == 1) {
                    Log.d("MainActivity", "** filterType "+Integer.toString(filterType)+" **");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        if (jsonObject.getString("website_name").equals(filterName)) {
                            jsonObjects(jsonObject);
                            items.add(newItem);
                        }
                    }
                } else if (filterType == 2){
                    Log.d("MainActivity", "** filterType "+Integer.toString(filterType)+" **");
                    getDaysBetween(startDate, endDate);
                    for (int j = 0; j < allDates.size(); j++) {
                        eachDate = String.valueOf(allDates.get(j));

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            if (jsonObject.getString("visit_date").equals(eachDate)) {
                                jsonObjects(jsonObject);
                                items.add(newItem);
                           }
                        }
                    }
                } else if (filterType == 3) {
                    getDaysBetween(startDate, endDate);
                    for (int j = 0; j < allDates.size(); j++) {
                        eachDate = String.valueOf(allDates.get(j));

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            if (jsonObject.getString("visit_date").equals(eachDate) && jsonObject.getString("website_name").equals(filterName)) {
                                jsonObjects(jsonObject);
                                items.add(newItem);
                            }
                        }
                    }
                }

                if (items.size() == 0){
                    checkData = "NO DATA!";
                } else {
                    checkData = "UPDATED!";
                }

                myRVA.notifyDataSetChanged();

                Toast.makeText(MainActivity.this, checkData, Toast.LENGTH_SHORT).show();

                txtItemCount.setText(String.valueOf(items.size()));

                JSONtoArray();

                filterType = 0;

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
        Log.d("MainActivity", "*** sortTotalVisits function ***");
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
        Log.d("MainActivity", "*** sortWebsitename function ***");
        Collections.sort(items, new Comparator<stackstagingcom.firstwebpage3_com.websiteranking.items>() {
            @Override
            public int compare(items items, items t1) {
                return items.getSiteName().compareTo(t1.getSiteName());
            }
        });
        myRVA.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "The list has been sorted Alphabetically", Toast.LENGTH_SHORT).show();
    }

    public void sortVisitDate () {
        Log.d("MainActivity", "*** sortVisitDate function ***");
        Collections.sort(items, new Comparator<stackstagingcom.firstwebpage3_com.websiteranking.items>() {
            @Override
            public int compare(items items, items t1) {
                return items.getVisitDate().compareTo(t1.getVisitDate());
            }
        });
         myRVA.notifyDataSetChanged();
         Toast.makeText(MainActivity.this, "The list has been sorted by date", Toast.LENGTH_SHORT).show();
    }

    public void viewChart () {
        Log.d("MainActivity", "*** ViewChart ***");
        Intent intent = new Intent(MainActivity.this, chartActivity.class);
        intent.putExtra("startDate", startDate);
        intent.putExtra("endDate", endDate);
        intent.putExtra("numericValue", visitors);
        intent.putExtra("stringValue", siteName);
        startActivity(intent);
    }

    public void creatorLink () {
        Log.d("MainActivity", "*** creatorLink ***");
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.github.com/sxaxmz"));
        startActivity(browserIntent);
    }

    public void exportFile (String fileName) {
        Log.d("MainActivity", "** exportFile function **");
        File file2 = new File(downloadDir, fileName);
        File file = new File(filePath,fileName);
        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Log.d("MainActivity", "file name --> "+ file.getName());
        Log.d("MainActivity", "file path --> "+ file.getAbsolutePath());
        Log.d("MainActivity", "file length --> "+ file.length());
        Objects.requireNonNull(dm).addCompletedDownload(file.getName(), file.getName(), false, "application/json",file.getAbsolutePath(), file.length() ,true);
        Toast.makeText(MainActivity.this, "File has been exported!", Toast.LENGTH_SHORT).show();
    }

}
