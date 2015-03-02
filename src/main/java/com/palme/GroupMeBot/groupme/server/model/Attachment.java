package com.palme.GroupMeBot.groupme.server.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
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

    public void setType(final AttachmentType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(final String placeholder) {
        this.placeholder = placeholder;
    }

    public List<List<Integer>> getCharmap() {
        return charmap;
    }

    public void setCharmap(final List<List<Integer>> charmap) {
        this.charmap = charmap;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(final String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(final String lat) {
        this.lat = lat;
    }
}
