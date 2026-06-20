# Phase 01: Async Initialization
Status: ✅ Completed

## Objective
Chuyển việc khởi tạo thư viện `YoutubeDL` và `FFmpeg` gốc sang luồng nền (`Dispatchers.IO`) bằng Coroutine để giải phóng luồng chính (Main Thread) lúc ứng dụng khởi động, đồng thời cập nhật UI để hiển thị trạng thái đang chuẩn bị nếu người dùng mở ứng dụng quá nhanh.

## Requirements
### Functional
- Khởi chạy ứng dụng ngay lập tức mà không bị đơ màn hình trắng.
- Người dùng không thể thực hiện thao tác tải nhạc cho đến khi thư viện đã sẵn sàng.
- Hiển thị thông báo thân thiện hoặc trạng thái chờ khi thư viện đang khởi tạo.

### Non-Functional
- Performance: Thời gian khởi chạy ứng dụng (App Startup Time) giảm tối thiểu 80%. Không gây ANR trên luồng chính.
- Security: Catch toàn bộ ngoại lệ trong quá trình nạp JNI, không để ứng dụng bị crash.

## Implementation Steps
1. [x] **Tạo đối chất quản lý khởi tạo** `LibraryInitializer` trong package `com.musicdownloader.app.util`:
   - Sử dụng `MutableStateFlow<Boolean>` để theo dõi xem thư viện đã sẵn sàng hay chưa.
   - Sử dụng `MutableStateFlow<String?>` để ghi nhận lỗi nạp (nếu có).
   - Hàm `initialize(context: Context)` chạy trên `CoroutineScope(Dispatchers.IO)` để chạy `YoutubeDL.getInstance().init` và `FFmpeg.getInstance().init`.
2. [x] **Cập nhật** `MusicDownloaderApp.kt`:
   - Thay thế việc init đồng bộ bằng cách gọi `LibraryInitializer.initialize(this)`.
3. [x] **Cập nhật** `DownloadViewModel.kt`:
   - Theo dõi StateFlow từ `LibraryInitializer` để cập nhật trạng thái UI.
4. [x] **Cập nhật** `MainScreen.kt`:
   - Lắng nghe trạng thái khởi tạo. Nếu chưa khởi tạo xong, disable nút "Download" và hiển thị trạng thái "Preparing download engine..." (Đang chuẩn bị công cụ tải...).

## Files to Create/Modify
- [NEW] [LibraryInitializer.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/util/LibraryInitializer.kt) - Quản lý trạng thái khởi tạo bất đồng bộ.
- [MODIFY] [MusicDownloaderApp.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/MusicDownloaderApp.kt) - Gọi khởi tạo bất đồng bộ.
- [MODIFY] [DownloadViewModel.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/ui/viewmodel/DownloadViewModel.kt) - Expose trạng thái init lên UI.
- [MODIFY] [MainScreen.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/ui/screens/MainScreen.kt) - Điều chỉnh nút Download và hiển thị loading/engine status.

## Test Criteria
- Khởi chạy ứng dụng kiểm tra log "YoutubeDL and FFmpeg initialized successfully" được ghi từ luồng IO.
- Ứng dụng không bị đứng hình khi vừa mở.
- Nút "Download" bị vô hiệu hóa kèm theo dòng chữ thông báo trạng thái khởi chạy của Engine cho đến khi tải thành công.

---
Next Phase: [phase-02-throttle-updates.md](file:///d:/skul9x/music-android-main/plans/260620-1925-fix-critical-performance/phase-02-throttle-updates.md)
