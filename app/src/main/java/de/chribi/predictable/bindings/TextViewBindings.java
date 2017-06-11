package de.chribi.predictable.bindings;


import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.widget.TextView;

import de.chribi.predictable.R;
import de.chribi.predictable.data.PredictionState;

public class TextViewBindings {

    // Adapters from
    // https://halfthought.wordpress.com/2016/05/27/two-way-data-binding-conversions/
    // Binding should in theory respect the current locale, but because of a bug in
    // android:inputType = "numberDecimal" for EditText
    // (https://code.google.com/p/android/issues/detail?id=2626) this would not be really useful.
    @BindingAdapter("android:text")
    public static void setText(TextView textView, double number) {
        boolean shouldSetText;
        try {
            double currentNumber = Double.parseDouble(textView.getText().toString());
            shouldSetText = currentNumber != number;
        } catch (NumberFormatException e) {
            shouldSetText = !Double.isNaN(number);
        }
        if(shouldSetText) {
            String newString = Double.isNaN(number) ? "" : String.valueOf(number); // NON-NLS
            textView.setText(newString);
        }
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static double getText(TextView textView)
    {
        double result;
        try {
            result = Double.parseDouble(textView.getText().toString());
        } catch (NumberFormatException e) {
            result = Double.NaN;
        }
        return result;
    }

    @BindingAdapter(value = "predictionState")
    public static void setPredictionState(TextView view, PredictionState state) {
        @ColorRes int colorResource;
        switch (state) {
            case Open:
                colorResource = R.color.normal_text;
                break;
            case Correct:
                colorResource = R.color.correct_prediction;
                break;
            case Incorrect:
                colorResource = R.color.incorrect_prediction;
                break;
            case Invalid:
                colorResource = R.color.invalid_prediction;
                break;
            default:
                throw new IllegalArgumentException("Unknown prediction state.");
        }
        Context ctx = view.getContext();
        @ColorInt int textColor = ctx.getResources().getColor(colorResource);

        view.setTextColor(textColor);

        int newPaintFlags;
        if(state == PredictionState.Invalid) {
            newPaintFlags = view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG;
        } else {
            newPaintFlags = view.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG);
        }
        view.setPaintFlags(newPaintFlags);
    }
}
