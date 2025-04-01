package com.dubhe.hyperlightmaster.util;

import android.content.Context;
import android.util.Log;

import com.dubhe.hyperlightmaster.LightApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MarkdownReader {

    // 读取assets目录下的.md文件并返回内容作为字符串
    public static String readAssetFile(Context context, String filename) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // 从assets文件夹读取文件
            InputStream inputStream = context.getAssets().open(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LightApplication.TAG, "读取文件失败: " + e.getMessage(), e);
        }
        return stringBuilder.toString();
    }

}
