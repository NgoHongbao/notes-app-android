# GHI CHÚ CODE – DỄ ĐỌC VÀ DỄ BẢO VỆ

## 1. MainActivity.java
Màn hình trung tâm. Có Search, filter, category, sort, ListView, nút thêm, thống kê, thùng rác và cài đặt.

Điểm đáng nói khi bảo vệ:
- `TextWatcher` + `Handler.postDelayed(..., 250)` tạo **debounce**.
- `ExecutorService` chạy query SQLite ở background thread.
- `AtomicInteger queryVersion` ngăn kết quả query cũ ghi đè kết quả mới.

## 2. DatabaseHelper.java
Dùng `SQLiteOpenHelper` quản lý `notes.db`.

Bảng `notes` giữ các cột cốt lõi:
- id
- title
- content
- created_date
- updated_date
- is_important

Bổ sung nâng cao:
- is_pinned
- category
- priority
- color_tag
- reminder_time
- is_deleted
- created_at / updated_at

Có index để tăng tốc các truy vấn hay dùng.

## 3. NoteAdapter.java
Custom `BaseAdapter` cho ListView.

Dùng **ViewHolder Pattern**: chỉ `findViewById()` khi item được tạo lần đầu, khi cuộn thì tái dùng `convertView`.

## 4. AddNoteActivity.java
- Validate tiêu đề/nội dung.
- Chọn danh mục, ưu tiên, quan trọng, ghim, reminder.
- Tự lưu nháp bằng `SharedPreferences` trong `onPause()`.
- Sau khi lưu thành công thì xóa draft.

## 5. EditNoteActivity.java
Nhận `NOTE_ID` bằng Intent Extra, đọc ghi chú từ SQLite, hiển thị dữ liệu cũ và gọi `updateNote()`.

## 6. NoteDetailActivity.java
Hiển thị chi tiết, sửa, chia sẻ và chuyển vào thùng rác.

## 7. ReminderHelper / ReminderReceiver / BootReceiver
- `ReminderHelper`: đăng ký AlarmManager.
- `ReminderReceiver`: nhận alarm và hiện Notification.
- `BootReceiver`: sau khi reboot máy, đăng ký lại reminder.

## 8. TrashActivity.java
Dùng xóa mềm `is_deleted=1`. Người dùng có thể khôi phục trước khi xóa vĩnh viễn.

## 9. BackupHelper.java
Xuất/nhập JSON qua `ACTION_CREATE_DOCUMENT` / `ACTION_OPEN_DOCUMENT`, không cần xin quyền đọc ghi bộ nhớ truyền thống.

## 10. ThemeManager.java
Lưu dark mode trong `SharedPreferences`, mọi Activity kế thừa `BaseActivity` để áp dụng theme trước `super.onCreate()`.
