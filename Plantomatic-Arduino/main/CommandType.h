enum CommandType {
  NONE,
  HUMIDITE,
  ARROSER
};

String CommandToString(CommandType type) {
  switch(type) {
    case HUMIDITE: return "HUMIDITE";
    case ARROSER: return "ARROSER";
  }
}