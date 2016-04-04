package com.aidor.secchargemobile.seccharge;

public class MyURL {
    public String getUrl() {
        return Url;
    }
    public void setUrl(String url) {
        Url = url;
    }
    public final String URL_FINAL = "http://172.20.10.3:8011/";
    public String Url = URL_FINAL + "seccharge/test/";
    public String UrlR = URL_FINAL + "seccharge/test";

    public String getUrlR() {
        return UrlR;
    }

    public String getMapURL_getLatLan() {
        return MapURL_getLatLan;
    }

    public void setMapURL_getLatLan(String mapURL_getLatLan) {
        MapURL_getLatLan = mapURL_getLatLan;
    }

    public String getMapURL_ChargSite_ID() {
        return MapURL_ChargSite_ID;
    }

    public void setMapURL_ChargSite_ID(String mapURL_ChargSite_ID) {
        MapURL_ChargSite_ID = mapURL_ChargSite_ID;
    }

    public String getRegisterMob() {
        return registerMob;
    }

    public void setRegisterMob(String registerMob) {
        registerMob = registerMob;
    }

    public String MapURL_getLatLan = URL_FINAL + "seccharge/getLatLongIds";
    public String MapURL_ChargSite_ID = URL_FINAL + "seccharge";
    public String registerMob = URL_FINAL + "seccharge/";

//    public String getSecUrl() {
//        return secUrl;
//    }
//
//    public void setSecUrl(String secUrl) {
//        this.secUrl = secUrl;
//    }
}
// Home 192.168.2.16
//Home Sahaj: 192.168.2.14
//Mobile Sahaj: 172.20.10.2
//Mobile 172.20.10.3
//10.137.213.121