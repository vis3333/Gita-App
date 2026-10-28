package com.example.data

import android.content.Context
import android.content.SharedPreferences

class GitaPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("gita_daily_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BOOKMARKS = "key_bookmarks"
        private const val KEY_NOTES_PREFIX = "note_"
        private const val KEY_NOTIF_ENABLED = "key_notif_enabled"
        private const val KEY_NOTIF_HOUR = "key_notif_hour"
        private const val KEY_NOTIF_MINUTE = "key_notif_minute"
        private const val KEY_STREAK_COUNT = "key_streak_count"
        private const val KEY_LAST_READ_DAY = "key_last_read_day"
        private const val KEY_APP_THEME = "key_app_theme"
    }

    var appThemeName: String
        get() = prefs.getString(KEY_APP_THEME, "SPIRITUAL_PAPER") ?: "SPIRITUAL_PAPER"
        set(value) = prefs.edit().putString(KEY_APP_THEME, value).apply()

    fun isBookmarked(verseId: String): Boolean {
        return getBookmarks().contains(verseId)
    }

    fun toggleBookmark(verseId: String): Boolean {
        val current = getBookmarks().toMutableSet()
        val willBeBookmarked = if (current.contains(verseId)) {
            current.remove(verseId)
            false
        } else {
            current.add(verseId)
            true
        }
        prefs.edit().putStringSet(KEY_BOOKMARKS, current).apply()
        return willBeBookmarked
    }

    fun getBookmarks(): Set<String> {
        return prefs.getStringSet(KEY_BOOKMARKS, emptySet()) ?: emptySet()
    }

    fun saveVerseNote(verseId: String, note: String) {
        prefs.edit().putString(KEY_NOTES_PREFIX + verseId, note).apply()
    }

    fun getVerseNote(verseId: String): String {
        return prefs.getString(KEY_NOTES_PREFIX + verseId, "") ?: ""
    }

    var isNotificationEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIF_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIF_ENABLED, value).apply()

    var notificationHour: Int
        get() = prefs.getInt(KEY_NOTIF_HOUR, 7) // 7:00 AM default
        set(value) = prefs.edit().putInt(KEY_NOTIF_HOUR, value).apply()

    var notificationMinute: Int
        get() = prefs.getInt(KEY_NOTIF_MINUTE, 0)
        set(value) = prefs.edit().putInt(KEY_NOTIF_MINUTE, value).apply()

    fun recordDailyRead(dayOfYear: Int): Int {
        val lastDay = prefs.getInt(KEY_LAST_READ_DAY, -1)
        var streak = prefs.getInt(KEY_STREAK_COUNT, 0)

        if (lastDay == -1) {
            streak = 1
        } else if (lastDay == dayOfYear - 1) {
            streak += 1
        } else if (lastDay != dayOfYear) {
            streak = 1
        }

        prefs.edit()
            .putInt(KEY_LAST_READ_DAY, dayOfYear)
            .putInt(KEY_STREAK_COUNT, streak)
            .apply()

        return streak
    }

    fun getStreak(): Int {
        return prefs.getInt(KEY_STREAK_COUNT, 1)
    }
}
