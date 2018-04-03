package com.amsu.wear.util;

import android.text.TextUtils;

import com.amsu.wear.bean.User;

import java.util.Date;

/**
 * @anthor haijun
 * @project name: Shop
 * @class name：com.haijun.shop.util
 * @time 2018-02-02 11:29 AM
 * @describe
 */
public class UserUtil {

    private static User user;
    public static User getUserInfo() {
        if (user==null){
            user = UserUtil.getUserFromSP();
            if (user!=null){
                user.setAge(getAge());
            }
        }
        return user;
    }

    private static User getUserFromSP(){
        return (User) SPUtil.getObjectFromSP("user",User.class);
    }

    public static void saveUserToLocal(User user){
        SPUtil.putObjectToSP("user",user);
    }

    public static boolean isLoginEd(){
        return getUserInfo() != null;
    }

    private static int getAge(){
        String birthday = getUserInfo().getBirthday();
        String formatUserBirthday = getFormatUserBirthday(birthday);
        if (!TextUtils.isEmpty(formatUserBirthday) && !formatUserBirthday.equals("null")) {
            String[] split = formatUserBirthday.split("-");
            if (split.length > 0) {
                Date date = new Date();
                return 1900 + date.getYear() - Integer.parseInt(split[0]);
            }
        }
        return 0;
    }

    public static String getFormatUserBirthday(String birthday) {
        try {
            long l = Long.parseLong(birthday);
            return FormatUtil.getSpecialFormatTime("yyyy-MM-dd",new Date(l));
        }catch (Exception e){
            return birthday;
        }
    }
}
