package net.info420.plantomatic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Activité qui gère l'arrosage manuellement
 */
public class ActiviteModeManuel extends AppCompatActivity {

    /**
     * TAG de l'activité
     */
    public static final String TAG = "manuel";

    /**
     * tirroir sur le coté de l'application
     */
    DrawerLayout drawerLayout;
    /**
     * La vue de Navigation
     */
    NavigationView navigationView;
    /**
     * La barre d'action du tirroir
     */
    ActionBarDrawerToggle actionBarDrawerToggle;
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
     * Liste ou se trouvent les plantes enregistrés
     */
    ListView listViewPlantes;
    /**
     * Bouton pour ouvrir la valve et arroser les plantes
      */
    Button btnArroser;
    /**
     * Boite de texte contenant l'humidite recu par le capteur
     */
    TextView txtViewHumidite;

    Cursor curseur;
    SimpleCursorAdapter adapteur;
    MyViewBinder viewBinder;
    static final String[] from = { BD_Plantes.C_IMAGE, BD_Plantes.C_NOMPLANTE };
    static final int[] to = { R.id.row_listePlante_Image, R.id.row_listePlante_Nom };


    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility", "Range"})

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
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.menu_nav);
        btnArroser = findViewById(R.id.btnAjouterPlante);
        txtViewHumidite = findViewById(R.id.TextViewQuantiteMl);

        //Ouverture du menu navigation et ajout du bouton pour l'ouvrir dans la barre d'action
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.ouvrirMenu, R.string.fermerMenu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Ouverture de la BD
        curseur = this.getBD_Plantes().query();
        adapteur = new SimpleCursorAdapter(this, R.layout.layout_row_listeplante, curseur, from, to, 0);
        viewBinder = new MyViewBinder();
        adapteur.setViewBinder(viewBinder);
        listViewPlantes.setAdapter(adapteur);

        // Listner de la listview
        listViewPlantes.setOnItemClickListener((parent, view, position, id) -> {
            Log.i("ActivitePrincipale", "Position: " + position + " id: " + id);
            Intent intent = new Intent(this, ActiviteAffichage.class);
            Cursor c = (Cursor) listViewPlantes.getItemAtPosition(position);
            intent.putExtra("idPlante", c.getInt(c.getColumnIndex(BD_Plantes.C_ID)));
            intent.putExtra("nomPlante", c.getString(c.getColumnIndex(BD_Plantes.C_NOMPLANTE)));
            intent.putExtra("imagePlante", c.getString(c.getColumnIndex(BD_Plantes.C_IMAGE)));
            startActivity(intent);
        });

        // Listener du menu de navigation
        navigationView.setNavigationItemSelectedListener(item -> {
            //Switch pour les différents items du menu
            switch (item.getItemId())
            {
                //Cas de l'activité principal, on ferme le tirroir et on lance l'activité
                case R.id.item_activitePrincipale:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(intentAccueil);
                    break;
                //Si on est déjà dans l'activité mode manuel, on ferme le tirroir
                case R.id.item_activiteModeManuel:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                //Cas de l'activité paramètres, on ferme le tirroir et on lance l'activité
                case R.id.item_activiteParametres:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(intentParametres);
                    break;
            }
            return true;
        });


        //Création objet MQTT
        PlantomaticMQTT plantomaticMQTT = new PlantomaticMQTT(getApplicationContext());
        //mettre en place les callback, l'objet va se connecté au broker a la fin
        plantomaticMQTT.setCallback(new MqttCallbackExtended() {
            //Une fois la connection complétée
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Toast.makeText(ActiviteModeManuel.this, "Connecté au broker" + serverURI, Toast.LENGTH_SHORT).show();
            }
            //Si la connexion est perdue au broker
            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(ActiviteModeManuel.this, "Connexion perdue: " + cause, Toast.LENGTH_LONG).show();
            }

            // Lorsqu'un message arrive
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                    CharSequence charSequence =  plantomaticMQTT.decodeJSONHumidite(message.toString());
                    txtViewHumidite.setText(charSequence);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) { }
        });

        //Listener du bouton pour arrose au toucher
        btnArroser.setOnTouchListener((view, motionEvent) -> {
             // Si je maintient le bouton enfoncé
             if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                 try {
                     //Envoi d'ouvrir la valve
                     plantomaticMQTT.publishToTopic("{ID:0,CMD:ARROSER_ON}");
                     Log.i(TAG, "Ouverture de pompe");
                 } catch (Exception e) {
                     throw new RuntimeException(e);
                 }
             }
             // Si je relache le bouton
             if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                 try {
                     //Envoi de fermé la valve
                     plantomaticMQTT.publishToTopic("{ID:0,CMD:ARROSER_OFF}");
                     Log.i(TAG, "Fermeture de pompe");
                 } catch (Exception e) {
                     throw new RuntimeException(e);
                 }
             }
             //Tout les autres cas, il ne se passe rien
             return false;
         });
    }

    /**
     * Quand une option est sélectionnée dans la liste
     * @param option l'option sélectionnée
     * @return true ou false
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

    public BD_Plantes getBD_Plantes()
    {
        return new BD_Plantes(this);
    }

    private class MyViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int fieldIndex) {
            return false;
        }
    }
}