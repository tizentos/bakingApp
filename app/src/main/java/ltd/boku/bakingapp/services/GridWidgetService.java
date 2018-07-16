package ltd.boku.bakingapp.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.R;
import ltd.boku.bakingapp.model.Ingredient;
import ltd.boku.bakingapp.model.Recipe;

import static ltd.boku.bakingapp.fragment.RecipeStepFragment.INGREDIENT_EXTRA;

public class GridWidgetService  extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        List<Ingredient> ingredients=(List<Ingredient>) intent.getSerializableExtra(INGREDIENT_EXTRA);
        if (ingredients!=null){
            return new GridRemoteViewFactory(this.getApplicationContext(),ingredients);
        }
        return new GridRemoteViewFactory(this.getApplicationContext());
    }
}

class GridRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory{

    public static final String IS_INGREDIENT="isIngredient";
    Context context;
    List<Recipe> recipes=new ArrayList<>();
    List<Ingredient> ingredients=new ArrayList<>();
    boolean showIngredient=false;

    public GridRemoteViewFactory(Context applicationContext){
        context=applicationContext;
    }
    public GridRemoteViewFactory(Context applicationContext, List<Ingredient> ingredients){
        this.context=applicationContext;
        this.ingredients=ingredients;
        showIngredient=true;
    }
    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        recipes=LoadRecipesService.recipes;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (recipes == null)return 0;
        return recipes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (recipes == null || recipes.size() == 0) return  null;
        Recipe recipe=recipes.get(position);
        RemoteViews remoteView=new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        Bundle extras=new Bundle();
        remoteView.setTextViewText(R.id.widget_recipe_name, recipe.getName());
        extras.putSerializable(INGREDIENT_EXTRA,(Serializable) recipe.getIngredients());

        Intent fillIntent=new Intent();
        fillIntent.putExtras(extras);

        remoteView.setOnClickFillInIntent(R.id.widget_recipe_linearlayout,fillIntent);
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
