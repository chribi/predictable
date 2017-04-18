package de.chribi.predictable.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.joda.time.DateTimeZone;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.chribi.predictable.R;
import de.chribi.predictable.data.Prediction;
import de.chribi.predictable.util.DateAxisConverter;

/**
 * Class for drawing a confidence time line.
 */
public class ConfidenceLineChart extends LineChart {


    private LineDataSet dataSet;
    private
    @ColorInt
    int graphColor;

    private DateAxisConverter dateAxisConverter;

    public ConfidenceLineChart(Context context) {
        this(context, null);
    }

    public ConfidenceLineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConfidenceLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        dataSet = new LineDataSet(new ArrayList<Entry>(), null);
        readXmlAttributes(context, attrs, defStyle);
        setupGraph();
    }

    private void setupGraph() {
        // disable touch interactions and some unwanted chart parts
        setScaleEnabled(false);
        getLegend().setEnabled(false);
        getDescription().setEnabled(false);

        // Configure x-Axis
        XAxis xAxis = getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(-60);
        dateAxisConverter = new DateAxisConverter(xAxis, DateTimeZone.getDefault());
        xAxis.setValueFormatter(dateAxisConverter);

        // Configure y-Axis
        YAxis yAxis = getAxisLeft();
        yAxis.setAxisMinimum(0.f);
        yAxis.setAxisMaximum(1.01f);


        LimitLine midLine = new LimitLine(.5f);
        midLine.enableDashedLine(8, 8, 0);
        yAxis.addLimitLine(midLine);

        final DecimalFormat format = new DecimalFormat("###");
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return format.format(100.f * value) + " %";
            }
        });

        LineData lineData = new LineData(dataSet);
        setData(lineData);
        lineData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return format.format(100.f * value) + " %";
            }
        });
    }

    public
    @ColorInt
    int getGraphColor() {
        return graphColor;
    }

    public void setGraphColor(@ColorInt int graphColor) {
        this.graphColor = graphColor;
        dataSet.setColor(graphColor);
        dataSet.setCircleColor(graphColor);
    }

    /**
     * Set a list of predictions to show in this line chart.
     *
     * @param predictions An ordered list of predictions.  The first element should be the earliest
     *                    prediction, the last element should be the latest prediction.
     */
    public void setPredictions(List<Prediction> predictions) {
        dataSet.clear();
        if (predictions != null && predictions.size() > 0) {
            Date minDate = predictions.get(0).getCreationDate();
            Date maxDate = predictions.get(predictions.size() - 1).getCreationDate();
            dateAxisConverter.setRange(minDate, maxDate);

            for (Prediction prediction : predictions) {
                final float xValue = dateAxisConverter.dateToAxisValue(prediction.getCreationDate());
                Entry entry = new Entry(xValue, (float) prediction.getConfidence());
                Log.d("Chart", "Entry: " + String.valueOf(entry.getX()) + ", " + String.valueOf(entry.getY()));
                dataSet.addEntry(entry);
            }
        }
        notifyDataSetChanged();
        invalidate();
    }


    private void readXmlAttributes(Context context, AttributeSet attrs, int defStyle) {
        // read in properties set from the XML
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ConfidenceLineChart, defStyle, 0);
        setGraphColor(a.getColor(R.styleable.ConfidenceLineChart_graphColor, 0));
        a.recycle();
    }

}
