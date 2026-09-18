# Robot Setup Guide

How to go from this repo to code actually running on the robot.

## 1. Prerequisites

- **Android Studio** — Narwhal 3 Feature Drop or later (required by FTC SDK v12.0).
- **Driver Station app**, installed on your Driver Hub or Android phone.
- **Control Hub**, charged and powered on.
- A robot wired with: 4 Studica mecanum drive motors, 1 REV planetary motor driving a hooded flywheel, and 1 feeder servo. Heading comes from the Control Hub's built-in IMU — no external odometry hardware needed.

## 2. Open the Project

Open the repo root (this directory) in Android Studio as an existing project. Let Gradle sync — this resolves the FTC SDK (v12.0.0), NextFTC (`dev.nextftc:*`), and Panels (`com.bylazar:fullpanels`) dependencies. First sync will take a few minutes.

If prompted for an SDK location, Android Studio will create `local.properties` automatically — it's gitignored, no need to touch it.

## 3. Robot Configuration (Driver Station)

Connect the Driver Station to the Control Hub's Wi-Fi network, then build a robot configuration with devices named **exactly** as below — the code looks up hardware by these names, and nothing will run if they don't match.

| Name | Type | Notes |
|---|---|---|
| `frontLeft` | Motor | Studica |
| `frontRight` | Motor | Studica |
| `backLeft` | Motor | Studica |
| `backRight` | Motor | Studica |
| `flywheel` | Motor | REV planetary, drives the hooded flywheel |
| `feeder` | Servo | Pushes game piece into the flywheel |
| `imu` | Built-in IMU | Already present in the default Control Hub configuration — don't add it manually, just make sure it's still named `imu` |

Save and activate the configuration on the Driver Station before deploying.

## 4. IMU Orientation Setup

The drivetrain uses the Control Hub's built-in IMU for heading (field-centric driving). It needs to know which way the hub is physically mounted, or heading — and therefore field-centric driving — will be wrong.

1. Figure out which way the Control/Expansion Hub's REV logo faces, and which way its USB ports face, when mounted on the robot.
2. Set `TuningConfig.HUB_LOGO_FACING_DIRECTION` and `HUB_USB_FACING_DIRECTION` in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/config/TuningConfig.java` to match (currently placeholders: `UP` / `FORWARD`). Valid values are documented on the `RevHubOrientationOnRobot.LogoFacingDirection` / `UsbFacingDirection` enums (autocomplete in Android Studio will list them: `UP`, `DOWN`, `FORWARD`, `BACKWARD`, `LEFT`, `RIGHT`).
3. These aren't Panels-editable (they're enum constants, not simple numbers) — edit the file directly and redeploy after changing them.
4. In `MainTeleOp`, press **gamepad1 A** any time to zero heading to the robot's current facing — handy for re-zeroing between matches or if heading drifts.

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

This shows live telemetry (wheel powers, heading, flywheel target/actual velocity) and lets you edit every `TuningConfig` value in real time — no redeploy needed. Use this to dial in `FLYWHEEL_TARGET_VELOCITY`, `FLYWHEEL_KP`/`KI`/`KD`, `FLYWHEEL_KV`/`KA`/`KS`, `FLYWHEEL_VELOCITY_TOLERANCE`, `FEEDER_EXTEND_POSITION`/`RETRACT_POSITION`/`EXTEND_SECONDS`, and `DRIVE_SPEED_MULTIPLIER` against the real robot, then copy the values you land on back into `TuningConfig.java` so they stick after a redeploy.

## 7. Controls (MainTeleOp)

| Input | Action |
|---|---|
| Gamepad1 left stick | Drive forward/strafe (field-centric by default) |
| Gamepad1 right stick X | Turn |
| Gamepad1 left bumper (held) | Switch to robot-centric driving |
| Gamepad1 A | Zero heading to current facing |
| Gamepad2 A | Toggle flywheel spin-up/stop |
| Gamepad2 B | Feed a game piece into the flywheel (only fires once flywheel is at speed) |

## 8. Team Logo on Driver Station

`MainTeleOp` pushes `TeamCode/src/main/res/drawable/team_logo.png` to the Driver Station's **Camera Stream** panel as soon as the OpMode is selected and initialized — no camera hardware needed. On the DS, open the three-dot menu → **Camera Stream** to view it. This uses the FTC SDK's `CameraStreamServer`/`CameraStreamSource` API (the same mechanism EasyOpenCV/VisionPortal use to preview live camera frames), just fed a static image instead.

To change the image: replace `TeamCode/src/main/res/drawable/team_logo.png` with a new file of the same name (or add a new file and update the `R.drawable.team_logo` reference in `LogoStreamComponent.java`). Android resource filenames must be lowercase with underscores only.

To add this to another OpMode (e.g. once autonomous exists), add `LogoStreamComponent.INSTANCE` to that OpMode's `addComponents(...)` call.

## 9. Known Gaps

- **Flywheel tuning is placeholder.** The target velocity, PIDF, and feedforward constants in `TuningConfig` aren't tuned to the real REV planetary motor yet — expect to adjust them via Panels before shots are consistent.
- **No autonomous yet.** This is teleop only; Pedro Pathing/autonomous path following is a deliberately separate future pass.
- **Unverified first compile.** This code was written without a local Android SDK to build against. A few Java-Kotlin interop calls into NextFTC (`.INSTANCE` accessors, `ActiveOpMode.getGamepad1()`/`getHardwareMap()`) are reasonable-confidence guesses, not confirmed — if Gradle sync/build throws errors on those specific lines, they're expected to be small naming fixes, not structural problems.

## Troubleshooting

- **Gradle sync fails on `dev.nextftc:*` or `com.bylazar:*` artifacts** — confirm you have internet access on first sync (these resolve from Maven Central) and that Android Studio's `local.properties` points at a valid SDK.
- **OpMode doesn't appear on Driver Station** — confirm the app finished installing (check Android Studio's Run output) and that the Driver Station is connected to the same robot.
- **Robot drives in the wrong direction relative to the driver** — check `HUB_LOGO_FACING_DIRECTION`/`HUB_USB_FACING_DIRECTION` in `TuningConfig` match how the hub is actually mounted; until fixed, hold left bumper for robot-centric driving as a fallback, or press A to re-zero heading from the current facing.
- **Flywheel won't spin up / never reports at-speed** — confirm `flywheel` is wired and named correctly; if it spins but never reaches `FLYWHEEL_TARGET_VELOCITY`, tune `FLYWHEEL_KP`/`KV` via Panels before assuming there's a code bug.
- **Feed does nothing** — the feed command only schedules once `FlywheelSubsystem.isAtSpeed()` is true; confirm the flywheel is spun up (gamepad2 A) first, and that `feeder` is wired and named correctly.
