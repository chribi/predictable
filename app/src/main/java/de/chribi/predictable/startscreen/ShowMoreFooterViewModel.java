package de.chribi.predictable.startscreen;


import android.support.annotation.NonNull;

import de.chribi.predictable.predictionsets.PredictionSet;

public class ShowMoreFooterViewModel {
    private final PredictionSet set;
    private final ShowMoreFooterView view;

    public ShowMoreFooterViewModel(@NonNull PredictionSet set, @NonNull ShowMoreFooterView view)
    {
        this.set = set;
        this.view = view;
    }

    public void showFullGroup() {
        view.showFullPredictionSet(set);
    }
}
