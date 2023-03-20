package com.drawin.panchylime.models;


import java.io.Serializable;


public class PetBag implements Serializable {

    private String _petName;
    private int _happiness;
    private int _cleanness;
    private int _hunger;
    private boolean _selected;

    public PetBag() {

    }

    public PetBag(String _petName) {
        this._petName = _petName;
        this._happiness = 50;
        this._cleanness = 50;
        this._hunger = 50;
        this._selected = false;
    }

    public PetBag(String _petName, int _happiness, int _cleanness, int _hunger, boolean _selected) {
        this._petName = _petName;
        this._happiness = _happiness;
        this._cleanness = _cleanness;
        this._hunger = _hunger;
        this._selected = _selected;
    }

    public String get_petName() {
        return _petName;
    }

    public void set_petName(String _petName) {
        this._petName = _petName;
    }

    public int get_happiness() {
        return _happiness;
    }

    public void set_happiness(int _happiness) {
        this._happiness = _happiness;
    }

    public int get_cleanness() {
        return _cleanness;
    }

    public void set_cleanness(int _cleanness) {
        this._cleanness = _cleanness;
    }

    public int get_hunger() {
        return _hunger;
    }

    public void set_hunger(int _hunger) {
        this._hunger = _hunger;
    }

    public boolean is_selected() {
        return _selected;
    }

    public void set_selected(boolean _selected) {
        this._selected = _selected;
    }
    @Override
    public String toString() {
        return "PetBag{" +
                "_petName='" + _petName + '\'' +
                ", _happiness=" + _happiness +
                ", _cleanness=" + _cleanness +
                ", _hunger=" + _hunger +
                ", _selected=" + _selected +
                '}';
    }


}
