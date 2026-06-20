# Phase 02: Throttling Progress Updates
Status: ✅ Completed
Dependencies: [Phase 01](file:///d:/skul9x/music-android-main/plans/260620-1925-fix-critical-performance/phase-01-async-init.md)

## Objective
Áp dụng cơ chế Throttling (tiết lưu) cho callback báo cáo tiến trình của `YoutubeDL.execute` nhằm giảm thiểu số lượng sự kiện gửi lên UI và Notification, tránh gây quá tải CPU và lag hệ thống.

## Requirements
### Functional
- Cập nhật tiến độ tải lên thanh thông báo và màn hình ứng dụng mượt mà, trực quan.
- Đảm bảo hiển thị ngay các thời điểm quan trọng: lúc bắt đầu (0%), lúc đổi bài (trong playlist), lúc hoàn thành (100%), và trạng thái xử lý hậu kỳ (post-processing).

### Non-Functional
- Performance: Giảm số lượng cập nhật UI Recomposition và Notification Manager đi 90%. Không gây lag giao diện khi tải tốc độ cao.

## Implementation Steps
1. [x] **Cập nhật** `DownloadRepository.kt`:
   - Khai báo biến `lastUpdateTime` và `throttleInterval = 500L` trong phương thức `download`.
   - Trong JNI callback của `YoutubeDL.getInstance().execute`:
     - Kiểm tra nếu `currentItem` thay đổi so với giá trị cũ (chỉ dấu chuyển bài trong playlist).
     - Kiểm tra xem thời gian hiện tại so với `lastUpdateTime` có lớn hơn hoặc bằng `500ms` hay không.
     - Chỉ kích hoạt callback `onProgress` khi: tiến độ đạt 100%, hoặc bắt đầu tải (0%), hoặc đổi bài trong playlist, hoặc khoảng thời gian từ lần cập nhật trước đạt `500ms`.

## Files to Create/Modify
- [MODIFY] [DownloadRepository.kt](file:///d:/skul9x/music-android-main/app/src/main/java/com/musicdownloader/app/data/repository/DownloadRepository.kt) - Cài đặt thuật toán Throttling trong callback.

## Test Criteria
- Tiến hành tải thử 1 bài hát và quan sát thanh tiến trình cập nhật đều đặn (không giật đơ liên tục).
- Đo đạc thời gian log tiến độ hiển thị không quá dày (tối đa 2 lần một giây).
- Đảm bảo khi chuyển từ tải sang post-processing (như convert audio) vẫn hiển thị dòng chữ thông báo thích hợp.

---
Next Phase: [phase-03-testing.md](file:///d:/skul9x/music-android-main/plans/260620-1925-fix-critical-performance/phase-03-testing.md)
