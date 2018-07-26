package stackstagingcom.firstwebpage3_com.websiteranking;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

public class chartActivity extends AppCompatActivity {

    public static String TAG = "chartActivity";
    ArrayList<items> items;

    String startDate, endDate, dates;

     String [] siteName;
     float[] visitors;

    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);
        Log.d(TAG, "*** OnCreate method ***");

        pieChart = findViewById(R.id.pieChart);
        siteName = getIntent().getStringArrayExtra("stringValue");
        visitors = getIntent().getFloatArrayExtra("numericValue");

        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");

        dates = "from "+startDate +" to "+ endDate+".";


        Description description = new Description();
        description.setTextColor(Color.BLACK);
        if (startDate.equals("") && endDate.equals("")){
            description.setText("Chart is based on data-set for all dates recorded.");
            description.setTextSize(18);
        } else {
            description.setText("Chart is based on data-set "+dates);
            description.setTextSize(15);
        }
        description.setPosition(1060,1500);

        pieChart.setDescription(description);
        pieChart.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(35f);
        pieChart.setTransparentCircleAlpha(25);
        pieChart.setCenterText("Sites by visits");
        pieChart.setCenterTextSize(18);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setExtraOffsets(0,0,0,-100);


        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(TAG, "** onValueSelected **");
                Log.d(TAG, "Entry --> "+e.toString());
                Log.d(TAG, "Highlight --> "+h.toString());

                //get numeric value
                int numericValue = e.toString().indexOf("y:");
                String numValue = e.toString().substring(numericValue + 3);

                //get string value
                for(int i = 0; i<visitors.length; i++){
                    if(visitors[i] == Float.parseFloat(numValue)){
                        numericValue = i;
                        break;
                    }
                }
                String stringValue = siteName[numericValue];
                Toast.makeText(chartActivity.this, "Website: "+stringValue+"\n"+ " Visitors: "+numValue, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Website: "+stringValue+" <--> "+" Visitors: "+numValue);
            }

            @Override
            public void onNothingSelected (){
                Log.d(TAG, "** onNothingSelected **");
            }
        });
        addDataSet();

    }

    public void addDataSet() {
        Log.d(TAG, "*** addDataSet function ***");
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<String> siteNames = new ArrayList<>();
        ArrayList<Integer> siteVisits = new ArrayList<>();

        for (int i = 0; i<visitors.length; i++){
            entries.add(new PieEntry(visitors[i], siteName[i]));
        }

        for (int i = 0; i<siteName.length; i++){
            siteNames.add(siteName[i]);
        }

        for (int i = 0; i<visitors.length; i++){
            siteVisits.add(Math.round(visitors[i]));
        }

        //Data set
        PieDataSet pieDataSet = new PieDataSet(entries, "Websites by visits");
        pieDataSet.setSliceSpace(7);
        pieDataSet.setValueTextSize(18);
        pieDataSet.setValueTextColor(Color.BLACK);


        //Data set colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.MAGENTA);
        colors.add(Color.DKGRAY);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.LTGRAY);

        pieDataSet.setColors(colors);

        //Legend
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11);
        legend.setYEntrySpace(3f);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //Pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
