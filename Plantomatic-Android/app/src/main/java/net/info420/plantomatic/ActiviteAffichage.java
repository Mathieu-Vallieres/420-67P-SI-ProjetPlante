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
import android.database.sqlite.SQLiteException;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ActiviteAffichage extends AppCompatActivity implements View.OnClickListener{

    public final String TAG = "affichage";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    EditText editTextNomPlante;
    EditText editTextHumidite;
    EditText editTextQuantiteEau;
    Button boutonPhotoPlante;
    ImageView imageViewPhoto;
    ImageButton boutonPoubelle;
    ImageButton boutonEnregistrer;

    Intent intentAccueil;
    Intent intentDetails;
    Intent intentStatistiques;
    Intent intentManuel;
    Intent intentParametres;
    Intent intentImage;

    //Variables pour la savegarde du résultat dans la bd
    private Uri imageUri;
    private String NomPlante;
    private int humidite;
    private int quantiteEau;


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


        //Assignation des variables pour les éléments du layout
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.menu_nav);
        boutonPhotoPlante = findViewById(R.id.boutonPhotoPlante);
        boutonEnregistrer = findViewById(R.id.boutonEnregistrer);
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        boutonPoubelle = findViewById(R.id.boutonPoubelle);
        editTextNomPlante = findViewById(R.id.editTextNomPlante);
        editTextHumidite = findViewById(R.id.editTextHumidite);
        editTextQuantiteEau = findViewById(R.id.editTextArrosage);

        //Mettre les données en cas de modification
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            int idPlante = extras.getInt("idPlante");
            String nomPlante = extras.getString("nomPlante");
            int humidite = extras.getInt("humidity");
            int quantiteEau = extras.getInt("mlEau");
            String uri = extras.getString("imagePlante");

            editTextNomPlante.setText(nomPlante);
            editTextHumidite.setText(String.valueOf(humidite));
            editTextQuantiteEau.setText(String.valueOf(quantiteEau));

            if(uri != null)
            {
                imageUri = Uri.parse(uri);
                imageViewPhoto.setImageURI(imageUri);
            }
        }


        //Ajout des listeners pour les éléments clickables (Boutons)
        boutonPhotoPlante.setOnClickListener(this);
        boutonPoubelle.setOnClickListener(this);
        boutonEnregistrer.setOnClickListener(this);

        //Menu de navigation
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
                imageUri = data.getData();
                imageViewPhoto.setImageURI(imageUri);
            }
            catch (Exception e)
            {
                imageUri = null;
                Log.e(TAG, e.getMessage());
            }
        }
    }


    //Listener pour les éléments clickables de l'interface
    @SuppressLint("WrongConstant")
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
                intentImage = new Intent();
                //Seules les images pourront être sélectionnées
                intentImage.setType("image/*");
                intentImage.setAction(Intent.ACTION_GET_CONTENT);
                //Démarrage de l'activité
                startActivityForResult(intentImage, code);
                break;

            //Bouton qui permet de supprimer la photo
            case R.id.boutonPoubelle:
                imageUri = null;
                imageViewPhoto.setImageDrawable(getDrawable(R.drawable.camera_transparent));
                Log.d(TAG, "bouton poubelle");
                break;

            //Bouton qui permet d'ajouter une plante à la base de données
            case R.id.boutonEnregistrer:
                try {
                    Log.d(TAG, "bouton ajouter plante");
                    NomPlante = editTextNomPlante.getText().toString();
                    humidite = Integer.parseInt(editTextHumidite.getText().toString());
                    quantiteEau = Integer.parseInt(editTextQuantiteEau.getText().toString());
                    if (imageUri != null && !NomPlante.isEmpty() && humidite <= 100 && quantiteEau > 0 && humidite > 0) {
                        Log.d(TAG, "Ajout de la plante");

                        //Téléversement de la photo dans le dossier cache de l'app
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        File file = new File(getCacheDir(), NomPlante + ".jpg");
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        Uri uriImage = Uri.parse(getCacheDir() + "/" + NomPlante + ".jpg");
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, length);
                        }

                        fileOutputStream.flush();
                        fileOutputStream.close();
                        inputStream.close();

                        //Ajout des plantes dans la bd
                        BD_Plantes bd_plantes = new BD_Plantes(this);
                        bd_plantes.insert(uriImage,NomPlante, humidite,quantiteEau);
                        startActivity(intentAccueil);
                    } else {
                        Log.d(TAG, "Erreur lors de l'ajout de la plante, un champ est invalide");
                    }
                } catch (SQLiteException e) {
                    Log.e(TAG, "Erreur [SQLITE] lors de l'ajout de la plante : " + e.getMessage());
                    throw new RuntimeException(e);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "Erreur [Image] lors de l'ajout de la plante : " + e.getMessage());
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }
    public BD_Plantes getBD_Plantes()
    {
        return new BD_Plantes(this);
    }
}