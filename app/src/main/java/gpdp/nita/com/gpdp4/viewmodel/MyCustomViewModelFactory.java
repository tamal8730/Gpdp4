package gpdp.nita.com.gpdp4.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class MyCustomViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private String benCode;

    public MyCustomViewModelFactory(Application application, String benCode) {
        this.application = application;
        this.benCode = benCode;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return modelClass.cast(new FormsViewModel(application, benCode));
    }
}
