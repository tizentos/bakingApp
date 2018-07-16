package ltd.boku.bakingapp.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.R;
import ltd.boku.bakingapp.model.Ingredient;


public class GridWidgetServiceIngredient extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) { ;
        return new IngredientGridRemoteViewFactory(this.getApplicationContext());
    }
}

class IngredientGridRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory{

    List<Ingredient> ingredients=new ArrayList<>();
    Context context;

    public IngredientGridRemoteViewFactory(Context applicationContext){
        context=applicationContext;
    }
    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        ingredients=LoadRecipesService.ingredients;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (ingredients== null)return 0;
        return ingredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (ingredients==null || ingredients.size() == 0)return null;
        Ingredient ingredient=ingredients.get(position);

        RemoteViews remoteViews=new RemoteViews(context.getPackageName(), R.layout.ingredient_widget);

        remoteViews.setTextViewText(R.id.widget_ingredient_name,ingredient.getIngredient());

        Intent fillIntent=new Intent();
        remoteViews.setOnClickFillInIntent(R.id.widget_ingredient_linearlayout,fillIntent);
        return remoteViews;
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
