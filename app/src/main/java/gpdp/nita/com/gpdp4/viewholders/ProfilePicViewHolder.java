package gpdp.nita.com.gpdp4.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import gpdp.nita.com.gpdp4.R;

public class ProfilePicViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView profilePic;
    private TextView benCode;
    private ProgressBar imageLoading;

    public ProfilePicViewHolder(@NonNull View itemView) {
        super(itemView);
        profilePic = itemView.findViewById(R.id.img_dp);
        benCode = itemView.findViewById(R.id.txt_ben_code);
        imageLoading = itemView.findViewById(R.id.image_loading);
    }

    public CircleImageView getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(CircleImageView profilePic) {
        this.profilePic = profilePic;
    }

    public TextView getBenCode() {
        return benCode;
    }

    public void setBenCode(TextView benCode) {
        this.benCode = benCode;
    }

    public ProgressBar getImageLoading() {
        return imageLoading;
    }

    public void setImageLoading(ProgressBar imageLoading) {
        this.imageLoading = imageLoading;
    }
}
