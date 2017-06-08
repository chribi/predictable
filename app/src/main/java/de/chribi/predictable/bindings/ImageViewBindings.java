package de.chribi.predictable.bindings;


import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import de.chribi.predictable.R;
import de.chribi.predictable.data.PredictionState;

public class ImageViewBindings {
    @BindingAdapter(value = "predictionState")
    public static void setPredictionState(ImageView view, PredictionState state) {
        @DrawableRes int iconResource;
        @ColorRes int colorResource;
        switch (state) {
            case Open:
                // no icon for open predictions
                view.setImageDrawable(null);
                return;
            case Correct:
                iconResource = R.drawable.ic_check_white_24dp;
                colorResource = R.color.correct_prediction;
                break;
            case Incorrect:
                iconResource = R.drawable.ic_clear_white_24dp;
                colorResource = R.color.incorrect_prediction;
                break;
            case Invalid:
                iconResource = R.drawable.ic_block_white_24dp;
                colorResource = R.color.invalid_prediction;
                break;
            default:
                throw new IllegalArgumentException("Unknown prediction state.");
        }
        Context ctx = view.getContext();

        Drawable icon = ctx.getResources().getDrawable(iconResource);
        @ColorInt int tintColor = ctx.getResources().getColor(colorResource);
        view.setImageDrawable(icon);
        view.setColorFilter(tintColor);
    }
}
