package de.chribi.predictable.statistics;


import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.support.annotation.ColorInt;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.chribi.predictable.R;

public final class CalibrationHistogramBindings {
    private CalibrationHistogramBindings() {
    }

    @BindingAdapter(value = "calibrationHistogramData")
    public static void setCalibrationHistogramData(BarChart chart,
                                                   List<CalibrationHistogramGroup> histogramGroups) {

        List<BarEntry> correctCounts = new ArrayList<>(histogramGroups.size());
        for (CalibrationHistogramGroup group : histogramGroups) {
            if(group.getTotalCount() == 0) {
                continue;
            }

            float groupMid = 100 * (group.getLowerBound() + group.getUpperBound()) / 2;
            float correct = group.getCorrectPercentage();
            float incorrect = 100 - correct;

            correctCounts.add(new BarEntry(groupMid, new float[] { correct, incorrect }));
        }
        BarDataSet dataSet = new BarDataSet(correctCounts, null);

        Context ctx = chart.getContext();
        @ColorInt int correctColor = setAlpha(ctx.getResources().getColor(R.color.chart_correct_prediction), 0xc0);
        @ColorInt int incorrectColor = setAlpha(ctx.getResources().getColor(R.color.chart_incorrect_prediction), 0xc0);
        dataSet.setColors(correctColor, incorrectColor);


        BarData data = new BarData(dataSet);
        data.setBarWidth(100.f / histogramGroups.size() - 4);
        data.setDrawValues(false);

        chart.getLegend().setCustom(new LegendEntry[] {
                new LegendEntry(ctx.getString(R.string.piechart_state_correct),
                        Legend.LegendForm.SQUARE, 14f, 2f, null, correctColor),
                new LegendEntry(ctx.getString(R.string.piechart_state_incorrect),
                        Legend.LegendForm.SQUARE, 14f, 2f, null, incorrectColor)
        });

        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getXAxis().setAxisMinimum(0.f);
        chart.getXAxis().setAxisMaximum(100.f);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        final DecimalFormat format = new DecimalFormat("###");
        IAxisValueFormatter yAxisFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return format.format(value) + " %";
            }
        };
        chart.getAxisLeft().setValueFormatter(yAxisFormatter);
        chart.getAxisRight().setValueFormatter(yAxisFormatter);

        chart.setHighlightPerTapEnabled(false);
        chart.setHighlightPerDragEnabled(false);

        chart.setData(data);
    }

    private static @ColorInt int setAlpha(@ColorInt int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

}