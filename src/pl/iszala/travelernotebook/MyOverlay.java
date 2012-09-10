package pl.iszala.travelernotebook;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyOverlay extends ItemizedOverlay<OverlayItem> {
	public MyOverlay(Drawable marker) {
		super(boundCenterBottom(marker));
		GeoPoint geoPoint = null;
		String title = null;
	}

	/**
	 * Creates customized overlay item
	 */
	@Override
	protected OverlayItem createItem(int i) {
		return new OverlayItem(geoPoint, title, null);
	}

	/**
	 * Returns size of MyOverlay
	 */
	@Override
	public int size() {
		return 1;
	}
	
	public void myPopulate(){
		populate();
	}
	
	GeoPoint geoPoint;
	String title;

}
