package de.chribi.predictable.util;


import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
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
    private final DateTimeFormatter formatter = DateTimeFormat.mediumDate();
    private final DateTimeZone timeZone;

    public DateAxisConverter(AxisBase axis, DateTimeZone timeZone) {
        this.axis = axis;
        this.timeZone = timeZone;
        this.firstDate = 0;
        this.stepSize = STEP_SIZE_DAY;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        LocalDateTime localDateTime = new LocalDateTime(axisValueToDate(value).getTime(), timeZone);
        return localDateTime.toString(formatter);
    }

    private static long getStepSizeForRange(Date startDate, Date endDate, int maxNumSteps) {
        long dateRange = endDate.getTime() - startDate.getTime();
        long stepSize;
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
        return stepSize;
    }

    private static Date alignDateAtStep(Date date, long stepSize, DateTimeZone timeZone) {
        LocalDateTime localDateTime = new LocalDateTime(date, timeZone);
        if (stepSize == STEP_SIZE_HALF_DAY) {
            int hour = localDateTime.getHourOfDay() >= 12 ? 12 : 0;
            return localDateTime.withTime(hour, 0, 0, 0).toDateTime(timeZone).toDate();
        }
        LocalDate localDate = localDateTime.toLocalDate();
        LocalDate result;
        if (stepSize == STEP_SIZE_DAY) {
            result = localDate;
        } else if (stepSize == STEP_SIZE_WEEK) {
            result = localDate.withDayOfWeek(DateTimeConstants.MONDAY);
        } else if (stepSize == STEP_SIZE_MONTH) {
            result = localDate.withDayOfMonth(1);
        } else if (stepSize == STEP_SIZE_THREE_MONTHS) {
            int month = localDate.getMonthOfYear();
            int january = DateTimeConstants.JANUARY;
            int alignedMonth = month - (month - january) % 3;
            result = localDate.withDayOfMonth(1).withMonthOfYear(alignedMonth);
        } else { // (stepSize == STEP_SIZE_YEAR)
            result = localDate.withDayOfMonth(1).withMonthOfYear(DateTimeConstants.JANUARY);
        }
        return result.toDateTimeAtStartOfDay(timeZone).toDate();
    }

    public void setRange(Date startDate, Date endDate) {
        stepSize = getStepSizeForRange(startDate, endDate, maxNumSteps);
        firstDate = alignDateAtStep(startDate, stepSize, timeZone).getTime();
        long dateRange = endDate.getTime() - firstDate;
        float range = ((float)dateRange) / stepSize;
        axis.setAxisMinimum(range * -0.03f);
        axis.setAxisMaximum(range * 1.03f);
    }

    public float dateToAxisValue(Date date) {
        if(stepSize > 0) {
            return ((float)(date.getTime() - firstDate)) / stepSize;
        } else {
            // TODO handle variable step size
            return 0.f;
        }
    }

    private Date axisValueToDate(float value) {
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
