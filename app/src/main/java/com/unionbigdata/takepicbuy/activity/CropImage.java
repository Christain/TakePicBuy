package com.unionbigdata.takepicbuy.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unionbigdata.takepicbuy.R;
import com.unionbigdata.takepicbuy.baseclass.BaseActivity;
import com.unionbigdata.takepicbuy.constant.Constant;
import com.unionbigdata.takepicbuy.cropper.CutView;
import com.unionbigdata.takepicbuy.http.AsyncHttpTask;
import com.unionbigdata.takepicbuy.http.ResponseHandler;
import com.unionbigdata.takepicbuy.params.UploadFileParam;
import com.unionbigdata.takepicbuy.utils.ClickUtil;
import com.unionbigdata.takepicbuy.utils.IntentManage;
import com.unionbigdata.takepicbuy.utils.PhoneManager;
import com.unionbigdata.takepicbuy.widget.CircleProgress;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 上传图片
 * Created by Christain on 2015/5/7.
 */
public class CropImage extends BaseActivity {

    private CutView imageView;
    @InjectView(R.id.llCrop)
    LinearLayout llCrop;
    @InjectView(R.id.progress)
    CircleProgress progress;
    @InjectView(R.id.frCutView)
    FrameLayout frCutview;
    @InjectView(R.id.llBack)
    LinearLayout llBack;
    @InjectView(R.id.tvBack)
    TextView tvBack;
    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    private Thread searchThread;

    private MenuItem menuItem;
    private boolean hasType = false;
    private File mTempDir, cropFile, cameraFile;
    private Bitmap cutBitmap;
    private Uri CameraUri = null;
    private MediaScannerConnection msc;

    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 1458;
    public static final int REQUEST_PICK = 9162;
    private boolean isBack = false;

    @Override
    protected int layoutResId() {
        return R.layout.crop_image;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        isBack = false;
        getToolbar().setTitle("");
//        getToolbar().setTitleTextColor(0xFFFFFFFF);
//        getToolbar().setNavigationIcon(R.mipmap.icon_toolbar_white_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recycleBmp();
                isBack = true;
                finish();
            }
        });
        tvTitle.setText("上传图片");
        tvBack.setText("返回");
        setSupportActionBar(getToolbar());
//        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                recycleBmp();
//                isBack = true;
//                finish();
//            }
//        });
        mTempDir = new File(Constant.UPLOAD_FILES_DIR_PATH);
        if (!mTempDir.exists()) {
            mTempDir.mkdirs();
        }
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        imageView = new CutView(this);
        imageView.setLayoutParams(fl);
        frCutview.addView(imageView);
        llCrop.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        Intent intent = getIntent();
        if (intent.hasExtra("TYPE")) {
            hasType = true;
            String type = intent.getStringExtra("TYPE");
            if (type.equals("ALBUM")) {
                getImageFromAlbum();
            } else {
                getImageFromCamera();
            }
        } else {
            hasType = false;
            SelectPicDialog dialog = new SelectPicDialog(CropImage.this, R.style.LoadingDialogTheme);
            dialog.show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            recycleBmp();
            isBack = true;
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                SelectPicDialog dialog = new SelectPicDialog(CropImage.this, R.style.LoadingDialogTheme);
                dialog.show();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.select) {
            SelectPicDialog dialog = new SelectPicDialog(CropImage.this, R.style.LoadingDialogTheme);
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.flSearch)
    void OnSearch() {
        if (!ClickUtil.isFastClick()) {
            if (imageView.m_bmp == null) {
                toast("请先选择图片");
                return;
            }
            progressStatus();
            searchThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    onSaveClicked();
                }
            });
            searchThread.start();
        }
    }

    private void UploadSearch(final File file) {
        UploadFileParam param = new UploadFileParam(file);
        AsyncHttpTask.post(param.getUrl(), param, new ResponseHandler() {
            @Override
            public void onResponseSuccess(int returnCode, Header[] headers, String result) {
                if (!isBack) {
                    try {
                        JSONObject object = new JSONObject(result);
                        String imgUrl = object.getString("srcimageurl");
                        String fileName = imgUrl.substring(imgUrl.lastIndexOf("/"), imgUrl.lastIndexOf("."));
                        file.renameTo(new File(Constant.UPLOAD_FILES_DIR_PATH + fileName + "_SEARCH.jpg"));
                        Intent intent = new Intent(CropImage.this, SearchResult.class);
                        intent.putExtra("IMGURL", imgUrl);
                        startActivity(intent);
                        uploadFailStatus();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (file.exists()) {
                            file.delete();
                        }
                        uploadFailStatus();
                        toast("搜索失败，请重试");
                    }
                }
            }

            @Override
            public void onResponseFailed(int returnCode, String errorMsg) {
                if (!isBack) {
                    uploadFailStatus();
                    if (file.exists()) {
                        file.delete();
                    }
                    toast("搜索失败，请重试");
                }
            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    UploadSearch(cropFile);
                    break;
            }
        }
    };

    /**
     * 相册选择
     */
    protected void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        startActivityForResult(intent, REQUEST_PICK);
    }

    /**
     * 拍照
     */
    protected void getImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
        cameraFile = new File(mTempDir, fileName);
        CameraUri = Uri.fromFile(cameraFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, CameraUri);
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
            if (requestCode == REQUEST_PICK) {
                Uri uri = result.getData();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String url = IntentManage.getPath(CropImage.this, uri);
                    if (url != null) {
                        recycleCutViewBmp();
                        imageView.SetImage(url);
                    } else {
                        toast("选择图片失败，请重试");
                    }
                } else {
                    String[] imgInfo = {MediaStore.Images.Media.DATA};
                    Cursor imgCursor = getContentResolver().query(uri, imgInfo, null, null, null);
                    if (imgCursor.moveToFirst()) {
                        int index = imgCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        recycleCutViewBmp();
                        imageView.SetImage(imgCursor.getString(index));
                    } else {
                        toast("选择图片失败，请重试");
                    }
                    imgCursor.close();
                }
            } else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String url = IntentManage.getPath(CropImage.this, CameraUri);
                    if (url != null) {
                        recycleCutViewBmp();
                        imageView.SetImage(url);
                        savePhotoToAlbum(url);
                    } else {
                        toast("加载图片失败，请重试");
                    }
                } else {
                    recycleCutViewBmp();
                    imageView.SetImage(cameraFile.getAbsolutePath());
                    savePhotoToAlbum(cameraFile.getAbsolutePath());
//                    String[] imgInfo = {MediaStore.Images.Media.DATA};
//                    Cursor imgCursor = getContentResolver().query(CameraUri, imgInfo, null, null, null);
//                    if (imgCursor.moveToFirst()) {
//                        int index = imgCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                        recycleCutViewBmp();
//                        imageView.SetImage(imgCursor.getString(index));
//                        savePhotoToAlbum(imgCursor.getString(index));
//                    } else {
//                        toast("加载图片失败，请重试");
//                    }
//                    imgCursor.close();
                }
            }
        }
    }

    /**
     * 保存拍照图片到手机相册
     */
    private void savePhotoToAlbum(final String CameraPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String fileName = "拍图购" + System.currentTimeMillis() + ".jpg";
                    final String uri = MediaStore.Images.Media.insertImage(getContentResolver(), CameraPath, fileName, fileName);
                    if (uri != null) {
                        msc = new MediaScannerConnection(CropImage.this, new MediaScannerConnectionClient() {
                            @Override
                            public void onMediaScannerConnected() {
                                Uri uri1 = Uri.parse(uri);
                                String[] imgInfo = {MediaStore.Images.Media.DATA};
                                Cursor imgCursor = getContentResolver().query(uri1, imgInfo, null, null, null);
                                if (imgCursor.moveToFirst()) {
                                    int index = imgCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                    msc.scanFile(imgCursor.getString(index), "image/jpeg");
                                } else {
                                    if (Constant.SHOW_LOG) {
                                        Toast.makeText(CropImage.this, "图片刷新失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                imgCursor.close();
                            }

                            @Override
                            public void onScanCompleted(String s, Uri uri) {
                                msc.disconnect();
                            }
                        });
                        msc.connect();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 截取图片
     */
    private void onSaveClicked() {
        if (imageView != null) {
            cutBitmap = imageView.GetCutImage();
            float scale = (float) cutBitmap.getWidth() / (float) cutBitmap.getHeight();
            if (scale > 1) {
                if (cutBitmap.getWidth() > 300) {
                    cutBitmap = PhoneManager.zoomImage(cutBitmap, (float) 300, ((float) 300) / scale);
                }
            } else {
                if (cutBitmap.getHeight() > 300) {
                    cutBitmap = PhoneManager.zoomImage(cutBitmap, ((float) 300) * scale, (float) 300);
                }
            }
            String fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
            cropFile = new File(mTempDir, fileName);

            if (PhoneManager.saveBitmapToImageFile(CropImage.this, Bitmap.CompressFormat.PNG, cutBitmap, cropFile, 100)) {
                mHandler.sendEmptyMessage(0);
            } else {
                uploadFailStatus();
                toast("裁剪图片失败，请重试");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (CameraUri != null && !TextUtils.isEmpty(CameraUri.toString())){
            outState.putString("URI", CameraUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (CameraUri != null && TextUtils.isEmpty(CameraUri.toString())) {
            CameraUri = Uri.parse(savedInstanceState.getString("URI"));
        }
    }

    /**
     * 正常状态
     */
    private void normalStatus() {
        getToolbar().setTitle("");
        menuItem.setVisible(true);
        progress.setVisibility(View.GONE);
        SelectPicDialog dialog = new SelectPicDialog(CropImage.this, R.style.LoadingDialogTheme);
        dialog.show();
        llCrop.setVisibility(View.VISIBLE);
    }

    /**
     * 上传失败状态
     */
    private void uploadFailStatus() {
        getToolbar().setTitle("");
        menuItem.setVisible(true);
        progress.stopAnim();
        progress.setVisibility(View.GONE);
        llCrop.setVisibility(View.VISIBLE);
    }

    /**
     * 搜索状态
     */
    private void progressStatus() {
        getToolbar().setTitle("");
        menuItem.setVisible(false);
        progress.setVisibility(View.VISIBLE);
        progress.startAnim();
        llCrop.setVisibility(View.GONE);
    }

    private void recycleBmp() {
        if (cutBitmap != null && !cutBitmap.isRecycled()) {
            cutBitmap.recycle();
            System.gc();
        }
        cutBitmap = null;
    }

    private void recycleCutViewBmp() {
        if (imageView.m_bmp != null && !imageView.m_bmp.isRecycled()) {
            imageView.m_bmp.recycle();
            System.gc();
        }
        imageView.m_bmp = null;
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
            lp.alpha = 1.0f;
            lp.dimAmount = 0.5f;
            TextView tvAlbum = (TextView) findViewById(R.id.tvAlbum);
            TextView tvCamera = (TextView) findViewById(R.id.tvCamera);
            tvAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hasType = false;
                    dismiss();
                    getImageFromAlbum();
                }
            });
            tvCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hasType = false;
                    dismiss();
                    getImageFromCamera();
                }
            });
        }
    }
}
