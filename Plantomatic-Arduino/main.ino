#include <ArduinoMqttClient.h>
#include <WiFiNINA.h>
#include "arduino_secrets.h"

// Configuration dans le fichier arduino_secrets.h
char ssid[] = SECRET_SSID;
char pass[] = SECRET_PASS;

// Création d'un client wifi et configuration du client MQTT
WiFiClient wifiClient;
MqttClient mqttClient(wifiClient);

// Paramètres pour MQTT
const char broker[] = "test.mosquitto.org";
int        port     = 1883;
const char topic[]  = "real_unique_topic";

// Temps avant envoie d'un nouveau message
const long interval = 8000;
unsigned long previousMillis = 0;

int count = 0;

void setup() {
  Serial.begin(9600);
  // En attente de récupération du port serial
  while (!Serial) {
    ;
  }

  // Connexion au réseau Wifi
  Serial.print("Attempting to connect to WPA SSID: ");
  Serial.println(ssid);
  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }

  Serial.println("Connexion au réseau Wifi réussi !");
  Serial.println();

  Serial.print("En attente de connexion au broker: ");
  Serial.println(broker);

  if (!mqttClient.connect(broker, port)) {
    Serial.print("Impossible de se connecter au broker MQTT, Code Erreur = ");
    Serial.println(mqttClient.connectError());

    while (1);
  }

  Serial.println("Connexion au broker MQTT réussi !");
  Serial.println();
}

void loop() {
  // Permet de garder la connexion en vie pour la durée de fonctionnement du programme
  mqttClient.poll();

  unsigned long currentMillis = millis();

  // Envoi des messages à un intervalle X
  if (currentMillis - previousMillis >= interval) {
    // Sauvegarde du dernier envoi
    previousMillis = currentMillis;

    // Récupération d'une valeur aléatoire
    int rdmValue = analogRead(A0);

    Serial.print("Sending message to topic: ");
    Serial.println(topic);
    Serial.println(rdmValue);

    // Envoi des données sur le broker
    mqttClient.beginMessage(topic);
    mqttClient.print(rdmValue);
    mqttClient.endMessage();

    Serial.println();
  }
}