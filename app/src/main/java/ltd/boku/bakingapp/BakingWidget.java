package ltd.boku.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.model.Ingredient;
import ltd.boku.bakingapp.services.GridWidgetService;
import ltd.boku.bakingapp.services.GridWidgetServiceIngredient;
import ltd.boku.bakingapp.services.LoadRecipesService;

import static android.support.constraint.Constraints.TAG;
import static ltd.boku.bakingapp.fragment.MainFragment.RECIPE_EXTRA;
import static ltd.boku.bakingapp.fragment.RecipeStepFragment.INGREDIENT_EXTRA;
import static ltd.boku.bakingapp.services.LoadRecipesService.UPDATEWIDGET_INTENT;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidget extends AppWidgetProvider {

    public static boolean showIngredient=false;
    private static final String TAG = "BakingWidget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views;
        if (showIngredient){
            showIngredient=false;
            views=getIngredientRemoteView(context);
        }else{
            views=getRecipeRemoteView(context);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.d(TAG, "onUpdate: entering");
        LoadRecipesService.startActionUpdateWidget(context);
        showIngredient=false;
    }

    public static void updateRecipeWidget(Context context,AppWidgetManager appWidgetManager,int[] appWidgetIds){
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    public static void updateRecipeWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, List<Ingredient> ingredient){
        if (ingredient.size() >  0)showIngredient=true;
        updateRecipeWidget(context,appWidgetManager,appWidgetIds);
    }


    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled: entering");
        LoadRecipesService.startActionUpdateWidget(context);
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static  RemoteViews getRecipeRemoteView(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.baking_widget);
        Intent intent = new Intent(context, GridWidgetService.class);
        remoteViews.setRemoteAdapter(R.id.widget_recipe_gridview, intent);
        remoteViews.setTextViewText(R.id.widget_title, "Baking App");


        getIngredientWidget(context, remoteViews);

        remoteViews.setEmptyView(R.id.widget_recipe_gridview, R.id.empty_view);
        returnToRecipeWidget(context, remoteViews, R.id.widget_title);


        return remoteViews;
    }

    private static  void getIngredientWidget(Context context,RemoteViews remoteViews){
        Intent appIntent=new Intent(context,LoadRecipesService.class);
        PendingIntent appPendingIntent=PendingIntent.getService(context,0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widget_recipe_gridview,appPendingIntent);
    }

    private static RemoteViews getIngredientRemoteView(Context context){

        RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.ingredient_widget);
        Intent intent=new Intent(context,GridWidgetServiceIngredient.class);
        remoteViews.setRemoteAdapter(R.id.ingredient_gridview,intent);
        remoteViews.setTextViewText(R.id.ingredient_widget_title,"Ingredient");

        returnToRecipeWidget(context,remoteViews,R.id.ingredient_gridview);
        setOnClickIngredientWidgetTitle(context,remoteViews);
        return  remoteViews;
    }

    private static void returnToRecipeWidget(Context context, RemoteViews remoteViews, int resId) {
        Intent appIntent=new Intent(context,LoadRecipesService.class);
        appIntent.setAction(UPDATEWIDGET_INTENT);
        PendingIntent appPendingIntent=PendingIntent.getService(context,0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(resId,appPendingIntent);
    }

    private static void setOnClickIngredientWidgetTitle(Context context, RemoteViews remoteViews) {
        Bundle bundle=new Bundle();
        bundle.putSerializable(RECIPE_EXTRA, LoadRecipesService.recipe);
        Intent recipeIntent=new Intent(context,MainActivity.class);
        recipeIntent.putExtras(bundle);
        PendingIntent recipePendingIntent=PendingIntent.getActivity(context,0,recipeIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ingredient_widget_title,recipePendingIntent);
    }
}

