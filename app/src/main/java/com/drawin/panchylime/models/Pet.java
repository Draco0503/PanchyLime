package com.drawin.panchylime.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Pet implements Serializable {
    private String _petName;
    private String _image;
    private String _rarity;
    private String _createdAt;

    public Pet() {

    }

    public Pet(String _petName, String _image, String _rarity) {
        this._petName = _petName;
        this._image = _image;
        this._rarity = _rarity;
        this._createdAt = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy").format(new Date(System.currentTimeMillis()));
    }
    public Pet(String _petName, String _image, String _rarity, String _createdAt) {
        this._petName = _petName;
        this._image = _image;
        this._rarity = _rarity;
        this._createdAt = _createdAt;
    }

    public String get_petName() {
        return _petName;
    }

    public void set_petName(String _petName) {
        this._petName = _petName;
    }

    public String get_image() {
        return _image;
    }

    public void set_image(String _image) {
        this._image = _image;
    }

    public String get_rarity() {
        return _rarity;
    }

    public void set_rarity(String _rarity) {
        this._rarity = _rarity;
    }

    public String get_createdAt() {
        return _createdAt;
    }

    public void set_createdAt(String _createdAt) {
        this._createdAt = _createdAt;
    }
}
