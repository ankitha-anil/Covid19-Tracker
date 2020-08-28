package com.ankitha.covid19tracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.callback.OnPieSelectListener;
import com.razerdp.widget.animatedpieview.data.IPieInfo;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class statistics extends AppCompatActivity {

    private LineChart linechart;
    ImageButton main;
    Button confirmed, recovered, deaths;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        linechart
                = findViewById(R.id.linechart);
        confirmed
                = findViewById(R.id.confirmed_button);
        recovered
                = findViewById(R.id.recovered_button);
        deaths
                = findViewById(R.id.death_button);
        main =
                findViewById(R.id.main);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(statistics.this, MainActivity.class));
            }
        });

        fetchdata();

    }
    public void fetchdata() {
        // Create a String request
        String url = "https://api.covid19india.org/data.json";

        // using Volley Library

        final StringRequest request
                = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onResponse(String response) {

                        // Handle the JSON object and
                        // handle it inside try and catch
                        try {

                            // Creating object of JSONObject
                            JSONObject obj
                                    = new JSONObject(
                                    response.toString());
                            JSONArray paramsArr = obj.getJSONArray("statewise");
                            JSONObject data = paramsArr.getJSONObject(0);

                            int recovered = Integer.parseInt(data.getString("recovered"));
                            int active = Integer.parseInt(data.getString("active"));
                            int death = Integer.parseInt(data.getString("deaths"));


                            // Set the data in text view
                            // which are available in JSON format
                            // Note that the parameter inside
                            // the getString() must match
                            // with the name given in JSON format

                            fetchpiechart(recovered, active, death); //Fetch the Pie Chart

                            fetchchart(obj); //Fetch the line chart

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                statistics.this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        RequestQueue requestQueue
                = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }
    public void fetchpiechart(int rec, int act, int dea) {
        AnimatedPieView mAnimatedPieView = findViewById(R.id.piechart);
        AnimatedPieViewConfig config = new AnimatedPieViewConfig();

        config.startAngle(-140)// Starting angle offset
                .addData(new SimplePieInfo(rec, Color.rgb(246, 180, 67), "Recovered"))//Data (bean that implements the IPieInfo interface)
                .addData(new SimplePieInfo(dea, Color.rgb(147, 147, 147), "Deaths"))
                .addData(new SimplePieInfo(act, Color.rgb(18, 182, 167), "Active"))
                .duration(1500)
                .drawText(true)
                .textSize(35);
        config.selectListener(new OnPieSelectListener<IPieInfo>() {
                                  @Override
                                  public void onSelectPie(@NonNull IPieInfo pieInfo, boolean isFloatUp) {
                                      Toast.makeText(statistics.this, pieInfo.getDesc() + ":  " + pieInfo.getValue(), Toast.LENGTH_SHORT).show();
                                  }
                              }
        );

        // The following two sentences can be replace directly 'mAnimatedPieView.start (config); '
        mAnimatedPieView.applyConfig(config);
        mAnimatedPieView.start();

    }

    public void fetchchart(JSONObject obj) throws JSONException {

        linechart.setTouchEnabled(true);
        linechart.setClickable(true);
        linechart.setDoubleTapToZoomEnabled(false);

        linechart.setNoDataText("Cannot obtain data");
        linechart.setNoDataTextColor(Color.BLACK);

        linechart.getDescription().setEnabled(false);
        linechart.getLegend().setEnabled(false);

        linechart.setDrawGridBackground(false);
        linechart.getAxisRight().setDrawGridLines(false);
        linechart.getAxisLeft().setDrawGridLines(false);

        linechart.animateXY(1000,1000);
        linechart.setHighlightPerTapEnabled(true);
        linechart.setDrawMarkers(true);

        CustomMarkerView mv =new CustomMarkerView(this);
        linechart.setMarker(mv);

        final JSONArray date = obj.getJSONArray("cases_time_series");

        final ArrayList<String> dateaxis = new ArrayList<String>();

        for( int i=0;i<date.length();i++) //Retrieve X Axis values
        {
            JSONObject data = date.getJSONObject(i);
            dateaxis.add(data.getString("date"));
        }

        XAxis xAxis = linechart.getXAxis();
        xAxis.setGranularity(2f); // minimum axis-step (interval) is 2
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dateaxis));

        final ArrayList<Entry> recs = new ArrayList<>();
        final ArrayList<Entry> deas = new ArrayList<>();
        final ArrayList<Entry> acts = new ArrayList<>();


        for(int i=date.length()-10; i <date.length();i++)
        {
            JSONObject data = date.getJSONObject(i);
            recs.add(new Entry(i,Integer.parseInt(data.getString("dailyrecovered"))));
            acts.add(new Entry(i,Integer.parseInt(data.getString("dailyconfirmed"))));
            deas.add(new Entry(i,Integer.parseInt(data.getString("dailydeceased"))));

        }

        final LineDataSet act = new LineDataSet(acts, "Daily Confirmed");
        act.setFillAlpha(110);
        act.setColor(Color.rgb(227, 77, 86));
        act.setLineWidth(2);
        act.setCircleColor(Color.rgb(227, 77, 86));
        act.setCircleRadius(3);
        act.setCircleHoleColor(Color.WHITE);
        act.setCircleHoleRadius(1.5f);
        act.setValueTextSize(0);
        act.setAxisDependency(YAxis.AxisDependency.LEFT);
        act.setDrawHighlightIndicators(false);

        final LineDataSet rec = new LineDataSet(recs, "Daily Recovered");
        rec.setFillAlpha(110);
        rec.setColor(Color.rgb(246, 180, 67));
        rec.setLineWidth(2);
        rec.setCircleColor(Color.rgb(246, 180, 67));
        rec.setCircleRadius(3);
        rec.setCircleHoleColor(Color.WHITE);
        rec.setCircleHoleRadius(1.5f);
        rec.setValueTextSize(0);
        rec.setAxisDependency(YAxis.AxisDependency.LEFT);
        rec.setDrawHighlightIndicators(false);

        final LineDataSet dea = new LineDataSet(deas, "Daily Deceased");
        dea.setFillAlpha(110);
        dea.setColor(Color.rgb(147, 147, 147));
        dea.setLineWidth(2);
        dea.setCircleColor(Color.rgb(147, 147, 147));
        dea.setCircleRadius(3);
        dea.setCircleHoleColor(Color.WHITE);
        dea.setCircleHoleRadius(1.5f);
        dea.setValueTextSize(0);
        dea.setAxisDependency(YAxis.AxisDependency.LEFT);
        dea.setDrawHighlightIndicators(false);


        final ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(rec);
        dataSets.add(act);
        dataSets.add(dea);

        LineData data = new LineData(dataSets);
        linechart.setData(data);

        confirmed.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        linechart.clear();
                        LineData confirmed = new LineData(act);
                        linechart.animateXY(1000,1000);
                        linechart.setData(confirmed);
                    }
                }
        );

        recovered.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        linechart.clear();
                        LineData confirmed = new LineData(rec);
                        linechart.animateXY(1000,1000);
                        linechart.setData(confirmed);
                    }
                }
        );

        deaths.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        linechart.clear();
                        LineData confirmed = new LineData(dea);
                        linechart.animateXY(1000,1000);
                        linechart.setData(confirmed);
                    }
                }
        );

    }
}
