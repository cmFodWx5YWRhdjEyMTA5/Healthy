# Add project specific ProGuard rules here.
# By default_1, the flags in this file are appended to flags specified
# in H:\professionSoft\androidSDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}



#-libraryjars libs/AMap_Location_V3.3.0_20170118.jar
#-libraryjars libs/Android_Map3D_SDK_V5.0.0_20170311.jar

#-libraryjars src/main/jniLibs/arm64-v8a/libecg.so
#-libraryjars src/main/jniLibs/armeabi/libecg.so
#-libraryjars src/main/jniLibs/armeabi-v7a/libecg.so
#-libraryjars src/main/jniLibs/mips/libecg.so
#-libraryjars src/main/jniLibs/mips64/libecg.so
#-libraryjars src/main/jniLibs/x86/libecg.so
#-libraryjars src/main/jniLibs/x86_64/libecg.so


-dontwarn com.amap.api.**
-dontwarn com.a.a.**
-dontwarn com.autonavi.**
-dontwarn com.loc.**


-keep class com.amap.api.** {*;}
-keep class com.autonavi.** {*;}
-keep class com.a.a.** {*;}
-keep class com.loc.**


#友盟
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.amsu.healthy.R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}

-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep public class **.R$*{
   public static final int *;
}

#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }


#短信sdk
# SMSSDK
-dontwarn com.mob.**
-keep class com.mob.**{*;}
-dontwarn cn.smssdk.**
-keep class cn.smssdk.**{*;}




-keep class com.ble.**{*;}


-keep class com.lidroid.** { *; }


-keep class com.google.**{*;}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}



-ignorewarnings

-keepattributes Signature,*Annotation*

# keep BmobSDK
-dontwarn cn.bmob.v3.**
-keep class cn.bmob.v3.** {*;}

# 确保JavaBean不被混淆-否则gson将无法将数据解析成具体对象
-keep class * extends cn.bmob.v3.BmobObject {
    *;
}
-keep class com.example.bmobexample.bean.BankCard{*;}
-keep class com.example.bmobexample.bean.GameScore{*;}
-keep class com.example.bmobexample.bean.MyUser{*;}
-keep class com.example.bmobexample.bean.Person{*;}
-keep class com.example.bmobexample.file.Movie{*;}
-keep class com.example.bmobexample.file.Song{*;}
-keep class com.example.bmobexample.relation.Post{*;}
-keep class com.example.bmobexample.relation.Comment{*;}

# keep BmobPush
-dontwarn  cn.bmob.push.**
-keep class cn.bmob.push.** {*;}

# keep okhttp3、okio
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
-keep interface okhttp3.** { *; }
-dontwarn okio.**

# keep rx
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# 如果你需要兼容6.0系统，请不要混淆org.apache.http.legacy.jar
-dontwarn android.net.compatibility.**
-dontwarn android.net.http.**
-dontwarn com.android.internal.http.multipart.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**
-keep class android.net.compatibility.**{*;}
-keep class android.net.http.**{*;}
-keep class com.android.internal.http.multipart.**{*;}
-keep class org.apache.commons.**{*;}
-keep class org.apache.http.**{*;}




-keep class sun.misc.Unsafe { *; }

-keep class com.google.gson.** { *; }

# Application classes that will be serialized/deserialized over Gson

#-keep class com.google.gson.examples.android.model.** { *; }

-keep class com.amsu.healthy.bean.** {*;}  #这句非常重要，主要是滤掉XXXX.entity包下的所有.class文件不进行混淆编译
-keep class com.amsu.healthy.utils.** {*;}  #这句非常重要，主要是滤掉XXXX.entity包下的所有.class文件不进行混淆编译
-keep class com.amsu.healthy.activity.** {*;}  #这句非常重要，主要是滤掉XXXX.entity包下的所有.class文件不进行混淆编译
-keep class com.amsu.healthy.service.** {*;}  #这句非常重要，主要是滤掉XXXX.entity包下的所有.class文件不进行混淆编译
-keep class com.test.** {*;}  #这句非常重要，主要是滤掉XXXX.entity包下的所有.class文件不进行混淆编译
-keep class com.amsu.bleinteraction.** {*;}  #这句非常重要，主要是滤掉XXXX.entity包下的所有.class文件不进行混淆编译

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-keep class no.nordicsemi.android.dfu.** { *; }


-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}