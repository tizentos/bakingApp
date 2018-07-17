package ltd.boku.bakingapp.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ltd.boku.bakingapp.R;
import ltd.boku.bakingapp.adapter.RecipeRecyclerViewAdapter;
import ltd.boku.bakingapp.databinding.MainFragmentBinding;
import ltd.boku.bakingapp.model.Recipe;
import ltd.boku.bakingapp.services.LoadRecipesService;
import ltd.boku.bakingapp.viewmodels.MainViewModel;

public class MainFragment extends Fragment implements RecipeRecyclerViewAdapter.OnRecipeClickListener {
    public RecipeRecyclerViewAdapter recipeRecyclerViewAdapter;
    MainFragmentBinding mainFragmentBinding;
    public static final String RECIPE_EXTRA="recipe";
    private static final String TAG = "MainFragment";

    NavigateToRecipeStepListener listener;


    public interface NavigateToRecipeStepListener{
        void navigateToRecipeStepListener(Recipe recipe);
    }

    public MainFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener=(NavigateToRecipeStepListener) context;
        Log.d(TAG, "onAttach: entering");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: entering");
        mainFragmentBinding= DataBindingUtil.inflate(inflater, R.layout.main_fragment,container,false);
//        View view=inflater.inflate(R.layout.main_fragment,container,false);
        return mainFragmentBinding.getRoot();
//        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: entering");
        setupRecyclerView();
        setupViewModel();
    }

    private void setupViewModel() {
        MainViewModel viewModel= ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getRecipesLiveData().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                Log.d("Test", "onChanged: change");
                recipeRecyclerViewAdapter.setRecipes(recipes);
                LoadRecipesService.startActionUpdateWidget(getActivity());
            }
        });
    }
    public void setupRecyclerView(){

        int density=getResources().getConfiguration().smallestScreenWidthDp;
        RecyclerView recipeRecyclerView=mainFragmentBinding.recipeRecyclerView;
//        RecyclerView recipeRecyclerView=getView().findViewById(R.id.recipe_recycler_view);
        if (density< 600) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recipeRecyclerView.setLayoutManager(linearLayoutManager);
        }else{
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
            recipeRecyclerView.setLayoutManager(gridLayoutManager);
        }

        recipeRecyclerViewAdapter=new RecipeRecyclerViewAdapter(this);
        recipeRecyclerView.setAdapter(recipeRecyclerViewAdapter);
    }

    @Override
    public void onRecipeClickListener(Recipe recipe) {
        listener.navigateToRecipeStepListener(recipe);
    }

}
