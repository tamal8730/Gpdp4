package gpdp.nita.com.gpdp4.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import gpdp.nita.com.gpdp4.R;

public class RadioGroupViewHolder extends RecyclerView.ViewHolder {

    private RadioGroup radioGroup;
    private TextView textView;
    private RadioButton yes, no;

    public RadioGroupViewHolder(@NonNull View itemView) {
        super(itemView);
        radioGroup = itemView.findViewById(R.id.rg_rbvh);
        textView = itemView.findViewById(R.id.tv_rbvh);
        yes = itemView.findViewById(R.id.rb0_rbvh);
        no = itemView.findViewById(R.id.rb1_rbvh);
    }

    public RadioGroup getRadioGroup() {
        return radioGroup;
    }

    public TextView getTextView() {
        return textView;
    }

    public RadioButton getYes() {
        return yes;
    }

    public RadioButton getNo() {
        return no;
    }

}
