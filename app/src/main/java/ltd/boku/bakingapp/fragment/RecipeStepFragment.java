package ltd.boku.bakingapp.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.R;
import ltd.boku.bakingapp.adapter.StepsRecyclerViewAdapter;
import ltd.boku.bakingapp.databinding.FragmentRecipeNameBinding;
import ltd.boku.bakingapp.model.Recipe;
import ltd.boku.bakingapp.model.Step;
import ltd.boku.bakingapp.viewmodels.RecipeStepViewModel;

import static android.support.constraint.Constraints.TAG;
import static ltd.boku.bakingapp.fragment.MainFragment.RECIPE_EXTRA;

public class RecipeStepFragment extends Fragment implements StepsRecyclerViewAdapter.OnStepsClickListener{

    private static Recipe recipe=new Recipe();
    FragmentRecipeNameBinding fragmentRecipeNameBinding;
    StepsRecyclerViewAdapter stepsRecyclerViewAdapter;
    NavigateToParticularStep listener;

    public static final String STEPS_EXTRA="steps";
    public static final String STEP_POSITION="position";
    public static final String INGREDIENT_EXTRA= "ingredient";


    public interface NavigateToParticularStep{
        void navigateToParticularStep(List<Step> steps,int position);
    }
    public RecipeStepFragment() {

    }
    public static List<Step> getRecipeSteps(){
        return recipe.getSteps();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle=getArguments();
        recipe=(Recipe) bundle.getSerializable(RECIPE_EXTRA);
        listener=(NavigateToParticularStep)context;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        fragmentRecipeNameBinding.recipeIngredientButton.setText(getString(R.string.ingredient_dashboard,
                recipe.getName()));

        fragmentRecipeNameBinding.recipeIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IngredientsDialogFragment ingredientsDialogFragment=new IngredientsDialogFragment();
                Bundle bundle=new Bundle();
                bundle.putSerializable(INGREDIENT_EXTRA,(Serializable) recipe.getIngredients());
                ingredientsDialogFragment.setArguments(bundle);
                ingredientsDialogFragment.show(getFragmentManager(),"dialog");
            }
        });

        setupRecyclerView();
        setupViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         fragmentRecipeNameBinding= DataBindingUtil.inflate(inflater,
                R.layout.fragment_recipe_name,
                container,
                false);
        return fragmentRecipeNameBinding.getRoot();
    }

    public void setupRecyclerView(){
        stepsRecyclerViewAdapter=new StepsRecyclerViewAdapter(this);
        stepsRecyclerViewAdapter.setSteps(recipe.getSteps());


        RecyclerView stepsRecyclerView=fragmentRecipeNameBinding.stepsRecycler;
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        stepsRecyclerView.setLayoutManager(linearLayoutManager);
        stepsRecyclerView.setAdapter(stepsRecyclerViewAdapter);
    }

    private void setupViewModel(){
        RecipeStepViewModel recipeStepViewModel= ViewModelProviders.of(this)
                .get(RecipeStepViewModel.class);

        recipeStepViewModel.getRecipeSteps().observe(this, new Observer<List<Step>>() {
            @Override
            public void onChanged(@Nullable List<Step> steps) {
                stepsRecyclerViewAdapter.setSteps(steps);
            }
        });
    }


    @Override
    public void onStepsClickListener(List<Step> steps,int position) {
        Toast.makeText(getContext(), steps.get(position).getShortDescription(), Toast.LENGTH_SHORT).show();
        listener.navigateToParticularStep(steps,position);
    }

    @Override
    public void onDetach() {
        int count= getFragmentManager().getBackStackEntryCount();
        Log.d(TAG, "onDetach: entering" + count);
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
