package de.chribi.predictable.util;


import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateAxisConverter implements IAxisValueFormatter {

    public static final long STEP_SIZE_HALF_DAY = 12 * 60 * 60 * 1000;
    public static final long STEP_SIZE_DAY = 2 * STEP_SIZE_HALF_DAY;
    public static final long STEP_SIZE_WEEK = 7 * STEP_SIZE_DAY;
    public static final long STEP_SIZE_MONTH = 30 * STEP_SIZE_DAY;
    public static final long STEP_SIZE_THREE_MONTHS = 3 * STEP_SIZE_MONTH;
    public static final long STEP_SIZE_YEAR = 365 * STEP_SIZE_DAY;

    private int maxNumSteps = 8;

    private long stepSize;
    private long firstDate;
    private final AxisBase axis;

    // Date time handling, currently hardcoded
    private DateTimeFormatter formatter = DateTimeFormat.mediumDateTime();
    private DateTimeZone timeZone;

    public DateAxisConverter(AxisBase axis, DateTimeZone timeZone) {
        this.axis = axis;
        this.timeZone = timeZone;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Date vd = axisValueToDate(value);
        Log.d("Chart", String.format("Convert value %f to %d. (%s)", value, vd.getTime(), vd.toString()));

        LocalDateTime localDateTime = new LocalDateTime(axisValueToDate(value).getTime(), timeZone);
        return localDateTime.toString(formatter);
    }

    public void setRange(Date minDate, Date maxDate) {
        // TODO obtain a reasonable step size for axis
        //Log.d("Chart", String.format("Set range from %d (%s) to %d (%s).",
        //        minDate.getTime(), minDate.toString(), maxDate.getTime(), maxDate.toString()));
        long dateRange = maxDate.getTime() - minDate.getTime();
        if (dateRange < maxNumSteps * STEP_SIZE_HALF_DAY) {
            stepSize = STEP_SIZE_HALF_DAY;
        } else if (dateRange < maxNumSteps * STEP_SIZE_DAY) {
            stepSize = STEP_SIZE_DAY;
        } else if(dateRange < maxNumSteps * STEP_SIZE_WEEK) {
            stepSize = STEP_SIZE_WEEK;
        } else if (dateRange < maxNumSteps * STEP_SIZE_MONTH) {
            stepSize = STEP_SIZE_MONTH;
        } else if (dateRange < maxNumSteps * STEP_SIZE_THREE_MONTHS) {
            stepSize = STEP_SIZE_THREE_MONTHS;
        } else {
            stepSize = STEP_SIZE_YEAR;
        }
        // TODO align at day/month
        firstDate = minDate.getTime();
        float range = ((float)dateRange) / stepSize;
        axis.setAxisMinimum(range * -0.03f);
        axis.setAxisMaximum(range * 1.03f);
        //Log.d("Chart", "Range: " + String.valueOf(range));
    }

    public float dateToAxisValue(Date date) {
        if(stepSize > 0) {
            return ((float)(date.getTime() - firstDate)) / stepSize;
        } else {
            // TODO handle variable step size
            return 0.f;
        }
    }

    public Date axisValueToDate(float value) {
        // TODO handle variable step size
        return new Date((long)(value * stepSize) + firstDate);
    }

    public int getMaxNumSteps() {
        return maxNumSteps;
    }

    public void setMaxNumSteps(int maxNumSteps) {
        this.maxNumSteps = maxNumSteps;
    }

    public long getStepSize() {
        return stepSize;
    }

}
