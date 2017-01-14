package de.chribi.predictable;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import de.chribi.predictable.data.Judgement;
import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.databinding.ActivityMainBinding;
import de.chribi.predictable.newprediction.NewPredictionActivity;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.util.DateTimeProvider;
import de.chribi.predictable.util.DefaultDateTimeHandler;
import me.tatarka.bindingcollectionadapter.ItemView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding
                = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_prediction);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewPredictionActivity.class));
            }
        });

        ObservableList<PredictionItemViewModel> events = new ObservableArrayList<>();
        DateTimeProvider dateTimeProvider = new DefaultDateTimeHandler();
        DefaultPredictionItemStringProvider strings = new DefaultPredictionItemStringProvider(this);
        PredictionStorage storage = PredictableApp.get(this).getPredictableComponent().getStorage();
        for (PredictedEvent event : storage.getPredictedEvents()) {
            PredictionItemViewModel vm = new PredictionItemViewModel(dateTimeProvider, strings);
            vm.setPredictedEvent(event);
            events.add(vm);
        }

        binding.setPredictions(events);
        binding.setPredictionItemView(ItemView.of(BR.itemViewModel, R.layout.item_prediction));

        // RecyclerView predictions =  (RecyclerView) findViewById(R.id.list_predictions);
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

                int numPredictions = r.nextInt(8);
                List<Prediction> predictions = new ArrayList<>(numPredictions);
                for(int k = 0; k < numPredictions; k++) {
                    predictions.add(new Prediction(r.nextDouble(), randomDateNearNow(r, now)));
                }
                long predictionId = storage.createPredictedEvent(title, description, dueDate,
                        predictions).getId();
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
                    Judgement judgement = new Judgement(state, randomDateNearNow(r, now));

                    storage.updatePredictedEvent(predictionId,
                            new PredictedEvent(predictionId, title, description, judgement,
                                    dueDate, predictions ));
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
}
