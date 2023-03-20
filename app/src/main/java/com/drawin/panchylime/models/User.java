package com.drawin.panchylime.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class User implements Serializable {
    // protected final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy");

    private String _username;
    private String _email;
    private int _coins;
    private List<PetBag> _bag;
    private String _createdAt;

    public User() {

    }

    public User(String _username, String _email, List<PetBag> _bag) {
        this._username = _username;
        this._email = _email;
        this._coins = 5;
        this._bag = _bag;
        this._createdAt = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy").format(new Date(System.currentTimeMillis()));
    }

    public User(String _username, String _email, int _coins, List<PetBag> _bag, String _createdAt) {
        this._username = _username;
        this._email = _email;
        this._coins = _coins;
        this._bag = _bag;
        this._createdAt = _createdAt;
    }

    public String get_username() {
        return _username;
    }

    public void set_username(String username) {
        this._username = username;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public int get_coins() {
        return _coins;
    }

    public void set_coins(int _coins) {
        this._coins = _coins;
    }

    public List<PetBag> get_bag() {
        return _bag;
    }

    public void set_bag(List<PetBag> _bag) {
        this._bag = _bag;
    }

    public String get_createdAt() {
        return _createdAt;
    }

    public void set_createdAt(String _createdAt) {
        this._createdAt = _createdAt;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "_username='" + _username + '\'' +
                ", _email='" + _email + '\'' +
                ", _coins=" + _coins +
                ", _bag=" + _bag +
                ", _createdAt='" + _createdAt + '\'' +
                '}';
    }

    public PetBag getSelectedPet() {
        for (int i = 0; i < _bag.size(); i++) {
            if (_bag.get(i).is_selected())
                return _bag.get(i);
        }
        return null;
    }
}

