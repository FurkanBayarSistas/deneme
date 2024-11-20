package com.audiocodes.mv.webrtcclient.Structure;

import com.audiocodes.mv.webrtcclient.General.AppUtils;
import com.audiocodes.mv.webrtcclient.General.MainApp;
import com.audiocodes.mv.webrtcclient.R;
import com.audiocodes.mv.webrtcsdk.sip.enums.Transport;

public class SipAccount {

    private String proxy;
    private int port;
    private String domain;
    private Transport transport;
    private String username;
    private String password;
    private String displayName;

    public SipAccount() {
        boolean debugMode = AppUtils.getStringBoolean(R.string.enable_debug_mode);
        if(debugMode) {
            proxy = MainApp.getGlobalContext().getString(R.string.sip_account_proxy_default);
            domain = MainApp.getGlobalContext().getString(R.string.sip_account_domain_default);
            username = MainApp.getGlobalContext().getString(R.string.sip_account_user_name_default);
            password = MainApp.getGlobalContext().getString(R.string.sip_account_password_default);
            displayName = MainApp.getGlobalContext().getString(R.string.sip_account_display_name_default);
        }
        String portStr = MainApp.getGlobalContext().getString(R.string.sip_account_port_default);
        port = Integer.valueOf(portStr);
        String transportStr =  MainApp.getGlobalContext().getString(R.string.sip_account_transport_default);
        transport = Transport.valueOf(transportStr);
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "SipAccount{" +
                "proxy='" + proxy + '\'' +
                ", port=" + port +
                ", domain='" + domain + '\'' +
                ", transport=" + transport +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
