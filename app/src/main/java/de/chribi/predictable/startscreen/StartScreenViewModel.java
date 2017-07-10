package de.chribi.predictable.startscreen;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.chribi.predictable.BR;
import de.chribi.predictable.PredictionItemViewModel;
import de.chribi.predictable.PredictionItemView;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.predictionsets.PredictionSet;
import de.chribi.predictable.predictionsets.PredictionSetQueries;
import de.chribi.predictable.predictionsets.PredictionSetTitles;
import de.chribi.predictable.storage.PredictionStorage;

public class StartScreenViewModel extends BaseObservable
        implements PredictionItemView, ShowMoreFooterView {
    private static final int GROUP_LIMIT = 8;
    private static final int GROUP_LIMIT_EXTENDED = 12;

    private final StartScreenView view;
    private final PredictionStorage storage;
    private final PredictionSet[] groups;
    private final PredictionSetTitles titles;
    private final PredictionSetQueries queries;
    private final PredictionItemViewModel.Factory itemViewModelFactory;
    private List<Object> groupedPredictions;


    @Inject
    public StartScreenViewModel(PredictionStorage storage, PredictionSetTitles setTitles,
                                PredictionSetQueries setQueries,
                                StartScreenView view,
                                PredictionItemViewModel.Factory itemViewModelFactory) {

        this.view = view;
        this.storage = storage;
        this.titles = setTitles;
        this.queries = setQueries;
        this.itemViewModelFactory = itemViewModelFactory;

        groups = new PredictionSet[]{
                PredictionSet.OVERDUE_PREDICTIONS,
                PredictionSet.UPCOMING_PREDICTIONS,
                PredictionSet.JUDGED_PREDICTIONS
        };

        loadPredictions();
    }

    /**
     * Load the prediction groups
     */
    public void loadPredictions() {
        groupedPredictions = new ArrayList<>();
        for (PredictionSet group : groups) {
            List<Prediction> groupPredictions
                    = storage.getPredictions(queries.getPredictionSetQuery(group));
            if(groupPredictions.size() == 0) {
                continue;
            }
            groupedPredictions.add(titles.getPredictionSetTitle(group));
            if(groupPredictions.size() <= GROUP_LIMIT_EXTENDED) {
                groupedPredictions.addAll(itemViewModelFactory.createMany(groupPredictions, this));
            } else {
                List<Prediction> predictionsToShow = groupPredictions.subList(0, GROUP_LIMIT);
                groupedPredictions.addAll(itemViewModelFactory.createMany(predictionsToShow, this));
                groupedPredictions.add(new ShowMoreFooterViewModel(group, this));
            }
        }
        notifyPropertyChanged(BR.groupedPredictions);
    }

    @Bindable
    public List<Object> getGroupedPredictions() {
        return groupedPredictions;
    }

    public void startAddPrediction() {
        view.showAddPredictionView();
    }

    @Override public void showPredictionDetails(long id) {
        view.showPredictionDetails(id);
    }

    @Override public void showFullPredictionSet(PredictionSet set) {
        view.showFullPredictionSet(set);
    }
}
