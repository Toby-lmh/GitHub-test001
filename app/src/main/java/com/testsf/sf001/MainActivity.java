package com.testsf.sf001;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oushangfeng.marqueelayout.MarqueeLayout;
import com.oushangfeng.marqueelayout.MarqueeLayoutAdapter;
import com.testsf.sf001.utils.CropUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends Activity {
//测试上传
    //public final static String WEIXIN_SNS_MIMETYPE = "vnd.android.cursor.item/vnd.com.tencent.mm.plugin.sns.timeline";//微信朋友圈
    public int SELECT_PIC_BY_PICK_PHOTO = 00000;
    MarqueeLayout mMarqueeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            // 主要是打开微信的
            @Override
            public void onClick(View v) {
                // ComponentName（组件名称）是用来打开其他应用程序中的Activity或服务的
                Intent intent = new Intent();
                ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");// 报名该有activity

                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);

                startActivityForResult(intent, 0);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            // 选择图片返回打开微信朋友圈并自动设置相关参数
            @Override
            public void onClick(View v) {
                // pickPhoto();// 选择图片返回打开微信朋友圈并自动设置相关参数
                shareWeChat("//storage/emulated/0/DCIM/Camera/IMG_20161116_100110.jpg");//发送单张图片给好友

            }
        });


//跳转QQ聊天界面测试
        findViewById(R.id.btn_qq).setOnClickListener(new View.OnClickListener() {
            // 选择图片返回打开微信朋友圈并自动设置相关参数
            @Override
            public void onClick(View v) {
                //String url="mqqwpa://im/chat?chat_type=wpa&uin=545461965";//跳转至QQ好友
                String url = "mqqwpa://im/chat?chat_type=group&uin=463028593&version=1";//跳转至QQ群
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

            }
        });

        //
        ArrayList<String> mSrcList;
        MarqueeLayoutAdapter mSrcAdapter;
        mMarqueeLayout = (MarqueeLayout) findViewById(R.id.marquee_layout);
        mSrcList = new ArrayList<>();
        mSrcList.add("我听见了你的声音 也藏着颗不敢见的心");
        mSrcList.add("我们的爱情到这刚刚好 剩不多也不少 还能忘掉");
        mSrcList.add("像海浪撞过了山丘以后还能撑多久 他可能只为你赞美一句后往回流");
        mSrcList.add("少了有点不甘 但多了太烦");
        mSrcAdapter = new MarqueeLayoutAdapter<String>(mSrcList) {
            @Override
            public int getItemLayoutId() {
                return R.layout.item_simple_text;
            }

            @Override
            public void initView(View view, int position, String item) {
                ((TextView) view).setText(item);
            }
        };
        mMarqueeLayout.setAdapter(mSrcAdapter);
        mMarqueeLayout.start();


    }

    private void shareWeChat(String path){
        Uri uriToImage = Uri.fromFile(new File(path));
        Intent shareIntent = new Intent();
        //发送图片到朋友圈
       // ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        //发送图片给好友。
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        shareIntent.setComponent(comp);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
        //shareIntent.putExtra(Intent.EXTRA_TEXT,"发送消息分享");
        shareIntent.setType("image/jpg");
        startActivity(Intent.createChooser(shareIntent, "分享图片"));
    }



    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK)
        {
           // doPhoto(requestCode,data);
            Uri newUri = Uri.parse("file:///"+ CropUtils.getPath(this, data.getData()));
            if (newUri != null) {
                //  Toast.makeText(mContext,"成功获取相册图片",Toast.LENGTH_LONG).show();
                //cropRawPhoto(newUri);
                String path = getRealFilePath(this, newUri);
                Log.d("----图片","----" + path);
               // shareWeChat(path);//单张图片上传朋友圈
                ArrayList<Uri> uris = new ArrayList<>();
                uris.add(newUri);
                shareToTimeLine("测试内容-----https://www.baidu.com/",uris);

            } else {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }





    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }



    /**
     * 分享多图到朋友圈，多张图片加文字
     *
     * @param uris
     */
    private void shareToTimeLine(String title, ArrayList<Uri> uris) {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");

        intent.putExtra("Kdescription", title);

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivity(intent);
    }
}
