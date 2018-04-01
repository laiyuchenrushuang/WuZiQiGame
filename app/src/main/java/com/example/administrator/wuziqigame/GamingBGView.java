package com.example.administrator.wuziqigame;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 *onSaveInstanceState and onRestoreInstanceState的储存数据的使用
 * bitmap's createScaledBitmap use
 * canvas's drawBitmap的准确定位
 * 五指棋结束游戏的规则，遍历计数
 * point类的使用
 * 棋盘的规定在onSizeChanged中去绘制，图片chess不要写死，按比例写
 * 自定义view 中onTouchEvent事件的监听
 * 自定义view，构造方法全部写出来，这里是view的开始
 * view的重绘制的方法invalidate();
 * menu的使用
 * 重玩机制和悔棋机制
 * 音频的加载SoundPool的使用 wav文件raw下
 * Created by Administrator on 2018/3/31.
 */

public class GamingBGView extends View {
    private int mPaintWidth;
    private float mLineHeight;
    private static final int NUMBER_LINE = 10;
    private static final int MAX_STEPBACK = 4;

    private Paint mPaint;
    private Bitmap mWhiteChess;
    private Bitmap mBlackChess;

    //棋子占格子的比例为3/4保留1/4的间隙
    private float  mRatioChess = 3*1.0f/4;

    boolean mIsBlackGo = false;

    //game over
    boolean isGameOver = false;
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();
    private int FIVE_IN_LINE = 5;
    private int mCountStepBack = 0;

    private SoundPool soundPool;//声明一个SoundPool
    private int soundID;//创建某个声音对应的音频ID


    //测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getSize(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getSize(heightMeasureSpec);

        int width = Math.min(widthSize,heightSize);
        if (widthMode==MeasureSpec.UNSPECIFIED) {
            width = heightMode;
        }else if (heightMode==MeasureSpec.UNSPECIFIED) {
            width = widthMode;
        }

        //因为设置的是正方形h=w
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPaintWidth = w;
        mLineHeight = mPaintWidth *1.0f/NUMBER_LINE;

        int chessSize = (int) (mLineHeight*mRatioChess);
        mWhiteChess = Bitmap.createScaledBitmap(mWhiteChess,chessSize,chessSize,false);
        mBlackChess = Bitmap.createScaledBitmap(mBlackChess,chessSize,chessSize,false);
    }

    private static final  String INSTANCE = "instance";
    private static final  String IS_GAMEOVER = "instance_isGameOver";
    private static final  String WHITE_ARRAY = "instance_whiteArray";
    private static final  String BLACK_ARRAY = "instance_blackArray";

    //切换横竖屏
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(IS_GAMEOVER,isGameOver);
        bundle.putParcelableArrayList(WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(BLACK_ARRAY,mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            isGameOver = bundle.getBoolean(IS_GAMEOVER);
            mWhiteArray= bundle.getParcelableArrayList(WHITE_ARRAY);
            mBlackArray= bundle.getParcelableArrayList(BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawChess(canvas);
        checkGameOver();
    }

    private void checkGameOver() {
      boolean winWhite = checkFiveInLine(mWhiteArray);
      boolean winBlack = checkFiveInLine(mBlackArray);
        if(winWhite || winBlack){
            isGameOver = true;
            if (winBlack) {
                Toast.makeText(getContext(),"黑棋胜利",Toast.LENGTH_LONG).show();
                winBlack =false;
            }else {
                Toast.makeText(getContext(),"白棋胜利",Toast.LENGTH_LONG).show();
                winWhite= false;
            }
        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for (Point p :points) {
            int x= p.x;
            int y =p.y;
            //lineHorizontal
            boolean win = checkLineHorizontal(x,y,points);
            if (win) return true;
             win = checkLineVertical(x,y,points);
            if (win) return true;
             win = checkLineLeftBias(x,y,points);
            if (win) return true;
             win = checkLineRightBias(x,y,points);
            if (win) return true;

        }
        return  false;
    }

    private boolean checkLineHorizontal(int x, int y, List<Point> points) {
        int count = 1;

        for (int i =1;i<FIVE_IN_LINE;i++) {
            if (points.contains(new Point (x-i,y))) {
                count++;
            }else{
                break;
            }
        }
        if (count ==FIVE_IN_LINE){return true;}
        for (int i =1;i<FIVE_IN_LINE;i++) {
            if (points.contains(new Point (x+i,y))) {
                count++;
            }else{
                break;
            }
        }
        if (count ==FIVE_IN_LINE){return true;}

        return  false;
    }

    private boolean checkLineVertical(int x, int y, List<Point> points) {
        int count = 1;

        for (int i =1;i<FIVE_IN_LINE;i++) {
            if (points.contains(new Point (x,y-i))) {
                count++;
            }else{
                break;
            }
        }
        if (count ==FIVE_IN_LINE){return true;}
        for (int i =1;i<FIVE_IN_LINE;i++) {
            if (points.contains(new Point (x,y+i))) {
                count++;
            }else{
                break;
            }
        }
        if (count ==FIVE_IN_LINE){return true;}

        return  false;
    }

    private boolean checkLineLeftBias(int x, int y, List<Point> points) {
        int count = 1;

        for (int i =1;i<FIVE_IN_LINE;i++) {
            if (points.contains(new Point (x-i,y+i))) {
                count++;
            }else{
                break;
            }
        }
        if (count ==FIVE_IN_LINE){return true;}
        for (int i =1;i<FIVE_IN_LINE;i++) {
            if (points.contains(new Point (x+i,y-i))) {
                count++;
            }else{
                break;
            }
        }
        if (count ==FIVE_IN_LINE){return true;}

        return  false;
    }

    private boolean checkLineRightBias(int x, int y, List<Point> points) {
        int count = 1;

        for (int i =1;i<FIVE_IN_LINE;i++) {
            if (points.contains(new Point (x+i,y+i))) {
                count++;
            }else{
                break;
            }
        }
        if (count ==FIVE_IN_LINE){return true;}
        for (int i =1;i<FIVE_IN_LINE;i++) {
            if (points.contains(new Point (x-i,y-i))) {
                count++;
            }else{
                break;
            }
        }
        if (count ==FIVE_IN_LINE){return true;}

        return  false;
    }

    private void drawChess(Canvas canvas) {
        for (int i=0;i<mWhiteArray.size();i++){
            canvas.drawBitmap(mWhiteChess,
                    (mWhiteArray.get(i).x+(1-mRatioChess)/2)*mLineHeight,
                    (mWhiteArray.get(i).y+(1-mRatioChess)/2)*mLineHeight,mPaint);
        }
        for (int i=0;i<mBlackArray.size();i++){
            canvas.drawBitmap(mBlackChess,(mBlackArray.get(i).x+(1-mRatioChess)/2)*mLineHeight,
                    (mBlackArray.get(i).y+(1-mRatioChess)/2)*mLineHeight,mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                if (isGameOver) {
                    return false;
                }
                playSound();
                int envenX = (int) event.getX();
                int envenY = (int) event.getY();
                Point p = new Point((int)(envenX/mLineHeight),(int)(envenY/mLineHeight));
                //同一位置不能再次点击
                if (mBlackArray.contains(p) || mWhiteArray.contains(p)) {
                    return false;
                }
                if (mIsBlackGo) {
                    mWhiteArray.add(p);
                }else{
                    mBlackArray.add(p);
                }
                invalidate();
                mIsBlackGo = !mIsBlackGo;
        }
        return true;
    }

    private void drawBoard(Canvas canvas) {
        int w = mPaintWidth;
        float lineHeight = mLineHeight;
        //横线
        for (int i =0;i<NUMBER_LINE;i++){
            int startX = (int) (lineHeight/2);
            int endX = (int) (w - lineHeight/2);
            int y = (int) ((0.5+i)*lineHeight);
            canvas.drawLine(startX,y,endX,y,mPaint);
        }
        int h = mPaintWidth;
        //竖线
        for (int i =0;i<NUMBER_LINE;i++){
            int startY = (int) (lineHeight/2);
            int endY = (int) (w - lineHeight/2);
            int x = (int) ((0.5+i)*lineHeight);
            canvas.drawLine(x,startY,x,endY,mPaint);
        }

    }
    //重玩
    public void reStart (){
        isGameOver = false;
        mWhiteArray.clear();
        mBlackArray.clear();
        invalidate();
    }

    //悔棋不过3次
    public void stepback (){
        if (mCountStepBack < MAX_STEPBACK) {
            mWhiteArray.remove(mWhiteArray.get(mWhiteArray.size() - 1));
            mBlackArray.remove(mBlackArray.get(mBlackArray.size() - 1));
            invalidate();
            mCountStepBack ++;
        }else {
            Toast.makeText(getContext(),"君子还是落定尘埃吧",Toast.LENGTH_SHORT).show();
        }
    }

    //播放音效
    private void playSound() {
        soundPool.play(
                soundID,
                0.1f,   //左耳道音量【0~1】
                0.5f,   //右耳道音量【0~1】
                0,     //播放优先级【0表示最低优先级】
                0,     //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1     //播放速度【1是正常，范围从0~2】
        );
    }

    //教练说要全部写出来
    public GamingBGView(Context context) {
        super(context);
    }

    public GamingBGView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //setBackgroundColor(0x44ff0000);
        init();
    }

    //初始化
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhiteChess = BitmapFactory.decodeResource(getResources(),R.mipmap.white_chess);
        mBlackChess = BitmapFactory.decodeResource(getResources(),R.mipmap.black_chess);
        //音频资源的加载
        soundPool = new SoundPool.Builder().build();
        soundID = soundPool.load(getContext(), R.raw.wuziqi, 1);
    }

    public GamingBGView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GamingBGView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
