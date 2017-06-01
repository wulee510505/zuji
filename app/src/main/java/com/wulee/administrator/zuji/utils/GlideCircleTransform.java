package com.wulee.administrator.zuji.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by wulee on 2015/7/29.
 */

public class GlideCircleTransform extends BitmapTransformation {
    private int bColoresId = 0;  //边框颜色资源Id
    private Context mContext;

    public GlideCircleTransform(Context context, int bColorId) {
        super(context);
        this.bColoresId = bColorId;
        this.mContext = context;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        // TODO this could be acquired from the pool too
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        if(bColoresId>0)
            setCircleBorder(result,bColoresId,r);
        return result;
    }

    /**
     * Bitmap 画圆形边框
     * @param bm 			Bitmap
     * @param bColorId   	边框颜色
     * @param br	圆形边框半径
     */
    private void setCircleBorder(Bitmap bm, int bColorId, float br){
        Canvas canvas =new Canvas(bm);
        Rect rect = canvas.getClipBounds();
        Paint paint = new Paint();
        RectF rectF=new RectF(rect);
        paint.setColor(mContext.getResources().getColor(bColorId));  //设置边框颜色
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        paint.setStrokeWidth(Resources.getSystem().getDisplayMetrics().density * 5);   //设置边框宽度5dp
        canvas.drawRoundRect(rectF, br, br, paint);
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
