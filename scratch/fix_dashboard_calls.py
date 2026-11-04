with open("app/src/main/java/com/simats/codeingo/ui/dashboard/RealmHeroDashboardCards.kt", "r", encoding="utf-8") as f:
    c = f.read()

c = c.replace(".pressSquish()\n                    .clip(RoundedCornerShape(14.dp))\n                    .background(PhoenixGreen)\n                    .clickable(onClick = onContinueClick)", ".pressSquish(onClick = onContinueClick)\n                    .clip(RoundedCornerShape(14.dp))\n                    .background(PhoenixGreen)")
c = c.replace(".pressSquish()\n            .clip(RoundedCornerShape(16.dp))\n            .background(Color(0xFF131B2A))\n            .border(1.2.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))\n            .clickable(onClick = onClick)", ".pressSquish(onClick = onClick)\n            .clip(RoundedCornerShape(16.dp))\n            .background(Color(0xFF131B2A))\n            .border(1.2.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))")
c = c.replace("modifier = Modifier.pulse(1.0f..1.12f, 600)", "modifier = Modifier.pulse(minScale = 1.0f, maxScale = 1.12f, periodMs = 600)")

with open("app/src/main/java/com/simats/codeingo/ui/dashboard/RealmHeroDashboardCards.kt", "w", encoding="utf-8") as f:
    f.write(c)
print("Fixed RealmHeroDashboardCards.kt")

with open("app/src/main/java/com/simats/codeingo/ui/mascot/MascotController.kt", "r", encoding="utf-8") as f:
    m = f.read()

m = m.replace("Modifier.pulse(1.0f..1.12f, 450)", "Modifier.pulse(minScale = 1.0f, maxScale = 1.12f, periodMs = 450)")
m = m.replace("Modifier.pulse(1.0f..1.08f, 600)", "Modifier.pulse(minScale = 1.0f, maxScale = 1.08f, periodMs = 600)")

with open("app/src/main/java/com/simats/codeingo/ui/mascot/MascotController.kt", "w", encoding="utf-8") as f:
    f.write(m)
print("Fixed MascotController.kt")

