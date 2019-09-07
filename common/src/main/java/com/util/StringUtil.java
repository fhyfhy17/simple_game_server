package com.util;

public class StringUtil {

    public static String cutByRemovePostfix(String complete, String postfix) {
        return complete.replace(postfix, "");
    }
    
    public static String getSplitePrefix(String content,String delimit){
        return content.substring(0,content.indexOf(delimit));
    }
    
    public static String getSpliteSuffix(String content,String delimit){
        return content.substring(content.indexOf(delimit)+1);
    }
    
    public static String replaceLast(String s, String sub, String with) {
        int i = s.lastIndexOf(sub);
        if (i == -1) {
            return s;
        }
        return with + s.substring(i + sub.length());
    }
}
