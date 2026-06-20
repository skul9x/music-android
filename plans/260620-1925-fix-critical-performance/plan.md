# Plan: Khắc phục 2 lỗi hiệu năng Nghiêm trọng (Critical Performance Issues)
Created: 2026-06-20T19:25:00+07:00
Status: 🟡 In Progress

## Overview
Kế hoạch này tập trung xử lý triệt để hai lỗi hiệu năng nghiêm trọng nhất đã được chẩn đoán trong ứng dụng **Music Downloader**:
1. **Nghẽn luồng chính lúc khởi chạy ứng dụng (App Startup Block/Lag)** do khởi tạo YoutubeDL & FFmpeg đồng bộ.
2. **Quá tải cập nhật thông báo và giao diện (Notification & UI Update Flood)** do tiến độ tải được bắn liên tục hàng trăm lần/giây.

## Tech Stack
- Kotlin
- Jetpack Compose
- Kotlin Coroutines (StateFlow, Dispatchers.IO)
- Android Foreground Service

## Phases

| Phase | Name | Status | Progress |
|-------|------|--------|----------|
| 01 | [Async Initialization](file:///d:/skul9x/music-android-main/plans/260620-1925-fix-critical-performance/phase-01-async-init.md) | ⬜ Pending | 0% |
| 02 | [Throttling Progress Updates](file:///d:/skul9x/music-android-main/plans/260620-1925-fix-critical-performance/phase-02-throttle-updates.md) | ⬜ Pending | 0% |
| 03 | [Verification & Testing](file:///d:/skul9x/music-android-main/plans/260620-1925-fix-critical-performance/phase-03-testing.md) | ⬜ Pending | 0% |

## Quick Commands
- Bắt đầu Phase 1: `/code phase-01`
- Kiểm tra tiến độ: `/next`
- Lưu bộ não: `/save-brain`

---
Next Phase: [phase-01-async-init.md](file:///d:/skul9x/music-android-main/plans/260620-1925-fix-critical-performance/phase-01-async-init.md)
