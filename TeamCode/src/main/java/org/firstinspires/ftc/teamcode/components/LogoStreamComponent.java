package org.firstinspires.ftc.teamcode.components;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.firstinspires.ftc.robotcore.external.function.Consumer;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamServer;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.firstinspires.ftc.teamcode.R;

import dev.nextftc.core.components.Component;
import dev.nextftc.ftc.ActiveOpMode;

/**
 * Pushes a static image to the Driver Station's Camera Stream panel as soon as an
 * OpMode is selected/initialized - no camera hardware involved. Same technique used
 * by EasyOpenCV/VisionPortal to preview frames on the DS (CameraStreamServer#setSource),
 * just fed a fixed Bitmap instead of live camera frames.
 *
 * On the DS: open the three-dot menu -> Camera Stream to view it.
 *
 * Add to any OpMode's addComponents(...) call to show the logo on init.
 */
public class LogoStreamComponent implements Component, CameraStreamSource {

    public static final LogoStreamComponent INSTANCE = new LogoStreamComponent();

    private Bitmap logo;

    private LogoStreamComponent() {
    }

    @Override
    public void preInit() {
        if (logo == null) {
            logo = BitmapFactory.decodeResource(
                    ActiveOpMode.getHardwareMap().appContext.getResources(),
                    R.drawable.team_logo);
        }
        CameraStreamServer.getInstance().setSource(this);
    }

    @Override
    public void getFrameBitmap(Continuation<? extends Consumer<Bitmap>> continuation) {
        continuation.dispatch(bitmapConsumer -> bitmapConsumer.accept(logo));
    }
}
