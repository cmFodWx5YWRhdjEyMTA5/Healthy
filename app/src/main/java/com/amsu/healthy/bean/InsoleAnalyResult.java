package com.amsu.healthy.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.bean
 * @time 8/1/2017 7:36 PM
 * @describe
 */
public class InsoleAnalyResult{
    public Header header;
    public Testee testee;
    public General general;
    public List<LeftAndRight> left;
    public List<LeftAndRight> right;

    public class Header{
        public String id;
        public long creationTime;
        public String description;
        public String comment;
        public String version;
        @Override
        public String toString() {
            return "Header [uuid=" + id + ", creationTime=" + creationTime
                    + ", description=" + description
                    + ", comment=" + comment + ", version=" + version + "]";
        }
    }

    public class Testee{
        public String name;
        public String gender;
        public int age;
        public int height;
        public int weight;
        public String phone;

        @Override
        public String toString() {
            return "Customer [name=" + name + ", gender=" + gender + ", age="
                    + age + ", height=" + height + ", weight=" + weight
                    + ", phone=" + phone + "]";
        }
    }

    public class General{
        public double symmetry;
        public String variability;
        public String stepRate;
        public String strideLength;
        public GeneralLeft left;
        public GeneralLeft right;

        public General() {
            this.symmetry = 0;
            this.variability = "";
            this.stepRate = "";
            this.strideLength = "";
            this.left = new GeneralLeft();
            this.right = new GeneralLeft();
        }

        public class GeneralLeft{
            public double supportStability;
            public LandingPosition landingPosition;
            public boolean inversion;


            public GeneralLeft() {
                this.supportStability = 0;
                this.landingPosition = new LandingPosition();
                this.inversion = false;
            }

            public class LandingPosition{
                public String sagital;
                public String frontal;

                public LandingPosition() {
                    this.sagital = "";
                    this.frontal = "";
                }

                @Override
                public String toString() {
                    return "LandingPosition [sagital=" + sagital + ", frontal="
                            + frontal + "]";
                }
            }

            @Override
            public String toString() {
                return "GeneralLeft{" +
                        "supportStability=" + supportStability +
                        ", landingPosition=" + landingPosition +
                        ", inversion=" + inversion +
                        '}';
            }
        };


    }

    public class LeftAndRight{
        public double duration;
        public int stepCount;
        public int validStepCount;
        public int heelLoadingCount;
        public int toeLoadingCount;

        public double cycleDurationMean;
        public double cycleDurationCv;
        public double swingDurationMean;
        public double swingDurationCv;
        public double stanceDurationMean;
        public double stanceDurationCv;
        public double loadingDurationMean;
        public double loadingDurationCv;
        public double flatfootDurationMeanan;
        public double flatfootDurationCv;
        public double pushingDurationMean;
        public double pushingDurationCv;
        public double loadingImpactMean;
        public double loadingImpactCv;
        public double stepHeightMean;
        public double stepHeightCv;
        public double swingWidthMean;
        public double swingWidthCv;
        public double strideLengthMean;
        public double strideLengthCv;
        public double supportStabilityMean;
        public double supportStabilityCv;



        @Override
        public String toString() {
            return "LeftAndRight{" +
                    "duration=" + duration +
                    ", stepCount=" + stepCount +
                    ", validStepCount=" + validStepCount +
                    ", heelLoadingCount=" + heelLoadingCount +
                    ", toeLoadingCount=" + toeLoadingCount +
                    ", cycleDurationMean=" + cycleDurationMean +
                    ", cycleDurationCv=" + cycleDurationCv +
                    ", swingDurationMean=" + swingDurationMean +
                    ", swingDurationCv=" + swingDurationCv +
                    ", stanceDurationMean=" + stanceDurationMean +
                    ", stanceDurationCv=" + stanceDurationCv +
                    ", loadingDurationMean=" + loadingDurationMean +
                    ", loadingDurationCv=" + loadingDurationCv +
                    ", flatfootDurationMeanan=" + flatfootDurationMeanan +
                    ", flatfootDurationCv=" + flatfootDurationCv +
                    ", pushingDurationMean=" + pushingDurationMean +
                    ", pushingDurationCv=" + pushingDurationCv +
                    ", loadingImpactMean=" + loadingImpactMean +
                    ", loadingImpactCv=" + loadingImpactCv +
                    ", stepHeightMean=" + stepHeightMean +
                    ", stepHeightCv=" + stepHeightCv +
                    ", swingWidthMean=" + swingWidthMean +
                    ", swingWidthCv=" + swingWidthCv +
                    ", strideLengthMean=" + strideLengthMean +
                    ", strideLengthCv=" + strideLengthCv +
                    ", supportStabilityMean=" + supportStabilityMean +
                    ", supportStabilityCv=" + supportStabilityCv +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "InsoleAnalyResult{" +
                "header=" + header +
                ", testee=" + testee +
                ", general=" + general +
                ", left=" + left +
                ", right=" + right +
                '}';
    }


    public InsoleAnalyResult() {
        this.header = new Header();
        this.testee = new Testee();
        this.general = new General();
        this.left = new ArrayList<>();
        this.right = new ArrayList<>();
    }
}
