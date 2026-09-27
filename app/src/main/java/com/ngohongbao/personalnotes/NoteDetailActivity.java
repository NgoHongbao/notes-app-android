package com.ngohongbao.personalnotes;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class NoteDetailActivity extends BaseActivity {
    private long noteId;
    private Note note;

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_note_detail);noteId=getIntent().getLongExtra("NOTE_ID",-1);
        findViewById(R.id.btnBack).setOnClickListener(v->finish());
        findViewById(R.id.btnEdit).setOnClickListener(v->{Intent i=new Intent(this,EditNoteActivity.class);i.putExtra("NOTE_ID",noteId);startActivity(i);});
        findViewById(R.id.btnShare).setOnClickListener(v->share());
        findViewById(R.id.btnDelete).setOnClickListener(v->confirmDelete());
    }
    private void load(){ note=DatabaseHelper.getInstance(this).getNoteById(noteId);if(note==null){finish();return;}
        ((TextView)findViewById(R.id.tvTitle)).setText(note.getTitle());((TextView)findViewById(R.id.tvContent)).setText(note.getContent());
        ((TextView)findViewById(R.id.tvMeta)).setText(note.getCategory()+" • "+note.getPriorityLabel()+"\nTạo: "+note.getCreatedDate()+"\nCập nhật: "+note.getUpdatedDate());
        String flags=(note.isPinned()?"📌 Đã ghim   ":"")+(note.isImportant()?"★ Quan trọng   ":"")+(note.getReminderTime()>0?"⏰ "+DateUtils.formatDisplay(note.getReminderTime()):"");
        ((TextView)findViewById(R.id.tvFlags)).setText(flags.isEmpty()?"Ghi chú thường":flags);
    }
    private void share(){ if(note==null)return;Intent i=new Intent(Intent.ACTION_SEND);i.setType("text/plain");i.putExtra(Intent.EXTRA_SUBJECT,note.getTitle());i.putExtra(Intent.EXTRA_TEXT,note.getTitle()+"\n\n"+note.getContent());startActivity(Intent.createChooser(i,"Chia sẻ ghi chú")); }
    private void confirmDelete(){new AlertDialog.Builder(this).setTitle("Chuyển vào thùng rác?").setMessage("Có thể khôi phục sau trong Thùng rác.").setNegativeButton("Hủy",null).setPositiveButton("Chuyển",(d,w)->{ReminderHelper.cancel(this,noteId);DatabaseHelper.getInstance(this).moveToTrash(noteId);Toast.makeText(this,"Đã chuyển vào thùng rác",Toast.LENGTH_SHORT).show();finish();}).show();}
    @Override protected void onResume(){super.onResume();load();}
}
