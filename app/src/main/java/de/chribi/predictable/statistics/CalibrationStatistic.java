package de.chribi.predictable.statistics;


import java.util.ArrayList;
import java.util.List;

import de.chribi.predictable.data.ConfidenceStatement;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.data.PredictionState;

public class CalibrationStatistic implements Statistic<List<CalibrationHistogramGroup>> {
    public static CalibrationStatistic create(float[] boundaries) {
        return new CalibrationStatistic(boundaries);
    }


    private final int groupCount;
    private final float[] boundaries;
    private final int[] correct;
    private final int[] incorrect;

    /**
     * Collect statistics with histogram groups defined by groupBoundaries
     * @param groupBoundaries Array of boundaries between groups.
     *                        Lower bound for first and upper bound for last group
     *                        should not be contained in the array.
     */
    private CalibrationStatistic(float[] groupBoundaries) {
        boundaries = groupBoundaries;
        groupCount = boundaries.length + 1;
        correct = new int[groupCount];
        incorrect = new int[groupCount];
    }

    @Override public void collect(Prediction prediction) {
        PredictionState state = prediction.getJudgement().getState();
        boolean isCorrect;
        if(state == PredictionState.Correct) {
            isCorrect = true;
        } else if (state == PredictionState.Incorrect) {
            isCorrect = false;
        } else {
            return; // Prediction not judged or invalid
        }

        for (ConfidenceStatement statement : prediction.getConfidences()) {
            collectConfidence(statement.getConfidence(), isCorrect);
        }
    }

    private void collectConfidence(double confidence, boolean isCorrect) {
        int group = getGroup(confidence);
        if(isCorrect) {
            correct[group]++;
        } else {
            incorrect[group]++;
        }
    }

    private int getGroup(double confidence) {
        for (int group = 0; group < boundaries.length ; group++) {
            if(boundaries[group] > confidence) {
                // boundaries[i] is upper bound of i.th group
                return group;
            }
        }
        // confidence is greater than all group boundaries => it's in the last group
        return groupCount - 1;
    }

    @Override public List<CalibrationHistogramGroup> value() {
        List<CalibrationHistogramGroup> groups = new ArrayList<>(groupCount);
        for (int i = 0; i < groupCount; i++) {
            float lowerBound = i == 0 ? 0.0f : boundaries[i - 1];
            float upperBound = i == groupCount - 1 ? 1.0f : boundaries[i];
            groups.add(CalibrationHistogramGroup.create(
                    lowerBound, upperBound,
                    correct[i], incorrect[i]));
        }
        return groups;
    }
}
