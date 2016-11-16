package de.chribi.predictable.views;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import de.chribi.predictable.R;

public class TimeEditView extends TextView
        implements TimePickerDialog.OnTimeSetListener, View.OnClickListener{

    private int hours, minutes;
    private DateFormat timeFormat;

    private TimePickerDialog timePickerDialog;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

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
        timePickerDialog = new TimePickerDialog(context, this, hours, minutes, true);
        timePickerDialog.setCustomTitle(null);
    }

    private void updateText() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        setText(timeFormat.format(calendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        timePickerDialog.updateTime(hours, minutes);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hours = hourOfDay;
        this.minutes = minute;
        if(onTimeSetListener != null) {
            onTimeSetListener.onTimeSet(view, hourOfDay, minute);
        }
        updateText();
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
        updateText();
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
        updateText();
    }

    public void setTimeFormat(DateFormat format) {
        this.timeFormat = format;
        updateText();
    }

    public void setTime(Date dateTime) {
        Calendar dateTimeCal = Calendar.getInstance();
        dateTimeCal.setTime(dateTime);
        this.hours = dateTimeCal.get(Calendar.HOUR_OF_DAY);
        this.minutes = dateTimeCal.get(Calendar.MINUTE);
        updateText();
    }
}
