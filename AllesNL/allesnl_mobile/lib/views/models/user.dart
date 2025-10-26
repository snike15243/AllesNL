import 'dart:convert';

// A function to convert a list of JSON objects to a list of User objects
List<User> userFromJson(String str) => List<User>.from(json.decode(str).map((x) => User.fromJson(x)));

// A function to convert a single User object to a JSON string
String userToJson(User data) => json.encode(data.toJson());

class User {
  // Properties are final to encourage immutability, which is a best practice in Flutter.
  final int? id; // Nullable to represent a user that hasn't been saved to the database yet.
  final String firstName;
  final String lastName;
  final String email;
  final String phoneNumber;
  final String? password; // Nullable because backend returns null for security

  // A const constructor for creating instances of the class.
  // Using named, required parameters is idiomatic Dart.
  const User({
    this.id,
    required this.firstName,
    required this.lastName,
    required this.email,
    required this.phoneNumber,
    this.password,
  });

  // A factory constructor for creating a new User instance from a map (e.g., from JSON).
  // This is essential for parsing API responses.
  factory User.fromJson(Map<String, dynamic> json) => User(
        id: json["id"],
        firstName: json["firstName"],
        lastName: json["lastName"],
        email: json["email"],
        phoneNumber: json["phoneNumber"],
        password: json["password"],
      );

  // A method for converting a User instance to a map.
  // This is essential for sending data in the body of a POST or PUT request.
  Map<String, dynamic> toJson() => {
        "id": id,
        "firstName": firstName,
        "lastName": lastName,
        "email": email,
        "phoneNumber": phoneNumber,
        "password": password,
      };

  // Overriding equals and hashCode to match the Java implementation (comparing by ID).
  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is User && runtimeType == other.runtimeType && id == other.id;

  @override
  int get hashCode => id.hashCode;

  // Overriding toString for easy debugging.
  @override
  String toString() {
    return 'User{id: $id, firstName: $firstName, lastName: $lastName, email: $email, phoneNumber: $phoneNumber, password: [HIDDEN]}';
  }
}
