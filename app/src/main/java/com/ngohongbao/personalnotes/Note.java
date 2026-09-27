package com.ngohongbao.personalnotes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model đại diện cho một ghi chú.
 * Các trường cốt lõi title/content/createdDate/updatedDate/isImportant
 * được giữ đúng tinh thần đề tài, đồng thời bổ sung một số trường nâng cao.
 */
public class Note {
    private long id;
    private String title;
    private String content;
    private String createdDate;
    private String updatedDate;
    private boolean important;
    private boolean pinned;
    private String category;
    private int priority;
    private String colorTag;
    private long reminderTime;
    private boolean deleted;
    private long createdAt;
    private long updatedAt;

    public Note() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public String getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }
    public boolean isImportant() { return important; }
    public void setImportant(boolean important) { this.important = important; }
    public boolean isPinned() { return pinned; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public String getColorTag() { return colorTag; }
    public void setColorTag(String colorTag) { this.colorTag = colorTag; }
    public long getReminderTime() { return reminderTime; }
    public void setReminderTime(long reminderTime) { this.reminderTime = reminderTime; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public String getPriorityLabel() {
        if (priority >= 2) return "Cao";
        if (priority == 1) return "Trung bình";
        return "Thấp";
    }

    /** Chuyển Note thành JSON để sao lưu dữ liệu. */
    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("title", title);
        o.put("content", content);
        o.put("created_date", createdDate);
        o.put("updated_date", updatedDate);
        o.put("is_important", important);
        o.put("is_pinned", pinned);
        o.put("category", category);
        o.put("priority", priority);
        o.put("color_tag", colorTag);
        o.put("reminder_time", reminderTime);
        o.put("is_deleted", deleted);
        o.put("created_at", createdAt);
        o.put("updated_at", updatedAt);
        return o;
    }

    /** Tạo Note từ JSON khi khôi phục bản sao lưu. */
    public static Note fromJson(JSONObject o) {
        Note n = new Note();
        n.title = o.optString("title", "Không tiêu đề");
        n.content = o.optString("content", "");
        n.createdDate = o.optString("created_date", DateUtils.nowDisplay());
        n.updatedDate = o.optString("updated_date", n.createdDate);
        n.important = o.optBoolean("is_important", false);
        n.pinned = o.optBoolean("is_pinned", false);
        n.category = o.optString("category", "Cá nhân");
        n.priority = o.optInt("priority", 1);
        n.colorTag = o.optString("color_tag", "#EAF1FF");
        n.reminderTime = o.optLong("reminder_time", 0L);
        n.deleted = o.optBoolean("is_deleted", false);
        n.createdAt = o.optLong("created_at", System.currentTimeMillis());
        n.updatedAt = o.optLong("updated_at", n.createdAt);
        return n;
    }
}
