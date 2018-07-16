package ltd.boku.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.model.Ingredient;
import ltd.boku.bakingapp.services.GridWidgetService;
import ltd.boku.bakingapp.services.GridWidgetServiceIngredient;
import ltd.boku.bakingapp.services.LoadRecipesService;

import static ltd.boku.bakingapp.fragment.RecipeStepFragment.INGREDIENT_EXTRA;
import static ltd.boku.bakingapp.services.LoadRecipesService.UPDATEWIDGET_INTENT;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidget extends AppWidgetProvider {

    public static boolean showIngredient=false;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Bundle options=appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width=options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews views;
        if (showIngredient){
            views=getIngredientRemoteView(context);
            showIngredient=false;
        }else{
            views=getRecipeRemoteView(context);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        LoadRecipesService.startActionUpdateWidget(context);
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
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static  RemoteViews getRecipeRemoteView(Context context){
        RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.baking_widget);
        Intent intent=new Intent(context,GridWidgetService.class);
        remoteViews.setRemoteAdapter(R.id.widget_recipe_gridview,intent);


        Intent appIntent=new Intent(context,LoadRecipesService.class);
        PendingIntent appPendingIntent=PendingIntent.getService(context,0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setPendingIntentTemplate(R.id.widget_recipe_gridview,appPendingIntent);

        remoteViews.setEmptyView(R.id.widget_recipe_gridview,R.id.empty_view);
        return remoteViews;
    }
    private static RemoteViews getIngredientRemoteView(Context context){

        RemoteViews remoteViews=new RemoteViews(context.getPackageName(),R.layout.ingredient_widget);
        Intent intent=new Intent(context,GridWidgetServiceIngredient.class);
        remoteViews.setRemoteAdapter(R.id.ingredient_gridview,intent);

        Intent appIntent=new Intent(context,LoadRecipesService.class);
        appIntent.setAction(UPDATEWIDGET_INTENT);
        PendingIntent appPendingIntent=PendingIntent.getService(context,0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setPendingIntentTemplate(R.id.ingredient_gridview,appPendingIntent);

        remoteViews.setEmptyView(R.id.ingredient_gridview,R.id.ingredient_empty_view);
        return remoteViews;
    }
}

