package gpdp.nita.com.gpdp4.interfaces;

import de.hdodenhof.circleimageview.CircleImageView;

public interface OnValuesEnteredListener {
    void onDateSet(String date, int position);

    void onViewRemoved(int position, int priority, String[] show, String[] hide);

    void onTyping(String text, int position);

    void onRadioButtonChecked(int checkId, int position);

    void onSpinnerItemSelected(Object key, int position);

    void onProfilePicTapped(CircleImageView circleImageView, int position);
}
