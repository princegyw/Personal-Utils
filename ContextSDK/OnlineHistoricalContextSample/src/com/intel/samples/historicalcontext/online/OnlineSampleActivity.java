package com.intel.samples.historicalcontext.online;

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

import com.intel.context.Auth;
import com.intel.context.Historical;
import com.intel.context.historical.Page;
import com.intel.context.historical.QueryOption;
import com.intel.context.item.Item;
import com.intel.context.item.LocationCurrent;
import com.intel.context.historical.filter.ANDCondition;
import com.intel.context.historical.filter.Expression;
import com.intel.context.historical.filter.Expression.Operator;
import com.intel.context.historical.filter.FilterQuery;
import com.intel.context.auth.AuthCallback;
import com.intel.context.error.ContextError;
import com.intel.context.exception.ContextException;

public class OnlineSampleActivity extends Activity {
	private final static String LOG_TAG = OnlineSampleActivity.class.getName();
	private final static double LATITUDE = 38;
	private final static double LONGITUDE = -121;
	private final static long A_DAY_IN_MILLISECONDS = 86400000;
	private Button authorizeButton;
	private Button setHistoricalButton;
	private Button getHistoricalButton;
	private Auth auth;
	private Historical mHistorical;
	private double i = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		auth = Auth.getInstance(this);
		mHistorical = new Historical(getApplicationContext());
		configureUI();
	}

	public void onDestroy() {
		super.onDestroy();
	}

	private void configureUI() {
		authorizeButton = (Button) findViewById(R.id.authorizeButton);
		authorizeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				authorize();
			}
		});

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
	
	private void authorize() {      
        if (!auth.isInit()) {
        	auth.init(Settings.API_KEY, Settings.SECRET, Settings.REDIRECT_URI, "user:details context:location:detailed context:post:location:detailed", new MyAuthCallback());
        } else {
            Toast.makeText(getApplicationContext(), "Already Authorized", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Already Authorized");
        }
    }

	private void getItem() {
		QueryOption query = new QueryOption();
		Expression expression1 = new Expression();
		expression1.setAttrOperValue("contextType", Operator.EQUAL,
				"urn:x-intel:context:type:location:current");

		LocationCurrent temp = new LocationCurrent();
		temp.setTimestamp(temp.getTimestamp() - A_DAY_IN_MILLISECONDS);

		Expression expression2 = new Expression();
		expression2.setAttrOperValue("clientCreatedTime",
				Operator.GREATER_EQUAL, "ISODate(" + temp.getDateTime() + ")");

		ANDCondition andCondition = new ANDCondition();
		andCondition.addExpression(expression1);
		andCondition.addExpression(expression2);

		FilterQuery filterQuery = new FilterQuery(andCondition);

		query.setFilter(filterQuery);

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
	
	private class MyAuthCallback implements AuthCallback {

        @Override
        public void onSuccess() {
            Toast.makeText(getApplicationContext(), "Authorization Success", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Authorization Success");
        }

        @Override
        public void onError(ContextError error) {
            String msg = "Authorization Error: " + error.getMessage();
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            Log.i(LOG_TAG, msg);
        }

        @Override
        public void onExpired() {
            // To be implemented in future versions             
        }
    }
}