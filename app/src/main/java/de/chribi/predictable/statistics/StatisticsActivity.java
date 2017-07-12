package de.chribi.predictable.statistics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import de.chribi.predictable.R;
import de.chribi.predictable.databinding.ActivityStatisticsBinding;

public class StatisticsActivity extends AppCompatActivity {
    private static final String KEY_INVERT_IMPROBABLE_PREDICTIONS = "INVERT_IMPROB_PREDS";

    public static void start(Context context) {
        Intent intent = new Intent(context, StatisticsActivity.class);
        context.startActivity(intent);
    }

    private StatisticsViewModel viewModel;

    @Inject
    void setDependencies(StatisticsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        ActivityStatisticsBinding binding
                = DataBindingUtil.setContentView(this, R.layout.activity_statistics);

        setSupportActionBar(binding.includedToolbar.toolbar);
        configureToolbar();

        // restore state of 'invert low confidence predictions' setting from activity preferences
        SharedPreferences activityPrefs = getPreferences(MODE_PRIVATE);
        viewModel.setInvertLowConfidencePredictions(
                activityPrefs.getBoolean(KEY_INVERT_IMPROBABLE_PREDICTIONS, false));

        binding.setStatistics(viewModel);
    }

    @Override protected void onPause() {
        super.onPause();
        // save the state of the 'invert low confidence predictions' setting across app restarts
        SharedPreferences activityPrefs = getPreferences(MODE_PRIVATE);
        activityPrefs.edit()
                .putBoolean(KEY_INVERT_IMPROBABLE_PREDICTIONS,
                        viewModel.isInvertLowConfidencePredictions())
                .apply();
    }

    private void configureToolbar() {
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null) {
            toolbar.setTitle(R.string.title_activity_statistics);
            toolbar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
