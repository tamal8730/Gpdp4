package gpdp.nita.com.gpdp4.interfaces;

public interface OnFormsEndListener {
    void onSyncStarted();

    void onFormsEnd(boolean isSuccessful, String errorMessage);
}
