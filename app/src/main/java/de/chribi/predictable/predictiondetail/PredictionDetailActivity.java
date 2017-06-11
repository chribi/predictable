package de.chribi.predictable.predictiondetail;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import de.chribi.predictable.R;
import de.chribi.predictable.databinding.ActivityPredictionDetailBinding;

public class PredictionDetailActivity extends AppCompatActivity implements PredictionDetailView {
    private static final String TAG = "Details";
    private static final String EXTRA_PREDICTION_ID = "PredictionId";

    public static void start(Context context, long predictionId) {
        Intent intent = new Intent(context, PredictionDetailActivity.class);
        intent.putExtra(EXTRA_PREDICTION_ID, predictionId);
        context.startActivity(intent);
    }

    private PredictionDetailViewModel viewModel;

    @Inject
    void setDependencies(PredictionDetailViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        // get prediction id from extras
        Bundle args = getIntent().getExtras();
        long predictionId = args.getLong(EXTRA_PREDICTION_ID, -1);
        Log.d(TAG, "onCreate: Details for prediction " + String.valueOf(predictionId)); // NON-NLS

        ActivityPredictionDetailBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_prediction_detail);

        viewModel.setPrediction(predictionId);
        if(viewModel.isValid()) {
            binding.setViewModel(viewModel);
            setSupportActionBar(binding.toolbar);
            configureToolbar();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_prediction_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_delete_prediction) {
            viewModel.deletePrediction();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configureToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public void closeView() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onInvalidPrediction() {
        Toast.makeText(getApplicationContext(), R.string.error_invalid_prediction, Toast.LENGTH_LONG).show();
        closeView();
    }
}
