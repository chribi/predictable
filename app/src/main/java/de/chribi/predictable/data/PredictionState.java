package de.chribi.predictable.data;

/**
 * The state of a {@link PredictionSet}.
 */
public enum PredictionState {
    /**
     * The prediction was not judged.
     */
    Open,

    /**
     * It was judged that the prediction was correct.
     */
    Correct,

    /**
     * It was judged that the prediction was incorrect.
     */
    Incorrect,

    /**
     * It was judged that the prediction was invalid (e.g. preconditions where not met,
     * or it is impossible to judge whether it is correct or incorrect).
     */
    Invalid
}
