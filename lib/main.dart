import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:xendora/core/theme/app_theme.dart';
import 'package:xendora/core/constants/app_constants.dart';
import 'package:xendora/screens/splash/splash_screen.dart';
import 'package:xendora/screens/welcome/welcome_screen.dart';
import 'package:xendora/screens/dashboard/dashboard_screen.dart';
import 'package:xendora/screens/send/send_screen.dart';
import 'package:xendora/screens/receive/receive_screen.dart';
import 'package:xendora/screens/history/history_screen.dart';
import 'package:xendora/screens/devices/devices_screen.dart';
import 'package:xendora/screens/security/security_screen.dart';
import 'package:xendora/screens/settings/settings_screen.dart';

void main() {
  runApp(const XendoraApp());
}

class XendoraApp extends StatelessWidget {
  const XendoraApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: AppConstants.appName,
      theme: AppTheme.darkTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: ThemeMode.dark,
      initialRoute: '/splash',
      routes: {
        '/splash': (context) => const SplashScreen(),
        '/welcome': (context) => const WelcomeScreen(),
        '/': (context) => const DashboardScreen(),
        '/send': (context) => const SendScreen(),
        '/receive': (context) => const ReceiveScreen(),
        '/history': (context) => const HistoryScreen(),
        '/devices': (context) => const DevicesScreen(),
        '/security': (context) => const SecurityScreen(),
        '/settings': (context) => const SettingsScreen(),
      },
    );
  }
}