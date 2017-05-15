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


#短信sdk



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
