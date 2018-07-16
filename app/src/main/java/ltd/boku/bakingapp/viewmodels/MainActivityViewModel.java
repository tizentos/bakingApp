package ltd.boku.bakingapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

public class MainActivityViewModel extends AndroidViewModel {

    public static MutableLiveData<Fragment> fragmentMutableLiveData=new MutableLiveData<>();

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public void setFragmentMutableLiveData(Fragment fragment) {
        fragmentMutableLiveData.setValue(fragment);
    }

    public MutableLiveData<Fragment> getFragmentMutableLiveData() {
        return fragmentMutableLiveData;
    }
}
