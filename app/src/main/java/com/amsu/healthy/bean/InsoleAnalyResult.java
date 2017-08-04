package com.amsu.healthy.bean;

/**
 * @anthor haijun
 * @project name: Healthy
 * @class name：com.amsu.healthy.bean
 * @time 8/1/2017 7:36 PM
 * @describe
 */
public class InsoleAnalyResult {
    /*{
    "header": {
        "uuid": "a3b51bed-4fa8-4d0c-87d5-c5454f24c568",
        "creationTime": 100000,
        "type": "walking",
        "description": null,
        "comment": null,
        "version": "V1-20170221"
    },
    "customer": {
        "name": "haijun",
        "gender": "male",
        "age": 24,
        "height": 165,
        "weight": 52,
        "phone": "18689463192"
    },
    "general": {
        "dataQuality": "good",
        "duration": 42.58000000000004,
        "stepCount": 36,
        "stepRate": 59.14972273567461,
        "strideLength": 10.815709236154412,
        "symmetry": 0.8436741301156385,
        "variability": 0.34099146209623155
    },
    "left": {
        "swingWidthMean": 1.3025415271426037,
        "stepHeightMean": 11.324873189693207,
        "stanceDurationMean": 1.0275000000000176,
        "inversion": true,
        "eversion": false,
        "landingPosition": {
            "sagital": "heel",
            "frontal": "outside"
        },
        "supportStabilityMean": 0.012690105455593601
    },
    "right": {
        "swingWidthMean": -1.302541527142604,
        "stepHeightMean": 11.324873189693207,
        "stanceDurationMean": 1.0275000000000176,
        "inversion": true,
        "eversion": false,
        "landingPosition": {
            "sagital": "heel",
            "frontal": "inside"
        },
        "supportStabilityMean": 0.012690105455593601
    }
}*/
    public Header header;
    public Customer customer;
    public General general;
    public LeftAndRight left;
    public LeftAndRight right;


    public class Header{
        public String uuid;
        public long creationTime;
        public String type;
        public String description;
        public String comment;
        public String version;
        @Override
        public String toString() {
            return "Header [uuid=" + uuid + ", creationTime=" + creationTime
                    + ", type=" + type + ", description=" + description
                    + ", comment=" + comment + ", version=" + version + "]";
        }


    }

    public class Customer{
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
        public String dataQuality;
        public float duration;
        public int stepCount;
        public float stepRate;
        public float strideLength;
        public float symmetry;
        public float variability;
        @Override
        public String toString() {
            return "General [dataQuality=" + dataQuality + ", duration="
                    + duration + ", stepCount=" + stepCount + ", stepRate="
                    + stepRate + ", strideLength=" + strideLength
                    + ", symmetry=" + symmetry + ", variability=" + variability
                    + "]";
        }



    }

    public class LeftAndRight{
        public float swingWidthMean;
        public float stepHeightMean;
        public float stanceDurationMean;
        public boolean inversion;
        public boolean eversion;
        public LandingPosition landingPosition;
        public float supportStabilityMean;
        @Override
        public String toString() {
            return "LeftAndRight [swingWidthMean=" + swingWidthMean
                    + ", stepHeightMean=" + stepHeightMean
                    + ", stanceDurationMean=" + stanceDurationMean
                    + ", inversion=" + inversion + ", eversion=" + eversion
                    + ", landingPosition=" + landingPosition
                    + ", supportStabilityMean=" + supportStabilityMean + "]";
        }


    }

    public class LandingPosition{
        public String sagital;
        public String frontal;
        @Override
        public String toString() {
            return "LandingPosition [sagital=" + sagital + ", frontal="
                    + frontal + "]";
        }


    }

    @Override
    public String toString() {
        return "InsoleAnalyResult [header=" + header + ", customer=" + customer
                + ", general=" + general + ", left=" + left + ", right="
                + right + "]";
    }


}
