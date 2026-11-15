import 'package:flutter/material.dart';

class AppColors {
  static const primary = Color(0xFF00B4FF);
  static const primaryDark = Color(0xFF0091D4);
  static const accent = Color(0xFF7C3AED);
  static const cyan = Color(0xFF00F5FF);

  static const background = Color(0xFF0A0E27);
  static const surface = Color(0xFF111630);
  static const cardBg = Color(0xFF161B3D);
  static const cardBgLight = Color(0xFF1C2148);

  static const textPrimary = Color(0xFFEAEAEA);
  static const textSecondary = Color(0xFF8A8EA0);
  static const textTertiary = Color(0xFF5A5E70);

  static const success = Color(0xFF00D68F);
  static const error = Color(0xFFFF4757);
  static const warning = Color(0xFFFEA602);
  static const info = Color(0xFF3B82F6);

  static const gradientStart = Color(0xFF00B4FF);
  static const gradientEnd = Color(0xFF7C3AED);
  static const cyanGradient = Color(0xFF00F5FF);
  static const cyanGradientEnd = Color(0xFF00B4FF);

  static const glassOverlay = Color(0x1A00B4FF);
  static const glassBorder = Color(0x26EAEAEA);
  static const glassSurface = Color(0x0D161B3D);

  static const divider = Color(0x1FEAEAEA);
  static const shadow = Color(0x40000000);
}

class AppGradients {
  static const LinearGradient primary = LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    colors: [AppColors.gradientStart, AppColors.gradientEnd],
  );

  static const LinearGradient cyan = LinearGradient(
    begin: Alignment.centerLeft,
    end: Alignment.centerRight,
    colors: [AppColors.cyanGradient, AppColors.cyanGradientEnd],
  );

  static const LinearGradient background = LinearGradient(
    begin: Alignment.topCenter,
    end: Alignment.bottomCenter,
    colors: [AppColors.surface, AppColors.background],
  );

  static const LinearGradient glass = LinearGradient(
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
    colors: [Color(0x1A00B4FF), Color(0x0D161B3D)],
  );
}