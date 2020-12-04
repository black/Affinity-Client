package com.black.affinity.Pojos;

public class Invite {
    private String mail;
    private String team;
    private String status;
    private String uniqueId;

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
}

