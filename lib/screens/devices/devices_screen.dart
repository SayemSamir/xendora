import 'package:flutter/material.dart';
import 'package:xendora/core/theme/colors.dart';

class DevicesScreen extends StatelessWidget {
  const DevicesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Connected Devices')),
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [AppColors.background, AppColors.surface],
          ),
        ),
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.devices, size: 80, color: AppColors.textTertiary),
              const SizedBox(height: 16),
              const Text(
                'No connected devices',
                style: TextStyle(fontSize: 18, color: AppColors.textSecondary),
              ),
              const SizedBox(height: 8),
              const Text(
                'Paired devices will appear here',
                style: TextStyle(fontSize: 14, color: AppColors.textTertiary),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
