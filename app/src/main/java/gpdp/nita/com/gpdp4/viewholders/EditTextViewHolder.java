package gpdp.nita.com.gpdp4.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import gpdp.nita.com.gpdp4.R;

public class EditTextViewHolder extends RecyclerView.ViewHolder {

    private TextView question;
    private EditText editText;

    public EditTextViewHolder(@NonNull View itemView) {
        super(itemView);
        question = itemView.findViewById(R.id.tv_edtvh);
        editText = itemView.findViewById(R.id.edt_edtvh);
    }

    public TextView getQuestion() {
        return question;
    }

    public void setQuestion(TextView question) {
        this.question = question;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }
}
