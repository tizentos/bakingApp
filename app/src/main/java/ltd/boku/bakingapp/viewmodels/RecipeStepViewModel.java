package ltd.boku.bakingapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import ltd.boku.bakingapp.fragment.RecipeStepFragment;
import ltd.boku.bakingapp.model.Step;

public class RecipeStepViewModel extends AndroidViewModel {

    MutableLiveData<List<Step>> stepsLiveData=new MutableLiveData<>();

    public RecipeStepViewModel(@NonNull Application application) {
        super(application);
        stepsLiveData.setValue(RecipeStepFragment.getRecipeSteps());
    }

    public MutableLiveData<List<Step>> getRecipeSteps(){
        return stepsLiveData;
    }

}
