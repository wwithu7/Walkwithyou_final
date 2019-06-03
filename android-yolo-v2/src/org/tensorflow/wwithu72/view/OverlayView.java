package org.tensorflow.wwithu72.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import org.tensorflow.wwithu72.Config;
import org.tensorflow.wwithu72.model.BoxPosition;
import org.tensorflow.wwithu72.model.Recognition;
import org.tensorflow.wwithu72.util.ClassAttrProvider;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple View providing a render callback to other classes.
 * Modified by Zoltan Szabo
 */
public class OverlayView extends View {
    private final Paint paint;
    private final List<DrawCallback> callbacks = new LinkedList();
    private List<Recognition> results;
    private List<Integer> colors;
    private float resultsViewHeight;

    public OverlayView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        //
        paint.setStrokeWidth(12.0f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeMiter(100);
        //textsize15
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                20, getResources().getDisplayMetrics()));
        resultsViewHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                112, getResources().getDisplayMetrics());
        colors = ClassAttrProvider.newInstance(context.getAssets()).getColors();
    }

    public void addCallback(final DrawCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public synchronized void onDraw(final Canvas canvas) {
        for (final DrawCallback callback : callbacks) {
            callback.drawCallback(canvas);
        }

        if (results != null) {
            for (int i = 0; i < results.size(); i++) {
                String objClass = results.get(i).getTitle();
                RectF box = reCalcSize(results.get(i).getLocation());
                if(objClass.equals("crosswalk")){
                    String title = "횡단보도 : "
                            + String.format("%.2f", results.get(i).getConfidence());
//                    String title = results.get(i).getTitle() + ":"
//                            + String.format("%.2f", results.get(i).getConfidence());
                    //paint.setColor(colors.get(results.get(i).getId()));
                    paint.setColor(Color.BLUE);
                    canvas.drawRect(box, paint);
                    //canvas.drawText(title, box.left, box.top, paint);
                }else{
                }


            }
        }
    }

    public void setResults(final List<Recognition> results) {
        this.results = results;
        postInvalidate();
    }

    /**
     * Interface defining the callback for client classes.
     */
    public interface DrawCallback {
        void drawCallback(final Canvas canvas);
    }

    private RectF reCalcSize(BoxPosition rect) {
        int padding = 5;
        float overlayViewHeight = this.getHeight() - resultsViewHeight;
        float sizeMultiplier = Math.min((float) this.getWidth() / (float) Config.INPUT_SIZE,
                overlayViewHeight / (float) Config.INPUT_SIZE);

        float offsetX = (this.getWidth() - Config.INPUT_SIZE * sizeMultiplier) / 2;
        float offsetY = (overlayViewHeight - Config.INPUT_SIZE * sizeMultiplier) / 2 + resultsViewHeight;

        float left = Math.max(padding,sizeMultiplier * rect.getLeft() + offsetX);
        float top = Math.max(offsetY + padding, sizeMultiplier * rect.getTop() + offsetY);

        float right = Math.min(rect.getRight() * sizeMultiplier, this.getWidth() - padding);
        float bottom = Math.min(rect.getBottom() * sizeMultiplier + offsetY, this.getHeight() - padding);

        return new RectF(left, top, right, bottom);
    }
}