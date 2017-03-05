package de.chribi.predictable.bindings;


import android.databinding.BindingConversion;
import android.view.View;

public class Conversions {
    @BindingConversion
    public static int visibilityFromBoolean(boolean isVisible) {
        return isVisible ? View.VISIBLE : View.GONE;
    }
}
