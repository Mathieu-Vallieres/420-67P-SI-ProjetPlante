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
const char CMDTopic[]  = BROKER_TOPIC_CMD;
const char RETURNTopic[]  = BROKER_TOPIC_RETURN;

// Temps avant envoie d'un nouveau message
const long interval = 15000;
unsigned long previousMillis = 0;

unsigned long waterInterval = 50000;
unsigned long previousWaterInterval = 0;

bool ledAllume = false; // [TEMP]

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

  SetupMQTTSubscribe();
}

void loop() {
  // Permet de garder la connexion en vie pour la durée de fonctionnement du programme
  mqttClient.poll();

  unsigned long currentMillis = millis();

  // Envoi des messages à un intervalle X
  if (currentMillis - previousMillis >= interval) {
    // Sauvegarde du dernier envoi
    previousMillis = currentMillis;

    //SendMQTTMessage("{\"CMD\":\"GET_HUMIDITY\"}");
    //SendMQTTCommand(GET_HUMIDITY);

    //Serial.println("");
    //Serial.println("");
    //Serial.println("");
  }

  if(waterInterval - previousWaterInterval >= waterInterval) {
    
  }
}
