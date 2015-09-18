package com.android.msx7.followinstagram.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Josn on 2015/9/13.
 */
public class StringsUtils {

    //找出@ 和#
    public static final String[] findString(String desc) {
        String[] arr = null;
        List<String> arrs = new ArrayList<String>();
        if (desc.contains("@") || desc.contains("#")) {
            int start = 0;
            while (true) {
                int index = desc.indexOf("@", start);
                int _index = desc.indexOf("#", start);
                if (index < 0 && _index < 0) break;
                if (_index < index && _index > -1 && index > -1) index = _index;
                if (index < 0 && _index > 0) index = _index;
                start = index;
                int end = desc.indexOf("@", index + 1);
                int end_0 = desc.indexOf("#", index + 1);
                int end_1 = desc.indexOf(" ", index + 1);
                int[] ends = new int[]{end, end_0, end_1};
                Arrays.sort(ends);
                int _end = ends[0];
                if (_end < 0) _end = ends[1];
                if (_end < 0) _end = ends[2];
                if (_end < 0) _end = desc.length();
                String _desc = desc.substring(start, _end);
                if (_desc.length() < 2) {
                    start = _end;
                    continue;
                }
                arrs.add(_desc);
                start = _end;
                if (start == desc.length()) break;
            }

//            start = 0;
//            while (true) {
//                int index = desc.indexOf("#", start);
//                if (index < 0) break;
//                start = index;
//                int end = desc.indexOf("@", index + 1);
//                int end_0 = desc.indexOf("#", index + 1);
//                int end_1 = desc.indexOf(" ", index + 1);
//                int[] ends = new int[]{end, end_0, end_1};
//                Arrays.sort(ends);
//                int _end = ends[0];
//                if (_end < 0) _end = ends[1];
//                if (_end < 0) _end = ends[2];
//                if (_end < 0) _end = desc.length();
//                String _desc = desc.substring(start, _end);
//                if (_desc.length() < 2) {
//                    start = _end;
//                    continue;
//                }
//                arrs.add(_desc);
//                start = _end;
//                if (start == desc.length()) break;
//            }
        }
        if (!arrs.isEmpty())
            arr = arrs.toArray(new String[arrs.size()]);
        return arr;
    }
}
