package com.example.login;

import java.io.IOException;

import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("HandlerLeak") public class RegisterActivity extends Activity {
	private String url;
	private static String result=null;//�ӷ������˻�ȡjson����

	private	EditText etusername;
	private	EditText etpassword;
	private	ProgressDialog dialog;
	private	String username=null;
	private	String password=null;

	private Button registButton;
	private Button return_login;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regist_main);
		init();
		registButton.setOnClickListener((OnClickListener)new RegisterOnclick());
		return_login.setOnClickListener((OnClickListener)new Return_Login_Onclick());		
	}
	private void init()
	{
		etusername=(EditText) findViewById(R.id.name);
		etpassword=(EditText) findViewById(R.id.password);
		registButton = (Button) findViewById(R.id.regist);
		return_login = (Button) findViewById(R.id.return_login);	
		dialog=new ProgressDialog(RegisterActivity.this);
		dialog.setTitle("�ϴ�������");
		dialog.setMessage("���Ե�...");
	}

	private class Return_Login_Onclick implements OnClickListener
	{
		public void onClick(View v) {
			Intent intent=new Intent();
			intent.setClass(RegisterActivity.this, LoginActivity.class);
			startActivity(intent);
		}

	}

	private class RegisterOnclick implements OnClickListener
	{
		public void onClick(View arg0) {
			username=etusername.getText().toString().trim();
			if (username==null||username.length()<=0) 
			{		
				etusername.requestFocus();
				etusername.setError("�Բ����û�������Ϊ��");
				return;
			}
			else 
			{
				username=etusername.getText().toString().trim();
			}
			password=etpassword.getText().toString().trim();
			if (password==null||password.length()<=0) 
			{		
				etpassword.requestFocus();
				etpassword.setError("�Բ������벻��Ϊ��");
				return;
			}
			else 
			{
				password=etpassword.getText().toString().trim();
			}
			dialog.show();
			new Thread(new Runnable() {

				public void run() {
					url="http://10.211.133.56:8080/NJUPT_STITP_Server/user/register?user.username="
							+username/*.getText()*/.toString()+"&user.password="+password/*.getText()*/.toString();
					String str=doHttpClientGet();
					int i=getRegistResult(str);
					Message msg_register=new Message();
					msg_register.obj=i;
					handler_register.sendMessage(msg_register);
				}
			}).start();

		}
	}	
	
	Handler handler_register=new Handler()
	{
		public void handleMessage(Message msg) {
			int i=(Integer) msg.obj;
			String string = null;
			if(i==0){
				string="ע��ɹ�,��ʼ��¼....!";
				Toast.makeText(RegisterActivity.this, string, Toast.LENGTH_LONG).show();
				Intent intent=new Intent(RegisterActivity.this,Function.class);
				startActivity(intent);
			}else if(i==1){
				string="ע��ʧ�ܣ��û����Ѵ���";	
				Toast.makeText(RegisterActivity.this, string, Toast.LENGTH_LONG).show();
			}
			dialog.dismiss();
			super.handleMessage(msg);
		}	
	};
	
	private  String doHttpClientGet() {
		// TODO Auto-generated method stub
		
		HttpGet httpGet=new HttpGet(url);
		HttpClient client=new DefaultHttpClient();
		try {
			HttpResponse response=client.execute(httpGet);
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				result=EntityUtils.toString(response.getEntity());	
				}
				
				System.out.println("content----->"+result);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}	
	
	public  int getRegistResult(String jsonString){
		JSONObject resultCode = new JSONObject().fromString(jsonString);
		return resultCode.getInt("result_code");
	}
}