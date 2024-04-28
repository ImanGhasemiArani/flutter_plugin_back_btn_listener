import 'package:flutter/material.dart';

import 'package:back_btn_listener/back_btn_listener.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: PopScope(
        canPop: false,
        child: HomeView(),
      ),
    );
  }
}

class HomeView extends StatefulWidget {
  const HomeView({super.key});

  @override
  State<HomeView> createState() => _HomeViewState();
}

class _HomeViewState extends State<HomeView> {
  int backBtnPressedCount = 0;
  int backBtnLongPressedCount = 0;

  @override
  void initState() {
    super.initState();

    BackBtnListener.events.listen((event) {
      print(event);
      if (event is BackBtnPressedEvent) {
        setState(() => backBtnPressedCount++);
      } else if (event is BackBtnLongPressedEvent) {
        setState(() => backBtnLongPressedCount++);
      }
    });

    Future.delayed(const Duration(seconds: 15), () async {
      print('Requesting accessibility permission');
      final isGranted = await BackBtnListener.requestAccessibilityPermission();
      print('Accessibility permission is granted: $isGranted');
      await BackBtnListener.startListening();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Plugin example app'),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Text('Back btn pressed: $backBtnPressedCount'),
          const SizedBox(height: 20),
          Text('Back btn long pressed: $backBtnLongPressedCount'),
        ],
      ),
    );
  }
}
