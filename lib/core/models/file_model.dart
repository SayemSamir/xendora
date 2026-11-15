import 'dart:io';

class XendoraFile {
  final String id;
  final String name;
  final String path;
  final int size;
  final String mimeType;
  final String? thumbnail;
  final DateTime lastModified;
  final String? checksum;

  XendoraFile({
    required this.id,
    required this.name,
    required this.path,
    required this.size,
    required this.mimeType,
    this.thumbnail,
    required this.lastModified,
    this.checksum,
  });

  factory XendoraFile.fromFile(File file) {
    final stat = file.statSync();
    return XendoraFile(
      id: file.path.hashCode.toString(),
      name: file.path.split(Platform.pathSeparator).last,
      path: file.path,
      size: stat.size,
      mimeType: _getMimeType(file.path),
      lastModified: stat.modified,
    );
  }

  static String _getMimeType(String path) {
    final ext = path.split('.').last.toLowerCase();
    switch (ext) {
      case 'jpg':
      case 'jpeg':
        return 'image/jpeg';
      case 'png':
        return 'image/png';
      case 'webp':
        return 'image/webp';
      case 'gif':
        return 'image/gif';
      case 'mp4':
        return 'video/mp4';
      case 'mkv':
        return 'video/x-matroska';
      case 'mov':
        return 'video/quicktime';
      case 'avi':
        return 'video/x-msvideo';
      case 'mp3':
        return 'audio/mpeg';
      case 'wav':
        return 'audio/wav';
      case 'flac':
        return 'audio/flac';
      case 'pdf':
        return 'application/pdf';
      case 'doc':
        return 'application/msword';
      case 'docx':
        return 'application/vnd.openxmlformats-officedocument.wordprocessingml.document';
      case 'xls':
        return 'application/vnd.ms-excel';
      case 'xlsx':
        return 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      case 'ppt':
        return 'application/vnd.ms-powerpoint';
      case 'pptx':
        return 'application/vnd.openxmlformats-officedocument.presentationml.presentation';
      case 'txt':
        return 'text/plain';
      case 'zip':
        return 'application/zip';
      case 'apk':
        return 'application/vnd.android.package-archive';
      case 'exe':
        return 'application/x-msdownload';
      default:
        return 'application/octet-stream';
    }
  }

  String get formattedSize {
    if (size < 1024) return '$size B';
    if (size < 1024 * 1024) return '${(size / 1024).toStringAsFixed(1)} KB';
    if (size < 1024 * 1024 * 1024) return '${(size / (1024 * 1024)).toStringAsFixed(1)} MB';
    return '${(size / (1024 * 1024 * 1024)).toStringAsFixed(2)} GB';
  }

  String get extension => name.split('.').last.toLowerCase();

  bool get isImage => mimeType.startsWith('image/');
  bool get isVideo => mimeType.startsWith('video/');
  bool get isAudio => mimeType.startsWith('audio/');
  bool get isDocument => mimeType.contains('pdf') || mimeType.contains('word') || mimeType.contains('spreadsheet') || mimeType.contains('presentation');
  bool get isArchive => mimeType.contains('zip') || extension == 'rar' || extension == '7z';
  bool get isExecutable => extension == 'apk' || extension == 'exe' || extension == 'dmg';
}