package de.chribi.predictable.newprediction;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import javax.inject.Inject;

import de.chribi.predictable.PredictableApp;
import de.chribi.predictable.R;
import de.chribi.predictable.databinding.ActivityNewPredictionBinding;

public class NewPredictionActivity extends AppCompatActivity
    implements NewPredictionView {

    @Inject NewPredictionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNewPredictionBinding binding
                = DataBindingUtil.setContentView(this, R.layout.activity_new_prediction);

        setSupportActionBar(binding.includedToolbar.toolbar);
        configureToolbar();

        binding.textDueDate.setDateFormat(android.text.format.DateFormat.getMediumDateFormat(this));
        binding.textDueTime.setTimeFormat(android.text.format.DateFormat.getTimeFormat(this));

        PredictableApp.get(this).getAppComponent().inject(this);

        viewModel.setView(this);

        binding.setViewModel(viewModel);
        binding.textDueDate.setDate(viewModel.dueDate.get());
        binding.textDueTime.setTime(viewModel.dueDate.get());
    }

    private void configureToolbar() {
        getSupportActionBar().setTitle(R.string.title_activity_new_prediction);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
    }

    @Override
    public void closeView() {
        // TODO this should go back to the main activity (prediction overview)
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_prediction, menu);
        return true;
    }
}
