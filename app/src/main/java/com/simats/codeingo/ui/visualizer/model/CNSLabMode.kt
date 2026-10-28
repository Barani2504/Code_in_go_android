package com.simats.codeingo.ui.visualizer.model

enum class CNSLabMode(val title: String, val icon: String, val subtitle: String) {
    EXPLORE("Explore", "🧪", "Hands-on parameters & state"),
    ATTACK("Attack / Break", "🔓", "Cryptanalysis & exploits"),
    CODE_TEST("Code & Test", "💻", "7 languages & NIST vectors"),
    CHALLENGE("Challenge", "🎯", "3 interactive skill checks")
}
