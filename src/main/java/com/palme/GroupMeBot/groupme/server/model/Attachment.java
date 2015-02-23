package com.palme.GroupMeBot.groupme.server.model;

import java.util.List;

public class Attachment {
    private AttachmentType type;
    
    //image url
    private String url;
    
    //Begin location attachment type
    private String lat;
    private String lng;
    private String name;
    
    //Begin emoji
    private String placeholder;
    private List<List<Integer>> charmap;
    
    
    public AttachmentType getType() {
        return type;
    }

    public void setType(AttachmentType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public List<List<Integer>> getCharmap() {
        return charmap;
    }

    public void setCharmap(List<List<Integer>> charmap) {
        this.charmap = charmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}
