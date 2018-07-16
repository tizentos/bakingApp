package ltd.boku.bakingapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.R;
import ltd.boku.bakingapp.adapter.IngredientsRecyclerViewAdapter;
import ltd.boku.bakingapp.databinding.IngredientsFragmentLayoutBinding;
import ltd.boku.bakingapp.model.Ingredient;

import static ltd.boku.bakingapp.fragment.RecipeStepFragment.INGREDIENT_EXTRA;

public class IngredientsDialogFragment extends DialogFragment {

    private static final String TAG = "IngredientsDialogFragme";

    IngredientsRecyclerViewAdapter ingredientsRecyclerViewAdapter;
    List<Ingredient> ingredients=new ArrayList<>();

    IngredientsFragmentLayoutBinding ingredientsFragmentLayoutBinding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: entering");
        if (savedInstanceState==null){
            Bundle bundle=getArguments();
            ingredients=(List<Ingredient>) bundle.getSerializable(INGREDIENT_EXTRA);
        }else{
            if (savedInstanceState.containsKey(INGREDIENT_EXTRA)){
                ingredients=(List<Ingredient>) savedInstanceState.getSerializable(INGREDIENT_EXTRA);
            }
        }

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: entering");
        ingredientsFragmentLayoutBinding=DataBindingUtil.inflate(getLayoutInflater(),R.layout.ingredients_fragment_layout,null,false);
        setRecyclerView();
//        this.getDialog().setTitle("Ingredients");
        ingredientsFragmentLayoutBinding.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IngredientsDialogFragment.this.getDialog().cancel();
            }
        });
        return ingredientsFragmentLayoutBinding.getRoot();
    }
    private void setRecyclerView() {
        ingredientsRecyclerViewAdapter=new IngredientsRecyclerViewAdapter(ingredients);
        RecyclerView ingredientRecycler=ingredientsFragmentLayoutBinding.ingredientRecycler;
        ingredientRecycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        ingredientRecycler.setHasFixedSize(true);
        ingredientRecycler.setAdapter(ingredientsRecyclerViewAdapter);

        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(getActivity().getApplicationContext(),
                DividerItemDecoration.VERTICAL);
        ingredientRecycler.addItemDecoration(dividerItemDecoration);
    }
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(TAG, "onCancel: entering");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: entering");
        outState.putSerializable(INGREDIENT_EXTRA,(Serializable)ingredients);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "onViewStateRestored: entering");
    }
    //    public void shareIngredient(){
//        String mimeType="text/plain";
//
//        String title=getString(R.string.share_title);
//
//        ShareCompat.IntentBuilder.from(getActivity())
//                .setType(mimeType)
//                .setChooserTitle(title)
//                .setText(getString(R.string.share_message,movie.getOriginal_title()))
//                .startChooser();
//    }
}
