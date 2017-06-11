package de.chribi.predictable.startscreen;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.chribi.predictable.PredictionItemViewModel;
import de.chribi.predictable.PredictionItemView;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.predictionsets.PredictionSet;
import de.chribi.predictable.predictionsets.PredictionSetQueries;
import de.chribi.predictable.predictionsets.PredictionSetTitles;
import de.chribi.predictable.storage.PredictionStorage;

public class StartScreenViewModel implements PredictionItemView, ShowMoreFooterView {
    private static final int GROUP_LIMIT = 8;
    private static final int GROUP_LIMIT_EXTENDED = 12;

    private List<Object> groupedPredictions = new ArrayList<>();

    private StartScreenView view;


    @Inject
    public StartScreenViewModel(PredictionStorage storage, PredictionSetTitles setTitles,
                                PredictionSetQueries setQueries,
                                PredictionItemViewModel.Factory itemViewModelFactory) {

        PredictionSet[] groups = new PredictionSet[]{
                PredictionSet.OVERDUE_PREDICTIONS,
                PredictionSet.UPCOMING_PREDICTIONS,
                PredictionSet.JUDGED_PREDICTIONS
        };

        groupedPredictions = new ArrayList<>();
        for (PredictionSet group : groups) {
            List<Prediction> groupPredictions
                    = storage.getPredictions(setQueries.getPredictionSetQuery(group));
            if(groupPredictions.size() == 0) {
                continue;
            }
            groupedPredictions.add(setTitles.getPredictionSetTitle(group));
            if(groupPredictions.size() <= GROUP_LIMIT_EXTENDED) {
                groupedPredictions.addAll(itemViewModelFactory.createMany(groupPredictions, this));
            } else {
                List<Prediction> predictionsToShow = groupPredictions.subList(0, GROUP_LIMIT);
                groupedPredictions.addAll(itemViewModelFactory.createMany(predictionsToShow, this));
                groupedPredictions.add(new ShowMoreFooterViewModel(group, this));
            }
        }
    }

    public List<Object> getGroupedPredictions() {
        return groupedPredictions;
    }

    public void showFullGroup(PredictionSet set) {
        if(view != null) {
            view.showFullPredictionSet(set);
        }
    }

    public void setView(StartScreenView view) {
        this.view = view;
    }

    public void startAddPrediction() {
        if(view != null) {
            view.showAddPredictionView();
        }
    }

    @Override public void showPredictionDetails(long id) {
        if(view != null) {
            view.showPredictionDetails(id);
        }
    }

    @Override public void showFullPredictionSet(PredictionSet set) {
        if(view != null) {
            view.showFullPredictionSet(set);
        }
    }
}