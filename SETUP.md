# Robot Setup Guide

How to go from this repo to code actually running on the robot.

## 1. Prerequisites

- **Android Studio** — Narwhal 3 Feature Drop or later (required by FTC SDK v12.0).
- **Driver Station app**, installed on your Driver Hub or Android phone.
- **Control Hub**, charged and powered on.
- A robot wired with: 4 Studica mecanum drive motors and 2 goBILDA Yellow Jacket catapult motors. Heading comes from the Control Hub's built-in IMU — no external odometry hardware needed.

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
| `catapultLeft` | Motor | goBILDA Yellow Jacket, catapult side 1 |
| `catapultRight` | Motor | goBILDA Yellow Jacket, catapult side 2 |
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

This shows live telemetry (wheel powers, heading, catapult position) and lets you edit every `TuningConfig` value in real time — no redeploy needed. Use this to dial in `CATAPULT_FIRE_POWER`, `CATAPULT_FIRE_POSITION`, `CATAPULT_READY_POSITION`, `CATAPULT_TOLERANCE`, and `DRIVE_SPEED_MULTIPLIER` against the real robot, then copy the values you land on back into `TuningConfig.java` so they stick after a redeploy.

## 7. Controls (MainTeleOp)

| Input | Action |
|---|---|
| Gamepad1 left stick | Drive forward/strafe (field-centric by default) |
| Gamepad1 right stick X | Turn |
| Gamepad1 left bumper (held) | Switch to robot-centric driving |
| Gamepad1 A | Zero heading to current facing |
| Gamepad2 A | Fire catapult |
| Gamepad2 B | Return catapult to ready position |

## 8. Known Gaps

- **Catapult tuning is placeholder.** The fire/ready target positions and power in `TuningConfig` aren't tuned to the real mechanism yet — expect to adjust them via Panels before the catapult behaves correctly.
- **No autonomous yet.** This is teleop only; Pedro Pathing/autonomous path following is a deliberately separate future pass.
- **Unverified first compile.** This code was written without a local Android SDK to build against. A few Java-Kotlin interop calls into NextFTC (`.INSTANCE` accessors, `ActiveOpMode.getGamepad1()`/`getHardwareMap()`) are reasonable-confidence guesses, not confirmed — if Gradle sync/build throws errors on those specific lines, they're expected to be small naming fixes, not structural problems.

## Troubleshooting

- **Gradle sync fails on `dev.nextftc:*` or `com.bylazar:*` artifacts** — confirm you have internet access on first sync (these resolve from Maven Central) and that Android Studio's `local.properties` points at a valid SDK.
- **OpMode doesn't appear on Driver Station** — confirm the app finished installing (check Android Studio's Run output) and that the Driver Station is connected to the same robot.
- **Robot drives in the wrong direction relative to the driver** — check `HUB_LOGO_FACING_DIRECTION`/`HUB_USB_FACING_DIRECTION` in `TuningConfig` match how the hub is actually mounted; until fixed, hold left bumper for robot-centric driving as a fallback, or press A to re-zero heading from the current facing.
- **Catapult doesn't move / moves unpredictably** — the two motors are driven as a leader/follower pair (`CatapultSubsystem`); confirm both `catapultLeft`/`catapultRight` are wired and named correctly, then tune via Panels before assuming there's a code bug.
