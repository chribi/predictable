package de.chribi.predictable;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import de.chribi.predictable.data.ConfidenceStatement;
import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.databinding.ActivityMainBinding;
import de.chribi.predictable.newprediction.NewPredictionActivity;
import de.chribi.predictable.predictiondetail.PredictionDetailActivity;
import de.chribi.predictable.predictionlist.PredictionListActivity;
import de.chribi.predictable.predictionsets.PredictionSet;
import de.chribi.predictable.startscreen.ShowMoreFooterViewModel;
import de.chribi.predictable.startscreen.StartScreenView;
import de.chribi.predictable.startscreen.StartScreenViewModel;
import de.chribi.predictable.storage.PredictionStorage;
import me.tatarka.bindingcollectionadapter2.OnItemBind;
import me.tatarka.bindingcollectionadapter2.itembindings.OnItemBindClass;

public class MainActivity extends AppCompatActivity implements StartScreenView {

    @Inject StartScreenViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding
                = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PredictableApp.get(this).getPredictableComponent().inject(this);
        viewModel.setView(this);

        binding.setViewModel(viewModel);
        OnItemBind itemBinding = new OnItemBindClass<>()
                .map(PredictionItemViewModel.class, BR.itemViewModel, R.layout.item_prediction)
                .map(String.class, BR.title, R.layout.item_prediction_list_header)
                .map(ShowMoreFooterViewModel.class, BR.footerViewModel, R.layout.item_prediction_list_show_more);
        binding.setPredictionItemBinding(itemBinding);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if(BuildConfig.DEBUG) {
            menu.add(0, 1234, Menu.NONE, "Create random test data");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == 1234) {
            // TODO better debug option implementation
            PredictionStorage storage = PredictableApp.get(this)
                    .getPredictableComponent().getStorage();
            Random r = new Random();
            Date now = new Date();
            for (int i = 0; i < 1000; i++) {
                String title = String.format(Locale.getDefault(), "Test prediction %1$d", i);
                String description = null;
                if(r.nextFloat() > 0.7) {
                    description = String.format(Locale.getDefault(),
                            "Some long test description full of random boring text" +
                            "and so on, %1$d.", i);
                }
                Date dueDate = randomDateNearNow(r, now);

                int numConfidences = r.nextInt(8);
                List<ConfidenceStatement> confidenceStatements = new ArrayList<>(numConfidences);
                for(int k = 0; k < numConfidences; k++) {
                    confidenceStatements.add(ConfidenceStatement.create(r.nextDouble(), randomDateNearNow(r, now)));
                }
                Prediction prediction = storage.createPrediction(title, description, dueDate,
                        confidenceStatements);
                double v = r.nextDouble();
                if(v > 0.4) {
                    PredictionState state;
                    if(v < 0.7) {
                        state = PredictionState.Correct;
                    } else if (v < 0.9) {
                        state = PredictionState.Incorrect;
                    } else {
                        state = PredictionState.Invalid;
                    }
                    Judgement judgement = Judgement.create(state, randomDateNearNow(r, now));
                    prediction = prediction.toBuilder().setJudgement(judgement).build();

                    storage.updatePrediction(prediction.getId(), prediction);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private static Date randomDateNearNow(Random r, Date now) {
        int days = r.nextInt(2000) - 700;
        int minutes = r.nextInt(24 * 60) + days * 24 * 60;

        return new Date(now.getTime() + minutes * 60 * 1000);
    }

    @Override
    public void showPredictionDetails(long id) {
        PredictionDetailActivity.start(this, id);
    }

    @Override public void showAddPredictionView() {
        startActivity(new Intent(MainActivity.this, NewPredictionActivity.class));
    }

    @Override public void showFullPredictionSet(PredictionSet set) {
        PredictionListActivity.start(this, set);
    }
}
