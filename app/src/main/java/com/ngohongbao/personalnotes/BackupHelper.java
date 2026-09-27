package com.ngohongbao.personalnotes;

import android.content.Context;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/** Sao lưu/khôi phục JSON qua Storage Access Framework, không cần quyền bộ nhớ. */
public final class BackupHelper {
    private BackupHelper() {}

    public static int exportJson(Context context, Uri uri) throws Exception {
        List<Note> notes = DatabaseHelper.getInstance(context).getAllForBackup();
        JSONArray arr = new JSONArray();
        for (Note n : notes) arr.put(n.toJson());
        JSONObject root = new JSONObject();
        root.put("app", "Personal Notes Android");
        root.put("version", 1);
        root.put("exported_at", System.currentTimeMillis());
        root.put("notes", arr);
        try (OutputStreamWriter w = new OutputStreamWriter(context.getContentResolver().openOutputStream(uri))) {
            w.write(root.toString(2));
        }
        return notes.size();
    }

    public static int importJson(Context context, Uri uri) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(context.getContentResolver().openInputStream(uri)))) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
        }
        JSONObject root = new JSONObject(sb.toString());
        JSONArray arr = root.getJSONArray("notes");
        List<Note> notes = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) notes.add(Note.fromJson(arr.getJSONObject(i)));
        return DatabaseHelper.getInstance(context).importNotes(notes);
    }
}
