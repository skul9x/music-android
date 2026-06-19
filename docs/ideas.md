# Ý tưởng dự án: Music Downloader

Ứng dụng Android giúp tải nhạc và video từ YouTube, TikTok với giao diện hiện đại và các tính năng tương tự bản Desktop của MusicYT.

## Ý tưởng & Tính năng chính:
- **Tải nhạc chất lượng cao:** Tự động chuyển đổi định dạng và chất lượng tốt nhất có thể (.m4a).
- **Nhúng bìa album (Cover Art):** Tự động tải thumbnail của video YouTube và nhúng thẳng vào metadata của file âm thanh tải xuống để hiển thị đẹp mắt trên các app nghe nhạc.
- **Engine tải cục bộ:** Chạy trực tiếp yt-dlp & ffmpeg trên thiết bị Android qua các thư viện native. Không cần server trung gian, đảm bảo quyền riêng tư và tốc độ tối đa.
- **Tính năng tự sửa lỗi (Fix error):** Khi các nền tảng thay đổi API dẫn tới lỗi tải, người dùng chỉ cần nhấn nút "Update engine" để tải phiên bản yt-dlp mới nhất trực tiếp trong app.
- **Giao diện Glassmorphism:** Áp dụng hiệu ứng kính mờ (blur), dải màu chuyển sắc (gradient), bo góc tròn mềm mại và animation mượt mà sử dụng Jetpack Compose & Material 3.
