package com.soon.fm.spotify.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Albums implements Results<AlbumItem> {

    @SerializedName("href")
    @Expose
    private String href;

    @SerializedName("items")
    @Expose
    private List<AlbumItem> items = new ArrayList<>();

    @SerializedName("limit")
    @Expose
    private Integer limit;

    @SerializedName("next")
    @Expose
    private String next;

    @SerializedName("offset")
    @Expose
    private Integer offset;

    @SerializedName("previous")
    @Expose
    private String previous;

    @SerializedName("total")
    @Expose
    private Integer total;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public List<AlbumItem> getItems() {
        return items;
    }

    public void setItems(List<AlbumItem> items) {
        this.items = items;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
