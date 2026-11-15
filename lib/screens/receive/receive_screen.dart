import 'package:flutter/material.dart';
import 'package:xendora/core/theme/colors.dart';

class ReceiveScreen extends StatefulWidget {
  const ReceiveScreen({super.key});

  @override
  State<ReceiveScreen> createState() => _ReceiveScreenState();
}

class _ReceiveScreenState extends State<ReceiveScreen> {
  bool _isDiscoverable = false;
  String _receiveCode = 'XN-2847';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Receive Files')),
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [AppColors.background, AppColors.surface],
          ),
        ),
        child: Column(
          children: [
            // Discoverable toggle
            Padding(
              padding: const EdgeInsets.all(20),
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(20),
                  child: Column(
                    children: [
                      Row(
                        children: [
                          Icon(Icons.visibility, color: _isDiscoverable ? AppColors.success : AppColors.textTertiary),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                const Text('Discoverable', style: TextStyle(fontWeight: FontWeight.w600)),
                                Text(
                                  _isDiscoverable ? 'Other devices can see you' : 'Toggle to become discoverable',
                                  style: const TextStyle(fontSize: 12, color: AppColors.textSecondary),
                                ),
                              ],
                            ),
                          ),
                          Switch(
                            value: _isDiscoverable,
                            onChanged: (v) => setState(() => _isDiscoverable = v),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
            ),

            // QR Code
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(20),
                  child: Column(
                    children: [
                      const Text('Scan to Connect', style: TextStyle(fontWeight: FontWeight.w600)),
                      const SizedBox(height: 16),
                      Container(
                        width: 200,
                        height: 200,
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Icon(Icons.qr_code, size: 180, color: Colors.black87),
                      ),
                      const SizedBox(height: 16),
                      const Text('Or enter this code on the other device:', style: TextStyle(fontSize: 12, color: AppColors.textSecondary)),
                      Text(_receiveCode, style: const TextStyle(fontSize: 24, fontWeight: FontWeight.w700, letterSpacing: 4, color: AppColors.primary)),
                    ],
                  ),
                ),
              ),
            ),

            const Spacer(),

            // Scan QR button
            Padding(
              padding: const EdgeInsets.all(20),
              child: OutlinedButton.icon(
                onPressed: () {},
                icon: const Icon(Icons.qr_code_scanner),
                label: const Text('Scan QR Code'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
