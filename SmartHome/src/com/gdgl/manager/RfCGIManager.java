package com.gdgl.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.gdgl.app.ApplicationController;
import com.gdgl.model.DevicesModel;
import com.gdgl.mydata.DataHelper;
import com.gdgl.mydata.Event;
import com.gdgl.mydata.EventType;
import com.gdgl.mydata.RespondDataEntity;
import com.gdgl.mydata.ResponseParamsEndPoint;
import com.gdgl.network.StringRequestChina;
import com.gdgl.network.VolleyErrorHelper;
import com.gdgl.network.VolleyOperation;
import com.gdgl.util.NetUtil;
import com.gdgl.util.UiUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RfCGIManager extends Manger{
	private final static String TAG = "RfCGIManager";

	private static RfCGIManager instance;

	public static RfCGIManager getInstance() {
		if (instance == null) {
			instance = new RfCGIManager();
		}
		return instance;
	}
	
	public void GetRFDevList() {
		String url = NetUtil.getInstance().getVideoURL(
				NetUtil.getInstance().IP, "GetRFDevList.cgi");
		StringRequestChina req = new StringRequestChina(url,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						new GetRFDevListTask().execute(response);
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						// TODO Auto-generated method stub

					}
				});
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	
	public void ChangeRFDevName(String rfid, String name) {
		HashMap<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("rfid", rfid);
		paraMap.put("name", name);
		String param = hashMap2ParamString(paraMap);

		String url = NetUtil.getInstance().getCumstomURL(
				NetUtil.getInstance().IP, "ChangeRFDevName.cgi", param);
		StringRequest req = new StringRequest(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						
					}
				});
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	
	//===20150818王晓飞===RF设备布撤防
	public void ChangeRFDevArmState(String rfid, int state) {
		HashMap<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("rfid", rfid);
		paraMap.put("state", state+"");
		String param = hashMap2ParamString(paraMap);

		String url = NetUtil.getInstance().getCumstomURL(
				NetUtil.getInstance().IP, "ChangeRFDevArmState.cgi", param);
		StringRequest req = new StringRequest(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String arg0) {
						// TODO Auto-generated method stub
					}
					
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						
					}
				});
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	//====开始停止RF警号报警
	public void RFWarningDevOperation(String rfid, int operatortype,int param1) {
		HashMap<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("rfid", rfid);
		paraMap.put("operatortype", operatortype+"");
		paraMap.put("param1", param1+"");
		String param = hashMap2ParamString(paraMap);

		String url = NetUtil.getInstance().getCumstomURL(
				NetUtil.getInstance().IP, "RFWarningDevOperation.cgi", param);
		Log.i(TAG, url);
		StringRequest req = new StringRequest(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String arg0) {
						// TODO Auto-generated method stub
					}
					
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						
					}
				});
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	//====RF设备启用禁止配置
	public void ChangeRFDevActivationState(String rfid, int state) {
		HashMap<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("rfid", rfid);
		paraMap.put("state", state+"");
		String param = hashMap2ParamString(paraMap);

		String url = NetUtil.getInstance().getCumstomURL(
				NetUtil.getInstance().IP, "ChangeRFDevActivationState.cgi", param);
		StringRequest req = new StringRequest(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String arg0) {
						// TODO Auto-generated method stub
					}
					
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						
					}
				});
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	//===RF设备全部布防撤防
	public void ChangeAllRFDevArmState(int state) {
		HashMap<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("state", state+"");
		String param = hashMap2ParamString(paraMap);

		String url = NetUtil.getInstance().getCumstomURL(
				NetUtil.getInstance().IP, "ChangeAllRFDevArmState.cgi", param);
		StringRequest req = new StringRequest(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String arg0) {
						// TODO Auto-generated method stub
					}
					
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						
					}
				});
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	
	public void ModifyRFDevRoomId(DevicesModel model, String new_roomid) {
		HashMap<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("rfid", model.getmIeee());
		paraMap.put("new_roomid", new_roomid);
		
		String param = hashMap2ParamString(paraMap);

		String url = NetUtil.getInstance().getCumstomURL(
				NetUtil.getInstance().IP, "ModifyRFDevRoomId.cgi", param);

		StringRequest req = new StringRequest(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						response = UiUtils.formatResponseString(response);
						JsonParser parser = new JsonParser();
						JsonObject jsonObject = parser.parse(response)
								.getAsJsonObject();
						int status = jsonObject.get("status").getAsInt();
						Event event = new Event(EventType.MODIFYDEVICEROOMID,
								true);
						event.setData(status);
						notifyObservers(event);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						String errorString = null;
						if (error != null && error.getMessage() != null) {
							VolleyLog.e("Error: ", error.getMessage());
							errorString = VolleyErrorHelper.getMessage(error,
									ApplicationController.getInstance());
						}
						Event event = new Event(EventType.MODIFYDEVICEROOMID,
								false);
						event.setData(errorString);
						notifyObservers(event);
					}
				});
		// add the request object to the queue to be executed
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	
	public void GetRFDevByRoomId(String rid) {
		HashMap<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("roomid", rid);

		String param = hashMap2ParamString(paraMap);

		String url = NetUtil.getInstance().getCumstomURL(
				NetUtil.getInstance().IP, "GetRFDevByRoomId.cgi", param);
		Log.i("GetRFDevByRoomId", url);
		StringRequestChina req = new StringRequestChina(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						new GetRFDevByRoomIdTask().execute(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		// add the request object to the queue to be executed
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	public void GetRFDevByRoomIdInit(String rid) {
		HashMap<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("roomid", rid);

		String param = hashMap2ParamString(paraMap);

		String url = NetUtil.getInstance().getCumstomURL(
				NetUtil.getInstance().IP, "GetRFDevByRoomId.cgi", param);

		StringRequestChina req = new StringRequestChina(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						new GetRFDevByRoomIdInitTask().execute(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});
		// add the request object to the queue to be executed
		ApplicationController.getInstance().addToRequestQueue(req);
	}
	
	class GetRFDevListTask extends AsyncTask<String, Object, Object> {
		@Override
		protected Object doInBackground(String... params) {
			RespondDataEntity<ResponseParamsEndPoint> data = VolleyOperation
					.handleEndPointString(params[0]);
			ArrayList<ResponseParamsEndPoint> devDataList = data
					.getResponseparamList();

			DataHelper mDateHelper = new DataHelper(
					ApplicationController.getInstance());
			SQLiteDatabase mSQLiteDatabase = mDateHelper.getSQLiteDatabase();

			mDateHelper.emptyTable(mSQLiteDatabase,DataHelper.RF_DEVICES_TABLE);
			mDateHelper.insertEndPointList(mSQLiteDatabase,DataHelper.RF_DEVICES_TABLE, null, devDataList);
			mSQLiteDatabase.close();
			
			Event event = new Event(EventType.RF_DEVICE_LIST_UPDATE, true);
			notifyObservers(event);
			return devDataList;
		}
	}
	
	class GetRFDevByRoomIdTask extends AsyncTask<String, Object, Object> {
		@Override
		protected Object doInBackground(String... params) {
			RespondDataEntity<ResponseParamsEndPoint> data = VolleyOperation
					.handleEndPointString(params[0]);
			ArrayList<ResponseParamsEndPoint> devDataList = data
					.getResponseparamList();
			List<DevicesModel> mDevicesList = DataHelper
					.convertToDevicesModel(devDataList);
//			DataHelper mDateHelper = new DataHelper(
//					ApplicationController.getInstance());
//			SQLiteDatabase mSQLiteDatabase = mDateHelper.getSQLiteDatabase();
//
//			for (DevicesModel mDevices : mDevicesList) {
//				ContentValues c = new ContentValues();
//				c.put(DevicesModel.R_ID, mDevices.getmRid());
//				mDateHelper.update(mSQLiteDatabase, DataHelper.DEVICES_TABLE,
//						c, " ieee=? ", new String[] { mDevices.getmIeee() });
//			}
//			mSQLiteDatabase.close();
			return mDevicesList;
		}

		@Override
		protected void onPostExecute(Object result) {
			Event event = new Event(EventType.RF_GETEPBYROOMINDEX, true);
			event.setData(result);
			notifyObservers(event);
		}

	}
	class GetRFDevByRoomIdInitTask extends AsyncTask<String, Object, Object> {
		@Override
		protected Object doInBackground(String... params) {
			RespondDataEntity<ResponseParamsEndPoint> data = VolleyOperation
					.handleEndPointString(params[0]);
			ArrayList<ResponseParamsEndPoint> devDataList = data
					.getResponseparamList();
			List<DevicesModel> mDevicesList = DataHelper
					.convertToDevicesModel(devDataList);
//			DataHelper mDateHelper = new DataHelper(
//					ApplicationController.getInstance());
//			SQLiteDatabase mSQLiteDatabase = mDateHelper.getSQLiteDatabase();
//
//			for (DevicesModel mDevices : mDevicesList) {
//				ContentValues c = new ContentValues();
//				c.put(DevicesModel.R_ID, mDevices.getmRid());
//				mDateHelper.update(mSQLiteDatabase, DataHelper.DEVICES_TABLE,
//						c, " ieee=? ", new String[] { mDevices.getmIeee() });
//			}
//			mSQLiteDatabase.close();
			return mDevicesList;
		}

		@Override
		protected void onPostExecute(Object result) {
			Event event = new Event(EventType.RF_GETEPBYROOMINDEXINIT, true);
			event.setData(result);
			notifyObservers(event);
		}

	}
}
