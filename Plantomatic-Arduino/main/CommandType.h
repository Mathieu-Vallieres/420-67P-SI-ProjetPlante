enum CommandType {
  NONE,
  HUMIDITE,
  ARROSER_ON,
  ARROSER_OFF,
  ARROSER_AUTO_ON,
  ARROSER_AUTO_OFF
};

String CommandToString(CommandType type) {
  switch(type) {
    case HUMIDITE: return "HUMIDITE";
    case ARROSER_ON: return "ARROSER_ON";
    case ARROSER_OFF: return "ARROSER_OFF";
    case ARROSER_AUTO_ON: return "ARROSER_AUTO_ON";
    case ARROSER_AUTO_OFF: return "ARROSER_AUTO_OFF";
  }
}