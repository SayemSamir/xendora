enum TransferStatus { pending, transferring, paused, completed, failed, cancelled }
enum TransferMethod { wifi, lan, bluetooth, internet }

class Transfer {
  final String id;
  final String fileName;
  final int fileSize;
  final String senderId;
  final String receiverId;
  final TransferMethod method;
  TransferStatus status;
  double progress;
  double speed; // bytes/sec
  final DateTime startedAt;
  DateTime? completedAt;
  bool checksumVerified;

  Transfer({
    required this.id,
    required this.fileName,
    required this.fileSize,
    required this.senderId,
    required this.receiverId,
    required this.method,
    this.status = TransferStatus.pending,
    this.progress = 0.0,
    this.speed = 0.0,
    required this.startedAt,
    this.completedAt,
    this.checksumVerified = false,
  });

  String get formattedSpeed {
    if (speed < 1024) return '${speed.toInt()} B/s';
    if (speed < 1024 * 1024) return '${(speed / 1024).toStringAsFixed(1)} KB/s';
    return '${(speed / (1024 * 1024)).toStringAsFixed(1)} MB/s';
  }

  Duration get estimatedTimeRemaining {
    if (speed <= 0) return Duration.zero;
    final remaining = fileSize - (fileSize * progress / 100);
    return Duration(seconds: (remaining / speed).ceil());
  }
}
