package com.xtreme.jx.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;
import com.xtreme.jx.model.Comic;
import com.xtreme.jx.model.User;
import com.google.gson.Gson;

import java.util.ArrayList;

public class AppPref {

    private static final String MyPref = "userPref";
    private static final String LANGUAGE_ENGLISH = "english";
    private static final String LANGUAGE_JAPANESE = "japanese";
    private static final String IS_LOGIN = "isLoggedIn";
    private static final String USER = "user";
    private static final String PURCHASED_COMICS = "purchased_comics";
    private static final String REVIEWED_COMICS = "reviewed_comics";

    public static boolean IsLanguageEnglish(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(LANGUAGE_ENGLISH, false);
    }

    public static void setIsLanguageEnglish(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(LANGUAGE_ENGLISH, z);
        edit.commit();
    }

    public static boolean IsLanguageJapanese(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(LANGUAGE_JAPANESE, false);
    }

    public static void setIsLanguageJapanese(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(LANGUAGE_JAPANESE, z);
        edit.commit();
    }

    public static boolean isLoggedIn(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_LOGIN, false);
    }

    public static void setLoginStatus(Context context, boolean status) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_LOGIN, status);
        edit.commit();
    }

    public static void setUserData(Context context, User user) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        Gson gson = new Gson();
        String userString = gson.toJson(user);
        edit.putString(USER, userString);
        edit.commit();
    }

    public static User getUser(Context context) {
        String userString = context.getApplicationContext().getSharedPreferences(MyPref, 0).getString(USER, "");
        Gson gson = new Gson();
        User user = gson.fromJson(userString, User.class);
        return user;
    }

    public static void setPurchasedComics(Context context, ArrayList<Comic> comics) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        Gson gson = new Gson();
        String userString = gson.toJson(comics);
        edit.putString(PURCHASED_COMICS, userString);
        edit.commit();
    }

    public static ArrayList<Comic> getPurchasedComics(Context context) {
        String userString = context.getApplicationContext().getSharedPreferences(MyPref, 0).getString(PURCHASED_COMICS, "");
        Gson gson = new Gson();
        ArrayList<Comic> comics = gson.fromJson(userString, new TypeToken<ArrayList<Comic>>(){}.getType());
        return comics;
    }

    public static void setMyReviewComics(Context context, ArrayList<Comic> comics) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        Gson gson = new Gson();
        String userString = gson.toJson(comics);
        edit.putString(REVIEWED_COMICS, userString);
        edit.commit();
    }

    public static ArrayList<Comic> getMyReviewComics(Context context) {
        String userString = context.getApplicationContext().getSharedPreferences(MyPref, 0).getString(REVIEWED_COMICS, "");
        Gson gson = new Gson();
        ArrayList<Comic> comics = gson.fromJson(userString, new TypeToken<ArrayList<Comic>>(){}.getType());
        return comics;
    }
}
