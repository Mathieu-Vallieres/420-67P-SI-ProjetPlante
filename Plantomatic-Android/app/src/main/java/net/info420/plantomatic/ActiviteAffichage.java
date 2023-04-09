package net.info420.plantomatic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

public class ActiviteAffichage extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;

    Intent intentAccueil;
    Intent intentDetails;
    Intent intentStatistiques;
    Intent intentManuel;
    Intent intentParametres;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activiteaffichage);

        //Génération des intents pour les options du menu de navigation
        intentAccueil = new Intent(this, ActivitePrincipale.class);
        intentDetails = new Intent(this, ActiviteAffichage.class);
        intentStatistiques = new Intent(this, ActiviteStatistiques.class);
        intentManuel = new Intent(this, ActiviteModeManuel.class);
        intentParametres = new Intent(this, ActiviteParametres.class);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.menu_nav);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.ouvrirMenu, R.string.fermerMenu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.item_activitePrincipale:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        //startActivity(intentAccueil);
                        break;
                    case R.id.item_activiteAffichage:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        startActivity(intentDetails);
                        break;
                    case R.id.item_activiteStatistiques:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        startActivity(intentStatistiques);
                        break;
                    case R.id.item_activiteModeManuel:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        startActivity(intentManuel);
                        break;
                    case R.id.item_activiteParametres:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        startActivity(intentParametres);
                        break;
                }
                return true;
            }
        });
    }
}