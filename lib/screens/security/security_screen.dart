import 'package:flutter/material.dart';
import 'package:xendora/core/theme/colors.dart';

class SecurityScreen extends StatelessWidget {
  const SecurityScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Security Center')),
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [AppColors.background, AppColors.surface],
          ),
        ),
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            children: [
              _securityTile('End-to-End Encryption', 'AES-256-GCM enabled', true),
              _securityTile('TLS 1.3', 'Transport layer secured', true),
              _securityTile('SHA-256 Verification', 'File integrity checking', true),
              _securityTile('Device Authentication', 'Paired device trust model', true),
              _securityTile('Active Sessions', '0 active sessions', true),
              _securityTile('Security Logs', 'View transfer logs', false),
            ],
          ),
        ),
      ),
    );
  }

  Widget _securityTile(String title, String subtitle, bool active) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: ListTile(
        leading: Icon(Icons.shield, color: active ? AppColors.success : AppColors.textTertiary),
        title: Text(title),
        subtitle: Text(subtitle),
        trailing: active ? const Icon(Icons.check_circle, color: AppColors.success) : const Icon(Icons.arrow_forward_ios, size: 16),
        onTap: () {},
      ),
    );
  }
}
