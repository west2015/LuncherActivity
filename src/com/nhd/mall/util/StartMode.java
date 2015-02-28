package com.nhd.mall.util;

/**
 * 界面跳转动画模式
 */
public class StartMode {
	/**
	 * 原界面不动，新建页面向上滑动
	 */
	public static int ON_NEXT_ON = 1;
	/**
	 * 原界面不动，新建界面向下滑动
	 */
	public static int NEXT_ON_NEXT = 2;
	/**
	 * 原界面不动，新建界面向左滑动
	 */
	public static int LEFT_RIGHT_LEFT = 3;
	/**
	 * 原界面不动，新建界面向右滑动
	 */
	public static int RIGHT_LEFT_RIGHT = 4;		
	/**
	 * 原界面和新建界面同时向左滑动
	 */
	public static int LEFT_RIGHT = 5;
	/**
	 * 原界面和新建界面同时向右滑动
	 */
	public static int RIGHT_LEFT = 6;
}
