package com.example.mike.tpdisk;

import java.util.ArrayList;

/**
 * Created by Mike on 27.10.2014.
 */
public class Embedded {
    private String sort;
    private ArrayList<FileInstanse> items;
    private Integer limit;
    private Integer offset;
    private String path;
    private Integer total;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public ArrayList<FileInstanse> getItems() {
        return items;
    }

    public void setItems(ArrayList<FileInstanse> items) {
        this.items = items;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Embedded(String sort, ArrayList<FileInstanse> items, int limit, int offset, String path, int total) {
        this.sort = sort;
        this.items = items;
        this.limit = limit;
        this.offset = offset;
        this.path = path;
        this.total = total;
    }
    public Embedded(){
        this.sort = this.path = "";
        this.limit = this.offset = this.total = -1;
        this.items = null;
    }

}
