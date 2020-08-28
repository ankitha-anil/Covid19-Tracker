package com.ankitha.covid19tracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class state_info extends AppCompatActivity {
    ImageButton main;
    TextView state, rec, tot, act, dea,none;
    TableRow tr, td,tableRow;
    TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.state_info);

        table
                = findViewById(R.id.table);
        tableRow
                = findViewById(R.id.tableRow1);
        main =
                findViewById(R.id.main);

        table.setColumnStretchable(0, true);
        table.setColumnStretchable(1, true);
        table.setColumnStretchable(2, true);
        table.setColumnStretchable(3, true);
        table.setColumnStretchable(4, true);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(state_info.this, MainActivity.class));
            }
        });

        fetchdata();

    }
    private void fetchdata() {

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
                            fetchtable(paramsArr); //Fetch the table for State info

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                state_info.this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        RequestQueue requestQueue
                = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }




    @SuppressLint({"DefaultLocale", "ResourceType"})
    public void fetchtable(JSONArray obj) throws JSONException {

        tableRow.setBackgroundColor(Color.argb(90,128,128,128));

        for (int i = 1; i < obj.length(); i++) {

            JSONObject data = obj.getJSONObject(i);

            int total = Integer.parseInt(data.getString("confirmed"));
            int recovered = Integer.parseInt(data.getString("recovered"));
            int active = Integer.parseInt(data.getString("active"));
            int death = Integer.parseInt(data.getString("deaths"));
            String name = data.getString("state");

            tr = new TableRow(this);
            td = new TableRow(this);

            // Here create the TextView dynamically

            state = new TextView(this);
            state.setText(name);
            if (name.equals("Dadra and Nagar Haveli and Daman and Diu")) {
                state.setText("Daman and Diu");
            }
            if (name.equals("Andaman and Nicobar Islands")) {
                state.setText("AN Islands");
            }
            state.setTextColor(Color.WHITE);
            state.setTextSize(14);
            state.setPadding(5, 5, 5, 5);
            tr.addView(state);

            tot = new TextView(this);
            tot.setText(String.format("%,d", total));
            tot.setPadding(5, 5, 5, 5);
            tot.setTextSize(14);
            tot.setTextColor(Color.rgb(227, 77, 86));
            tr.addView(tot);

            rec = new TextView(this);
            rec.setText(String.format("%,d", recovered));
            rec.setPadding(5, 5, 5, 5);
            rec.setTextSize(14);
            rec.setTextColor(Color.rgb(246, 180, 67));
            tr.addView(rec);

            act = new TextView(this);
            act.setText(String.format("%,d", active));
            act.setPadding(5, 5, 5, 5);
            act.setTextSize(14);
            act.setTextColor(Color.rgb(18, 182, 167));
            tr.addView(act);

            dea = new TextView(this);
            dea.setPadding(5, 5, 5, 5);
            dea.setTextSize(14);
            dea.setText(String.format("%,d", death));
            dea.setTextColor(Color.rgb(128, 128, 128));
            tr.addView(dea);

            none= new TextView(this);
            none.setText("  ");
            td.addView(none);

            // finally add this to the table row
            table.addView(tr);
            table.addView(td);

        }
    }

}
