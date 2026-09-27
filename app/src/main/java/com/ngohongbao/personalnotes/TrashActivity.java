package com.ngohongbao.personalnotes;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TrashActivity extends BaseActivity {
    private NoteAdapter adapter; private TextView empty;
    @Override protected void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);setContentView(R.layout.activity_trash);adapter=new NoteAdapter(this);ListView list=findViewById(R.id.lvTrash);list.setAdapter(adapter);empty=findViewById(R.id.tvEmpty);
        findViewById(R.id.btnBack).setOnClickListener(v->finish());findViewById(R.id.btnEmptyTrash).setOnClickListener(v->confirmEmpty());
        list.setOnItemClickListener((p,v,pos,id)->Toast.makeText(this,"Nhấn giữ ghi chú để khôi phục",Toast.LENGTH_SHORT).show());
        list.setOnItemLongClickListener((p,v,pos,id)->{Note n=adapter.getItem(pos);new AlertDialog.Builder(this).setTitle("Khôi phục ghi chú?").setMessage(n.getTitle()).setNegativeButton("Hủy",null).setPositiveButton("Khôi phục",(d,w)->{DatabaseHelper.getInstance(this).restore(n.getId());Toast.makeText(this,"Đã khôi phục",Toast.LENGTH_SHORT).show();load();}).show();return true;});
    }
    private void load(){List<Note> notes=DatabaseHelper.getInstance(this).getDeletedNotes();adapter.submitList(notes);empty.setVisibility(notes.isEmpty()?View.VISIBLE:View.GONE);}
    private void confirmEmpty(){new AlertDialog.Builder(this).setTitle("Xóa vĩnh viễn?").setMessage("Tất cả ghi chú trong thùng rác sẽ không thể khôi phục.").setNegativeButton("Hủy",null).setPositiveButton("Xóa hết",(d,w)->{int n=DatabaseHelper.getInstance(this).emptyTrash();Toast.makeText(this,"Đã xóa "+n+" ghi chú",Toast.LENGTH_SHORT).show();load();}).show();}
    @Override protected void onResume(){super.onResume();load();}
}
