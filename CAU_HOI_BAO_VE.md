# 30 CÂU HỎI BẢO VỆ GỢI Ý

1. **Đề tài làm gì?** – Quản lý ghi chú cá nhân offline trên Android: CRUD, tìm kiếm, lọc, nhắc việc, sao lưu.
2. **Vì sao dùng Java + XML?** – Phù hợp yêu cầu môn học và minh họa rõ Activity, Intent, View, SQLite.
3. **SQLite là gì?** – CSDL quan hệ nhúng trên thiết bị, không cần server.
4. **SQLiteOpenHelper để làm gì?** – Tạo/nâng cấp DB và cung cấp readable/writable database.
5. **Tên DB?** – `notes.db`.
6. **Bảng chính?** – `notes`.
7. **Khóa chính?** – `id INTEGER PRIMARY KEY AUTOINCREMENT`.
8. **CRUD là gì?** – Create, Read, Update, Delete.
9. **Intent dùng ở đâu?** – Chuyển Activity và truyền `NOTE_ID`.
10. **Custom Adapter là gì?** – Chuyển dữ liệu Note thành item hiển thị trong ListView.
11. **ViewHolder có tác dụng gì?** – Giảm findViewById khi cuộn, tăng hiệu năng.
12. **Search hoạt động thế nào?** – SQL LIKE trên title/content với selectionArgs.
13. **Debounce là gì?** – Chờ 250 ms sau khi người dùng ngừng gõ mới query.
14. **Vì sao query ở background?** – Tránh block UI thread.
15. **Index SQLite để làm gì?** – Tăng tốc lọc/sắp xếp trên cột hay truy vấn.
16. **SelectionArgs có lợi gì?** – Tránh SQL injection và xử lý ký tự an toàn hơn.
17. **Xóa mềm là gì?** – Đặt `is_deleted=1`, chưa xóa record thật.
18. **Thùng rác giúp gì?** – Cho phép khôi phục khi xóa nhầm.
19. **SharedPreferences dùng ở đâu?** – Dark mode, sort mặc định, draft thêm ghi chú.
20. **AlarmManager dùng để làm gì?** – Hẹn thời điểm phát reminder.
21. **BroadcastReceiver dùng để làm gì?** – Nhận alarm/BOOT_COMPLETED.
22. **NotificationChannel là gì?** – Kênh thông báo bắt buộc từ Android 8.
23. **Vì sao có quyền POST_NOTIFICATIONS?** – Android 13+ yêu cầu quyền runtime để hiện notification.
24. **Backup JSON hoạt động thế nào?** – Chuyển danh sách Note thành JSONArray và ghi ra URI do người dùng chọn.
25. **App có cần Internet không?** – Không, toàn bộ dữ liệu chính lưu local SQLite.
26. **Bản hiện tại có backend không?** – Không.
27. **Điểm nâng cao của app?** – reminder, trash, backup, dark mode, statistics, debounce, background query.
28. **Nếu dữ liệu rất lớn sẽ cải tiến gì?** – paging, FTS, repository/architecture component, worker.
29. **Nếu cần đồng bộ nhiều thiết bị?** – Bổ sung backend/Firebase/API nhưng không nằm trong phạm vi hiện tại.
30. **Điểm mạnh của project?** – Bám đề tài, dễ demo, có tính năng thực tế và có tối ưu hiệu năng rõ ràng.
