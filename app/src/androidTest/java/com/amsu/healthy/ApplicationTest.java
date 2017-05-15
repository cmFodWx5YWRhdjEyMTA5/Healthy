package com.amsu.healthy;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.amsu.healthy.utils.Constant;
import com.test.objects.HeartRateResult;
import com.test.utils.DiagnosisNDK;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {
        super(Application.class);
        int[] calcuData = new int[1002460];

        System.out.print("calcuData.length"+calcuData.length);
        /*HeartRateResult heartRateResult = DiagnosisNDK.AnalysisEcg(calcuData, calcuData.length, Constant.oneSecondFrame);

        System.out.print("heartRateResult");
        System.out.print("heartRateResult.toString()"+heartRateResult.toString());*/

    }


    public void testEcg(){
       Log.i("Test","ok");

        int[] calcuData = new int[1002460];

        System.out.print("calcuData.length"+calcuData.length);
        HeartRateResult heartRateResult = DiagnosisNDK.AnalysisEcg(calcuData, calcuData.length, Constant.oneSecondFrame);

        System.out.print("heartRateResult");
        System.out.print("heartRateResult.toString()"+heartRateResult.toString());
    }
}