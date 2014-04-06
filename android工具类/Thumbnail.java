package com.bslee.MeSport.utils;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 功能：创建一张缩略图，对图片进行放大缩小处理，水平和横向排放
 * 
 * @author YUKI Kaoru <k_yuki@menue.co.jp>
 */
public class Thumbnail
{
    public static final int DIRECTION_PORTRAIT = 1;
    public static final int DIRECTION_LANDSCAPE = 2;
    
    private Bitmap mBitmap;
        
    public Thumbnail(Bitmap bitmap)
    {
        mBitmap = bitmap.copy(bitmap.getConfig(), true);
    }
    
    /**
     * Bitmapを読み込んでサムネイルオブジェクトを生成する
     * 
     * @param bitmap
     * @return Thumbnail object
     */
    public static Thumbnail create(Bitmap bitmap)
    {
        return new Thumbnail(bitmap);
    }
    
    /**
     * Uriオブジェクトからファイルを読み込んでサムネイルオブジェクトを生成する
     * 
     * @param context
     * @param uri 画像パス
     * @param sampleWidth サンプリング用の画像幅
     * @return Thumbnail object
     * @throws java.io.IOException 画像ファイルにアクセスできなかったときに投げられる
     * @throws java.io.FileNotFoundException 画像ファイルが存在しなかったときに投げられる
     */
    public static Thumbnail create(Context context, Uri uri, int sampleWidth) throws IOException, FileNotFoundException
    {
        if (uri != null) {
            InputStream is = null;
            BitmapFactory.Options options = null;
            try {
                // retrieve image's infomation
                is = context.getContentResolver().openInputStream(uri);
                options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, options);
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            try {
                BitmapFactory.Options options2 = new BitmapFactory.Options();
                float outWidth = options.outWidth;
                int scale = (int)(outWidth / sampleWidth);
                options2.inSampleSize = scale;
                is = context.getContentResolver().openInputStream(uri);
                return new Thumbnail(BitmapFactory.decodeStream(is, null, options2));
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }
    
    public Bitmap getBitmap()
    {
        return mBitmap;
    }
    
    /**
     * recycle bitmap object
     */
    public void recycle()
    {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
        
    /**
     * 拡大縮小
     * 
     * @param width
     * @param height
     * @return Scaled Bitmap object.
     */
    public Thumbnail scale(int width, int height)
    {
        return scale(width, height, true);
    }
    
    /**
     * 拡大縮小
     * 
     * @param width
     * @param height
     * @param keepAspectRatio アスペクト比を維持するかどうか
     * @return
     */
    public Thumbnail scale(int width, int height, boolean keepAspectRatio)
    {
        Bitmap thumb = null;
        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();
            
        if (width < bitmapWidth || height < bitmapHeight) {
            if (keepAspectRatio) {
                float ratio = 1.f;
                if (width != bitmapWidth) {
                    ratio = (float)width / (float)bitmapWidth;
                } else {
                    ratio = (float)height / (float)bitmapHeight;
                }
                width *= ratio;
                height *= ratio;
            }
            thumb = Bitmap.createScaledBitmap(mBitmap, width, height, true);
        }
        if (thumb != null) {
            recycle();
            mBitmap = thumb;
        }
            
        return this;
    }
        
    /**
     * 中央で切り取ってサムネイルを作成する
     * 
     * @param width
     * @param height
     * @return Cut out center Bitmap object.
     */
    public Thumbnail centerCrop(int width, int height)
    {
        return centerCrop(width, height, Color.TRANSPARENT);
    }
        
    /**
     * 中央で切り取ってサムネイルを作成する
     * 
     * @param width
     * @param height
     * @param backgroundColor Color flag. see Bitmap.Config
     * @return Cut out center Bitmap object.
     */
    public Thumbnail centerCrop(int width, int height, int backgroundColor)
    {
        Bitmap thumb = null;
        Canvas canvas = new Canvas();
        Paint paint = new Paint();
        paint.setDither(false);
        paint.setFilterBitmap(true);
        Rect srcRect = new Rect();
        Rect dstRect = new Rect();
            
        int canvasWidth, canvasHeight;
        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();
            
        canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG));
        canvasWidth = width;
        canvasHeight = height;
            
        if (canvasWidth < bitmapWidth && canvasHeight < bitmapWidth) {
            // キャンバスより大きかったら長い辺をキャンバスサイズに合わせてリサイズする
            float ratio = bitmapWidth < bitmapHeight ? (float)width / (float)bitmapWidth : (float)height / (float)bitmapHeight;
            Bitmap bitmap = Bitmap.createScaledBitmap(mBitmap, (int)(bitmapWidth * ratio), (int)(bitmapHeight * ratio), true);
            recycle();
            bitmapWidth = bitmap.getWidth();
            bitmapHeight = bitmap.getHeight();
            mBitmap = bitmap;
        }
            
        if (bitmapWidth > canvasWidth || bitmapHeight > canvasHeight) {
            // キャンバスからはみ出していたら中央をキャンバスサイズで切り取る
            Config config = (canvasWidth == width && canvasHeight == height &&
                    mBitmap.getConfig() != null) ? mBitmap.getConfig() : Config.ARGB_8888;
            thumb = Bitmap.createBitmap(canvasWidth, canvasHeight, config);
            canvas.setBitmap(thumb);
            canvas.drawColor(backgroundColor);
            if (bitmapWidth > canvasWidth) {
                int left = (bitmapWidth - canvasWidth) / 2;
                srcRect.set(left , 0, left + canvasWidth, bitmapHeight);
                int top = (canvasHeight - bitmapHeight) / 2;
                dstRect.set(0, top, canvasWidth, canvasHeight - top);
            } else {
                int top = (bitmapHeight - canvasHeight) / 2;
                srcRect.set(0, top, bitmapWidth, top + canvasHeight);
                int left = (canvasWidth - bitmapWidth) / 2;
                dstRect.set(left, 0, canvasWidth - left, canvasHeight);
            }
            canvas.drawBitmap(mBitmap, srcRect, dstRect, paint);
        } else {
            // 画像がキャンバスサイズよりも小さかったら中央寄せ
            Config config = Config.ARGB_8888;
            thumb = Bitmap.createBitmap(canvasWidth, canvasHeight, config);
            canvas.setBitmap(thumb);
            canvas.drawColor(backgroundColor);
            int left = (canvasWidth - bitmapWidth) / 2;
            int top = (canvasHeight - bitmapHeight) / 2;
            canvas.drawBitmap(mBitmap, left, top, paint);
        }
        if (thumb != null) {
            recycle();
            mBitmap = thumb;
        }
        return this;
    }
        
    /**
     * 图片水平排放
     * 
     * @return Landscape bitmap object.
     */
    public Thumbnail convertToLandscape()
    {
        return fixDirection(DIRECTION_LANDSCAPE);
    }
        
    /**
     * 图片垂直排放
     * 
     * @return Portait bitmap object.
     */
    public Thumbnail convertToPortrait()
    {
        return fixDirection(DIRECTION_PORTRAIT);
    }
        
    /**
     * 改变图像的方向
     * 
     * @param direction
     * @return
     */
    private Thumbnail fixDirection(int direction)
    {
        boolean shouldRotate = (direction == DIRECTION_LANDSCAPE && mBitmap.getWidth() < mBitmap.getHeight()) ||
                (direction == DIRECTION_PORTRAIT && mBitmap.getHeight() < mBitmap.getWidth());
        Matrix matrix = new Matrix();
        if (shouldRotate) {
            matrix.postRotate(90.f);
        }
        Bitmap rotatedImage = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        recycle();
        mBitmap = rotatedImage;
        return this;
    }
    
    /**
     * 放大缩小画布
     * 
     * @param width
     * @param height
     * @return
     */
    public Thumbnail scaleCanvas(int width, int height)
    {
        return scaleCanvas(width, height, Color.TRANSPARENT);
    }
    
    /**
     * キャンバスを拡大縮小する
     * 
     * @param width
     * @param height
     * @param color
     * @return
     */
    public Thumbnail scaleCanvas(int width, int height, int color)
    {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color, PorterDuff.Mode.CLEAR);
        float left = (float)((width / 2.f) - (mBitmap.getWidth() / 2.f));
        float top = (float)((height / 2.f) - (mBitmap.getHeight() / 2.f));
        canvas.drawBitmap(mBitmap, left, top, null);
        recycle();
        mBitmap = bitmap;
        return this;
    }
}