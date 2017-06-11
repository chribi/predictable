package de.chribi.predictable.predictionlist;


import java.util.List;

import javax.inject.Inject;

import de.chribi.predictable.PredictionItemView;
import de.chribi.predictable.PredictionItemViewModel;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.predictionsets.PredictionSet;
import de.chribi.predictable.predictionsets.PredictionSetQueries;
import de.chribi.predictable.predictionsets.PredictionSetTitles;
import de.chribi.predictable.storage.PredictionStorage;

public class PredictionListViewModel implements PredictionItemView {

    private final PredictionItemView view;
    private final List<PredictionItemViewModel> predictions;
    private final String title;

    @Inject
    public PredictionListViewModel(PredictionStorage storage,
                                   PredictionItemViewModel.Factory itemViewModelFactory,
                                   PredictionSet set,
                                   PredictionSetTitles titles,
                                   PredictionSetQueries queries,
                                   PredictionItemView view) {
        this.view = view;
        List<Prediction> queryResult = storage.getPredictions(queries.getPredictionSetQuery(set));
        predictions = itemViewModelFactory.createMany(queryResult, this);
        title = titles.getPredictionSetTitle(set);
    }

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
