import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.lectorsimple"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lectorsimple"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions { jvmTarget = "17" }
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources.merges += setOf("META-INF/LICENSE*", "META-INF/NOTICE*", "META-INF/DEPENDENCIES")
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("org.apache.poi:poi-scratchpad:5.5.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    testImplementation("junit:junit:4.13.2")
    testImplementation("net.sf.kxml:kxml2:2.3.0")
}

// POI's desktop mapped-buffer cleaner invokes MethodHandle.invokeExact (API 26+).
// Our DOC reader opens POIFS read-only, which uses heap buffers, never mapped ones.
// Disable ONLY this desktop cleaner before D8, in debug and release, keeping API 23.
abstract class AndroidPoiCleaner : AsmClassVisitorFactory<InstrumentationParameters.None> {
    override fun isInstrumentable(classData: ClassData) =
        classData.className == "org.apache.poi.poifs.nio.CleanerUtil"

    override fun createClassVisitor(classContext: ClassContext, nextClassVisitor: ClassVisitor): ClassVisitor =
        object : ClassVisitor(Opcodes.ASM9, nextClassVisitor) {
            override fun visitField(access: Int, name: String, descriptor: String, signature: String?, value: Any?): FieldVisitor? =
                when (name) {
                    "UNMAP_SUPPORTED" -> super.visitField(access, name, descriptor, signature, 0)
                    "UNMAP_NOT_SUPPORTED_REASON" -> super.visitField(access, name, descriptor, signature,
                        "Mapped-buffer cleanup is not used by the read-only Android reader.")
                    else -> null
                }

            override fun visitMethod(access: Int, name: String, descriptor: String, signature: String?, exceptions: Array<out String>?): MethodVisitor? =
                if (name == "<init>") super.visitMethod(access, name, descriptor, signature, exceptions) else null

            override fun visitEnd() {
                super.visitMethod(Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC, "getCleaner",
                    "()Lorg/apache/poi/poifs/nio/CleanerUtil\$BufferCleaner;", null, null).apply {
                    visitCode()
                    visitInsn(Opcodes.ACONST_NULL)
                    visitInsn(Opcodes.ARETURN)
                    visitMaxs(1, 0)
                    visitEnd()
                }
                super.visitEnd()
            }
        }
}

androidComponents {
    onVariants(selector().all()) { variant ->
        variant.instrumentation.transformClassesWith(AndroidPoiCleaner::class.java, InstrumentationScope.ALL) {}
    }
}
