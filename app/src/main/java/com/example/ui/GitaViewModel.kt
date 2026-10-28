package com.example.ui

import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import com.example.data.GitaPreferences
import com.example.data.GitaRepository
import com.example.model.GitaVerse
import com.example.receiver.NotificationScheduler
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.Locale

enum class NavTab(val title: String) {
    DAILY("Daily Teaching"),
    EXPLORE("Explore & Search"),
    COMMENTARY("Commentary"),
    SAVED("Saved Verses"),
    SETTINGS("Settings & Dev")
}

class GitaViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = GitaPreferences(application)
    private var currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

    private val _currentNavTab = MutableStateFlow(NavTab.DAILY)
    val currentNavTab: StateFlow<NavTab> = _currentNavTab.asStateFlow()

    private val _dailyVerse = MutableStateFlow(GitaRepository.getDailyVerse(currentDayOfYear))
    val dailyVerse: StateFlow<GitaVerse> = _dailyVerse.asStateFlow()

    private val _selectedVerseForDetail = MutableStateFlow(GitaRepository.getDailyVerse(currentDayOfYear))
    val selectedVerseForDetail: StateFlow<GitaVerse> = _selectedVerseForDetail.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedChapterFilter = MutableStateFlow<Int?>(null)
    val selectedChapterFilter: StateFlow<Int?> = _selectedChapterFilter.asStateFlow()

    private val _selectedTagFilter = MutableStateFlow<String?>("All")
    val selectedTagFilter: StateFlow<String?> = _selectedTagFilter.asStateFlow()

    private val _filteredVerses = MutableStateFlow(GitaRepository.verses)
    val filteredVerses: StateFlow<List<GitaVerse>> = _filteredVerses.asStateFlow()

    private val _bookmarkedIds = MutableStateFlow(prefs.getBookmarks())
    val bookmarkedIds: StateFlow<Set<String>> = _bookmarkedIds.asStateFlow()

    private val _verseNotes = MutableStateFlow<Map<String, String>>(emptyMap())
    val verseNotes: StateFlow<Map<String, String>> = _verseNotes.asStateFlow()

    private val _isNotificationEnabled = MutableStateFlow(prefs.isNotificationEnabled)
    val isNotificationEnabled: StateFlow<Boolean> = _isNotificationEnabled.asStateFlow()

    private val _notificationHour = MutableStateFlow(prefs.notificationHour)
    val notificationHour: StateFlow<Int> = _notificationHour.asStateFlow()

    private val _notificationMinute = MutableStateFlow(prefs.notificationMinute)
    val notificationMinute: StateFlow<Int> = _notificationMinute.asStateFlow()

    private val _streakCount = MutableStateFlow(prefs.getStreak())
    val streakCount: StateFlow<Int> = _streakCount.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val initialTheme = try {
        AppThemeMode.valueOf(prefs.appThemeName)
    } catch (_: Exception) {
        AppThemeMode.SPIRITUAL_PAPER
    }
    private val _appTheme = MutableStateFlow(initialTheme)
    val appTheme: StateFlow<AppThemeMode> = _appTheme.asStateFlow()

    fun setAppTheme(theme: AppThemeMode) {
        _appTheme.value = theme
        prefs.appThemeName = theme.name
    }

    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false

    init {
        // Record daily read & streak
        val streak = prefs.recordDailyRead(currentDayOfYear)
        _streakCount.value = streak

        // Load notes for verses
        val notesMap = mutableMapOf<String, String>()
        for (v in GitaRepository.verses) {
            val note = prefs.getVerseNote(v.id)
            if (note.isNotEmpty()) {
                notesMap[v.id] = note
            }
        }
        _verseNotes.value = notesMap

        initTextToSpeech(application)
    }

    private fun initTextToSpeech(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialized = true
                textToSpeech?.language = Locale.ENGLISH
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }
                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })
            }
        }
    }

    fun setNavTab(tab: NavTab) {
        _currentNavTab.value = tab
    }

    fun selectVerseForDetail(verse: GitaVerse, navigate: Boolean = true) {
        _selectedVerseForDetail.value = verse
        if (navigate) {
            _currentNavTab.value = NavTab.COMMENTARY
        }
    }

    fun selectVerseById(verseId: String) {
        val found = GitaRepository.getVerseById(verseId)
        if (found != null) {
            _selectedVerseForDetail.value = found
            _dailyVerse.value = found
            _currentNavTab.value = NavTab.DAILY
        }
    }

    fun previousDayVerse() {
        currentDayOfYear = if (currentDayOfYear <= 1) 365 else currentDayOfYear - 1
        val verse = GitaRepository.getDailyVerse(currentDayOfYear)
        _dailyVerse.value = verse
        _selectedVerseForDetail.value = verse
    }

    fun nextDayVerse() {
        currentDayOfYear = if (currentDayOfYear >= 365) 1 else currentDayOfYear + 1
        val verse = GitaRepository.getDailyVerse(currentDayOfYear)
        _dailyVerse.value = verse
        _selectedVerseForDetail.value = verse
    }

    fun resetToToday() {
        currentDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val verse = GitaRepository.getDailyVerse(currentDayOfYear)
        _dailyVerse.value = verse
        _selectedVerseForDetail.value = verse
    }

    fun toggleBookmark(verseId: String) {
        prefs.toggleBookmark(verseId)
        _bookmarkedIds.value = prefs.getBookmarks()
    }

    fun isBookmarked(verseId: String): Boolean {
        return _bookmarkedIds.value.contains(verseId)
    }

    fun saveVerseNote(verseId: String, note: String) {
        prefs.saveVerseNote(verseId, note)
        val updated = _verseNotes.value.toMutableMap()
        if (note.trim().isEmpty()) {
            updated.remove(verseId)
        } else {
            updated[verseId] = note
        }
        _verseNotes.value = updated
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    fun selectChapterFilter(chapter: Int?) {
        _selectedChapterFilter.value = chapter
        applyFilter()
    }

    fun selectTagFilter(tag: String?) {
        _selectedTagFilter.value = tag
        applyFilter()
    }

    private fun applyFilter() {
        val tag = if (_selectedTagFilter.value == "All") null else _selectedTagFilter.value
        _filteredVerses.value = GitaRepository.searchVerses(
            query = _searchQuery.value,
            chapterFilter = _selectedChapterFilter.value,
            tagFilter = tag
        )
    }

    fun updateNotificationTime(context: Context, hour: Int, minute: Int) {
        _notificationHour.value = hour
        _notificationMinute.value = minute
        prefs.notificationHour = hour
        prefs.notificationMinute = minute
        if (_isNotificationEnabled.value) {
            NotificationScheduler.scheduleDailyNotification(context, hour, minute)
        }
    }

    fun toggleNotification(context: Context, enabled: Boolean) {
        _isNotificationEnabled.value = enabled
        prefs.isNotificationEnabled = enabled
        if (enabled) {
            NotificationScheduler.scheduleDailyNotification(
                context,
                _notificationHour.value,
                _notificationMinute.value
            )
        } else {
            NotificationScheduler.cancelDailyNotification(context)
        }
    }

    fun sendTestNotification(context: Context) {
        NotificationScheduler.sendImmediateTestNotification(context)
    }

    fun speakVerse(verse: GitaVerse) {
        if (!isTtsInitialized || textToSpeech == null) return

        if (_isSpeaking.value) {
            stopSpeaking()
            return
        }

        val speechText = "${verse.verseReference}. ${verse.transliteration}. Meaning: ${verse.englishTranslation}. Teaching: ${verse.coreTeaching}"
        textToSpeech?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "GITA_TTS_${verse.id}")
        _isSpeaking.value = true
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        _isSpeaking.value = false
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}
