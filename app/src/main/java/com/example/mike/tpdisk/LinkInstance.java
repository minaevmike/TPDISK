package com.example.mike.tpdisk;

/**
 * Created by Mike on 29.10.2014.
 */
public class LinkInstance {
    private String href;
    private String method;

    public String getTemplated() {
        return templated;
    }

    public void setTemplated(String templated) {
        this.templated = templated;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    private String templated;

    public LinkInstance(String href, String method, String templated) {
        this.href = href;
        this.method = method;
        this.templated = templated;
    }
}
