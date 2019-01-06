package gpdp.nita.com.gpdp4.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.interfaces.OnMenuItemSelected;
import gpdp.nita.com.gpdp4.models.MainMenuModel;
import gpdp.nita.com.gpdp4.repositories.Constants;
import gpdp.nita.com.gpdp4.viewholders.MainMenuViewHolder;

public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<MainMenuModel> models;
    private OnMenuItemSelected onMenuItemSelected;

    public MenuAdapter(Context context, ArrayList<MainMenuModel> models, OnMenuItemSelected onMenuItemSelected) {
        this.context = context;
        this.models = models;
        this.onMenuItemSelected = onMenuItemSelected;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new MainMenuViewHolder(inflater.inflate(R.layout.layout_main_menu, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        MainMenuViewHolder mainMenuViewHolder = (MainMenuViewHolder) viewHolder;
        TextView title = mainMenuViewHolder.getTitle();
        ImageView icon = mainMenuViewHolder.getIcon();
        ConstraintLayout root = mainMenuViewHolder.getRoot();

        title.setText(models.get(i).getTitle());
        icon.setImageResource(models.get(i).getIconResId());

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                if (pos == 0) onClickAddBen();
                else if (pos == 1) onClickViewAll();
                else if (pos == 2) onClickSync();
                else if (pos == 3) onClickLogout();
            }
        });
    }

    private void onClickLogout() {
        SharedPreferences rememberLogin = context.getSharedPreferences(Constants.REMEMBER_LOGIN, Context.MODE_PRIVATE);
        rememberLogin.edit()
                .remove(Constants.KEY_LOGGED_IN)
                .remove(Constants.KEY_SERVER_RESPONSE)
                .apply();
        onMenuItemSelected.onLogout();
    }

    private void onClickSync() {
        onMenuItemSelected.onSync();
    }

    private void onClickViewAll() {
        onMenuItemSelected.onViewAll();
    }

    private void onClickAddBen() {
        onMenuItemSelected.onAddBen();
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
