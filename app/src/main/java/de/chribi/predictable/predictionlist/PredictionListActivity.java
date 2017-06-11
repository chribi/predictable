package de.chribi.predictable.predictionlist;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import javax.inject.Inject;

import de.chribi.predictable.BR;
import de.chribi.predictable.PredictableApp;
import de.chribi.predictable.PredictionItemView;
import de.chribi.predictable.R;
import de.chribi.predictable.databinding.ActivityPredictionListBinding;
import de.chribi.predictable.predictiondetail.PredictionDetailActivity;
import de.chribi.predictable.predictionsets.PredictionSet;
import de.chribi.predictable.predictionsets.PredictionSetQueries;
import de.chribi.predictable.predictionsets.PredictionSetTitles;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class PredictionListActivity extends AppCompatActivity implements PredictionItemView {
    private static final String TAG = "PredictionList";
    private static final String EXTRA_PREDICTION_SET_NAME = "PredictionSet";

    public static void start(Context context, PredictionSet set) {
        Intent intent = new Intent(context, PredictionListActivity.class);
        intent.putExtra(EXTRA_PREDICTION_SET_NAME, set.name());
        context.startActivity(intent);
    }

    @Inject PredictionListViewModel viewModel;
    @Inject PredictionSetQueries queries;
    @Inject PredictionSetTitles titles;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getIntent().getExtras();
        PredictionSet set;
        try {
            set = PredictionSet.valueOf(args.getString(EXTRA_PREDICTION_SET_NAME));
        } catch (IllegalArgumentException | NullPointerException e) {
            Log.e(TAG, e.getMessage());
            finish();
            return;
        }

        ActivityPredictionListBinding binding
                = DataBindingUtil.setContentView(this, R.layout.activity_prediction_list);


        PredictableApp.get(this)
                .getPredictableComponent()
                .inject(this);
        viewModel.setView(this);
        viewModel.setPredictionQuery(queries.getPredictionSetQuery(set));
        binding.setViewModel(viewModel);
        binding.setPredictionItemBinding(
                ItemBinding.of(BR.itemViewModel, R.layout.item_prediction));

        configureToolbar(binding.includedToolbar.toolbar, titles.getPredictionSetTitle(set));
    }

    private void configureToolbar(Toolbar toolbar, String predictionSetTitle) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(predictionSetTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override public void showPredictionDetails(long id) {
        PredictionDetailActivity.start(this, id);
    }
}
