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

import de.chribi.predictable.data.PredictedEvent;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.databinding.ActivityMainBinding;
import de.chribi.predictable.newprediction.NewPredictionActivity;
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
        int numTestPredictions = 1000;
        for(int i = 0; i < numTestPredictions; i++) {
            ArrayList<Prediction> predictionList = new ArrayList<>();
            predictionList.add(new Prediction((double)i / numTestPredictions,
                    new Date(i * 12 * 60 * 60 * 1000L)));
            PredictedEvent event = new PredictedEvent(i, "Test " + String.valueOf(i), "",
                    PredictionState.values()[ i % 4 ], new Date(i * 24 * 60 * 60 * 1000L),
                    predictionList);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
