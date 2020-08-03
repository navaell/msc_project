package com.example.msc.models;

import com.google.gson.annotations.SerializedName;

public enum RequestType {
    @SerializedName("INVERSE_KINEMATICS")
    INVERSE_KINEMATICS,

    @SerializedName("ORIENTATION")
    ORIENTATION

}
