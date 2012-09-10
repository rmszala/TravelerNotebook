package pl.iszala.travelernotebook;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class Mapy extends MapActivity{

	/**
	 * Called when map activity is created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);
		
		MapView mapview = (MapView)findViewById(R.id.mapview);
		mapview.setBuiltInZoomControls(true);
		mapController = mapview.getController();
		
		Bundle extras = getIntent().getExtras();
		long logitude = extras.getLong("logitude");
		long latitude = extras.getLong("latitude");
		
		
		
		/**
		 * This part should show my localization but it does't work on
		 * emaluator. Any idea why?
		 */
        List<Overlay> overlays = mapview.getOverlays();
        myLocationOverlay = new MyLocationOverlay(this,mapview);
	    myLocationOverlay.enableCompass();
	    boolean ok = myLocationOverlay.enableMyLocation();
	    if (!ok) Toast.makeText(getBaseContext(), "No provider for location!", Toast.LENGTH_SHORT).show();
	    myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                mapController.animateTo(myLocationOverlay.getMyLocation());
            }
        });
	    overlays.add(myLocationOverlay);
	    
	    MyOverlay position = new MyOverlay(getResources().getDrawable(R.drawable.marker_red));
	    GeoPoint geoPoint = new GeoPoint((int)(latitude*1E6), (int)(logitude*1E6));
	    position.geoPoint=geoPoint;
	    position.myPopulate();
	    overlays.add(position);
		
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/**
	 * Private fields
	 */
	private MapController mapController;
	private LocationManager locationManager;
	private MyLocationOverlay myLocationOverlay;
}
