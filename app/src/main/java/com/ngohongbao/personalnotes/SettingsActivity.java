package com.ngohongbao.personalnotes;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity {
    private static final int REQ_EXPORT=101,REQ_IMPORT=102;
    private Spinner spSort;
    @Override protected void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);setContentView(R.layout.activity_settings);findViewById(R.id.btnBack).setOnClickListener(v->finish());
        Switch sw=findViewById(R.id.swDark);sw.setChecked(ThemeManager.isDark(this));sw.setOnCheckedChangeListener((b,checked)->{ThemeManager.setDark(this,checked);recreate();});
        spSort=findViewById(R.id.spDefaultSort);ArrayAdapter<CharSequence>a=ArrayAdapter.createFromResource(this,R.array.sort_options,android.R.layout.simple_spinner_item);a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);spSort.setAdapter(a);spSort.setSelection(getSharedPreferences("notes_settings",MODE_PRIVATE).getInt("default_sort",0));
        spSort.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onItemSelected(android.widget.AdapterView<?>p,android.view.View v,int pos,long id){getSharedPreferences("notes_settings",MODE_PRIVATE).edit().putInt("default_sort",pos).apply();}public void onNothingSelected(android.widget.AdapterView<?>p){}});
        findViewById(R.id.btnExport).setOnClickListener(v->exportData());findViewById(R.id.btnImport).setOnClickListener(v->importData());findViewById(R.id.btnResetDemo).setOnClickListener(v->confirmReset());
    }
    private void exportData(){Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/json");i.putExtra(Intent.EXTRA_TITLE,"notes_backup_"+System.currentTimeMillis()+".json");startActivityForResult(i,REQ_EXPORT);}
    private void importData(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/json");startActivityForResult(i,REQ_IMPORT);}
    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){super.onActivityResult(requestCode,resultCode,data);if(resultCode!=RESULT_OK||data==null)return;Uri uri=data.getData();if(uri==null)return;try{if(requestCode==REQ_EXPORT){int n=BackupHelper.exportJson(this,uri);Toast.makeText(this,"Đã sao lưu "+n+" ghi chú",Toast.LENGTH_LONG).show();}else if(requestCode==REQ_IMPORT){int n=BackupHelper.importJson(this,uri);Toast.makeText(this,"Đã nhập "+n+" ghi chú",Toast.LENGTH_LONG).show();}}catch(Exception e){Toast.makeText(this,"Lỗi dữ liệu: "+e.getMessage(),Toast.LENGTH_LONG).show();}}
    private void confirmReset(){new AlertDialog.Builder(this).setTitle("Khôi phục dữ liệu mẫu?").setMessage("Thao tác này xóa toàn bộ dữ liệu hiện tại và tạo lại dữ liệu demo.").setNegativeButton("Hủy",null).setPositiveButton("Đồng ý",(d,w)->{DatabaseHelper.getInstance(this).resetDemoData();Toast.makeText(this,"Đã tạo lại dữ liệu mẫu",Toast.LENGTH_SHORT).show();}).show();}
}
