package de.chribi.predictable.startscreen;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.chribi.predictable.PredictionItemViewModel;
import de.chribi.predictable.PredictionItemView;
import de.chribi.predictable.data.PredictionState;
import de.chribi.predictable.storage.PredictionStorage;
import de.chribi.predictable.storage.queries.PredictionQuery;
import de.chribi.predictable.util.DateTimeProvider;

import static de.chribi.predictable.storage.queries.PredictionField.DUE_DATE;
import static de.chribi.predictable.storage.queries.PredictionField.JUDGEMENT_DATE;
import static de.chribi.predictable.storage.queries.PredictionField.STATE;

public class StartScreenViewModel implements PredictionItemView {
    private List<PredictionItemViewModel> overduePredictions;
    private List<PredictionItemViewModel> upcomingPredictions;
    private List<PredictionItemViewModel> resolvedPredictions;

    private List<Object> groupedPredictions = new ArrayList<>();

    private StartScreenView view;


    @Inject
    public StartScreenViewModel(PredictionStorage storage, DateTimeProvider dateTimeProvider,
                                PredictionItemViewModel.Factory itemViewModelFactory) {
        Date now = dateTimeProvider.getCurrentDateTime();

        overduePredictions = itemViewModelFactory.createMany(storage.getPredictions(
                PredictionQuery
                        .where(
                                STATE.equalTo(PredictionState.Open),
                                DUE_DATE.before(now))
                        .orderBy(
                                DUE_DATE.Ascending)), this);

        upcomingPredictions = itemViewModelFactory.createMany(storage.getPredictions(
                PredictionQuery
                        .where(
                                STATE.equalTo(PredictionState.Open),
                                DUE_DATE.after(now))
                        .orderBy(
                                DUE_DATE.Ascending)), this);

        resolvedPredictions = itemViewModelFactory.createMany(storage.getPredictions(
                PredictionQuery
                        .where(
                                STATE.notEqualTo(PredictionState.Open))
                        .orderBy(
                                JUDGEMENT_DATE.Descending)), this);

        groupedPredictions = new ArrayList<>();
        groupedPredictions.add("Overdue");
        groupedPredictions.addAll(overduePredictions);
        groupedPredictions.add("Upcoming");
        groupedPredictions.addAll(upcomingPredictions);
        groupedPredictions.add("Past");
        groupedPredictions.addAll(resolvedPredictions);
    }

    public List<PredictionItemViewModel> getOverduePredictions() {
        return overduePredictions;
    }

    public List<PredictionItemViewModel> getUpcomingPredictions() {
        return upcomingPredictions;
    }

    public List<PredictionItemViewModel> getResolvedPredictions() {
        return resolvedPredictions;
    }

    public List<Object> getGroupedPredictions() {
        return groupedPredictions;
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
}
