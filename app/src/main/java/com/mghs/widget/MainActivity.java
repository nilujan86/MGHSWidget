package com.mghs.widget;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    static final String[] DAYS      = {"mo","tu","we","th","fr"};
    static final String[] DAY_NAMES = {"MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"};
    static final String[] P_HINTS   = {
        "Period 1   7:40 - 8:20",
        "Period 2   8:30 - 9:10",
        "Period 3   9:10 - 9:50",
        "Period 4   9:50 - 10:30",
        "Period 5  10:50 - 11:30",
        "Period 6  11:30 - 12:10",
        "Period 7  12:10 - 12:50",
        "Period 8  12:50 - 13:30"
    };

    private final EditText[][] fields = new EditText[5][8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Build UI entirely in code - zero XML layout dependency
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Color.parseColor("#F0EAF9"));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(root);
        setContentView(scroll);

        // ── Header ──────────────────────────────────────────────────────────
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundColor(Color.parseColor("#6C3FC5"));
        header.setPadding(dp(16), dp(20), dp(16), dp(16));
        root.addView(header);

        TextView tvSchool = new TextView(this);
        tvSchool.setText("J/Methodist Girls' High School");
        tvSchool.setTextColor(Color.parseColor("#BBFFFFFF"));
        tvSchool.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        header.addView(tvSchool);

        TextView tvTeacher = new TextView(this);
        tvTeacher.setText("Teacher A. Nilujan");
        tvTeacher.setTextColor(Color.WHITE);
        tvTeacher.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tvTeacher.setTypeface(null, Typeface.BOLD);
        header.addView(tvTeacher);

        TextView tvHint = new TextView(this);
        tvHint.setText("Enter timetable below, then tap Save to update widget");
        tvHint.setTextColor(Color.parseColor("#CCFFFFFF"));
        tvHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvHint.setPadding(0, dp(6), 0, 0);
        header.addView(tvHint);

        // ── Day sections ─────────────────────────────────────────────────────
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(12), dp(12), dp(12), dp(12));
        root.addView(content);

        for (int d = 0; d < 5; d++) {
            TextView tvDay = new TextView(this);
            tvDay.setText(DAY_NAMES[d]);
            tvDay.setTextColor(Color.parseColor("#6C3FC5"));
            tvDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tvDay.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams dayLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
            dayLp.setMargins(0, dp(14), 0, dp(6));
            tvDay.setLayoutParams(dayLp);
            content.addView(tvDay);

            for (int p = 0; p < 8; p++) {
                EditText et = new EditText(this);
                et.setHint(P_HINTS[p]);
                et.setBackgroundColor(Color.WHITE);
                et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                et.setPadding(dp(10), dp(10), dp(10), dp(10));
                LinearLayout.LayoutParams etLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
                etLp.setMargins(0, 0, 0, dp(3));
                et.setLayoutParams(etLp);
                content.addView(et);
                fields[d][p] = et;
            }
        }

        // ── Save button ───────────────────────────────────────────────────────
        Button btnSave = new Button(this);
        btnSave.setText("SAVE TIMETABLE & UPDATE WIDGET");
        btnSave.setTextColor(Color.WHITE);
        btnSave.setTypeface(null, Typeface.BOLD);
        btnSave.setBackgroundColor(Color.parseColor("#6C3FC5"));
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(56));
        btnLp.setMargins(0, dp(16), 0, dp(16));
        btnSave.setLayoutParams(btnLp);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndUpdate();
            }
        });
        content.addView(btnSave);

        loadFields();
    }

    private void loadFields() {
        SharedPreferences prefs = getSharedPreferences("mghs_tt", Context.MODE_PRIVATE);
        for (int d = 0; d < 5; d++) {
            for (int p = 0; p < 8; p++) {
                String key = DAYS[d] + "_" + (p + 1);
                fields[d][p].setText(prefs.getString(key, ""));
            }
        }
    }

    private void saveAndUpdate() {
        SharedPreferences.Editor ed =
            getSharedPreferences("mghs_tt", Context.MODE_PRIVATE).edit();
        for (int d = 0; d < 5; d++) {
            for (int p = 0; p < 8; p++) {
                String key = DAYS[d] + "_" + (p + 1);
                ed.putString(key, fields[d][p].getText().toString().trim());
            }
        }
        ed.apply();
        TimetableWidget.forceUpdate(this);
        Toast.makeText(this,
            "Saved! Widget updated on home screen.", Toast.LENGTH_SHORT).show();
    }

    private int dp(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
