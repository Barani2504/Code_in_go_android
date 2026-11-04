path = "app/src/main/java/com/simats/codeingo/ui/exercise/DSAGamifiedExerciseViews.kt"
with open(path, "r", encoding="utf-8") as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if "python cipher_runtime.py" in line:
        lines[i] = "                    text = \"python cipher_runtime.py --token='$activeToken'\",\n"
        print(f"Replaced line {i+1}")

with open(path, "w", encoding="utf-8") as f:
    f.writelines(lines)
print("Saved.")

