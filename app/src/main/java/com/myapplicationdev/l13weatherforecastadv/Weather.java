package com.myapplicationdev.l13weatherforecastadv;

class Weather {
    private String city;
    private String condition;
    private int temperature;

    public Weather(String city, String condition, int temperature) {
        this.city = city;
        this.condition = condition;
        this.temperature = temperature;
    }

    public String getCity() {
        return city;
    }
    public String getCondition() {
        return condition;
    }

    public int getTemperature() {
        return temperature;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }


}
