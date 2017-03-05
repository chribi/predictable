package de.chribi.predictable.predictiondetail;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.chribi.predictable.BuildConfig;
import de.chribi.predictable.PredictableApp;
import de.chribi.predictable.R;
import de.chribi.predictable.databinding.ActivityPredictionDetailBinding;

public class PredictionDetailActivity extends AppCompatActivity {
    private static final String TAG = "Details";
    private static final String EXTRA_PREDICTION_ID = "PredictionId";

    public static void start(Context context, long predictionId) {
        Intent intent = new Intent(context, PredictionDetailActivity.class);
        intent.putExtra(EXTRA_PREDICTION_ID, predictionId);
        context.startActivity(intent);
    }

    @Inject PredictionDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPredictionDetailBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_prediction_detail);

        PredictableApp.get(this).getPredictableComponent().inject(this);

        Bundle args = getIntent().getExtras();
        long predictionId = args.getLong(EXTRA_PREDICTION_ID, -1);

        viewModel.setPredictedEvent(predictionId);
        Log.d(TAG, "onCreate: Details for prediction " + String.valueOf(predictionId));

        binding.setViewModel(viewModel);

        setSupportActionBar(binding.toolbar);
        configureToolbar();

        configureChart(binding.chartConfidence);
    }

    private void configureChart(LineChart chartConfidence) {
        // TODO Extract to seperate class and use correct confidence values
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1.1f, 90));
        entries.add(new Entry(2f, 83));
        entries.add(new Entry(2.3f, 70));
        entries.add(new Entry(4f, 22));
        entries.add(new Entry(5.5f, 55));
        entries.add(new Entry(6.1f, 60));

        LineDataSet dataSet = new LineDataSet(entries, "Confidence [%]");
        @ColorInt int graphColor;
        if(Build.VERSION.SDK_INT >= 23) {
            graphColor = getColor(R.color.colorPrimaryDark);
        } else {
            graphColor = getResources().getColor(R.color.colorPrimaryDark);
        }
        dataSet.setColor(graphColor);
        dataSet.setCircleColor(graphColor);
        LineData data = new LineData(dataSet);
        chartConfidence.setScaleEnabled(false);
        chartConfidence.getLegend().setEnabled(false);
        chartConfidence.getDescription().setEnabled(false);
        XAxis xAxis = chartConfidence.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-60);
        xAxis.setAxisMinimum(0.9f);
        xAxis.setAxisMaximum(6.3f);

        YAxis yAxis = chartConfidence.getAxisLeft();
        yAxis.setAxisMinimum(0.f);
        yAxis.setAxisMaximum(100.f);

        LimitLine midLine = new LimitLine(50.f);
        midLine.enableDashedLine(8, 8, 0);
        yAxis.addLimitLine(midLine);

        final DecimalFormat format = new DecimalFormat("###");
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return format.format(value) + " %";
            }
        });


        chartConfidence.getAxisRight().setEnabled(false);
        chartConfidence.setData(data);
    }

    private void configureToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }
}
