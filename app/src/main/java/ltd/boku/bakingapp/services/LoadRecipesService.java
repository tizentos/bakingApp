package ltd.boku.bakingapp.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.BakingWidget;
import ltd.boku.bakingapp.R;
import ltd.boku.bakingapp.model.Ingredient;
import ltd.boku.bakingapp.model.Recipe;
import ltd.boku.bakingapp.utils.AppUtility;

import static ltd.boku.bakingapp.fragment.RecipeStepFragment.INGREDIENT_EXTRA;

public class LoadRecipesService extends IntentService {

    public static List<Recipe> recipes=new ArrayList<>();
    public static List<Ingredient> ingredients=new ArrayList<>();

    public static final String UPDATEWIDGET_INTENT="update-widget";

    public LoadRecipesService() {
        super("LoadRecipesService");
    }



    public static void startActionUpdateWidget(Context context){
        Intent intent=new Intent(context,LoadRecipesService.class);
        intent.setAction(UPDATEWIDGET_INTENT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        List<Ingredient> ingredients=(List<Ingredient>) intent.getSerializableExtra(INGREDIENT_EXTRA);
        if (ingredients != null){
            handleIngredientWidgetUpdate(ingredients);
        }
        if (intent != null){
            String action=intent.getAction();
            if (UPDATEWIDGET_INTENT.equals(action) && recipes!=null){
                handleWidgetUpdate();
            }else{
                AppUtility.setRecipes();
            }
        }
        //load recipes
    }

    private  void handleWidgetUpdate(){
        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(this);

        int[] appWidgetIds= appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_recipe_gridview);
        BakingWidget.updateRecipeWidget(this,appWidgetManager,appWidgetIds);
    }

    private  void handleIngredientWidgetUpdate(List<Ingredient> ingredients){
        LoadRecipesService.ingredients=ingredients;
        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(this);
        int[] appWidgetIds= appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.ingredient_gridview);
        BakingWidget.updateRecipeWidget(this,appWidgetManager,appWidgetIds,ingredients);
    }

}
