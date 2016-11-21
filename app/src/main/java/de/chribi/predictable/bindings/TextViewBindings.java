package de.chribi.predictable.bindings;


import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.widget.TextView;

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
            shouldSetText = true;
        }
        if(shouldSetText) {
            String newString = String.valueOf(number);
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
            result = 0;
        }
        return result;
    }
}
