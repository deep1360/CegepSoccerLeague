package com.example.cegepsoccerleague;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceData {

    static final String Useremail="email",Userpassword="password";


    public static SharedPreferences getSharedPreferences(Context ctx)
    {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }


    public static void setUseremail(Context ctx, String useremail){
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Useremail, useremail);
        editor.commit();
    }
    public static void setUserpassword(Context ctx, String userpassword){
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(Userpassword, userpassword);
        editor.commit();
    }

    public static String getUseremail(Context ctx)
    {
        return getSharedPreferences(ctx).getString(Useremail,"");
    }

    public static String getUserpassword(Context ctx)
    {
        return getSharedPreferences(ctx).getString(Userpassword,"");
    }


    public static void clearUserData(Context ctx)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(Useremail);
        editor.remove(Userpassword);

        editor.commit();
    }



}
