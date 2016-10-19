/**
 * Copyright (c) 2007 KMS2 Co.,Ltd.All Rights Reserved.
 */

package kms2.EagleEye;

import android.graphics.drawable.BitmapDrawable;

/**
 * 目的地までの距離・方向保持クラス
 * 
 * @version 1.0, 4 Jan, 2010
 * @author kurisu
 */
public class ResultValue {
	private static String sPrintDistance;
	private static String sPrintDirection;
	private static double sDistance;
	private static double sDirection;
	private static BitmapDrawable sDirectionArrow;
	
	public static String getsPrintDistance() {
		return sPrintDistance;
	}
	public static String getsPrintDirection() {
		return sPrintDirection;
	}
	public static double getsDistance() {
		return sDistance;
	}
	public static double getsDirection() {
		return sDirection;
	}
	public static BitmapDrawable getsDirectionArrow() {
		return sDirectionArrow;
	}
	public static void setsPrintDistance(String sPrintDistance) {
		ResultValue.sPrintDistance = sPrintDistance;
	}
	public static void setsPrintDirection(String sPrintDirection) {
		ResultValue.sPrintDirection = sPrintDirection;
	}
	public static void setsDistance(double sDistance) {
		ResultValue.sDistance = sDistance;
	}
	public static void setsDirection(double sDirection) {
		ResultValue.sDirection = sDirection;
	}
	public static void setsDirectionArrow(BitmapDrawable sDirectionArrow) {
		ResultValue.sDirectionArrow = sDirectionArrow;
	}
	
}
