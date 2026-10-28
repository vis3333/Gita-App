package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GitaRepository
import com.example.model.GitaChapters
import com.example.ui.GitaViewModel
import com.example.ui.components.VerseCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: GitaViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedChapter by viewModel.selectedChapterFilter.collectAsState()
    val selectedTag by viewModel.selectedTagFilter.collectAsState()
    val filteredVerses by viewModel.filteredVerses.collectAsState()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val selectedDetailVerse by viewModel.selectedVerseForDetail.collectAsState()

    var chapterDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("explore_screen_content")
    ) {
        // Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search by keyword, theme, or BG 2.47...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_text_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Chapter Filter Dropdown
        ExposedDropdownMenuBox(
            expanded = chapterDropdownExpanded,
            onExpandedChange = { chapterDropdownExpanded = !chapterDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            val selectedChapterTitle = if (selectedChapter == null) {
                "All Chapters (1 to 18)"
            } else {
                val chObj = GitaChapters.allChapters.find { it.number == selectedChapter }
                "Chapter $selectedChapter: ${chObj?.sanskritName ?: ""}"
            }

            OutlinedTextField(
                value = selectedChapterTitle,
                onValueChange = {},
                readOnly = true,
                label = { Text("Filter by Chapter") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter Chapter",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = chapterDropdownExpanded) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .testTag("chapter_filter_dropdown")
            )

            ExposedDropdownMenu(
                expanded = chapterDropdownExpanded,
                onDismissRequest = { chapterDropdownExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Chapters (1 to 18)", fontWeight = FontWeight.Bold) },
                    onClick = {
                        viewModel.selectChapterFilter(null)
                        chapterDropdownExpanded = false
                    }
                )
                GitaChapters.allChapters.forEach { ch ->
                    DropdownMenuItem(
                        text = {
                            Text("Ch ${ch.number}: ${ch.sanskritName} (${ch.englishName})")
                        },
                        onClick = {
                            viewModel.selectChapterFilter(ch.number)
                            chapterDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Topic / Life Situation Tag Chips (Horizontally Scrollable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GitaRepository.getAllTags().forEach { tag ->
                val isSelected = selectedTag == tag
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectTagFilter(tag) },
                    label = { Text(text = tag, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("tag_chip_$tag")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Results count summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredVerses.size} Verses Found",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (searchQuery.isNotEmpty() || selectedChapter != null || selectedTag != "All") {
                Text(
                    text = "Reset Filters",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Verse List
        if (filteredVerses.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "🕉️", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No verses match your search",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Try searching for words like 'duty', 'peace', 'karma', 'mind', or select another chapter.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredVerses, key = { it.id }) { verse ->
                    VerseCard(
                        verse = verse,
                        isBookmarked = bookmarkedIds.contains(verse.id),
                        onToggleBookmark = { viewModel.toggleBookmark(verse.id) },
                        onViewCommentary = { viewModel.selectVerseForDetail(verse, navigate = true) },
                        onSpeak = { viewModel.speakVerse(verse) },
                        isSpeakingThisVerse = isSpeaking && selectedDetailVerse.id == verse.id
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}
