package com.intel.samples.historicalcontext.offline;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


import com.intel.context.Historical;
import com.intel.context.historical.Page;
import com.intel.context.historical.QueryOption;
import com.intel.context.item.Item;
import com.intel.context.item.LocationCurrent;
import com.intel.context.exception.ContextException;

public class OfflineSampleActivity extends Activity {
	private final static String LOG_TAG = OfflineSampleActivity.class
			.getName();
	private final static double LATITUDE = 38;
	private final static double LONGITUDE = -121;
	private Button setHistoricalButton;

	private Button getHistoricalButton;
	private Historical mHistorical;
	private double i = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mHistorical = new Historical(getApplicationContext());
		configureUI();
	}

	public void onDestroy() {
		super.onDestroy();
	}

	private void configureUI() {

		getHistoricalButton = (Button) findViewById(R.id.getHistoricalButton);
		getHistoricalButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getItem();
			}
		});

		setHistoricalButton = (Button) findViewById(R.id.setHistoricalButton);
		setHistoricalButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setItem();
			}
		});

	}

	private void getItem() {

		QueryOption query = new QueryOption();

		try {
			Page mPage = mHistorical.getItem(query);
			StringBuilder output = new StringBuilder("Received Items\n");
			for (Item item : mPage.getItems()) {
				LocationCurrent mItem = (LocationCurrent) item;
				output.append("Lat: " + mItem.getLocation().getLatitude()
						+ " - Long: " + mItem.getLocation().getLongitude()
						+ " - Date: " + mItem.getDateTime() + "\n");
			}
			Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG)
					.show();
			Log.i(LOG_TAG, output.toString());

		} catch (ContextException e) {

			e.printStackTrace();
			Log.e(LOG_TAG, "Error retrieving Context data :  " + e.getMessage());
		} catch (JSONException e) {

			e.printStackTrace();
			Log.e(LOG_TAG, "Error Parsing Json data :  " + e.getMessage());
		}
	}

	private void setItem() {
		LocationCurrent mCurrentLocation = new LocationCurrent();
		Location mLocation = new Location("noProvider");

		mLocation.setLatitude(LATITUDE + i);
		mLocation.setLongitude(LONGITUDE + i);
		i++;
		mCurrentLocation.setLocation(mLocation);
		mCurrentLocation.setAccuracy(50);
		List<Item> items = new ArrayList<Item>();
		items.add(mCurrentLocation);

		try {
			mHistorical.setItem(items);
			Toast.makeText(
					getApplicationContext(),
					"Location Pushed successfully!\nLat: "
							+ mLocation.getLatitude() + " - Long: "
							+ mLocation.getLongitude(), Toast.LENGTH_SHORT)
					.show();
			Log.i(LOG_TAG,
					"Location Pushed successfully! Lat: "
							+ mLocation.getLatitude() + " - Long: "
							+ mLocation.getLongitude());
		} catch (ContextException e) {

			Log.e(LOG_TAG, "Error pushing Context data :  " + e.getMessage());
			e.printStackTrace();
		}

	}

}