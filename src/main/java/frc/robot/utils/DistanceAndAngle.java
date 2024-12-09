// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.utils;

public class DistanceAndAngle {
    private final double distance;
    private final double theta;

    public DistanceAndAngle(double distance, double theta) {

        this.distance = distance;
        this.theta = theta;
    }

    public double getDistance() {
        return distance;
    }

    public double getTheta() {
        return theta;
    }

    public double getDistanceMeters() {
        return distance / 39.37;
    }

    @Override
    public String toString() {
        return String.format("Distance: %.2f inches, Angle: %.2f degrees", distance, theta);
    }
}
