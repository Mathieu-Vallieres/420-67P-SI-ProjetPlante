package net.info420.plantomatic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class ActiviteStatistiques extends AppCompatActivity {
    MqttHelper mqttHelper;
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activitestatistiques);
        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        startMqtt();

    }
    private void startMqtt(){
        MqttMessage message = new MqttMessage();
        Context test = getApplicationContext();
        message.setPayload("{CMD:GET_HUMIDITY}".getBytes());

        mqttHelper = new MqttHelper(test);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Toast.makeText(ActiviteStatistiques.this, "Connexion réussi avec le broker", Toast.LENGTH_SHORT).show();
                button.setOnClickListener(v -> {
                    try {
                        mqttHelper.mqttAndroidClient.publish("plantomatic_hygro/cmd",message);
                    } catch (MqttPersistenceException e) {
                        Toast.makeText(ActiviteStatistiques.this, "Erreur lors de l'envoie" + e, Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    } catch (MqttException e) {
                        Toast.makeText(ActiviteStatistiques.this, "Erreur lors de l'envoie" + e, Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    }
                });
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Toast.makeText(ActiviteStatistiques.this, "Connexion perdu avec le broker", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
                textView.setText(mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}