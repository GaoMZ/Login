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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


//��Ϊ��activity��ʵ�ֵ�¼�ġ���android2.3�Ժ�android�涨����activity�����������߳�����һЩ��ʱ�϶��
//��������������Ĳ�������Ҫ�Ǽ���Ӧ�ó���ֹͣ��Ӧ�����⡣
@SuppressLint("ShowToast") public class LoginActivity extends Activity {
private Button login;
private	Button register;
private	EditText etusername;
private	EditText etpassword;
private CheckBox checkBox;
private	String username;
private	String password;
private	ProgressDialog p;

private String url;
private static String result=null;//�ӷ������˻�ȡjson����
/*
 * �洢����
 */
SharedPreferences preferences;
Editor editor;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		init();
		register.setOnClickListener((OnClickListener) new RegisterOnclick());
		login.setOnClickListener(new LoginOnclick());
		saveInf();
	}
	private void saveInf() {
		// TODO Auto-generated method stub
	//	SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
		 preferences=getSharedPreferences("userInfo", MODE_PRIVATE);
		 editor=preferences.edit();
		 username=preferences.getString("userName", "");
		if(username==null){
			checkBox.setChecked(false);		
		}
		else {
			checkBox.setChecked(true);
			etusername.setText(username);
		}
	}
	private void init() 
	{
		etusername=(EditText) findViewById(R.id.etusername);
		etpassword=(EditText) findViewById(R.id.etpassword);
		login=(Button) findViewById(R.id.login);
		register=(Button) findViewById(R.id.register);
		p=new ProgressDialog(LoginActivity.this);
		p.setTitle("��¼��");
		p.setMessage("��¼�У����Ͼͺ�");
		checkBox=(CheckBox) findViewById(R.id.chkSaveName);
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
			p.show();
			new Thread(new Runnable() {
				public void run() {
					url="http://10.1.63.21:8080/NJUPT_STITP_Server/user/login?user.username="
						+username/*getText()*/.toString()+"&user.password="+password/*.getText()*/.toString();
					String str=doHttpClientGet();
					int i=getLoginResult(str);
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
				string="��¼�ɹ�!";
				/*
				 * �ж��Ƿ�洢�û���������
				 */
				if(checkBox.isChecked()){
					editor.putString("userName", username);
					editor.putString("passWord", password);
					editor.commit();
				}else{
					editor.remove("userName");
					editor.remove("passWord");
					editor.commit();					
				}
				Toast.makeText(LoginActivity.this, string, Toast.LENGTH_LONG).show();
				Intent intent=new Intent(LoginActivity.this,Function.class);
				startActivity(intent);
				
			}else if(i==1){
				string="��½ʧ�ܣ��û������������";
				Toast.makeText(LoginActivity.this, string, Toast.LENGTH_LONG).show();
			}else if(i==2){
				string="��½ʧ�ܣ��û���������";	
				Toast.makeText(LoginActivity.this, string, Toast.LENGTH_LONG).show();
			}
			p.dismiss();
			//Toast.makeText(LoginActivity.this, string, Toast.LENGTH_LONG).show();
			super.handleMessage(msg);
		}	
	};
	

	private  String doHttpClientGet() {
		
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
	
	public  int getLoginResult(String jsonString){
		JSONObject resultCode = new JSONObject().fromString(jsonString);
		return resultCode.getInt("result_code");
	}
	
}
