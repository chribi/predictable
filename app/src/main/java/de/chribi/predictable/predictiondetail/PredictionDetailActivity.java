package de.chribi.predictable.predictiondetail;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
        Log.d(TAG, "onCreate: Details for prediction " + String.valueOf(predictionId));

        binding.setViewModel(viewModel);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

    }
}
