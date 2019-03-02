package gpdp.nita.com.gpdp4.interfaces;

import android.widget.ProgressBar;

import de.hdodenhof.circleimageview.CircleImageView;

public interface OnValuesEnteredListener {
    void onDateSet(String date, int position);

    void onViewRemoved(int position, int category);

    void onTyping(String text, int position);

    void onRadioButtonChecked(int checkId, int position);

    void onSpinnerItemSelected(Object key, int position);

    void onProfilePicTapped(CircleImageView circleImageView, int position);

    void onProfilePictureFetchOffline(CircleImageView circleImageView, String imageURL, ProgressBar loading);
}
