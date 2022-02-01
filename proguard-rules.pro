-dontoptimize
-dontobfuscate
-keepkotlinmetadata

-dontwarn kotlinx.**
-dontwarn ch.qos.logback.**
-dontwarn com.sun.jna.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.h2.**
-dontwarn org.slf4j.**

-dontwarn javax.servlet.**
-dontwarn org.jaxen.**
-dontwarn com.vividsolutions.jts.**
-dontwarn com.ibm.oti.**
-dontwarn com.sun.cdc.**
-dontwarn org.codehaus.janino.**
-dontwarn javax.mail.**
-dontwarn org.apache.log4j.**
-dontwarn android.**
-dontwarn org.conscrypt.**
-dontwarn org.apache.lucene.**
-dontwarn org.osgi.**

# wildcard, use if above doesn`t suffice
#-ignorewarnings

-keepdirectories **
-keep class org.briarproject.**
-keepattributes InnerClasses
-keepclasseswithmembers public class org.briarproject.briar.desktop.MainKt {
    public static void main(java.lang.String[]);
}
-keep class org.whispersystems.curve25519.**
-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }
-keep class com.ibm.icu.text.MessageFormat
-keep class com.ibm.icu.text.MessagePattern$ApostropheMode {*;}

-keep class org.h2.** { *; }
-keep class org.slf4j.** { *; }
-keep class ch.qos.logback.** { *; }

-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }
