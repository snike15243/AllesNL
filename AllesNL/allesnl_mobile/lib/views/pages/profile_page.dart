import 'package:flutter/material.dart';
import 'package:allesnl_mobile/views/login_page.dart';

class ProfilePage extends StatelessWidget{
  final String userName;
  final String userEmail;
  final String userPhoneNumber;

  const ProfilePage({
    super.key,
    required this.userName,
    required this.userEmail,
    required this.userPhoneNumber,
  });

  @override
  Widget build(BuildContext context){
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.primary,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.white),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
        title: const Text('Account', style: TextStyle(color: Colors.white)),
        centerTitle: true,
      ),
      body: Container(
        padding: const EdgeInsets.all(20),
        width: double.infinity,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children:[
            const SizedBox(height: 40),
            SizedBox(
              height: 50,
              width: double.infinity,
              child:ListTile(
                title: const Text("Name"),
                subtitle: Text(userName)
            )),
            const SizedBox(height: 10),
            SizedBox(
              height: 50,
              width: double.infinity,
              child: ListTile(
                title: const Text("Email"),
                subtitle: Text(userEmail)
            )),
            const SizedBox(height: 10),
            SizedBox(
              height: 50,
              width: double.infinity,
              child: ListTile(
                title: const Text("Phone Number"),
                subtitle: Text(userPhoneNumber)
            )),
            const SizedBox(height: 40),
            SizedBox(
              width: double.infinity,
              child: FilledButton(
                onPressed: () {
                  Navigator.pushAndRemoveUntil(
                    context,
                    MaterialPageRoute(builder: (context) => const LoginPage()),
                    (route) => false,
                  );
                },
                style: FilledButton.styleFrom(
                  backgroundColor: Colors.red,
                  padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
                ),
                child: const Text('Logout', style: TextStyle(color: Colors.white, fontSize: 16)),
              ),
            ),
          ]
        )
      ),
    );
  }
}
