package com.ngohongbao.personalnotes;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/** Custom Adapter + ViewHolder giúp ListView cuộn mượt và tái sử dụng view. */
public class NoteAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<Note> data = new ArrayList<>();

    public NoteAdapter(Context context) { inflater = LayoutInflater.from(context); }

    public void submitList(List<Note> notes) {
        data.clear();
        if (notes != null) data.addAll(notes);
        notifyDataSetChanged();
    }

    public Note getItem(int position) { return data.get(position); }
    public int getCount() { return data.size(); }
    public long getItemId(int position) { return data.get(position).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_note, parent, false);
            h = new Holder();
            h.title = convertView.findViewById(R.id.tvTitle);
            h.content = convertView.findViewById(R.id.tvContent);
            h.meta = convertView.findViewById(R.id.tvMeta);
            h.flags = convertView.findViewById(R.id.tvFlags);
            h.priority = convertView.findViewById(R.id.tvPriority);
            convertView.setTag(h);
        } else h = (Holder) convertView.getTag();

        Note n = getItem(position);
        h.title.setText(n.getTitle());
        h.content.setText(n.getContent());
        h.meta.setText(n.getCategory() + "  •  " + n.getUpdatedDate());

        StringBuilder f = new StringBuilder();
        if (n.isPinned()) f.append("📌 ");
        if (n.isImportant()) f.append("★ ");
        if (n.getReminderTime() > System.currentTimeMillis()) f.append("⏰ ");
        h.flags.setText(f.toString());
        h.priority.setText(n.getPriorityLabel());
        if (n.getPriority() == 2) h.priority.setTextColor(Color.parseColor("#D93025"));
        else if (n.getPriority() == 0) h.priority.setTextColor(Color.parseColor("#188038"));
        else h.priority.setTextColor(Color.parseColor("#F29900"));
        return convertView;
    }

    static class Holder {
        TextView title, content, meta, flags, priority;
    }
}
