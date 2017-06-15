package com.wulee.administrator.zuji.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.wulee.administrator.zuji.R;

import java.util.List;

/**
 * Created by liuwan on 2016/10/4.
 */
public class CustomBarChart extends View {

    // 坐标单位
    private String[] xLabel;
    private String[] yLabel;
    // 曲线数据
    private List<float[]> dataList;
    private List<Integer> colorList;
    // 默认边距
    private int margin = 40;
    // 距离左边偏移量
    private int marginX = 30;
    // 原点坐标
    private int xPoint;
    private int yPoint;
    // X,Y轴的单位长度
    private int xScale;
    private int yScale;
    // 画笔
    private Paint paintAxes;
    private Paint paintCoordinate;
    private Paint paintRectF;
    private Paint paintValue;

    public CustomBarChart(Context context, String[] xLabel, String[] yLabel,
                          List<float[]> dataList, List<Integer> colorList) {
        super(context);
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.dataList = dataList;
        this.colorList = colorList;
    }

    public CustomBarChart(Context context) {
        super(context);
    }

    /**
     * 初始化数据值和画笔
     */
    public void init() {
        xPoint = margin + marginX;
        yPoint = this.getHeight() - margin;
        xScale = (this.getWidth() - 2 * margin - marginX) / (xLabel.length - 1);
        yScale = (this.getHeight() - 2 * margin) / (yLabel.length - 1);

        paintAxes = new Paint();
        paintAxes.setStyle(Paint.Style.STROKE);
        paintAxes.setAntiAlias(true);
        paintAxes.setDither(true);
        paintAxes.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        paintAxes.setStrokeWidth(4);

        paintCoordinate = new Paint();
        paintCoordinate.setStyle(Paint.Style.STROKE);
        paintCoordinate.setDither(true);
        paintCoordinate.setAntiAlias(true);
        paintCoordinate.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        paintCoordinate.setTextSize(15);

        paintRectF = new Paint();
        paintRectF.setStyle(Paint.Style.FILL);
        paintRectF.setDither(true);
        paintRectF.setAntiAlias(true);
        paintRectF.setStrokeWidth(1);

        paintValue = new Paint();
        paintValue.setStyle(Paint.Style.STROKE);
        paintValue.setAntiAlias(true);
        paintValue.setDither(true);
        paintValue.setTextAlign(Paint.Align.CENTER);
        paintValue.setTextSize(13);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(ContextCompat.getColor(getContext(), R.color.color_white));
        init();
        drawAxesLine(canvas, paintAxes);
        drawCoordinate(canvas, paintCoordinate);
        if (dataList.size() == 1) {
            drawBar(canvas, paintRectF, dataList.get(0), colorList);
            drawValue(canvas, paintValue, dataList.get(0), colorList.get(2));
        }
    }

    /**
     * 绘制坐标轴
     */
    private void drawAxesLine(Canvas canvas, Paint paint) {
        // X
        canvas.drawLine(xPoint, yPoint, this.getWidth() - margin / 6, yPoint, paint);
        canvas.drawLine(this.getWidth() - margin / 6, yPoint, this.getWidth() - margin / 2, yPoint - margin / 3, paint);
        canvas.drawLine(this.getWidth() - margin / 6, yPoint, this.getWidth() - margin / 2, yPoint + margin / 3, paint);

        // Y
        canvas.drawLine(xPoint, yPoint, xPoint, margin / 6, paint);
        canvas.drawLine(xPoint, margin / 6, xPoint - margin / 3, margin / 2, paint);
        canvas.drawLine(xPoint, margin / 6, xPoint + margin / 3, margin / 2, paint);
    }

    /**
     * 绘制刻度
     */
    private void drawCoordinate(Canvas canvas, Paint paint) {
        // X轴坐标
        for (int i = 0; i <= (xLabel.length - 1); i++) {
            paint.setTextAlign(Paint.Align.CENTER);
            int startX = xPoint + i * xScale;
            canvas.drawText(xLabel[i], startX, this.getHeight() - margin / 6, paint);
        }

        // Y轴坐标
        for (int i = 0; i <= (yLabel.length - 1); i++) {
            paint.setTextAlign(Paint.Align.LEFT);
            int startY = yPoint - i * yScale;
            int offsetX;
            switch (yLabel[i].length()) {
                case 1:
                    offsetX = 28;
                    break;

                case 2:
                    offsetX = 20;
                    break;

                case 3:
                    offsetX = 12;
                    break;

                case 4:
                    offsetX = 5;
                    break;

                default:
                    offsetX = 0;
                    break;
            }
            int offsetY;
            if (i == 0) {
                offsetY = 0;
            } else {
                offsetY = margin / 5;
            }
            canvas.drawText(yLabel[i], margin / 4 + offsetX, startY + offsetY, paint);
        }
    }

    /**
     * 绘制单柱形
     */
    private void drawBar(Canvas canvas, Paint paint, float data[], List<Integer> colorList) {
        for (int i = 1; i <= (xLabel.length - 1); i++) {
            int startX = xPoint + i * xScale;
            RectF rect = new RectF(startX - 10, toY(data[i - 1]), startX + 10, this.getHeight() - margin - 2);
            if (i % 2 == 1) {
                paint.setColor(ContextCompat.getColor(getContext(), colorList.get(0)));
            } else {
                paint.setColor(ContextCompat.getColor(getContext(), colorList.get(1)));
            }
            canvas.drawRect(rect, paint);
        }
    }


    /**
     * 绘制数值
     */
    private void drawValue(Canvas canvas, Paint paint, float data[], int color) {
        paint.setColor(ContextCompat.getColor(getContext(), color));
        for (int i = 1; i <= (xLabel.length - 1); i++) {
            if(data[i-1] > 15000){
                canvas.drawText(">15000步", xPoint + i * xScale, toY(15500), paintValue);
            }else{
                canvas.drawText(data[i - 1] + "步", xPoint + i * xScale, toY(data[i - 1]) - 5, paintValue);
            }
        }
    }

    /**
     * 数据按比例转坐标
     */
    private float toY(float num) {
        float y;
        try {
            float a =  num / 1000;
            y = yPoint - a * yScale;
        } catch (Exception e) {
            return 0;
        }
        return y;
    }

}
