package com.thinktubekorea.menu.Preferences;


import android.content.Context;
import android.content.SharedPreferences;

/*
 * 환경설정 정보 저장
 */
public class CommonPreferences {

	public static String PRE_NAME = "thinktube_menu";
	public static String IS_FIRST = "is_first";


	/*
	 * 문자열 저장
	 */
	public static boolean getIsFirst(Context context){
		return context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE).getBoolean(IS_FIRST, true);
	}
	public static void setBooleanPreferences(Context context,String preferencesKey, boolean val){
		SharedPreferences prefs = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(preferencesKey, val);
		editor.commit();
	}
}
