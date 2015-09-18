package com.android.msx7.followinstagram.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Josn on 2015/9/13.
 */
public class StringsUtils {

    //找出@ 和#
    public static final String[] findString(String desc) {
        String[] arr = null;
        List<String> arrs = new ArrayList<String>();
        if (!TextUtils.isEmpty(desc) && (desc.contains("@") || desc.contains("#"))) {
            Pattern pattern = Pattern.compile("[@|#]{1}[^@#\\s]{1,}");
            Matcher matcher = pattern.matcher(desc);
            while (matcher.find()) {
                arrs.add(matcher.group());
            }
        }
        if (!arrs.isEmpty())
            arr = arrs.toArray(new String[arrs.size()]);
        return arr;
    }
}
