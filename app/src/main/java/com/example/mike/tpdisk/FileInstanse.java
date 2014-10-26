package com.example.mike.tpdisk;

/**
 * Created by Mike on 26.10.2014.
 */
public class FileInstanse {
    public String size, public_key, origin_size, name, created, public_url,
    modified, path, media_type, preview, type, mime_type, md5;

    public FileInstanse(String size, String public_key, String origin_size, String name, String created, String public_url, String modified, String path, String media_type, String preview, String type, String mime_type, String md5) {
        this.size = size;
        this.public_key = public_key;
        this.origin_size = origin_size;
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
}
