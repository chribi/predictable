package de.chribi.predictable.statistics;


import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.TableLayout;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.chribi.predictable.R;
import de.chribi.predictable.databinding.TablerowConfidenceTableBinding;

public final class CalibrationHistogramBindings {
    private CalibrationHistogramBindings() {
    }

    @BindingAdapter(value = "calibrationHistogramData")
    public static void setCalibrationHistogramData(CombinedChart chart,
                                                   List<CalibrationHistogramGroup> histogramGroups) {

        Context ctx = chart.getContext();
        @ColorInt int correctColor = setAlpha(ctx.getResources().getColor(R.color.chart_correct_prediction), 0xc0);
        @ColorInt int incorrectColor = setAlpha(ctx.getResources().getColor(R.color.chart_incorrect_prediction), 0xc0);
        float lowerBound = 100.f * histogramGroups.get(0).getLowerBound();
        float upperBound = 100.f * histogramGroups.get(histogramGroups.size() - 1).getUpperBound();

        CombinedData combinedData = new CombinedData();
        combinedData.setData(getCalibrationBarData(histogramGroups, correctColor, incorrectColor,
                lowerBound, upperBound));
        combinedData.setData(getPerfectCalibrationLineData(lowerBound, upperBound,
                        Color.argb(180, 0, 0, 240)));
        configureCalibrationChart(chart, correctColor, incorrectColor, lowerBound,
                upperBound);
        chart.setData(combinedData);
        chart.invalidate();
    }

    @NonNull
    private static BarData getCalibrationBarData(List<CalibrationHistogramGroup> histogramGroups,
                                                 int correctColor, int incorrectColor,
                                                 float lowerBound, float upperBound) {
        BarDataSet dataSet = getCalibrationDataSet(histogramGroups);
        dataSet.setColors(correctColor, incorrectColor);
        BarData data = new BarData(dataSet);
        data.setBarWidth((upperBound - lowerBound) / histogramGroups.size() - 4);
        data.setDrawValues(false);
        return data;
    }

    @NonNull
    private static BarDataSet getCalibrationDataSet(List<CalibrationHistogramGroup> histogramGroups) {
        List<BarEntry> correctCounts = new ArrayList<>(histogramGroups.size());
        for (CalibrationHistogramGroup group : histogramGroups) {
            if (group.getTotalCount() == 0) {
                continue;
            }

            float groupMid = 100 * (group.getLowerBound() + group.getUpperBound()) / 2;
            float correct = group.getCorrectPercentage();
            float incorrect = 100 - correct;

            correctCounts.add(new BarEntry(groupMid, new float[]{ correct, incorrect }));
        }
        return new BarDataSet(correctCounts, null);
    }

    @NonNull
    private static LineData getPerfectCalibrationLineData(float min, float max, @ColorInt int color) {
        List<Entry> entries = new ArrayList<>(2);
        entries.add(new Entry(min, min));
        entries.add(new Entry(max, max));

        LineDataSet dataSet = new LineDataSet(entries, null);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setColor(color);
        dataSet.enableDashedLine(10f, 10f, 5f);
        return new LineData(dataSet);
    }

    private static void configureCalibrationChart(CombinedChart chart, @ColorInt int correctColor,
                                                  @ColorInt int incorrectColor, float lowerBound,
                                                  float upperBound) {
        Context ctx = chart.getContext();
        chart.getLegend().setCustom(new LegendEntry[]{
                new LegendEntry(ctx.getString(R.string.piechart_state_correct),
                        Legend.LegendForm.SQUARE, 14f, 2f, null, correctColor),
                new LegendEntry(ctx.getString(R.string.piechart_state_incorrect),
                        Legend.LegendForm.SQUARE, 14f, 2f, null, incorrectColor)
        });

        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getXAxis().setAxisMinimum(lowerBound);
        chart.getXAxis().setAxisMaximum(upperBound);
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
        chart.setScaleEnabled(false);
    }

    @BindingAdapter(value = "calibrationHistogramData")
    public static void setCalibrationHistogramData(TableLayout table,
                                                   List<CalibrationHistogramGroup> histogramGroups) {
        Context ctx = table.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        table.removeViews(1, table.getChildCount() - 1);
        for (CalibrationHistogramGroup group : histogramGroups) {
            TablerowConfidenceTableBinding binding =
                    DataBindingUtil.inflate(inflater, R.layout.tablerow_confidence_table, table, true);
            binding.setRowData(group);
        }
    }

    private static @ColorInt int setAlpha(@ColorInt int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

}
