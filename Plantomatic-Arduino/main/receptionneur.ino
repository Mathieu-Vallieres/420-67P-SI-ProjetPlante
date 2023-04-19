#include <ArduinoJson.h>

// Fonction pour convertir un message que l'on reçois du broker pour le convertir en commande
CommandType ConvertMessageToEnum(String msg) {
  CommandType cmd = NONE;

  StaticJsonBuffer<200> jsonBuffer;
  JsonObject& root = jsonBuffer.parseObject(msg);

  if(!root.success())
    return cmd;

  if (root["CMD"] == "GET_HUMIDITY") {
    cmd = GET_HUMIDITY;
  } else if (root["CMD"] == "WATER") {
    cmd = WATER;
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

  CommandType cmd = ConvertMessageToEnum(message);
  if(cmd == NONE) { 
    Serial.print(message);
    return;
  }

  switch(cmd) {
    case NONE:
      break;
    // Quand on veut récupérer l'humidité de la plante
    case GET_HUMIDITY:
      int value;
      value = analogRead(0);
      Serial.print("Récupération de l'humidité : ");
      Serial.println(value);
      SendMQTTMessage("{\"RETURN_HUMIDITY\":\"" + String(value) + "\"}");
      break;
    // Quand on veut arroser la plante
    case WATER:
      if(ledAllume){
        digitalWrite(LED_BUILTIN,LOW);
        Serial.println("LED Eteint"); 
      }
      else {
        digitalWrite(LED_BUILTIN, HIGH);  
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
