// Fonction pour envoyer un message via MQTT
void SendMQTTMessage(String message) {

  Serial.println("sendMQttmessage");

  // Envoi des données sur le broker
  mqttClient.beginMessage(RETURNTopic);
  mqttClient.print(message);
  mqttClient.endMessage();
}

void SendMQTTCommand(CommandType type) {
  if(type == NONE)
    return;

  Serial.println("trezad");

  mqttClient.beginMessage(CMDTopic);
  mqttClient.print("{CMD:" + CommandToString(type) + "}");
  mqttClient.endMessage();
}