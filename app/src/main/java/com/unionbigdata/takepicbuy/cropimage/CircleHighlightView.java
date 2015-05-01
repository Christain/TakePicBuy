package com.unionbigdata.takepicbuy.cropimage;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.Region;
import android.view.View;

/**
 * @author JianTao.Young
 * @time: 2015-1-28 ����5:56:28
 */
public class CircleHighlightView extends HighlightView {

    public CircleHighlightView(View context) {
        super( context);
    }

    @Override
    protected void draw(Canvas canvas) {
        canvas.save();
        Path path = new Path();
        outlinePaint.setStrokeWidth( outlineWidth);
        if(!hasFocus()) {//û�����ǣ�ֱ�ӻ�һ����ɫ�ľ��ο�
            outlinePaint.setColor( Color.BLACK);
            canvas.drawRect( drawRect, outlinePaint);
        }
        else {
            Rect viewDrawingRect = new Rect();
            viewContext.getDrawingRect( viewDrawingRect);

            //�Ѳü���drawRect�����Բ�İ뾶
            float radius = (drawRect.right - drawRect.left) / 2;
            //���һ��Բ��
            path.addCircle( drawRect.left + radius, drawRect.top + radius, radius, Direction.CW);
            outlinePaint.setColor( highlightColor);

            //�ü�������path֮���������outsidePaint���
            canvas.clipPath( path, Region.Op.DIFFERENCE);
            canvas.drawRect( viewDrawingRect, outsidePaint);

            canvas.restore();
            //����Բ�ϸ����ߣ�����outlinePaint�����Paint.Style.STROKE����ʾֻ���Ƽ���ͼ�ε�������
            canvas.drawPath( path, outlinePaint);
            
            //��modifyModeΪgrowʱ������handles,Ҳ�������ĸ�СԲ
            if(handleMode == HandleMode.Always || (handleMode == HandleMode.Changing && modifyMode == ModifyMode.Grow)) {
                drawHandles( canvas);
            }
        }
    }

    // Determines which edges are hit by touching at (x, y)
    @Override
    public int getHit(float x, float y) {
        return getHitOnCircle( x, y);
    }

    @Override
    void handleMotion(int edge, float dx, float dy) {
        Rect r = computeLayout();
        if(edge == MOVE) {
            // Convert to image space before sending to moveBy()
            moveBy( dx * (cropRect.width() / r.width()), dy * (cropRect.height() / r.height()));
        }
        else {
            if(((GROW_LEFT_EDGE | GROW_RIGHT_EDGE) & edge) == 0) {
                dx = 0;
            }

            if(((GROW_TOP_EDGE | GROW_BOTTOM_EDGE) & edge) == 0) {
                dy = 0;
            }
            // �Ǹ�����仯���ȡ�����ο�;Ĭ�ϲο�dx
            if(Math.abs( dx) < Math.abs( dy)) {
                dx = 0.0f;// dx����Ϊ0��growBy�ͻ���dy�ο�������1:1�����dx
            }
            float xDelta = dx * (cropRect.width() / r.width());
            float yDelta = dy * (cropRect.height() / r.height());
            growBy( (((edge & GROW_LEFT_EDGE) != 0) ? -1 : 1) * xDelta, (((edge & GROW_TOP_EDGE) != 0) ? -1 : 1) * yDelta);
        }
    }

    /**
     * ���x,y��꣬��������Բ�Ĺ�ϵ��Բ�ϡ�Բ�ڡ�Բ�⣩
     * @param x
     * @param y
     * @return
     */
    private int getHitOnCircle(float x, float y) {
        Rect r = computeLayout();
        int retval = GROW_NONE;
        final float hysteresis = 20F;
        int radius = (r.right - r.left) / 2;

        int centerX = r.left + radius;
        int centerY = r.top + radius;
        
        //�жϴ���λ���Ƿ���Բ��
        float ret = (x - centerX) * (x - centerX) + (y - centerY) * (y - centerY);
        double rRadius = Math.sqrt( ret);
        double gap = Math.abs( rRadius - radius);

        if(gap <= hysteresis) {// Բ�ϡ����������Ǽ̳���HighlightView�����ƾ��ο�ģ������?����ģ�ⷵ�����������£���Ǵ�Բ�ϣ��ײ���á���Ҳ�����Զ��塣
            if(x < centerX) {// left
                retval |= GROW_LEFT_EDGE;
            }
            else {
                retval |= GROW_RIGHT_EDGE;
            }

            if(y < centerY) {// up
                retval |= GROW_TOP_EDGE;
            }
            else {
                retval |= GROW_BOTTOM_EDGE;
            }
        }
        else if(rRadius > radius) {// outside
            retval = GROW_NONE;
        }
        else if(rRadius < radius) {// inside��Բ�ھ�ִ��move
            retval = MOVE;
        }

        return retval;
    }

}
