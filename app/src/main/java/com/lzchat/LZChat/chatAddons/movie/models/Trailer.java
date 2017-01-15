package com.lzchat.LZChat.chatAddons.movie.models;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Disha on 10/20/2016.
 */
public class Trailer implements Serializable{

    private String id;
    @SerializedName("key")
    private String url;
    private String title;


    public Trailer(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");
        this.url = jsonObject.getString("key");
        this.title=jsonObject.getString("name");
        this.site = jsonObject.getString("site");

    }

    public String getId() {
        return id;
    }
    public String getUrl() {
        //return String.format("https://www.youtube.com/watch?v=%s",url);
        return url;
    }
    public String getTitle() {
        return title;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String size;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    private String site;
    private String type;

    public static ArrayList<Trailer> fromJsonArray(JSONArray trailerArray) {
        ArrayList<Trailer> results = new ArrayList<>();
        for (int x = 0; x < trailerArray.length(); x++) {
            try {
                results.add(new Trailer(trailerArray.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

}
