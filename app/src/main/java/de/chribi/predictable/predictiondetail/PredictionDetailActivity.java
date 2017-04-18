package de.chribi.predictable.predictiondetail;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import javax.inject.Inject;

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
        binding.setViewModel(viewModel);

        setSupportActionBar(binding.toolbar);
        Log.d(TAG, "onCreate: Details for prediction " + String.valueOf(predictionId));

        configureToolbar();

        // configureChart(binding.chartConfidence);
    }

        /*
    private void configureChart(LineChart chartConfidence) {
        ArrayList<Prediction> predictions = new ArrayList<>();
        predictions.add(new Prediction(0.9, new Date(110, 10, 15,  0, 0)));
        predictions.add(new Prediction(0.83, new Date(110, 10, 16, 12, 0)));
        predictions.add(new Prediction(0.7, new Date(110, 10, 17, 3, 0)));
        predictions.add(new Prediction(0.22, new Date(110, 10, 19, 18, 30)));
        predictions.add(new Prediction(0.55, new Date(110, 10, 20,  22, 0)));
        predictions.add(new Prediction(0.6, new Date(110, 10, 21, 3, 0)));
        predictions.add(new Prediction(0.77, new Date(110, 10, 21, 23, 0)));

        ((ConfidenceLineChart)chartConfidence).setPredictions(predictions);
    }
        */

    private void configureToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }
}
