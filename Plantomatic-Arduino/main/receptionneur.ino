// Fonction pour convertir un message que l'on reçois du broker pour le convertir en command
CommandType ConvertMessageToEnum(String msg) {
  CommandType cmd = NONE;

  if (msg == "GET_HUMIDITY") {
    cmd = GET_HUMIDITY;
  } else if (msg == "WATER") {
    cmd = WATER;
  } else {
    cmd = NONE;
  }

  return cmd;
}

// Quand on reçois un message du broker
void OnMqttMessage(int messageSize) {
  // Récupération du message que l'on reçois du réseau MQTT
  String message = "";

  while (mqttClient.available()) {
    message += String((char)mqttClient.read());
  }

  CommandType cmd = ConvertMessageToEnum(message);
  if(cmd == NONE) { 
    Serial.print(message);
    return;
  }

  Serial.println(message);
}

// Fonction pour mettre en place le système d'écoute
void SetupMQTTSubscribe(MqttClient& client) {
  client.onMessage(OnMqttMessage);
  client.subscribe(getDataTopic);
}
