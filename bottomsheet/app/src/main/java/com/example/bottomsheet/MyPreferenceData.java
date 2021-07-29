package com.example.bottomsheet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.json.JSONArray;

import kotlin.jvm.JvmField;

@SuppressLint("CommitPrefEdits")
public class MyPreferenceData {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public MyPreferenceData(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public void commit() {
        editor.commit();
    }

    // 마이페이지 편집
    private final String MYPAGE_ORDER = "MYPAGE_ORDER";

    /**
     * BottomEditMyPageTest 의 (kotlin)
     * data = MyPreferenceData(view.context)
     * orderList = data?.mypageOder
     * data?.mypageOder = this
     * 으로 sharedPreference 에서 데이터 저장한 것 불러오고, 또, 데이터 다시 저장하기도 함
     */
    public void setMypageOder(JSONArray array) {
        try {
            editor.putString(MYPAGE_ORDER, array.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONArray getMypageOder() {
        JSONArray array = new JSONArray();
        try {
            String str = preferences.getString(MYPAGE_ORDER, "[{\"code\":\"CARD\",\"display\":\"Y\",\"name\":\"카드 홈\",\"order\":1},{\"code\":\"WALLET\",\"display\":\"Y\",\"name\":\"월렛\",\"order\":2},{\"code\":\"ASSET\",\"display\":\"Y\",\"name\":\"자산\",\"order\":3},{\"code\":\"DISCOVER\",\"display\":\"Y\",\"name\":\"디스커버\",\"order\":4},{\"code\":\"TIMELINE\",\"display\":\"Y\",\"name\":\"알림\",\"order\":5}]\n");
            array = new JSONArray(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

}