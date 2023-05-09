#include <ArduinoJson.h>
#include <DHT22.h>

// Fonction pour convertir un message que l'on reçois du broker pour le convertir en commande
CommandType ConvertStringToEnum(String msg) {
  CommandType cmd = NONE;

  if (msg == "HUMIDITE") {
    cmd = HUMIDITE;
  } else if (msg == "ARROSER") {
    cmd = ARROSER;
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
        DHT22 dht22(pStruct.capteurAnalog);
        float temperature = dht22.getHumidity();

        Serial.print("Récupération de l'humidité : ");
        Serial.println(temperature);
        SendMQTTMessage("{\"id\":\""+ String(id) +"\",\"HUMIDITE\":\"" + String(temperature, 1) + "\"}");
      }
      break;

    // Quand on veut arroser la plante
    case ARROSER:
      {
        Serial.println(pStruct.enabled);

        if(pStruct.enabled) {
          Serial.println("Lampe eteinte");
          digitalWrite(pStruct.pompeAnalog, LOW);
        } else {
          Serial.println("Lampe allumé");
          digitalWrite(pStruct.pompeAnalog, HIGH);
        }

        pStruct.enabled = !pStruct.enabled;
        pinDico.set(id, pStruct);
      }
      break;
  }

  Serial.println("");
  Serial.println("");
  Serial.println("");
}

// Fonction pour mettre en place le système d'écoute
void SetupMQTTSubscribe() {
  Serial.println("Subscribe to MQTT");

  mqttClient.onMessage(OnMqttMessage);
  mqttClient.subscribe(CMDTopic);
}
