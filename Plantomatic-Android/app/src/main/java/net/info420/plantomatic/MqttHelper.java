package net.info420.plantomatic;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class MqttHelper {
    public MqttAndroidClient mqttAndroidClient;
    final String serverUri = "tcp://test.mosquitto.org:1883";
    final String clientId = "ExampleAndroidClient";
    final String subscriptionTopic = "plantomatic_hygro/return";
    final String publishingTopic = "plantomatic_hygro/cmd";

    /**
     * Constructeur de la classe avec les callback a surdéfinir
     * @param context le context de l'application
     */
    public MqttHelper(Context context){
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }
            @Override
            public void connectionLost(Throwable throwable) { }
            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) {
                Log.w("Mqtt", mqttMessage.toString());
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) { }
        });
        connect();
    }

    /**
     * Méthode définissant les callbacks a définir quand l'objet se créé
     * @param callback
     */
    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    /**
     * Méthode pour se connecter au broker
     */
    private void connect(){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to: " + serverUri + exception.toString());
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Fonction pour décodé le JSON envoyé par l'arduino
     * @param mqttMessage
     * @return la commande sous forme de String
     * @throws Exception
     */
    public String decodeJSON(MqttMessage mqttMessage) throws Exception{
        String jsonString = mqttMessage.toString();
        JSONObject obj = new JSONObject(jsonString);
        String commande = obj.getString("HUMIDITE");
        return commande;
    }

    /**
     * Méthode pour souscrire au topic de l'arduino
     */
    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt","Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Subscribed fail!");
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    /**
     * Méthode pour écrire sur un topic
     * @param commande la commande a envoyer sur le topic
     */
    public void publishToTopic(String commande) {
        MqttMessage message = new MqttMessage();
        message.setPayload(commande.getBytes());

        try {
            mqttAndroidClient.publish(publishingTopic, message);
        } catch (MqttException exception) {
            System.err.println("Exception lors de l'envoi du message");
            exception.printStackTrace();
        }
    }
}
