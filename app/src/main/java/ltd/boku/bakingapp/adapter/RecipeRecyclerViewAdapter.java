package ltd.boku.bakingapp.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.R;
import ltd.boku.bakingapp.databinding.RecipeCardBinding;
import ltd.boku.bakingapp.model.Recipe;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.RecipeViewHolder> {

    OnRecipeClickListener listener;
    Context context;
    List<Recipe> recipes=new ArrayList<>();

    public interface OnRecipeClickListener{
        void onRecipeClickListener(Recipe recipe);
    }

    public RecipeRecyclerViewAdapter(Context context,OnRecipeClickListener listener) {
        this.listener = listener;
        this.context=context;
    }

    public RecipeRecyclerViewAdapter(OnRecipeClickListener listener, List<Recipe> recipes) {
        this.listener = listener;
        this.recipes=recipes;
    }


    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
         RecipeCardBinding binding= DataBindingUtil.inflate(layoutInflater,R.layout.recipe_card,parent,false);
         RecipeViewHolder recipeViewHolder=new RecipeViewHolder(binding);
         return recipeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder,  int position) {
        final Recipe recipe=recipes.get(position);

        Picasso.with(context)
                .load(recipe.getImage().isEmpty()?null:recipe.getImage())
                .error(context.getResources().getDrawable(R.drawable.recipe))
                .placeholder(context.getResources().getDrawable(R.drawable.recipe))
                .into(holder.recipeImage);
        holder.textRecipeName.setText(recipe.getName());
        holder.itemView.setOnClickListener(v -> listener.onRecipeClickListener(recipe));
    }

    @Override
    public int getItemCount() {
        if(recipes == null) return 0;
        return recipes.size();
    }

    public void setRecipes(List<Recipe> recipes){
        this.recipes=recipes;
        notifyDataSetChanged();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder{
        ImageView recipeImage;
        TextView textRecipeName;
        public RecipeViewHolder(RecipeCardBinding binding) {
            super(binding.getRoot());
            textRecipeName=binding.recipeName;
            recipeImage=binding.recipeImage;
        }
    }
}
