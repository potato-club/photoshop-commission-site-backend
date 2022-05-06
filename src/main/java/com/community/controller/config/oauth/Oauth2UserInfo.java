package com.community.controller.config.oauth;

public interface Oauth2UserInfo {

    String getNickName();
    String getEmail();
    String getImageUrl();
    String getProvider();
    String getProviderId();

}
