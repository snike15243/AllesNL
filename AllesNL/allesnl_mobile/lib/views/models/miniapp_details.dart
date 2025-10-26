import 'dart:convert';

// A function to convert a list of JSON objects to a list of MiniAppDetails objects
List<MiniAppDetails> miniAppDetailsFromJson(String str) => List<MiniAppDetails>.from(json.decode(str).map((x) => MiniAppDetails.fromJson(x)));

// A function to convert a single MiniAppDetails object to a JSON string
String miniAppDetailsToJson(MiniAppDetails data) => json.encode(data.toJson());

class MiniAppDetails {
  // Properties adapted to match the Java record
  final int registrationId;
  final String appName;
  final String appDescription;
  final String logoUrl;

  // A const constructor for creating instances of the class.
  const MiniAppDetails({
    required this.registrationId,
    required this.appName,
    required this.appDescription,
    required this.logoUrl,
  });

  // The `fromJson` factory is updated with the new keys.
  factory MiniAppDetails.fromJson(Map<String, dynamic> json) => MiniAppDetails(
        registrationId: json["registrationId"],
        appName: json["appName"],
        appDescription: json["appDescription"],
        logoUrl: json["logoUrl"],
      );

  // The `toJson` method is updated with the new keys.
  Map<String, dynamic> toJson() => {
        "registrationId": registrationId,
        "appName": appName,
        "appDescription": appDescription,
        "logoUrl": logoUrl,
      };

  // Equality is now based on registrationId.
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is MiniAppDetails && runtimeType == other.runtimeType && registrationId == other.registrationId;

  @override
  int get hashCode => registrationId.hashCode;

  @override
  String toString() {
    return 'MiniAppDetails{registrationId: $registrationId, appName: $appName, appDescription: $appDescription, logoUrl: $logoUrl}';
  }
}
