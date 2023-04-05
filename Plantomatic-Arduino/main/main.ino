#include <ArduinoMqttClient.h>
#include <WiFiNINA.h>
#include "arduino_parameters.h"
#include "CommandType.h"

// Configuration dans le fichier arduino_secrets.h
char ssid[] = SECRET_SSID;
char pass[] = SECRET_PASS;

// Création d'un client wifi et configuration du client MQTT
WiFiClient wifiClient;
MqttClient mqttClient(wifiClient);

// Paramètres pour MQTT
const char broker[] = BROKER_IP;
int        port     = BROKER_PORT;
const char sendDataTopic[]  = BROKER_TOPIC_SENDDATA;
const char getDataTopic[]  = BROKER_TOPIC_GETDATA;

// Temps avant envoie d'un nouveau message
const long interval = 8000;
unsigned long previousMillis = 0;
bool ledAllume = false;

int count = 0;

void setup() {
  Serial.begin(9600);
  // En attente de récupération du port serial
  while (!Serial) {
    ;
  }

  pinMode(LED_BUILTIN, OUTPUT);
  ConnectWifi();
  ConnectBroker(mqttClient);

  SetupMQTTSubscribe(mqttClient);
}

void loop() {
  // Permet de garder la connexion en vie pour la durée de fonctionnement du programme
  mqttClient.poll();

  unsigned long currentMillis = millis();

  // Envoi des messages à un intervalle X
  if (currentMillis - previousMillis >= interval) {
    // Sauvegarde du dernier envoi
    previousMillis = currentMillis;

    SendMQTTMessage("get_humidity");

    Serial.println();
  }
}
