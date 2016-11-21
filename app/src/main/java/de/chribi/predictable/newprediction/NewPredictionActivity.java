package de.chribi.predictable.newprediction;

import android.databinding.DataBindingUtil;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null) {
            toolbar.setTitle(R.string.title_activity_new_prediction);
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        }
    }

    @Override
    public void closeView() {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_prediction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_save_event)
        {
            viewModel.onSavePrediction();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
