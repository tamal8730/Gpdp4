package gpdp.nita.com.gpdp4.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import gpdp.nita.com.gpdp4.R;

public class SpinnerViewHolder extends RecyclerView.ViewHolder {

    private Spinner spinner;
    private TextView textView;

    public SpinnerViewHolder(@NonNull View itemView) {
        super(itemView);
        spinner = itemView.findViewById(R.id.spinner_spinnervh);
        textView = itemView.findViewById(R.id.tv_spinnervh);
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public TextView getTextView() {
        return textView;
    }
}
