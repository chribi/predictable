package de.chribi.predictable.newprediction;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.joda.time.format.DateTimeFormat;

import java.util.Date;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import de.chribi.predictable.R;
import de.chribi.predictable.databinding.ActivityNewPredictionBinding;

public class NewPredictionActivity extends AppCompatActivity
    implements NewPredictionView {

    private static final String KEY_TITLE = "TITLE";
    private static final String KEY_CONFIDENCE = "CONFIDENCE";
    private static final String KEY_DUE_DATE_TIME = "DUE_DATE_TIME";

    public static void start(Context context) {
        Intent intent = new Intent(context, NewPredictionActivity.class);
        context.startActivity(intent);
    }

    private NewPredictionViewModel viewModel;

    @Inject
    void setDependencies(NewPredictionViewModel viewModel) {
        this.viewModel = viewModel;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        ActivityNewPredictionBinding binding
                = DataBindingUtil.setContentView(this, R.layout.activity_new_prediction);

        setSupportActionBar(binding.includedToolbar.toolbar);
        configureToolbar();

        binding.textDueDate.setDateFormat(DateTimeFormat.mediumDate());
        binding.textDueTime.setTimeFormat(DateTimeFormat.shortTime());

        if(savedInstanceState != null) {
            restoreViewModelState(savedInstanceState, viewModel);
        }
        binding.setViewModel(viewModel);
    }

    private void configureToolbar() {
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null) {
            toolbar.setTitle(R.string.title_activity_new_prediction);
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_TITLE, viewModel.getPredictionTitle());
        outState.putDouble(KEY_CONFIDENCE, viewModel.getConfidencePercentage());
        outState.putLong(KEY_DUE_DATE_TIME, viewModel.getDueDateTime().getTime());
    }

    private void restoreViewModelState(Bundle bundle, NewPredictionViewModel viewModel) {
        viewModel.setPredictionTitle(bundle.getString(KEY_TITLE));
        viewModel.setConfidencePercentage(bundle.getDouble(KEY_CONFIDENCE));
        long dueDateLong = bundle.getLong(KEY_DUE_DATE_TIME);
        Date dueDate = new Date(dueDateLong);
        viewModel.setDueDateTime(dueDate);
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
        if(item.getItemId() == R.id.action_save_prediction)
        {
            viewModel.onSavePrediction();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
