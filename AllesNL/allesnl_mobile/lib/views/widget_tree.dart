import 'package:allesnl_mobile/views/data/notifiers.dart';
import 'package:flutter/material.dart';
import 'package:allesnl_mobile/views/pages/profile_page.dart';
import 'package:allesnl_mobile/views/pages/home_page.dart';

class WidgetTree extends StatelessWidget{
    final String userName;
    final String userFullName;
    final String userEmail;
    final String userPhoneNumber;

    const WidgetTree({
      super.key,
      required this.userName,
      required this.userFullName,
      required this.userEmail,
      required this.userPhoneNumber,
    });

    @override
    Widget build(BuildContext context){
      return ValueListenableBuilder(
        valueListenable: darkModeNotifier,
        builder: (context, darkMode, child) {
          return Scaffold(
            appBar: AppBar(
              backgroundColor: darkMode ? Colors.blueGrey : Colors.orangeAccent,
              leading: IconButton(
                icon: Icon(Icons.account_circle, color: Colors.white, size: 28),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => ProfilePage(
                        userName: userFullName,
                        userEmail: userEmail,
                        userPhoneNumber: userPhoneNumber,
                      ),
                    ),
                  );
                },
              ),
              title: Text('Hi $userName!', style: TextStyle(color: Colors.white)),
              centerTitle: true,
              actions: [
                IconButton(
                  onPressed: (){darkModeNotifier.value = !darkMode;},
                  icon: darkMode ? Icon(Icons.light_mode) : Icon(Icons.dark_mode),
                ),
              ],
            ),
            // Pass the user data down to the HomePage
            body: HomePage(
              userName: userName,
              userEmail: userEmail,
              userPhoneNumber: userPhoneNumber,
            ),
          );
        }
      );
    }
}
