package ltd.boku.bakingapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import ltd.boku.bakingapp.model.Step;

public class ParticularStepViewModel extends AndroidViewModel {

    public static MutableLiveData<Step> stepMutableLiveData=new MutableLiveData<>();

    public ParticularStepViewModel(@NonNull Application application) {
        super(application);
    }

    public static void setStepMutableLiveData(Step step) {
        stepMutableLiveData.setValue(step);
    }

    public  MutableLiveData<Step> getStepMutableLiveData() {
        return stepMutableLiveData;
    }
}
