package de.chribi.predictable.statistics;


import android.content.Context;
import android.databinding.BindingAdapter;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import de.chribi.predictable.R;

public final class PredictionStateCountsPieChartBindings {
    private PredictionStateCountsPieChartBindings() {}

    @BindingAdapter(value = "predictionStateCounts")
    public static void setPredictionStateCounts(PieChart pieChart, PredictionStateCounts counts) {
        List<PieEntry> dataPoints = new ArrayList<>(4);

        Context ctx = pieChart.getContext();
        dataPoints.add(new PieEntry(counts.getCorrectCount(),
                ctx.getString(R.string.piechart_state_correct)));
        dataPoints.add(new PieEntry(counts.getIncorrectCount(),
            ctx.getString(R.string.piechart_state_incorrect)));
        dataPoints.add(new PieEntry(counts.getInvalidCount(),
            ctx.getString(R.string.piechart_state_invalid)));
        dataPoints.add(new PieEntry(counts.getOpenCount(),
            ctx.getString(R.string.piechart_state_open)));

        int[] colors = new int[] {
                R.color.piechart_correct_prediction,
                R.color.piechart_incorrect_prediction,
                R.color.piechart_invalid_prediction,
                R.color.piechart_open_prediction,
        };

        PieDataSet dataSet = new PieDataSet(dataPoints, null);
        dataSet.setColors(colors, ctx);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getLegend().setEnabled(false);
    }
}
