package com.amsu.healthy.utils;

import android.content.Context;
import android.util.Log;

import com.amsu.healthy.R;
import com.amsu.healthy.appication.MyApplication;
import com.amsu.healthy.bean.IndicatorAssess;
import com.amsu.healthy.fragment.analysis.HRVFragment;
import com.test.objects.HeartRateResult;

import java.util.Date;

/**
 * Created by HP on 2017/2/19.
 */

public class HealthyIndexUtil {

    private static final String TAG = "HealthyIndexUtil";

    //BMI
    public static IndicatorAssess calculateScoreBMI(Context context){
        String heightSP = MyUtil.getStringValueFromSP("height");
        String weightSP = MyUtil.getStringValueFromSP("weight");
        if (!heightSP.equals("") && !heightSP.equals("null") && !weightSP.equals("") && !weightSP.equals("null")){
            float height = Float.parseFloat(heightSP)/100;
            float weight = Float.parseFloat(weightSP);
            float bmi = weight/(height*height);
            Log.i(TAG,"bmi:"+bmi);
            int scoreBMI = 0;
            String suggestion = "";
            if (bmi<16){
                //0
                scoreBMI = 0;
                suggestion = context.getResources().getString(R.string.bmi_suggetstion_lev3);
            }
            else if (16<=bmi && bmi<16.99){
                //1-30
                scoreBMI = (int) ((bmi - 16) * (30.0 / (17.0 - 16.0)));
                suggestion = context.getResources().getString(R.string.bmi_suggetstion_lev2);
            }
            else if (17<=bmi && bmi<18.49){
                //31-60
                scoreBMI = 31+(int) ((bmi - 17) * (30.0 / (18.5 - 17.0)));
                suggestion = context.getResources().getString(R.string.bmi_suggetstion_lev1);
            }
            else if (18.5<=bmi && bmi<20.99){
                //61-99
                scoreBMI = 61+(int) ((bmi - 18.5) * (30.0/ (21.0 - 18.5)));
                suggestion = context.getResources().getString(R.string.bmi_suggetstion_lev9);
            }
            else if (21<=bmi && bmi<23){
                //100
                scoreBMI = 100;
                suggestion = context.getResources().getString(R.string.bmi_suggetstion_lev1);
            }
            else if (23.1<=bmi && bmi<24.99){
                //99-61
                scoreBMI = 99-(int) ((bmi - 23.1) * (30.0 / (24.5 - 23.1)));
                suggestion = context.getResources().getString(R.string.bmi_suggetstion_lev9);
            }
            else if (25<=bmi && bmi<27.49){
                //31-60
                scoreBMI = 31+(int) ((bmi - 25) * (30.0 / (27.5 - 25)));
                suggestion = context.getResources().getString(R.string.bmi_suggetstion_lev5);
            }
            else if (27.5<=bmi && bmi<29.99){
                //1-30
                scoreBMI = (int) ((bmi - 27.5) * (30.0 / (18.5 - 27.5)));
                suggestion = context.getResources().getString(R.string.bmi_suggetstion_lev5);
            }
            else if (bmi>=30){
                //0
                scoreBMI = 0;
                suggestion =context.getResources().getString(R.string.bmi_suggetstion_lev7);
            }
            Log.i(TAG,"scoreBMI:"+scoreBMI);
            IndicatorAssess indicatorAssess = new IndicatorAssess((int) bmi,scoreBMI,"BMI",suggestion);

            return indicatorAssess;
        }
        return null;
    }

    public static int getUserAge(){
        String birthday = MyUtil.getStringValueFromSP("birthday");  //	1999-11-11
        Log.i(TAG,"birthday:"+birthday);
        if (!birthday.equals("") && !birthday.equals("null")) {
            String[] split = birthday.split("-");
            if (split.length > 0) {
                Date date = new Date();
                int age = 1900 + date.getYear() - Integer.parseInt(split[0]);
                Log.i(TAG, "age:" + age);
                return age;
            }
        }
        return 0;
    }

    //储备心率
    public static IndicatorAssess calculateScorehrReserve(Context context){
        int age = getUserAge();
        if (age!=0){
            int hrReserve;
            int restingHR = Integer.parseInt(MyUtil.getStringValueFromSP(Constant.restingHR));
            if (restingHR !=0){
                hrReserve = 220-age-restingHR;
                Log.i(TAG,"hrReserveeserve:"+hrReserve);
                int scorehrReserve = 0;
                String suggestion = "";
                if (hrReserve>=200){
                    scorehrReserve = 100;
                    suggestion =context.getResources().getString(R.string.reserverate_suggetstion_lev1);
                }
                else if (190<=hrReserve && hrReserve<=199){
                    //91-100
                    scorehrReserve = (int) (91+(hrReserve-190)*(float)((100.0-91.0)/(199.0-190.0)));
                    suggestion = context.getResources().getString(R.string.reserverate_suggetstion_lev2);
                }
                else if (160<hrReserve && hrReserve<=189){
                    //81-90
                    scorehrReserve = (int) (81+(hrReserve-160)*(float)((90.0-81.0)/(189.0-160.0)));
                    suggestion = context.getResources().getString(R.string.reserverate_suggetstion_lev3);
                }
                else if (130<=hrReserve && hrReserve<=159){
                    //71-80
                    scorehrReserve = (int) (71+(hrReserve-130)*(float)((80.0-71.0)/(159.0-130.0)));
                    suggestion = context.getResources().getString(R.string.reserverate_suggetstion_lev4);
                }
                else if (100<=hrReserve && hrReserve<=129){
                    //61-70
                    scorehrReserve = (int) (61+(hrReserve-100)*(float)((70.0-61.0)/(129.0-100.0)));
                    suggestion = context.getResources().getString(R.string.reserverate_suggetstion_lev5);
                }
                else if (70<=hrReserve && hrReserve<=99){
                    //31-60
                    scorehrReserve = (int) (31+(hrReserve-70)*(float)((60.0-31.0)/(99.0-70.0)));
                    suggestion = context.getResources().getString(R.string.reserverate_suggetstion_lev6);
                }
                else if (11<=hrReserve && hrReserve<=69){
                    //1-30
                    scorehrReserve = (int) (hrReserve*(float)(30.0/(69.0-11.0)));
                    suggestion = context.getResources().getString(R.string.reserverate_suggetstion_lev6);
                }
                else if (0<=hrReserve && hrReserve<=10){
                    //0
                    scorehrReserve =0;
                    suggestion = context.getResources().getString(R.string.reserverate_suggetstion_lev6);
                }
                Log.i(TAG,"scorehrReserve:"+scorehrReserve);
                IndicatorAssess indicatorAssess = new IndicatorAssess(hrReserve,scorehrReserve,context.getResources().getString(R.string.heart_rate_reserve),suggestion);

                return indicatorAssess;
            }
        }
        return null;
    }

    //恢复心率HRR
    public static IndicatorAssess calculateScoreHRR(){
        /*
        *   分值域描述	分数区域	恢复心率
            Bpm	恢复心率
            参考标准计算方法	建议
            完美	100	>65	100分	你是最棒的，要坚持
            非凡	99	61-65	99分	你是最棒的，要坚持
            优秀	87-98	49-60	1分/1bpm	请保持训练强度
            非常好	75-86	37-48	1分/1bpm	请保持训练强度
            好	63-74	25-36	1分/1bpm	请保持锻炼习惯
            平均水平	51-62	13-24	1分/1bpm	请提高训练强度
            差	3-50	1-12	1分/4bpm	请适度增加有氧训练
                0-2	0
        * */
        int hrr = 50;   //由算法得出  测试
        String suggestion = "";
        String state = "";

        int scoreHRR = 0;
        if (hrr>65){
            scoreHRR = 100;
            suggestion = "您是最棒的，坚持下去，没有什么人能超过您了";
            state = "完美";
        }
        else if (61<=hrr && hrr<=65){
            //99
            scoreHRR = 99;
            suggestion = "您是最棒的，坚持下去，没有什么人能超过您了";
            state = "非凡";
        }
        else if (49<=hrr && hrr<=60){
            //87-98
            scoreHRR = (int) (87+(hrr-49)*(float)((98.0-87.0)/(60.0-49.0)));
            suggestion = "棒棒的，请保持训练强度，后面的人会追上来的呦！";
            state = "优秀";
        }
        else if (37<=hrr && hrr<=48){
            //75-86
            scoreHRR = (int) (75+(hrr-37)*(float)((86.0-75.0)/(48.0-37.0)));
            suggestion = "您比较突出了，但是保持训练强度，还是有可能提高的。";
            state = "优秀";
        }
        else if (25<=hrr && hrr<=36){
            //63-74
            scoreHRR = (int) (63+(hrr-25)*(float)((74.0-63.0)/(36.0-25.0)));
            suggestion = "您已经超过一般人了，但是要保持锻炼习惯才不会下滑哦！";
            state = "较好 ";
        }
        else if (13<=hrr && hrr<=24){
            //51-62
            scoreHRR = (int) (51+(hrr-13)*(float)((62.0-51.0)/(24.0-13.0)));
            suggestion = "您处于一般水平，还有很大提升空间呢！赶快提高训练强度吧！";
            state = "一般";
        }
        else if (1<=hrr && hrr<=12){
            //3-50
            scoreHRR = (int) (3+(hrr-1)*(float)((50.0-3.0)/(12.0-1.0)));
            suggestion = "不要让人把您看扁了，请适度增加有氧训练，路漫漫其修远兮，吾将上下而求索！";
            state = "差";
        }
        else if (hrr==0){
            //0
            scoreHRR =0;
            suggestion = "不要让人把您看扁了，请适度增加有氧训练，路漫漫其修远兮，吾将上下而求索！";
            state = "差";
        }

        IndicatorAssess indicatorAssess = new IndicatorAssess(hrr,scoreHRR,"恢复心率(HRR)",suggestion,state);

        return indicatorAssess;
    }

    //恢复心率HRR
    public static IndicatorAssess calculateScoreHRR(int hrr,Context context){
        String suggestion = "";
        String state = "";

        int scoreHRR = 0;
        if (hrr>65){
            scoreHRR = 100;
            suggestion = context.getResources().getString(R.string.hrr_suggetstion_lev1);
            state = "完美";
        }
        else if (61<=hrr && hrr<=65){
            //99
            scoreHRR = 99;
            suggestion = context.getResources().getString(R.string.hrr_suggetstion_lev2);
            state = "非凡";
        }
        else if (49<=hrr && hrr<=60){
            //87-98
            scoreHRR = (int) (87+(hrr-49)*(float)((98.0-87.0)/(60.0-49.0)));
            suggestion = context.getResources().getString(R.string.hrr_suggetstion_lev3);
            state = "优秀";
        }
        else if (37<=hrr && hrr<=48){
            //75-86
            scoreHRR = (int) (75+(hrr-37)*(float)((86.0-75.0)/(48.0-37.0)));
            suggestion = context.getResources().getString(R.string.hrr_suggetstion_lev4);
            state = "优秀";
        }
        else if (25<=hrr && hrr<=36){
            //63-74
            scoreHRR = (int) (63+(hrr-25)*(float)((74.0-63.0)/(36.0-25.0)));
            suggestion = context.getResources().getString(R.string.hrr_suggetstion_lev5);
            state = "较好 ";
        }
        else if (13<=hrr && hrr<=24){
            //51-62
            scoreHRR = (int) (51+(hrr-13)*(float)((62.0-51.0)/(24.0-13.0)));
            suggestion = context.getResources().getString(R.string.hrr_suggetstion_lev6);
            state = "一般";
        }
        else if (1<=hrr && hrr<=12){
            //3-50
            scoreHRR = (int) (3+(hrr-1)*(float)((50.0-3.0)/(12.0-1.0)));
            suggestion = context.getResources().getString(R.string.hrr_suggetstion_lev7);
            state = "差";
        }

        IndicatorAssess indicatorAssess = new IndicatorAssess(hrr,scoreHRR,context.getResources().getString(R.string.heart_rate_recovery),suggestion,state);

        return indicatorAssess;
    }

    //抗疲劳指数HRV(心电分析算法得出)
    public static IndicatorAssess calculateScoreHRV(){
        /*
        *   分值域描述	分数区域	抗疲劳
            指数SDNN	抗疲劳指数
            参考标准计算方法	建议
            优秀	91-100	181-200	1分/2	保持运动习惯
            非常好	81-90	161-180	1分/2	保持运动习惯
            好	71-80	141-160	1分/2	保持运动习惯
            平均水平	61-70	111-140	1分/3	保持运动习惯，增加低心率有氧运动
            差	0-60	80-110	2分/1	运动过量，请降低运动强度
        * */

        int hrv = 150;//由算法得出  测试
        HeartRateResult heartRateResultFromSP = MyUtil.getHeartRateResultFromSP();
        if (heartRateResultFromSP==null){
            return null;
        }

        int rr_sdnn = heartRateResultFromSP.RR_SDNN;
        hrv = rr_sdnn;

        int scoreHRV = 0;
        String suggestion = "";
        String state = "";
        if (181<=hrv && hrv<=200){
            //	91-100
            scoreHRV = (int) (91+(hrv-181)*((100.0-91.0)/(200.0-181.0)));
            suggestion = "您的身体充满活力，力拔山兮气盖世！再累的锻炼都不怕哦！";
            state = "优秀";
        }
        else if (161<=hrv && hrv<=180){
            //	81-90
            scoreHRV = (int) (81+(hrv-161)*((90.0-81.0)/(180.0-161.0)));
            suggestion = "您的身体状态不错哦，可以进行一些较重负荷的锻炼计划了！";
            state = "非常好";
        }
        else if (141<=hrv && hrv<=160){
            //	71-80
            scoreHRV = (int) (71+(hrv-141)*(float)((80.0-71.0)/(160.0-141.0)));
            suggestion = "您的身体还是很有潜力的，可以承受一些轻负荷的锻炼计划了！";
            state = "好";
        }
        else if (111<=hrv && hrv<=140){
            //	61-70
            scoreHRV = (int) (61+(hrv-111)*(float)((70.0-61.0)/(140.0-111.0)));
            suggestion = "懒洋洋的，好久没有锻炼了吧？赶快结束没有激情的状态吧！";
            state = "平均水平";
        }
        else if (hrv<=110){
            //	0-60
            scoreHRV = (int) (0+(hrv-0)*(float)((60.0-0)/(110.0-0)));
            suggestion = "看起来很累的样子，请适度进行有氧训练，劳逸结合，感觉就会越来越好！";
            state = "差";
        }
        Log.i(TAG,"hrv:"+hrv+",scoreHRV:"+scoreHRV);

        IndicatorAssess indicatorAssess = new IndicatorAssess(hrv,scoreHRV,"抗疲劳指数(HRV)",suggestion,state);

        return indicatorAssess;
    }

    //抗疲劳指数HRV(心电分析算法得出)
    public static IndicatorAssess calculateScoreHRV(int hrv,Context context){
        int scoreHRV = 0;
        String suggestion = "";
        String state = "";
        //if (181<=hrv && hrv<=200){
        if (181<=hrv){
            //	91-100
            //scoreHRV = (int) (91+(hrv-181)*((100.0-91.0)/(200.0-181.0)));
            scoreHRV = ((int) (91+(10)*(hrv-181.0)/(1200-181.0)));
            suggestion = context.getResources().getString(R.string.rv_suggetstion_lev1);
            state = "优秀";
        }
        else if (161<=hrv && hrv<=180){
            //	81-90
            scoreHRV = (int) (81+(hrv-161)*((90.0-81.0)/(180.0-161.0)));
            suggestion = context.getResources().getString(R.string.rv_suggetstion_lev2);
            state = "非常好";
        }
        else if (141<=hrv && hrv<=160){
            //	71-80
            scoreHRV = (int) (71+(hrv-141)*(float)((80.0-71.0)/(160.0-141.0)));
            suggestion = context.getResources().getString(R.string.rv_suggetstion_lev3);
            state = "好";
        }
        else if (111<=hrv && hrv<=140){
            //	61-70
            scoreHRV = (int) (61+(hrv-111)*(float)((70.0-61.0)/(140.0-111.0)));
            suggestion =context.getResources().getString(R.string.rv_suggetstion_lev4);
            state = "平均水平";
        }
        else if (0<hrv && hrv<=110){
            //	0-60
            scoreHRV = (int) (0+(hrv-0)*(float)((60.0-0)/(110.0-0)));
            suggestion = context.getResources().getString(R.string.rv_suggetstion_lev5);
            state = "差";
        }
        Log.i(TAG,"hrv:"+hrv+",scoreHRV:"+scoreHRV);

        IndicatorAssess indicatorAssess = new IndicatorAssess(hrv,scoreHRV,context.getResources().getString(R.string.indicator_for_resistance_to_fatigue),suggestion,state);

        return indicatorAssess;
    }


    //过缓
    public static IndicatorAssess calculateTypeSlow(){
        /*
            优秀	100	56-60
            好	81-99	㊀46-55 ㊁61-70
            平均水平	61-80	㊀36-45 ㊁71-80
            差	0-60	㊀<36 ㊁81-105

        * */
        int over_slow = 60;//由算法得出   测试
        int slowType = 0; //默认是哦，正常。1：黄色预警。2：红色预警
        String suggestion = "您的心率很好，没有过缓现象，请坚持锻炼。";
        if (30<=over_slow && over_slow<=36){
            //	100
            slowType = 1;
            suggestion = "您发生过心率较低的现象，有心动过缓可能。建议您到医院听取医生的专业意见。";
        }
        else if (over_slow<30){
            //	81-99
            slowType = 2;
            suggestion = "您发生过心率很低的现象，有可能是因病理性或药物导致心动过缓。希望您尽快到医院进行专业的检查并诊断排除。";
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(over_slow,slowType,"心率过缓",suggestion);

        return indicatorAssess;
    }

    //过缓
    public static IndicatorAssess calculateTypeSlow(int over_slow,Context context){
        int slowType = 0; //默认是哦，正常。1：黄色预警。2：红色预警
        String suggestion = context.getResources().getString(R.string.low_suggetstion_lev1);
        if (30<=over_slow && over_slow<=36){
            //	100
            slowType = 1;
            suggestion = context.getResources().getString(R.string.low_suggetstion_lev2);
        }
        else if (over_slow<30){
            //	81-99
            slowType = 2;
            suggestion = context.getResources().getString(R.string.low_suggetstion_lev3);
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(over_slow,slowType,"心率过缓",suggestion);

        return indicatorAssess;
    }

    //过速
    public static IndicatorAssess calculateTypeOver(){
        int over_slow = 30;//由算法得出   测试
        int slowType = 0; //默认是哦，正常。1：黄色预警。2：红色预警
        String suggestion = "您的心率很好，没有过速现象，请坚持锻炼。";
        if (105<=over_slow && over_slow<=140){
            //	100
            slowType = 1;
            suggestion = "您出现了心率较高的现象，如非因跑步、饮酒、重体力劳动及情绪激动导致心律加快，则有可能是因疾病引起心动过速，建议您到医院听取医生的专业意见。";
        }
        else if (over_slow>140){
            //	81-99
            slowType = 2;
            suggestion = "您出现了心率较高的现象，如非因跑步、饮酒、重体力劳动及情绪激动导致心律加快，则有可能是因疾病引起心动过速，建议您到医院听取医生的专业意见。";
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(over_slow,slowType,"心率过速",suggestion);

        return indicatorAssess;
    }

    //过速
    public static IndicatorAssess calculateTypeOver(int over_slow,Context context){
        /*
            优秀	100	56-60
            好	81-99	㊀46-55 ㊁61-70
            平均水平	61-80	㊀36-45 ㊁71-80
            差	0-60	㊀<36 ㊁81-105

        * */
        int slowType = 0; //默认是哦，正常。1：黄色预警。2：红色预警
        String suggestion = context.getResources().getString(R.string.over_suggetstion_lev1);
        if (105<=over_slow && over_slow<=140){
            //	100
            slowType = 1;
            suggestion = context.getResources().getString(R.string.over_suggetstion_lev2);
        }
        else if (over_slow>140){
            //	81-99
            slowType = 2;
            suggestion = context.getResources().getString(R.string.over_suggetstion_lev3);
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(over_slow,slowType,"心率过速",suggestion);

        return indicatorAssess;
    }


    //早搏
    public static IndicatorAssess calculateTypeBeforeBeat(int over_slow,Context context){
        /*
            优秀	100	56-60
            好	81-99	㊀46-55 ㊁61-70
            平均水平	61-80	㊀36-45 ㊁71-80
            差	0-60	㊀<36 ㊁81-105

        * */
        int slowType = 0; //默认是哦，正常。1：黄色预警。2：红色预警
        String suggestion = context.getResources().getString(R.string.beforebeat_suggetstion_lev1);
        if (1<=over_slow && over_slow<=3){
            //	100
            slowType = 1;
            suggestion = context.getResources().getString(R.string.beforebeat_suggetstion_lev2);
        }
        else if (over_slow>3){
            //	81-99
            slowType = 2;
            suggestion = context.getResources().getString(R.string.beforebeat_suggetstion_lev3);
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(over_slow,slowType,context.getResources().getString(R.string.premature_beat),suggestion);

        return indicatorAssess;
    }

    //漏博
    public static IndicatorAssess calculateTypeMissBeat(){
        /*
            优秀	100	56-60
            好	81-99	㊀46-55 ㊁61-70
            平均水平	61-80	㊀36-45 ㊁71-80
            差	0-60	㊀<36 ㊁81-105

        * */
        int over_slow = 1;//由算法得出   测试
        int slowType = 0; //默认是哦，正常。1：黄色预警。2：红色预警
        String suggestion = "未发现漏搏现象，您的心脏很棒，可以保持当前训练强度。";
        if (over_slow>0){
            //	100
            slowType = 1;
            suggestion = "您发生过漏博现象，通常可能因为精神紧张、烟酒过度、生活不规律、夜间休息不足等原因所导致。如只出现心脏漏跳的感觉，" +
                    "没有其它诸如头晕、乏力、昏厥甚至心绞痛的感觉，则只需注意调整生活节奏保持良好生活习惯，不需要过分担心。";
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(over_slow,slowType,"漏博",suggestion);

        return indicatorAssess;
    }

    //漏博
    public static IndicatorAssess calculateTypeMissBeat(int over_slow ,Context context){
        int slowType = 0; //默认是哦，正常。1：黄色预警。2：红色预警
        String suggestion = context.getResources().getString(R.string.missbeat_suggetstion_lev1);
        if (over_slow>0){
            //	100
            slowType = 1;
            suggestion = context.getResources().getString(R.string.missbeat_suggetstion_lev2);
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(over_slow,slowType,"漏博",suggestion);

        return indicatorAssess;
    }



    //过缓/过速(心电分析算法得出)
    public static IndicatorAssess calculateScoreOver_slow(){
        /*
            优秀	100	56-60
            好	81-99	㊀46-55 ㊁61-70
            平均水平	61-80	㊀36-45 ㊁71-80
            差	0-60	㊀<36 ㊁81-105

        * */
        int over_slow = 60;//由算法得出   测试
        int scoreOver_slow = 0;
        String suggestion = "";
        if (56<=over_slow && over_slow<=60){
            //	100
            scoreOver_slow = 100;
            suggestion = "您的心率很好，没有过速和过缓现象，请坚持锻炼。";
        }
        else if ((46<=over_slow && over_slow<=55)){
            //	81-99
            scoreOver_slow = (int) (81+(over_slow-46)*(float)((99.0-81.0)/(55.0-46.0)));
            suggestion = "您的心率偏低，如果您经常保持锻炼，这正是心脏功能强大的表现。";
        }
        else if ((61<=over_slow && over_slow<=70)){
            //	81-99
            scoreOver_slow = (int) (81+(over_slow-61)*(float)((99.0-81.0)/(70.0-61.0)));
            suggestion = "您的心率偏低，如果您经常保持锻炼，这正是心脏功能强大的表现。";
        }
        else if ((36<=over_slow && over_slow<=45)){
            //	61-80
            scoreOver_slow = (int) (61+(over_slow-36)*(float)((80.0-61.0)/(45.0-36.0)));
            suggestion = "您有心率过缓倾向，如果您经常参加高负荷运动，可能是正常现象。";
        }
        else if ((71<=over_slow && over_slow<=80)){
            //	61-80
            scoreOver_slow = (int) (61+(over_slow-71)*(float)((80.0-61.0)/(80.0-71.0)));
            suggestion = "您的心率较高，如果增加有氧训练时间，提升心肺能力，可以逐渐下降。";
        }
        else if (over_slow<=36){
            //	0-60
            scoreOver_slow = (int) (0+(over_slow-0)*(float)((60.0-0.0)/(36.0-0.0)));
            suggestion = "您的心率很低，有心动过缓可能，不过不影响您锻炼身体。建议到医院听取医生的专业意见。";
        }
        else if ((81<=over_slow && over_slow<=105)){
            //	0-60
            scoreOver_slow = (int) (0+(over_slow-81)*(float)((60.0-0.0)/(105.0-81.0)));
            suggestion = "您的心率很低，有心动过缓可能，不过不影响您锻炼身体。建议到医院听取医生的专业意见。";
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(over_slow,scoreOver_slow,"过缓/过速",suggestion);

        return indicatorAssess;
    }

    //过缓/过速(心电分析算法得出)
    public static IndicatorAssess calculateScoreOver_slow(int over_slow,Context context){
        int scoreOver_slow = 0;
        String suggestion = "";
        if (56<=over_slow && over_slow<=70){
            //	100
            scoreOver_slow = 100;
            suggestion = context.getString(R.string.over_slow_suggetstion_lev1);
        }
        else if ((46<=over_slow && over_slow<=55)){
            //	81-99
            scoreOver_slow = (int) (81+(over_slow-46)*(float)((99.0-81.0)/(55.0-46.0)));
            suggestion = context.getString(R.string.over_slow_suggetstion_lev2);
        }
        /*else if ((61<=over_slow && over_slow<=70)){
            //	81-99
            scoreOver_slow = (int) (81+(over_slow-61)*(float)((99.0-81.0)/(70.0-61.0)));
            suggestion = "您的心率偏低，如果您经常保持锻炼，这正是心脏功能强大的表现。";
        }*/
        else if ((36<=over_slow && over_slow<=45)){
            //	61-80
            scoreOver_slow = (int) (61+(over_slow-36)*(float)((80.0-61.0)/(45.0-36.0)));
            suggestion = context.getString(R.string.over_slow_suggetstion_lev3);
        }
        else if ((71<=over_slow && over_slow<=80)){
            //	61-80
            scoreOver_slow = (int) (61+(over_slow-71)*(float)((80.0-61.0)/(80.0-71.0)));
            suggestion = context.getString(R.string.over_slow_suggetstion_lev4);
        }
        else if (over_slow<=36){
            //	0-60
            scoreOver_slow = (int) (0+(over_slow-0)*(float)((60.0-0.0)/(36.0-0.0)));
            suggestion = context.getString(R.string.over_slow_suggetstion_lev5);
        }
        else if ((81<=over_slow )){
            //	0-60
            //scoreOver_slow = (int) (0+(over_slow-81)*(float)((60.0-0.0)/(over_slow-81.0)));
            scoreOver_slow = 60- (int) ((Math.abs(81.0-over_slow))*(float)((60.0-0.0)/(200-81.0)));
            suggestion = context.getString(R.string.over_slow_suggetstion_lev6);
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(over_slow,scoreOver_slow,context.getString(R.string.too_fast_too_slow),suggestion);

        return indicatorAssess;
    }

    //早搏/漏搏
    public static IndicatorAssess calculateScoreBeat(){
        int prematureBeat = 1; //早搏次数   测试
        int missedBeat = 2; //漏搏次数
        int scoreBeat = 0;
        String suggestion = "";
        if (prematureBeat==0 && missedBeat==0){
            scoreBeat = 0;
            suggestion = "动态和静态累计测试时间已经大于等于180分钟，并未发现早搏漏搏现象，您的心脏很棒，可以保持当前训练强度";
        }
        else if (prematureBeat==0){
            int length = missedBeat<=5?missedBeat:5;
            scoreBeat = 100-2*length;
            suggestion = "动态和静态累计测试时间在120到179分钟之间，累计有1-5次漏搏，未发现早搏现象，通常可能因为精神紧张、吸烟、饮酒、生活不规律、夜间没有好好休息等原因所导致。如果单纯出现心脏漏跳一拍的感觉，但没有其它诸如头晕、乏力、昏厥甚至心绞痛的感觉，则不需要过分担心。还是可以保持一些较重负荷的锻炼计划的！";
        }
        else if (prematureBeat==1){
            int length = missedBeat<=10?missedBeat:10;
            scoreBeat = 80-2*length;
            suggestion = "动态和静态累计测试时间在90到119分钟之间，累计有1-5次漏搏，未发现早搏现象，通常可能因为精神紧张、吸烟、饮酒、生活不规律、夜间没有好好休息等原因所导致。如果单纯出现心脏漏跳一拍的感觉，但没有其它诸如头晕、乏力、昏厥甚至心绞痛的感觉，则不需要过分担心。可以进行一些轻负荷的锻炼计划了！";
        }
        else if (prematureBeat==2){
            int length = missedBeat<=10?missedBeat:10;
            scoreBeat = 60-2*length;
            suggestion = "动态和静态累计测试时间不低于90分钟，发现1次早搏，漏搏1-10次， 偶尔的早搏可见于正常人，漏博次数较多，如果没有诸如头晕、乏力、昏厥甚至心绞痛的感觉，则不需要过分担心。可以适当进行有氧训练，提升心肺能力。";
        }
        else if (prematureBeat==3){
            int length = missedBeat<=10?missedBeat:10;
            scoreBeat = 40-2*length;
            suggestion = "动态和静态累计测试时间不足90分钟，发现连续早搏1-3次，同时伴有漏搏1-10次，您的心脏功能不容乐观建议到医院进行详细的心电检查，请经常关注心脏是否有不适感。适度进行锻炼，劳逸结合。";
        }
        else if (prematureBeat>3){
            int length = missedBeat<=10?missedBeat:10;
            scoreBeat = 20-2*length;
            suggestion = "在已知测试时间段内发现连续早搏超过3次，同时伴有漏搏1-5次，您的心脏有病理风险，建议您尽快到医院进行详细的心电检查。运动有风险，请减少高强度运动！";
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(0,scoreBeat,"早搏/漏搏",suggestion);

        return indicatorAssess;

    }

    //早搏/漏搏
    public static IndicatorAssess calculateScoreBeat(int prematureBeat, int missedBeat,Context context){
        int scoreBeat = 0;
        String suggestion = "";
        if (prematureBeat==0 && missedBeat==0){
            scoreBeat = 100;
            suggestion = context.getString(R.string.sosreBeat_suggetstion_lev1);
        }
        else if (prematureBeat==0){
            int length = missedBeat<=5?missedBeat:5;
            scoreBeat = 100-2*length;
            suggestion = context.getString(R.string.sosreBeat_suggetstion_lev2);
        }
        else if (prematureBeat==1){
            int length = missedBeat<=10?missedBeat:10;
            scoreBeat = 80-2*length;
            suggestion = context.getString(R.string.sosreBeat_suggetstion_lev2);
        }
        else if (prematureBeat==2){
            int length = missedBeat<=10?missedBeat:10;
            scoreBeat = 60-2*length;
            suggestion = context.getString(R.string.sosreBeat_suggetstion_lev3);
        }
        else if (prematureBeat==3){
            int length = missedBeat<=10?missedBeat:10;
            scoreBeat = 40-2*length;
            suggestion = context.getString(R.string.sosreBeat_suggetstion_lev4);
        }
        else if (prematureBeat>3){
            int length = missedBeat<=10?missedBeat:10;
            scoreBeat = 20-2*length;
            suggestion = context.getString(R.string.sosreBeat_suggetstion_lev5);
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(0,scoreBeat,context.getString(R.string.premature_beat_missed_beat),suggestion);

        return indicatorAssess;

    }

    //健康储备(按训练时间计算)
    public static IndicatorAssess calculateScoreReserveHealth(){
        /*
        *   100	401-420
            99	381-400
            87-98	331-380
            75-86	281-330
            63-74	231-280
            61-70	181-230
            41-60	121-180

        * */
        int sportTime = 300;  //测试的
        int scoreReserveHealth = 0;
        String suggestion = "";
        if (401<=sportTime&& sportTime<=420){
            scoreReserveHealth = 100;
            suggestion = "您是最棒的，坚持下去，健康人生属于您！";
        }
        else if (381<=sportTime&& sportTime<=400){
            scoreReserveHealth = 99;
            suggestion = "您是最棒的，坚持下去，健康人生属于您！";
        }
        else if (331<=sportTime&& sportTime<=380){
            //87-98
            scoreReserveHealth = (int) (87+(sportTime-331)*(float)((98.0-87.0)/(380.0-331.0)));
            suggestion = "棒棒的，请保持训练强度，保护健康身体，打造美好生活！";
        }
        else if (281<=sportTime&& sportTime<=330){
            //75-86
            scoreReserveHealth = (int) (75+(sportTime-281)*(float)((86.0-75.0)/(330.0-281.0)));
            suggestion = "您比较突出了，但是保持训练强度，您的身体还是可以更好的！";
        }
        else if (231<=sportTime&& sportTime<=280){
            // 63-74
            scoreReserveHealth = (int) (63+(sportTime-231)*(float)((74.0-63.0)/(280.0-231.0)));
            suggestion = "您的健康储备已经超过一般人了，但是要保持锻炼习惯才能保持健康的身体哦！";
        }
        else if (181<=sportTime&& sportTime<=230){
            //61-70
            scoreReserveHealth = (int) (61+(sportTime-181)*(float)((70.0-61.0)/(230.0-181.0)));
            suggestion = "您的健康储备处于一般水平，还有很大提升空间呢！赶快提高训练强度吧！";
        }
        else if (121<=sportTime&& sportTime<=180){
            //41-60
            scoreReserveHealth = (int) (41+(sportTime-121)*(float)((60.0-41.0)/(180.0-121.0)));
            suggestion = "不要让人把您看扁了，请适度增加有氧训练，路漫漫其修远兮，吾将上下而求索！";
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(sportTime,scoreReserveHealth,"健康储备",suggestion);

        return indicatorAssess;
    }

    //健康储备(按训练时间计算)
    public static IndicatorAssess calculateScoreReserveHealth(int sportTime,Context context){
        /*
        *   100	401-420
            99	381-400
            87-98	331-380
            75-86	281-330
            63-74	231-280
            61-70	181-230
            41-60	121-180

        * */
        int scoreReserveHealth = 0;
        String suggestion = "";
        //if (401<=sportTime&& sportTime<=420){
        if (401<=sportTime){
            scoreReserveHealth = 100;
            suggestion = context.getResources().getString(R.string.reservehealth_suggetstion_lev1);;
        }
        else if (381<=sportTime&& sportTime<=400){
            scoreReserveHealth = 99;
            suggestion = context.getResources().getString(R.string.reservehealth_suggetstion_lev3);;
        }
        else if (331<=sportTime&& sportTime<=380){
            //87-98
            scoreReserveHealth = (int) (87+(sportTime-331)*(float)((98.0-87.0)/(380.0-331.0)));
            suggestion = context.getResources().getString(R.string.reservehealth_suggetstion_lev3);;
        }
        else if (281<=sportTime&& sportTime<=330){
            //75-86
            scoreReserveHealth = (int) (75+(sportTime-281)*(float)((86.0-75.0)/(330.0-281.0)));
            suggestion =context.getResources().getString(R.string.reservehealth_suggetstion_lev4);;
        }
        else if (231<=sportTime&& sportTime<=280){
            // 63-74
            scoreReserveHealth = (int) (63+(sportTime-231)*(float)((74.0-63.0)/(280.0-231.0)));
            suggestion = context.getResources().getString(R.string.reservehealth_suggetstion_lev5);;
        }
        else if (181<=sportTime&& sportTime<=230){
            //61-70
            scoreReserveHealth = (int) (61+(sportTime-181)*(float)((70.0-61.0)/(230.0-181.0)));
            suggestion = context.getResources().getString(R.string.reservehealth_suggetstion_lev5);;
        }
        else if (121<=sportTime&& sportTime<=180){
            //41-60
            scoreReserveHealth = (int) (41+(sportTime-121)*(float)((60.0-41.0)/(180.0-121.0)));
            suggestion = context.getResources().getString(R.string.reservehealth_suggetstion_lev6);
        }
        else if (0<=sportTime&& sportTime<=120){
            //41-60
            scoreReserveHealth = (int) (0+(sportTime-0)*(float)((41.0-0.0)/(120.0-0.0)));
            suggestion = context.getResources().getString(R.string.reservehealth_suggetstion_lev7);
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(sportTime,scoreReserveHealth,context.getResources().getString(R.string.health_reserve),suggestion);

        return indicatorAssess;
    }

    //计算生理年龄
    /*public static int calculatePhysicalAgeDValue(){
        //BMI
        IndicatorAssess scoreBMI = HealthyIndexUtil.calculateScoreBMI();
        //储备心率
        IndicatorAssess scorehrReserve = HealthyIndexUtil.calculateScorehrReserve();
        //恢复心率HRR
        IndicatorAssess scoreHRR = HealthyIndexUtil.calculateScoreHRR();
        //抗疲劳指数HRV(心电分析算法得出)
        IndicatorAssess scoreHRV = HealthyIndexUtil.calculateScoreHRV();
        // 健康储备(按训练时间计算)
        IndicatorAssess scoreReserveHealth = HealthyIndexUtil.calculateScoreReserveHealth();

        if (scoreBMI!=null && scorehrReserve!=null && scoreHRR!=null && scoreHRV!=null && scoreReserveHealth!=null){
            double v = 5 * 0.15 * (scoreBMI.getPercent() - 60) / 100 + 5 * 0.15 * (scorehrReserve.getPercent() - 60) / 100 + 5 * 0.15 * (scoreHRR.getPercent() - 60) / 100 +
                    5 * 0.15 * (scoreHRV.getPercent() - 60) / 100 + 5 * 0.40 * (scoreReserveHealth.getPercent() - 60) / 100;
            return (int) (getUserAge() - v);
        }
        return 0;

    }*/

    //计算生理年龄
    public static int calculatePhysicalAgeDValue(IndicatorAssess scoreBMI, IndicatorAssess scorehrReserve, IndicatorAssess scoreHRR, IndicatorAssess scoreHRV ,
                                                 IndicatorAssess scoreReserveHealth){
        if (scoreBMI!=null && scorehrReserve!=null && scoreHRR!=null && scoreHRV!=null && scoreReserveHealth!=null){
            double v = 5 * 0.15 * (scoreBMI.getPercent() - 60) / 100 + 5 * 0.15 * (scorehrReserve.getPercent() - 60) / 100 + 5 * 0.15 * (scoreHRR.getPercent() - 60) / 100 +
                    5 * 0.15 * (scoreHRV.getPercent() - 60) / 100 + 5 * 0.40 * (scoreReserveHealth.getPercent() - 60) / 100;
            //差值范围为-3~5，实际算出来的范围是-3~2
            if (v>0){
                v = v*2.5;
            }
            return (int) v;
        }
        return 0;
    }

    //计算健康指标
    public static int calculateIndexvalue(IndicatorAssess scoreBMI,IndicatorAssess scorehrReserve,IndicatorAssess scoreHRR,IndicatorAssess scoreHRV,
                                          IndicatorAssess scoreOver_slow,IndicatorAssess scoreBeat,IndicatorAssess scoreReserveHealth){
        /*//BMI
        IndicatorAssess scoreBMI = HealthyIndexUtil.calculateScoreBMI();
        //储备心率
        IndicatorAssess scorehrReserve = HealthyIndexUtil.calculateScorehrReserve();
        //恢复心率HRR
        IndicatorAssess scoreHRR = HealthyIndexUtil.calculateScoreHRR();
        //抗疲劳指数HRV(心电分析算法得出)
        IndicatorAssess scoreHRV = HealthyIndexUtil.calculateScoreHRV();
        //过缓/过速(心电分析算法得出)
        IndicatorAssess scoreOver_slow = HealthyIndexUtil.calculateScoreOver_slow();
        //早搏 包括房早搏APB和室早搏VPB，两者都记为早搏(心电分析算法得出)
        IndicatorAssess scoreBeat = HealthyIndexUtil.calculateScoreBeat();
        // 健康储备(按训练时间计算)
        IndicatorAssess scoreReserveHealth = HealthyIndexUtil.calculateScoreReserveHealth();*/

        if ( scoreBMI!=null && scorehrReserve!=null && scoreHRR!=null && scoreHRV!=null && scoreReserveHealth!=null && scoreBeat!=null && scoreOver_slow!=null){
            Log.i(TAG,"calculateIndexvalue======="+"scoreBMI:"+scoreBMI.getPercent()+",scorehrReserve:"+scorehrReserve.getPercent()+",scoreHRR:"+scoreHRR.getPercent()+",scoreHRV:"+scoreHRV.getPercent()+
                    ",scoreOver_slow:"+scoreOver_slow.getPercent()+",scoreBeat:"+scoreBeat.getPercent()+",scoreReserveHealth:"+scoreReserveHealth.getPercent());

            return (int) (scoreBMI.getPercent()*0.125+scorehrReserve.getPercent()*0.125+scoreHRR.getPercent()*0.125+scoreHRV.getPercent()*0.125+scoreOver_slow.getPercent()*0.125+
                    scoreBeat.getPercent()*0.125+scoreReserveHealth.getPercent()*0.25);
        }
        return 0;
    }

    //情绪指数（精神紧张或放松状态）LF/HF   SDNN:80-200
    public static IndicatorAssess calculateLFHFMoodIndex(){
        int LF_HF=0;

        HeartRateResult heartRateResultFromSP = MyUtil.getHeartRateResultFromSP();
        if (heartRateResultFromSP==null){
            return null;
        }
        LF_HF = (int) (heartRateResultFromSP.LF/heartRateResultFromSP.HF);
        int state = 0;
        String suggestion = "";
        if (LF_HF>150){
            state = 90;
            suggestion="您当前处于情绪高度紧张状态，也可能是一直专注于做一件事情，请适当放松一下。";
        }
        else if (120<LF_HF && LF_HF<=150){
            state = 80;
            suggestion="您当前处于情绪比较紧张状态，换件事情做，也可以换换心情。";
        }
        else if (80<LF_HF && LF_HF<120){
            state = 60;
            suggestion="您当前处于情绪紧张状态，您的注意力还是满集中的。";
        }
        else if (20<LF_HF && LF_HF<=80){
            state = 40;
            suggestion="您当前情绪比较放松，懒洋洋的心情不错吧";
        }
        else if (2<LF_HF && LF_HF<20){
            state = 30;
            suggestion="您当前情绪放松，内心在休息的时候，谁也打扰不了你。";
        }
        else if (0<LF_HF && LF_HF<=2){
            state = 10;
            suggestion="您当前情绪很放松，看起来无所事事的样子了。";
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(LF_HF,state,"情绪指数",suggestion);
        return indicatorAssess;

    }

    //情绪指数（精神紧张或放松状态）LF/HF   SDNN:80-200
    public static IndicatorAssess calculateLFHFMoodIndex(double LF_HF){
        int state = 0;
        String suggestion = "";
        if (LF_HF>150){
            state = 90;
            suggestion="您当前处于情绪高度紧张状态，也可能是一直专注于做一件事情，请适当放松一下。";
        }
        else if (120<LF_HF && LF_HF<=150){
            state = 80;
            suggestion="您当前处于情绪比较紧张状态，换件事情做，也可以换换心情。";
        }
        else if (80<LF_HF && LF_HF<120){
            state = 60;
            suggestion="您当前处于情绪紧张状态，您的注意力还是满集中的。";
        }
        else if (20<LF_HF && LF_HF<=80){
            state = 40;
            suggestion="您当前情绪比较放松，懒洋洋的心情不错吧";
        }
        else if (2<LF_HF && LF_HF<20){
            state = 30;
            suggestion="您当前情绪放松，内心在休息的时候，谁也打扰不了你。";
        }
        else if (0<=LF_HF && LF_HF<=2){
            state = 10;
            suggestion="您当前情绪很放松，看起来无所事事的样子了。";
        }

        /*if (LF_HF>=60){
            LF_HF = 60;
        }
        state = (int) (100*(1-LF_HF/60.0));*/

        IndicatorAssess indicatorAssess = new IndicatorAssess((int)LF_HF,state,"情绪指数",suggestion,"");
        return indicatorAssess;
    }

    //抗压指数 ---精神疲劳状态（SDNN）
    public static IndicatorAssess calculateSDNNPressureIndex(){
        int sdnn=150;
        HeartRateResult heartRateResultFromSP = MyUtil.getHeartRateResultFromSP();
        if (heartRateResultFromSP==null){
            return null;
        }
        sdnn = heartRateResultFromSP.RR_SDNN;
        int stateScore = 0;
        String suggestion = "";
        if (sdnn>170){
            stateScore = 90;
        }
        else if (sdnn<140){
            stateScore = 10;
        }
        else if (140<=sdnn && sdnn<=170){
            stateScore = (int) (100*(float)((sdnn-140.0)/(170.0-140.0)));
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(sdnn,stateScore,"抗压指数",suggestion);
        return indicatorAssess;
    }

    //抗压指数 ---精神疲劳状态（SDNN）
    public static IndicatorAssess calculateSDNNPressureIndex(int sdnn){
        int stateScore = 0;
        String suggestion = "";
       /* if (sdnn>170){
            stateScore = 90;
        }
        else if (sdnn<140){
            stateScore = 10;
        }
        else if (140<=sdnn && sdnn<=170){
            stateScore = 10+(int) (80*(float)((sdnn-140.0)/(170.0-140.0)));
        }*/

       if (sdnn>290){
           sdnn = 290;
       }
        stateScore = (int) (95*(1-sdnn/300.0));
        IndicatorAssess indicatorAssess = new IndicatorAssess(sdnn,stateScore,"抗压指数",suggestion,"");
        return indicatorAssess;
    }

    //运动疲劳（SDNN）
    public static IndicatorAssess calculateSDNNSportIndex(){
        int sdnn=130;

        HeartRateResult heartRateResultFromSP = MyUtil.getHeartRateResultFromSP();
        if (heartRateResultFromSP==null){
            return null;
        }

        int rr_sdnn = heartRateResultFromSP.RR_SDNN;
        sdnn = rr_sdnn;

        int state = 0;
        String suggestion = "";
        if (181<sdnn && sdnn<=200){
            state = 10;
            suggestion="您的身体充满活力，但是精神非常疲惫，适当做一些较高强度体育锻炼，劳逸结合才能保持身心健康!";
        }
        else if (161<sdnn && sdnn<180){
            state = 30;
            suggestion="您的身体状态不错哦，但是精神有些疲累，不要想太多事情，通过进行一些体能训练来调整一下！";
        }
        else if (141<sdnn && sdnn<=160){
            state = 50;
            suggestion="您的体能很有潜力的，精神满满，注意保持不要用脑过度，还可以进行一些轻负荷的健身锻炼！";
        }
        else if (111<sdnn && sdnn<=140){
            state = 70;
            suggestion="你的身体似乎比较疲惫，但刚好是您思维活跃的时段，正是发挥您聪明才智的好机会，做一些的轻体力偏重脑力的活动吧！";
        }
        else if (0<sdnn && sdnn<=111){
            state = 90;
            suggestion=" 您看起来身体很疲惫，你需要休息了，但是你有一个充满活力的大脑，想一想自己还有那些没有解决的问题吧，可能灵感就在眼前！";
        }
        IndicatorAssess indicatorAssess = new IndicatorAssess(sdnn,state,"运动疲劳",suggestion);
        return indicatorAssess;
    }

    //运动疲劳（SDNN）
    public static IndicatorAssess calculateSDNNSportIndex(int sdnn){
        int state = 0;
        String suggestion = "";


        /*if (sdnn>200){
            state = 5;
            suggestion="您的身体充满活力。";
        }
        else if (181<sdnn && sdnn<=200){
            state = 10;
            suggestion="您的身体充满活力，但是精神非常疲惫，适当做一些较高强度体育锻炼，劳逸结合才能保持身心健康!";
        }
        if (181<sdnn && sdnn<=200){
            state = 10;
            suggestion="您的身体充满活力，但是精神非常疲惫，适当做一些较高强度体育锻炼，劳逸结合才能保持身心健康!";
        }
        else if (161<sdnn && sdnn<=180){
            state = 30;
            suggestion="您的身体状态不错哦，但是精神有些疲累，不要想太多事情，通过进行一些体能训练来调整一下！";
        }
        else if (141<sdnn && sdnn<=160){
            state = 50;
            suggestion="您的体能很有潜力的，精神满满，注意保持不要用脑过度，还可以进行一些轻负荷的健身锻炼！";
        }
        else if (111<sdnn && sdnn<=140){
            state = 70;
            suggestion="你的身体似乎比较疲惫，但刚好是您思维活跃的时段，正是发挥您聪明才智的好机会，做一些的轻体力偏重脑力的活动吧！";
        }
        else if (0<sdnn && sdnn<=111){
            state = 90;
            suggestion=" 您看起来身体很疲惫，你需要休息了，但是你有一个充满活力的大脑，想一想自己还有那些没有解决的问题吧，可能灵感就在眼前！";
        }
*/
        if (sdnn>=290){
            sdnn = 290;
        }
        state = (int) (95*(sdnn/300.0));

        IndicatorAssess indicatorAssess = new IndicatorAssess(sdnn,state,"运动疲劳",suggestion);
        return indicatorAssess;
    }


    public static String getHeartRateSuggetstion(int state,int heartRate,Context context){
        String suggestion;
        if (state == 1) {
            if (heartRate > 220- HealthyIndexUtil.getUserAge()) {
                suggestion =context.getResources().getString(R.string.HeartRate_suggetstion_fast);
            }else if(heartRate < 45 && heartRate >0){
                suggestion =context.getResources().getString(R.string.HeartRate_suggetstion_slow);
            }else if(heartRate == 0){
                suggestion =context.getResources().getString(R.string.HeartRate_suggetstion_nodata);
            }else{
                suggestion = context.getResources().getString(R.string.HeartRate_suggetstion_normal);
            }
        }
        else {
            if (heartRate>105){
                suggestion =context.getResources().getString(R.string.HeartRate_suggetstion_fast);
            }
            else if (heartRate<45){
                suggestion =context.getResources().getString(R.string.HeartRate_suggetstion_slow);
            }
            else if (heartRate==0){
                suggestion =context.getResources().getString(R.string.HeartRate_suggetstion_nodata);
            }
            else{
                suggestion =context.getResources().getString(R.string.HeartRate_suggetstion_normal);
            }
        }
        return suggestion;
    }

    public static String  getHRVSuggetstion(int sdnn, double lf, Context context){
        String defaultSuggetstion = context.getResources().getString(R.string.HeartRate_suggetstion_nodata);
        if (sdnn >=280 || lf<2) {
            return context.getResources().getString(R.string.hrv_suggetstion_lev1);
        }else if ((sdnn >=200 && sdnn<280) ||(lf>=2 && lf<=5)){
            return context.getResources().getString(R.string.hrv_suggetstion_lev2);
        }else if ((sdnn>=120 && sdnn<200 )||(lf>5 && lf<=30)){
            return context.getResources().getString(R.string.hrv_suggetstion_lev3);
        }else if((sdnn >=60 &&sdnn<120) ||(lf>5 && lf<=30)){
            return context.getResources().getString(R.string.hrv_suggetstion_lev4);
        }else if (sdnn <60 || lf> 30){
            return context.getResources().getString(R.string.hrv_suggetstion_lev5);
        }
        else {
            return  defaultSuggetstion;
        }
    }


    public static int judgeHRVMentalFatigueData(HeartRateResult heartRateResultPre5Min, HeartRateResult heartRateResultLater5Min,HeartRateResult heartRateResult) {
        /*1、	不疲劳
        SDNN1<120	&	SDNN2<120	&	-0.5<(SDNN2-SDNN1)/5<0.5
        2、	有点疲劳
        0.5<(SDNN2-SDNN1)/5<1	||	(140>SDNN2>120	&	HF/LF2>HF/LF1)
        3、	比较疲劳
                (SDNN2-SDNN1)/5>1	||
                (160>SDNN2>140	&	（HF/LF2-HF/LF1）/HF/LF1>0.05)
        4、	极度疲劳
        SDNN2>160	&
                -0.5<(SDNN2-SDNN1)/5<0.5		&
                -0.05<(HF/LF2-HF/LF1)/HF/LF1<0.05
        5、	保持上一次判断结果
                除符合以上四点的其他情况*/
        int state;

        if (heartRateResultPre5Min.RR_SDNN<120 && heartRateResultLater5Min.RR_SDNN<120 && (heartRateResultLater5Min.RR_SDNN-heartRateResultPre5Min.RR_SDNN)/5<0.5 &&
                (heartRateResultLater5Min.RR_SDNN-heartRateResultPre5Min.RR_SDNN)/5>-0.5){
            state = 1;
        }
        else if (((heartRateResultLater5Min.RR_SDNN-heartRateResultPre5Min.RR_SDNN)/5<1 && (heartRateResultLater5Min.RR_SDNN-heartRateResultPre5Min.RR_SDNN)/5>0.5) ||
                ((heartRateResultLater5Min.RR_SDNN<140 && heartRateResultLater5Min.RR_SDNN>120) && heartRateResult.HF/heartRateResultLater5Min.LF>heartRateResult.HF/heartRateResultPre5Min.HF)){
            state = 2;
        }
        else if ((heartRateResultLater5Min.RR_SDNN-heartRateResultPre5Min.RR_SDNN)/5>1 || ((heartRateResultLater5Min.RR_SDNN>140 && heartRateResultLater5Min.RR_SDNN<160)
                && (heartRateResult.HF/heartRateResultLater5Min.LF-heartRateResult.HF/heartRateResultPre5Min.HF)/heartRateResult.HF/heartRateResultPre5Min.LF>0.05)){
            state = 3;
        }
        else if (heartRateResultLater5Min.RR_SDNN>160 && (heartRateResultLater5Min.RR_SDNN-heartRateResultPre5Min.RR_SDNN)/5<0.5 &&
                (heartRateResultLater5Min.RR_SDNN-heartRateResultPre5Min.RR_SDNN)/5>-0.5
                && (heartRateResult.HF/heartRateResultLater5Min.LF-heartRateResult.HF/heartRateResultPre5Min.HF)/heartRateResult.HF/heartRateResultPre5Min.LF<0.05
                && (heartRateResult.HF/heartRateResultLater5Min.LF-heartRateResult.HF/heartRateResultPre5Min.HF)/heartRateResult.HF/heartRateResultPre5Min.LF>-0.05){
            state = 4;
        }
        else {
            state = 5;
        }
        return state;
    }

    //精神疲劳度
    public static HRVFragment.HRVResult judgeHRVMentalFatigueData(Context context,double HF, double LF, double HF1, double LF1, int SDNN1, double HF2, double LF2, int SDNN2, int t) {
        Log.i(TAG,",HF:"+HF+",LF:"+LF+",HF1:"+HF1+",LF1:"+LF1+",SDNN1:"+SDNN1+",HF2:"+HF2+",LF2:"+LF2+",SDNN2:"+SDNN2+",t:"+t);
        int state = 0;
        String suggestion = "";
        /*1、	不疲劳
        SDNN1<120	&	SDNN2<120	&	-0.5<(SDNN2-SDNN1)/5<0.5
        2、	有点疲劳
        0.5<(SDNN2-SDNN1)/5<1	||	(140>SDNN2>120	&	HF/LF2>HF/LF1)
        3、	比较疲劳
                (SDNN2-SDNN1)/5>1	||
                (160>SDNN2>140	&	（HF/LF2-HF/LF1）/HF/LF1>0.05)
        4、	极度疲劳
        SDNN2>160	&
                -0.5<(SDNN2-SDNN1)/5<0.5		&
                -0.05<(HF/LF2-HF/LF1)/HF/LF1<0.05
        5、	保持上一次判断结果
                除符合以上四点的其他情况
*/
        try {
            if ( SDNN1<120	&&	SDNN2<120	&&	(SDNN2-SDNN1)/t>-0.5&&(SDNN2-SDNN1)/t<0.5){
                state  = 1;
                //suggestion = "您现在精神满满，正是发挥您聪明才智的好机会，想一想自己还有那些没有解决的问题吧，可能灵感就在眼前。";
                suggestion =  context.getResources().getString(R.string.hrv_mental_fatigue_suggestion1);
            }
            else if (((SDNN2-SDNN1)/t>0.5&&(SDNN2-SDNN1)/t<1) || (SDNN2<140 && SDNN2>120 && HF/LF2>HF/LF1)){
                state  = 2;
                //suggestion = "您现在精神状态良好，您可以继续保持当前状态进行脑力活动。";
                suggestion =  context.getResources().getString(R.string.hrv_mental_fatigue_suggestion2);
            }
            else if ((SDNN2-SDNN1)/t>1	|| (SDNN2<160 && SDNN2>140 &&(HF/LF2-HF/LF1)/HF/LF1>0.01*t)){
                state  = 3;
                //suggestion = "您现在精神较为疲劳，试着放下手中的工作休息一下，缓解疲劳吧。";
                suggestion =  context.getResources().getString(R.string.hrv_mental_fatigue_suggestion3);
            }
            else if ( SDNN2>160	& (SDNN2-SDNN1)/t>-0.5&&(SDNN2-SDNN1)/t<0.5 && (HF/LF2-HF/LF1)/HF/LF1>-0.05*t && (HF/LF2-HF/LF1)/HF/LF1<0.01*t){
                state  = 4;
                //suggestion = "您现在精神非常疲惫，这将影响您的工作效率，建议您适当减轻脑力劳动，离开办公桌释放一下自己吧!";
                suggestion =  context.getResources().getString(R.string.hrv_mental_fatigue_suggestion4);
            }
            else {
                state = 0;
                suggestion = "";
            }
        }catch (ArithmeticException e){
            e.printStackTrace();
        }
        return new HRVFragment.HRVResult(state,suggestion);

    }

    //身体疲劳度 静态
    public static HRVFragment.HRVResult judgeHRVPhysicalFatigueStatic(Context context,double HF, double LF, double HF3, double LF3, int SDNN3, double HF4, double LF4, int SDNN4,int t) {
        int state = 0;
        String suggestion = "";
        //静态
        /*1、	非运动状态,不疲劳
        SDNN4>120	||
                ((SDNN4-SDNN3)/SDNN3>0.05	&	(LF/HF4-LF/HF3)/LF/HF3>0.05	&	SDNN3>70)
        2、	非运动状态,比较疲劳
                (50<SDNN4<70	&	-0.05<(LF/HF4-LF/HF3)/LF/HF3<0.05)	||	0.1>(LF/HF4-LF/HF3)/LF/HF3>0.05
        3、	非运动状态,非常疲劳
        SDNN4<50	&	-0.05<(LF/HF4-LF/HF3)/LF/HF3<0.05)	||	 ((LF/HF4-LF/HF3)/LF/HF3>0.1	&	SDNN4<70)*/

        try {
            if (SDNN4>120 || ((SDNN4-SDNN3)/SDNN3>0.01*t && (LF/HF4-LF/HF3)/LF/HF3>0.01*t && SDNN3>70)){
                state = 1;
                //suggestion = "您现在体能充沛，现在进行体育锻炼将能取得良好的效果，快去进行一些运动吧。";
                suggestion =  context.getResources().getString(R.string.hrv_body_fatigue_rest_suggestion1);
            }
            else if ((SDNN4>50&&SDNN4<70 && ((LF/HF4-LF/HF3)/LF/HF3>-0.01*t && (LF/HF4-LF/HF3)/LF/HF3<0.01*t)) || (LF/HF4-LF/HF3)/LF/HF3<0.02*t&&(LF/HF4-LF/HF3)/LF/HF3>0.01*t){
                state = 2;
                //suggestion = "您现在体力消耗较多，建议您现在不要进行体力运动，适当进行休息吧。";
                suggestion =  context.getResources().getString(R.string.hrv_body_fatigue_rest_suggestion2);
            }
            else if ((SDNN4 <50 && (LF/HF4-LF/HF3)/LF/HF3>-0.01*t && (LF/HF4-LF/HF3)/LF/HF3<0.01*t) || ((LF/HF4-LF/HF3)/LF/HF3>0.02*t&&SDNN4<70)){
                state = 3;
                //suggestion = "您的体力似乎已经消耗殆尽，请不要过度透支自己的体力，放松下自己的疲惫的身体吧！";
                suggestion =  context.getResources().getString(R.string.hrv_body_fatigue_rest_suggestion3);
            }
            else {
                state = 0;
                suggestion = "";
            }
        }catch (ArithmeticException e){
            e.printStackTrace();
        }

        return new HRVFragment.HRVResult(state,suggestion);
    }

    //身体疲劳度 动态
    public static HRVFragment.HRVResult judgeHRVPhysicalFatigueSport(Context context,double HF, double LF, double HF1, double LF1, int SDNN1, double HF2, double LF2, int SDNN2,int t) {
        int state = 0;
        String suggestion = "";
        //动态
        /*(SDNN2-SDNN1)/SDNN1<0.2	&	SDNN1>70	||	(LF/HF2-LF/HF1)/LF/HF1<5
        5、	运动状态,比较疲劳
        0.2<(SDNN2-SDNN1)/SDNN1<0.5	||	5<(LF/HF2-LF/HF1)/LF/HF1<10
        6、	运动状态,非常疲劳
        0.5<(SDNN2-SDNN1)/SDNN1	||	（10<(LF/HF2-LF/HF1)/LF/HF1	&	SDNN2<50）*/

        try {
            if ((SDNN2 - SDNN1) / SDNN1 < 0.04*t && SDNN1 > 70 || (LF / HF2 - LF / HF1) / LF / HF1 < t) {
                state = 1;
                //suggestion = "您刚才的运动量较少，体能还较为充足，还可以继续进行适度的体育锻炼达到更好的锻炼效果。";
                suggestion =  context.getResources().getString(R.string.hrv_body_fatigue_active_suggestion1);
            } else if (((SDNN2 - SDNN1) / SDNN1 > 0.04*t && (SDNN2 - SDNN1) / SDNN1 < 0.1*t) || ((LF / HF2 - LF / HF1) / LF / HF1 > t && (LF / HF2 - LF / HF1) / LF / HF1 < 2*t)) {
                state = 2;
                //suggestion = "您的体能消耗较大，建议您适当休息，劳逸结合才能达到最佳的锻炼效果。";
                suggestion =  context.getResources().getString(R.string.hrv_body_fatigue_active_suggestion2);
            } else if (0.1*t < (SDNN2 - SDNN1) / SDNN1 || (2*t < (LF / HF2 - LF / HF1) / LF / HF1 && SDNN2 < 50)) {
                state = 3;
                //suggestion = "您的体力已经耗尽了，注意不要过度透支自己的体能，停下运动休息一下吧。";
                suggestion =  context.getResources().getString(R.string.hrv_body_fatigue_active_suggestion3);
            } else {
                state = 0;
                suggestion = "";
            }
        } catch (ArithmeticException e){
                e.printStackTrace();
        }
        return new HRVFragment.HRVResult(state,suggestion);
    }


    public static void calcuIndexWarringHeartIcon(IndicatorAssess scoreSlow, IndicatorAssess scoreOver, IndicatorAssess zaoboIndicatorAssess, IndicatorAssess louboIndicatorAssess) {
        Log.i(TAG,"scoreSlow:"+scoreSlow);
        Log.i(TAG,"scoreOver:"+scoreOver);
        Log.i(TAG,"zaoboIndicatorAssess:"+zaoboIndicatorAssess);
        Log.i(TAG,"louboIndicatorAssess:"+louboIndicatorAssess);
        int IndexWarringHeartIconType = 1;
        int yellowCount = 0;
        int redCount = 0;

        if (scoreSlow!=null){
            if (scoreSlow.getPercent()==1){
                yellowCount++;
            }
            else if (scoreSlow.getPercent()==2){
                redCount++;
            }
        }

        if (scoreOver!=null){
            if (scoreOver.getPercent()==1){
                yellowCount++;
            }
            else if (scoreOver.getPercent()==2){
                redCount++;
            }
        }

        if (zaoboIndicatorAssess!=null){
            if (zaoboIndicatorAssess.getPercent()==1){
                yellowCount++;
            }
            else if (zaoboIndicatorAssess.getPercent()==2){
                redCount++;
            }
        }

        if (louboIndicatorAssess!=null){
            if (louboIndicatorAssess.getPercent()==1){
                yellowCount++;
            }
            else if (louboIndicatorAssess.getPercent()==2){
                redCount++;
            }
        }

        if (redCount>0){
            IndexWarringHeartIconType = 4;
        }
        else if (yellowCount==3 || yellowCount==4){
            IndexWarringHeartIconType = 3;
        }
        else if (yellowCount==1 || yellowCount==2){
            IndexWarringHeartIconType = 2;
        }
        else {
            IndexWarringHeartIconType = 1;
        }

        MyUtil.putIntValueFromSP("IndexWarringHeartIconType",IndexWarringHeartIconType);
        Log.i(TAG,"indexWarringHeartIconType:"+IndexWarringHeartIconType);
        MyApplication.IndexWarringHeartIconType = IndexWarringHeartIconType;

    }
}


