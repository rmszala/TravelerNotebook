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

public class Transfers extends ListActivity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transfers_layout);
		
		database = (new DatabaseHelper(this).getWritableDatabase());
		
		Bundle extras = getIntent().getExtras();
		fk_id_object = extras.get("fk_id");
		fk_id = Integer.parseInt(fk_id_object.toString());
		showTransfers(fk_id);
		
		registerForContextMenu(getListView());
	
	}
	
	/**
	 * Creates menu in transfers list view
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.transfer_menu, menu);
		return true;
	}
	
	/**
	 * Steering in menu
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.new_transfer:
			//Toast.makeText(this, "Sezamek", Toast.LENGTH_SHORT).show();
			addNewTransferDialog();
			return true;
		case R.id.transfer_back:
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
		case R.id.new_transfer:
			addNewTransferDialog();
			return true;
		case R.id.delete_point:
			deleteTransfer(info.id);
			Toast.makeText(this, R.string.delete_info, Toast.LENGTH_SHORT).show();
			return true;
		}
		return true;
	}
	
	/**
	 * Creates list of transfers to next point for selected point
	 * @param fk_id id of selected point
	 */
	public void showTransfers(long pointId){
		String resultColumn[] = new String[]{"_id", "NOTE", "ADDRESS", "CITY", "COUNTRY", "TIME","DATE","TRANSFER_TYPE"};
		cursor = database.query(TABLE_NAME, resultColumn, null, null, "_id", "FK_POINT_ID=" + fk_id, null);
		ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.transfers_list_view, cursor, 
													  new String[]{ "NOTE", "ADDRESS", "CITY", "COUNTRY", "TIME", "DATE", "TRANSFER_TYPE"},
													  new int[]{R.id.transfer_list_note,R.id.transfer_list_address,R.id.transfer_list_city,
																R.id.transfer_list_country,R.id.transfer_list_time,R.id.transfer_list_date,
																R.id.transfer_list_transfer_type});
		setListAdapter(adapter);
		
		this.getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long fk_id) {
				//transferActivity(fk_id);
				//database.close();
				//cursor.close();
			}
		});
	}
	
	/**
	 * Creates dialog used to add new transfer
	 */
	private void addNewTransferDialog(){
		//TODO zaimplementowac dodawanie nowego transferu do bazy danych
		LayoutInflater inflater = LayoutInflater.from(this);
		View addView = inflater.inflate(R.layout.new_transfer_dialog, null);
		final NewTransferWrapper wrapper = new NewTransferWrapper(addView);
		
		new AlertDialog.Builder(this).setTitle(R.string.new_tranfer)
		.setView(addView).setPositiveButton(R.string.save,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						addNewTranfer(wrapper);

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
	 * Using for deleting the transfer in table
	 * @param transfer_id indentyfier of position which we want to delete
	 */
	public void deleteTransfer(long transfer_id){
		String[] arguments = {String.valueOf(transfer_id)};
		database.delete(TABLE_NAME, "_id=?", arguments);
		cursor.requery();
	}
	
	/**
	 * Creates new map activity with transfer position
	 * @param latitude
	 * @param logitude
	 */
	public void mapActivity(long latitude,long logitude){
		Intent intent = new Intent(Transfers.this, Mapy.class);
		intent.putExtra("latitude", latitude);
		intent.putExtra("logitude", logitude);
		startActivity(intent);
	}
	
	
	/**
	 * Used to add new transfer in database
	 * @param wrapper
	 */
	public void addNewTranfer(NewTransferWrapper wrapper){
		//TODO zaimplementowac dodawanie nowego transferu razem z dialogiem
		Geocoder gc = new Geocoder(this, Locale.getDefault());
		List<Address> addresses = null;
		String transferAddress = wrapper.getAddress() + ", " + wrapper.getCity() + ", " + wrapper.getCountry();
		try{
			addresses = gc.getFromLocationName(transferAddress, 2);
			Address address = addresses.get(0);
			ContentValues values = new ContentValues(10);
			values.put("NOTE", wrapper.getNote());
			values.put("TIME", wrapper.getTime());
			values.put("DATE", wrapper.getDate());
			values.put("ADDRESS", wrapper.getAddress());
			values.put("CITY", wrapper.getCity());
			values.put("COUNTRY", wrapper.getCountry());
			values.put("LATITUDE", address.getLatitude());
			values.put("LOGITUDE", address.getLongitude());
			//TODO zaimplementowac dodawanie rodzaju transferu
			values.put("FK_POINT_ID", fk_id);
			database.insert(TABLE_NAME, null, values);
			cursor.requery();
		}
		catch(IOException ex){
			
		}
	}

	
	class NewTransferWrapper{
		public NewTransferWrapper(View view){
			note = (EditText)view.findViewById(R.id.note_transfer_dialog);
			time = (EditText)view.findViewById(R.id.time_transfer_dialog);
			date = (EditText)view.findViewById(R.id.date_transfer_dialog);
			address = (EditText)view.findViewById(R.id.address_transfer_dialog);
			city = (EditText)view.findViewById(R.id.city_transfer_dialog);
			country = (EditText)view.findViewById(R.id.country_transfer_dialog);
		}
				
		public String getNote() {
			return note.getText().toString();
		}
		public String getTime() {
			return time.getText().toString();
		}
		public String getDate() {
			return date.getText().toString();
		}
		public String getAddress() {
			return address.getText().toString();
		}
		public String getCity() {
			return city.getText().toString();
		}
		public String getCountry() {
			return country.getText().toString();
		}
		
		private EditText note;
		private EditText time;
		private EditText date;
		private EditText address;
		private EditText city;
		private EditText country;
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
	private static final String TABLE_NAME = "TRANSFER";
}
