package de.chribi.predictable.predictionlist;


import java.util.List;

import javax.inject.Inject;

import de.chribi.predictable.PredictionItemView;
import de.chribi.predictable.PredictionItemViewModel;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.storage.queries.PredictionQuery;

public class PredictionListViewModel implements PredictionItemView {

    private PredictionItemView view;
    private PredictionStorage storage;
    private PredictionItemViewModel.Factory itemViewModelFactory;
    private List<PredictionItemViewModel> predictions;

    @Inject
    public PredictionListViewModel(PredictionStorage storage,
                                   PredictionItemViewModel.Factory itemViewModelFactory) {
        this.storage = storage;
        this.itemViewModelFactory = itemViewModelFactory;
    }

    public void setPredictionQuery(PredictionQuery query) {
        List<Prediction> queryResult = storage.getPredictions(query);
        predictions = itemViewModelFactory.createMany(queryResult, this);
    }

    public List<PredictionItemViewModel> getPredictions() {
        return predictions;
    }

    public void setView(PredictionItemView view) {
        this.view = view;
    }

    @Override public void showPredictionDetails(long id) {
        if(view != null) {
            view.showPredictionDetails(id);
        }
    }
}
