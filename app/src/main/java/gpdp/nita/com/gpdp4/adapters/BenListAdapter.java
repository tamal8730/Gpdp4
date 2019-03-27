package gpdp.nita.com.gpdp4.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.interfaces.OnBenListItemSelected;
import gpdp.nita.com.gpdp4.models.BenModel;

public class BenListAdapter extends RecyclerView.Adapter<BenListAdapter.BenListViewHolder> implements Filterable {

    private ArrayList<BenModel> models;
    private ArrayList<BenModel> modelsFiltered;
    private Context context;
    private OnBenListItemSelected onBenListItemSelected;

    public BenListAdapter(ArrayList<BenModel> models, Context context, OnBenListItemSelected onBenListItemSelected) {
        this.models = models;
        this.context = context;
        this.modelsFiltered = models;
        this.onBenListItemSelected = onBenListItemSelected;
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

        benListViewHolder.getHeadName().setText(modelsFiltered.get(i).getName());

        TextView benStatus = benListViewHolder.getCount();

        final String ben = modelsFiltered.get(i).getBenCode();
        benCode.setText(ben);


        benListViewHolder.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBenListItemSelected.onBenListItemSelected(ben);
            }
        });

        int status = modelsFiltered.get(i).getStatus();
        if (status == 0) {  //not synced
            benStatus.setTextColor(Color.parseColor("#7f8c8d"));
            dp.setBorderColor(Color.parseColor("#7f8c8d"));
            benStatus.setText("Not synced");
        } else if (status == 1) {
            int count = modelsFiltered.get(i).getCount();
            if (count == 0) {
                benStatus.setTextColor(Color.parseColor("#2ecc71"));
                dp.setBorderColor(Color.parseColor("#2ecc71"));
                benStatus.setText("Synced all");
            } else {
                benStatus.setTextColor(Color.parseColor("#3498db"));
                dp.setBorderColor(Color.parseColor("#3498db"));
                benStatus.setText(String.format("Synced %d", 26 - count));
            }
        }


        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.ic_default_avatar)
                        .error(R.drawable.ic_default_avatar)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true))
                .load(modelsFiltered.get(i).getImageUrl())
                .into(dp);

    }

    @Override
    public int getItemCount() {
        return modelsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    modelsFiltered = models;
                } else {
                    ArrayList<BenModel> filteredList = new ArrayList<>();
                    for (BenModel benModel : models) {
                        if (
                                benModel.getName().toLowerCase().contains(charString.toLowerCase())
                                        ||
                                        benModel.getBenCode().toLowerCase().contains(charString.toLowerCase())
                                        ||
                                        (charString.toLowerCase().contains("un") && benModel.getStatus() == 0)
                                        ||
                                        ("synced".contains(charString.toLowerCase()) && benModel.getStatus() == 1)
                        ) {
                            filteredList.add(benModel);
                        }
                    }
                    modelsFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = modelsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                modelsFiltered = (ArrayList<BenModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class BenListViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView dp;
        private TextView benCode, headName, count;
        private ConstraintLayout root;

        BenListViewHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.img_ben_img);
            benCode = itemView.findViewById(R.id.txt_ben_name);
            headName = itemView.findViewById(R.id.head_name);
            count = itemView.findViewById(R.id.sync_count);
            root = itemView.findViewById(R.id.root);
        }

        public ConstraintLayout getRoot() {
            return root;
        }

        public TextView getHeadName() {
            return headName;
        }

        public TextView getCount() {
            return count;
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
