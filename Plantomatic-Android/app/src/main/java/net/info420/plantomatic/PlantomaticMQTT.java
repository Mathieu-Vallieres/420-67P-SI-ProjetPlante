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
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Objet MQTT, permet la communication MQTT avec le broker
 */
public class PlantomaticMQTT {
    /**
     * Objet de la librairie Paho de eclispe
     */
    public MqttAndroidClient mqttAndroidClient;
    /**
     * Serveur du courtier
     */
    final String serveurURI = "tcp://test.mosquitto.org:1883";
    /**
     * ID du client
     */
    final String clientId = "AndroidClient";
    /**
     * Sujet d'abonnement
     */
    final String sujetAbonnement = "plantomatic_hygro/return";
    /**
     * Sujet publiement
     */
    final String sujetPubliement = "plantomatic_hygro/cmd";

    /**
     * Constructeur de l'objet MQTT, ouvre la connection, se connecte et gère les callback
     * @param context
     */
    public PlantomaticMQTT(Context context){
        mqttAndroidClient = new MqttAndroidClient(context, serveurURI, clientId);

        //Gabarit des callback
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("mqtt", s);
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Mqtt", mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

        //Connection au courtier
        connect();
    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    /**
     * Procédure pour se connecter au courtier
     */
    private void connect(){
        //OPtions de connection
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true); //Va essayer de se reconnecter
        mqttConnectOptions.setCleanSession(false); //Peux commencer avec des données en attente

        try {

            //Connection au courtier
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                //Quand il réussi
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    try {
                        subscribeToTopic();//S'abonne au sujet donné
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                }

                // Quand il échoue
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Failed to connect to: " + serveurURI + exception.toString());
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Méthode pour s'abonner a un sujet
     */
    private void subscribeToTopic() throws MqttException {

        //Abonnement sur le sujet
        mqttAndroidClient.subscribe(sujetAbonnement, 0, null, new IMqttActionListener() {
            //Quand il réussi
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.w("Mqtt","Subscribed!");
            }

            //Quand il échoue
            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.w("Mqtt", "Subscribed fail!");
            }
        });
    }

    /**
     * Méthode pour publier sur un sujet
     * @param commande Commande à envoyé sur le sujet
     * @throws MqttException Erreur de MQTT
     */
    public void publishToTopic(String commande) throws MqttException{
        mqttAndroidClient.publish(sujetPubliement, new MqttMessage(commande.getBytes()));
    }

    /**
     * Fonction pour récupéré et décodé le JSON contenant l'humidité
     * @param mqttMessage Le message recu
     * @return À quel point la plante est humide, peut être: très sec, sec, humide ou indisponible
     * @throws JSONException erreur sur le JSON
     */
    public String decodeJSONHumidite(String mqttMessage) throws JSONException{

        String jsonString = mqttMessage;
        JSONObject object = new JSONObject(jsonString);
        String humidite = object.getString("HUMIDITE");

        switch(humidite){
            case "0":
                return "Humide";
            case "1":
                return "Sec";
            case "2":
                return "Très sec";
            default:
                return "Indisponible";
        }
    }
}