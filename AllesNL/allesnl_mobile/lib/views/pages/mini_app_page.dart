import 'dart:convert'; // Import for jsonDecode
import 'dart:ffi';
import 'package:allesnl_mobile/views/api/api_service.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';

class MiniAppPage extends StatefulWidget {
  final String title;
  final String htmlContent;
  final int id; // This is the MiniApp's registration ID

  final String userName;
  final String userEmail;
  final String userPhoneNumber;

  const MiniAppPage({
    super.key,
    required this.title,
    required this.htmlContent,
    required this.id,
    required this.userName,
    required this.userEmail,
    required this.userPhoneNumber,
  });

  @override
  State<MiniAppPage> createState() => _MiniAppPageState();
}

class _MiniAppPageState extends State<MiniAppPage> {
  late final WebViewController _controller;

  @override
  void initState() {
    super.initState();

    _controller = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setOnConsoleMessage((JavaScriptConsoleMessage message) {
        debugPrint('-- WebView Console [${message.level.name}] --');
        debugPrint(message.message);
        debugPrint('------------------------------------');
      })
      ..addJavaScriptChannel(
        'Bridge', 
        onMessageReceived: (JavaScriptMessage message) {
          debugPrint('Command received from WebView: ${message.message}');
          _handleBridgeCommand(message.message);
        },
      )
      ..loadHtmlString(widget.htmlContent);
  }

  // This function is now a generic command router
  void _handleBridgeCommand(String jsonString) async {
    try {
      final Map<String, dynamic> command = jsonDecode(jsonString);

      final String method = command['method'] as String? ?? '';
      final bool sendUserData = command['sendUserData'] as bool? ?? false;
      final Map<String, dynamic> body = command['body'] as Map<String, dynamic>? ?? {};
      if(sendUserData){
        body['userName'] = widget.userName;
        body['userEmail'] = widget.userEmail;
      }
      final Map<String, String> headers = (command['headers'] as Map<String, dynamic>? ?? {})
          .map((key, value) => MapEntry(key, value.toString()));

      dynamic responseData;

      // Use a switch statement to route based on the method or header

      switch(method) {
          case 'POST':
            switch (headers['Data-Type']) {
              case 'payment':
                debugPrint('Routing to payment endpoint...');
                final amount = body['amount'] as int? ?? 0;
                responseData = await ApiService.getPaymentData(0, amount);
                break;

              default:
                debugPrint('Routing to generic post data endpoint...');
                responseData = await ApiService.postMiniAppData(widget.id, body, customHeaders: headers);
                break;
                }
          case 'GET':
            debugPrint('Routing to generic get data endpoint...');
            responseData = await ApiService.getMiniAppData(widget.id, customHeaders: headers);
            break;
          case 'DELETE':
            debugPrint('Routing to generic delete data endpoint...');
            responseData = await ApiService.deleteMiniAppData(widget.id, customHeaders: headers);
            break;
          case 'UPDATE':
            debugPrint('Routing to generic update data endpoint...');
            //responseData = await ApiService.updateMiniAppData(
            break;
          default:
            debugPrint('Unknown method: $method');
            break;
          }

        //debugPrint('Routing to generic post data endpoint...');
        //responseData = await ApiService.postMiniAppData(widget.id, body, customHeaders: headers);

      if (!mounted) return;

      if (responseData != null) {
        final jsonLiteral = jsonEncode(responseData);
        _controller.runJavaScript('updateStatus($jsonLiteral, false)');
      } else {
        _controller.runJavaScript('updateStatus("API call failed from Flutter.", true)');
      }

    } catch (e) {
      debugPrint('Error processing command from WebView: $e');
       _controller.runJavaScript('updateStatus("Error: Invalid command format from WebView.", true)');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: WebViewWidget(
        controller: _controller,
      ),
    );
  }
}
