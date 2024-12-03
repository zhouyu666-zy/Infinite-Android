package edu.ace.infinite.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityManager {
    //存储Activity的List
    public static List<Activity> activities = new ArrayList<>();
    private static final ActivityManager instance = new ActivityManager();
    public static synchronized ActivityManager getInstance() {
        return instance;
    }

    //添加Activity
    public void addActivity(Activity activity) {
        if(activity != null){
            activities.add(activity);
        }
    }


    /**
     * 获取当前Activity
     */
    public Activity getCurrActivity(){
        if(activities.size() == 0){
            return null;
        }
        return activities.get(activities.size()-1);
    }

    //移出Activity
    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * 获取指定activity
     * @param specifyActivity 指定activity
     */
    public Activity getSpecifyActivity(Class<?> specifyActivity){
        try {
            for (int i = 0; i < activities.size(); i++) {
                Activity activity = activities.get(i);
                if(activity.getClass().equals(specifyActivity)){
                    return activity;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 仅保留一个activity
     * @param thisActivity 要保留的activity
     */
    public void KeepOneActivity(Class<?>  thisActivity){
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            if(activity.getClass() != thisActivity){
                activity.finish();
            }
        }
    }

}
