package de.chribi.testutils;


import android.widget.DatePicker;
import android.widget.TimePicker;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.PickerActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.equalTo;

public class DialogUtils {
    private DialogUtils() {}

    public static void setDatePickerDialogAndConfirm(int year, int month, int day) {
        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(setDate(year, month, day));
        onView(withId(android.R.id.button1))
                .perform(click());
    }

    public static void setTimePickerDialogAndConfirm(int hour, int minute) {
        onView(withClassName(equalTo(TimePicker.class.getName())))
                .perform(setTime(hour, minute));
        onView(withId(android.R.id.button1))
                .perform(click());
    }
}
