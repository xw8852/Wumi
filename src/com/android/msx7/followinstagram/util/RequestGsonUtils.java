package com.android.msx7.followinstagram.util;

import android.util.Pair;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by Josn on 2015/9/7.
 */
public class RequestGsonUtils {

    public static final String getGson(Pair<String,String>...pairs){
        HashMap<String,String> maps=new HashMap<String,String>();
        for (Pair<String,String> pair:pairs) {
            maps.put(pair.first,pair.second);
        }
        return new Gson().toJson(maps);
    }

}
