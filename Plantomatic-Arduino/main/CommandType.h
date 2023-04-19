enum CommandType {
  NONE,
  GET_HUMIDITY,
  WATER,
  CONFIGURE
};

String CommandToString(CommandType type) {
  switch(type) {
    case GET_HUMIDITY: return "GET_HUMIDITY";
    case WATER: return "WATER";
  }
}