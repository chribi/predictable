package de.chribi.predictable.views;

import android.app.TimePickerDialog;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import de.chribi.predictable.R;

@InverseBindingMethods(
        @InverseBindingMethod(
                type = TimeEditView.class,
                attribute = "time"
        )
)
public class TimeEditView extends TextView
        implements TimePickerDialog.OnTimeSetListener, View.OnClickListener{
    public interface OnTimeChangedListener {
        void OnTimeChanged(TimeEditView view, LocalTime newTime);
    }

    @NonNull
    private LocalTime time = new LocalTime();
    private DateTimeFormatter timeFormat;

    private TimePickerDialog timePickerDialog;
    private OnTimeChangedListener onTimeChangedListener;

    public TimeEditView(Context context) {
        super(context);
        setUpView(context);
    }

    public TimeEditView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.dialogEditViewStyle);
        setUpView(context);
    }

    public TimeEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpView(context);
    }

    private void setUpView(Context context) {
        this.setOnClickListener(this);
        int hours = time.getHourOfDay();
        int minutes = time.getMinuteOfHour();
        timePickerDialog = new TimePickerDialog(context, this, hours, minutes, true);
        timePickerDialog.setCustomTitle(null);
    }

    private void updateText() {
        setText(time.toString(timeFormat));
    }

    @Override
    public void onClick(View v) {
        timePickerDialog.updateTime(time.getHourOfDay(), time.getMinuteOfHour());
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        setTime(new LocalTime(hourOfDay, minute));
    }

    public @NonNull LocalTime getTime() {
        return time;
    }

    public void setTime(@NonNull LocalTime newTime) {
        if(!newTime.equals(time)) {
            time = newTime;
            updateText();
            if(onTimeChangedListener != null) {
                onTimeChangedListener.OnTimeChanged(this, time);
            }
        }
    }

    public void setTimeFormat(DateTimeFormatter format) {
        this.timeFormat = format;
        updateText();
    }

    @BindingAdapter("timeAttrChanged")
    public static void setTimeChangeListener(TimeEditView view,
                                             final InverseBindingListener onAttrChange) {
        if(onAttrChange != null) {
            view.onTimeChangedListener = new OnTimeChangedListener() {
                @Override
                public void OnTimeChanged(TimeEditView view, LocalTime newTime) {
                    onAttrChange.onChange();
                }
            };
        }
    }
}
