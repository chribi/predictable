package de.chribi.predictable.data;

/**
 * The state of a {@link Prediction}.
 */
public enum PredictionState {
    /**
     * The prediction was not judged.
     */
    Open(0),

    /**
     * It was judged that the prediction was correct.
     */
    Correct(1),

    /**
     * It was judged that the prediction was incorrect.
     */
    Incorrect(2),

    /**
     * It was judged that the prediction was invalid (e.g. preconditions where not met,
     * or it is impossible to judge whether it is correct or incorrect).
     */
    Invalid(3);

    private final int storedValue;

    PredictionState(int value) {
        storedValue = value;
    }

    /**
     * Get an int representation of this {@link PredictionState}.
     * @return A fixed int that can be converted back using
     *      {@link PredictionState#fromStoredValue(int)}.
     */
    public int getStoredValue() {
        return storedValue;
    }

    /**
     * Convert an int representation of a {@link PredictionState} back.
     * @param storedValue The stored value obtained from
     *      {@link PredictionState#getStoredValue()}
     * @return The original {@link PredictionState}.
     */
    public static PredictionState fromStoredValue(int storedValue) {
        switch (storedValue) {
            case 0:
                return Open;
            case 1:
                return Correct;
            case 2:
                return Incorrect;
            case 3:
                return Invalid;
            default:
                throw new IllegalArgumentException();
        }
    }
}
