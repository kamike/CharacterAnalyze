package com.fiiipay.wangtao.characteranalyze.utils;

import android.os.Handler;
import android.os.Message;

import java.util.HashMap;

/**
 * 计时工具
 * 
 * @author Administrator
 * 
 */
public class CountTime {
	private static HashMap<String, Long> map;

	public static void countStart(String key) {
		if (map == null) {
			map = new HashMap<String, Long>();
		}
		map.put(key, System.currentTimeMillis());
	}

	public static void countEnd(String key) {
		if (map == null) {
			map = new HashMap<String, Long>();
		}
		Long time = map.get(key);
		if (time != null) {
			LogUtils.logTime(key + ",用时:" + (System.currentTimeMillis() - time)
					+ "毫秒");
		} else {
			LogUtils.logTime("not find this key:" + key);
		}
	}

	private static HashMap<String, Boolean> mapTime;

	/**
	 * 是否超过设置的时间
	 * 
	 * @param key
	 * @return
	 */
	public static boolean isBeyoundTime(final String key, int time) {
		if (mapTime == null) {
			mapTime = new HashMap<String, Boolean>();
		}
		if (!mapTime.containsKey(key)) {
			Handler h = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					mapTime.remove(key);
				}
			};
			h.sendEmptyMessageDelayed(0, time);
			mapTime.put(key, false);
			LogUtils.logTime("not key:true");
			return true;
		}
		LogUtils.logTime("key:" + mapTime.get(key));
		return mapTime.get(key);
	}
}
