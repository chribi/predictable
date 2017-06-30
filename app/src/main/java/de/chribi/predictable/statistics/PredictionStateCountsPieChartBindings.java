package de.chribi.predictable.statistics;


import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Color;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

import de.chribi.predictable.R;

public final class PredictionStateCountsPieChartBindings {
    private PredictionStateCountsPieChartBindings() {}

    private static final IValueFormatter intValueFormatter = new IValueFormatter() {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex,
                                        ViewPortHandler viewPortHandler) {
            return String.valueOf((int)value);
        }
    };

    @BindingAdapter(value = "predictionStateCounts")
    public static void setPredictionStateCounts(PieChart pieChart, PredictionStateCounts counts) {
        List<PieEntry> dataPoints = new ArrayList<>(4);
        List<Integer> colorList = new ArrayList<>(4);

        Context ctx = pieChart.getContext();

        if(counts.getCorrectCount() > 0) {
            dataPoints.add(new PieEntry(counts.getCorrectCount(),
                    ctx.getString(R.string.piechart_state_correct)));
            colorList.add(R.color.chart_correct_prediction);
        }

        if(counts.getIncorrectCount() > 0) {
            dataPoints.add(new PieEntry(counts.getIncorrectCount(),
                    ctx.getString(R.string.piechart_state_incorrect)));
            colorList.add(R.color.chart_incorrect_prediction);
        }

        if(counts.getInvalidCount() > 0) {
            dataPoints.add(new PieEntry(counts.getInvalidCount(),
                    ctx.getString(R.string.piechart_state_invalid)));
            colorList.add(R.color.chart_invalid_prediction);
        }
        if(counts.getOpenCount() > 0) {
            dataPoints.add(new PieEntry(counts.getOpenCount(),
                    ctx.getString(R.string.piechart_state_open)));
            colorList.add(R.color.chart_open_prediction);
        }

        int[] colors = new int[colorList.size()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = colorList.get(i);
        }

        PieDataSet dataSet = new PieDataSet(dataPoints, null);
        dataSet.setColors(colors, ctx);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(intValueFormatter);

        pieChart.setData(data);
        pieChart.getLegend().setEnabled(false);
        pieChart.setHighlightPerTapEnabled(false);
    }
}
