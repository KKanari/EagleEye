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
			// �n�}��̏ꏊ�ƁA�`��p��Canvas�̍��W�̕ϊ�
			Projection projection = mapView.getProjection();
			Point myPoint = new Point();
			projection.toPixels(mMyPoint, myPoint);
			myPoint.offset(sMyOffsetX, sMyOffsetY);

			// �A�C�R����`��
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
			// �m�F�^�u�\��
			MapForm.showsSeachLayout();
		}

		// �ʒu�L�^
		sPoint = point;

		// ���S�ړ�
		MapForm.mapCtrl.animateTo(sPoint);

		//�@�ʒu�\��
		MapForm.showPointData(sPoint);
	}
	
	public static void clearPoint(){
		sPoint = null;
	}
};