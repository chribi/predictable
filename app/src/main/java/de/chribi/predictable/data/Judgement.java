package de.chribi.predictable.data;


import android.support.annotation.NonNull;

import java.util.Date;

public class Judgement {
    private PredictionState state;
    private Date date;

    public Judgement(PredictionState state, @NonNull Date date) {
        this.state = state;
        this.date = date;
    }

    public PredictionState getState() {
        return state;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Judgement judgement = (Judgement) o;

        if (state != judgement.state)
            return false;
        return date != null ? date.equals(judgement.date) : judgement.date == null;

    }

    @Override
    public int hashCode() {
        int result = state != null ? state.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
