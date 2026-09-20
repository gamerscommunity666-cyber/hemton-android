package ai.hemton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class OrbView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float pulse = 0f;

    public OrbView(Context context) {
        super(context);
        postInvalidateDelayed(16);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;

        canvas.drawColor(Color.rgb(5, 5, 10));

        pulse += 0.06f;

        float glow = 80f + (float)Math.sin(pulse) * 15f;

        paint.setColor(Color.argb(35, 80, 140, 255));
        canvas.drawCircle(cx, cy, glow + 55f, paint);

        paint.setColor(Color.argb(70, 100, 180, 255));
        canvas.drawCircle(cx, cy, glow + 25f, paint);

        paint.setColor(Color.rgb(90, 170, 255));
        canvas.drawCircle(cx, cy, glow, paint);

        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(28f);
        canvas.drawText("H", cx, cy + 10f, paint);

        postInvalidateDelayed(16);
    }
}
