// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.commands.ApproachTagAuto;

import frc.robot.subsystems.DrivetrainSubsystem.CommandSwerveDrivetrain;
import frc.robot.utils.DistanceAndAngle;
import frc.robot.utils.LimelightHelpers;

public class VisionSubsystem extends SubsystemBase {

    ApproachTagAuto autoCommand;

    public VisionSubsystem() {

        LimelightHelpers.setLEDMode_PipelineControl("");
        LimelightHelpers.setLEDMode_ForceBlink("");

    }

    public boolean canSeeTag(int tag_id) {
        int detectedTagId = (int) LimelightHelpers.getFiducialID("");
        return (detectedTagId == tag_id);
    }

    private Pose3d poseCache;
    private int poseAge = 0;

    public Pose3d getTagPose3d(int tag_id) {
        int detectedTagId = (int) LimelightHelpers.getFiducialID("");
        poseAge++;
        if (detectedTagId == tag_id) {
            poseCache = LimelightHelpers.getTargetPose3d_CameraSpace("");
            poseAge = 0;
            return poseCache;
        } else {
            if (poseAge < 100) {
                return poseCache;
            }
            // Safety
            autoCommand.cancel();
            return null;
        }
    }

    public DistanceAndAngle getTagDistanceAndAngle(int tag_id) {

        int detectedTagId = (int) LimelightHelpers.getFiducialID("");
        if (detectedTagId == tag_id) {

            double tagHeight = LimelightHelpers.getT2DArray("")[15];

            double tx = LimelightHelpers.getTX("");
            System.out.println("TX: " + tx);

            double distance = Constants.LIMELIGHT_FOV_MMPP / tagHeight; // inches

            return new DistanceAndAngle(distance, tx);
        }

        return new DistanceAndAngle(-1.0, -1.0);
    }

    public ApproachTagAuto approachDynamically(CommandSwerveDrivetrain drivetrain, int tag_id, VacuumMaster vacuum,
            ArmSubsystem arm,
            boolean away, boolean wait, boolean three, int waitTime, int moveTime) {
        if (tag_id == -1) {
            System.out.println("----------- UNSET AUTO! SKIPPING -----------");
            return null;
        }
        ApproachTagAuto a = new ApproachTagAuto(drivetrain, this, tag_id, vacuum, arm, away, wait, three, waitTime, moveTime);
        a.schedule();
        this.autoCommand = a;
        return a;
    }

    @Override
    public void periodic() {
    }

}