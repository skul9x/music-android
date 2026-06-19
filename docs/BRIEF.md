# 💡 BRIEF: Music Downloader

**Ngày tạo:** 2026-06-19  
**Brainstorm cùng:** skul9x  

---

## 1. VẤN ĐỀ CẦN GIẢI QUYẾT
- Khó khăn trong việc tải nhạc chất lượng cao (.m4a) và video từ YouTube, TikTok trực tiếp trên điện thoại Android một cách nhanh chóng.
- Các ứng dụng tải nhạc miễn phí hiện nay thường chứa rất nhiều quảng cáo rác, không hỗ trợ tự động nhúng ảnh bìa (Cover Art) của YouTube/TikTok vào file nhạc để hiển thị đẹp mắt trên các ứng dụng nghe nhạc.
- Các app tải dễ bị dừng hoạt động (lỗi tải) khi YouTube thay đổi thuật toán và người dùng phải đợi nhà phát triển cập nhật app lên chợ ứng dụng Store.

## 2. GIẢI PHÁP ĐỀ XUẤT
Xây dựng ứng dụng native Android **"Music Downloader"** bằng **Kotlin** + **Jetpack Compose** + **Material 3**:
- Tích hợp thư viện **`youtubedl-android`** (chạy `yt-dlp` và `ffmpeg` cục bộ trực tiếp trên máy qua JNI và Python-for-Android) để tải offline không qua server trung gian.
- Tự động tải và nhúng Cover Art chất lượng cao vào siêu dữ liệu (Metadata) của file nhạc.
- Tích hợp tính năng tự động cập nhật engine `yt-dlp` thông qua nút "Update Engine" ngay trong app.
- Giao diện **Glassmorphism** tối giản và hiện đại.

## 3. ĐỐI TƯỢNG SỬ DỤNG
- **Primary:** Bản thân Anh và những người dùng muốn có một công cụ tải nhạc/video YouTube & TikTok sạch sẽ, chất lượng cao và giao diện đẹp trên Android.

## 4. NGHIÊN CỨU GIẢI PHÁP & THỊ TRƯỜNG
### Đối thủ & Thư viện tham khảo:
| Ứng dụng | Điểm mạnh | Điểm yếu |
|----------|-----------|----------|
| **Seal** | UI Material You rất đẹp, nhiều tuỳ chỉnh tải, cập nhật engine tốt. | Giao diện có nhiều cấu hình sâu dễ làm người dùng cơ bản bối rối. |
| **YTDLnis** | Có thanh search và trình duyệt tích hợp, tải đa luồng mạnh mẽ. | UI khá rối, tập trung nhiều vào tính năng nâng cao. |

### Giải pháp kỹ thuật cho Music Downloader:
- Sử dụng **`yausername/youtubedl-android`** kết hợp với thư viện con **`ffmpeg`** bọc sẵn của nó để thực hiện toàn bộ tác vụ tải & convert offline trên điện thoại.
- Để cập nhật yt-dlp, gọi trực tiếp API `YoutubeDL.getInstance().updateYoutubeDL(context)`.

### Điểm khác biệt của Music Downloader:
- Giao diện **Glassmorphism** sang trọng mang đậm chất "Vibe Coding" thừa hưởng từ bản Desktop.
- Tập trung vào tính năng tải nhạc nhanh bằng 1 nút bấm và tự nhúng bìa đĩa (Cover Art) sạch sẽ.

## 5. TÍNH NĂNG

### 🚀 MVP (Bắt buộc có):
- [ ] Dán URL YouTube/TikTok nhanh và tự nhận diện link.
- [ ] Chọn định dạng tải:
  - **M4A Audio:** Tải nhạc chất lượng cao + tự nhúng Cover Art (Thumbnail).
  - **1080p Video / 720p Video:** Tải video và tự động ghép hình + tiếng bằng FFmpeg cục bộ.
- [ ] Hiển thị tiến trình tải chi tiết: Phần trăm (%), Tốc độ tải (MB/s), và thời gian dự kiến (ETA).
- [ ] Quản lý thư mục lưu tệp tải xuống bằng Android SAF (Storage Access Framework).
- [ ] Nút "Sửa lỗi tải (Update Engine)" để cập nhật yt-dlp trong ứng dụng.

### 🎁 Phase 2 (Làm sau):
- [ ] Đọc tự động link từ clipboard khi mở app.
- [ ] Chạy ngầm (Background Service) kết hợp Notification hiển thị thanh tiến trình để tải tiếp khi thoát app ra ngoài màn hình chính.
- [ ] Lịch sử các file đã tải xuống kèm trình phát nhạc mini (Mini Player).

---

## 6. ƯỚC TÍNH SƠ BỘ & RỦI RO
- **Độ phức tạp:** Trung bình. (Viết UI bằng Compose rất nhanh, tuy nhiên cần quản lý cẩn thận phần native JNI của `youtubedl-android` trên các kiến trúc chip của Android).
- **Rủi ro dung lượng:** Vì app bao gồm cả Python runtime và FFmpeg binary, dung lượng APK debug có thể lên tới 50-70MB. Khi release, ta cần cấu hình chia nhỏ APK theo kiến trúc chip (splits APK) để giảm dung lượng tải xuống của người dùng (~20-25MB cho mỗi file APK arm64-v8a).

---

## 7. BƯỚC TIẾP THEO
→ Chạy `/plan` để bắt đầu thiết kế kỹ thuật, cấu hình các file Gradle và cấu trúc thư mục code chi tiết.
