import 'package:allesnl_mobile/views/widget_tree.dart';
import 'package:flutter/material.dart';
import 'package:allesnl_mobile/views/models/user.dart';
import 'package:allesnl_mobile/views/api/api_service.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  TextEditingController firstNameController = TextEditingController();
  TextEditingController lastNameController = TextEditingController();
  TextEditingController phoneNumberController = TextEditingController();
  TextEditingController emailController = TextEditingController();
  TextEditingController passwordController = TextEditingController();

  // Default to login mode
  bool isLogin = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // App Title
              Text(
                'AllesNL',
                style: TextStyle(
                  fontSize: 36,
                  fontWeight: FontWeight.bold,
                  color: Theme.of(context).colorScheme.primary,
                ),
              ),
              const SizedBox(height: 20),

              // This Card widget dynamically changes based on the `isLogin` state.
              Card(
                elevation: 5.0,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(15.0),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(24.0),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      // Title that changes based on isLogin state
                      Text(
                        isLogin ? 'Login' : 'Register',
                        style: TextStyle(color: Theme.of(context).colorScheme.primary, fontSize: 28, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 24),

                      // Conditionally show the Name field for registration
                      if (!isLogin)
                        TextFormField(
                          controller: firstNameController,
                          decoration: const InputDecoration(
                            labelText: 'First Name',
                            border: OutlineInputBorder(),
                            prefixIcon: Icon(Icons.person),
                          ),
                        ),
                      if (!isLogin) const SizedBox(height: 16),
                      if (!isLogin)
                        TextFormField(
                          controller: lastNameController,
                          decoration: const InputDecoration(
                            labelText: 'Last name',
                            border: OutlineInputBorder(),
                            prefixIcon: Icon(Icons.person),
                          ),
                        ),
                      if (!isLogin) const SizedBox(height: 16),

                      // Email field
                      TextFormField(
                        controller: emailController,
                        decoration: const InputDecoration(
                          labelText: 'Email',
                          border: OutlineInputBorder(),
                          prefixIcon: Icon(Icons.email),
                        ),
                        keyboardType: TextInputType.emailAddress,
                      ),
                      const SizedBox(height: 16),

                      // Phone Number field (only for registration)
                      if (!isLogin)
                        TextFormField(
                          controller: phoneNumberController,
                          decoration: const InputDecoration(
                            labelText: 'Phone Number',
                            border: OutlineInputBorder(),
                            prefixIcon: Icon(Icons.phone),
                          ),
                          keyboardType: TextInputType.phone,
                        ),
                      if (!isLogin) const SizedBox(height: 16),

                      // Password field
                      TextFormField(
                        controller: passwordController,
                        decoration: const InputDecoration(
                          labelText: 'Password',
                          border: OutlineInputBorder(),
                          prefixIcon: Icon(Icons.lock),
                        ),
                        obscureText: true,
                      ),
                      const SizedBox(height: 24),

                      // Dynamic button
                      FilledButton(
                        onPressed: () async {
                            if (!isLogin) {
                              // --- REGISTRATION LOGIC ---
                              final user = User(
                                  firstName: firstNameController.text,
                                  lastName: lastNameController.text,
                                  email: emailController.text,
                                  phoneNumber: phoneNumberController.text,
                                  password: passwordController.text);

                              final createdUser = await ApiService.createUser(user);

                              // It's good practice to check if the widget is still in the tree.
                              if (!mounted) return;

                              // Check the result of the API call.
                              if (createdUser != null) {
                                // SUCCESS: Show a confirmation and switch to login mode.
                                ScaffoldMessenger.of(context).showSnackBar(
                                  const SnackBar(content: Text('Registration successful! Please login.')),
                                );
                                // Clear the password field and switch to login mode
                                passwordController.clear();
                                setState(() {
                                  isLogin = true;
                                });
                              } else {
                                // FAILURE: Show an error message.
                                ScaffoldMessenger.of(context).showSnackBar(
                                  const SnackBar(content: Text('Registration Failed. Please try again.')),
                                );
                              }
                            } else {
                              // --- LOGIN LOGIC ---
                              final loginResponse = await ApiService.login(
                                emailController.text,
                                passwordController.text,
                              );

                              // It's good practice to check if the widget is still in the tree.
                              if (!mounted) return;

                              // Check the result of the API call.
                              if (loginResponse != null && loginResponse.success) {
                                // SUCCESS: Navigate to home with user info
                                String fullName = '${loginResponse.firstName ?? ''} ${loginResponse.lastName ?? ''}'.trim();
                                if (fullName.isEmpty) fullName = 'User';

                                Navigator.pushReplacement(context,
                                    MaterialPageRoute(builder: (context) {
                                  return WidgetTree(
                                    userName: loginResponse.firstName ?? 'User',
                                    userFullName: fullName,
                                    userEmail: emailController.text,
                                    userPhoneNumber: loginResponse.phoneNumber ?? '',
                                  );
                                }));
                              } else {
                                // FAILURE: Show an error message.
                                String errorMessage = loginResponse?.message ?? 'Login Failed. Please try again.';
                                ScaffoldMessenger.of(context).showSnackBar(
                                  SnackBar(content: Text(errorMessage)),
                                );
                              }
                            }

                          },
                        style: FilledButton.styleFrom(
                          backgroundColor: Theme.of(context).colorScheme.primary,
                          padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 12),
                          textStyle: const TextStyle(fontSize: 16),
                        ),
                        child: Text(isLogin ? 'Login' : 'Register'),
                      ),
                    ], 
                  ),
                ),
              ),
              const SizedBox(height: 20),

              // This TextButton replaces the Switch for a better user experience.
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    isLogin
                        ? "Don't have an account?"
                        : "Already have an account?",
                    style: const TextStyle(fontSize: 14),
                  ),
                  TextButton(
                    onPressed: () {
                      setState(() {
                        isLogin = !isLogin;
                      });
                    },
                    child: Text(
                      isLogin ? 'Register' : 'Login',
                      style: TextStyle(
                        color: Theme.of(context).colorScheme.primary,
                        fontSize: 14,
                      ),
                    ),
                  )
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
