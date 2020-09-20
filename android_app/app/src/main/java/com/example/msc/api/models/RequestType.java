package com.example.msc.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * The type of request made from BulletBot to PyBullet
 */
public enum RequestType {
    @SerializedName("INVERSE_KINEMATICS")
    INVERSE_KINEMATICS,

    @SerializedName("ORIENTATION")
    ORIENTATION

}
