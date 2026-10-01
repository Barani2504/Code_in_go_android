package com.simats.codeingo.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.codeingo.data.model.DashboardTab
import com.simats.codeingo.ui.theme.DarkBackground
import com.simats.codeingo.ui.theme.DsaBlue
import com.simats.codeingo.ui.theme.DuolingoBlue
import com.simats.codeingo.ui.theme.InputBorder
import com.simats.codeingo.ui.theme.SubtextGray

@Composable
fun SideMenuDrawer(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    text = "DSA Learning",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = DsaBlue
                )
                Text(
                    text = "Duolingo for Data Structures",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SubtextGray
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(InputBorder)
            )

            Spacer(modifier = Modifier.height(16.dp))

            DashboardTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                val shape = RoundedCornerShape(14.dp)
                val isMore = tab == DashboardTab.MORE

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(shape)
                        .background(
                            if (isSelected && !isMore) DuolingoBlue.copy(alpha = 0.18f) else Color.Transparent
                        )
                        .border(
                            width = 1.5.dp,
                            color = if (isSelected && !isMore) DuolingoBlue.copy(alpha = 0.4f) else Color.Transparent,
                            shape = shape
                        )
                        .clickable {
                            if (isMore) {
                                onMoreClick()
                            } else {
                                onTabSelected(tab)
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tab.gameEmoji,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = tab.title.uppercase(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected && !isMore) DuolingoBlue else Color.White
                    )
                }
            }
        }

        // Right border line
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(InputBorder)
                .align(Alignment.CenterEnd)
        )
    }
}
