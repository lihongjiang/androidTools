package com.bslee.MeSport.utils;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import com.bslee.MeSport.global.AppConstant;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
/**
 * Created with IntelliJ IDEA.
 * User: max
 * Date: 13-10-28
 * Time: 下午7:37
 * 描述：图片处理类.
 */

public class ImageUtil {

    /** The tag. */
    private static String TAG = "ImageUtil";

    /** The Constant D. */
    private static final boolean D = AppData.DEBUG;

    /**
     * 直接获取互联网上的图片.
     * @param imageUrl 要下载文件的网络地址
     * @param type 图片的处理类型（剪切或者缩放到指定大小，参考AbConstant类）
     * @param newWidth 新图片的宽
     * @param newHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getBitmapFormURL(String imageUrl,int type,int newWidth,int newHeight){
        Bitmap bm = null;
        URLConnection con = null;
        InputStream is = null;
        try {
            URL url = new URL(imageUrl);
            con = url.openConnection();
            con.setDoInput(true);
            con.connect();
            is = con.getInputStream();
            //获取资源图片
            Bitmap wholeBm =  BitmapFactory.decodeStream(is,null,null);
            if(type== AppConstant.CUTIMG){
                bm = ImageUtil.cutImg(wholeBm, newWidth, newHeight);
            }else if(type== AppConstant.SCALEIMG){
                bm = ImageUtil.scaleImg(wholeBm, newWidth, newHeight);
            }
        } catch (Exception e) {
            if(D) Log.d(TAG, ""+e.getMessage());
        }finally{
            try {
                if(is!=null){
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bm;
    }

    /**
     * 描述：缩放图片.压缩
     * @param file File对象
     * @param newWidth 新图片的宽
     * @param newHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap scaleImg(File file,int newWidth, int newHeight){
        Bitmap resizeBmp = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        //设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(),opts);
        if(newWidth!=-1 && newHeight!=-1){
            //inSampleSize=2表示图片宽高都为原来的二分之一，即图片为原来的四分之一
            //缩放可以将像素点打薄
            int srcWidth = opts.outWidth;  // 获取图片的原始宽度
            int srcHeight = opts.outHeight;// 获取图片原始高度
            int destWidth = 0;
            int destHeight = 0;
            // 缩放的比例
            double ratio = 0.0;
            if (srcWidth < newWidth || srcHeight < newHeight) {
                ratio = 0.0;
                destWidth = srcWidth;
                destHeight = srcHeight;
                // 按比例计算缩放后的图片大小
            } else if (srcWidth > srcHeight) {
                ratio = (double) srcWidth / newWidth;
                destWidth = newWidth;
                destHeight = (int) (srcHeight / ratio);
            } else {
                ratio = (double) srcHeight / newHeight;
                destHeight = newHeight;
                destWidth = (int) (srcWidth / ratio);
            }
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            opts.inSampleSize = (int) ratio + 1;
            // 设置大小
            opts.outHeight = destHeight;
            opts.outWidth = destWidth;
        }else{
            opts.inSampleSize = 1;
        }
        //创建内存
        opts.inJustDecodeBounds = false;
        //使图片不抖动
        opts.inDither = false;
        resizeBmp = BitmapFactory.decodeFile(file.getPath(),opts);
        return resizeBmp;
    }

    /**
     * 描述：缩放图片,不压缩的缩放.
     *
     * @param bitmap the bitmap
     * @param newWidth 新图片的宽
     * @param newHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap scaleImg(Bitmap bitmap, int newWidth, int newHeight) {
        if(bitmap == null){
            return null;
        }
        if(newHeight<=0 || newWidth<=0){
            return bitmap;
        }
        // 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if(width <= 0 || height <= 0){
            return null;
        }
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        //得到新的图片
        Bitmap newBm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,true);
        return newBm;
    }

    /**
     * 描述：缩放图片.
     *
     * @param bitmap the bitmap
     * @param scale 比例
     * @return Bitmap 新图片
     */
    public static Bitmap scaleImg(Bitmap bitmap,int scale){
        Bitmap resizeBmp = null;
        try {
            //获取Bitmap资源的宽和高
            int bmpW = bitmap.getWidth();
            int bmpH = bitmap.getHeight();
            //注意这个Matirx是android.graphics底下的那个
            Matrix mt = new Matrix();
            //设置缩放系数，分别为原来的0.8和0.8
            mt.postScale(scale, scale);
            resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpW, bmpH, mt, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resizeBmp;
    }

    /**
     * 描述：裁剪图片.
     * @param file  File对象
     * @param newWidth 新图片的宽
     * @param newHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap cutImg(File file,int newWidth, int newHeight){
        Bitmap newBitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        //设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(),opts);
        if(newWidth!=-1 && newHeight!=-1){
            //inSampleSize=2表示图片宽高都为原来的二分之一，即图片为原来的四分之一
            //缩放可以将像素点打薄,裁剪前将图片缩放一些
            int srcWidth = opts.outWidth;  // 获取图片的原始宽度
            int srcHeight = opts.outHeight;// 获取图片原始高度
            int destWidth = 0;
            int destHeight = 0;
            int cutSrcWidth = newWidth*2;
            int cutSrcHeight = newHeight*2;

            // 缩放的比例
            double ratio = 0.0;
            if (srcWidth < cutSrcWidth || srcHeight < cutSrcHeight) {
                ratio = 0.0;
                destWidth = srcWidth;
                destHeight = srcHeight;
                // 按比例计算缩放后的图片大小
            } else if (srcWidth > srcHeight) {
                ratio = (double) srcWidth / cutSrcWidth;
                destWidth = cutSrcWidth;
                destHeight = (int) (srcHeight / ratio);
            } else {
                ratio = (double) srcHeight / cutSrcHeight;
                destHeight = cutSrcHeight;
                destWidth = (int) (srcWidth / ratio);
            }
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            opts.inSampleSize = (int) ratio + 1;
            // 设置大小
            opts.outHeight = destHeight;
            opts.outWidth = destWidth;
        }else{
            opts.inSampleSize = 1;
        }
        //创建内存
        opts.inJustDecodeBounds = false;
        //使图片不抖动
        opts.inDither = false;
        Bitmap resizeBmp = BitmapFactory.decodeFile(file.getPath(),opts);
        if(resizeBmp!=null){
            newBitmap = cutImg(resizeBmp,newWidth,newHeight);
        }
        if(newBitmap!=null){
            return newBitmap;
        }else{
            return resizeBmp;
        }
    }

    /**
     * 描述：裁剪图片.
     *
     * @param bitmap the bitmap
     * @param newWidth 新图片的宽
     * @param newHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap cutImg(Bitmap bitmap, int newWidth, int newHeight) {
        if(bitmap == null){
            return null;
        }
        Bitmap newBitmap = null;
        if(newHeight <= 0 || newWidth <= 0){
            return bitmap;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if(width <= 0 || height <= 0){
            return null;
        }
        int offsetX = 0;
        int offsetY = 0;

        if(width>newWidth){
            offsetX = (width-newWidth)/2;
        }
        if(height>newHeight){
            offsetY = (height-newHeight)/2;
        }

        newBitmap = Bitmap.createBitmap(bitmap,offsetX,offsetY,newWidth,newHeight);
        return newBitmap;
    }

    /**
     * Drawable转Bitmap.
     * @param drawable 要转化的Drawable
     * @return Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888: Config.RGB_565
        );
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Drawable对象转换Bitmap对象.
     * @param bitmap 要转化的Bitmap对象
     * @return Drawable 转化完成的Drawable对象
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        BitmapDrawable mBitmapDrawable = null;
        try {
            if(bitmap==null){
                return null;
            }
            mBitmapDrawable = new BitmapDrawable(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmapDrawable;
    }

    /**
     * 将Bitmap转换为byte[].
     *
     * @param bitmap the bitmap
     * @param mCompressFormat 图片格式 Bitmap.CompressFormat.JPEG,CompressFormat.PNG
     * @param needRecycle 是否需要回收
     * @return byte[] 图片的byte[]
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap,Bitmap.CompressFormat mCompressFormat,final boolean needRecycle){
        byte[] result = null;
        ByteArrayOutputStream output = null;
        try {
            output = new ByteArrayOutputStream();
            bitmap.compress(mCompressFormat, 100, output);
            result = output.toByteArray();
            if (needRecycle) {
                bitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(output!=null){
                try {
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 描述：将byte[]转换为Bitmap.
     * @param b 图片格式的byte[]数组
     * @return bitmap 得到的Bitmap
     */
    public static  Bitmap bytes2Bitmap(byte[] b){
        Bitmap bitmap = null;
        try {
            if(b.length!=0){
                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将ImageView转换为Bitmap.
     * @param view 要转换为bitmap的View
     * @return byte[] 图片的byte[]
     */
    public static Bitmap imageView2Bitmap(ImageView view){
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * 将View转换为Drawable.需要最上层布局为Linearlayout
     * @param view 要转换为Drawable的View
     * @return BitmapDrawable Drawable
     */
    public static Drawable view2Drawable(View view){
        BitmapDrawable mBitmapDrawable = null;
        try {
            Bitmap newbmp = view2Bitmap(view);
            if(newbmp!=null){
                mBitmapDrawable = new BitmapDrawable(newbmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmapDrawable;
    }

    /**
     * 将View转换为Bitmap.需要最上层布局为Linearlayout
     * @param view 要转换为bitmap的View
     * @return byte[] 图片的byte[]
     */
    public static Bitmap view2Bitmap(View view){
        Bitmap bitmap = null;
        try {
            if (view != null) {
                view.setDrawingCacheEnabled(true);
                view.measure(
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                view.buildDrawingCache();
                bitmap = view.getDrawingCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将View转换为byte[].
     *
     * @param view 要转换为byte[]的View
     * @param compressFormat the compress format
     * @return byte[] View图片的byte[]
     */
    public static byte[] view2Bytes(View view,Bitmap.CompressFormat compressFormat){
        byte[] b = null;
        try {
            Bitmap bitmap = ImageUtil.view2Bitmap(view);
            b = ImageUtil.bitmap2Bytes(bitmap, compressFormat, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 描述：旋转Bitmap为一定的角度.
     *
     * @param bitmap the bitmap
     * @param degrees the degrees
     * @return the bitmap
     */
    public static Bitmap rotateBitmap(Bitmap bitmap,float degrees){
        Bitmap mBitmap = null;
        try {
            Matrix m = new Matrix();
            m.setRotate(degrees%360);
            mBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),m,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    /**
     * 描述：旋转Bitmap为一定的角度并四周暗化处理.
     *
     * @param bitmap the bitmap
     * @param degrees the degrees
     * @return the bitmap
     */
    public static Bitmap rotateBitmapTranslate(Bitmap bitmap,float degrees){
        Bitmap mBitmap = null;
        int width;
        int height;
        try {
            Matrix matrix = new Matrix();
            if ((degrees / 90) % 2!= 0) {
                width =  bitmap.getWidth();
                height =  bitmap.getHeight();
            } else {
                width = bitmap.getHeight();
                height =  bitmap.getWidth();
            }
            int cx = width / 2;
            int cy = height/ 2;
            matrix.preTranslate(-cx, -cy);
            matrix.postRotate(degrees);
            matrix.postTranslate(cx, cy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    /**
     * 转换图片转换成圆形.
     *
     * @param bitmap 传入Bitmap对象
     * @return the bitmap
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        if(bitmap == null){
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**
     * 释放Bitmap对象.
     * @param bitmap 要释放的Bitmap
     */
    public static void releaseBitmap(Bitmap bitmap){
        if(bitmap!=null){
            try {
                if(!bitmap.isRecycled()){
                    if(D) Log.d(TAG, "Bitmap释放"+bitmap.toString());
                    bitmap.recycle();
                }
            } catch (Exception e) {
            }
            bitmap = null;
        }
    }

    /**
     * 释放Bitmap数组.
     * @param bitmaps 要释放的Bitmap数组
     */
    public static void releaseBitmapArray(Bitmap[] bitmaps){
        if(bitmaps!=null){
            try {
                for(Bitmap bitmap:bitmaps){
                    if(bitmap!=null && !bitmap.isRecycled()){
                        if(D) Log.d(TAG, "Bitmap释放"+bitmap.toString());
                        bitmap.recycle();
                    }
                }
            } catch (Exception e) {
            }
        }
    }

}
