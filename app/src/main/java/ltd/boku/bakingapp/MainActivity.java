package ltd.boku.bakingapp;

import android.app.FragmentManager;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import ltd.boku.bakingapp.adapter.RecipeRecyclerViewAdapter;
import ltd.boku.bakingapp.databinding.ActivityMainBinding;
import ltd.boku.bakingapp.databinding.AppBarMainBinding;
import ltd.boku.bakingapp.fragment.MainFragment;
import ltd.boku.bakingapp.fragment.ParticularStepFragment;
import ltd.boku.bakingapp.fragment.RecipeStepFragment;
import ltd.boku.bakingapp.model.Ingredient;
import ltd.boku.bakingapp.model.Recipe;
import ltd.boku.bakingapp.model.Step;
import ltd.boku.bakingapp.services.LoadRecipesService;
import ltd.boku.bakingapp.viewmodels.MainActivityViewModel;
import ltd.boku.bakingapp.viewmodels.MainViewModel;

import static ltd.boku.bakingapp.fragment.MainFragment.RECIPE_EXTRA;
import static ltd.boku.bakingapp.fragment.RecipeStepFragment.INGREDIENT_EXTRA;
import static ltd.boku.bakingapp.fragment.RecipeStepFragment.STEPS_EXTRA;
import static ltd.boku.bakingapp.fragment.RecipeStepFragment.STEP_POSITION;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainFragment.NavigateToRecipeStepListener,
        RecipeStepFragment.NavigateToParticularStep{

    ActivityMainBinding mainBinding;
    private static final String TAG = "MainActivity";

    Fragment currentFragment;
    MainActivityViewModel mainActivityViewModel;
    boolean twoPane=false;
    FrameLayout childListLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: entering");
        mainBinding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        //start services
        initiateRecipeLoadingService();
        setupUIPeripheral();
        mainActivityViewModel=ViewModelProviders.of(this).get(MainActivityViewModel.class);

        if (findViewById(R.id.child_list_frame_layout) != null){
            twoPane = true;
            childListLayout=findViewById(R.id.child_list_frame_layout);
        }


        Recipe recipe=(Recipe)getIntent().getSerializableExtra(RECIPE_EXTRA);
        if (recipe !=null){
            navigateToHome();
            navigateToRecipeStepListener(recipe);
            return;
        }
        if (savedInstanceState == null){
            navigateToHome();
        }
        mainActivityViewModel.getFragmentMutableLiveData().observe(this, new Observer<Fragment>() {
            @Override
            public void onChanged(@Nullable Fragment fragment) {
                int resId;
                resId= (fragment instanceof MainFragment) || (fragment instanceof RecipeStepFragment)
                        ? R.id.content_frame : R.id.child_list_frame_layout;
                setCurrentFragment(fragment,true,resId);
            }
        });
    }

    private void setCurrentFragment(Fragment fragment, boolean addToStack, int resId) {
        currentFragment= (fragment == null)? new MainFragment(): fragment;
        performFragmentTransaction(fragment, addToStack, resId);
    }

    private void performFragmentTransaction(Fragment fragment, boolean addToStack, int ResId) {
        String backStackName=fragment.getClass().getName();
        android.support.v4.app.FragmentManager fragmentManager=getSupportFragmentManager();

        if (addToStack) {
            fragmentManager.beginTransaction()
                    .replace(ResId, fragment)
                    .addToBackStack(backStackName)
                    .commit();
        }else{
            fragmentManager.beginTransaction()
                    .replace(ResId, fragment)
                    .commit();
        }
    }

    private void setupUIPeripheral() {
        Toolbar toolbar = mainBinding.appBarMainId.toolbar;
        setSupportActionBar(toolbar);
        DrawerLayout drawer = mainBinding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = mainBinding.navView;
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: entering");
        popFragment(currentFragment);
        DrawerLayout drawer = mainBinding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        }  else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initiateRecipeLoadingService(){
        Intent recipeLoadingIntent=new Intent(this, LoadRecipesService.class);
        startService(recipeLoadingIntent);
    }

    public void navigateToHome(){
        Log.d(TAG, "navigateToHome: twopane" + twoPane);
        if (twoPane) childListLayout.setVisibility(View.GONE);
        MainFragment mainFragment=new MainFragment();
        setCurrentFragment(mainFragment,false,R.id.content_frame);
        //MainActivityViewModel.fragmentMutableLiveData.postValue(mainFragment);
    }


    @Override
    public void navigateToRecipeStepListener(Recipe recipe) {

        if (twoPane) {
            childListLayout.setVisibility(View.VISIBLE);
        }


        Toast.makeText(this, recipe.getName(), Toast.LENGTH_SHORT).show();
        Bundle bundle=new Bundle();
        bundle.putSerializable(RECIPE_EXTRA,recipe);
        RecipeStepFragment recipeStepFragment=new RecipeStepFragment();
        recipeStepFragment.setArguments(bundle);
        setTitle(recipe.getName());
        setCurrentFragment(recipeStepFragment,true,R.id.content_frame);
      //  MainActivityViewModel.fragmentMutableLiveData.postValue(recipeStepFragment);
    }

    @Override
    public void navigateToParticularStep(List<Step> steps, int position) {

        int ResId;
        Bundle bundle=new Bundle();
        bundle.putSerializable(STEPS_EXTRA, (Serializable) steps);
        bundle.putSerializable(STEP_POSITION,position);
        ParticularStepFragment particularStepFragment=new ParticularStepFragment();
        particularStepFragment.setArguments(bundle);


        if (twoPane){
            ResId = R.id.child_list_frame_layout;
            childListLayout.setVisibility(View.VISIBLE);
            setCurrentFragment(particularStepFragment,true,ResId);
            getSupportFragmentManager().popBackStack();
        }else {
            ResId=R.id.content_frame;
            setCurrentFragment(particularStepFragment,true,ResId);
        }

       // MainActivityViewModel.fragmentMutableLiveData.postValue(particularStepFragment);
    }

    public  void popFragment(Fragment fragment){
        if (fragment!= null){
            getSupportFragmentManager().popBackStack(fragment.getClass().getName(),0);
        }
    }

}
