package de.chribi.predictable.newprediction;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

        binding.textDueDate.setDateFormat(android.text.format.DateFormat.getMediumDateFormat(this));
        binding.textDueTime.setTimeFormat(android.text.format.DateFormat.getTimeFormat(this));

        PredictableApp.get(this).getAppComponent().inject(this);

        viewModel.setView(this);

        binding.setViewModel(viewModel);
        binding.textDueDate.setDate(viewModel.dueDate.get());
        binding.textDueTime.setTime(viewModel.dueDate.get());
    }

    @Override
    public void closeView() {
        // TODO this should go back to the main activity (prediction overview)
        finish();
    }
}
