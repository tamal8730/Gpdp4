package gpdp.nita.com.gpdp4.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.models.BenModel;

public class BenListAdapter extends RecyclerView.Adapter<BenListAdapter.BenListViewHolder> {

    private ArrayList<BenModel> models;
    private Context context;

    public BenListAdapter(ArrayList<BenModel> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @NonNull
    @Override
    public BenListAdapter.BenListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new BenListViewHolder(inflater.inflate(R.layout.single_beneficiary, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BenListAdapter.BenListViewHolder benListViewHolder, int i) {
        TextView benCode = benListViewHolder.getBenCode();
        CircleImageView dp = benListViewHolder.getDp();

        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar))
                .load(models.get(i).getImageUrl())
                .into(dp);

        benCode.setText(models.get(i).getBenCode());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class BenListViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView dp;
        private TextView benCode;

        BenListViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.img_ben_img);
            benCode = itemView.findViewById(R.id.txt_ben_name);
        }

        public CircleImageView getDp() {
            return dp;
        }

        public void setDp(CircleImageView dp) {
            this.dp = dp;
        }

        public TextView getBenCode() {
            return benCode;
        }

        public void setBenCode(TextView benCode) {
            this.benCode = benCode;
        }
    }
}
