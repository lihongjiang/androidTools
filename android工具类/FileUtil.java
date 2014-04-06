package com.bslee.MeSport.utils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import com.bslee.MeSport.global.AppConstant;
import com.bslee.MeSport.global.AppData;
import com.bslee.MeSport.global.AppException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：文件操作类.
 * @author max
 * @date 2013-10-28
 */
public class FileUtil {

    /** The tag. */
    private static String TAG = "FileUtil";

    /** The Constant D. */
    private static final boolean D = AppData.DEBUG;

    /** 默认下载文件地址. */
    public static  String downPathRootDir = File.separator + "download" + File.separator;

    /** 默认下载图片文件地址. */
    public static  String downPathImageDir = File.separator + "download"
            + File.separator + "cache_images" + File.separator;

    /** 默认下载文件地址. */
    public static  String downPathFileDir = File.separator + "download"
            + File.separator + "cache_files" + File.separator;

    /**
     * 下载网络文件到SD卡中.如果SD中存在同名文件将不再下载
     * @param url 要下载文件的网络地址
     * @return 下载好的本地文件地址
     */
    public static String downFileToSD(String url){
        InputStream in = null;
        FileOutputStream fileOutputStream = null;
        HttpURLConnection con = null;
        String downFilePath = null;
        try {
            if(!isCanUseSD()){
                return null;
            }
            File path = Environment.getExternalStorageDirectory();
            File fileDirectory = new File(path.getAbsolutePath() + downPathImageDir);
            if(!fileDirectory.exists()){
                fileDirectory.mkdirs();
            }
            File f = new File(fileDirectory,getFileNameFromUrl(url));
            if(!f.exists()){
                f.createNewFile();
            }else{
                //文件已经存在
                if(f.length()!=0){
                    return f.getPath();
                }
            }
            downFilePath = f.getPath();
            URL mUrl = new URL(url);
            con = (HttpURLConnection)mUrl.openConnection();
            con.connect();
            in = con.getInputStream();
            fileOutputStream = new FileOutputStream(f);
            byte[] b = new byte[1024];
            int temp = 0;
            while((temp=in.read(b))!=-1){
                fileOutputStream.write(b, 0, temp);
            }
        }catch(Exception e){
            if(D)Log.d(TAG, ""+e.getMessage());
            return null;
        }finally{
            try {
                if(in!=null){
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if(fileOutputStream!=null){
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if(con!=null){
                    con.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return downFilePath;
    }

    /**
     * 描述：通过文件的网络地址从SD卡中读取图片，如果SD中没有则自动下载并保存.
     * @param url 文件的网络地址
     * @param type 图片的处理类型（剪切或者缩放到指定大小，参考AbConstant类）
     * @param newWidth 新图片的宽
     * @param newHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getBitmapFromSDCache(String url,int type,int newWidth, int newHeight){
        Bitmap bit = null;
        try {
            //SD卡是否存在
            if(!isCanUseSD()){
                bit = getBitmapFormURL(url,type,newWidth,newHeight);
                return bit;
            }
            //文件是否存在
            File path = Environment.getExternalStorageDirectory();
            File fileDirectory = new File(path.getAbsolutePath() + downPathImageDir);
            File f = new File(fileDirectory,getFileNameFromUrl(url));
            if(!f.exists()){
                downFileToSD(url);
                return getBitmapFromSD(f,type,newWidth,newHeight);
            }else{
                if(D)Log.d(TAG, "要获取的图片路径为："+f.getPath());
                //文件存在
                if(type == AppConstant.CUTIMG){
                    bit = ImageUtil.cutImg(f, newWidth, newHeight);
                }else{
                    bit = ImageUtil.scaleImg(f, newWidth, newHeight);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bit;

    }

    /**
     * 描述：通过文件的网络地址从SD卡中读取图片.
     * @param url 文件的网络地址
     * @param type 图片的处理类型（剪切或者缩放到指定大小，参考AbConstant类）
     * @param newWidth 新图片的宽
     * @param newHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getBitmapFromSD(String url,int type,int newWidth, int newHeight){
        Bitmap bit = null;
        try {
            //SD卡是否存在
            if(!isCanUseSD()){
                return null;
            }
            //文件是否存在
            File path = Environment.getExternalStorageDirectory();
            File fileDirectory = new File(path.getAbsolutePath() + downPathImageDir);
            File f = new File(fileDirectory,getFileNameFromUrl(url));
            if(!f.exists()){
                return null;
            }else{
                //文件存在
                if(type == AppConstant.CUTIMG){
                    bit = ImageUtil.cutImg(f, newWidth, newHeight);
                }else{
                    bit = ImageUtil.scaleImg(f, newWidth, newHeight);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bit;

    }

    /**
     * 描述：通过文件的本地地址从SD卡读取图片.
     *
     * @param file the file
     * @param type 图片的处理类型（剪切或者缩放到指定大小，参考AbConstant类）
     * @param newWidth 新图片的宽
     * @param newHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getBitmapFromSD(File file,int type,int newWidth, int newHeight){
        Bitmap bit = null;
        try {
            //SD卡是否存在
            if(!isCanUseSD()){
                return null;
            }
            //文件是否存在
            if(!file.exists()){
                return null;
            }
            //文件存在
            if(type== AppConstant.CUTIMG){
                bit = ImageUtil.cutImg(file, newWidth, newHeight);
            }else{
                bit = ImageUtil.scaleImg(file, newWidth, newHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bit;
    }

    /**
     * 描述：将图片的byte[]写入本地文件.
     * @param imgByte 图片的byte[]形势
     * @param fileName 文件名称，需要包含后缀，如.jpg
     * @param type 图片的处理类型（剪切或者缩放到指定大小，参考AbConstant类）
     * @param newWidth 新图片的宽
     * @param newHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getBitmapFormByte(byte[] imgByte,String fileName,int type,int newWidth, int newHeight){
        FileOutputStream fos = null;
        DataInputStream dis = null;
        ByteArrayInputStream bis = null;
        Bitmap b = null;
        try {
            if(imgByte!=null){
                File sdcardDir = Environment.getExternalStorageDirectory();
                String path = sdcardDir.getAbsolutePath()+downPathImageDir;
                File file = new File(path+fileName);

                if(!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                if(!file.exists()){
                    file.createNewFile();
                }
                fos = new FileOutputStream(file);
                int readLength = 0;
                bis = new ByteArrayInputStream(imgByte);
                dis = new DataInputStream(bis);
                byte[] buffer = new byte[1024];

                while ((readLength = dis.read(buffer))!=-1) {
                    fos.write(buffer, 0, readLength);
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                    }
                }
                fos.flush();
            }
            b = getBitmapFormURL("/"+fileName,type,newWidth,newHeight);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(dis!=null){
                try {
                    dis.close();
                } catch (Exception e) {
                }
            }
            if(bis!=null){
                try {
                    bis.close();
                } catch (Exception e) {
                }
            }
            if(fos!=null){
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
        return  b;
    }

    /**
     * 描述：根据URL从互连网获取图片.
     * @param url 要下载文件的网络地址
     * @param type 图片的处理类型（剪切或者缩放到指定大小，参考AbConstant类）
     * @param newWidth 新图片的宽
     * @param newHeight 新图片的高
     * @return Bitmap 新图片
     */
    public static Bitmap getBitmapFormURL(String url,int type,int newWidth, int newHeight){
        Bitmap bit = null;
        try {
            bit = ImageUtil.getBitmapFormURL(url, type, newWidth, newHeight);
        } catch (Exception e) {
            if(D)Log.d(TAG, "下载图片异常："+e.getMessage());
        }
        if(D)Log.d(TAG, "返回的Bitmap："+bit);
        return bit;
    }

    /**
     * 描述：获取src中的图片资源.
     *
     * @param src 图片的src路径，如（“image/arrow.png”）
     * @return Bitmap 图片
     */
    public static Bitmap getBitmapFormSrc(String src){
        Bitmap bit = null;
        try {
            bit = BitmapFactory.decodeStream(FileUtil.class.getResourceAsStream(src));
        } catch (Exception e) {

            if(D)Log.d(TAG, "获取图片异常："+e.getMessage());
        }
        if(D)Log.d(TAG, "返回的Bitmap："+bit);
        return bit;
    }

    /**
     * 描述：获取网络文件的大小.
     *
     * @param Url 图片的网络路径
     * @return int 网络文件的大小
     */
    public static int getContentLengthFormUrl(String Url){
        int mContentLength = 0;
        try {
            URL url = new URL(Url);
            HttpURLConnection mHttpURLConnection = (HttpURLConnection) url.openConnection();
            mHttpURLConnection.setConnectTimeout(5 * 1000);
            mHttpURLConnection.setRequestMethod("GET");
            mHttpURLConnection.setRequestProperty("Accept","image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            mHttpURLConnection.setRequestProperty("Accept-Language", "zh-CN");
            mHttpURLConnection.setRequestProperty("Referer", Url);
            mHttpURLConnection.setRequestProperty("Charset", "UTF-8");
            mHttpURLConnection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            mHttpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            mHttpURLConnection.connect();
            if (mHttpURLConnection.getResponseCode() == 200){
                // 根据响应获取文件大小
                mContentLength = mHttpURLConnection.getContentLength();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(D)Log.d(TAG, "获取长度异常："+e.getMessage());
        }
        return mContentLength;
    }

    /**
     * HTTP文件上传.
     *
     * @param actionUrl 要使用的URL
     * @param params 表单参数
     * @param files 要上传的文件列表
     * @return  http返回的结果 或http响应码
     * @throws com.bslee.MeSport.global.AppException the ab app exception
     */
    public static String postFile(String actionUrl, HashMap<String, String> params,
                                  HashMap<String, File> files) throws AppException {
        //标识每个文件的边界
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--";
        String LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        HttpURLConnection conn = null;
        DataOutputStream outStream = null;
        String retStr = "200";
        try {
            URL uri = new URL(actionUrl);
            conn = (HttpURLConnection) uri.openConnection();
            //允许输入
            conn.setDoInput(true);
            //允许输出
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            // Post方式
            conn.setRequestMethod("POST");
            //设置request header 属性
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                    + ";boundary=" + BOUNDARY);
            //组装表单参数数据
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\""
                        + entry.getKey() + "\"" + LINEND);
                sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                sb.append(LINEND);
                sb.append(entry.getValue());
                sb.append(LINEND);
            }
            //获取连接发送参数数据
            outStream = new DataOutputStream(conn.getOutputStream());
            outStream.write(sb.toString().getBytes());
            // 发送文件数据
            if (files != null)
                for (Map.Entry<String, File> file : files.entrySet()) {
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append(PREFIX);
                    sb1.append(BOUNDARY);
                    sb1.append(LINEND);
                    sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                            + file.getKey() + "\"" + LINEND);
                    sb1.append("Content-Type: application/octet-stream; charset="
                            + CHARSET + LINEND);
                    sb1.append(LINEND);
                    //请求头结束至少有一个空行（即有两对\r\n）表示请求头结束了
                    if(D)Log.d("TAG", "request start:"+sb1.toString());
                    outStream.write(sb1.toString().getBytes());
                    InputStream is = new FileInputStream(file.getValue());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    is.close();
                    //一个文件结束一个空行
                    outStream.write(LINEND.getBytes());
                }
            //请求结束的边界打印
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            Log.d("TAG","request end:"+ new String(end_data).toString());
            outStream.write(end_data);
            outStream.flush();
            outStream.close();
            // 获取响应码
            int ret = conn.getResponseCode();
            retStr = String.valueOf(ret);
            if(ret == 200){
                String result = StrUtil.convertStreamToString(conn.getInputStream());
                return result;
            }
        } catch (Exception e) {
            throw new AppException(e);
        } finally{
            if(conn!=null){
                conn.disconnect();
            }
        }
        return retStr;
    }

    /**
     * 获取文件名，通过网络获取，如果网络失败截取文件地址URL作为文件名，获取的是最后一个“/”以后的字符串.
     * @param strUrl 文件地址
     * @return 文件名
     */
    public static String getFileNameFromUrl(String strUrl){
        String name = null;
        try {
            if(StrUtil.isEmpty(strUrl) || strUrl.indexOf("/")==-1 || strUrl.length()<2){
                URL url = new URL(strUrl);
                HttpURLConnection mHttpURLConnection = (HttpURLConnection) url.openConnection();
                mHttpURLConnection.setConnectTimeout(5 * 1000);
                mHttpURLConnection.setRequestMethod("GET");
                mHttpURLConnection.setRequestProperty("Accept","image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                mHttpURLConnection.setRequestProperty("Accept-Language", "zh-CN");
                mHttpURLConnection.setRequestProperty("Referer", strUrl);
                mHttpURLConnection.setRequestProperty("Charset", "UTF-8");
                mHttpURLConnection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                mHttpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                mHttpURLConnection.connect();
                if (mHttpURLConnection.getResponseCode() == 200){
                    for (int i = 0;; i++) {
                        String mine = mHttpURLConnection.getHeaderField(i);
                        if (mine == null){
                            break;
                        }
                        if ("content-disposition".equals(mHttpURLConnection.getHeaderFieldKey(i).toLowerCase())) {
                            Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
                            if (m.find())
                                return m.group(1);
                        }
                    }
                    // 默认取一个文件名
                    return DateUtil.getCurrentDate(DateUtil.dateFormatYMDHMS) + ".tmp";
                }
                return null;
            }else{
                int index = strUrl.lastIndexOf("/");
                name = strUrl.substring(index+1);
            }
        } catch (Exception e) {
        }
        return name;
    }

    /**
     * 描述：从sd卡中的文件读取到byte[].
     *
     * @param path sd卡中文件路径
     * @return byte[]
     */
    public static byte[] getByteArrayFromSD(String path) {
        byte[] bytes = null;
        ByteArrayOutputStream out = null;
        try {
            File file = new File(path);
            //SD卡是否存在
            if(!isCanUseSD()){
                return null;
            }
            //文件是否存在
            if(!file.exists()){
                return null;
            }

            long fileSize = file.length();
            if (fileSize > Integer.MAX_VALUE) {
                return null;
            }

            FileInputStream in = new FileInputStream(path);
            out = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int size=0;
            while((size=in.read(buffer))!=-1) {
                out.write(buffer,0,size);
            }
            in.close();
            bytes = out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(out!=null){
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }
        return bytes;
    }

    /**
     * 描述：将byte数组写入文件.
     *
     * @param path the path
     * @param content the content
     * @param create the create
     */
    public static void writeByteArrayToSD(String path, byte[] content,boolean create){

        FileOutputStream fos = null;
        try {
            File file = new File(path);
            //SD卡是否存在
            if(!isCanUseSD()){
                return;
            }
            //文件是否存在
            if(!file.exists()){
                if(create){
                    File parent = file.getParentFile();
                    if(!parent.exists()){
                        parent.mkdirs();
                        file.createNewFile();
                    }
                }else{
                    return;
                }
            }
            fos = new FileOutputStream(path);
            fos.write(content);

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(fos!=null){
                try {
                    fos.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 描述：SD卡是否能用.
     *
     * @return true 可用,false不可用
     */
    public static boolean isCanUseSD() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 描述：获得当前下载的地址.
     * @return 下载的地址（默认SD卡download）
     */
    public static String getDownPathImageDir() {
        return downPathImageDir;
    }

    /**
     * 描述：设置图片文件的下载保存路径（默认SD卡download/cache_images）.
     * @param downPathImageDir 图片文件的下载保存路径
     */
    public static void setDownPathImageDir(String downPathImageDir) {
        FileUtil.downPathImageDir = downPathImageDir;
    }


    /**
     * Gets the down path file dir.
     *
     * @return the down path file dir
     */
    public static String getDownPathFileDir() {
        return downPathFileDir;
    }

    /**
     * 描述：设置文件的下载保存路径（默认SD卡download/cache_files）.
     * @param downPathFileDir 文件的下载保存路径
     */
    public static void setDownPathFileDir(String downPathFileDir) {
        FileUtil.downPathFileDir = downPathFileDir;
    }

    /**
     * 描述：获取默认的图片保存全路径.
     *
     * @return the default image down path dir
     */
    public static String getDefaultImageDownPathDir(){
        String pathDir = null;
        try {
            if(!isCanUseSD()){
                return null;
            }
            //初始化图片保存路径
            File fileRoot = Environment.getExternalStorageDirectory();
            File dirFile = new File(fileRoot.getAbsolutePath() + FileUtil.downPathImageDir);
            if(!dirFile.exists()){
                dirFile.mkdirs();
            }
            pathDir = dirFile.getPath();
        } catch (Exception e) {
        }
        return pathDir;
    }



}
