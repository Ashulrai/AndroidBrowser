package com.example.ibrowse;

import android.widget.ImageView;

public class Websites {

    private int _id;
    private String _url;
    private ImageView image;
    private String title;

    public Websites(){
      // empty constructor
  }
    public Websites(String url){
        this._url = url;
    }
    public Websites(String url, String title){
        this._url = url;
        this.title = title;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_url() {
        return _url;
    }

    public void set_url(String _url) {
        this._url = _url;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
