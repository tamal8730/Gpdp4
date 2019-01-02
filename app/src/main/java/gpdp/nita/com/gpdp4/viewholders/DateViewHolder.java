package gpdp.nita.com.gpdp4.viewholders;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.interfaces.OnDateSet;

public class DateViewHolder extends RecyclerView.ViewHolder {

    private ConstraintLayout root;
    private TextView textView, dateField;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private OnDateSet myListener;

    public DateViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.root_datevh);
        textView = itemView.findViewById(R.id.tv_datevh);
        dateField = itemView.findViewById(R.id.tv2_datevh);
    }

    public ConstraintLayout getRoot() {
        return root;
    }

    public TextView getTextView() {
        return textView;
    }

    public TextView getDateField() {
        return dateField;
    }

    public void linkPicker(Context ctx, OnDateSet listener) {
        writeDate(dateField);
        initCal(ctx).show();
        myListener = listener;
    }

    private void writeDate(final TextView writeTo) {
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                writeTo.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                myListener.onDateSet(writeTo.getText().toString());
            }
        };
    }

    private DatePickerDialog initCal(Context context) {
        Calendar calendar = Calendar.getInstance();
        return new DatePickerDialog(context, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }
}
