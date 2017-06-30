package de.chribi.predictable.statistics;


import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CalibrationHistogramGroup {
    public static CalibrationHistogramGroup create(float lowerBound, float upperBound,
                                                   int correct, int incorrect) {
        return new AutoValue_CalibrationHistogramGroup(lowerBound, upperBound, correct, incorrect);
    }
    public abstract float getLowerBound();
    public abstract float getUpperBound();
    public abstract int getCorrectCount();
    public abstract int getIncorrectCount();
    public int getTotalCount() {
        return getCorrectCount() + getIncorrectCount();
    }

    public float getCorrectPercentage() {
        int total = getTotalCount();
        int correct = getCorrectCount();
        return (100.f * correct) / total;
    }
}
