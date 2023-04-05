// Fonction pour envoyer un message via MQTT
void SendMQTTMessage(String message) {
  // Envoi des données sur le broker
  mqttClient.beginMessage(sendDataTopic);
  mqttClient.print(message);
  mqttClient.endMessage();

}