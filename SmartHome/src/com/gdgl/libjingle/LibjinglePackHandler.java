/*******************************************************************************
  Copyright (C), 2009-2014 GuangdongGuanglian Electronic Technology Co.,Ltd.
  FileName:      CIPHandler.java
  Author:        椹檽妲�
  Version :      1.1   
  Date:          2014-11-23
  Description:   瀹炵幇浜戞帴鍙ｅ崗璁皝鍖呫�佽В鍖呭鐞嗐��
  鐗瑰埆璇存槑锛�
  1. reqId鍙栧�艰寖鍥达細0锝�65535
  2. jid鏍煎紡锛� <account>@<server_ip>/<resource_id>
      鍏朵腑<account>蹇呴』鍏ㄩ儴灏忓啓锛�<resource_id>蹇呴』娣诲姞鈥淐鈥濅綔涓哄墠缂�锛屽悎娉曠殑绀轰緥濡備笅锛�
      bc6a2987d431@121.199.21.14/C08002786F712
      guanglian@121.199.21.14/Cd16ff382-d4f3-43b1-91bc-afadd21bb143
  3. chId鍙栧�艰寖鍥达細0锝�9
  4. 瀵逛簬鍝嶅簲鏁版嵁鍖咃紝鍙湁褰� status > 0 鏃讹紝other涓墠鏈夋暟鎹��
  History:        
      <author>    <time>    <version>    <desc>
      椹檽妲�		2014-11-23	   1.1		澧炲姞缃戠粶鐘舵�佹秷鎭被鍨嬶紙gl_msgtype = 14锛�
*******************************************************************************/
package com.gdgl.libjingle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import org.json.*;

import android.content.Context;

public class LibjinglePackHandler  {
	
	public int request_id;
	public int gl_status;
	public int gl_msgtype;
	public int status;
	public String jid;
	public Map<String, Object> other;
	
	final public static int MT_URL 			= 3;
	final public static int MT_CallBack 	= 5;
	final public static int MT_IpcVideo 	= 6;
	final public static int MT_CaHeartBeat 	= 8;
	final public static int MT_IpcStat 		= 9;
	final public static int MT_NetStat		= 14;
	
	public enum IpcVideoOpt { IVO_START, IVO_STOP }
	
	public LibjinglePackHandler(String json) throws JSONException 
	{
		JSONObject root = new JSONObject(json);
		this.other = new HashMap<String, Object>();
		
		if (root.has("gl_status"))
		{
			this.gl_status = root.getInt("gl_status");
		}
		else
		{
			this.gl_status = 0;
		}
		
		if (this.gl_status >= 0)
		{
			this.request_id = root.getInt("request_id");
			this.gl_msgtype = root.getInt("gl_msgtype");
			this.jid = root.getString("jid");
			
			JSONObject sub;
			
			if (this.gl_status > 0)
			{
				sub = root.getJSONObject("response");
				this.status = sub.getInt("status");
				
				switch (this.gl_msgtype)
				{
					case MT_URL:
						if (this.status > 0)
						{
							this.other.put("result", getJsonSubStr(sub.getString("result")));
						}
						break;
					case MT_CaHeartBeat:
						this.other.put("heartbeat", sub.getInt("heartbeat"));
						this.other.put("status_msg", sub.getString("status_msg"));
						break;
					case MT_IpcStat:
						if (this.status > 0)
						{
							this.other.put("onlist", sub.getString("onlist"));
						}
						break;
					default: break;
				}
			}
			else // gl_status = 0
			{
				sub = root.getJSONObject("send");
				this.status = 0;
				
				switch (this.gl_msgtype)
				{
					case MT_CallBack:
						this.other.put("callback", sub.getString("callback"));
						break;
					case MT_NetStat:
						this.other.put("netstat", sub.getInt("netstat"));
						break;
					default: break;
				}
			}
		}
		else
		{
			this.request_id = -1;
			this.gl_msgtype = -1;
			this.status = -1;
		}
	}
	
	/**************************************************************************/
	
	public static String getJsonSubStr(String s)
	{
		int start = s.indexOf('{');
		int end = s.lastIndexOf('}');
		return s.substring(start, end + 1);
	}
	
	/**************************************************************************/
	
	public static String createJid(String user, String server, String uuid)
	{
		return user.toLowerCase() + "@" + server + "/C" + uuid;
	}
	
	/**************************************************************************/
	
    public synchronized static String getUUID(Context context) 
    {
    	String uuid = "";
    	String uuidFileName = "gl-uuid";
        final File uuidFile = new File(context.getFilesDir(), uuidFileName);
        try 
        {
            if (!uuidFile.exists()) 
            {
                createUuidFile(uuidFile);
            }
            uuid = readUuidFile(uuidFile);
        } 
        catch (IOException e) 
        {
            return "";
        } 
        catch (RuntimeException e) 
        {
            return "";
        }
        return uuid;
    }

    private static String readUuidFile(File uuidFile) throws IOException 
    {
    	final RandomAccessFile f = new RandomAccessFile(uuidFile, "r");
    	final byte[] bytes = new byte[(int) f.length()];
    	try 
    	{
    		f.readFully(bytes);
    	} 
    	finally 
    	{
    		f.close();
    	}
    	return new String(bytes);
    }

    private static void createUuidFile(File uuidFile) throws IOException 
    {
    	final FileOutputStream out = new FileOutputStream(uuidFile);
    	try 
    	{
    		final String id = UUID.randomUUID().toString();
    		out.write(id.getBytes());
    	} 
    	finally 
    	{
    		out.close();
    	}
    }
	
	/**************************************************************************/

	public static String packUrl(int reqId, String jid, 
			String url)
	{
		try
		{			
			if (reqId < 0 || reqId > 65535)
			{
				return "";
			}
			
			JSONObject root = new JSONObject();
			JSONObject send = new JSONObject();
			send.put("url", url);
			root.put("request_id", reqId);
			root.put("gl_msgtype", MT_URL);
			root.put("jid", jid);
			root.put("send", send);
			return root.toString();
		} 
		catch(JSONException ex) 
		{
			return "";
		}
	}
	
	public static String packCaHeartBeat(int reqId, String jid, 
			 String passwd)
	{
		try
		{
			if (reqId < 0 || reqId > 65535)
			{
				return "";
			}
			
			JSONObject root = new JSONObject();
			JSONObject send = new JSONObject();
			send.put("heartbeat", 1);
			send.put("password", passwd);
			root.put("request_id", reqId);
			root.put("gl_msgtype", MT_CaHeartBeat);
			root.put("jid", jid);
			root.put("send", send);
			return root.toString();
		} 
		catch(JSONException ex) 
		{
			return "";
		}
	}
	
	public static String packCaHeartBeat(int reqId, String jid, 
			 String passwd, String alias)
	{
		try
		{
			if (reqId < 0 || reqId > 65535)
			{
				return "";
			}
			
			JSONObject root = new JSONObject();
			JSONObject send = new JSONObject();
			send.put("heartbeat", 2);
			send.put("password", passwd);
			send.put("alias", alias);
			root.put("request_id", reqId);
			root.put("gl_msgtype", MT_CaHeartBeat);
			root.put("jid", jid);
			root.put("send", send);
			return root.toString();
		} 
		catch(JSONException ex) 
		{
			return "";
		}
	}
	
	public static String packIpcVideo(int reqId, String jid, 
			IpcVideoOpt opt, int chId)
	{
		try
		{
			if (reqId < 0 || reqId > 65535)
			{
				return "";
			}
			
			if (chId < 0 || chId > 9)
			{
				return "";
			}
			
			JSONObject root = new JSONObject();
			JSONObject send = new JSONObject();
			send.put("channel", chId);
			
			if (opt == IpcVideoOpt.IVO_START)
			{
				send.put("action", "startvideo");
			}
			else if (opt == IpcVideoOpt.IVO_STOP)
			{
				send.put("action", "disconnect");
			}
			
			root.put("request_id", reqId);
			root.put("gl_msgtype", MT_IpcVideo);
			root.put("jid", jid);
			root.put("send", send);
			return root.toString();
		} 
		catch(JSONException ex) 
		{
			return "";
		}
	}
	
	public static String packIpcStat(int reqId, String jid)
	{
		try
		{
			if (reqId < 0 || reqId > 65535)
			{
				return "";
			}
			
			JSONObject root = new JSONObject();
			JSONObject send = new JSONObject();
			send.put("action", "getipcstatus");
			root.put("request_id", reqId);
			root.put("gl_msgtype", MT_IpcStat);
			root.put("jid", jid);
			root.put("send", send);
			return root.toString();
		} 
		catch(JSONException ex) 
		{
			return "";
		}
	}
	
}