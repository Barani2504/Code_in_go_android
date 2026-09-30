package com.simats.duolingo.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.duolingo.data.AppState
import com.simats.duolingo.ui.theme.*

data class VideoTutorial(
    val title: String,
    val duration: String,
    val url: String
)

data class VideoCategory(
    val title: String,
    val videos: List<VideoTutorial>
)

@Composable
fun LettersScreen() {
    val currentLang = AppState.selectedLanguage?.name ?: "Python"
    val context = LocalContext.current

    val categories = remember(currentLang) {
        listOf(
            VideoCategory(
                title = "Basic Level",
                videos = listOf(
                    VideoTutorial("$currentLang for Beginners - Part 1", "10:24", "https://www.youtube.com/results?search_query=${Uri.encode(currentLang)}+for+beginners"),
                    VideoTutorial("Variables & Data Types in $currentLang", "15:30", "https://www.youtube.com/results?search_query=${Uri.encode(currentLang)}+variables")
                )
            ),
            VideoCategory(
                title = "Intermediate Level",
                videos = listOf(
                    VideoTutorial("Functions and Logic in $currentLang", "22:15", "https://www.youtube.com/results?search_query=${Uri.encode(currentLang)}+functions"),
                    VideoTutorial("Object Oriented $currentLang", "18:45", "https://www.youtube.com/results?search_query=${Uri.encode(currentLang)}+oop")
                )
            ),
            VideoCategory(
                title = "Advanced Level",
                videos = listOf(
                    VideoTutorial("Concurrency & Performance in $currentLang", "30:00", "https://www.youtube.com/results?search_query=${Uri.encode(currentLang)}+concurrency"),
                    VideoTutorial("Build a Full App with $currentLang", "45:20", "https://www.youtube.com/results?search_query=${Uri.encode(currentLang)}+full+course")
                )
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DuolingoDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Learn $currentLang with Videos",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Watch tutorials from Basic to Advanced",
                color = DuolingoSubtext,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        // Video Categories
        categories.forEach { category ->
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Section divider with title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = DuolingoInputBorder.copy(alpha = 0.8f))
                    Text(category.title, color = DuolingoSubtext, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = DuolingoInputBorder.copy(alpha = 0.8f))
                }

                // Grid of 2 video cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    category.videos.forEach { video ->
                        VideoCard(
                            video = video,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.url))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun VideoCard(
    video: VideoTutorial,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DuolingoCardBg)
            .border(1.dp, DuolingoInputBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Thumbnail 16:9 Aspect Ratio with Play Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .background(DuolingoBlue.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text("▶", color = DuolingoBlue, fontSize = 28.sp)
        }

        Text(
            text = video.title,
            color = Color.White,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = video.duration,
            color = DuolingoSubtext,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
