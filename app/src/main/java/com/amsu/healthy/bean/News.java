package com.amsu.healthy.bean;

/**
 * Created by HP on 2017/1/11.
 */

public class News {
    private String title;
    private String time;
    private String url;
    private String smallPictureUrl;
    private String type;

    public News(String title, String time, String url, String smallPictureUrl, String type) {
        this.title = title;
        this.time = time;
        this.url = url;
        this.smallPictureUrl = smallPictureUrl;
        this.type = type;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSmallPictureUrl() {
        return smallPictureUrl;
    }

    public void setSmallPictureUrl(String smallPictureUrl) {
        this.smallPictureUrl = smallPictureUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
