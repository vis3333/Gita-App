package com.example.model

data class GitaVerse(
    val id: String,
    val chapter: Int,
    val verse: Int,
    val chapterName: String,
    val sanskrit: String,
    val transliteration: String,
    val englishTranslation: String,
    val hindiTranslation: String,
    val coreTeaching: String,
    val context: String,
    val commentary: String,
    val practicalApplication: String,
    val reflectionPrompt: String,
    val tags: List<String>
) {
    val verseReference: String
        get() = "BG $chapter.$verse"
}
