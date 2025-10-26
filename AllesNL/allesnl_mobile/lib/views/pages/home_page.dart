import 'package:allesnl_mobile/views/api/api_service.dart';
import 'package:allesnl_mobile/views/models/miniapp_details.dart';
import 'package:allesnl_mobile/views/widgets/mini_app_card.dart';
import 'package:flutter/material.dart';

class HomePage extends StatefulWidget {
  // 1. Add the properties to receive the user data
  final String userName;
  final String userEmail;
  final String userPhoneNumber;

  // 2. Update the constructor
  const HomePage({
    super.key,
    required this.userName,
    required this.userEmail,
    required this.userPhoneNumber,
  });

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  late Future<List<MiniAppDetails>?> _miniAppsFuture;

  @override
  void initState() {
    super.initState();
    _miniAppsFuture = ApiService.getAllMiniAppDetails(); 
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List<MiniAppDetails>?>(
      future: _miniAppsFuture,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }

        if (snapshot.hasError) {
          return Center(child: Text('Error: ${snapshot.error}'));
        }

        if (snapshot.hasData && snapshot.data!.isNotEmpty) {
          final miniApps = snapshot.data!;
          return Scrollbar(
            child: ListView.builder(
              padding: const EdgeInsets.all(16.0),
              itemCount: miniApps.length,
              itemBuilder: (context, index) {
                // 3. Pass the user data down to the MiniAppCard
                return MiniAppCard(
                  miniApp: miniApps[index],
                  userName: widget.userName,
                  userEmail: widget.userEmail,
                  userPhoneNumber: widget.userPhoneNumber,
                );
              },
            ),
          );
        }

        return const Center(child: Text('No mini-apps are available.'));
      },
    );
  }
}
