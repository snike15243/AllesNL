import 'dart:convert';
import 'package:allesnl_mobile/views/models/miniapp_details.dart';
import 'package:flutter/foundation.dart';
import 'package:allesnl_mobile/views/api/endpoints.dart';
import 'package:allesnl_mobile/views/models/user.dart';
import 'package:allesnl_mobile/views/models/login_response.dart';
import 'package:http/http.dart' as http;

class ApiService {
  
  static Future<String?> getHello() async {
    try {
      final response = await http.get(Uri.parse(ApiEndpoints.hello));

      if (response.statusCode == 200) {
        return response.body;
      } else {
        print("Error: ${response.statusCode}");
        return null;
      }
    } catch (e) {
      print("Exception: $e");
      return null;
    }
  }


  static Future<User?> getUser(int id) async {
    try {
      final response = await http.get(Uri.parse("${ApiEndpoints.user}/$id"));
      if (response.statusCode == 200) {
        return User.fromJson(jsonDecode(response.body));
      } else {
        print("Failed to get user: ${response.statusCode}");
        return null;
      }
    } catch (e) {
      print("Exception: $e");
      return null;
    }
  }


  static Future<User?> createUser(User user) async {
    debugPrint("Creating user: $user");
    try {
      final response = await http.post(
        Uri.parse(ApiEndpoints.user),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(user.toJson()),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        debugPrint("Registration successful");
        return User.fromJson(jsonDecode(response.body));
      } else {
        debugPrint("Registration failed: ${response.statusCode}");
        debugPrint("Response body: ${response.body}");
        return null;
      }
    } catch (e) {
      debugPrint("Registration error: $e");
      return null;
    }
  }


  static Future<LoginResponse?> login(String email, String password) async {
    try {
      final requestBody = {
        "email": email,
        "password": password,
      };

      final response = await http.post(
        Uri.parse(ApiEndpoints.login),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(requestBody),
      );

      if (response.statusCode == 200) {
        debugPrint("Login successful");
        return LoginResponse.fromJson(jsonDecode(response.body));
      } else if (response.statusCode == 401) {
        debugPrint("Login failed: Invalid credentials");
        return LoginResponse.fromJson(jsonDecode(response.body));
      } else {
        debugPrint("Login failed: ${response.statusCode}");
        return null;
      }
    } catch (e) {
      debugPrint("Login error: $e");
      return null;
    }
  }


  static Future<List<MiniAppDetails>?> getAllMiniAppDetails() async {
    try {
      final response = await http.get(Uri.parse("${ApiEndpoints.miniapp}/all"));
      if (response.statusCode == 200) {
        final List<dynamic> decodedList = jsonDecode(response.body);
        return decodedList.map((item) => MiniAppDetails.fromJson(item)).toList();
      } else {
        print("Failed to get mini apps: ${response.statusCode}");
        return null;
      }
    } catch (e) {
      print("Exception: $e");
      return null;
    }
  }


  static Future<String?> getMiniAppById(int id) async {
    try {
      final response = await http.get(Uri.parse("${ApiEndpoints.miniapp}/$id"));
      if (response.statusCode == 200) {
        return response.body;
      } else {
        print("Failed to get mini app $id: ${response.statusCode}");
        return null;
      }
    } catch (e) {
      print("Exception: $e");
      return null;
    }
  }

  static Future<String?> getMiniAppByIdUser(int id, User user) async {
    try {
      final response = await http.post(
          Uri.parse("${ApiEndpoints.miniapp}/$id"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(user.toJson()),
      );
      if (response.statusCode == 200) {
        return response.body;
      } else {
        print("Failed to get mini app $id: ${response.statusCode}");
        return null;
      }
    } catch (e) {
      print("Exception: $e");
      return null;
    }
  }

  static Future<Map<String, dynamic>?> postMiniAppData(
      int id,
      Map<String, dynamic> body,
      {Map<String, String>? customHeaders}
      ) async {
    final headers = {'Content-Type': 'application/json'};
    if (customHeaders != null) headers.addAll(customHeaders);
    debugPrint('Posting to ${ApiEndpoints.miniapp}/data/$id with headers: $headers');
    try {
      final response = await http.post(
        Uri.parse('${ApiEndpoints.miniapp}/data/$id'),
        headers: headers,
        body: jsonEncode(body),
      );
      if (response.statusCode == 200) {
        final Map<String, dynamic> envelope = jsonDecode(response.body);
        return envelope['data'] as Map<String, dynamic>?;
      } else {
        debugPrint('Failed to post mini-app data: ${response.statusCode}\nBody: ${response.body}');
        return null;
      }
    } catch (e) {
      debugPrint('Exception posting mini-app data: $e');
      return null;
    }
  }

  // New function for GET /miniapp/data/{id}
  static Future<Map<String, dynamic>?> getMiniAppData(
      int id,
      {Map<String, String>? customHeaders}
      ) async {
    final headers = {'Content-Type': 'application/json'};
    if (customHeaders != null) headers.addAll(customHeaders);
    debugPrint('Getting from ${ApiEndpoints.miniapp}/data/$id with headers: $headers');
    try {
      final response = await http.get(
        Uri.parse('${ApiEndpoints.miniapp}/data/$id'),
        headers: headers,
      );
      if (response.statusCode == 200) {
        final Map<String, dynamic> envelope = jsonDecode(response.body);
        return envelope['data'] as Map<String, dynamic>?;
      } else {
        debugPrint('Failed to get mini-app data: ${response.statusCode}\nBody: ${response.body}');
        return null;
      }
    } catch (e) {
      debugPrint('Exception getting mini-app data: $e');
      return null;
    }
  }

  // New function for DELETE /miniapp/data/{id}
  static Future<Map<String, dynamic>?> deleteMiniAppData(
      int id,
      {Map<String, String>? customHeaders}
      ) async {
    final headers = {'Content-Type': 'application/json'};
    if (customHeaders != null) headers.addAll(customHeaders);
    debugPrint('Deleting from ${ApiEndpoints.miniapp}/data/$id with headers: $headers');
    try {
      final response = await http.delete(
        Uri.parse('${ApiEndpoints.miniapp}/data/$id'),
        headers: headers,
      );
      if (response.statusCode == 200) {
        final Map<String, dynamic> envelope = jsonDecode(response.body);
        return envelope['data'] as Map<String, dynamic>?;
      } else {
        debugPrint('Failed to delete mini-app data: ${response.statusCode}\nBody: ${response.body}');
        return null;
      }
    } catch (e) {
      debugPrint('Exception deleting mini-app data: $e');
      return null;
    }
  }

  // Renamed from makePayment to be more accurate
  static Future<Map<String, dynamic>?> getPaymentData(int userId, int amount) async {
    try {
      final response = await http.get(Uri.parse('${ApiEndpoints.payment}/$userId/$amount'));
      if (response.statusCode == 200) {
        return jsonDecode(response.body) as Map<String, dynamic>;
      } else {
        debugPrint('Failed to get payment data: ${response.statusCode}\nBody: ${response.body}');
        return null;
      }
    } catch (e) {
      debugPrint('Exception getting payment data: $e');
      return null;
    }
  }
}
