import 'package:allesnl_mobile/views/data/notifiers.dart';
import 'package:allesnl_mobile/views/login_page.dart';
import 'package:allesnl_mobile/views/widget_tree.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder(
      valueListenable: darkModeNotifier,
      builder: (context, darkMode, child) {
        return MaterialApp(
          debugShowCheckedModeBanner: false,
          theme: ThemeData(
            colorScheme: ColorScheme.fromSeed(
              // Set the seed color to orange to theme the app
              seedColor: Colors.white,
              brightness: darkMode ? Brightness.dark : Brightness.light,
              primary: Colors.orangeAccent
            ),
            useMaterial3: true, // Recommended for modern themes
          ),
          home: LoginPage(),
        );
      },
    );
  }
}
