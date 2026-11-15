import 'package:flutter/material.dart';
import 'package:xendora/core/theme/colors.dart';

class SendScreen extends StatefulWidget {
  const SendScreen({super.key});

  @override
  State<SendScreen> createState() => _SendScreenState();
}

class _SendScreenState extends State<SendScreen> {
  bool _scanning = false;
  int _selectedDevice = -1;
  List<Device> _nearbyDevices = [];

  @override
  void initState() {
    super.initState();
    _scanDevices();
  }

  Future<void> _scanDevices() async {
    setState(() => _scanning = true);
    await Future.delayed(const Duration(seconds: 2));
    setState(() {
      _scanning = false;
      _nearbyDevices = [
        Device('PC-DESKTOP', 'Windows 11', 'LAN', '192.168.1.100'),
        Device('iPhone 14', 'iOS 17', 'Wi-Fi Direct', '192.168.1.101'),
      ];
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Send Files')),
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
            // Select files button
            Padding(
              padding: const EdgeInsets.all(20),
              child: OutlinedButton.icon(
                onPressed: () {},
                icon: const Icon(Icons.file_open),
                label: const Text('Select Files'),
              ),
            ),

            // Scan status
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: Row(
                children: [
                  const Text('Nearby Devices', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
                  const Spacer(),
                  TextButton.icon(
                    onPressed: _scanDevices,
                    icon: Icon(_scanning ? Icons.hourglass_empty : Icons.refresh),
                    label: const Text('Scan'),
                  ),
                ],
              ),
            ),

            // Device list
            Expanded(
              child: _scanning
                  ? const Center(child: CircularProgressIndicator())
                  : _nearbyDevices.isEmpty
                      ? Center(
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(Icons.devices, size: 64, color: AppColors.textTertiary),
                              const SizedBox(height: 16),
                              const Text('No devices found', style: TextStyle(color: AppColors.textSecondary)),
                              const SizedBox(height: 8),
                              OutlinedButton(
                                onPressed: _scanDevices,
                                child: const Text('Scan Again'),
                              ),
                            ],
                          ),
                        )
                      : ListView.builder(
                          itemCount: _nearbyDevices.length,
                          padding: const EdgeInsets.all(20),
                          itemBuilder: (context, index) {
                            final d = _nearbyDevices[index];
                            return Card(
                              margin: const EdgeInsets.only(bottom: 12),
                              child: ListTile(
                                leading: Icon(d.os.contains('Windows') ? Icons.desktop_windows : d.os.contains('iOS') ? Icons.phone_iphone : Icons.phone_android, color: AppColors.primary),
                                title: Text(d.name),
                                subtitle: Text('${d.os} - ${d.connectionType}'),
                                trailing: d.ipAddress,
                                onTap: () => setState(() => _selectedDevice = index),
                              ),
                            );
                          },
                        ),
            ),

            // Send button
            Padding(
              padding: const EdgeInsets.all(20),
              child: ElevatedButton(
                onPressed: _selectedDevice >= 0 ? () {} : null,
                child: const Text('Send Selected Files'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class Device {
  final String name;
  final String os;
  final String connectionType;
  final String ipAddress;

  Device(this.name, this.os, this.connectionType, this.ipAddress);
}
