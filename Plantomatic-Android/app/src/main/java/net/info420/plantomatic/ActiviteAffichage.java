package net.info420.plantomatic;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;

public class ActiviteAffichage extends AppCompatActivity implements View.OnClickListener{

    public final String TAG = "affichage";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Button boutonPhotoPlante;
    ImageView imageViewPhoto;
    ImageButton boutonPoubelle;

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
        intentManuel = new Intent(this, ActiviteModeManuel.class);
        intentParametres = new Intent(this, ActiviteParametres.class);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.menu_nav);
        boutonPhotoPlante = findViewById(R.id.boutonPhotoPlante);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        boutonPoubelle = findViewById(R.id.boutonPoubelle);

        boutonPhotoPlante.setOnClickListener(this);
        boutonPoubelle.setOnClickListener(this);

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

    //Permet de mettre la photo choisie dans l'imageview
    @Override
    public void onActivityResult(int requestCode, int Resultcode, Intent data) {
        super.onActivityResult(requestCode, Resultcode, data);

        if(requestCode == 1)
        {
            try {
                imageViewPhoto.setImageURI(data.getData());
            }
            catch (Exception e)
            {
                Log.e(TAG, e.getMessage());
            }
        }
    }


    //Listener pour les éléments clickables de l'interface
    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            //Bouton qui permet de sélectionner une photo
            case R.id.boutonPhotoPlante:

                Log.d(TAG, "Choisir photo");

                int code = 1;

                //Création d'un intent qui invoquera un menu de sélection
                Intent intentImage = new Intent();
                //Seules les images pourront être sélectionnées
                intentImage.setType("image/*");
                intentImage.setAction(Intent.ACTION_GET_CONTENT);
                //Démarrage de l'activité
                startActivityForResult(intentImage, code);
                break;

            case R.id.boutonPoubelle:

                imageViewPhoto.setImageDrawable(getDrawable(R.drawable.camera_transparent));
                Log.d(TAG, "bouton poubelle");

                break;
        }
    }
}