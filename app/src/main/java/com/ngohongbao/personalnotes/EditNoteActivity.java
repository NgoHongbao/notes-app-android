package com.ngohongbao.personalnotes;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditNoteActivity extends BaseActivity {
    private Note note;
    private EditText etTitle, etContent;
    private Spinner spCategory, spPriority;
    private CheckBox cbImportant, cbPinned;
    private TextView tvReminder;
    private long reminderTime;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_edit_note);
        long id=getIntent().getLongExtra("NOTE_ID",-1); note=DatabaseHelper.getInstance(this).getNoteById(id);
        if(note==null){ Toast.makeText(this,"Không tìm thấy ghi chú",Toast.LENGTH_SHORT).show(); finish(); return; }
        bind(); setupSpinners(); fill();
        findViewById(R.id.btnPickReminder).setOnClickListener(v -> NoteFormHelper.pickReminder(this,reminderTime,tvReminder,t -> reminderTime=t));
        findViewById(R.id.btnClearReminder).setOnClickListener(v -> { reminderTime=0; tvReminder.setText("Chưa đặt nhắc việc"); });
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
    }
    private void bind(){ etTitle=findViewById(R.id.etTitle);etContent=findViewById(R.id.etContent);spCategory=findViewById(R.id.spCategory);spPriority=findViewById(R.id.spPriority);cbImportant=findViewById(R.id.cbImportant);cbPinned=findViewById(R.id.cbPinned);tvReminder=findViewById(R.id.tvReminder); }
    private void setupSpinners(){
        ArrayAdapter<CharSequence> cat=ArrayAdapter.createFromResource(this,R.array.categories,android.R.layout.simple_spinner_item);cat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);spCategory.setAdapter(cat);
        ArrayAdapter<CharSequence> pri=ArrayAdapter.createFromResource(this,R.array.priorities,android.R.layout.simple_spinner_item);pri.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);spPriority.setAdapter(pri);
    }
    private void fill(){ etTitle.setText(note.getTitle());etContent.setText(note.getContent());spCategory.setSelection(indexOf(getResources().getStringArray(R.array.categories),note.getCategory()));spPriority.setSelection(note.getPriority());cbImportant.setChecked(note.isImportant());cbPinned.setChecked(note.isPinned());reminderTime=note.getReminderTime();if(reminderTime>0)tvReminder.setText("Nhắc lúc: "+DateUtils.formatDisplay(reminderTime)); }
    private int indexOf(String[] a,String value){ for(int i=0;i<a.length;i++) if(a[i].equals(value))return i; return 0; }
    private void save(){
        String t=etTitle.getText().toString().trim(),c=etContent.getText().toString().trim();
        if(t.isEmpty()){etTitle.setError("Vui lòng nhập tiêu đề");return;} if(c.isEmpty()){etContent.setError("Vui lòng nhập nội dung");return;}
        note.setTitle(t);note.setContent(c);note.setCategory(String.valueOf(spCategory.getSelectedItem()));note.setPriority(spPriority.getSelectedItemPosition());note.setImportant(cbImportant.isChecked());note.setPinned(cbPinned.isChecked());note.setReminderTime(reminderTime);
        if(DatabaseHelper.getInstance(this).updateNote(note)){ ReminderHelper.cancel(this,note.getId()); if(reminderTime>System.currentTimeMillis())ReminderHelper.schedule(this,note); Toast.makeText(this,"Đã cập nhật",Toast.LENGTH_SHORT).show(); finish(); }
    }
}
