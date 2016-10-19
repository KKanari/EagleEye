package kms2.EagleEye;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class CustomOverlay extends Overlay{
	private static Bitmap sMyIcon;
	private static int sMyOffsetX;
	private static int sMyOffsetY;
	private static Bitmap sIcon;
	private static int sOffsetX;
	private static int sOffsetY;
	private static GeoPoint sPoint = null;

	private GeoPoint mMyPoint;

	CustomOverlay(Bitmap inMyIcon, Bitmap inIcon, GeoPoint myInitial) {
		sMyIcon = inMyIcon;
		sMyOffsetX = 0 - sMyIcon.getWidth() / 2;
		sMyOffsetY = 0 - sMyIcon.getHeight();
		sIcon = inIcon;
		sOffsetX = 0 - sIcon.getWidth() / 2;
		sOffsetY = 0 - sIcon.getHeight();
		mMyPoint = myInitial;
		
		clearPoint();
	}

	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		setPoint(point);
		MapForm.setCustomZoom(mMyPoint, sPoint);
		MapForm.showsConfirmationLayout();
		
		return super.onTap(point, mapView);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView,
			boolean shadow) {
		super.draw(canvas, mapView, shadow);
		if (shadow) {
			// 地図上の場所と、描画用のCanvasの座標の変換
			Projection projection = mapView.getProjection();
			Point myPoint = new Point();
			projection.toPixels(mMyPoint, myPoint);
			myPoint.offset(sMyOffsetX, sMyOffsetY);

			// アイコンを描画
			canvas.drawBitmap(sMyIcon, myPoint.x, myPoint.y, null);
		}else{
			if(sPoint != null){
				Projection projection = mapView.getProjection();
				Point bPoint = new Point();
				projection.toPixels(sPoint, bPoint);
				bPoint.offset(sOffsetX, sOffsetY);

				canvas.drawBitmap(sIcon, bPoint.x, bPoint.y, null);
			}
		}
	}
	
	public static void setPoint(GeoPoint point){
		if(sPoint == null){
			// 確認タブ表示
			MapForm.showsSeachLayout();
		}

		// 位置記録
		sPoint = point;

		// 中心移動
		MapForm.mapCtrl.animateTo(sPoint);

		//　位置表示
		MapForm.showPointData(sPoint);
	}
	
	public static void clearPoint(){
		sPoint = null;
	}
};