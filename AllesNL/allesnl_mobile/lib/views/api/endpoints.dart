class ApiEndpoints {
  static const String baseUrl = "http://10.0.2.2:5002/api-gateway";

  static const String hello = "$baseUrl/hello";
  static const String user = "$baseUrl/user";
  static const String login = "$baseUrl/login";
  static const String payment = "$baseUrl/payment";
  static const String miniapp = "$baseUrl/miniapp";
  static const String sendData = "$baseUrl/miniapp/data/";
}