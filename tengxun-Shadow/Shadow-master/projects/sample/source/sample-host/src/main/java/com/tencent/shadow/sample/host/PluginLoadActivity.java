/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.sample.host;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.sample.constant.Constant;

import java.io.File;


public class PluginLoadActivity extends Activity {

    private ViewGroup mViewGroup;

    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        mViewGroup = findViewById(R.id.container);
        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlugin();
            }
        });

    }


    public void startPlugin() {

        PluginHelper.getInstance().singlePool.execute(new Runnable() {
            @Override
            public void run() {
                // /data/user/0/com.tencent.shadow.sample.host/files/pluginmanager.apk
                File pluginManagerFile=PluginHelper.getInstance().pluginManagerFile;
                HostApplication.getApp().loadPluginManager(pluginManagerFile);

                Bundle bundle = new Bundle();
                ///data/user/0/com.tencent.shadow.sample.host/files/plugin-debug.zip
                File pluginZipFile=PluginHelper.getInstance().pluginZipFile;
                bundle.putString(Constant.KEY_PLUGIN_ZIP_PATH, pluginZipFile.getAbsolutePath());
                //sample-plugin-app
                String str1=getIntent().getStringExtra(Constant.KEY_PLUGIN_PART_KEY);
                bundle.putString(Constant.KEY_PLUGIN_PART_KEY,str1 );
                // com.tencent.shadow.sample.plugin.app.lib.gallery.splash.SplashActivity
                //com.tencent.shadow.sample.plugin.app.lib.gallery.MainActivity b-这是一个位于dynamic-pluginmanager-apk中的view
                String str2= getIntent().getStringExtra(Constant.KEY_ACTIVITY_CLASSNAME);
                bundle.putString(Constant.KEY_ACTIVITY_CLASSNAME,str2);

                HostApplication.getApp().getPluginManager().enter(PluginLoadActivity.this, Constant.FROM_ID_START_ACTIVITY, bundle, new EnterCallback() {
                            @Override
                            public void onShowLoadingView(final View view) {
                                System.out.println(">]开始loading");
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mViewGroup.addView(view);// 只是loading页面
                                    }
                                });
                            }

                            @Override
                            public void onCloseLoadingView() {
                                finish();
                                //运行此方法时候 插件就会加载出来
                                System.out.println(">]关闭loading");
                            }

                            @Override
                            public void onEnterComplete() {
                                System.out.println(">]完成插件加载");
                            }
                        });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewGroup.removeAllViews();
    }
}
