package pl.iszala.travelernotebook;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class TravelPoint extends ListActivity{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.points_layout);

		database = (new DatabaseHelper(this).getWritableDatabase());

		/**
		 * Get necessary id from last activity
		 */
		Bundle extras = getIntent().getExtras();
		fk_id_object = extras.get("fk_id");
		fk_id = Integer.parseInt(fk_id_object.toString());
		
		showPoints(fk_id);
		registerForContextMenu(getListView());
		
	}
	
	/**
	 * Creates menu in point menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.point_menu, menu);
		return true;

	}
	
	/**
	 * Steering in menu of point
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.point_new:
			addNewPointDialog();
			return true;
		case R.id.point_back:
			return true;
		}
		return false;
	}
	
	/**
	 * Creates context menu on point list view
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.point_context_menu, menu);
	}
	
	/**
	 * Sterring in context menu
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		switch(item.getItemId()){
		case R.id.show_on_map:
			long id = getListAdapter().getItemId(info.position);
			cursor = database.query(TABLE_NAME, new String[] {"LATITUDE","LOGITUDE"}, null, null, "_id", "_id="+id, null);
			cursor.moveToFirst();
			latitude = cursor.getLong(cursor.getColumnIndex("LATITUDE"));
			logitude = cursor.getLong(cursor.getColumnIndex("LOGITUDE"));
			mapActivity(latitude,logitude);
			return true;
		case R.id.show_transfer:
			transferActivity(getListAdapter().getItemId(info.position));
			return true;
		case R.id.delete_point:
			deletePoint(info.id);
			Toast.makeText(this, R.string.delete_info, Toast.LENGTH_SHORT).show();
			return true;
		}
		return true;
	}
	
	/**
	 * Creates new Activity- list of transfers from selected point
	 */
	public void transferActivity(long fk_id){
		Intent intent = new Intent(TravelPoint.this, Transfers.class);
		intent.putExtra("fk_id", fk_id);
		startActivity(intent);
	}
	
	/**
	 * Start new activity with position
	 * @param latitude point latitude
	 * @param logitude point logitude
	 */
	public void mapActivity(long latitude,long logitude){
		Intent intent = new Intent(TravelPoint.this, Mapy.class);
		intent.putExtra("latitude", latitude);
		intent.putExtra("logitude", logitude);
		startActivity(intent);
	}
	

	
	/**
	 * Creates list with points connected with selected travel
	 * @param travelId id of selected travel
	 */
	private void showPoints(int travelId){
		String resultColumn[] = new String[]{"_id", "NAME", "ADDRESS", "CITY", "COUNTRY", "LATITUDE","LOGITUDE"};
		cursor = database.query(TABLE_NAME, resultColumn, null, null, "_id", "FK_TRAVEL_ID="+fk_id, null);
		ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.point_list_view, cursor, 
													  new String[]{ "NAME", "ADDRESS", "CITY", "COUNTRY"},
													  new int[]{R.id.point_list_name,R.id.point_list_address,R.id.point_list_city,
													  			R.id.point_list_country});
		setListAdapter(adapter);
		
		this.getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long fk_id) {
				transferActivity(fk_id);
				//database.close();
				//cursor.close();
			}
		});
	}
	

	
	/**
	 * Adding new point to our travel
	 * Function using geocoder for forward geocoding
	 * Changing from address to latitude and logitude
	 * @param wrapper
	 */
	private void addNewPoint(PointDialogWrapper wrapper){
		Geocoder gc = new Geocoder(this, Locale.getDefault());
		List<Address> addresses = null;
		String pointAddress = wrapper.getAddress_point()+ ", " + wrapper.getCity_point() + ", " + wrapper.getCountry_point();
		try{
			//Get lat and log from address
			addresses = gc.getFromLocationName(pointAddress, 5);
			Address address = addresses.get(0);
			//Saving into database
			ContentValues values = new ContentValues(8);
			values.put("NAME", wrapper.getName_point());
			values.put("ADDRESS", wrapper.getAddress_point());
			values.put("CITY", wrapper.getCity_point());
			values.put("COUNTRY", wrapper.getCountry_point());
			values.put("LATITUDE", address.getLatitude());
			values.put("LOGITUDE", address.getLongitude());
			values.put("FK_TRAVEL_ID", fk_id);
			database.insert(TABLE_NAME, null, values);
			cursor.requery();
		}
		catch(IOException ex){
			
		}
		
	}
	
	/**
	 * Used to delete point from database
	 * @param point_id ID of point
	 */
	private void deletePoint(long point_id){
		String[] arguments = {String.valueOf(point_id)};
		database.delete(TABLE_NAME, "_id=?", arguments);
		cursor.requery();
	}
	
	/**
	 * Creates new dialog window, used to add new point in trip
	 */
	private void addNewPointDialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View addView = inflater.inflate(R.layout.new_point_dialog, null);
		final PointDialogWrapper wrapper = new PointDialogWrapper(addView);

		new AlertDialog.Builder(this).setTitle(R.string.new_point)
				.setView(addView).setPositiveButton(R.string.save,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								addNewPoint(wrapper);

							}
						}).setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
	}
	
	/**
	 * Creates class with necessary getters and fields for new point dialog
	 * @author Robert
	 *
	 */
	class PointDialogWrapper{
		private EditText name_point;
		private EditText address_point;
		private EditText city_point;
		private EditText country_point;
		
		public PointDialogWrapper(View view){
			name_point = (EditText)view.findViewById(R.id.name_point_dialog);
			address_point = (EditText)view.findViewById(R.id.adres_point_dialog);
			city_point = (EditText)view.findViewById(R.id.city_point_dialog);
			country_point = (EditText)view.findViewById(R.id.country_point_dialog);
		}

		public String getName_point() {
			return name_point.getText().toString();
		}

		public String getAddress_point() {
			return address_point.getText().toString();
		}

		public String getCity_point() {
			return city_point.getText().toString();
		}

		public String getCountry_point() {
			return country_point.getText().toString();
		}
		
		
	}
	
	/**
	 * Private fields
	 */
	private Cursor cursor;
	private SQLiteDatabase database;
	private Object fk_id_object;
	private int fk_id;
	
	private long latitude;
	private long logitude;
	
	private static final String TABLE_NAME = "POINT";
}
