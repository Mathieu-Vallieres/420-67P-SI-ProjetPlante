package net.info420.plantomatic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.android.material.navigation.NavigationView;

import java.net.URI;

public class ActivitePrincipale extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;

    Intent intentAccueil;
    Intent intentDetails;
    Intent intentStatistiques;
    Intent intentManuel;
    Intent intentParametres;

    //Bouton pour ajouter une plante -> Redirige vers détails
    Button boutonAjouterPlante;
    //ListView qui affiche le contenue de la bd des plantes
    ListView listViewListePlantes;
    //Curseur pour naviger dans la base de données des plantes
    Cursor curseur;
    //Adapteur pour afficher les plantes dans la liste
    SimpleCursorAdapter adapteur;
    MyViewBinder viewBinder;
    static final String[] from = { BD_Plantes.C_IMAGE, BD_Plantes.C_NOMPLANTE };
    static final int[] to = { R.id.row_listePlante_Image, R.id.row_listePlante_Nom };

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
        setContentView(R.layout.layout_activiteprincipale);

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

        //Bouton pour ajouter une plante -> Redirige vers détails
        boutonAjouterPlante = findViewById(R.id.boutonAjouterPlante);
        boutonAjouterPlante.setOnClickListener(v -> startActivity(intentDetails));


        //Population de la liste view avec les plantes de la bd
        listViewListePlantes = findViewById(R.id.listePlantes);
        curseur = this.getBD_Plantes().query();
        adapteur = new SimpleCursorAdapter(this, R.layout.layout_row_listeplante, curseur, from, to, 0);
        viewBinder = new MyViewBinder();
        adapteur.setViewBinder(viewBinder);
        listViewListePlantes.setAdapter(adapteur);
        /*listViewListePlantes.setOnItemClickListener((parent, view, position, id) -> {
            Log.i("ActivitePrincipale", "Position: " + position + " id: " + id);
            Intent intent = new Intent(this, ActiviteAffichage.class);
            intent.putExtra("id", id);
            startActivity(intent);
        });*/
        BD_Plantes bd_plantes = new BD_Plantes(this);
        Uri.Builder builder = new Uri.Builder();
        builder.path("//foo");
        Uri uri = builder.build();
        bd_plantes.insert(uri,"Plante verte", 65,25);
    }
    private class MyViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int fieldIndex) {
            return false;
        }
    }
    public BD_Plantes getBD_Plantes()
    {
        return new BD_Plantes(this);
    }
}