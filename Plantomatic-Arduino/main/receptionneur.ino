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

  switch(cmd) {
    case NONE:
      break;
    // Quand on veut récupérer l'humidité de la plante
    case HUMIDITE:
      {
        DHT22 dht22(A0);
        float temperature = dht22.getHumidity();

        Serial.print("t=");Serial.println(temperature);
        Serial.print("Récupération de l'humidité : ");
        Serial.println(temperature);
        SendMQTTMessage("{\"id\":\""+ String(id) +"\",\"HUMIDITE\":\"" + String(temperature, 1) + "\"}");
      }
      break;

    // Quand on veut arroser la plante
    case ARROSER:
      if(ledAllume){
        digitalWrite(A1,LOW);
        Serial.println("LED Eteint"); 
      }
      else {
        digitalWrite(A1, HIGH);  
        Serial.println("LED Allumee"); 
      }
    
      ledAllume = !ledAllume;
      break;
  }
}

// Fonction pour mettre en place le système d'écoute
void SetupMQTTSubscribe() {
  Serial.println("subscribe");

  mqttClient.onMessage(OnMqttMessage);
  mqttClient.subscribe(CMDTopic);
}
