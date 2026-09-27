# HƯỚNG DẪN MỞ VÀ CHẠY TRÊN ANDROID STUDIO

## A. Yêu cầu
- Android Studio bản tương đối mới.
- Android SDK Platform 35 (nếu chưa có, Android Studio sẽ gợi ý cài).
- JDK 17 (Android Studio thường có sẵn Embedded JDK).
- Máy ảo Android API 24+ hoặc điện thoại Android thật.

## B. Mở project
1. Giải nén file ZIP.
2. Mở Android Studio.
3. Chọn **Open**.
4. Chọn đúng thư mục `ThietKe_XayDung_UngDung_QuanLyGhiChu_Android_2311553403_NgoHongBao`.
5. Chờ **Gradle Sync**.
6. Nếu Android Studio báo thiếu SDK 35, chọn **Install missing SDK package**.

## C. Nếu báo thiếu Gradle Wrapper
Project có sẵn `gradle/wrapper/gradle-wrapper.properties` nhưng môi trường tạo file không kèm file nhị phân wrapper jar.

Trên Windows:
1. Nhấp đúp `TAO_GRADLE_WRAPPER_WINDOWS.bat`.
2. Chờ script tải Gradle 8.9 và tạo wrapper.
3. Mở lại Android Studio và Sync.

Hoặc nếu máy đã cài Gradle:
```text
gradle wrapper --gradle-version 8.9
```

## D. Chạy app
1. Mở **Device Manager** > tạo Pixel/API 35 (hoặc máy khác API >=24).
2. Bấm nút **Run ▶**.
3. Chọn thiết bị.
4. Lần chạy đầu app sẽ có 5 ghi chú mẫu.
5. Android 13+ sẽ hỏi quyền thông báo; chọn Allow nếu muốn thử reminder.

## E. Các luồng demo nên quay/chụp cho báo cáo
1. Main: danh sách + tìm kiếm + filter.
2. Thêm ghi chú.
3. Chi tiết ghi chú.
4. Sửa ghi chú.
5. Đặt reminder và nhận notification.
6. Chuyển vào thùng rác + khôi phục.
7. Trang thống kê.
8. Dark mode.
9. Export JSON.

## F. Lỗi thường gặp
- `SDK location not found`: mở project bằng Android Studio để IDE tự tạo `local.properties`.
- `Unsupported Java`: chọn Gradle JDK = Embedded JDK 17.
- `compileSdk 35 not found`: cài Android SDK 35 trong SDK Manager.
- Không thấy thông báo: cấp quyền Notification trên Android 13+.
- Reminder không đúng từng giây: app chủ động dùng alarm không-exact để không cần quyền đặc biệt; phù hợp app ghi chú học tập.
