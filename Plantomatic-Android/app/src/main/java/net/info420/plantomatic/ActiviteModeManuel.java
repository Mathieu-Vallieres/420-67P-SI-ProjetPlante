package net.info420.plantomatic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;

public class ActiviteModeManuel extends AppCompatActivity {

    public static final String TAG = "manuel";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Intent intentAccueil;
    Intent intentDetails;
    Intent intentManuel;
    Intent intentParametres;

    ListView listViewPlantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activitemodemanuel);

        //Génération des intents pour les options du menu de navigation
        intentAccueil = new Intent(this, ActivitePrincipale.class);
        intentDetails = new Intent(this, ActiviteAffichage.class);
        intentManuel = new Intent(this, ActiviteModeManuel.class);
        intentParametres = new Intent(this, ActiviteParametres.class);

        //Liaison des variables aux éléments du layout
        listViewPlantes = findViewById(R.id.listePlantes);
        listViewPlantes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

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
                        startActivity(intentAccueil);
                        break;
                    case R.id.item_activiteModeManuel:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        //startActivity(intentManuel);
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