import 'package:flutter/material.dart';
import 'package:xendora/core/theme/colors.dart';

class WelcomeScreen extends StatelessWidget {
  const WelcomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [AppColors.background, AppColors.surface],
          ),
        ),
        child: SafeArea(
          child: Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(Icons.share, size: 100, color: AppColors.primary),
                const SizedBox(height: 32),
                const Text(
                  'XENDORA',
                  style: TextStyle(
                    fontSize: 48,
                    fontWeight: FontWeight.w700,
                    color: AppColors.textPrimary,
                    letterSpacing: 4,
                  ),
                ),
                const SizedBox(height: 8),
                const Text(
                  'Universal File Transfer',
                  style: TextStyle(
                    fontSize: 20,
                    color: AppColors.textSecondary,
                  ),
                ),
                const SizedBox(height: 48),
                _featureTile(Icons.wifi, 'Wi-Fi Direct Transfer'),
                _featureTile(Icons.security, 'End-to-End Encrypted'),
                _featureTile(Icons.devices, 'Cross-Platform Support'),
                _featureTile(Icons.speed, 'Lightning Fast'),
                const Spacer(),
                ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    minimumSize: const Size.fromHeight(56),
                  ),
                  onPressed: () => Navigator.of(context).pushReplacementNamed('/'),
                  child: const Text('Get Started'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _featureTile(IconData icon, String label) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Row(
        children: [
          Icon(icon, color: AppColors.primary, size: 24),
          const SizedBox(width: 16),
          Text(label, style: const TextStyle(fontSize: 16, color: AppColors.textPrimary)),
        ],
      ),
    );
  }
}
