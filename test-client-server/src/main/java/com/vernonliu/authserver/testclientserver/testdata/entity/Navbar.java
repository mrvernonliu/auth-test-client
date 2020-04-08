package com.vernonliu.authserver.testclientserver.testdata.entity;

public class Navbar {
    public final String APPLICATION_TITLE = System.getenv("APPLICATION_TITLE");
    public final String LOGIN_URL =
            System.getenv("AUTH_SERVER_LOGIN_URL")
                    + "/login?clientUuid="
                    + System.getenv("AUTH_CLIENT_UUID")
                    + "&redirectUrl="
                    + System.getenv("AUTH_REDIRECT_URL");

}
