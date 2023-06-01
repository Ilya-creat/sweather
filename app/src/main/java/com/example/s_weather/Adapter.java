package com.example.s_weather;

import com.google.gson.annotations.SerializedName;

public class Adapter {
    @SerializedName("main")
    Main main;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }
}
