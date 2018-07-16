package ltd.boku.bakingapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import ltd.boku.bakingapp.model.Recipe;
import ltd.boku.bakingapp.utils.AppUtility;

public class MainViewModel extends AndroidViewModel {

    public static MutableLiveData<List<Recipe>> recipesLiveData=new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        setRecipesLiveData();
    }

    public MutableLiveData<List<Recipe>> getRecipesLiveData() {
        return recipesLiveData;
    }

    public void setRecipesLiveData() {
        List<Recipe> recipes= AppUtility.getRecipes();
        this.recipesLiveData.setValue(recipes);
    }
}
