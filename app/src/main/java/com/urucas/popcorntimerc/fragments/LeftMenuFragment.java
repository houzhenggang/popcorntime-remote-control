package com.urucas.popcorntimerc.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.urucas.popcorntimerc.PopcornApplication;
import com.urucas.popcorntimerc.R;
import com.urucas.popcorntimerc.activities.AboutActivity;
import com.urucas.popcorntimerc.activities.HelpActivity;
import com.urucas.popcorntimerc.activities.SplashActivity;
import com.urucas.popcorntimerc.interfaces.JSONRPCCallback;
import com.urucas.popcorntimerc.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Urucas on 8/20/14.
 */
public class LeftMenuFragment extends android.support.v4.app.Fragment {

    private View view;

    private SplashActivity _activity;
    private EditText ptPort, ptUser, ptPass, ptHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_leftmenu, container, false);

        ptHost = (EditText) view.findViewById(R.id.ptHost);
        ptHost.setText(PopcornApplication.getSetting(PopcornApplication.PT_HOST));

        ptPort = (EditText) view.findViewById(R.id.ptPort);
        ptPort.setText(PopcornApplication.getSetting(PopcornApplication.PT_PORT));

        ptUser = (EditText) view.findViewById(R.id.ptUser);
        ptUser.setText(PopcornApplication.getSetting(PopcornApplication.PT_USER));

        ptPass = (EditText) view.findViewById(R.id.ptPass);
        ptPass.setText(PopcornApplication.getSetting(PopcornApplication.PT_PASS));

        Button connectBtt = (Button) view.findViewById(R.id.connectBtt);
        connectBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    testConnection();

            }
        });

        Button aboutBtt = (Button) view.findViewById(R.id.aboutBtt);
        aboutBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAbout();
            }
        });

        Button helpBtt = (Button) view.findViewById(R.id.helpBtt);
        helpBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHelp();
            }
        });

        return view;
    }

    private void openAbout() {
        Intent intent = new Intent(getActivity(), AboutActivity.class);
        getActivity().startActivity(intent);
    }

    private void openHelp() {
        Intent intent = new Intent(getActivity(), HelpActivity.class);
        getActivity().startActivity(intent);
    }

    private void testConnection() {

        String host = ptHost.getText().toString().trim();
        String port = ptPort.getText().toString().trim();
        String user = ptUser.getText().toString().trim();
        String pass = ptPass.getText().toString().trim();

        PopcornApplication.setSetting(PopcornApplication.PT_HOST, host);
        PopcornApplication.setSetting(PopcornApplication.PT_PORT, port);
        PopcornApplication.setSetting(PopcornApplication.PT_USER, user);
        PopcornApplication.setSetting(PopcornApplication.PT_PASS, pass);

        try {
            SplashActivity.getRemoteControl().setSettings(host, port, user, pass);

        }catch (Exception e){
            Utils.Toast(getActivity(), R.string.settingserror);
            return;
        }

        SplashActivity.getRemoteControl().ping(new JSONRPCCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {

            }

            @Override
            public void onSuccess() {
                Utils.Toast(getActivity(), R.string.connectionworking);
            }

            @Override
            public void onError(int error) {
                Utils.Toast(getActivity(), error);
            }
        });
    }

    public void setActivity(SplashActivity activity) {
        _activity = activity;
    }

}
