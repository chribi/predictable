package de.chribi.predictable.util;

import com.github.mikephil.charting.components.AxisBase;

import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class DateAxisConverterTest {
    private DateAxisConverter dateAxisConverter;
    private @Mock AxisBase axis;
    private DateTimeZone timeZone;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        timeZone = DateTimeZone.forOffsetHours(-2);
        dateAxisConverter = new DateAxisConverter(axis, timeZone);
    }

    //region Tests for step size depending on maxNumSteps and range
    private void assertStepSize_afterSetMaxNumStepsAndRange(int maxNumSteps, Date minDate,
                                                            Date maxDate, long expectedStepSize) {
        dateAxisConverter.setMaxNumSteps(maxNumSteps);
        dateAxisConverter.setRange(minDate, maxDate);
        assertThat(dateAxisConverter.getStepSize(), is(equalTo(expectedStepSize)));
    }

    @Test
    public void range3Days_withMaxNumSteps8_setsStepSizeHalfDay() {
        assertStepSize_afterSetMaxNumStepsAndRange(8,
                new Date(90, 3, 10), new Date(90, 3, 13),
                DateAxisConverter.STEP_SIZE_HALF_DAY);
    }

    @Test
    public void range5Days_withMaxNumSteps8_setsStepSizeDay() {
        assertStepSize_afterSetMaxNumStepsAndRange(8,
                new Date(90, 3, 10), new Date(90, 3, 15),
                DateAxisConverter.STEP_SIZE_DAY);
    }

    @Test
    public void range9Days_withMaxNumSteps8_setsStepSizeWeek() {
        assertStepSize_afterSetMaxNumStepsAndRange(8,
                new Date(90, 3, 10), new Date(90, 3, 19),
                DateAxisConverter.STEP_SIZE_WEEK);
    }

    @Test
    public void range2Month_withMaxNumSteps10_setsStepSizeWeek() {
        assertStepSize_afterSetMaxNumStepsAndRange(10,
                new Date(90, 3, 10), new Date(90, 5, 10),
                DateAxisConverter.STEP_SIZE_WEEK);
    }

    @Test
    public void range2Month_withMaxNumSteps7_setsStepSizeMonth() {
        assertStepSize_afterSetMaxNumStepsAndRange(7,
                new Date(90, 3, 10), new Date(90, 5, 10),
                DateAxisConverter.STEP_SIZE_MONTH);
    }

    @Test
    public void range9Months_withMaxNumSteps8_setsStepSizeThreeMonths() {
        assertStepSize_afterSetMaxNumStepsAndRange(8,
                new Date(90, 2, 10), new Date(90, 11, 10),
                DateAxisConverter.STEP_SIZE_THREE_MONTHS);
    }

    @Test
    public void range26Months_withMaxNumSteps8_setsStepSizeYear() {
        assertStepSize_afterSetMaxNumStepsAndRange(8,
                new Date(90, 2, 10), new Date(92, 4, 10),
                DateAxisConverter.STEP_SIZE_YEAR);
    }

    @Test
    public void range40Years_withMaxNumSteps8_setsStepSizeYear() {
        assertStepSize_afterSetMaxNumStepsAndRange(8,
                new Date(90, 2, 10), new Date(130, 2, 10),
                DateAxisConverter.STEP_SIZE_YEAR);
    }
    //endregion

    //region Tests for alignment (0-value should correspond to a meaningful date/time)
    @Test
    public void stepSizeHalfDay_alignsWithNoon() {
        dateAxisConverter.setMaxNumSteps(8);
        dateAxisConverter.setRange(new Date(90, 2, 2, 16, 12, 22), new Date(90, 2, 3));
        Date expectedZeroDate = new Date(90, 2, 2, 14, 0, 0); // time zone offset = -2
        // check precondition
        assertThat(dateAxisConverter.getStepSize(), is(equalTo(DateAxisConverter.STEP_SIZE_HALF_DAY)));

        assertThat((double)dateAxisConverter.dateToAxisValue(expectedZeroDate),
                closeTo(0., 1e-5));
    }

    @Test
    public void stepSizeHalfDay_alignsWithMidnight() {
        dateAxisConverter.setMaxNumSteps(8);
        dateAxisConverter.setRange(new Date(90, 2, 2, 13, 12, 22), new Date(90, 2, 3));
        Date expectedZeroDate = new Date(90, 2, 2, 2, 0, 0); // time zone offset = -2
        // check precondition
        assertThat(dateAxisConverter.getStepSize(), is(equalTo(DateAxisConverter.STEP_SIZE_HALF_DAY)));

        assertThat((double)dateAxisConverter.dateToAxisValue(expectedZeroDate),
                closeTo(0., 1e-5));
    }

    @Test
    public void stepSizeDay_alignsWithMidnight() {
        dateAxisConverter.setMaxNumSteps(8);
        dateAxisConverter.setRange(new Date(90, 2, 2, 16, 12, 22), new Date(90, 2, 8));
        Date expectedZeroDate = new Date(90, 2, 2, 2, 0, 0); // time zone offset = -2
        // check precondition
        assertThat(dateAxisConverter.getStepSize(), is(equalTo(DateAxisConverter.STEP_SIZE_DAY)));

        assertThat((double)dateAxisConverter.dateToAxisValue(expectedZeroDate),
                closeTo(0., 1e-5));
    }

    @Test
    public void stepSizeWeek_alignsWithMondayMidnight() {
        dateAxisConverter.setMaxNumSteps(8);
        dateAxisConverter.setRange(new Date(90, 2, 10, 16, 12, 22), new Date(90, 3, 3));
        Date expectedZeroDate = new Date(90, 2, 5, 2, 0, 0); // time zone offset = -2
        // check precondition
        assertThat(dateAxisConverter.getStepSize(), is(equalTo(DateAxisConverter.STEP_SIZE_WEEK)));

        assertThat((double)dateAxisConverter.dateToAxisValue(expectedZeroDate),
                closeTo(0., 1e-5));
    }

    @Test
    public void stepSizeMonth_alignsWithFirstOfMonth() {
        dateAxisConverter.setMaxNumSteps(8);
        dateAxisConverter.setRange(new Date(90, 2, 10, 16, 12, 22), new Date(90, 8, 3));
        Date expectedZeroDate = new Date(90, 2, 1, 2, 0, 0); // time zone offset = -2
        // check precondition
        assertThat(dateAxisConverter.getStepSize(), is(equalTo(DateAxisConverter.STEP_SIZE_MONTH)));

        assertThat((double)dateAxisConverter.dateToAxisValue(expectedZeroDate),
                closeTo(0., 1e-5));
    }

    @Test
    public void stepSizeThreeMonth_alignsWithFirstOfQuarter() {
        dateAxisConverter.setMaxNumSteps(8);
        dateAxisConverter.setRange(new Date(90, 4, 10, 16, 12, 22), new Date(91, 4, 3));
        Date expectedZeroDate = new Date(90, 2, 1, 2, 0, 0); // time zone offset = -2
        // check precondition
        assertThat(dateAxisConverter.getStepSize(), is(equalTo(DateAxisConverter.STEP_SIZE_THREE_MONTHS)));

        assertThat((double)dateAxisConverter.dateToAxisValue(expectedZeroDate),
                closeTo(0., 1e-5));
    }

    @Test
    public void stepSizeYear_alignsWithFirstOfJanuary() {
        dateAxisConverter.setMaxNumSteps(8);
        dateAxisConverter.setRange(new Date(90, 2, 10, 16, 12, 22), new Date(98, 8, 3));
        Date expectedZeroDate = new Date(90, 0, 1, 2, 0, 0); // time zone offset = -2
        // check precondition
        assertThat(dateAxisConverter.getStepSize(), is(equalTo(DateAxisConverter.STEP_SIZE_YEAR)));

        assertThat((double)dateAxisConverter.dateToAxisValue(expectedZeroDate),
                closeTo(0., 1e-5));
    }
    //endregion

}