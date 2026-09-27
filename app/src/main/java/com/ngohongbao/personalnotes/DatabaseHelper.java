package com.ngohongbao.personalnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp quản lý SQLite của ứng dụng.
 * Tối ưu quan trọng:
 * - dùng Singleton để tránh mở nhiều kết nối DB;
 * - tạo index cho cột hay lọc/sắp xếp;
 * - dùng selectionArgs để tránh nối dữ liệu người dùng trực tiếp vào SQL;
 * - transaction khi import/reset nhiều bản ghi.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "notes.db";
    public static final int DB_VERSION = 3;
    public static final String TABLE = "notes";

    private static volatile DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) instance = new DatabaseHelper(context.getApplicationContext());
            }
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "created_date TEXT," +
                "updated_date TEXT," +
                "is_important INTEGER NOT NULL DEFAULT 0," +
                "is_pinned INTEGER NOT NULL DEFAULT 0," +
                "category TEXT NOT NULL DEFAULT 'Cá nhân'," +
                "priority INTEGER NOT NULL DEFAULT 1," +
                "color_tag TEXT DEFAULT '#EAF1FF'," +
                "reminder_time INTEGER NOT NULL DEFAULT 0," +
                "is_deleted INTEGER NOT NULL DEFAULT 0," +
                "created_at INTEGER NOT NULL DEFAULT 0," +
                "updated_at INTEGER NOT NULL DEFAULT 0" +
                ")");
        createIndexes(db);
        insertSamples(db);
    }

    private void createIndexes(SQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_notes_updated ON " + TABLE + "(updated_at DESC)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_notes_flags ON " + TABLE + "(is_deleted, is_pinned, is_important)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_notes_category ON " + TABLE + "(category)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_notes_reminder ON " + TABLE + "(reminder_time)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Migration an toàn để project cũ có thể nâng cấp mà không xóa ghi chú.
        if (oldVersion < 2) {
            addColumnSafe(db, "is_pinned INTEGER NOT NULL DEFAULT 0");
            addColumnSafe(db, "category TEXT NOT NULL DEFAULT 'Cá nhân'");
            addColumnSafe(db, "priority INTEGER NOT NULL DEFAULT 1");
            addColumnSafe(db, "color_tag TEXT DEFAULT '#EAF1FF'");
        }
        if (oldVersion < 3) {
            addColumnSafe(db, "reminder_time INTEGER NOT NULL DEFAULT 0");
            addColumnSafe(db, "is_deleted INTEGER NOT NULL DEFAULT 0");
            addColumnSafe(db, "created_at INTEGER NOT NULL DEFAULT 0");
            addColumnSafe(db, "updated_at INTEGER NOT NULL DEFAULT 0");
            long now = System.currentTimeMillis();
            db.execSQL("UPDATE " + TABLE + " SET created_at=?, updated_at=? WHERE created_at=0 OR updated_at=0",
                    new Object[]{now, now});
        }
        createIndexes(db);
    }

    private void addColumnSafe(SQLiteDatabase db, String definition) {
        try { db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN " + definition); }
        catch (Exception ignored) { }
    }

    private void insertSamples(SQLiteDatabase db) {
        long now = System.currentTimeMillis();
        insertSample(db, "Nộp báo cáo Android", "Hoàn thiện báo cáo và kiểm tra source trước thứ 6.", true, true, "Học tập", 2, now);
        insertSample(db, "Học Android", "Ôn Activity, Intent, ListView, Custom Adapter và SQLiteOpenHelper.", true, false, "Học tập", 2, now - 3600000L);
        insertSample(db, "Đi mua đồ", "Sữa, trứng, bánh mì và nước.", false, false, "Mua sắm", 1, now - 7200000L);
        insertSample(db, "Ý tưởng đồ án", "Bổ sung tìm kiếm, bộ lọc, dark mode và sao lưu JSON.", false, true, "Ý tưởng", 1, now - 10800000L);
        insertSample(db, "Lịch học", "Kiểm tra lịch học và chuẩn bị bài thực hành.", false, false, "Cá nhân", 0, now - 14400000L);
    }

    private void insertSample(SQLiteDatabase db, String title, String content, boolean important,
                              boolean pinned, String category, int priority, long time) {
        Note n = new Note();
        n.setTitle(title); n.setContent(content); n.setImportant(important); n.setPinned(pinned);
        n.setCategory(category); n.setPriority(priority); n.setColorTag("#EAF1FF");
        n.setCreatedAt(time); n.setUpdatedAt(time);
        n.setCreatedDate(DateUtils.formatDisplay(time)); n.setUpdatedDate(DateUtils.formatDisplay(time));
        db.insert(TABLE, null, values(n));
    }

    private ContentValues values(Note n) {
        ContentValues v = new ContentValues();
        v.put("title", n.getTitle());
        v.put("content", n.getContent());
        v.put("created_date", n.getCreatedDate());
        v.put("updated_date", n.getUpdatedDate());
        v.put("is_important", n.isImportant() ? 1 : 0);
        v.put("is_pinned", n.isPinned() ? 1 : 0);
        v.put("category", n.getCategory());
        v.put("priority", n.getPriority());
        v.put("color_tag", n.getColorTag());
        v.put("reminder_time", n.getReminderTime());
        v.put("is_deleted", n.isDeleted() ? 1 : 0);
        v.put("created_at", n.getCreatedAt());
        v.put("updated_at", n.getUpdatedAt());
        return v;
    }

    public long insertNote(Note n) {
        long now = System.currentTimeMillis();
        if (n.getCreatedAt() <= 0) n.setCreatedAt(now);
        n.setUpdatedAt(now);
        if (n.getCreatedDate() == null) n.setCreatedDate(DateUtils.formatDisplay(n.getCreatedAt()));
        n.setUpdatedDate(DateUtils.formatDisplay(now));
        return getWritableDatabase().insert(TABLE, null, values(n));
    }

    public boolean updateNote(Note n) {
        long now = System.currentTimeMillis();
        n.setUpdatedAt(now);
        n.setUpdatedDate(DateUtils.formatDisplay(now));
        return getWritableDatabase().update(TABLE, values(n), "id=?", new String[]{String.valueOf(n.getId())}) > 0;
    }

    public Note getNoteById(long id) {
        try (Cursor c = getReadableDatabase().query(TABLE, null, "id=?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (c.moveToFirst()) return fromCursor(c);
        }
        return null;
    }

    /**
     * Truy vấn danh sách với tìm kiếm + bộ lọc + sắp xếp.
     * sortCode: 0 mới nhất, 1 cũ nhất, 2 ghim trước, 3 quan trọng trước, 4 ưu tiên cao, 5 A-Z.
     */
    public List<Note> queryNotes(String keyword, int filterMode, String category, int sortCode) {
        List<Note> result = new ArrayList<>();
        StringBuilder sel = new StringBuilder("is_deleted=0");
        List<String> args = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sel.append(" AND (title LIKE ? OR content LIKE ?)");
            String k = "%" + keyword.trim() + "%";
            args.add(k); args.add(k);
        }
        if (filterMode == 1) sel.append(" AND is_important=1");
        if (filterMode == 2) sel.append(" AND is_pinned=1");
        if (category != null && !category.equals("Tất cả")) {
            sel.append(" AND category=?"); args.add(category);
        }

        String orderBy;
        switch (sortCode) {
            case 1: orderBy = "updated_at ASC"; break;
            case 2: orderBy = "is_pinned DESC, updated_at DESC"; break;
            case 3: orderBy = "is_important DESC, updated_at DESC"; break;
            case 4: orderBy = "priority DESC, updated_at DESC"; break;
            case 5: orderBy = "title COLLATE NOCASE ASC"; break;
            default: orderBy = "updated_at DESC";
        }

        try (Cursor c = getReadableDatabase().query(TABLE, null, sel.toString(),
                args.toArray(new String[0]), null, null, orderBy)) {
            while (c.moveToNext()) result.add(fromCursor(c));
        }
        return result;
    }

    public List<Note> getDeletedNotes() {
        List<Note> result = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query(TABLE, null, "is_deleted=1", null, null, null, "updated_at DESC")) {
            while (c.moveToNext()) result.add(fromCursor(c));
        }
        return result;
    }

    public List<Note> getAllForBackup() {
        List<Note> result = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query(TABLE, null, null, null, null, null, "id ASC")) {
            while (c.moveToNext()) result.add(fromCursor(c));
        }
        return result;
    }

    public List<Note> getUpcomingReminders() {
        List<Note> result = new ArrayList<>();
        String sel = "is_deleted=0 AND reminder_time>?";
        try (Cursor c = getReadableDatabase().query(TABLE, null, sel,
                new String[]{String.valueOf(System.currentTimeMillis())}, null, null, "reminder_time ASC")) {
            while (c.moveToNext()) result.add(fromCursor(c));
        }
        return result;
    }

    public boolean moveToTrash(long id) {
        ContentValues v = new ContentValues();
        v.put("is_deleted", 1);
        v.put("updated_at", System.currentTimeMillis());
        return getWritableDatabase().update(TABLE, v, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean restore(long id) {
        ContentValues v = new ContentValues();
        v.put("is_deleted", 0);
        v.put("updated_at", System.currentTimeMillis());
        return getWritableDatabase().update(TABLE, v, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public int emptyTrash() {
        return getWritableDatabase().delete(TABLE, "is_deleted=1", null);
    }

    public int[] getStats() {
        int[] stats = new int[6]; // total, important, pinned, high, reminders, trash
        SQLiteDatabase db = getReadableDatabase();
        stats[0] = count(db, "is_deleted=0");
        stats[1] = count(db, "is_deleted=0 AND is_important=1");
        stats[2] = count(db, "is_deleted=0 AND is_pinned=1");
        stats[3] = count(db, "is_deleted=0 AND priority=2");
        stats[4] = count(db, "is_deleted=0 AND reminder_time>" + System.currentTimeMillis());
        stats[5] = count(db, "is_deleted=1");
        return stats;
    }

    private int count(SQLiteDatabase db, String where) {
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE + " WHERE " + where, null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    public int importNotes(List<Note> notes) {
        SQLiteDatabase db = getWritableDatabase();
        int ok = 0;
        db.beginTransaction();
        try {
            for (Note n : notes) {
                n.setId(0);
                if (db.insert(TABLE, null, values(n)) != -1) ok++;
            }
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
        return ok;
    }

    public void resetDemoData() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE, null, null);
            insertSamples(db);
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
    }

    private Note fromCursor(Cursor c) {
        Note n = new Note();
        n.setId(c.getLong(c.getColumnIndexOrThrow("id")));
        n.setTitle(c.getString(c.getColumnIndexOrThrow("title")));
        n.setContent(c.getString(c.getColumnIndexOrThrow("content")));
        n.setCreatedDate(c.getString(c.getColumnIndexOrThrow("created_date")));
        n.setUpdatedDate(c.getString(c.getColumnIndexOrThrow("updated_date")));
        n.setImportant(c.getInt(c.getColumnIndexOrThrow("is_important")) == 1);
        n.setPinned(c.getInt(c.getColumnIndexOrThrow("is_pinned")) == 1);
        n.setCategory(c.getString(c.getColumnIndexOrThrow("category")));
        n.setPriority(c.getInt(c.getColumnIndexOrThrow("priority")));
        n.setColorTag(c.getString(c.getColumnIndexOrThrow("color_tag")));
        n.setReminderTime(c.getLong(c.getColumnIndexOrThrow("reminder_time")));
        n.setDeleted(c.getInt(c.getColumnIndexOrThrow("is_deleted")) == 1);
        n.setCreatedAt(c.getLong(c.getColumnIndexOrThrow("created_at")));
        n.setUpdatedAt(c.getLong(c.getColumnIndexOrThrow("updated_at")));
        return n;
    }
}
