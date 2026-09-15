# Robot Setup Guide

How to go from this repo to code actually running on the robot.

## 1. Prerequisites

- **Android Studio** — Narwhal 3 Feature Drop or later (required by FTC SDK v12.0).
- **Driver Station app**, installed on your Driver Hub or Android phone.
- **Control Hub**, charged and powered on.
- A robot wired with: 4 mecanum drive motors, 2 catapult motors, and a goBILDA Pinpoint odometry computer.

## 2. Open the Project

Open the repo root (this directory) in Android Studio as an existing project. Let Gradle sync — this resolves the FTC SDK (v12.0.0), NextFTC (`dev.nextftc:*`), and Panels (`com.bylazar:fullpanels`) dependencies. First sync will take a few minutes.

If prompted for an SDK location, Android Studio will create `local.properties` automatically — it's gitignored, no need to touch it.

## 3. Robot Configuration (Driver Station)

Connect the Driver Station to the Control Hub's Wi-Fi network, then build a robot configuration with devices named **exactly** as below — the code looks up hardware by these names, and nothing will run if they don't match.

| Name | Type | Notes |
|---|---|---|
| `frontLeft` | Motor | goBILDA Yellow Jacket |
| `frontRight` | Motor | goBILDA Yellow Jacket |
| `backLeft` | Motor | goBILDA Yellow Jacket |
| `backRight` | Motor | goBILDA Yellow Jacket |
| `catapultLeft` | Motor | goBILDA Yellow Jacket, catapult side 1 |
| `catapultRight` | Motor | goBILDA Yellow Jacket, catapult side 2 |
| `pinpoint` | I2C Device → goBILDA Pinpoint Odometry Computer | **Any I2C port except port 0** (reserved for the internal IMU) |

Save and activate the configuration on the Driver Station before deploying.

## 4. Pinpoint Setup

The drivetrain uses Pinpoint's heading for field-centric driving, so it needs to be physically mounted and roughly configured before driving feels right (though it'll run fine before that — driving will just be robot-centric-looking until offsets are correct).

1. Mount both odometry pods, then measure their offsets from the robot's tracking center (see the `pinpoint` skill's `POD_OFFSETS.md` for how to measure this).
2. Update `TuningConfig.PINPOINT_X_OFFSET_MM` / `PINPOINT_Y_OFFSET_MM` in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/config/TuningConfig.java` with the real measured values (currently `0.0`/`0.0` placeholders). These can also be tweaked live via Panels (see below), but bake the final values into the file so they survive a redeploy.
3. If you're using swingarm pods instead of 4-bar pods, change `GoBildaOdometryPods.goBILDA_4_BAR_POD` to `goBILDA_SWINGARM_POD` in `DrivetrainSubsystem.initialize()`.
4. **Keep the robot stationary during init** — `DrivetrainSubsystem.initialize()` calls `pinpoint.resetPosAndIMU()`, which recalibrates while the Driver Station is on the init screen (before pressing start). Moving the robot during this window will throw off heading for the whole match.
5. Check the Pinpoint's LED after init — it should be solid green (READY) before you press start:

| LED Color | Status | Action |
|---|---|---|
| Green | READY | Normal, good to go |
| Red (steady) | NOT_READY | Still powering up |
| Red (blinking) | CALIBRATING | Wait ~0.25s |
| Purple | NO_PODS_DETECTED | Check both pod connections |
| Blue | X_POD_NOT_DETECTED | Check forward pod |
| Orange | Y_POD_NOT_DETECTED | Check strafe pod |

## 5. Deploy

From Android Studio: select `MainTeleOp` isn't a build target directly — instead, hit **Run** (▶) with the `TeamCode` module selected, connected to the Control Hub over USB or Wi-Fi. Android Studio builds and installs the app; `MainTeleOp` will then appear in the Driver Station's TeleOp list.

Command-line alternative:
```bash
./gradlew installDebug
```

## 6. Panels Dashboard

Once deployed and connected to the robot's Wi-Fi, open a browser (laptop or phone) to:

```
http://192.168.43.1:8001
```

This shows live telemetry (wheel powers, heading, catapult position) and lets you edit every `TuningConfig` value in real time — no redeploy needed. Use this to dial in `CATAPULT_FIRE_POWER`, `CATAPULT_FIRE_POSITION`, `CATAPULT_READY_POSITION`, `CATAPULT_TOLERANCE`, and `DRIVE_SPEED_MULTIPLIER` against the real robot, then copy the values you land on back into `TuningConfig.java` so they stick after a redeploy.

## 7. Controls (MainTeleOp)

| Input | Action |
|---|---|
| Gamepad1 left stick | Drive forward/strafe (field-centric by default) |
| Gamepad1 right stick X | Turn |
| Gamepad1 left bumper (held) | Switch to robot-centric driving |
| Gamepad2 A | Fire catapult |
| Gamepad2 B | Return catapult to ready position |

## 8. Known Gaps

- **Catapult tuning is placeholder.** The fire/ready target positions and power in `TuningConfig` aren't tuned to the real mechanism yet — expect to adjust them via Panels before the catapult behaves correctly.
- **No autonomous yet.** This is teleop only; Pedro Pathing/autonomous path following is a deliberately separate future pass.
- **Unverified first compile.** This code was written without a local Android SDK to build against. A few Java-Kotlin interop calls into NextFTC (`.INSTANCE` accessors, `ActiveOpMode.getGamepad1()`/`getHardwareMap()`) are reasonable-confidence guesses, not confirmed — if Gradle sync/build throws errors on those specific lines, they're expected to be small naming fixes, not structural problems.

## Troubleshooting

- **Gradle sync fails on `dev.nextftc:*` or `com.bylazar:*` artifacts** — confirm you have internet access on first sync (these resolve from Maven Central) and that Android Studio's `local.properties` points at a valid SDK.
- **OpMode doesn't appear on Driver Station** — confirm the app finished installing (check Android Studio's Run output) and that the Driver Station is connected to the same robot.
- **Robot drives in the wrong direction relative to the driver** — check Pinpoint LED is green and pod offsets are set; until then, hold left bumper for robot-centric driving as a fallback.
- **Catapult doesn't move / moves unpredictably** — the two motors are driven as a leader/follower pair (`CatapultSubsystem`); confirm both `catapultLeft`/`catapultRight` are wired and named correctly, then tune via Panels before assuming there's a code bug.
