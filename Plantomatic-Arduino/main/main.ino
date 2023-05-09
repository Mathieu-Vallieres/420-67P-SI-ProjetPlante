#include <ArduinoMqttClient.h>
#include <WiFiNINA.h>

#include "arduino_parameters.h"
#include "CommandType.h"
#include "pinStruct.h"
#include "Dictionary.h"

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
const long intervalHumidite = 15000;
unsigned long long previousHumiditeMillis = 0;

unsigned long long previous1SecondMillis = 0;
unsigned long long tempsEntreChaqueArrosage = 30;
int dureeArrosage = 10;

Dictionary<int, pinStruct> pinDico;
Dictionary<int, unsigned long long> autoArrosage;

void setup() {
  Serial.begin(9600);
  // En attente de récupération du port serial
  while (!Serial) {
    ;
  }

  pinMode(11, OUTPUT);

  SetupPins();

  ConnectWifi();

  ConnectBroker(mqttClient);

  SetupMQTTSubscribe();
}

void loop() {
  // Permet de garder la connexion en vie pour la durée de fonctionnement du programme
  mqttClient.poll();

  unsigned long long currentMillis = millis();

  // Envoi des messages à un intervalle X
  if (currentMillis - previousHumiditeMillis >= intervalHumidite) {
    // Sauvegarde du dernier envoi
    previousHumiditeMillis = currentMillis;

    SendMQTTCommand(0, HUMIDITE);
  }

  if(currentMillis - previous1SecondMillis >= 1000) {
    previous1SecondMillis = currentMillis;

    LinkedList<int> keys = autoArrosage.GetKeys();
    for(int i = 0; i < keys.size(); i++) {
      unsigned long time = autoArrosage.get(keys.get(i)) + 1;
      Serial.println("For id : " + String(keys.get(i)) + " | time : " + String(time));

      // On peut lancer l'arrosage
      if(time >= tempsEntreChaqueArrosage) {
        pinStruct pStruct = pinDico.get(keys.get(i));

        // On démarre l'arrosage
        if(time == tempsEntreChaqueArrosage) {
          digitalWrite(pStruct.pompeAnalog, HIGH);
        }

        // L'arrosage est terminé
        if(time - tempsEntreChaqueArrosage >= dureeArrosage) {
          digitalWrite(pStruct.pompeAnalog, LOW);
          time = 0;
        }
      }

      autoArrosage.set(keys.get(i), time);
    }
  }
}

void SetupPins() {
  pinDico.set(0, {0, A0, 11, false});
}