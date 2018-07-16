package ltd.boku.bakingapp.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.R;
import ltd.boku.bakingapp.databinding.IngredientItemBinding;
import ltd.boku.bakingapp.model.Ingredient;

public class IngredientsRecyclerViewAdapter extends RecyclerView.Adapter<IngredientsRecyclerViewAdapter.IngredientViewHolder> {

    List<Ingredient> ingredients=new ArrayList<>();

    public IngredientsRecyclerViewAdapter(List<Ingredient> ingredients){
        setIngredients(ingredients);
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        IngredientItemBinding ingredientItemBinding= DataBindingUtil.inflate(layoutInflater,
                R.layout.ingredient_item,parent,false);

        IngredientViewHolder ingredientViewHolder=new IngredientViewHolder(ingredientItemBinding);
        return ingredientViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient=ingredients.get(position);
        holder.ingredient.setText(ingredient.getIngredient());
        holder.quantity.setText(ingredient.getQuantity()+ingredient.getMeasure().toString());
    }

    @Override
    public int getItemCount() {
        if (ingredients == null)return 0;
        return ingredients.size();
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder{
        TextView ingredient;
        TextView quantity;
        public IngredientViewHolder(IngredientItemBinding itemView) {
            super(itemView.getRoot());

            ingredient=itemView.ingredientText;
            quantity=itemView.quantityText;
        }
    }
}
