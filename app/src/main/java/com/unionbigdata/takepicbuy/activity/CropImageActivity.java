package com.unionbigdata.takepicbuy.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.constant.Constant;
import com.unionbigdata.takepicbuy.cropimage.CircleHighlightView;
import com.unionbigdata.takepicbuy.cropimage.Crop;
import com.unionbigdata.takepicbuy.cropimage.CropImageView;
import com.unionbigdata.takepicbuy.cropimage.CropUtil;
import com.unionbigdata.takepicbuy.cropimage.GraphicsUtil;
import com.unionbigdata.takepicbuy.cropimage.HighlightView;
import com.unionbigdata.takepicbuy.cropimage.ImageViewTouchBase;
import com.unionbigdata.takepicbuy.cropimage.MonitoredActivity;
import com.unionbigdata.takepicbuy.cropimage.RotateBitmap;
import com.unionbigdata.takepicbuy.http.AsyncHttpTask;
import com.unionbigdata.takepicbuy.http.ResponseHandler;
import com.unionbigdata.takepicbuy.params.UploadFileParam;
import com.unionbigdata.takepicbuy.utils.ClickUtil;
import com.unionbigdata.takepicbuy.utils.PhoneManager;
import com.unionbigdata.takepicbuy.widget.CircleProgress;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 剪裁图片，并上传搜索
 * Created by Christain on 2015/4/21.
 */
public class CropImageActivity extends MonitoredActivity {

    @InjectView(R.id.flSearch)
    FrameLayout flSearch;
    @InjectView(R.id.crop_image)
    CropImageView imageView;
    @InjectView(R.id.llCrop)
    LinearLayout llCrop;
    @InjectView(R.id.progress)
    CircleProgress progress;

    private MenuItem menuItem;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1458;
    private String mCurrentPhotoPath;
    private File mTempDir;
    private boolean hasType = false;

    /**
     * 裁剪相关
     */
    private static final boolean IN_MEMORY_CROP = Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD_MR1;
    private static final int SIZE_DEFAULT = 2048;
    private static final int SIZE_LIMIT = 4096;

    private final Handler handler = new Handler();

    private int aspectX;
    private int aspectY;

    // Output image size
    private int maxX;
    private int maxY;
    private int exifRotation;

    private Uri sourceUri;
    private Uri saveUri;

    private boolean isSaving;

    private int sampleSize;
    private RotateBitmap rotateBitmap;
    private HighlightView cropView;
    private boolean isCircleCrop = false;

    @Override
    protected int layoutResId() {
        return R.layout.crop_image_activity;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        getToolbar().setTitle("上传图片");
        getToolbar().setTitleTextColor(0xFFFFFFFF);
        getToolbar().setNavigationIcon(R.mipmap.icon_toolbar_white_back);
        setSupportActionBar(getToolbar());
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTempDir = new File(Constant.UPLOAD_FILES_DIR_PATH);
        if (!mTempDir.exists()) {
            mTempDir.mkdirs();
        }
        llCrop.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        Intent intent = getIntent();
        if (intent.hasExtra("TYPE")) {
            hasType = true;
            String type = intent.getStringExtra("TYPE");
            if (type.equals("ALBUM")) {
                Crop.pickImage(CropImageActivity.this);
            } else {
                getImageFromCamera();
            }
        } else {
            hasType = false;
            SelectPicDialog dialog = new SelectPicDialog(CropImageActivity.this, R.style.LoadingDialogTheme);
            dialog.show();
        }

        imageView.context = CropImageActivity.this;
        imageView.setRecycler(new ImageViewTouchBase.Recycler() {
            @Override
            public void recycle(Bitmap b) {
                b.recycle();
                System.gc();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crop, menu);
        menuItem = menu.findItem(R.id.select);
        menuItem.setActionView(R.layout.menu_item_view);
        LinearLayout layout = (LinearLayout) menuItem.getActionView();
        ImageView btn = (ImageView) layout.findViewById(R.id.ivItem);
        btn.setImageResource(R.mipmap.icon_toolbar_select_pic);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectPicDialog dialog = new SelectPicDialog(CropImageActivity.this, R.style.LoadingDialogTheme);
                dialog.show();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @OnClick(R.id.flSearch)
    void OnSearch() {
        if (!ClickUtil.isFastClick()) {
            if (rotateBitmap == null) {
                toast("请先选择图片");
                return;
            }
            progressStatus();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    onSaveClicked();
                }
            }).start();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Uri uri = (Uri) msg.obj;
                    UploadSearch(uri);
                    break;
            }
        }
    };

    private void UploadSearch(Uri uri) {
        try {
            final File file = new File(new URI(uri.toString()));
            UploadFileParam param = new UploadFileParam(file);
            AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
                @Override
                public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                    try {
                        progress.stopAnim();
                        JSONObject object = new JSONObject(result);
                        String imgUrl = object.getString("srcimageurl");
                        String fileName = imgUrl.substring(imgUrl.lastIndexOf("/"), imgUrl.lastIndexOf("."));
                        file.renameTo(new File(Constant.UPLOAD_FILES_DIR_PATH + fileName + "_SEARCH.jpg"));
                        Intent intent = new Intent(CropImageActivity.this, SearchResult.class);
                        intent.putExtra("IMGURL", imgUrl);
                        intent.putExtra("FROM", "SEARCH");
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progress.stopAnim();
                        if (file.exists()) {
                            file.delete();
                        }
                        toast("搜索失败，请重试");
                        finish();
                    }
                }

                @Override
                public void onResponseFailed(int returnCode, String errorMsg) {
                    progress.stopAnim();
                    if (file.exists()) {
                        file.delete();
                    }
                    toast("搜索失败，请重试");
                    finish();
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    protected void getImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = String.valueOf(System.currentTimeMillis()) + "_SEARCH.jpg";
        File cropFile = new File(mTempDir, fileName);
        Uri fileUri = Uri.fromFile(cropFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        mCurrentPhotoPath = fileUri.getPath();
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (resultCode == Activity.RESULT_CANCELED) {
            if (hasType) {
                finish();
            }
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Crop.REQUEST_PICK) {
                beginCrop(result.getData());
            } else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
                if (mCurrentPhotoPath != null) {
                    beginCrop(Uri.fromFile(new File(mCurrentPhotoPath)));
                }
            }
        }
    }

    private void beginCrop(Uri source) {
        rotateBitmap = null;
        if (imageView != null && imageView.highlightViews != null) {
            imageView.clear();
            imageView.highlightViews.clear();
        }
        String fileName = String.valueOf(System.currentTimeMillis()) + "_SEARCH.jpg";
        File cropFile = new File(mTempDir, fileName);
        aspectX = 0;
        aspectY = 0;
        maxX = 0;
        maxY = 0;
        isCircleCrop = false;
        saveUri = Uri.fromFile(cropFile);
        sourceUri = source;
        if (sourceUri != null) {
            exifRotation = CropUtil.getExifRotation(CropUtil.getFromMediaUri(getContentResolver(), sourceUri));
            InputStream is = null;
            try {
                sampleSize = calculateBitmapSampleSize(sourceUri);
                is = getContentResolver().openInputStream(sourceUri);
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = sampleSize;
                rotateBitmap = new RotateBitmap(BitmapFactory.decodeStream(is, null, option), exifRotation);
            } catch (IOException e) {
                setResultException(e);
            } catch (OutOfMemoryError e) {
                setResultException(e);
            } finally {
                CropUtil.closeSilently(is);
            }
        }
        if (rotateBitmap == null) {
            finish();
            return;
        }
        startCrop();
    }

    private int calculateBitmapSampleSize(Uri bitmapUri) throws IOException {
        InputStream is = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            is = getContentResolver().openInputStream(bitmapUri);
            BitmapFactory.decodeStream(is, null, options);
        } finally {
            CropUtil.closeSilently(is);
        }

        int maxSize = getMaxImageSize();
        int sampleSize = 1;
        while (options.outHeight / sampleSize > maxSize || options.outWidth / sampleSize > maxSize) {
            sampleSize = sampleSize << 1;
        }
        return sampleSize;
    }

    private int getMaxImageSize() {
        int textureLimit = getMaxTextureSize();
        if (textureLimit == 0) {
            return SIZE_DEFAULT;
        } else {
            return Math.min(textureLimit, SIZE_LIMIT);
        }
    }

    private int getMaxTextureSize() {
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
    }

    private void startCrop() {
        if (isFinishing()) {
            return;
        }
        imageView.setImageRotateBitmapResetBase(rotateBitmap, true);
        CropUtil.startBackgroundJob(this, null, getResources().getString(R.string.crop__wait), new Runnable() {
            public void run() {
                final CountDownLatch latch = new CountDownLatch(1);
                handler.post(new Runnable() {

                    public void run() {
                        if (imageView.getScale() == 1F) {
                            imageView.center(true, true);
                        }
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                new Cropper().crop();
            }
        }, handler);
    }

    private class Cropper {

        private void makeDefault() {
            if (rotateBitmap == null) {
                return;
            }
            HighlightView hv = isCircleCrop ? new CircleHighlightView(imageView) : new HighlightView(imageView);
            final int width = rotateBitmap.getWidth();
            final int height = rotateBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            int cropWidth = Math.min(width, height) * 4 / 5;
            @SuppressWarnings("SuspiciousNameCombination")
            int cropHeight = cropWidth;

            if (!isCircleCrop && aspectX != 0 && aspectY != 0) {
                if (aspectX > aspectY) {
                    cropHeight = cropWidth * aspectY / aspectX;
                } else {
                    cropWidth = cropHeight * aspectX / aspectY;
                }
            }

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            hv.setup(imageView.getUnrotatedMatrix(), imageRect, cropRect, aspectX != 0 && aspectY != 0);
            imageView.add(hv);
        }

        public void crop() {
            handler.post(new Runnable() {

                public void run() {
                    makeDefault();
                    imageView.invalidate();
                    if (imageView.highlightViews.size() == 1) {
                        cropView = imageView.highlightViews.get(0);
                        cropView.setFocus(true);
                    }
                }
            });
        }
    }

    private void onSaveClicked() {
        if (cropView == null || isSaving) {
            normalStatus();
            return;
        }
        isSaving = true;

        Bitmap croppedImage = null;
        Rect r = cropView.getScaledCropRect(sampleSize);
        int width = r.width();
        int height = r.height();

        int outWidth = width, outHeight = height;
        if (maxX > 0 && maxY > 0 && (width > maxX || height > maxY)) {
            float ratio = (float) width / (float) height;
            if ((float) maxX / (float) maxY > ratio) {
                outHeight = maxY;
                outWidth = (int) ((float) maxY * ratio + .5f);
            } else {
                outWidth = maxX;
                outHeight = (int) ((float) maxX / ratio + .5f);
            }
        }

        if (IN_MEMORY_CROP && rotateBitmap != null) {
            croppedImage = inMemoryCrop(rotateBitmap, croppedImage, r, width, height, outWidth, outHeight);
            if (croppedImage != null) {
                float scale = (float) croppedImage.getWidth() / (float) croppedImage.getHeight();
                if (scale > 1) {
                    if (croppedImage.getWidth() > 300) {
                        croppedImage = PhoneManager.zoomImage(croppedImage, (float) 300, ((float) 300) / scale);
                    }
                } else {
                    if (croppedImage.getHeight() > 300) {
                        croppedImage = PhoneManager.zoomImage(croppedImage, ((float) 300) * scale, (float) 300);
                    }
                }
                imageView.setImageBitmapResetBase(croppedImage, true);
                imageView.center(true, true);
                imageView.highlightViews.clear();
            }
        } else {
            try {
                croppedImage = decodeRegionCrop(croppedImage, r);
                if (isCircleCrop) {
                    croppedImage = cropCircleView(croppedImage);
                }
            } catch (IllegalArgumentException e) {
                setResultException(e);
                finish();
                return;
            }

            if (croppedImage != null) {
                float scale = (float) croppedImage.getWidth() / (float) croppedImage.getHeight();
                if (scale > 1) {
                    if (croppedImage.getWidth() > 300) {
                        croppedImage = PhoneManager.zoomImage(croppedImage, (float)300, ((float) 300) / scale);
                    }
                } else {
                    if (croppedImage.getHeight() > 300) {
                        croppedImage = PhoneManager.zoomImage(croppedImage, ((float) 300) * scale, (float) 300);
                    }
                }
                imageView.setImageRotateBitmapResetBase(new RotateBitmap(croppedImage, exifRotation), true);
                imageView.center(true, true);
                imageView.highlightViews.clear();
            }
        }
        saveImage(croppedImage);
    }

    private void saveImage(Bitmap croppedImage) {
        if (croppedImage != null) {
            final Bitmap b = croppedImage;
            CropUtil.startBackgroundJob(this, null, getResources().getString(R.string.crop__saving), new Runnable() {
                public void run() {
                    saveOutput(b);
                }
            }, handler);
        } else {
            finish();
        }
    }

    @TargetApi(10)
    private Bitmap decodeRegionCrop(Bitmap croppedImage, Rect rect) {
        // Release memory now
        clearImageView();

        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(sourceUri);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            final int width = decoder.getWidth();
            final int height = decoder.getHeight();

            if (exifRotation != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(-exifRotation);

                RectF adjusted = new RectF();
                matrix.mapRect(adjusted, new RectF(rect));

                adjusted.offset(adjusted.left < 0 ? width : 0, adjusted.top < 0 ? height : 0);
                rect = new Rect((int) adjusted.left, (int) adjusted.top, (int) adjusted.right, (int) adjusted.bottom);
            }

            try {
                croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());

            } catch (IllegalArgumentException e) {
                // Rethrow with some extra information
                throw new IllegalArgumentException("Rectangle " + rect + " is outside of the image (" + width + "," + height + ","
                        + exifRotation + ")", e);
            }

        } catch (IOException e) {
            finish();
        } catch (OutOfMemoryError e) {
            setResultException(e);
        } finally {
            CropUtil.closeSilently(is);
        }
        return croppedImage;
    }

    private Bitmap inMemoryCrop(RotateBitmap rotateBitmap, Bitmap croppedImage, Rect r, int width, int height, int outWidth, int outHeight) {
        System.gc();
        try {
            croppedImage = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.RGB_565);

            RectF dstRect = new RectF(0, 0, width, height);

            Matrix m = new Matrix();
            m.setRectToRect(new RectF(r), dstRect, Matrix.ScaleToFit.FILL);
            m.preConcat(rotateBitmap.getRotateMatrix());
            if (isCircleCrop) {
                return cropCircleView(rotateBitmap.getBitmap(), croppedImage, m);
            }
            Canvas canvas = new Canvas(croppedImage);
            canvas.drawBitmap(rotateBitmap.getBitmap(), m, null);
        } catch (OutOfMemoryError e) {
            setResultException(e);
            System.gc();
        }

        clearImageView();
        return croppedImage;
    }

    private void clearImageView() {
        imageView.clear();
        if (rotateBitmap != null) {
            rotateBitmap.recycle();
        }
        System.gc();
    }

    private void saveOutput(Bitmap croppedImage) {
        if (saveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(saveUri);
                if (outputStream != null) {

                    if (exifRotation > 0) {
                        try {
                            Matrix matrix = new Matrix();
                            matrix.reset();
                            matrix.postRotate(exifRotation);
                            Bitmap bMapRotate =
                                    Bitmap.createBitmap(croppedImage, 0, 0, croppedImage.getWidth(), croppedImage.getHeight(), matrix,
                                            true);
                            bMapRotate.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                        } catch (Exception e) {
                            e.printStackTrace();
                            croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        } finally {
                            if (croppedImage != null && !croppedImage.isRecycled()) {
                                croppedImage.recycle();
                            }
                        }
                    } else {
                        croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    }
                }

            } catch (IOException e) {
                setResultException(e);
            } finally {
                CropUtil.closeSilently(outputStream);
            }

            if (!IN_MEMORY_CROP) {
                CropUtil.copyExifRotation(CropUtil.getFromMediaUri(getContentResolver(), sourceUri),
                        CropUtil.getFromMediaUri(getContentResolver(), saveUri));
            }

            final Bitmap b = croppedImage;
            handler.post(new Runnable() {
                public void run() {
                    imageView.clear();
                    b.recycle();
                }
            });
            Message msg = new Message();
            msg.what = 0;
            msg.obj = saveUri;
            mHandler.sendMessage(msg);
//            setResultUri(saveUri);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rotateBitmap != null) {
            rotateBitmap.recycle();
        }
    }

    @Override
    public boolean onSearchRequested() {
        return false;
    }

    public boolean isSaving() {
        return isSaving;
    }

    private void setResultException(Throwable throwable) {
        progress.stopAnim();
        normalStatus();
        toast("截取图片失败,请重试");
    }

    /**
     * 正常状态
     */
    private void normalStatus() {
        getToolbar().setTitle("上传图片");
        menuItem.setVisible(true);
        progress.setVisibility(View.GONE);
        imageView.clear();
        SelectPicDialog dialog = new SelectPicDialog(CropImageActivity.this, R.style.LoadingDialogTheme);
        dialog.show();
        llCrop.setVisibility(View.VISIBLE);
    }

    /**
     * 上传状态
     */
    private void progressStatus() {
        getToolbar().setTitle("搜索中...");
        menuItem.setVisible(false);
        progress.setVisibility(View.VISIBLE);
        progress.startAnim();
        llCrop.setVisibility(View.GONE);
    }

    private Bitmap cropCircleView(Bitmap scaledSrcBmp, Bitmap output, Matrix m) {
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2, scaledSrcBmp.getWidth() / 2, scaledSrcBmp.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, m, paint);
        return output;
    }

    private Bitmap cropCircleView(Bitmap scaledSrcBmp) {
        System.out.println(" cropCircleView !!! ");

        return GraphicsUtil.getCircleBitmap(scaledSrcBmp);
//        return GraphicsUtil.getOvalBitmap( scaledSrcBmp);
//        return GraphicsUtil.getRoundedShape( scaledSrcBmp);

    }

    /**
     * 选择图片Dialog
     */
    private class SelectPicDialog extends Dialog {

        public SelectPicDialog(Context context) {
            super(context);
        }

        public SelectPicDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_select_pic);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            getWindow().setAttributes(lp);
            lp.alpha = 1.0f;// 透明度，1是不透明
            lp.dimAmount = 0.5f;// 黑暗度
            TextView tvAlbum = (TextView) findViewById(R.id.tvAlbum);
            TextView tvCamera = (TextView) findViewById(R.id.tvCamera);
            tvAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    Crop.pickImage(CropImageActivity.this);
                }
            });
            tvCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    getImageFromCamera();
                }
            });
        }
    }

}
