package com.amsu.healthy.utils;

/**
 * author：WangLei
 * date:2017/10/30.
 * QQ:619321796
 */

public class MarathonUtil {
    private final static String level_1 = "较差";
    private final static String level_2 = "一般";
    private final static String level_3 = "优秀";
    private final static String level_4 = "顶尖";

    public static String getEnduranceLevel(double distance) {
        int userSex = MyUtil.getUserSex();
        int userAge = HealthyIndexUtil.getUserAge();
        String level = "";
        if (userSex == 1) {//男
            if (userAge < 30) {
                return distanceMan30(distance);
            } else if (userAge <= 39 && userAge <= 30) {
                return distanceMan30_39(distance);
            } else if (userAge <= 49 && userAge <= 40) {
                return distanceMan40_49(distance);
            } else if (userAge > 50) {
                return distanceMan50(distance);
            }
        } else {//女
            if (userAge < 30) {
                return distanceWoMan30(distance);
            } else if (userAge <= 39 && userAge <= 30) {
                return distanceWoMan30_39(distance);
            } else if (userAge <= 49 && userAge <= 40) {
                return distanceWoMan40_49(distance);
            } else if (userAge > 50) {
                return distanceWoMan50(distance);
            }
        }
        return level;
    }

    /**
     * 男30岁以下
     */
    private static String distanceMan30(double distance) {
        if (distance < 2.0) {
            return level_1;
        } else if (distance >= 2.0 && distance <= 2.4) {
            return level_2;
        } else if (distance >= 2.4 && distance <= 2.8) {
            return level_3;
        } else if (distance >= 2.8) {
            return level_4;
        }
        return "";
    }

    /**
     * 男30-39
     */
    private static String distanceMan30_39(double distance) {
        if (distance < 1.8) {
            return level_1;
        } else if (distance >= 1.8 && distance <= 2.2) {
            return level_2;
        } else if (distance >= 2.2 && distance <= 2.6) {
            return level_3;
        } else if (distance >= 2.6) {
            return level_4;
        }
        return "";
    }

    /**
     * 男40-49
     */
    private static String distanceMan40_49(double distance) {
        if (distance < 1.7) {
            return level_1;
        } else if (distance >= 1.7 && distance <= 2.1) {
            return level_2;
        } else if (distance >= 2.1 && distance <= 2.5) {
            return level_3;
        } else if (distance >= 2.5) {
            return level_4;
        }
        return "";
    }

    /**
     * 男50以上
     */
    private static String distanceMan50(double distance) {
        if (distance < 1.6) {
            return level_1;
        } else if (distance >= 1.6 && distance <= 2.0) {
            return level_2;
        } else if (distance >= 2.0 && distance <= 2.4) {
            return level_3;
        } else if (distance >= 2.4) {
            return level_4;
        }
        return "";
    }
//    --------------------------------------------------------------------------

    /**
     * 女30岁以下
     */
    private static String distanceWoMan30(double distance) {
        if (distance < 1.8) {
            return level_1;
        } else if (distance >= 1.8 && distance <= 2.2) {
            return level_2;
        } else if (distance >= 2.2 && distance <= 2.6) {
            return level_3;
        } else if (distance >= 2.6) {
            return level_4;
        }
        return "";
    }

    /**
     * 女30-39
     */
    private static String distanceWoMan30_39(double distance) {
        if (distance < 1.6) {
            return level_1;
        } else if (distance >= 1.6 && distance <= 2.0) {
            return level_2;
        } else if (distance >= 2.0 && distance <= 2.4) {
            return level_3;
        } else if (distance >= 2.4) {
            return level_4;
        }
        return "";
    }

    /**
     * 女40-49
     */
    private static String distanceWoMan40_49(double distance) {
        if (distance < 1.5) {
            return level_1;
        } else if (distance >= 1.5 && distance <= 1.8) {
            return level_2;
        } else if (distance >= 1.8 && distance <= 2.3) {
            return level_3;
        } else if (distance >= 2.3) {
            return level_4;
        }
        return "";
    }

    /**
     * 女50以上
     */
    private static String distanceWoMan50(double distance) {
        if (distance < 1.4) {
            return level_1;
        } else if (distance >= 1.4 && distance <= 1.7) {
            return level_2;
        } else if (distance >= 1.7 && distance <= 2.2) {
            return level_3;
        } else if (distance >= 2.2) {
            return level_4;
        }
        return "";
    }
}
