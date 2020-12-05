package com.black.affinity.Pojos;

import java.util.HashMap;

public class Invite {
    private String mail;
    private String team;
    private String status;
    private String uniqueId;
    private HashMap<String,String> info;

    public String getMail() {
        return mail;
    }

    public String getTeam() {
        return team;
    }

    public String getStatus() {
        return status;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public HashMap<String, String> getInfo() {
        return info;
    }

    public void setInfo(HashMap<String, String> info) {
        this.info = info;
    }
}

