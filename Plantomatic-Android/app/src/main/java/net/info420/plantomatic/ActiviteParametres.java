package net.info420.plantomatic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class ActiviteParametres extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    PreferenceFragmentPlantomatic preferenceFragmentPlantomatic;

    Intent intentAccueil;
    Intent intentDetails;
    Intent intentManuel;
    Intent intentParametres;
    Preference prefUsine;

    public static final String TAG = "Parametres";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activiteparametres);

        //Génération des intents pour les options du menu de navigation
        intentAccueil = new Intent(this, ActivitePrincipale.class);
        intentDetails = new Intent(this, ActiviteAffichage.class);
        intentManuel = new Intent(this, ActiviteModeManuel.class);
        intentParametres = new Intent(this, ActiviteParametres.class);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.menu_nav);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.ouvrirMenu, R.string.fermerMenu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            //Listener pour les éléments du menu de navigation
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.item_activitePrincipale:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        startActivity(intentAccueil);
                        break;
                    case R.id.item_activiteModeManuel:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        startActivity(intentManuel);
                        break;
                    case R.id.item_activiteParametres:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        //startActivity(intentParametres);
                        break;
                }
                return true;
            }
        });

        preferenceFragmentPlantomatic = new PreferenceFragmentPlantomatic();

        getFragmentManager().beginTransaction().add(R.id.fragmentParametres, preferenceFragmentPlantomatic).commit();


    }

    @Override
    public void onPostCreate(Bundle savedIstanceState)
    {
        super.onPostCreate(savedIstanceState);

        prefUsine = preferenceFragmentPlantomatic.findPreference("usine");

        prefUsine.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                getApplicationContext().deleteDatabase("/data/data/net.info420.plantomatic/databases/plantomatic.db");
                Log.d(TAG,"Base de données supprimée avec succès");

                return true;
            }
        });
    }

    private void deleteFiles(File dir){
        if (dir != null){
            if (dir.listFiles() != null && dir.listFiles().length > 0){
                // RECURSIVELY DELETE FILES IN DIRECTORY
                for (File file : dir.listFiles()){
                    deleteFiles(file);
                }
            } else {
                // JUST DELETE FILE
                dir.delete();
            }
        }
    }

    public static class PreferenceFragmentPlantomatic extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}