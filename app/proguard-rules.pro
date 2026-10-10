# POI also contains desktop Office drawing/conversion APIs. This app only uses HWPF text.
-dontwarn java.awt.**
-dontwarn javax.imageio.**
-dontwarn javax.xml.stream.**
-dontwarn javax.management.**
-dontwarn java.lang.management.**
-dontwarn org.osgi.**
-dontwarn org.apache.logging.log4j.core.**
-dontwarn org.apache.logging.log4j.util.internal.**
-dontwarn org.bouncycastle.**
# Log4j's fallback is selected by class name when no logging provider is installed.
-keep class org.apache.logging.log4j.simple.SimpleLoggerContextFactory { public <init>(); }
# Optional build/nullability annotations in Log4j's API, not runtime implementation.
-dontwarn aQute.bnd.annotation.baseline.BaselineIgnore
-dontwarn aQute.bnd.annotation.spi.ServiceConsumer
-dontwarn aQute.bnd.annotation.spi.ServiceProvider
-dontwarn edu.umd.cs.findbugs.annotations.Nullable
-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings
-dontwarn org.jspecify.annotations.NullMarked
