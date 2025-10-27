import 'package:allesnl_mobile/views/api/api_service.dart';
import 'package:allesnl_mobile/views/models/miniapp_details.dart';
import 'package:allesnl_mobile/views/models/user.dart';
import 'package:allesnl_mobile/views/pages/mini_app_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart' show rootBundle; // Import for local asset loading

class MiniAppCard extends StatelessWidget {
  final MiniAppDetails miniApp;
  // 1. Add the properties to receive user data
  final String userName;
  final String userEmail;
  final String userPhoneNumber;
  //final int userId;

  // 2. Update the constructor
  const MiniAppCard({
    super.key,
    required this.miniApp,
    required this.userName,
    required this.userEmail,
    required this.userPhoneNumber,
    //required this.userId,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 3,
      margin: const EdgeInsets.symmetric(vertical: 8.0),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12.0)),
      child: ListTile(
        contentPadding: const EdgeInsets.all(12.0),
        leading: CircleAvatar(
          radius: 30,
          backgroundImage: NetworkImage(miniApp.logoUrl),
          onBackgroundImageError: (exception, stackTrace) {},
          child: const Icon(Icons.apps, color: Colors.white),
        ),
        title: Text(miniApp.appName, style: const TextStyle(fontWeight: FontWeight.bold)),
        subtitle: Text(
          miniApp.appDescription,
          maxLines: 2,
          overflow: TextOverflow.ellipsis,
        ),
        trailing: const Icon(Icons.arrow_forward_ios, size: 16),
        onTap: () async {
          // --- DEVELOPMENT OVERRIDE: Uncomment the line below to load local dummy HTML ---
          //final String htmlContent = await rootBundle.loadString('assets/html/index.html');


          // --- PRODUCTION CODE ---
          //final user = User(id: 0, firstName: userName, lastName: userName, email: userEmail, phoneNumber: userPhoneNumber);
          //final String? htmlContent = await ApiService.getMiniAppByIdUser(miniApp.registrationId , user);
          final String? htmlContent = await ApiService.getMiniAppById(miniApp.registrationId);

          if (!context.mounted) return;

          if (htmlContent != null) {
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => MiniAppPage(
                  title: miniApp.appName,
                  id: miniApp.registrationId,
                  htmlContent: htmlContent,
                  // 3. Pass the user data to the MiniAppPage
                  //userId: userId,
                  userName: userName,
                  userEmail: userEmail,
                  userPhoneNumber: userPhoneNumber,
                ),
              ),
            );
          } else {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('Could not load Mini-App.')),
            );
          }
        },
      ),
    );
  }
}
