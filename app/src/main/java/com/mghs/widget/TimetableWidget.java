package com.mghs.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import java.util.Calendar;

public class TimetableWidget extends AppWidgetProvider {

    // Period start times in minutes from midnight
    static final int[] P_START = {460, 510, 550, 590, 650, 690, 730, 770};
    // Period end times in minutes from midnight
    static final int[] P_END   = {500, 550, 590, 630, 690, 730, 770, 810};
    // Period display times
    static final String[] P_TIME = {
        "7:40","8:30","9:10","9:50","10:50","11:30","12:10","12:50"
    };

    @Override
    public void onUpdate(Context ctx, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) updateWidget(ctx, mgr, id);
    }

    public static void updateWidget(Context ctx, AppWidgetManager mgr, int widgetId) {
        RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.widget_layout);
        SharedPreferences prefs = ctx.getSharedPreferences("mghs_tt", Context.MODE_PRIVATE);

        // ── Time ────────────────────────────────────────────────────────────
        Calendar cal = Calendar.getInstance();
        int dow  = cal.get(Calendar.DAY_OF_WEEK); // 1=Sun, 2=Mon … 7=Sat
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min  = cal.get(Calendar.MINUTE);
        int nowMins = hour * 60 + min;

        String clockStr = String.format("%02d:%02d", hour, min);
        views.setTextViewText(R.id.wgt_clock, clockStr);

        // ── Day prefix & name ────────────────────────────────────────────────
        String prefix, dayName;
        switch (dow) {
            case Calendar.MONDAY:    prefix = "mo"; dayName = "Monday";    break;
            case Calendar.TUESDAY:   prefix = "tu"; dayName = "Tuesday";   break;
            case Calendar.WEDNESDAY: prefix = "we"; dayName = "Wednesday"; break;
            case Calendar.THURSDAY:  prefix = "th"; dayName = "Thursday";  break;
            case Calendar.FRIDAY:    prefix = "fr"; dayName = "Friday";    break;
            case Calendar.SATURDAY:  prefix = "";   dayName = "Saturday";  break;
            default:                 prefix = "";   dayName = "Sunday";    break;
        }
        views.setTextViewText(R.id.wgt_day, dayName);

        // ── Build period list ────────────────────────────────────────────────
        StringBuilder sb = new StringBuilder();
        int count = 0;

        if (prefix.isEmpty()) {
            sb.append("Weekend \uD83C\uDF89\nNo classes today");
        } else {
            for (int i = 0; i < 8; i++) {
                String key = prefix + "_" + (i + 1);
                String subj = prefs.getString(key, "");
                if (subj.isEmpty()) continue;

                boolean isNow = (nowMins >= P_START[i] && nowMins < P_END[i]);
                if (isNow) {
                    sb.append("\u25BA P").append(i + 1)
                      .append(" ").append(P_TIME[i])
                      .append("  ").append(subj).append("  \u25C4 NOW\n");
                } else {
                    sb.append("   P").append(i + 1)
                      .append(" ").append(P_TIME[i])
                      .append("  ").append(subj).append("\n");
                }
                count++;
            }
            if (count == 0) sb.append("No classes today\nTap to add your timetable");
        }

        views.setTextViewText(R.id.wgt_periods, sb.toString().trim());
        views.setTextViewText(R.id.wgt_footer,
            count + " classes today  |  Tap to edit");

        // ── Tap to open app ──────────────────────────────────────────────────
        Intent intent = new Intent(ctx, MainActivity.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, intent, flags);
        views.setOnClickPendingIntent(R.id.wgt_root, pi);

        mgr.updateAppWidget(widgetId, views);
    }

    /** Call this from anywhere to force-refresh all widgets */
    public static void forceUpdate(Context ctx) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(ctx);
        android.content.ComponentName cn =
            new android.content.ComponentName(ctx, TimetableWidget.class);
        int[] ids = mgr.getAppWidgetIds(cn);
        for (int id : ids) updateWidget(ctx, mgr, id);
    }
}
