package com.hzh.easy.cache.sample;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hzh.easy.cache.CacheManager;
import com.hzh.easy.cache.base.CacheFactory;
import com.hzh.easy.cache.sample.bean.UserInfo;
import com.hzh.easy.cache.sample.cache.UserInfoCache;
import com.hzh.easy.cache.sample.cache.params.UserInfoCacheParams;
import com.hzh.easy.cache.sample.util.AsyncExecutor;
import com.hzh.fast.permission.FastPermission;
import com.hzh.fast.permission.callback.PermissionCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText nameInput;
    private EditText signInput;
    private Button saveCache;
    private Button readCache;
    private ProgressBar progress;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameInput = (EditText) findViewById(R.id.name);
        signInput = (EditText) findViewById(R.id.sign);
        saveCache = (Button) findViewById(R.id.saveCache);
        readCache = (Button) findViewById(R.id.readCache);
        progress = (ProgressBar) findViewById(R.id.progress);
        result = (TextView) findViewById(R.id.result);

        CacheManager.getInstance().init(getApplicationContext());
        //请求存储权限
        String[] perms;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE};
        } else {
            perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
        requestPermission(perms, new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "授权成功，可进行下一步操作");
            }
        });
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.saveCache:
                        save();
                        break;
                    case R.id.readCache:
                        read();
                        break;
                    default:
                        break;
                }
            }
        };
        saveCache.setOnClickListener(listener);
        readCache.setOnClickListener(listener);
    }

    /**
     * 检查用户是否将姓名和签名都输入
     */
    private boolean checkInput() {
        String name = nameInput.getText().toString().trim();
        String sign = signInput.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            toastMsg("请输入姓名");
            return false;
        }
        if (TextUtils.isEmpty(sign)) {
            toastMsg("请输入个性签名");
            return false;
        }
        return true;
    }

    /**
     * 保存用户信息到缓存中
     */
    private void save() {
        boolean isComplet = checkInput();
        if (!isComplet) {
            return;
        }
        final String name = nameInput.getText().toString().trim();
        final String sign = signInput.getText().toString().trim();
        AsyncExecutor.getInstance().execute(this, new AsyncExecutor.AsyncCallback<Boolean>() {
            @Override
            protected void runBefore() {
                super.runBefore();
                showProgress();
            }

            @Override
            protected Boolean running() {
                boolean isSuccess;
                try {
                    SystemClock.sleep(600);
                    UserInfoCache infoCache = CacheFactory.create(UserInfoCache.class);
                    UserInfoCacheParams params = new UserInfoCacheParams();
                    params.putUserId(String.valueOf(name.hashCode()));
                    UserInfo userInfo = new UserInfo(name, sign);
                    infoCache.put(params, userInfo);
                    isSuccess = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    isSuccess = false;
                }
                return isSuccess;
            }

            @Override
            protected void runAfter(Boolean isSuccess) {
                hideProgress();
                if (isSuccess) {
                    toastMsg("保存成功");
                } else {
                    toastMsg("保存失败");
                }
            }
        });
    }

    /**
     * 从缓存中读取用户信息
     */
    private void read() {
        final String name = nameInput.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            toastMsg("姓名不能为空");
            return;
        }
        AsyncExecutor.getInstance().execute(this, new AsyncExecutor.AsyncCallback<UserInfo>() {
            @Override
            protected void runBefore() {
                super.runBefore();
                showProgress();
            }

            @Override
            protected UserInfo running() {
                SystemClock.sleep(600);
                UserInfoCache infoCache = CacheFactory.create(UserInfoCache.class);
                UserInfoCacheParams params = new UserInfoCacheParams()
                        .putUserId(String.valueOf(name.hashCode()));
                return infoCache.get(params);
            }

            @Override
            protected void runAfter(UserInfo info) {
                hideProgress();
                if (null == info) {
                    toastMsg("查询成功");
                    result.setText("无该用户信息，请确认后再试");
                } else {
                    result.setText("查询到用户：\n" + "name: "
                            + info.getName() + "\n" + "sign：" + info.getSign());
                }
            }
        });
    }

    private void toastMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showProgress() {
        if (progress == null) {
            progress = (ProgressBar) findViewById(R.id.progress);
        }
        progress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        if (progress == null) {
            progress = (ProgressBar) findViewById(R.id.progress);
        }
        progress.setVisibility(View.GONE);
    }

    /**
     * 请求权限
     *
     * @param perms           需要申请的权限数组
     * @param grantedAfterRun 授权之后的动作
     */
    private void requestPermission(String[] perms, final Runnable grantedAfterRun) {
        FastPermission.getInstance().request(MainActivity.this, new PermissionCallback() {
            @Override
            public void onGranted() {
                if (grantedAfterRun != null) {
                    grantedAfterRun.run();
                }
            }

            @Override
            public void onDenied(final List<String> perms) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("申请权限")
                        .setMessage("请允许app申请的所有权限，以便正常使用")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //再次请求未允许的权限
                                String[] againPerms = new String[perms.size()];
                                for (int j = 0; j < perms.size(); j++) {
                                    againPerms[j] = perms.get(j);
                                }
                                requestPermission(againPerms, grantedAfterRun);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();
            }
        }, perms);
    }
}