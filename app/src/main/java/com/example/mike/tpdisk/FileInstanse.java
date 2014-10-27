package com.example.mike.tpdisk;

/**
 * Created by Mike on 26.10.2014.
 */
public class FileInstanse {

    private int size;
    private String public_key;
    private String origin_path;
    private String name;
    private String created;
    private String public_url;
    private String modified;
    private String path;
    private String media_type;
    private String preview;
    private String type;
    private String mime_type;
    private String md5;

    public int getSize() {
        return size;
    }

    public String getPublic_key() {
        return public_key;
    }

    public String getOrigin_path() {
        return origin_path;
    }

    public String getName() {
        return name;
    }

    public String getCreated() {
        return created;
    }

    public String getPublic_url() {
        return public_url;
    }

    public String getModified() {
        return modified;
    }

    public String getPath() {
        return path;
    }

    public String getMedia_type() {
        return media_type;
    }

    public String getPreview() {
        return preview;
    }

    public String getType() {
        return type;
    }

    public String getMime_type() {
        return mime_type;
    }

    public String getMd5() {
        return md5;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public void setOrigin_path(String origin_path) {
        this.origin_path = origin_path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setPublic_url(String public_url) {
        this.public_url = public_url;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public FileInstanse(int size, String public_key, String origin_path, String name, String created, String public_url,
                        String modified, String path, String media_type, String preview, String type, String mime_type, String md5) {
        this.size = size;
        this.public_key = public_key;
        this.origin_path = origin_path;
        this.name = name;
        this.created = created;
        this.public_url = public_url;
        this.modified = modified;
        this.path = path;
        this.media_type = media_type;
        this.preview = preview;
        this.type = type;
        this.mime_type = mime_type;
        this.md5 = md5;
    }

    public FileInstanse(){
        this.size = -1;
        this.public_key = this.origin_path = this.name = this.created = this.public_url = this.modified = this.path =
        this.media_type = this.preview = this.type = this.mime_type = this.md5 = "";
    }
}
