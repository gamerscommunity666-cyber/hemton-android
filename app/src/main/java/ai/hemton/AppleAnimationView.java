package ai.hemton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

public class AppleAnimationView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float progress = 0f;
    private boolean running = false;

    public AppleAnimationView(Context context) {
        super(context);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public void startAnimation() {
        progress = 0f;
        running = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!running) {
            return;
        }

        canvas.drawColor(Color.rgb(5, 5, 10));

        float width = getWidth();
        float height = getHeight();
        float centerX = width / 2f;

        float appleY;

        if (progress < 0.55f) {
            float fallProgress = progress / 0.55f;
            appleY = -100f
                    + (height * 0.55f + 100f) * fallProgress;
        } else {
            appleY = height * 0.55f;
        }

        // Glow
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(45, 0, 150, 255));

        canvas.drawCircle(
                centerX,
                appleY,
                130f,
                paint
        );

        // Apple
        float split = 0f;

        if (progress > 0.65f) {
            split =
                    (progress - 0.65f)
                    / 0.35f
                    * 80f;
        }

        paint.setColor(Color.rgb(220, 30, 45));

        canvas.drawCircle(
                centerX - 35f - split,
                appleY,
                55f,
                paint
        );

        canvas.drawCircle(
                centerX + 35f + split,
                appleY,
                55f,
                paint
        );

        canvas.drawOval(
                centerX - 90f - split,
                appleY - 5f,
                centerX + 90f + split,
                appleY + 75f,
                paint
        );

        // Stem
        paint.setColor(Color.rgb(90, 55, 25));
        paint.setStrokeWidth(12f);

        canvas.drawLine(
                centerX,
                appleY - 55f,
                centerX + 10f,
                appleY - 95f,
                paint
        );

        // HEMTON message
        if (progress > 0.75f) {

            float alpha =
                    Math.min(
                            1f,
                            (progress - 0.75f) / 0.25f
                    );

            paint.setAlpha((int) (255 * alpha));
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);

            paint.setTextSize(48f);

            canvas.drawText(
                    "HEMTON",
                    centerX,
                    height * 0.72f,
                    paint
            );

            paint.setTextSize(24f);

            canvas.drawText(
                    "How can I help you?",
                    centerX,
                    height * 0.78f,
                    paint
            );

            paint.setAlpha(255);
        }

        if (progress < 1f) {
            progress += 0.018f;
            postInvalidateDelayed(16);
        } else {
            running = false;
        }
    }
}
