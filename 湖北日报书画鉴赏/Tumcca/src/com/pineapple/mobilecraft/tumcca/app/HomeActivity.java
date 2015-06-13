package com.pineapple.mobilecraft.tumcca.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.pineapple.mobilecraft.R;
import com.pineapple.mobilecraft.tumcca.manager.UserManager;
import com.pineapple.mobilecraft.tumcca.mediator.IHome;
import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.drafts.Draft_76;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by yihao on 15/6/4.
 */
public class HomeActivity extends FragmentActivity implements IHome{
    Button mBtnLogin = null;
    Button mBtnRegister = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_home);
        if(UserManager.getInstance().isLogin()){
            setTitle(UserManager.getInstance().getCachedUsername());
        }
        else{
            setTitle("书法+");
            addAccountView();
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                testWebsocket();
            }
        });
        t.start();




    }

    @Override
    public void addHotTab() {

    }

    @Override
    public void addTrendsTab() {

    }

    @Override
    public void onTabSelect() {

    }

    @Override
    public void addAccountView() {
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_account);
        layout.setVisibility(View.VISIBLE);
        mBtnLogin = (Button)findViewById(R.id.button_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.startActivity(HomeActivity.this);
//                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
//
//                builder.setView(getLayoutInflater().inflate(R.layout.dialog_register, null));
//
//                builder.create().show();
            }
        });

        mBtnRegister = (Button)findViewById(R.id.button_register);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.startActivity(HomeActivity.this);
                //RegisterFragment fragment = new RegisterFragment();
                //getFragmentManager().beginTransaction().add(fragment, "register").commit();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == LoginActivity.REQ_LOGIN&&resultCode == RESULT_OK){
            setTitle(UserManager.getInstance().getCachedUsername());
            RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_account);
            layout.setVisibility(View.GONE);
        }
        if(requestCode == RegisterActivity.REQ_REGISTER&&resultCode == RESULT_OK){
            setTitle(UserManager.getInstance().getCachedUsername());
            RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout_account);
            layout.setVisibility(View.GONE);


        }
        //super.onActivityResult();
    }
    private final WebSocket mConnection = new WebSocketConnection();

    private void testWebsocket(){
        try {
            mConnection.connect("ws://120.26.202.114:6696/follow/ws", new WebSocketConnectionHandler() {
                @Override
                public void onOpen() {
                    Log.d("Websocket", "onOpen");
                    mConnection.sendTextMessage("Hello");
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d("Websocket", payload);

                }

                @Override
                public void onClose(int code, String reason) {
                }
            });
        } catch (WebSocketException e) {

            Log.d("Websocket", e.toString());
        }




    }
}