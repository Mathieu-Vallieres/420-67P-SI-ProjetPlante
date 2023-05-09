package net.info420.plantomatic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ActiviteModeManuel extends AppCompatActivity {

    public static final String TAG = "manuel";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Intent intentAccueil;
    Intent intentDetails;
    Intent intentManuel;
    Intent intentParametres;
    Button btnArroser;
    TextView textView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activitemodemanuel);

        //Génération des intents pour les options du menu de navigation
        intentAccueil = new Intent(this, ActivitePrincipale.class);
        intentDetails = new Intent(this, ActiviteAffichage.class);
        intentManuel = new Intent(this, ActiviteModeManuel.class);
        intentParametres = new Intent(this, ActiviteParametres.class);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.menu_nav);
        btnArroser = findViewById(R.id.btnAjouterPlante);
        textView = findViewById(R.id.TextViewQuantiteMl);

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
        //Création objet MQTT
        PlantomaticMQTT plantomaticMQTT = new PlantomaticMQTT(getApplicationContext());
        //mettre en place les callback, l'objet va se connecté au broker a la fin
        plantomaticMQTT.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Toast.makeText(ActiviteModeManuel.this, "Connecté au broker" + serverURI, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(ActiviteModeManuel.this, "Connexion perdue: " + cause, Toast.LENGTH_LONG).show();
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                    CharSequence charSequence =  plantomaticMQTT.decodeJSONHumidite(message.toString());
                    textView.setText(charSequence);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) { }
        });

        btnArroser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    try {
                        plantomaticMQTT.publishToTopic("{CMD:ARROSER_ON}");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        plantomaticMQTT.publishToTopic("{CMD:ARROSER_OFF}");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                return false;
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