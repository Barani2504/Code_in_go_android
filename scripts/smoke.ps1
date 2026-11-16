# Smoke test automation script for AshCrypt
# Runs Gradle assemble, installs debug APK, and runs 5,000 monkey events

param(
    [string]$PackageName = "com.simats.codeingo",
    [int]$Events = 5000,
    [string]$OutputFile = "docs/crash-baseline.txt"
)

Write-Host "=== AshCrypt Smoke & Crash Reproducer ==="
Write-Host "Target Package: $PackageName"
Write-Host "Events: $Events"

# Step 1: Ensure docs directory exists
if (-not (Test-Path "docs")) {
    New-Item -ItemType Directory -Path "docs" | Out-Null
}

# Step 2: Clear logcat
Write-Host "Clearing adb logcat buffer..."
adb logcat -c

# Step 3: Run monkey test
Write-Host "Running 5,000 UI stress monkey events (touch 70%, nav 15%, throttle 150ms)..."
adb shell monkey -p $PackageName --pct-touch 70 --pct-nav 15 --throttle 150 -v $Events

# Step 4: Dump crash logs
Write-Host "Checking for AndroidRuntime fatal errors..."
adb logcat -d -s AndroidRuntime:E > $OutputFile

$crashCount = (Get-Content $OutputFile | Measure-Object -Line).Lines
Write-Host "Monkey stress test finished. AndroidRuntime errors logged: $crashCount"
Write-Host "Baseline report written to $OutputFile"
