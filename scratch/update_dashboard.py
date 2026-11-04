path = "app/src/main/java/com/simats/codeingo/ui/dashboard/MainDashboardScreen.kt"
with open(path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Imports
import_marker = "import com.simats.codeingo.ui.dashboard.BasecampFoundationsCard"
if "com.simats.codeingo.ui.dashboard.ContinueHeroCard" not in content:
    new_imports = """import com.simats.codeingo.ui.dashboard.ContinueHeroCard
import com.simats.codeingo.ui.dashboard.TodaysMissionsCarousel
import com.simats.codeingo.ui.dashboard.DailyGoalAndStreakPill
import com.simats.codeingo.ui.dashboard.DailyRewardChestCard
import com.simats.codeingo.ui.mascot.MascotView
import com.simats.codeingo.ui.mascot.MascotEmotion
import com.simats.codeingo.ui.mascot.PhoenixStage"""
    content = content.replace("package com.simats.codeingo.ui.dashboard", "package com.simats.codeingo.ui.dashboard\n\n" + new_imports, 1)

# 2. Add Living Realm Hero Cards above BasecampFoundationsCard
target = """                                // Spacing for floating top stats header (status bar + cutout insets + header height)
                                Spacer(modifier = Modifier.height(topInsetPadding + 96.dp))

                                // Chapter 0: Basecamp Foundations Primer Card (Android Expansion)"""

replacement = """                                // Spacing for floating top stats header (status bar + cutout insets + header height)
                                Spacer(modifier = Modifier.height(topInsetPadding + 96.dp))

                                // ── LIVING REALM DASHBOARD HERO CARDS ──────────────────────
                                val currentChapterModel = remember(maxUnlockedChapter) {
                                    com.simats.codeingo.data.model.DSA5ChapterData.chapters.firstOrNull { it.id == maxUnlockedChapter }
                                }
                                val currentChapterTitle = currentChapterModel?.title ?: "Cryptographic Security Foundations"

                                ContinueHeroCard(
                                    activeLevelIndex = activeLevelIndex,
                                    chapterId = maxUnlockedChapter,
                                    chapterTitle = currentChapterTitle,
                                    onContinueClick = {
                                        onStartLesson(maxUnlockedChapter, activeLevelIndex, 5, false)
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )

                                DailyGoalAndStreakPill(
                                    currentXp = totalXP % 50,
                                    targetXp = 50,
                                    streakDays = streakDays,
                                    hasStreakFreeze = true,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )

                                TodaysMissionsCarousel(
                                    onOpenLesson = { onStartLesson(maxUnlockedChapter, activeLevelIndex, 5, false) },
                                    onOpenMistakeVault = { selectedTab = DashboardTab.VISUALIZER },
                                    onOpenArena = { selectedTab = DashboardTab.ARENAS },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )

                                DailyRewardChestCard(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )

                                // Chapter 0: Basecamp Foundations Primer Card (Android Expansion)"""

if target in content:
    content = content.replace(target, replacement, 1)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print("MainDashboardScreen.kt updated successfully.")
else:
    print("WARN: target not found in MainDashboardScreen.kt")

