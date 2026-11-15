import 'package:flutter/material.dart';
import 'package:xendora/core/theme/colors.dart';
import 'package:xendora/core/constants/app_constants.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Settings')),
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [AppColors.background, AppColors.surface],
          ),
        ),
        child: ListView(
          children: [
            _section('General', [
              _settingTile('Dark Mode', 'Always on', true),
              _settingTile('Language', 'English', false),
              _settingTile('Startup', 'Open dashboard', false),
            ]),
            _section('Transfer', [
              _settingTile('Download Folder', 'Default', false),
              _settingTile('Notifications', 'Enabled', false),
              _settingTile('Max Concurrent', '4 transfers', false),
              _settingTile('Auto Resume', 'Enabled', false),
            ]),
            _section('Privacy', [
              _settingTile('Visibility', 'Friends only', false),
              _settingTile('Incoming Requests', 'Ask for approval', false),
              _settingTile('Session Expiry', '10 minutes', false),
            ]),
            _section('About', [
              _settingTile('${AppConstants.appName} v${AppConstants.appVersion}', 'By ${AppConstants.appDeveloper}', false),
              _settingTile('Privacy Policy', 'View policy', false),
              _settingTile('Open Source Licenses', 'View licenses', false),
            ]),
            const SizedBox(height: 40),
          ],
        ),
      ),
    );
  }

  Widget _section(String title, List<Widget> tiles) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.fromLTRB(20, 16, 20, 8),
          child: Text(title, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w600, color: AppColors.textSecondary)),
        ),
        ...tiles,
      ],
    );
  }

  Widget _settingTile(String title, String subtitle, bool active) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 4),
      child: ListTile(
        title: Text(title),
        subtitle: Text(subtitle),
        trailing: active ? const Icon(Icons.check_circle, color: AppColors.success) : const Icon(Icons.arrow_forward_ios, size: 16),
        onTap: () {},
      ),
    );
  }
}
