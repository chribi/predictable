package de.chribi.predictable.data;


import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import java.util.Date;

@AutoValue
public abstract class Judgement {
    /**
     * Create a new Judgement.  Use {@link #Open} instead of creating a
     * new Judgement with state {@link PredictionState#Open}
     * @param state The actual judgement.
     * @param date When the judgement was made.
     */
    public static Judgement create(@NonNull PredictionState state, @NonNull Date date) {
        if(state == PredictionState.Open) {
            // Ensure that there is only one true open Judgement
            return Judgement.Open;
        } else {
            return new AutoValue_Judgement(state, date);
        }
    }

    /**
     * Constant value for any open (i.e. unjudged) Judgements.  You can always compare with this
     * value for checking if a Judgement is open, instead of checking {@link Judgement#getState()}.
     */
    public static final Judgement Open = new AutoValue_Judgement(PredictionState.Open, new Date(0));

    public abstract @NonNull PredictionState getState();

    /**
     * The date the judgement was made.  This has undefined semantics for an 'Open' judgement.
     * WARNING: Any modification of the resulting Date might lead to inconsistent behaviour!
     */
    public abstract @NonNull Date getDate();
}
