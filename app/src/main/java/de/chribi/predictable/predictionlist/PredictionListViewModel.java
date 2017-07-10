package de.chribi.predictable.predictionlist;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.List;

import javax.inject.Inject;

import de.chribi.predictable.BR;
import de.chribi.predictable.PredictionItemView;
import de.chribi.predictable.PredictionItemViewModel;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.predictionsets.PredictionSet;
import de.chribi.predictable.predictionsets.PredictionSetQueries;
import de.chribi.predictable.predictionsets.PredictionSetTitles;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.storage.queries.PredictionQuery;

public class PredictionListViewModel extends BaseObservable implements PredictionItemView {

    private final PredictionItemView view;
    private final String title;
    private final PredictionQuery query;
    private final PredictionStorage storage;
    private final PredictionItemViewModel.Factory itemViewModelFactory;
    private List<PredictionItemViewModel> predictions;

    @Inject
    public PredictionListViewModel(PredictionStorage storage,
                                   PredictionItemViewModel.Factory itemViewModelFactory,
                                   PredictionSet set,
                                   PredictionSetTitles titles,
                                   PredictionSetQueries queries,
                                   PredictionItemView view) {
        this.view = view;
        this.itemViewModelFactory = itemViewModelFactory;
        query = queries.getPredictionSetQuery(set);
        this.storage = storage;
        title = titles.getPredictionSetTitle(set);
    }

    /**
     * Load the list of predictions for this view model.
     */
    public void loadPredictions() {
        List<Prediction> queryResult = storage.getPredictions(query);
        predictions = itemViewModelFactory.createMany(queryResult, this);
        notifyPropertyChanged(BR.predictions);
    }

    @Bindable
    public List<PredictionItemViewModel> getPredictions() {
        return predictions;
    }

    public String getTitle() {
        return title;
    }

    @Override public void showPredictionDetails(long id) {
        view.showPredictionDetails(id);
    }
}
