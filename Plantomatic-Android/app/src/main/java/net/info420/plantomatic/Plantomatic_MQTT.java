package net.info420.plantomatic;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class Plantomatic_MQTT {
    private int numeroPlante;
    private String commande;
    private boolean etatConnexion = false;
    private Context context;
    private MqttHelper mqttHelper;
    private MqttMessage message;

    public void envoi(String commande) {
        this.commande = commande;
        message = new MqttMessage();
        message.setPayload(commande.getBytes());

        try {
            mqttHelper.mqttAndroidClient.publish("plantomatic_hygro/cmd",message);
        } catch (MqttPersistenceException e) {
            Toast.makeText(context, "Erreur lors de l'envoie" + e, Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        } catch (MqttException e) {
            Toast.makeText(context, "Erreur lors de l'envoie" + e, Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }

    public Plantomatic_MQTT(Context context, int numeroPlante) {
        this.numeroPlante = numeroPlante;
        this.context = context;
        mqttHelper = new MqttHelper(context);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Toast.makeText(context, "Connexion réussi avec le broker", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void connectionLost(Throwable throwable) {
                Toast.makeText(context, "Connexion perdu avec le broker", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Valeur Recu du Broker : ",mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    }
}
