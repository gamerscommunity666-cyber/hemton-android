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

        // Animation timing
        float appleY;

        if (progress < 0.55f) {
            float fallProgress = progress / 0.55f;
            appleY = -100f + (height * 0.55f + 100f) * fallProgress;
        } else {
            appleY = height * 0.55f;
        }

        // Glow
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(45, 0, 150, 255));
        canvas.drawCircle(centerX, appleY, 130f, paint);

        // Apple
        float split = 0f;

        if (progress
