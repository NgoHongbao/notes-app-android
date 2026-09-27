package com.ngohongbao.personalnotes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddNoteActivity extends BaseActivity {
    private EditText etTitle, etContent;
    private Spinner spCategory, spPriority;
    private CheckBox cbImportant, cbPinned;
    private TextView tvReminder;
    private long reminderTime = 0L;
    private boolean saved = false;
    private static final String DRAFT = "note_draft";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        bind(); setupSpinners(); restoreDraft();
        findViewById(R.id.btnPickReminder).setOnClickListener(v -> NoteFormHelper.pickReminder(this, reminderTime, tvReminder, t -> reminderTime = t));
        findViewById(R.id.btnClearReminder).setOnClickListener(v -> { reminderTime = 0; tvReminder.setText("Chưa đặt nhắc việc"); });
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
    }

    private void bind() {
        etTitle=findViewById(R.id.etTitle); etContent=findViewById(R.id.etContent);
        spCategory=findViewById(R.id.spCategory); spPriority=findViewById(R.id.spPriority);
        cbImportant=findViewById(R.id.cbImportant); cbPinned=findViewById(R.id.cbPinned);
        tvReminder=findViewById(R.id.tvReminder);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> cat=ArrayAdapter.createFromResource(this,R.array.categories,android.R.layout.simple_spinner_item);
        cat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); spCategory.setAdapter(cat);
        ArrayAdapter<CharSequence> pri=ArrayAdapter.createFromResource(this,R.array.priorities,android.R.layout.simple_spinner_item);
        pri.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); spPriority.setAdapter(pri); spPriority.setSelection(1);
    }

    private void save() {
        String title=etTitle.getText().toString().trim(); String content=etContent.getText().toString().trim();
        if(title.isEmpty()){ etTitle.setError("Vui lòng nhập tiêu đề"); etTitle.requestFocus(); return; }
        if(content.isEmpty()){ etContent.setError("Vui lòng nhập nội dung"); etContent.requestFocus(); return; }
        Note n=new Note(); n.setTitle(title); n.setContent(content); n.setCategory(String.valueOf(spCategory.getSelectedItem()));
        n.setPriority(spPriority.getSelectedItemPosition()); n.setImportant(cbImportant.isChecked()); n.setPinned(cbPinned.isChecked());
        n.setReminderTime(reminderTime); n.setColorTag("#EAF1FF");
        long id=DatabaseHelper.getInstance(this).insertNote(n);
        if(id!=-1){ n.setId(id); if(reminderTime>System.currentTimeMillis()) ReminderHelper.schedule(this,n); saved=true; clearDraft(); Toast.makeText(this,"Đã lưu ghi chú",Toast.LENGTH_SHORT).show(); finish(); }
        else Toast.makeText(this,"Không thể lưu ghi chú",Toast.LENGTH_SHORT).show();
    }

    /** Tự lưu nháp để người dùng không mất nội dung khi vô tình thoát màn hình. */
    private void saveDraft(){
        if(saved) return;
        getSharedPreferences(DRAFT,MODE_PRIVATE).edit()
                .putString("title",etTitle.getText().toString()).putString("content",etContent.getText().toString())
                .putInt("category",spCategory.getSelectedItemPosition()).putInt("priority",spPriority.getSelectedItemPosition())
                .putBoolean("important",cbImportant.isChecked()).putBoolean("pinned",cbPinned.isChecked())
                .putLong("reminder",reminderTime).apply();
    }
    private void restoreDraft(){
        SharedPreferences p=getSharedPreferences(DRAFT,MODE_PRIVATE);
        etTitle.setText(p.getString("title","")); etContent.setText(p.getString("content",""));
        spCategory.setSelection(p.getInt("category",0)); spPriority.setSelection(p.getInt("priority",1));
        cbImportant.setChecked(p.getBoolean("important",false)); cbPinned.setChecked(p.getBoolean("pinned",false));
        reminderTime=p.getLong("reminder",0); if(reminderTime>0) tvReminder.setText("Nhắc lúc: "+DateUtils.formatDisplay(reminderTime));
    }
    private void clearDraft(){ getSharedPreferences(DRAFT,MODE_PRIVATE).edit().clear().apply(); }
    @Override protected void onPause(){ saveDraft(); super.onPause(); }
}
