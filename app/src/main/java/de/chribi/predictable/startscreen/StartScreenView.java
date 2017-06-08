package de.chribi.predictable.startscreen;


import de.chribi.predictable.PredictionItemView;
import de.chribi.predictable.predictionsets.PredictionSet;

public interface StartScreenView extends PredictionItemView, ShowMoreFooterView {
    void showAddPredictionView();
}
