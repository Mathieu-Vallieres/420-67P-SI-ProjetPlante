// Fonction pour envoyer un message via MQTT
void SendMQTTMessage(String message) {
  if(message == "")
    return;

  // Envoi des données sur le broker
  mqttClient.beginMessage(RETURNTopic);
  mqttClient.print(message);
  mqttClient.endMessage();
}

void SendMQTTCommand(int id, CommandType type) {
  if(type == NONE)
    return;

  mqttClient.beginMessage(CMDTopic);
  mqttClient.print("{ID:" + String(id) + ",CMD:" + CommandToString(type) + "}");
  mqttClient.endMessage();
}