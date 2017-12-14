package com.wulee.administrator.zuji.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片工具类
 */
public class ImageUtil {
	private static final String TAG = ImageUtil.class.getSimpleName();
	
	public static final String IMGJSUFFIX = ".imgj"; // 所有jpg图片的后缀
	public static final String IMGPSUFFIX = ".imgp"; // 所有png图片的后缀
	public static final String IMGGSUFFIX = ".imgg"; // 所有gif图片的后缀

	public static final int RESIZE_MODE_WIDTH_LIMIT = 1; // 压缩图片时在宽度上限制
	public static final int RESIZE_MODE_HEIGHT_LIMIT = 2; // 压缩图片时在高度上限制

	/**
	 * 按照压缩比例压缩图片
	 * @param src 图片资源
	 * @param factor 压缩系数
	 * @return bitmap
	 */
	public static Bitmap resizeBitmap(Bitmap src, float factor) {
		Bitmap dst = null;
		if (src != null) {
			int dstW = (int) (src.getWidth() * factor);
			int dstH = (int) (src.getHeight() * factor);
			try {
				dst = Bitmap.createScaledBitmap(src, dstW, dstH, true);
			} catch (OutOfMemoryError e) {
				Log.w(TAG, String.format("resizeBitmap -- OutOfMemoryError occur with weight=%s, height=%s", dstW, dstH));
			}

			if (src != dst) {
				safeReleaseBitmap(src);
			}
		}
		return dst;
	}	

	/**
	 * 等比例压缩图片 
	 * 1：如果图片的宽和高都小于目标压缩图片的宽和高，那么不做任何操作
	 * 2：如果图片的宽和高任何一个大于目标压缩图片的宽和高，那么以最大边为基准来计算压缩比，然后等比例压缩图片
	 * @param srcFilePath 源图片的路径
	 * @param resampleWidth 压缩的目标宽度
	 * @param resampleHeight 压缩的目标高度
	 * @return bitmap 缩小后的图片对象， 使用完要用recycle方法释放空间
	 */
	public static Bitmap resizeBitmap(String srcFilePath, int resampleWidth, int resampleHeight) {
		if (srcFilePath == null)
			throw new IllegalArgumentException("Image file path should not be null");

		Bitmap taget = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPurgeable = true;
		opts.inJustDecodeBounds = true; 
		BitmapFactory.decodeFile(srcFilePath, opts);
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = getClosestResampleSize(opts.outWidth, opts.outHeight, Math.max(resampleWidth, resampleHeight));
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(srcFilePath, opts);
			if (bitmap == null) {
				return taget;
			}
			// 拿到图片的旋转值：为了满足有的机型
			int degree = getExifOrientation(srcFilePath);
			Matrix m = new Matrix();

			if (bitmap.getWidth() > resampleWidth || bitmap.getHeight() > resampleHeight) {
				float qw = ((float) resampleWidth) / bitmap.getWidth();
				float qh = ((float) resampleHeight) / bitmap.getHeight();
				if (qh < qw) {
					float width01 = qh * bitmap.getWidth();
					bitmap = Bitmap.createScaledBitmap(bitmap, (int) width01, resampleHeight, true);
				} else {
					float height01 = qw * bitmap.getHeight();
					bitmap = Bitmap.createScaledBitmap(bitmap, resampleWidth, (int) height01, true);
				}
			}

			if (0 != degree) {
				m.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
			}
			taget = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			if (taget != bitmap) {
				safeReleaseBitmap(bitmap);
			}

		} catch (OutOfMemoryError e) {
			Log.e(TAG, "resizeBitmap -- outofMemoryError.", e);
		}
		return taget;
	}
	
	/**
	 * 压缩图片
	 * @param srcFilePath
	 * @param resampleWidth
	 * @param resampleHeight
	 * @return
	 */
	public static Bitmap resizeBitmapForce(String srcFilePath, int resampleWidth, int resampleHeight) {
		if (srcFilePath == null)
			throw new IllegalArgumentException("Image file path should not be null");

		Bitmap taget = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPurgeable = true;
		opts.inJustDecodeBounds = true; // 为true那么将不返回实际的bitmap
		BitmapFactory.decodeFile(srcFilePath, opts);
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = getClosestResampleSize(opts.outWidth, opts.outHeight, Math.max(resampleWidth, resampleHeight));
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(srcFilePath, opts);
			if (bitmap == null) {
				return taget;
			}
			// 拿到图片的旋转值：为了满足有的机型
			int degree = getExifOrientation(srcFilePath);
			Matrix m = new Matrix();

			float qw = ((float) resampleWidth) / bitmap.getWidth();
			float qh = ((float) resampleHeight) / bitmap.getHeight();
			if (qh < qw) {
				float width01 = qh * bitmap.getWidth();
				bitmap = Bitmap.createScaledBitmap(bitmap, (int) width01, resampleHeight, true);
			} else {

				float height01 = qw * bitmap.getHeight();
				bitmap = Bitmap.createScaledBitmap(bitmap, resampleWidth, (int) height01, true);
			}
			if (0 != degree) {
				m.setRotate(degree, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
			}
			taget = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			if (taget != bitmap) {
				safeReleaseBitmap(bitmap);
			}

		} catch (OutOfMemoryError e) {
			Log.e(TAG, "resizeBitmap outofMemoryError.", e);
		}
		return taget;
	}		

	public static Bitmap resizeBitmapForce(Bitmap srcBitmap, int resampleWidth, int resampleHeight) {
		if (srcBitmap == null){
			throw new IllegalArgumentException("Image file path should not be null");
		}

		Bitmap taget = null;
		try {
			// 拿到图片的旋转值：为了满足有的机型
			Matrix m = new Matrix();

			float qw = ((float) resampleWidth) / srcBitmap.getWidth();
			float qh = ((float) resampleHeight) / srcBitmap.getHeight();
			if (qh < qw) {
				float width01 = qh * srcBitmap.getWidth();
				srcBitmap = Bitmap.createScaledBitmap(srcBitmap, (int) width01, resampleHeight, true);
			} else {
				float height01 = qw * srcBitmap.getHeight();
				srcBitmap = Bitmap.createScaledBitmap(srcBitmap, resampleWidth, (int) height01, true);
			}
			taget = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), m, true);
			if (taget != srcBitmap) {
				safeReleaseBitmap(srcBitmap);
			}

		} catch (OutOfMemoryError e) {
			Log.e(TAG, "resizeBitmap -- outofMemoryError.", e);
		}
		return taget;
	}
	
	/**
	 * 压缩图片之后保存到file文件中
	 * @param src
	 * @param dstFilePath
	 * @param factor
	 * @return
	 * @throws IOException
	 */
	public static File resizeBitmapAndSave(Bitmap src, String dstFilePath, float factor) throws IOException {
		Bitmap dst = resizeBitmap(src, factor);

		File dstFile = null;
		if (dst != null) {
			dstFile = saveStickerBitmap(dst, dstFilePath);
			dst.recycle();
		}

		return dstFile;
	}	

	/*
	 * 计算需要压缩的尺寸
	 */
	private static int getClosestResampleSize(int cw, int ch, int maxDim) {
		int max = Math.max(cw, ch);

		int resample = 1;
		for (resample = 1; resample < Integer.MAX_VALUE; resample++) {
			if (resample * maxDim > max) {
				resample--;
				break;
			}
		}

		if (resample > 0) {
			return resample;
		}
		return 1;
	}

	/*
	 * 检查图片是否需要旋转
	 */
	private static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException ex) {
			Log.e(TAG, "cannot read exif", ex);
		}
		
		if (exif != null) {
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
			}
		}
		return degree;
	}

	/**
	 * (暂时未使用)
	 * @param src
	 * @param mode
	 * @param limit
	 * @return
	 */
	public static float get1DResizeFactor(Bitmap src, int mode, int limit) {
		float f = 1.0f;
		if (src != null) {
			float w = src.getWidth();
			float h = src.getHeight();

			switch (mode) {
			case RESIZE_MODE_WIDTH_LIMIT:
				if (w > limit) {
					f = limit / w;
				}
				break;
			case RESIZE_MODE_HEIGHT_LIMIT:
				if (h > limit) {
					f = limit / h;
				}
				break;
			}
		}

		return f;
	}

	/**
	 * 根据要求的最大宽wLimit和高hLimit，计算压缩比例
	 */
	public static float get2DResizeFactor(Bitmap src, int wLimit, int hLimit) {
		float f = 1.0f;
		if (src != null) {
			float w = src.getWidth();
			float h = src.getHeight();

			if (w < wLimit && h < hLimit) {
				return f;
			}
			float qw = wLimit / w;
			float qh = hLimit / h;

			if (qw > qh) {
				f = qh;
			} else {
				f = qw;
			}
		}
		return f;

	}

	/**
	 * 计算图片压缩比例，此方法用于只在一个方向上限制图片大小。对于小于限制的图片，不做压缩。
	 * @param  filePath 源文件路径
	 * @param  mode @see RESIZE_MODE_WIDTH_LIMIT @see RESIZE_MODE_HEIGHT_LIMIT
	 * @param  limit 限制值
	 * @return float factor 图片缩小系数
	 */
	public static float get1DResizeFactor(String filePath, int mode, int limit) {
		if (filePath == null){
			throw new IllegalArgumentException("Image file path should not be null");
		}
		
		Bitmap src = BitmapFactory.decodeFile(filePath);
		return get1DResizeFactor(src, mode, limit);

	}

	/**
	 * 计算图片压缩比例，此方法用于只在一个矩形区域内限制图片大小。对于小于限制的图片，不做压缩。
	 * @param  filePath 源文件路径
	 * @param  wLimit 宽度限制
	 * @param  hLimit 高度限制
	 * @return float factor 图片缩小系数
	 */
	public static float get2DResizeFactor(String filePath, int wLimit, int hLimit) {
		if (filePath == null){
			throw new IllegalArgumentException("Image file path should not be null");
		}

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);
		float factor = 1.0f;
		float optsWidth = opts.outWidth;
		float optsHeight = opts.outHeight;
		if (optsWidth < wLimit && optsHeight < hLimit) {
			return factor;
		}
		float qw = wLimit / optsWidth;
		float qh = hLimit / optsHeight;

		if (qw > qh) {
			factor = qh;
		} else {
			factor = qw;
		}
		return factor;
	}

	/**
	 * 将图片保存为文件,并返回图片的文件
	 * @param  bmp 要保存的图片对象
	 * @param  dstFilePath 要保存的文件路径
	 * @return File 被保存的图片文件
	 */
	public static File saveBitmap(Bitmap bmp, String dstFilePath) throws IOException {
		if (bmp == null)
			throw new IllegalArgumentException("Image should not be null");
		
		if (dstFilePath == null)
			throw new IllegalArgumentException("File path should not be null");

		File file = new File(dstFilePath);
		if (!file.exists()) {			
			file.createNewFile();
		}

		FileOutputStream fos = new FileOutputStream(file);

		if (bmp != null && fos != null) {
			BufferedOutputStream bos = new BufferedOutputStream(fos, 4096);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.close();
			fos.close();
		}

		return file;
	}

	/**
	 * 用于保存sticker的缩略图的图片信息
	 * @param bmp
	 * @param dstFilePath
	 * @return
	 * @throws IOException
	 */
	public static File saveStickerBitmap(Bitmap bmp, String dstFilePath) throws IOException {
		if (bmp == null)
			throw new IllegalArgumentException("Image should not be null");
		if (dstFilePath == null)
			throw new IllegalArgumentException("File path should not be null");

		File file = new File(dstFilePath);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fos = new FileOutputStream(file);

		if (bmp != null && fos != null) {
			BufferedOutputStream bos = new BufferedOutputStream(fos, 4096);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
			bos.close();
			fos.close();
		}
		return file;
	}

	/**
	 * 安全释放Bitmap占用的资源
	 * @param bitmap
	 */
	public static void safeReleaseBitmap(Bitmap bitmap) {
		if (null == bitmap)
			return;
		
		if (!bitmap.isRecycled()) {
			bitmap.recycle();
		}
		bitmap = null;
	}
	
	/**
	 * 把缩略图转换成数据流格式
	 * @param filepath
	 * @param width
	 * @param height
	 * @return
	 */
	public static byte[] getThumbData(String filepath, int width, int height){
		Bitmap bitmap = resizeBitmap(filepath, width, height);
		if(null == bitmap){
			return null;
		}
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, bos);
		safeReleaseBitmap(bitmap);
		return bos.toByteArray();
	}
	
	/**
	 * 把缩略图转换成数据流格式
	 * @param filepath
	 * @param
	 * @return
	 * @throws FileNotFoundException
	 */
	public static byte[] getThumbData(String filepath){
		File f = new File(filepath);
        if(!f.exists()){  
            return null;
        }  
  
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int)f.length());
        BufferedInputStream in = null;
        try{  
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;  
            byte[] buffer = new byte[buf_size];  
            int len = 0;  
            while(-1 != (len = in.read(buffer,0,buf_size))){  
                bos.write(buffer,0,len);  
            }  
            return bos.toByteArray();  
        }catch (IOException e) {
            return null;
        }finally{  
            try{  
                in.close(); 
                bos.close();  
            }catch (IOException e) {
            }
            
        }  
	}
	
	/**
	 * 切割图片的四个角为圆角
	 * @param bitmap
	 * @param
	 * @return
	 */
	public static Drawable cutImgToRoundCorner(Bitmap bitmap) {
		if(null == bitmap){
			return null;
		}
		float roundRadius = 10f;// 圆角半径，单位：像素
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		int color = 0xff424242;
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundRadius, roundRadius, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		
		Drawable drawable = null;
		if(null != output){
			drawable =new BitmapDrawable(output);
		}
		
		return drawable;
	}
	
	
	/**
	 * 转换图片成圆形
	 * @param bitmap  传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;

			left = 0;
			top = 0;
			right = width;
			bottom = width;

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

		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// 设置画笔无锯齿

		canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

		// 以下有两种方法画圆,drawRounRect和drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
		canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

		return output;
	}
	
	
	/**
     * 将彩色图转换为灰度图
     * @param img 位图
     * @return  返回转换好的位图
     */ 
    public static Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth();         //获取位图的宽  
        int height = img.getHeight();       //获取位图的高  

        int []pixels = new int[width * height]; //通过位图的大小创建像素点数组  

        img.getPixels(pixels, 0, width, 0, 0, width, height); 
        int alpha = 0xFF << 24;  
        for(int i = 0; i < height; i++)  { 
            for(int j = 0; j < width; j++) { 
                int grey = pixels[width * i + j]; 

                int red = ((grey  & 0x00FF0000 ) >> 16); 
                int green = ((grey & 0x0000FF00) >> 8); 
                int blue = (grey & 0x000000FF); 

                grey = (int)((float) red * 0.3 + (float)green * 0.59 + (float)blue * 0.11); 
                grey = alpha | (grey << 16) | (grey << 8) | grey; 
                pixels[width * i + j] = grey; 
            } 
        } 
        Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height); 
        return result; 
    }


	/**
	 * 处理图像
	 * @param iv
	 * @param imgUrl
	 * @param defResId  默认图资源Id
	 */
	public static void setDefaultImageView(ImageView iv, String imgUrl, int defResId, Context context){
		RequestManager glideRequest = Glide.with(context);
		if(!TextUtils.isEmpty(imgUrl)){
			ViewPropertyAnimation.Animator anim = new ViewPropertyAnimation.Animator() {
				@Override
				public void animate(View view) {
					ObjectAnimator fadeAnim = ObjectAnimator.ofFloat( view, "alpha", 0.5f, 1f );
					fadeAnim.setDuration( 600 );
					fadeAnim.start();
				}
			};
			glideRequest.load(imgUrl).animate(anim).placeholder(defResId).centerCrop().into(iv);
		}else{
			iv.setImageResource(defResId);
		}
	}
	/**
	 * 处理圆角图像（圆角默认资源图：  bg_round_img_def）
	 * @param iv
	 * @param imgUrl
	 * @param defResId  默认图资源Id
	 * 圆角弧度默认为 8dp
	 */
	public static void setRoundImageView(ImageView iv, String imgUrl, int defResId, Context context){
		RequestManager glideRequest = Glide.with(context);
		if(!TextUtils.isEmpty(imgUrl)){
			ViewPropertyAnimation.Animator anim = new ViewPropertyAnimation.Animator() {
				@Override
				public void animate(View view) {
					ObjectAnimator fadeAnim = ObjectAnimator.ofFloat( view, "alpha", 0.5f, 1f );
					fadeAnim.setDuration( 600 );
					fadeAnim.start();
				}
			};
			glideRequest.load(imgUrl).animate(anim).placeholder(defResId).transform(new GlideRoundTransform(context, 10)).into(iv);
		}else{
			iv.setImageResource(defResId);
		}
	}

	/**
	 * 处理圆形图
	 */
	public static void setCircleImageView(ImageView iv, String imgUrl, int defResId, Context context){
		RequestManager glideRequest = Glide.with(context);
		if(!TextUtils.isEmpty(imgUrl)){
			ViewPropertyAnimation.Animator anim = view -> {
                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat( view, "alpha", 0.5f, 1f );
                fadeAnim.setDuration( 600 );
                fadeAnim.start();
            };
			glideRequest.load(imgUrl).animate(anim).placeholder(defResId).transform(new GlideCircleTransform(context,0)).into(iv);
		}else{
			iv.setImageResource(defResId);
		}
	}


	public static byte[] getSmallBitmap(String filePath) {
		Bitmap bitmap;
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inSampleSize = calculateInSampleSize(options, 1000, 1000);
		options.inJustDecodeBounds = false;

		try {
			bitmap = BitmapFactory.decodeFile(filePath, options);
		} catch (Exception e) {
			options.inSampleSize = calculateInSampleSize(options, 500, 500);
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(filePath, options);
		}
		byte[] bytes = Bitmap2Bytes(bitmap != null ? bitmap : null);
		if (null != bitmap && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
		return bytes;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	private static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 75, baos);
		return baos.toByteArray();
	}
}
