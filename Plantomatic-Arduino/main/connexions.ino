// Fonction pour établir une connexion sur un réseau WIFI
void ConnectWifi() {
  Serial.print("Attempting to connect to WPA SSID: ");
  Serial.println(ssid);

  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }

  Serial.println("Connexion au réseau Wifi réussi !");
  Serial.println();
}

// Fonction pour établir une connexion à un broker
void ConnectBroker(MqttClient& client) {
  Serial.print("En attente de connexion au broker: ");
  Serial.println(broker);

  if (!client.connect(broker, port)) {
    Serial.print("Impossible de se connecter au broker MQTT, Code Erreur = ");
    Serial.println(client.connectError());

    while (1);
  }

  Serial.println("Connexion au broker MQTT réussi !");
  Serial.println();
}
