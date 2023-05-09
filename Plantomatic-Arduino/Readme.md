# Documentation Arduino
## Projet utilisé pour exemple
      
[Lien du projet](https://community.dfrobot.com/makelog-312988.html)

## Pièce requise pour faire notre projet

- 1 MKR1010 WIFI
- 1 Capteur sans-contact de capacité de liquide
- 1 Gravity: capteur d'humidité analogique de la terre imperméable 
- 1 Convertisseur DC-DC ajustable
- 1 Module de relais
- 1 valve Solenoid
- 1 Boite imprimer en 3D
- 1 plateforme ou mettre le contenant d'eau
- 1 Contenant d'eau
- 1 Bouche d'irrigation
- plusieurs risselants

![Plan de l'arduino](https://github.com/Mathieu-Vallieres/420-67P-SI-ProjetPlante/assets/75104224/c1bb668c-483c-457e-bdce-6ce80a84e054)


## MQTT

Version MQTT : 5

Broker MQTT: [test.mosquitto.org](https://test.mosquitto.org)

Port Broker: "1883"

Sujet Broker: 

"plantomatic_hygro/CMD"

 - publisheur: Android
 - Subscribeur: Arduino
  
"plantomatic_hygro/RETURN"

 - publisheur: Arduino
 - Subscribeur: Android
