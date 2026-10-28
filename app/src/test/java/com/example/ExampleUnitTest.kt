package com.example

import com.example.data.GitaRepository
import com.example.model.GitaChapters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun gitaRepository_hasVerses() {
        assertTrue("Repository must have curated verses", GitaRepository.verses.isNotEmpty())
    }

    @Test
    fun gitaRepository_dailyVerse_returnsValidVerse() {
        val verse1 = GitaRepository.getDailyVerse(1)
        val verse100 = GitaRepository.getDailyVerse(100)
        assertNotNull(verse1)
        assertNotNull(verse100)
        assertTrue(verse1.sanskrit.isNotEmpty())
        assertTrue(verse1.englishTranslation.isNotEmpty())
        assertTrue(verse1.coreTeaching.isNotEmpty())
    }

    @Test
    fun gitaRepository_search_findsKarmaVerses() {
        val results = GitaRepository.searchVerses("karma")
        assertTrue("Searching for 'karma' should return matches", results.isNotEmpty())
    }

    @Test
    fun gitaRepository_search_chapterFilter() {
        val ch2Verses = GitaRepository.searchVerses(query = "", chapterFilter = 2)
        assertTrue("Should return chapter 2 verses", ch2Verses.isNotEmpty())
        assertTrue("All returned verses should be chapter 2", ch2Verses.all { it.chapter == 2 })
    }

    @Test
    fun gitaChapters_hasEighteenChapters() {
        assertEquals("Bhagavad Gita must have 18 chapters", 18, GitaChapters.allChapters.size)
    }
}
