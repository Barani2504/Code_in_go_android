package com.simats.codeingo.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simats.codeingo.ui.components.motion.skeletonShimmer

/**
 * Native Jetpack Compose Skeleton Shimmer for Dashboard & Level Paths.
 *
 * Implements Part 2F:
 * - Replaces blank screens with progressive disclosure skeleton shimmers.
 * - Simulates the top stats island, hero card, missions carousel, and winding nodes.
 */
@Composable
fun DashboardSkeletonLoading(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Stats Island Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(20.dp))
                .skeletonShimmer()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Greeting / Hero Card Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(22.dp))
                .skeletonShimmer()
        )

        // 3. Goal Ring & Streak Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .clip(RoundedCornerShape(18.dp))
                .skeletonShimmer()
        )

        // 4. Missions Carousel Skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(96.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .skeletonShimmer()
            )
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(96.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .skeletonShimmer()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Winding Node Path Skeleton
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .skeletonShimmer()
            )
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .skeletonShimmer()
            )
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .skeletonShimmer()
            )
        }
    }
}

