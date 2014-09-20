package com.urucas.popcorntimerc.socket;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.ConnectionConfigurator;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import com.urucas.popcorntimerc.R;
import com.urucas.popcorntimerc.interfaces.RemoteControlCallback;
import com.urucas.popcorntimerc.model.Movie;
import com.urucas.popcorntimerc.utils.Utils;

import org.json.JSONObject;

import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Urucas on 8/20/14.
 */
public class RemoteControl {

    private static final String TAG_NAME = "RemoteControl";

    private static String _ip, _port, _user, _pass;

    private JSONRPC2Session _jsonRPCSession;

    public RemoteControl(String ip, String port, String user, String pass) {
        _ip = ip;
        _port = port;
        _user = user;
        _pass = pass;
    }

    private JSONRPC2Session getJsonRPCSession() throws MalformedURLException {
        if(_jsonRPCSession == null) {
            _jsonRPCSession = new JSONRPC2Session(new URL("http://" + _ip + ":" + _port));

            BasicAuthenticator auth = new BasicAuthenticator();
            auth.setCredentials(_user, _pass);

            _jsonRPCSession.setConnectionConfigurator(auth);
        }
        return _jsonRPCSession;
    }

    private static int requestID = 1;

    private class JSONRPCTask extends AsyncTask<String, String, JSONRPC2Response> {

        private Map<String, Object> params;
        private JSONRPC2Session session;

        public JSONRPCTask(JSONRPC2Session session) {
            this.session = session;
        }

        public JSONRPCTask(JSONRPC2Session session, Map<String, Object> params){
            this.session = session;
            this.params  = params;
        }

        @Override
        protected JSONRPC2Response doInBackground(String... params1) {
            if(params == null) {
                params = new HashMap<String, Object>();
            }
            String method = params1[0];
            // Log.i("method", method);
            session.getOptions().setRequestContentType("application/json");
            JSONRPC2Request request = new JSONRPC2Request(method, params, requestID);
            JSONRPC2Response response = null;

            try {
                response = session.send(request);
                Log.i("response", response.toString());

            } catch (JSONRPC2SessionException e) {
                e.printStackTrace();
            }
            requestID++;
            return response;
        }

    }

    private class BasicAuthenticator implements ConnectionConfigurator {

        private String user;
        private String pass;

        public void setCredentials(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }

        @Override
        public void configure(HttpURLConnection connection) {

            byte[] encodedBytes = Base64.encode((user + ":" + pass).getBytes(), Base64.DEFAULT);
            // add custom HTTP header
            connection.addRequestProperty("Authorization", "Basic "+ new String(encodedBytes));
        }
    }

    /**
     * remote control events to emit
     */

    private void emit(String event){
        try {
            new JSONRPCTask(getJsonRPCSession()).execute(event);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void toggleplay() {
        this.emit("toggleplaying");
    }

    public void fullscreen() {
        // toggle fullscreen only works on player view
        this.emit("togglefullscreen");
    }

    public void mute() {
        this.emit("mute");
    }

    public void escape() {
        this.emit("back");
    }

    public void moveRight() {
        this.emit("right");
    }

    public void moveLeft() {
        this.emit("left");
    }

    public void moveUp() {
        this.emit("up");
    }

    public void moveDown() {
        this.emit("down");
    }

    public void enter() {
        this.emit("enter");
    }

    public void showMoviesList() {
        this.emit("movieslist");
    }

    public void showSeriesList() {
        this.emit("showslist");
    }

    public void seasonUp() {
        this.emit("previousseason");
    }

    public void seasonDown() {
        this.emit("nextseason");
    }

    public void favourite() {
        this.emit("togglefavourite");
    }

    public void watched() {
        this.emit("togglewatched");
    }
}
