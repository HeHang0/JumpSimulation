package com.wx.jumpsimulation;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by HeHang on 2018/1/1.
 */

public class ImageProcess {

    private Point nextPoint;
    private Point currentPoint;
    private Bitmap bmp;
    public ImageProcess(String path)
    {
        Init(path);
    }
    private ImageProcess()
    {
    }
    public  Bitmap getBmp(){return bmp;}
    public Point getNextPoint(){
        return nextPoint;
    }
    public Point getCurrentPoint(){
        return currentPoint;
    }

    private void Init(String path) {
        bmp = BitmapFactory.decodeFile(path);
        currentPoint = GetCurrentPoint();
        nextPoint = GetNextPoint();
    }

    private Point GetCurrentPoint() {
        int ColorLeft;
        int ColorRight;
        int ColorCenter = Color.rgb(55, 56, 97)  ;//Color.valueOf(55, 56, 97);
        Point PointLeft = new Point(bmp.getWidth(),bmp.getHeight());
        for (int i = 1400; i > 900; i=i-10)
        {
            for (int j = 100; j < 1000; j=j+10)
            {
                ColorLeft = bmp.getPixel(j, i);
                ColorRight = bmp.getPixel(j+10, i-10);
                if (ColorSub(ColorCenter, ColorLeft) > 1500 && ColorSub(ColorCenter, ColorRight) < 100)
                {
                    if (j + 5 >= PointLeft.x)
                    {
                        break;
                    }
                    PointLeft.x = j + 5;
                    PointLeft.y = i - 16;
                }
            }
        }
        Point p = new Point(PointLeft.x+30, PointLeft.y);
        return p;
    }

    private Point GetNextPoint() {
        int ColorTop;
        int ColorButtom;
        Point PointTop = new Point(bmp.getWidth(), bmp.getHeight());
        for (int i = 350; i < 1100; i++)
        {
            boolean NeedBreak = false;
            for (int j = bmp.getWidth()-1; j > 5; j = j - 5)
            {
                ColorTop = bmp.getPixel(j, i);
                ColorButtom = bmp.getPixel(j, i+1);
                if (ColorSub(ColorTop, ColorButtom) > 800 && (ColorButtom == Color.rgb(72, 72, 72) || ColorSub(Color.rgb(52, 53, 60), ColorButtom) > 3000))
                {
                    PointTop.x = j;
                    PointTop.y = i + 1;
                    NeedBreak = true;
                    break;
                }
            }
            if (NeedBreak)
            {
                break;
            }
        }
        ColorTop = bmp.getPixel(PointTop.x, PointTop.y+5);
        Point PointButtom = new Point(PointTop.x, currentPoint.y);
        for (int i = 400; i > 5; i--)
        {
            ColorButtom = bmp.getPixel(PointTop.x, PointTop.y+i);
            if (ColorSub(ColorTop, ColorButtom) < 88)
            {
                PointButtom.y = PointTop.y + i;
                break;
            }
        }

        return new Point(PointTop.x, (PointButtom.y + PointTop.y) / 2);
    }

    private double ColorSub(int color1, int color2)
    {
        return Math.pow((Color.red(color1) - Color.red(color2)), 2) + Math.pow((Color.green(color1) - Color.green(color2)), 2) + Math.pow((Color.blue(color1) - Color.blue(color2)), 2);
    }
}
