package com.ngohongbao.personalnotes;

import android.os.Bundle;
import android.widget.TextView;

public class StatsActivity extends BaseActivity {
    @Override protected void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);setContentView(R.layout.activity_stats);findViewById(R.id.btnBack).setOnClickListener(v->finish());}
    @Override protected void onResume(){super.onResume();int[] s=DatabaseHelper.getInstance(this).getStats();set(R.id.tvTotal,s[0]);set(R.id.tvImportant,s[1]);set(R.id.tvPinned,s[2]);set(R.id.tvHigh,s[3]);set(R.id.tvReminder,s[4]);set(R.id.tvTrash,s[5]);}
    private void set(int id,int value){((TextView)findViewById(id)).setText(String.valueOf(value));}
}
