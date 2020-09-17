package com.example.msc.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model which contains the position and orientation for the end-effector.
 */
public class InverseKinematicsModel {
    @SerializedName("type")
    private final RequestType type;

    @SerializedName("x_coord")
    private final double xCoordinate;
    @SerializedName("y_coord")
    private final double yCoordinate;
    @SerializedName("z_coord")
    private final double zCoordinate;
    @SerializedName("a_orient")
    private final double alphaOrientation;
    @SerializedName("b_orient")
    private final double betaOrientation;
    @SerializedName("c_orient")
    private final double gammaOrientation;

    public InverseKinematicsModel(RequestType type,
                                  double xCoordinate,
                                  double yCoordinate,
                                  double zCoordinate,
                                  double alphaOrientation,
                                  double betaOrientation,
                                  double gammaOrientation) {
        this.type = type;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.zCoordinate = zCoordinate;
        this.alphaOrientation = alphaOrientation;
        this.betaOrientation = betaOrientation;
        this.gammaOrientation = gammaOrientation;

    }

    public RequestType getType() {
        return type;
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public double getzCoordinate() {
        return zCoordinate;
    }

    public double getAlphaOrientation() {
        return alphaOrientation;
    }

    public double getBetaOrientation() {
        return betaOrientation;
    }

    public double getGammaOrientation() {
        return gammaOrientation;
    }
}
