package net.info420.plantomatic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
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


/**
 * Classe gérant l'activité d'affichage des plantes
 */
public class ActiviteAffichage extends AppCompatActivity implements View.OnClickListener{

    /**
     * TAG de l'activité
     */
    public final String TAG = "affichage";

    /**
     * Tirroir sur le coté
     */
    DrawerLayout drawerLayout;
    /**
     * La vue de navigation de l'actvvité
     */
    NavigationView navigationView;
    /**
     * la barre d'action du tirroir
     */
    ActionBarDrawerToggle actionBarDrawerToggle;
    /**
     * Zone de texte pour le nom de la plante
     */
    EditText editTextNomPlante;
    /**
     * ZOne de texte pour l'humidité de la plante
     */
    EditText editTextHumidite;
    /**
     * Zone de texte pour la quantité d'eau de la plante
     */
    EditText editTextQuantiteEau;
    /**
     * Bouton pour aller chercher une photo de la plante
     */
    Button boutonPhotoPlante;
    /**
     * Vue sur l'image sélectionnée dans l'appareil mobile
     */
    ImageView imageViewPhoto;
    /**
     * Bouton pour supprimer la photo choisie
     */
    ImageButton boutonPoubelle;
    /**
     * Bouton pour enregistrer les modifications
     */
    ImageButton boutonEnregistrer;

    /**
     * Lien vers l'acceuil dans le tirroir
     */
    Intent intentAccueil;
    /**
     * Lien vers les détails dans le tirroir
     */
    Intent intentDetails;
    /**
     * Lien vers le mode manuel dans le tirroir
     */
    Intent intentManuel;
    /**
     * Lien vers les paramètres dans le tirroir
     */
    Intent intentParametres;
    /**
     *  Intent pour sélectionné que les images dans la bibliothèque d'image
     */
    Intent intentImage;

    /**
     * URI de l'image à mettre dans la base de données
     */
    private Uri imageUri;
    /**
     * Nom de la plante à ajouté dans la base de données
     */
    private String NomPlante;
    /**
     * Quel humidité à mettre dans la base de données
     */
    private int humidite;
    /**
     * quel quantité d'eau à mettre dans la base de données
     */
    private int quantiteEau;


    /**
     * Évènement quand on option est choisi dans le menu
     * @param option
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem option)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(option))
        {
            return true;
        }

        return super.onOptionsItemSelected(option);
    }

    /**
     * Démarrage de l'activité
     * @param savedInstanceState Instance géré par le système d'exploitation
     */
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