#include <ArduinoJson.h>
#include <DHT22.h>

// Fonction pour convertir un message que l'on reçois du broker pour le convertir en commande
CommandType ConvertStringToEnum(String msg) {
  CommandType cmd = NONE;

  if (msg == "HUMIDITE") {
    cmd = HUMIDITE;
  } else if (msg == "ARROSER_ON") {
    cmd = ARROSER_ON;
  } else if (msg == "ARROSER_OFF") {
    cmd = ARROSER_OFF;
  } else if (msg == "ARROSER_AUTO_ON") {
    cmd = ARROSER_AUTO_ON;
  } else if (msg == "ARROSER_AUTO_OFF") {
    cmd = ARROSER_AUTO_OFF;
  }

  if(cmd != NONE)
    Serial.println("Message reçu : " + msg);

  return cmd;
}

// Quand on reçois un message du broker
void OnMqttMessage(int messageSize) {
  // Récupération du message que l'on reçois du réseau MQTT
  String message = "";

  while (mqttClient.available()) {
    message += String((char)mqttClient.read());
  }

  Serial.println("Message : " + message);

  StaticJsonBuffer<200> jsonBuffer;
  JsonObject& json = jsonBuffer.parseObject(message);

  if(!json.success()) return;

  CommandType cmd = ConvertStringToEnum(json["CMD"]);
  String stringId = json["ID"];

  int id = stringId.toInt();
  TraiterMessage(id, cmd);
}

void TraiterMessage(int id, CommandType cmd) {
  if(cmd == NONE) { 
    return;
  }

  // Récupération des pins associés à l'id en paramètre
  pinStruct pStruct = pinDico.get(id);

  switch(cmd) {
    case NONE:
      break;
    // Quand on veut récupérer l'humidité de la plante
    case HUMIDITE:
      {
        float humidite = analogRead(pStruct.capteurAnalog);
        int rValue = 0;

        Serial.println("Valeur de l'humidité récupéré : " + String(humidite));
        if(humidite >= 190 && humidite < 380)
        {
          rValue = 1;
        }
        if(humidite >= 380)
        {
          rValue = 2;
        }
        Serial.println("Valeur de retour : " + String(rValue));
        SendMQTTReponse("{\"id\":\""+ String(id) +"\",\"HUMIDITE\":\"" + String(rValue) + "\"}");
      }
      break;
    // Quand ouvrir la valve
    case ARROSER_ON:
      {
        Serial.println("Identifiant : " + String(pStruct.pompeAnalog));

        digitalWrite(pStruct.pompeAnalog, HIGH);
        // On met le temps de l'arrosage automatique sur l'identifiant de la plante
        autoArrosage.set(id, 0);
      }
      break;
    // Quand fermer la valve
    case ARROSER_OFF:
      {
        digitalWrite(pStruct.pompeAnalog, LOW);
        // On met le temps de l'arrosage automatique sur l'identifiant de la plante
        autoArrosage.set(id, 0);
      }
      break;
    
    // Quand on veut démarrer le système d'arrosage automatique de la plante
    case ARROSER_AUTO_ON:
      {
        Serial.println("ARROSER_AUTO_ON");
        autoArrosage.add(id, 0);
      }
      break;
    // Quand on veut arreter le système d'arrosage automatique de la plante
    case ARROSER_AUTO_OFF:
      {
        digitalWrite(pStruct.pompeAnalog, LOW);
        autoArrosage.remove(id);
      }
      break;
  }

  Serial.println("");
  Serial.println("");
  Serial.println("");
}

// Fonction pour mettre en place le système d'écoute sur le topic des commandes
void SetupMQTTSubscribe() {
  Serial.println("Abonné au broker MQTT");

  mqttClient.onMessage(OnMqttMessage);
  mqttClient.subscribe(CMDTopic);
}
