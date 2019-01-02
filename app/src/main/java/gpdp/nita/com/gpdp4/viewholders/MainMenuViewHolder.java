package gpdp.nita.com.gpdp4.viewholders;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import gpdp.nita.com.gpdp4.R;

public class MainMenuViewHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private ImageView icon;
    private ConstraintLayout root;

    public MainMenuViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.menu_item_text);
        icon = itemView.findViewById(R.id.menu_icon);
        root = itemView.findViewById(R.id.menu_root);
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
    }

    public ConstraintLayout getRoot() {
        return root;
    }

    public void setRoot(ConstraintLayout root) {
        this.root = root;
    }
}
