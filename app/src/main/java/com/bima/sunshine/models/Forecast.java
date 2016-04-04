package com.bima.sunshine.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bimapw on 4/5/16.
 */
public class Forecast {

    private City city;
    private String cod;
    private Double message;
    private Integer cnt;
    private List<Day> list = new ArrayList<>();

    /**
     *
     * @return
     * The city
     */
    public City getCity() {
        return city;
    }

    /**
     *
     * @param city
     * The city
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     *
     * @return
     * The cod
     */
    public String getCod() {
        return cod;
    }

    /**
     *
     * @param cod
     * The cod
     */
    public void setCod(String cod) {
        this.cod = cod;
    }

    /**
     *
     * @return
     * The message
     */
    public Double getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(Double message) {
        this.message = message;
    }

    /**
     *
     * @return
     * The cnt
     */
    public Integer getCnt() {
        return cnt;
    }

    /**
     *
     * @param cnt
     * The cnt
     */
    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    /**
     *
     * @return
     * The list
     */
    public List<Day> getList() {
        return list;
    }

    /**
     *
     * @param list
     * The list
     */
    public void setList(List<Day> list) {
        this.list = list;
    }

}
