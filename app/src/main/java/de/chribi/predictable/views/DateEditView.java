package de.chribi.predictable.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.chribi.predictable.R;

public class DateEditView extends TextView
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private int year, month, dayOfMonth;
    private DateFormat dateFormat;

    private DatePickerDialog datePickerDialog;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    public DateEditView(Context context) {
        super(context);
        setUpView(context);
    }

    public DateEditView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.dialogEditViewStyle);
        setUpView(context);
    }

    public DateEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpView(context);
    }

    private void setUpView(Context context) {
        this.setOnClickListener(this);
        datePickerDialog = new DatePickerDialog(context, this, year, month, dayOfMonth);
        datePickerDialog.setCustomTitle(null);
    }

    private void updateText() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        datePickerDialog.updateDate(year, month, dayOfMonth);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        if(onDateSetListener != null) {
            onDateSetListener.onDateSet(view, year, month, dayOfMonth);
        }
        updateText();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
        updateText();
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
        updateText();
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
        updateText();
    }

    public void setDateFormat(DateFormat format) {
        this.dateFormat = format;
        updateText();
    }

    public void setDate(Date dateTime) {
        Calendar dateTimeCal = Calendar.getInstance();
        dateTimeCal.setTime(dateTime);
        this.year = dateTimeCal.get(Calendar.YEAR);
        this.month = dateTimeCal.get(Calendar.MONTH);
        this.dayOfMonth = dateTimeCal.get(Calendar.DAY_OF_MONTH);
        updateText();
    }
}
