package de.chribi.predictable.statistics;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import de.chribi.predictable.PredictableApp;
import de.chribi.predictable.R;
import de.chribi.predictable.databinding.ActivityStatisticsBinding;

public class StatisticsActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, StatisticsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStatisticsBinding binding
                = DataBindingUtil.setContentView(this, R.layout.activity_statistics);


        long start = System.currentTimeMillis();
        Statistics statistics =
                Statistics.of(PredictableApp.get(this)
                        .getPredictableComponent().getStorage().getPredictions());
        long end = System.currentTimeMillis();
        Log.d("STATS", "Calculating statistics took " + String.valueOf(end - start) + " ms");

        setSupportActionBar(binding.includedToolbar.toolbar);
        configureToolbar();
        binding.setStatistics(statistics);
    }

    private void configureToolbar() {
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null) {
            toolbar.setTitle(R.string.title_activity_statistics);
            toolbar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
