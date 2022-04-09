package com.example.taskserver.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tasks")
public class TaskClientProperties {
    private String urlWeapons;
    private String urlUsers;
    private String urlBands;

    public String getUrlWeapons() {
        return urlWeapons;
    }

    public void setUrlWeapons(String urlWeapons) {
        this.urlWeapons = urlWeapons;
    }

    public String getUrlUsers() {
        return urlUsers;
    }

    public void setUrlUsers(String urlUsers) {
        this.urlUsers = urlUsers;
    }

    public String getUrlBands() {
        return urlBands;
    }

    public void setUrlBands(String urlBands) {
        this.urlBands = urlBands;
    }
}
