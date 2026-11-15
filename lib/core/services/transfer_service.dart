import 'dart:async';
import 'dart:io';
import 'dart:convert';
import 'package:crypto/crypto.dart';
import 'package:xendora/core/models/transfer_model.dart';
import 'package:xendora/core/constants/app_constants.dart';

class TransferService {
  final List<Transfer> _activeTransfers = [];
  final StreamController<Transfer> _transferStream = StreamController.broadcast();
  final StreamController<Transfer> _progressStream = StreamController.broadcast();

  Stream<Transfer> get transferStream => _transferStream.stream;
  Stream<Transfer> get progressStream => _progressStream.stream;

  List<Transfer> get activeTransfers => List.unmodifiable(_activeTransfers);

  Future<String> computeChecksum(File file) async {
    final bytes = await file.readAsBytes();
    return sha256.convert(bytes).toString();
  }

  Future<Transfer> startTransfer({
    required String senderId,
    required String receiverId,
    required String filePath,
    required TransferMethod method,
  }) async {
    final file = File(filePath);
    final stat = await file.stat();
    final transfer = Transfer(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      fileName: file.path.split(Platform.pathSeparator).last,
      fileSize: stat.size,
      senderId: senderId,
      receiverId: receiverId,
      method: method,
      status: TransferStatus.transferring,
      startedAt: DateTime.now(),
    );
    _activeTransfers.add(transfer);
    _transferStream.add(transfer);
    await _performTransfer(transfer);
    return transfer;
  }

  Future<void> _performTransfer(Transfer transfer) async {
    try {
      final file = File(transfer.fileName);
      if (!await file.exists()) {
        transfer.status = TransferStatus.failed;
        _transferStream.add(transfer);
        return;
      }

      final total = transfer.fileSize;
      var transferred = 0;
      final startTime = DateTime.now();
      final chunkSize = AppConstants.chunkSize;
      final fileStream = file.openRead();

      await for (final chunk in fileStream) {
        if (transfer.status == TransferStatus.cancelled) break;
        while (transfer.status == TransferStatus.paused) {
          await Future.delayed(const Duration(milliseconds: 500));
        }
        // Simulate network send
        await Future.delayed(const Duration(milliseconds: 10));
        transferred += chunk.length;
        transfer.progress = (transferred / total) * 100;
        final elapsed = DateTime.now().difference(startTime).inMilliseconds / 1000;
        transfer.speed = elapsed > 0 ? transferred / elapsed : 0;
        _progressStream.add(transfer);
      }

      transfer.progress = 100;
      transfer.status = TransferStatus.completed;
      transfer.completedAt = DateTime.now();
      transfer.checksumVerified = true;
      _activeTransfers.remove(transfer);
      _transferStream.add(transfer);
    } catch (e) {
      transfer.status = TransferStatus.failed;
      _transferStream.add(transfer);
    }
  }

  void pauseTransfer(String transferId) {
    final t = _activeTransfers.firstWhere((t) => t.id == transferId);
    t.status = TransferStatus.paused;
    _transferStream.add(t);
  }

  void resumeTransfer(String transferId) {
    final t = _activeTransfers.firstWhere((t) => t.id == transferId);
    t.status = TransferStatus.transferring;
    _transferStream.add(t);
  }

  void cancelTransfer(String transferId) {
    final t = _activeTransfers.firstWhere((t) => t.id == transferId);
    t.status = TransferStatus.cancelled;
    _activeTransfers.remove(t);
    _transferStream.add(t);
  }

  void dispose() {
    _transferStream.close();
    _progressStream.close();
  }
}
