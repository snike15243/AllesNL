import 'dart:convert';

// A function to convert a single LoginResponse object to a JSON string
String loginResponseToJson(LoginResponse data) => json.encode(data.toJson());

class LoginResponse {
  final bool success;
  final String message;
  final String? firstName;
  final String? lastName;
  final String? phoneNumber;

  const LoginResponse({
    required this.success,
    required this.message,
    this.firstName,
    this.lastName,
    this.phoneNumber,
  });

  // A factory constructor for creating a new LoginResponse instance from a map (e.g., from JSON).
  factory LoginResponse.fromJson(Map<String, dynamic> json) => LoginResponse(
        success: json["success"],
        message: json["message"],
        firstName: json["firstName"],
        lastName: json["lastName"],
        phoneNumber: json["phoneNumber"],
      );

  // A method for converting a LoginResponse instance to a map.
  Map<String, dynamic> toJson() => {
        "success": success,
        "message": message,
        "firstName": firstName,
        "lastName": lastName,
        "phoneNumber": phoneNumber,
      };

  @override
  String toString() {
    return 'LoginResponse{success: $success, message: $message, firstName: $firstName, lastName: $lastName, phoneNumber: $phoneNumber}';
  }
}
