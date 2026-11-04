for filename in [
    "app/src/main/java/com/simats/codeingo/ui/components/motion/CelebrationOverlay.kt",
    "app/src/main/java/com/simats/codeingo/ui/exercise/AssessmentScreen.kt",
    "app/src/main/java/com/simats/codeingo/ui/exercise/DSAGamifiedExerciseViews.kt"
]:
    with open(filename, "r", encoding="utf-8") as f:
        c = f.read()
    c = c.replace("com.simats.codeingo.ui.components.motion.AshMotionModifiers.", "com.simats.codeingo.ui.components.motion.")
    with open(filename, "w", encoding="utf-8") as f:
        f.write(c)
    print(f"Fixed imports in {filename}")

