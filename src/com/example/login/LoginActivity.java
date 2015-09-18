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


//此为主activity的实现登录的。在android2.3以后，android规定了主activity不允许在主线程中做一些耗时较多的
//操作，包括网络的操作，主要是减少应用程序停止响应的问题。
@SuppressLint("ShowToast") public class LoginActivity extends Activity {
private Button login;
private	Button register;
private	EditText etusername;
private	EditText etpassword;
private	String username;
private	String password;
private	ProgressDialog p;

private String url;
private static String result=null;//从服务器端获取json数据


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		init();
		register.setOnClickListener((OnClickListener) new RegisterOnclick());
		login.setOnClickListener(new LoginOnclick());
	}
	private void init() 
	{
		etusername=(EditText) findViewById(R.id.etusername);
		etpassword=(EditText) findViewById(R.id.etpassword);
		login=(Button) findViewById(R.id.login);
		register=(Button) findViewById(R.id.register);
		p=new ProgressDialog(LoginActivity.this);
		p.setTitle("登录中");
		p.setMessage("登录中，马上就好");
	}
	private class RegisterOnclick implements OnClickListener
	{
		public void onClick(View v) {
			Intent intent=new Intent();
			intent.setClass(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);
		}

	}
	private class LoginOnclick implements OnClickListener
	{
		public void onClick(View arg0) {
			username=etusername.getText().toString().trim();
			if (username==null||username.length()<=0) 
			{		
				etusername.requestFocus();
				etusername.setError("对不起，用户名不能为空");
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
				etpassword.setError("对不起，密码不能为空");
				return;
			}
			else 
			{
				password=etpassword.getText().toString().trim();
			}
			p.show();
			new Thread(new Runnable() {

				public void run() {
					url="http://10.211.135.72:8080/NJUPT_STITP_Server/user/login?user.username="
							+username/*.getText()*/.toString()+"&user.password="+password/*.getText()*/.toString();
					String str=doHttpClientGet();
					int i=getRegistResult(str);
					Message msg=new Message();
					msg.obj=i;
					handler.sendMessage(msg);
				}
			}).start();

		}
	}	
	@SuppressLint("HandlerLeak") Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			int i=(Integer) msg.obj;
			String string = null;
			if(i==0){
				string="登录成功!";
				Toast.makeText(LoginActivity.this, string, Toast.LENGTH_LONG).show();
				Intent intent=new Intent(LoginActivity.this,Function.class);
				startActivity(intent);
				
			}else if(i==1){
				string="登陆失败，用户名或密码错误";
				Toast.makeText(LoginActivity.this, string, Toast.LENGTH_LONG).show();
			}else if(i==2){
				string="登陆失败，用户名不存在";	
				Toast.makeText(LoginActivity.this, string, Toast.LENGTH_LONG).show();
			}
			p.dismiss();
			//Toast.makeText(LoginActivity.this, string, Toast.LENGTH_LONG).show();
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
