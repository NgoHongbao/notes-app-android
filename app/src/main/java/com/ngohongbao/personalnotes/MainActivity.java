package com.ngohongbao.personalnotes;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Màn hình chính: hiển thị, tìm kiếm, lọc, sắp xếp và điều hướng.
 * Query được chạy ở background thread để danh sách lớn vẫn phản hồi mượt.
 */
public class MainActivity extends BaseActivity {
    private EditText etSearch;
    private Spinner spCategory, spSort;
    private ListView lvNotes;
    private TextView tvEmpty, tvCount;
    private Button btnAll, btnImportant, btnPinned;
    private NoteAdapter adapter;
    private DatabaseHelper db;
    private int filterMode = 0;

    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final AtomicInteger queryVersion = new AtomicInteger(0);
    private final Runnable searchRunnable = this::loadNotes;

    private static final String PREFS = "notes_settings";
    private static final String KEY_SORT = "default_sort";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = DatabaseHelper.getInstance(this);
        bindViews();
        setupSpinners();
        setupList();
        setupEvents();
        requestNotificationPermission();
    }

    private void bindViews() {
        etSearch = findViewById(R.id.etSearch);
        spCategory = findViewById(R.id.spCategory);
        spSort = findViewById(R.id.spSort);
        lvNotes = findViewById(R.id.lvNotes);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvCount = findViewById(R.id.tvCount);
        btnAll = findViewById(R.id.btnAll);
        btnImportant = findViewById(R.id.btnImportant);
        btnPinned = findViewById(R.id.btnPinned);
        adapter = new NoteAdapter(this);
        lvNotes.setAdapter(adapter);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> cat = ArrayAdapter.createFromResource(this, R.array.categories_filter, android.R.layout.simple_spinner_item);
        cat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(cat);

        ArrayAdapter<CharSequence> sort = ArrayAdapter.createFromResource(this, R.array.sort_options, android.R.layout.simple_spinner_item);
        sort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSort.setAdapter(sort);
        int savedSort = getSharedPreferences(PREFS, MODE_PRIVATE).getInt(KEY_SORT, 0);
        if (savedSort >= 0 && savedSort < sort.getCount()) spSort.setSelection(savedSort);
    }

    private void setupList() {
        lvNotes.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(this, NoteDetailActivity.class);
            i.putExtra("NOTE_ID", adapter.getItem(position).getId());
            startActivity(i);
        });
        lvNotes.setOnItemLongClickListener((parent, view, position, id) -> {
            Note n = adapter.getItem(position);
            new AlertDialog.Builder(this)
                    .setTitle("Chuyển vào thùng rác?")
                    .setMessage(n.getTitle())
                    .setNegativeButton("Hủy", null)
                    .setPositiveButton("Chuyển", (d, w) -> {
                        ReminderHelper.cancel(this, n.getId());
                        db.moveToTrash(n.getId());
                        Toast.makeText(this, "Đã chuyển vào thùng rác", Toast.LENGTH_SHORT).show();
                        loadNotes();
                    }).show();
            return true;
        });
    }

    private void setupEvents() {
        findViewById(R.id.btnAdd).setOnClickListener(v -> startActivity(new Intent(this, AddNoteActivity.class)));
        findViewById(R.id.btnStats).setOnClickListener(v -> startActivity(new Intent(this, StatsActivity.class)));
        findViewById(R.id.btnTrash).setOnClickListener(v -> startActivity(new Intent(this, TrashActivity.class)));
        findViewById(R.id.btnSettings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        btnAll.setOnClickListener(v -> setFilter(0));
        btnImportant.setOnClickListener(v -> setFilter(1));
        btnPinned.setOnClickListener(v -> setFilter(2));

        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            public void onTextChanged(CharSequence s, int st, int before, int count) {
                // Debounce 250ms: tránh query SQLite liên tục khi đang gõ nhanh.
                mainHandler.removeCallbacks(searchRunnable);
                mainHandler.postDelayed(searchRunnable, 250);
            }
            public void afterTextChanged(Editable s) {}
        });

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getId() == R.id.spSort) {
                    getSharedPreferences(PREFS, MODE_PRIVATE).edit().putInt(KEY_SORT, position).apply();
                }
                loadNotes();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spCategory.setOnItemSelectedListener(listener);
        spSort.setOnItemSelectedListener(listener);
    }

    private void setFilter(int mode) {
        filterMode = mode;
        btnAll.setAlpha(mode == 0 ? 1f : .55f);
        btnImportant.setAlpha(mode == 1 ? 1f : .55f);
        btnPinned.setAlpha(mode == 2 ? 1f : .55f);
        loadNotes();
    }

    private void loadNotes() {
        final int version = queryVersion.incrementAndGet();
        final String keyword = etSearch.getText().toString();
        final String category = String.valueOf(spCategory.getSelectedItem());
        final int sort = spSort.getSelectedItemPosition();
        final int filter = filterMode;

        dbExecutor.execute(() -> {
            List<Note> notes = db.queryNotes(keyword, filter, category, sort);
            mainHandler.post(() -> {
                // Chỉ hiển thị kết quả của query mới nhất, bỏ kết quả cũ nếu người dùng gõ tiếp.
                if (version != queryVersion.get() || isFinishing()) return;
                adapter.submitList(notes);
                tvCount.setText(notes.size() + " ghi chú");
                tvEmpty.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
                lvNotes.setVisibility(notes.isEmpty() ? View.GONE : View.VISIBLE);
            });
        });
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 900);
        }
    }

    @Override protected void onResume() { super.onResume(); loadNotes(); }
    @Override protected void onDestroy() {
        mainHandler.removeCallbacksAndMessages(null);
        dbExecutor.shutdownNow();
        super.onDestroy();
    }
}
