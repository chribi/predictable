package de.chribi.predictable.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import de.chribi.predictable.R;

@InverseBindingMethods(
        @InverseBindingMethod(
                type = DateEditView.class,
                attribute = "date"
        )
)
public class DateEditView extends TextView
        implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public interface OnDateChangedListener {
        void OnDateChanged(DateEditView view, LocalDate newDate);
    }

    @NonNull
    private LocalDate date = new LocalDate();
    private DateTimeFormatter dateFormat;

    private DatePickerDialog datePickerDialog;
    private OnDateChangedListener onDateChangedListener;

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
        updateText();
        datePickerDialog = new DatePickerDialog(context, this, date.getYear(),
                date.getMonthOfYear() - 1, date.getDayOfMonth());
        datePickerDialog.setCustomTitle(null);
    }

    private void updateText() {
        setText(date.toString(dateFormat));
    }

    @Override
    public void onClick(View v) {
        datePickerDialog.updateDate(date.getYear(),
                date.getMonthOfYear() - 1, date.getDayOfMonth());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // XXX DatePicker uses months with values 0..11
        setDate(new LocalDate(year, month + 1, dayOfMonth));
    }

    public void setDateFormat(DateTimeFormatter format) {
        this.dateFormat = format;
        updateText();
    }

    public @NonNull LocalDate getDate() {
        return date;
    }

    public void setDate(@NonNull LocalDate newDate) {
        if(!newDate.equals(date)) {
            date = newDate;
            updateText();
            if (onDateChangedListener != null) {
                onDateChangedListener.OnDateChanged(this, date);
            }
        }
    }


    @BindingAdapter("dateAttrChanged")
    public static void setDateChangeListener(DateEditView dateEditView,
                                             final InverseBindingListener onAttrChange)
    {
        if(onAttrChange != null) {
            dateEditView.onDateChangedListener = new OnDateChangedListener() {
                @Override
                public void OnDateChanged(DateEditView view, LocalDate newDate) {
                    onAttrChange.onChange();
                }
            };
        } else {
            dateEditView.onDateChangedListener = null;
        }
    }
}
