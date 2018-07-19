package ltd.boku.bakingapp.utils;


import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ltd.boku.bakingapp.MainActivity;
import ltd.boku.bakingapp.model.Ingredient;
import ltd.boku.bakingapp.model.Measure;
import ltd.boku.bakingapp.model.Recipe;
import ltd.boku.bakingapp.model.Step;
import ltd.boku.bakingapp.services.LoadRecipesService;
import ltd.boku.bakingapp.viewmodels.MainViewModel;

public class AppUtility {

    public static final String BASEURL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";
    public static final String BAKINGPATH="baking.json";
    private static List<Recipe> recipes=new ArrayList<>();

    //buid a URL with a specific path
    public static URL composeURL(String baseUrl, String path) {
        Uri uri = Uri.parse(baseUrl).buildUpon()
                .appendPath(path)
                .build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    public static URL composeURL(String fullUrl){

        Uri uri = Uri.parse(fullUrl).buildUpon()
                .build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    //handle http request and response
    public static String getResponseFromHttpUrl(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
       // urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(5000);
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        int res = urlConnection.getResponseCode();
        try {
            BufferedReader inputStreamResponse = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while (null != (line = inputStreamResponse.readLine())) {
                result.append(line).append("\n");
            }
            return result.toString();
        } finally {
            urlConnection.disconnect();
        }
    }

    public static  List<Recipe> JSONParser(String JSONResponse){
        List<Recipe> recipes=new ArrayList<>();

        int id;
        String name;
        int servings;
        String image;

        try{
            JSONArray jsonArray=new JSONArray(JSONResponse);
            for (int i=0; i< jsonArray.length(); i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                JSONArray IngredientsJSONArray=jsonObject.getJSONArray("ingredients");
                JSONArray StepsJSONArray=jsonObject.getJSONArray("steps");
                id=jsonObject.getInt("id");
                name=jsonObject.getString("name");
                servings=jsonObject.getInt("servings");
                image=jsonObject.getString("image");
                List<Ingredient> ingredients=new ArrayList<>();
                List<Step> steps=new ArrayList<>();

                for(int j=0; j<IngredientsJSONArray.length();j++){
                    JSONObject ingredientJSON=IngredientsJSONArray.getJSONObject(j);

                    int quantity=ingredientJSON.getInt("quantity");
                    String sMeasure=ingredientJSON.getString("measure");
                    String ingredient=ingredientJSON.getString("ingredient");
                    Measure measure=Measure.valueOf(sMeasure);

                    ingredients.add(new Ingredient(quantity,measure,ingredient));
                }
                for (int k=0; k<StepsJSONArray.length(); k++){
                    JSONObject stepJSON=StepsJSONArray.getJSONObject(k);

                    int _id=stepJSON.getInt("id");
                    String shortDescription=stepJSON.getString("shortDescription");
                    String description=stepJSON.getString("description");
                    String videoURL=stepJSON.getString("videoURL");
                    String thumbnailURL=stepJSON.getString("thumbnailURL");

                    steps.add(new Step(_id,shortDescription,description,videoURL,thumbnailURL));
                }
                recipes.add(new Recipe(id,name,ingredients,steps,servings,image));
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return recipes;
    }

    public static List<Recipe> getRecipes() {
        return recipes;
    }

    public static void setRecipes() {
        LoadRecipes loadRecipes=new LoadRecipes();
        loadRecipes.execute(BASEURL);
    }

    private static class LoadRecipes extends AsyncTask<String, Void,List<Recipe>>{
        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            Log.d("Test", "onPostExecute: loadfinished");
            MainViewModel.recipesLiveData.postValue(recipes);
            AppUtility.recipes=recipes;
            LoadRecipesService.recipes=recipes;
            new MainActivity().runOnUiThread(() -> MainActivity.loadingProgressBar.setVisibility(View.GONE));
        }

        @Override
        protected List<Recipe> doInBackground(String... strings) {
            Log.d("Test", "doInBackground: loading");
            URL url=composeURL(strings[0],BAKINGPATH);
            String responseJSON=null;

            try{
                 responseJSON= getResponseFromHttpUrl(url);
            }catch (IOException e){
                e.printStackTrace();
            }
            return JSONParser(responseJSON);
        }
    }


}
