package com.bib.esma;

import java.util.ArrayList;

public class ISINList {
    private ArrayList<String> smId = new ArrayList<String>();;
    private String smTicker;
    private boolean smStatus;
    private boolean smShare;

    public String getSmTicker() {
        return smTicker;
    }

    public void setSmTicker(String smTicker) {
        this.smTicker = smTicker;
    }


    public String getSmId(int id) {
        return smId.get(id);
    }

    public void addSmId(String smId) {
        this.smId.add(smId);
    }
    public int smIdLen() {
        return this.smId.size();
    }

    public boolean isSmShare (){
        return smShare;
    }

    public boolean isSmStatus() {
        return smStatus;
    }

    public void setSmStatus(boolean smStatus) {
        this.smStatus = smStatus;
    }
    public String getSmStatus() {
        if (smStatus) {
            return "YES";
        } else {
            return "NO";
        }
    }

}
