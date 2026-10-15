# Contexto del proyecto FreeDocs

## 1. Árbol de `app/src/main`

```text
app/src/main/
├── assets/
│   └── unpack/
│       ├── etc/
│       │   └── fonts/
│       │       └── fonts.conf
│       ├── program/
│       │   └── sofficerc
│       └── user/
│           └── fonts/
├── java/
│   ├── com/
│   │   └── example/
│   │       └── lectorsimple/
│   │           ├── DocumentReader.kt
│   │           ├── DocxFontCompatibility.kt
│   │           ├── LibreOfficeEngine.kt
│   │           ├── LibreOfficeResources.kt
│   │           ├── MainActivity.kt
│   │           ├── PdfEditModel.kt
│   │           ├── PdfSession.kt
│   │           ├── PdfZoomView.kt
│   │           ├── ReaderSafety.kt
│   │           ├── ReaderWorker.kt
│   │           └── WordZoomView.kt
│   └── org/
│       └── libreoffice/
│           └── kit/
│               ├── DirectBufferAllocator.java
│               ├── Document.java
│               ├── LibreOfficeKit.java
│               └── Office.java
├── jniLibs/
│   ├── arm64-v8a/
│   │   ├── libc++_shared.so
│   │   ├── libfreebl3.so
│   │   ├── liblo-native-code.so
│   │   ├── libnspr4.so
│   │   ├── libnss3.so
│   │   ├── libnssckbi.so
│   │   ├── libnssdbm3.so
│   │   ├── libnssutil3.so
│   │   ├── libplc4.so
│   │   ├── libplds4.so
│   │   ├── libsmime3.so
│   │   ├── libsoftokn3.so
│   │   ├── libsqlite3.so
│   │   └── libssl3.so
│   └── x86_64/
│       ├── libc++_shared.so
│       ├── libfreebl3.so
│       ├── liblo-native-code.so
│       ├── libnspr4.so
│       ├── libnss3.so
│       ├── libnssckbi.so
│       ├── libnssdbm3.so
│       ├── libnssutil3.so
│       ├── libplc4.so
│       ├── libplds4.so
│       ├── libsmime3.so
│       ├── libsoftokn3.so
│       ├── libsqlite3.so
│       └── libssl3.so
├── res/
│   ├── drawable/
│   │   └── launch_background.xml
│   ├── drawable-hdpi/
│   │   └── freedocs_splash.png
│   ├── drawable-mdpi/
│   │   └── freedocs_splash.png
│   ├── drawable-xhdpi/
│   │   └── freedocs_splash.png
│   ├── drawable-xxhdpi/
│   │   └── freedocs_splash.png
│   ├── drawable-xxxhdpi/
│   │   ├── freedocs_logo.png
│   │   ├── freedocs_splash.png
│   │   └── ic_launcher_foreground.png
│   ├── mipmap-anydpi-v26/
│   │   └── ic_launcher.xml
│   ├── mipmap-hdpi/
│   │   └── ic_launcher.png
│   ├── mipmap-mdpi/
│   │   └── ic_launcher.png
│   ├── mipmap-xhdpi/
│   │   └── ic_launcher.png
│   ├── mipmap-xxhdpi/
│   │   └── ic_launcher.png
│   ├── mipmap-xxxhdpi/
│   │   └── ic_launcher.png
│   ├── values/
│   │   ├── colors.xml
│   │   ├── strings.xml
│   │   └── styles.xml
│   ├── values-v31/
│   │   └── styles.xml
│   └── values-v33/
│       └── styles.xml
└── AndroidManifest.xml
```

## 2. Documentación y configuración

### `README.md`

````markdown
# FreeDocs para Android

<p align="center">
  <img src="artwork/freedocs-logo-transparent.png" alt="FreeDocs Logo" width="160" />
</p>

Lector Android ligero para abrir archivos PDF y documentos de Word desde el selector del dispositivo. La app trabaja localmente y permite ampliar el contenido con gestos de zoom. Los PDF también se pueden anotar y exportar desde el móvil.
> **Estado:** PDF tiene vista por páginas con zoom y edición visual. DOC y DOCX disponen de vista **Hoja (Diseño)** procesada localmente por LibreOfficeKit y de la vista conmutable **Móvil (Texto)**. Consulta [WORD_POR_HOJAS.md](WORD_POR_HOJAS.md) y [LEGAL_NOTICE.md](LEGAL_NOTICE.md).

## Funciones

- Abrir PDF, DOCX y DOC mediante el selector de archivos de Android.
- Leer PDF por páginas, navegar entre ellas y ampliar hasta 6× con pellizco o doble toque.
- Editar visualmente un PDF con cuadros de texto, reemplazo superpuesto de renglones, movimiento, firma/dibujo y resaltado; deshacer o borrar ediciones y guardar en el documento o como una copia.
- Ajustar tamaño y color del texto nuevo o reemplazado, y elegir si se cubre el texto de origen.
- Vista Word **Hoja**: lectura fiel por páginas conservando el formato original, tamaño de papel, márgenes, tablas e imágenes mediante conversión local y visor por bloques con zoom.
- Vista Word **Móvil**: lectura de texto con ajuste automático al ancho de la pantalla y zoom con pellizco.
- Selector de modo conmutable **"Hoja | Móvil"** para Word, conservando la posición de lectura y nivel de zoom en cada vista.
- Procesar documentos en almacenamiento privado temporal. Al guardar un PDF, se intenta escribir en el URI original; si el proveedor no permite escribir, se ofrece guardar una copia.
- Para conservar la apariencia al exportar, las páginas originales del PDF se rasterizan y las ediciones se dibujan encima. El texto de origen de la copia exportada deja de ser seleccionable o buscable.
- Funcionamiento 100% offline: no requiere conexión a Internet ni permisos de red ni acceso general al almacenamiento.

## Requisitos

- Android 6.0 o posterior (API 23+).
- Android Studio con Android SDK 35 y JDK 17 para compilar.

## Abrir y compilar

1. Clona este repositorio y abre su carpeta en Android Studio.
2. Espera a que Gradle sincronice las dependencias.
3. Conecta un teléfono con depuración USB o inicia un emulador.
4. Ejecuta la configuración **app** desde Android Studio.

Para generar el APK de depuración desde PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`. Para una compilación de producción:

```powershell
.\gradlew.bat assembleRelease
```

El APK release requiere firma antes de distribuirse. No publiques la clave de firma ni sus contraseñas en el repositorio.

## Detalles técnicos

- Kotlin, Android Gradle Plugin 8.7.3 y Gradle Wrapper 8.14.3.
- PDF: `PdfRenderer` de Android con renderizado por bloques de 512 px, caché limitada y zoom mediante gestos. La edición y exportación usan `PdfDocument` del sistema, sin dependencias Gradle adicionales.
- DOCX: parser XML de Android con límites de tamaño y descompresión.
- DOC: Apache POI Scratchpad 5.5.1.
- La lectura y el renderizado se ejecutan fuera del hilo de interfaz.
- Los límites actuales son 100 MiB para PDF, 12 MiB para Word, 8 MiB de XML descomprimido y un millón de caracteres.
- Se adapta la clase `CleanerUtil` de POI para mantener compatibilidad con API 23. La razón y el alcance están documentados en `app/build.gradle.kts`.

## Licencias

La app incluye Apache POI y sus dependencias, que llevan sus propios avisos de licencia en los artefactos Android. Consulta las licencias y avisos empaquetados en el APK antes de redistribuirlo, y conserva los avisos requeridos. La licencia de la imagen de marca depende de los derechos del archivo original proporcionado para el proyecto.

## Documentación

- [Plan para mostrar Word por hojas](WORD_POR_HOJAS.md)
- [Estado y comprobaciones de validación](VALIDACION.md)
- [Preparación de los iconos](artwork/README.md)


````

### `WORD_POR_HOJAS.md`

````markdown
# Word por hojas

FreeDocs abre archivos `.doc` y `.docx` localmente con LibreOfficeKit y convierte una copia temporal a PDF para mostrar el diseño de página. La vista por hojas conserva el tamaño de página, los márgenes y la paginación que produce el motor; la vista **Móvil (Texto)** presenta el texto reajustado al ancho.

## Compatibilidad tipográfica y guiones

Antes de convertir un DOCX, `DocxFontCompatibility` prepara una copia privada y:

- Mapea las fuentes comunes de Office en `<w:rFonts>` y en la tabla de tema a las familias métricas empaquetadas: Carlito, Caladea, Liberation Sans, Liberation Mono y Gentium Book Basic. Los nombres alternativos de `word/fontTable.xml` también se usan para resolver referencias en los estilos.
- Inserta un espacio de ancho cero entre guiones consecutivos en nodos `<w:t>` de los XML de Word. La transformación conserva exactamente el número de guiones y crea puntos de corte dentro de las tiras largas.
- Conserva la configuración de cuadrícula y compatibilidad de paginación original del DOCX.

La configuración de Fontconfig se extrae en almacenamiento privado antes de inicializar LibreOfficeKit. Incluye las fuentes métricas del paquete y no activa `SAL_LOK_OPTIONS=compact_fonts`.

La conversión solo modifica la copia temporal, nunca el archivo Word seleccionado. Los resultados dependen del contenido y de la versión de LibreOfficeKit; la compilación por sí sola no demuestra paridad visual con Word de escritorio. Para comprobar un documento, hay que comparar su paginación, fuentes, saltos de línea y tablas en un dispositivo.

## Flujo

1. FreeDocs copia el archivo seleccionado al almacenamiento privado.
2. Para DOCX, prepara una copia compatible con las fuentes locales y los cortes de guiones.
3. LibreOfficeKit convierte el documento a un PDF temporal.
4. `PdfSession` y `PdfZoomView` muestran las páginas con zoom y renderizado por bloques.
5. Al cerrar el documento, se eliminan los temporales de lectura.

Los DOC binarios usan la ruta de conversión local de LibreOfficeKit sin reescritura OOXML.

````

### `VALIDACION.md`

````markdown
# Validación de la implementación

Fecha: 10 de octubre de 2026.

## Visor por bloques y fondo negro

Se sustituyó el renderizado de una imagen del área visible por una vista previa completa y bloques reutilizables de 512 píxeles. La caché de detalle tiene un límite de 16 MiB; la vista previa ocupa aproximadamente 1 MiB. La entrega a la interfaz se limita a dos imágenes pendientes. El fondo fuera de la hoja es negro; se retiró el relleno lateral y inferior que dejaba una franja exterior clara.

El zoom reutiliza las imágenes existentes durante el pellizco. El detalle se solicita al terminar, y el desplazamiento solo solicita los bloques ausentes. Los resultados de movimientos anteriores pueden reutilizarse dentro de la misma página; los de otra página, documento o anchura se descartan.

`compileDebugKotlin` y `assembleDebug` terminaron con `BUILD SUCCESSFUL`. El APK debug se generó en `app/build/outputs/apk/debug/app-debug.apk`. No se añadieron ni ejecutaron pruebas para este cambio; no se comprobó el gesto en un dispositivo ni se midieron FPS o consumo real de memoria. La compilación no demuestra que el parpadeo esté resuelto en todos los dispositivos.

Word por hojas sigue pendiente. Se investigó LibreOfficeKit como opción gratuita y local, pero falta su biblioteca nativa y sus recursos. La descarga directa desde GitHub falló y la consulta de WSL devolvió acceso denegado. El plan de integración y el bloqueo están en `WORD_POR_HOJAS.md`.

## Comprobaciones ejecutadas

Se compiló con Kotlin 2.0.21 y JDK 17 la utilidad real `ReaderSafety.kt`, junto con un comprobador local en `.build-tools/ReaderSafetyCheck.kt`. Se ejecutaron y pasaron ocho comprobaciones:

1. Firmas PDF, DOCX y DOC, independientes del nombre del archivo.
2. Rechazo de firmas vacías, truncadas y ajenas a los formatos admitidos.
3. Límite de bytes descomprimidos con lecturas por bloques.
4. El mismo límite al mezclar lecturas individuales y por bloques con desplazamiento.
5. Fin de archivo justo en el límite, sin rechazo ni truncamiento incorrectos.
6. Limpieza de controles Word conservando acentos y Unicode.
7. División de texto largo conservando todos los caracteres, incluidos pares UTF-16 de emoji.
8. División de un millón de caracteres en 334 bloques de hasta 3.000 caracteres.

También se generó localmente el Gradle Wrapper. Su distribución configurada es Gradle 8.14.3.

En la primera sesión se intentó además compilar directamente las fuentes Kotlin contra el SDK Android 35. El compilador se detuvo en las referencias a Apache POI, entonces ausente de la caché; esa comprobación no produjo una compilación completa ni un APK.

## Bloqueos de esta sesión

En la primera sesión, el comando `gradlew.bat assembleDebug assembleRelease testDebugUnitTest lintDebug` no llegó a compilar el proyecto: la descarga de Gradle falló con `java.net.SocketException: Permission denied: getsockopt`. Android Studio descargó posteriormente Gradle y las dependencias de la app.

No hay un dispositivo conectado a ADB ni un AVD configurado. No se han validado gestos, renderizado PDF, lectura de Word real, insets, recreación de la Activity, memoria en Android ni el tamaño final de release.

Las comprobaciones locales de seguridad no sustituyen la suite JUnit ni las pruebas en dispositivo.

## Corrección del error MethodHandle de POI

Android Studio detectó `MethodHandle.invokeExact` en `org.apache.poi.poifs.nio.CleanerUtil`. El método requiere API 26, mientras el proyecto permite API 23; el desugaring no puede convertir esta llamada.

El build adapta únicamente esa clase antes de D8, para ambas variantes. Su bandera `UNMAP_SUPPORTED` pasa a ser `false` y su getter devuelve `null`; se eliminan los métodos de limpieza de escritorio que contienen `MethodHandle`. El lector usa `POIFSFileSystem(file, true)`, que lee con buffers de heap y no utiliza el mapeo de archivos. No se cambió `minSdk` ni se modificó la biblioteca original en la caché.

Verificación ejecutada:

- El build pasó por `transformDebugClassesWithAsm`, D8 y empaquetado; generó `app/build/outputs/apk/debug/app-debug.apk`, aproximadamente 6,05 MiB.
- `aapt2 dump badging` confirmó `minSdkVersion: 23`, `targetSdkVersion: 35` y ausencia de permisos solicitados.
- `javap` sobre el JAR transformado confirmó la bandera constante `false`, el getter sin llamadas externas y la ausencia de los métodos con `MethodHandle` en `CleanerUtil`.
- `minifyReleaseWithR8` terminó con `BUILD SUCCESSFUL`. Se añadieron reglas puntuales para seis anotaciones opcionales de Log4j que no se necesitan en ejecución. La compilación completa de release sigue pendiente de lint; no se generó un APK release en esta sesión.

La suite JUnit se detuvo porque `junit:junit:4.13.2` no está disponible en la caché. Lint también requiere descargas, incluida `com.android.tools.lint:lint-gradle:31.7.3`. La terminal sigue sin acceso de red. La compilación debug resuelve el error reportado, pero el funcionamiento en Android aún debe probarse en dispositivo.

## Verificación pendiente

En Android Studio con acceso a los repositorios, sincronizar y ejecutar:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug assembleRelease
```

Después, comprobar PDF y Word reales en Android: documentos pequeños y grandes, variantes `.doc` de Word 97–2007 y Word 6/95, archivos dañados/protegidos, zoom, desplazamiento, cambio de página/documento, rotación y permisos de distintos proveedores. Comprobar memoria en un dispositivo modesto y la pantalla en Android 15 o posterior.

## Edición PDF y ajuste DOCX (15 de octubre de 2026)

- DOCX: `DocxFontCompatibility.prepare()` recorre los XML de texto bajo `word/`, conserva la cantidad original de guiones y añade `U+200B` entre guiones consecutivos. Mantiene el mapeo de fuentes Office a las fuentes métricas del paquete. No modifica `w:docGrid`, `w:compatSetting` ni `w:usePrinterMetrics`.
- PDF: la vista ofrece modo de edición con texto, reemplazo visual de renglón, mover cuadros de texto, firma/dibujo, resaltado, borrar, deshacer y guardado. La exportación usa `PdfDocument` de Android y no agrega dependencias Gradle.
- La exportación PDF rasteriza cada página original y dibuja encima las ediciones. El resultado preserva la apariencia visual, pero el texto original de la copia exportada no queda seleccionable ni buscable. El guardado directo depende de que el proveedor del URI permita escribir; si no, se puede crear una copia.
- Renderizado PDF: la preview permite escala hasta 1.5 para el límite de píxeles configurado; los bounds de tiles compensan el redondeo del bitmap. La página se escala usando todo el ancho de `PdfZoomView`.
- Verificación: `assembleDebug --offline` terminó con `BUILD SUCCESSFUL`; `git diff --check` no reportó errores de whitespace. No se hizo una comparación visual de un DOCX contractual en dispositivo ni pruebas de uso del editor en Android. Por tanto, la compilación no confirma paginación 1:1 con Word ni el resultado de guardado con proveedores reales.

---

## Corrección de preparación de LibreOffice (11 de octubre de 2026)

- El cierre observado era un SIGABRT con `uno::DeploymentException` durante `initializeNative`, antes de cargar el documento.
- La inspección mediante `adb shell run-as com.example.lectorsimple` confirmó que faltaban `program/`, `etc/fonts/` y `user/fonts/` en los datos privados. `unorc` referencia las bases UNO en ese directorio.
- Se añadió `LibreOfficeResources`: extrae `assets/unpack/` antes de JNI, comprueba recursos obligatorios, usa escritura atómica por archivo y registra tamaños e identidad de la instalación solo al completar la extracción. Reintenta si fue interrumpida o faltan archivos.
- La inicialización Java ahora comunica un retorno nativo fallido y el motor comprueba su controlador antes de marcarse listo. Se preservan las clases JNI en R8.
- Al reproducir con los recursos preparados, ART identificó el siguiente fallo: `GetStringUTFChars received NULL jstring` desde `Document.saveAs(String, String, String)`. El wrapper JNI no acepta `null` para opciones; se cambió por cadena vacía en ambas llamadas de exportación.
- Compilación local posterior `:app:assembleDebug --offline`: **BUILD SUCCESSFUL**. `adb install -r` fue rechazado porque el APK y la instalación existente tienen certificados distintos; no se desinstaló la app ni se borraron sus datos. Falta instalar el APK actualizado y abrir DOC/DOCX para validar en ejecución.

## Validación Sesión: Integración LibreOfficeKit (11 de octubre de 2026)

Rama: `feature/word-sheet-libreoffice`

### 1. Entorno de compilación
- JDK: OpenJDK 17 (Temurin 17.0.20.1) configurado en `/home/laurent/.jdks/jdk-17`.
- Android SDK: Command-line tools 12.0 instalados en `/home/laurent/Android/Sdk`.
- Plataformas: Android SDK 34 y 35; Build-Tools 35.0.0 y 34.0.0.
- Android NDK: 27.2.12479018.

### 2. Motor nativo y recursos integrados
- Revisión oficial fijada: `libreoffice-26.2.6.3` (commit `8221e31b3ac356a1623c672912a3d2b492f7e3d1`).
- Clases JNI/Java oficiales añadidas en `org.libreoffice.kit` (`LibreOfficeKit`, `Office`, `Document`, `DirectBufferAllocator`).
- Bibliotecas nativas empaquetadas para `arm64-v8a` y `x86_64` (`liblo-native-code.so`, `libc++_shared.so`, NSS/NSPR).
- Recursos de configuración UNO y tipografías métricas (Liberation, Carlito, Caladea) empaquetados en `assets/`.

### 3. Resultados de compilación
- `gradlew assembleDebug` ejecutado con éxito (`BUILD SUCCESSFUL`).
- APK generado en `app/build/outputs/apk/debug/app-debug.apk` (175 MiB con ambas arquitecturas `arm64-v8a` y `x86_64`).
- `aapt2 dump badging` verificado:
  - `minSdkVersion: 23`
  - `targetSdkVersion: 35`
  - `compileSdkVersion: 35`
  - `native-code: 'arm64-v8a' 'x86_64'`
  - 0 permisos solicitados (sin INTERNET, sin WRITE_EXTERNAL_STORAGE).


````

### `settings.gradle.kts`

````kotlin
pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "LectorSimple"
include(":app")

````

### `build.gradle.kts`

````kotlin
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
}

````

### `app/build.gradle.kts`

````kotlin
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

````

### `app/proguard-rules.pro`

````proguard
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

# LibreOffice JNI looks up wrapper fields and callbacks by their original names.
-keep class org.libreoffice.kit.** { *; }

````

### `app/src/main/AndroidManifest.xml`

````xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application
        android:allowBackup="false"
        android:label="@string/app_name"
        android:icon="@mipmap/ic_launcher"
        android:roundIcon="@mipmap/ic_launcher"
        android:theme="@style/Theme.LectorSimple.Launch"
        android:supportsRtl="true">
        <activity
            android:name=".MainActivity"
            android:launchMode="singleTop"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:mimeType="application/pdf" />
                <data android:mimeType="application/msword" />
                <data android:mimeType="application/vnd.openxmlformats-officedocument.wordprocessingml.document" />
            </intent-filter>
        </activity>
    </application>
</manifest>

````

### `app/src/main/assets/unpack/etc/fonts/fonts.conf`

````xml
<?xml version="1.0"?>
<!DOCTYPE fontconfig SYSTEM "fonts.dtd">
<!-- /etc/fonts/fonts.conf file to configure system font access -->
<fontconfig>

<!-- Font directory list -->

	<!--FREEDOCS_USER_FONTS_DIR-->
	<dir>/system/fonts</dir>

	<alias>
		<family>serif</family>
		<prefer>
			<family>Caladea</family>
			<family>Gentium Book Basic</family>
			<family>Liberation Serif</family>
		</prefer>
	</alias>
	<alias>
		<family>sans-serif</family>
		<prefer>
			<family>Liberation Sans</family>
			<family>Carlito</family>
			<family>Roboto</family>
		</prefer>
	</alias>
	<alias>
		<family>monospace</family>
		<prefer>
			<family>Liberation Mono</family>
		</prefer>
	</alias>

	<!-- Office-compatible metrically matched fonts bundled with FreeDocs. -->
	<match target="pattern"><test qual="any" name="family"><string>Calibri</string><string>Calibri Light</string><string>Aptos</string><string>Aptos Narrow</string><string>Aptos Display</string></test><edit name="family" mode="assign" binding="same"><string>Carlito</string></edit></match>
	<match target="pattern"><test qual="any" name="family"><string>Cambria</string><string>Cambria Math</string><string>Georgia</string></test><edit name="family" mode="assign" binding="same"><string>Caladea</string></edit></match>
	<match target="pattern"><test qual="any" name="family"><string>Arial</string><string>Helvetica</string><string>Segoe UI</string><string>Tahoma</string><string>Verdana</string></test><edit name="family" mode="assign" binding="same"><string>Liberation Sans</string></edit></match>
	<match target="pattern"><test qual="any" name="family"><string>Times New Roman</string><string>Times</string></test><edit name="family" mode="assign" binding="same"><string>Gentium Book Basic</string></edit></match>
	<match target="pattern"><test qual="any" name="family"><string>Courier New</string><string>Courier</string><string>Consolas</string></test><edit name="family" mode="assign" binding="same"><string>Liberation Mono</string></edit></match>

<!--
  Accept deprecated 'mono' alias, replacing it with 'monospace'
-->
	<match target="pattern">
		<test qual="any" name="family">
			<string>mono</string>
		</test>
		<edit name="family" mode="assign">
			<string>monospace</string>
		</edit>
	</match>

<!--
  Accept alternate 'sans serif' spelling, replacing it with 'sans-serif'
-->
	<match target="pattern">
		<test qual="any" name="family">
			<string>sans serif</string>
		</test>
		<edit name="family" mode="assign">
			<string>sans-serif</string>
		</edit>
	</match>

<!--
  Accept deprecated 'sans' alias, replacing it with 'sans-serif'
-->
	<match target="pattern">
		<test qual="any" name="family">
			<string>sans</string>
		</test>
		<edit name="family" mode="assign">
			<string>sans-serif</string>
		</edit>
	</match>

<!--
  Load local system customization file
-->
	<include ignore_missing="yes">conf.d</include>

<!-- Font cache directory list -->

	<!-- XDG_CACHE_HOME is set to this app's private cache before native initialization. -->
	<cachedir prefix="xdg">fontconfig</cachedir>

	<config>
<!--
  Rescan configuration every 3600 seconds when FcFontSetList is called
 -->
		<rescan>
			<int>3600</int>
		</rescan>
	</config>

</fontconfig>

````

### `app/src/main/assets/program/bootstraprc`

````text
            [Bootstrap]
            InstallMode=<installmode>
            ProductKey=LibreOffice 26.2
UserInstallation=file://$APP_DATA_DIR

````

### `app/src/main/assets/program/fundamentalrc`

````text
[Bootstrap]
LO_LIB_DIR=file://$APP_DATA_DIR/lib/
BRAND_BASE_DIR=file:///assets
BRAND_SHARE_SUBDIR=share
CONFIGURATION_LAYERS=xcsxcu:${BRAND_BASE_DIR}/share/registry res:${BRAND_BASE_DIR}/share/registry
URE_BIN_DIR=file:///assets/ure/bin/dir/nothing-here/we-can/exec-anyway

````

### `app/src/main/assets/program/unorc`

````text
[Bootstrap]
URE_INTERNAL_LIB_DIR=file://$APP_DATA_DIR/lib/
UNO_TYPES=file://$APP_DATA_DIR/program/udkapi.rdb file://$APP_DATA_DIR/program/offapi.rdb file://$APP_DATA_DIR/program/oovbaapi.rdb
UNO_SERVICES=file:///assets/program/services.rdb file:///assets/program/services/services.rdb

````

### `app/src/main/assets/program/versionrc`

````text
            [Version]
            AllLanguages=en-US
            buildid=8221e31b3ac356a1623c672912a3d2b492f7e3d1
ReferenceOOoMajorMinor=4.1

````

### `app/src/main/assets/unpack/program/sofficerc`

````text
[Bootstrap]
Logo=1
NativeProgress=1
URE_BOOTSTRAP=file:///assets/program/fundamentalrc
HOME=$APP_DATA_DIR/cache
OSL_SOCKET_PATH=$APP_DATA_DIR/cache

````

## 3. Código fuente

### `app/src/main/java/com/example/lectorsimple/DocumentReader.kt`

````kotlin
package com.example.lectorsimple

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Xml
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.HWPFOldDocument
import org.apache.poi.hwpf.OldWordFileFormatException
import org.apache.poi.hwpf.usermodel.Range
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.util.IOUtils
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.InputStream
import java.io.InterruptedIOException
import java.util.zip.ZipFile

data class LocalDocument(val file: File, val name: String, val kind: DocumentKind)

/** Runs on the reader's single worker. Files are private and never sent to a server. */
object DocumentReader {
    const val MAX_TEXT = 1_000_000
    const val MAX_XML_BYTES = 8L * 1024 * 1024
    const val MAX_WORD_BYTES = 12L * 1024 * 1024
    const val MAX_FILE_BYTES = 100L * 1024 * 1024
    private const val WORD_NS = "http://schemas.openxmlformats.org/wordprocessingml/2006/main"
    private const val STRICT_WORD_NS = "http://purl.oclc.org/ooxml/wordprocessingml/main"
    private var cachePrepared = false

    @Synchronized
    private fun prepareCache(cacheDir: File) {
        if (cachePrepared) return
        // Only once per process: remove reader files left behind by a killed process.
        cacheDir.listFiles { file -> file.isFile && (
            (file.name.startsWith("reader-") && file.name.endsWith(".bin")) ||
            (file.name.startsWith("reader-fonts-") && file.name.endsWith(".docx")) ||
            (file.name.startsWith("reader-sheet-") && file.name.endsWith(".pdf")) ||
            (file.name.startsWith("reader-edited-") && file.name.endsWith(".pdf")) ||
            (file.name.startsWith("reader-export-") && file.name.endsWith(".pdf"))
        ) }
            ?.forEach { it.delete() }
        cachePrepared = true
    }

    fun stage(resolver: ContentResolver, uri: Uri, cacheDir: File): LocalDocument {
        prepareCache(cacheDir)
        val name = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
            if (it.moveToFirst()) it.getString(0) else null
        } ?: "Documento"
        val file = File.createTempFile("reader-", ".bin", cacheDir)
        try {
            val stream = resolver.openInputStream(uri) ?: throw ReadingException("No se pudo leer el archivo.")
            stream.use { source ->
                file.outputStream().use { target ->
                    val buffer = ByteArray(32 * 1024)
                    var total = 0L
                    while (true) {
                        checkCancelled()
                        val count = source.read(buffer)
                        if (count < 0) break
                        total += count
                        if (total > MAX_FILE_BYTES) throw ReadingException("El archivo supera el límite de 100 MB.")
                        target.write(buffer, 0, count)
                    }
                }
            }
            val header = ByteArray(1024)
            val count = file.inputStream().use { it.read(header) }.coerceAtLeast(0)
            val kind = identify(header.copyOf(count))
            if (kind != DocumentKind.PDF && file.length() > MAX_WORD_BYTES) {
                throw ReadingException("El archivo Word supera el límite de 12 MB.")
            }
            return LocalDocument(file, name, kind)
        } catch (failure: Throwable) {
            file.delete()
            throw failure
        }
    }

    fun identify(header: ByteArray): DocumentKind = DocumentSignature.identify(header)

    fun readWord(document: LocalDocument): String = when (document.kind) {
        DocumentKind.DOCX -> readDocx(document.file)
        DocumentKind.DOC -> readDoc(document.file)
        else -> throw ReadingException("El archivo no es un documento Word.")
    }.ifBlank { "Este documento no contiene texto legible." }

    private fun readDocx(file: File): String = ZipFile(file).use { zip ->
        val entry = zip.getEntry("word/document.xml")
            ?: throw ReadingException("El archivo no contiene un documento Word válido.")
        if (entry.size > MAX_XML_BYTES) throw ReadingException("El contenido Word es demasiado grande.")
        zip.getInputStream(entry).use { parseDocx(it) }
    }

    internal fun parseDocx(input: InputStream, parser: XmlPullParser = Xml.newPullParser()): String {
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
        parser.setInput(LimitedInputStream(input, MAX_XML_BYTES), null)
        val result = StringBuilder()
        var inText = false
        var inCell = false
        var seenDocument = false
        while (true) {
            checkCancelled()
            val event = parser.next()
            if (event == XmlPullParser.END_DOCUMENT) break
            val wordTag = parser.namespace == WORD_NS || parser.namespace == STRICT_WORD_NS
            when (event) {
                XmlPullParser.DOCDECL -> throw ReadingException("El documento contiene XML no compatible.")
                XmlPullParser.START_TAG -> {
                    if (!seenDocument) {
                        if (!wordTag || parser.name != "document") throw ReadingException("El archivo no contiene un documento Word válido.")
                        seenDocument = true
                    }
                    if (wordTag) when (parser.name) {
                    "t" -> inText = true
                    "tc" -> inCell = true
                    "tab" -> result.append('\t')
                    "br", "cr" -> result.append('\n')
                    }
                }
                XmlPullParser.TEXT -> if (inText) result.append(parser.text)
                XmlPullParser.END_TAG -> if (wordTag) when (parser.name) {
                    "t" -> inText = false
                    "p" -> result.append(if (inCell) "\n" else "\n\n")
                    "tc" -> { result.append('\t'); inCell = false }
                    "tr" -> result.append('\n')
                }
            }
            if (result.length > MAX_TEXT) throw ReadingException("El documento contiene demasiado texto.")
        }
        if (!seenDocument) throw ReadingException("El archivo no contiene un documento Word válido.")
        return result.toString().trim()
    }

    private fun readDoc(file: File): String {
        // Bound allocations declared by untrusted binary records as well as physical file size.
        IOUtils.setByteArrayMaxOverride(16 * 1024 * 1024)
        POIFSFileSystem(file, true).use { fs ->
            if (fs.root.hasEntry("EncryptedPackage")) throw ReadingException("El documento está protegido con contraseña.")
            if (!fs.root.hasEntry("WordDocument")) throw ReadingException("El archivo no es un documento Word válido.")
            fs.createDocumentInputStream("WordDocument").use { stream ->
                val header = ByteArray(12)
                stream.readFully(header)
                if ((header[11].toInt() and 1) != 0) throw ReadingException("El documento está protegido con contraseña.")
            }
            val text = try {
                HWPFDocument(fs.root).use { document -> checkedText(document.range.text()) }
            } catch (_: OldWordFileFormatException) {
                HWPFOldDocument(fs.root).use { document -> checkedText(document.range.text()) }
            }
            return cleanWordText(Range.stripFields(text))
        }
    }

    private fun checkedText(text: String): String {
        checkCancelled()
        if (text.length > MAX_TEXT) throw ReadingException("El documento contiene demasiado texto.")
        return text
    }

    internal fun cleanWordText(text: String): String = WordText.clean(text)

    fun checkCancelled() {
        if (Thread.currentThread().isInterrupted) throw InterruptedIOException()
    }
}

````

### `app/src/main/java/com/example/lectorsimple/DocxFontCompatibility.kt`

````kotlin
package com.example.lectorsimple

import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InterruptedIOException
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/** Normalizes Word font metrics and long hyphen runs in a private DOCX copy. */
internal object DocxFontCompatibility {
    private const val WORD_NS = "http://schemas.openxmlformats.org/wordprocessingml/2006/main"
    private const val STRICT_NS = "http://purl.oclc.org/ooxml/wordprocessingml/main"
    private const val MAX_XML_BYTES = 8L * 1024 * 1024
    private const val MAX_PACKAGE_BYTES = 100L * 1024 * 1024
    private val MULTI_HYPHEN_REGEX = Regex("-{2,}")
    private val officeFonts = mapOf(
        "calibri" to "Carlito", "calibri light" to "Carlito", "aptos" to "Carlito", "aptos narrow" to "Carlito", "aptos display" to "Carlito",
        "cambria" to "Caladea", "cambria math" to "Caladea", "georgia" to "Caladea",
        "arial" to "Liberation Sans", "helvetica" to "Liberation Sans", "segoe ui" to "Liberation Sans", "tahoma" to "Liberation Sans", "verdana" to "Liberation Sans",
        "times new roman" to "Gentium Book Basic", "times" to "Gentium Book Basic",
        "courier new" to "Liberation Mono", "courier" to "Liberation Mono", "consolas" to "Liberation Mono"
    )

    /** Returns null when no compatibility copy is required (including binary DOC). */
    fun prepare(source: File, cacheDir: File): File? {
        val signature = source.inputStream().use { it.readNBytesCompat(4) }
        if (!signature.contentEquals(byteArrayOf(0x50, 0x4b, 0x03, 0x04))) return null
        ZipFile(source).use { zip ->
            val aliases = mutableMapOf<String, String>()
            zip.getEntry("word/fontTable.xml")?.let { table ->
                val fontTable = parseXml(zip.getInputStream(table).use { readXml(it) })
                val fonts = fontTable.getElementsByTagNameNS("*", "font")
                for (index in 0 until fonts.length) {
                    val font = fonts.item(index) as Element
                    val namespace = font.namespaceURI
                    if (!isWordNamespace(namespace)) continue
                    val name = font.getAttributeNS(namespace, "name").trim()
                    val mapped = officeFonts[name.lowercase()]
                    val alternative = font.getElementsByTagNameNS(namespace, "altName").item(0) as? Element
                    val altName = alternative?.getAttributeNS(namespace, "val")?.trim().orEmpty()
                    val mappedAlt = officeFonts[altName.lowercase()]
                    val target = mapped ?: mappedAlt
                    if (target != null) {
                        if (name.isNotEmpty()) aliases[name.lowercase()] = target
                        if (altName.isNotEmpty()) aliases[altName.lowercase()] = target
                    }
                }
            }

            val temporary = File.createTempFile("reader-fonts-", ".docx", cacheDir)
            try {
                var changed = false
                var remaining = MAX_PACKAGE_BYTES
                ZipOutputStream(temporary.outputStream().buffered()).use { output ->
                    val entries = zip.entries()
                    while (entries.hasMoreElements()) {
                        checkCancelled()
                        val entry = entries.nextElement()
                        output.putNextEntry(ZipEntry(entry.name))
                        if (!entry.isDirectory) zip.getInputStream(entry).use { input ->
                            if (entry.name.startsWith("word/") && entry.name.endsWith(".xml")) {
                                val bytes = readXml(input)
                                remaining -= bytes.size
                                if (remaining < 0) throw IOException("El documento expandido es demasiado grande.")
                                val xml = parseXml(bytes)
                                var modified = false
                                val families = xml.getElementsByTagNameNS("*", "rFonts")
                                for (index in 0 until families.length) {
                                    val run = families.item(index) as Element
                                    val namespace = run.namespaceURI
                                    if (!isWordNamespace(namespace)) continue
                                    for (attribute in arrayOf("ascii", "hAnsi", "eastAsia", "cs")) {
                                        val node = run.getAttributeNodeNS(namespace, attribute) ?: continue
                                        val replacement = officeFonts[node.value.trim().lowercase()] ?: aliases[node.value.trim().lowercase()] ?: continue
                                        node.value = replacement
                                        modified = true
                                    }
                                }
                                if (entry.name.startsWith("word/theme/") && entry.name.endsWith(".xml")) {
                                    val nodes = xml.getElementsByTagNameNS("*", "latin")
                                    for (index in 0 until nodes.length) {
                                        val node = nodes.item(index) as? Element ?: continue
                                        val attr = node.getAttributeNode("typeface") ?: continue
                                        val replacement = officeFonts[attr.value.trim().lowercase()] ?: aliases[attr.value.trim().lowercase()] ?: continue
                                        attr.value = replacement; modified = true
                                    }
                                }
                                // Hyphen runs can be present in document, header, footer, footnote and endnote parts.
                                val textNodes = xml.getElementsByTagNameNS("*", "t")
                                for (index in 0 until textNodes.length) {
                                    val textNode = textNodes.item(index) as? Element ?: continue
                                    if (!isWordNamespace(textNode.namespaceURI)) continue
                                    val text = textNode.textContent ?: continue
                                    if (!text.contains("--")) continue
                                    val normalized = normalizeHyphenRuns(text)
                                    if (normalized != text) { textNode.textContent = normalized; modified = true }
                                }
                                if (modified) {
                                    val transformer = TransformerFactory.newInstance().newTransformer()
                                    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
                                    transformer.transform(DOMSource(xml), StreamResult(output))
                                    changed = true
                                } else output.write(bytes)
                            } else {
                                remaining -= copyLimited(input, output, remaining)
                            }
                        }
                        output.closeEntry()
                    }
                }
                if (changed) return temporary
                temporary.delete()
                return null
            } catch (failure: Throwable) {
                temporary.delete()
                throw failure
            }
        }
    }

    /** Insert break opportunities between hyphens without changing the source hyphen count. */
    internal fun normalizeHyphenRuns(text: String): String {
        return MULTI_HYPHEN_REGEX.replace(text) { match ->
            val count = match.value.length
            buildString(count * 2 - 1) {
                for (index in 0 until count) {
                    if (index > 0) append('\u200B')
                    append('-')
                }
            }
        }
    }

    private fun isWordNamespace(namespace: String?) = namespace == WORD_NS || namespace == STRICT_NS

    private fun parseXml(bytes: ByteArray): org.w3c.dom.Document {
        checkCancelled()
        // DOCX parts must not contain a DTD. Removing NULs also detects UTF-16/32 declarations.
        if (bytes.toString(Charsets.UTF_8).replace("\u0000", "").contains("<!DOCTYPE", ignoreCase = true)) {
            throw IOException("El documento contiene una declaración XML no admitida.")
        }
        val builder = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }.newDocumentBuilder()
        builder.setEntityResolver { _, _ -> throw IOException("No se admiten entidades XML externas.") }
        return builder.parse(ByteArrayInputStream(bytes))
    }

    private fun readXml(input: InputStream): ByteArray = ByteArrayOutputStream().use { output ->
        copyLimited(input, output, MAX_XML_BYTES)
        output.toByteArray()
    }

    private fun copyLimited(input: InputStream, output: OutputStream, limit: Long): Long {
        val buffer = ByteArray(32 * 1024)
        var total = 0L
        while (true) {
            checkCancelled()
            val count = input.read(buffer)
            if (count < 0) return total
            total += count
            if (total > limit) throw IOException("El documento expandido es demasiado grande.")
            output.write(buffer, 0, count)
        }
    }

    private fun InputStream.readNBytesCompat(count: Int): ByteArray {
        val bytes = ByteArray(count)
        var position = 0
        while (position < count) {
            val read = read(bytes, position, count - position)
            if (read < 0) break
            position += read
        }
        return bytes.copyOf(position)
    }

    private fun checkCancelled() {
        if (Thread.currentThread().isInterrupted) throw InterruptedIOException("Lectura cancelada")
    }
}

````

### `app/src/main/java/com/example/lectorsimple/LibreOfficeEngine.kt`

````kotlin
package com.example.lectorsimple

import android.app.Activity
import android.util.Log
import org.libreoffice.kit.Document
import org.libreoffice.kit.LibreOfficeKit
import org.libreoffice.kit.Office
import java.io.File
import java.io.InterruptedIOException

/**
 * Encapsulates LibreOfficeKit initialization and local offline document conversion.
 * All operations run on background threads (ReaderWorker).
 */
object LibreOfficeEngine {
    private const val TAG = "LibreOfficeEngine"
    private var initialized = false

    @Synchronized
    fun ensureInitialized(activity: Activity) {
        if (initialized) return
        try {
            LibreOfficeResources.prepare(activity.applicationContext)
            checkCancelled()
            Log.i(TAG, "Inicializando LibreOfficeKit...")
            LibreOfficeKit.putenv("SAL_LOG=+WARN+INFO")
            LibreOfficeKit.putenv("XDG_CACHE_HOME=${activity.cacheDir.absolutePath}")
            LibreOfficeKit.putenv("FONTCONFIG_FILE=${File(activity.applicationInfo.dataDir, "etc/fonts/fonts.conf").absolutePath}")
            LibreOfficeKit.putenv("FONTCONFIG_PATH=${File(activity.applicationInfo.dataDir, "etc/fonts").absolutePath}")
            LibreOfficeKit.init(activity)
            checkNotNull(LibreOfficeKit.getLibreOfficeKitHandle()) {
                "LibreOfficeKit no devolvió un controlador después de inicializar."
            }
            initialized = true
            Log.i(TAG, "LibreOfficeKit inicializado exitosamente.")
        } catch (interrupted: InterruptedIOException) {
            throw interrupted
        } catch (t: Throwable) {
            Log.e(TAG, "Error inicializando LibreOfficeKit", t)
            throw ReadingException("No se pudo preparar el lector de Word. Comprueba el espacio disponible y vuelve a abrir el documento.")
        }
    }

    /**
     * Converts a local .doc or .docx file to a temporary PDF file for sheet display.
     * Must be called from a worker thread.
     */
    @Synchronized
    fun convertWordToPdf(activity: Activity, sourceWordFile: File, outputPdfFile: File) {
        checkCancelled()
        ensureInitialized(activity)
        checkCancelled()

        val compatibleSource = DocxFontCompatibility.prepare(sourceWordFile, activity.cacheDir)
        try {
            convertPreparedWordToPdf(compatibleSource ?: sourceWordFile, outputPdfFile)
        } finally {
            compatibleSource?.delete()
        }
    }

    private fun convertPreparedWordToPdf(sourceWordFile: File, outputPdfFile: File) {

        val handle = LibreOfficeKit.getLibreOfficeKitHandle()
            ?: throw ReadingException("El motor de LibreOffice no proporcionó un controlador válido.")

        val office = Office(handle)
        val sourcePath = sourceWordFile.absolutePath

        val doc: Document? = try {
            office.documentLoad(sourcePath)
                ?: office.documentLoad("file://$sourcePath")
        } catch (t: Throwable) {
            Log.e(TAG, "Fallo al cargar documento en LibreOffice", t)
            throw ReadingException("Error al abrir el documento con LibreOffice: ${t.message}")
        }

        if (doc == null) {
            val error = office.error ?: "Formato o estructura no reconocidos"
            Log.e(TAG, "LibreOffice documentLoad retornó null: $error")
            throw ReadingException("No se pudo abrir el documento Word: $error")
        }

        try {
            checkCancelled()
            val outputPath = outputPdfFile.absolutePath
            Log.i(TAG, "Convirtiendo documento a PDF: $outputPath")
            doc.initializeForRendering()
            // The native JNI wrapper calls GetStringUTFChars for all three arguments;
            // a null options argument aborts the entire Android process.
            doc.saveAs(outputPath, "pdf", "")
            checkCancelled()
            if (!outputPdfFile.exists() || outputPdfFile.length() == 0L) {
                // Intento alternativo con esquema URI si la ruta plana no generó archivo
                doc.saveAs("file://$outputPath", "pdf", "")
            }
        } catch (interrupted: InterruptedException) {
            outputPdfFile.delete()
            throw InterruptedIOException("Conversión cancelada")
        } catch (interrupted: InterruptedIOException) {
            outputPdfFile.delete()
            throw interrupted
        } catch (t: Throwable) {
            outputPdfFile.delete()
            Log.e(TAG, "Error guardando documento como PDF", t)
            throw ReadingException("Error al generar la vista por hojas: ${t.message}")
        } finally {
            try {
                doc.destroy()
            } catch (e: Throwable) {
                Log.w(TAG, "Error al destruir instancia del documento LOK", e)
            }
        }

        if (!outputPdfFile.exists() || outputPdfFile.length() == 0L) {
            outputPdfFile.delete()
            throw ReadingException("No se pudo generar el archivo de visualización por hojas.")
        }
    }

    private fun checkCancelled() {
        if (Thread.currentThread().isInterrupted) throw InterruptedIOException("Operación cancelada")
    }
}

````

### `app/src/main/java/com/example/lectorsimple/LibreOfficeResources.kt`

````kotlin
package com.example.lectorsimple

import android.content.Context
import android.content.res.AssetManager
import android.util.AtomicFile
import java.io.File
import java.io.IOException
import java.io.InterruptedIOException

/** Prepares the real filesystem paths referenced by LibreOffice's unorc and sofficerc. */
internal object LibreOfficeResources {
    @Synchronized
    fun prepare(context: Context) {
        val assets = context.assets
        val files = mutableListOf<String>()
        collectFiles(assets, "unpack", files)
        val required = listOf(
            "program/sofficerc", "program/udkapi.rdb", "program/offapi.rdb",
            "program/oovbaapi.rdb", "etc/fonts/fonts.conf"
        )
        if (!files.containsAll(required) || files.none { it.startsWith("user/fonts/") }) {
            throw IOException("El paquete de recursos de LibreOffice está incompleto.")
        }
        // These stay inside the APK: native code accesses them through /assets.
        listOf(
            "program/fundamentalrc", "program/unorc", "program/bootstraprc",
            "program/services.rdb", "program/services/services.rdb", "share/registry/main.xcd"
        ).forEach { path ->
            assets.open(path).use { if (it.read() < 0) throw IOException("Recurso vacío: $path") }
        }

        val dataDir = File(context.applicationInfo.dataDir)
        val apk = File(context.applicationInfo.sourceDir)
        @Suppress("DEPRECATION")
        val updateTime = context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
        // Includes APK identity so a development reinstall with the same version also refreshes assets.
        val version = "2:$updateTime:${apk.absolutePath}:${apk.length()}:${apk.lastModified()}"
        val preferences = context.getSharedPreferences("libreoffice-resources", Context.MODE_PRIVATE)
        val ready = preferences.getString("version", null) == version && files.all { path ->
            val file = File(dataDir, path)
            file.isFile && file.length() > 0 && file.length() == preferences.getLong(path, -1)
        }
        if (ready) return

        // Invalidate first. Interrupted/failed extraction must be retried before entering JNI.
        if (!preferences.edit().clear().commit()) throw IOException("No se pudo preparar LibreOffice.")
        File(context.cacheDir, "fontconfig").deleteRecursively()
        val completed = preferences.edit()
        val buffer = ByteArray(32 * 1024)
        files.forEach { path ->
            checkCancelled()
            val destination = File(dataDir, path)
            val parent = destination.parentFile ?: throw IOException("Ruta de recursos inválida.")
            if (!parent.isDirectory && !parent.mkdirs()) throw IOException("No se pudo crear $path")
            val atomic = AtomicFile(destination)
            val output = atomic.startWrite()
            var copiedBytes = 0L
            try {
                assets.open("unpack/$path").use { input ->
                    if (path == "etc/fonts/fonts.conf") {
                        val config = input.readBytes().toString(Charsets.UTF_8).replace(
                            "<!--FREEDOCS_USER_FONTS_DIR-->",
                            "<dir>${File(dataDir, "user/fonts").absolutePath}</dir>"
                        )
                        output.write(config.toByteArray(Charsets.UTF_8))
                        copiedBytes = config.toByteArray(Charsets.UTF_8).size.toLong()
                    } else {
                    while (true) {
                        checkCancelled()
                        val count = input.read(buffer)
                        if (count < 0) break
                        output.write(buffer, 0, count)
                        copiedBytes += count
                    }
                    }
                }
                if (output.channel.size() == 0L) throw IOException("Recurso vacío: $path")
                atomic.finishWrite(output)
            } catch (failure: Throwable) {
                atomic.failWrite(output)
                throw failure
            }
            if (!destination.isFile || copiedBytes == 0L || destination.length() != copiedBytes) {
                throw IOException("No se pudo extraer $path")
            }
            completed.putLong(path, destination.length())
        }
        checkCancelled()
        if (!completed.putString("version", version).commit()) {
            throw IOException("No se pudo guardar la preparación de LibreOffice.")
        }
    }

    private fun collectFiles(assets: AssetManager, directory: String, files: MutableList<String>) {
        checkCancelled()
        val children = assets.list(directory) ?: throw IOException("Faltan recursos: $directory")
        if (children.isEmpty()) {
            files.add(directory.removePrefix("unpack/"))
        } else {
            children.forEach { collectFiles(assets, "$directory/$it", files) }
        }
    }

    private fun checkCancelled() {
        if (Thread.currentThread().isInterrupted) throw InterruptedIOException("Preparación cancelada")
    }
}

````

### `app/src/main/java/com/example/lectorsimple/MainActivity.kt`

````kotlin
package com.example.lectorsimple

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import java.io.File
import java.io.IOException
import java.io.InterruptedIOException
import java.util.concurrent.Future
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger

enum class WordViewMode { HOJA, MOVIL }

class MainActivity : Activity() {
    private lateinit var content: LinearLayout
    private lateinit var modeBar: LinearLayout
    private lateinit var btnHoja: Button
    private lateinit var btnMovil: Button
    private lateinit var title: TextView
    private lateinit var previous: Button
    private lateinit var next: Button
    private lateinit var pageLabel: TextView
    private val worker = ReaderWorker.executor
    private val generation = AtomicInteger()
    private var loading: Future<*>? = null
    private var rendering: Future<*>? = null
    private var frameVersion = 0
    // Bound decoded tiles waiting for the UI, independently of the worker speed.
    private val pendingBitmaps = Semaphore(2)
    // The worker exclusively owns the PDF session, including disposal.
    private var session: PdfSession? = null
    private var pdfView: PdfZoomView? = null
    private var wordView: WordZoomView? = null
    private var currentDocument: LocalDocument? = null
    private var currentWordText: String? = null
    private var uri: Uri? = null
    private var page = 0
    private var pageCount = 0
    private var wordMode = WordViewMode.HOJA
    private var sourceIsPdf = false
    private var pdfEditMode = false
    private val pdfEdits = PdfDocumentEdits()
    private var pendingEditedFile: File? = null
    private var extractedPdfText = ""
    private var editTextSize = 12f
    private var editColor = Color.BLACK
    private var savedHojaState: Bundle? = null
    private var savedMovilState: Bundle? = null
    private var pendingState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Android displays the launch theme until the first frame; no extra Activity or delay.
        setTheme(R.style.Theme_LectorSimple)
        super.onCreate(savedInstanceState)
        buildScreen()
        val restoredUri = savedInstanceState?.getString("uri")
        if (restoredUri != null) {
            pendingState = savedInstanceState.getBundle("position")
            savedHojaState = savedInstanceState.getBundle("hoja_position")
            savedMovilState = savedInstanceState.getBundle("movil_position")
            page = savedInstanceState.getInt("page", 0)
            savedInstanceState.getString("word_mode")?.let { modeStr ->
                wordMode = try { WordViewMode.valueOf(modeStr) } catch (_: Exception) { WordViewMode.HOJA }
            }
            openDocument(Uri.parse(restoredUri), restoring = true)
        } else {
            openIncomingDocument(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openIncomingDocument(intent)
    }

    private fun openIncomingDocument(intent: Intent?) {
        if (intent?.action != Intent.ACTION_VIEW) return
        val incoming = intent.data ?: return
        openDocument(incoming)
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(248, 249, 251))
        }
        root.setOnApplyWindowInsetsListener { view, insets ->
            if (Build.VERSION.SDK_INT >= 30) {
                val safe = insets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
                view.setPadding(safe.left, safe.top + dp(8), safe.right, safe.bottom)
            } else {
                @Suppress("DEPRECATION")
                view.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop + dp(8),
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            }
            insets
        }
        title = TextView(this).apply {
            text = "Lector simple"
            textSize = 18f
            setTextColor(Color.rgb(28, 35, 48))
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            setPadding(dp(16), 0, dp(16), dp(4))
        }
        val open = Button(this).apply {
            text = "Abrir documento"
            setOnClickListener { chooseDocument() }
        }

        // Barra conmutadora "Hoja | Móvil" para documentos Word
        modeBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            visibility = View.GONE
            setPadding(dp(12), dp(4), dp(12), dp(4))
        }
        btnHoja = Button(this).apply {
            text = "Hoja (Diseño)"
            setOnClickListener { if (sourceIsPdf) setPdfEditMode(false) else switchWordMode(WordViewMode.HOJA) }
        }
        btnMovil = Button(this).apply {
            text = "Móvil (Texto)"
            setOnClickListener { if (sourceIsPdf) setPdfEditMode(true) else switchWordMode(WordViewMode.MOVIL) }
        }
        modeBar.addView(btnHoja, LinearLayout.LayoutParams(0, -2, 1f).apply { setMargins(0, 0, dp(4), 0) })
        modeBar.addView(btnMovil, LinearLayout.LayoutParams(0, -2, 1f).apply { setMargins(dp(4), 0, 0, 0) })

        content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(title, LinearLayout.LayoutParams(-1, -2))
        root.addView(open, LinearLayout.LayoutParams(-1, -2).apply { setMargins(dp(12), 0, dp(12), 0) })
        root.addView(modeBar, LinearLayout.LayoutParams(-1, -2))
        root.addView(content, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
        if (Build.VERSION.SDK_INT >= 30) {
            val lightBars = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            window.insetsController?.setSystemBarsAppearance(lightBars, lightBars)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                (if (Build.VERSION.SDK_INT >= 26) View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR else 0)
        }
        root.requestApplyInsets()
        showMessage("Abre un PDF o un archivo Word (.docx o .doc).\n\nPellizca para hacer zoom.")
    }

    private fun chooseDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "application/msword", "application/octet-stream",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        startActivityForResult(intent, REQUEST_OPEN)
    }

    @Deprecated("Platform result API avoids an additional UI dependency.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SAVE && resultCode == RESULT_OK) {
            val target = data?.data ?: return
            val edited = pendingEditedFile ?: return
            worker.execute {
                try { contentResolver.openOutputStream(target, "wt")?.use { output -> edited.inputStream().use { it.copyTo(output) } } ?: throw IOException("No se pudo guardar en el destino seleccionado.")
                    edited.delete(); pendingEditedFile = null
                    runOnUiThread { showMessage("PDF editado guardado correctamente."); showPdfAgain() }
                } catch (failure: Exception) { edited.delete(); pendingEditedFile = null; runOnUiThread { showMessage("No se pudo guardar el PDF editado.") } }
            }
            return
        }
        if (requestCode != REQUEST_OPEN || resultCode != RESULT_OK) return
        val selected = data?.data ?: return
        try {
            contentResolver.takePersistableUriPermission(selected, data.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: SecurityException) {
            // Some providers only grant access for the current activity/session.
        }
        uri?.takeIf { it != selected }?.let { old ->
            try { contentResolver.releasePersistableUriPermission(old, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            catch (_: SecurityException) { /* No persisted grant for this provider. */ }
        }
        openDocument(selected)
    }

    private fun openDocument(selected: Uri, restoring: Boolean = false) {
        val revision = generation.incrementAndGet()
        frameVersion++
        loading?.cancel(true)
        rendering?.cancel(true)
        worker.purge()
        pdfView = null
        wordView = null
        uri = selected
        sourceIsPdf = false
        pdfEditMode = false
        pdfEdits.reset()
        extractedPdfText = ""
        if (!restoring) {
            page = 0
            pendingState = null
            savedHojaState = null
            savedMovilState = null
            wordMode = WordViewMode.HOJA
        }
        val requestedPage = page
        title.text = "Lector simple"
        modeBar.visibility = View.GONE
        showMessage("Abriendo documento…")
        loading = worker.submit {
            closeSession()
            closeCurrentDocument()
            currentWordText = null
            var local: LocalDocument? = null
            try {
                local = DocumentReader.stage(contentResolver, selected, cacheDir)
                DocumentReader.checkCancelled()
                val name = local.name
                currentDocument = local

                if (local.kind == DocumentKind.PDF) {
                    val pdf = PdfSession(local.file)
                    session = pdf
                    // PdfSession owns the file now
                    currentDocument = null
                    val count = pdf.pageCount
                    val selectedPage = requestedPage.coerceIn(0, count - 1)
                    val size = pdf.pageSize(selectedPage)
                    runOnUiThread {
                        if (generation.get() == revision && !isDestroyed) {
                            title.text = name
                            page = selectedPage
                            pageCount = count
                            sourceIsPdf = true
                            modeBar.visibility = View.VISIBLE
                            updateModeButtons()
                            extractedPdfText = try { PdfTextExtractor.extract(pdf.file).lineSequence().firstOrNull { it.isNotBlank() }.orEmpty() } catch (_: Exception) { "" }
                            showPdf(size, pendingState)
                            pendingState = null
                        }
                    }
                } else {
                    // Documento Word (.doc o .docx)
                    runOnUiThread {
                        if (generation.get() == revision && !isDestroyed) {
                            title.text = name
                            modeBar.visibility = View.VISIBLE
                            updateModeButtons()
                        }
                    }
                    if (wordMode == WordViewMode.HOJA) {
                        displayWordAsHoja(local, requestedPage, revision, name, pendingState ?: savedHojaState)
                        pendingState = null
                    } else {
                        displayWordAsMovil(local, revision, name, pendingState ?: savedMovilState)
                        pendingState = null
                    }
                }
            } catch (failure: Exception) {
                closeSession()
                closeCurrentDocument()
                reportFailure(failure, revision)
            } catch (_: OutOfMemoryError) {
                closeSession()
                closeCurrentDocument()
                reportFailure(ReadingException("El documento necesita demasiada memoria."), revision)
            }
        }
    }

    private fun displayWordAsHoja(local: LocalDocument, requestedPage: Int, revision: Int, docTitle: String, stateToRestore: Bundle?) {
        val sheetTempPdf = File.createTempFile("reader-sheet-", ".pdf", cacheDir)
        try {
            LibreOfficeEngine.convertWordToPdf(this@MainActivity, local.file, sheetTempPdf)
            DocumentReader.checkCancelled()
            val pdf = PdfSession(sheetTempPdf)
            session = pdf
            val count = pdf.pageCount
            val selectedPage = requestedPage.coerceIn(0, count - 1)
            val size = pdf.pageSize(selectedPage)
            runOnUiThread {
                if (generation.get() == revision && !isDestroyed) {
                    title.text = docTitle
                    page = selectedPage
                    pageCount = count
                    modeBar.visibility = View.VISIBLE
                    updateModeButtons()
                    showPdf(size, stateToRestore)
                }
            }
        } catch (t: Throwable) {
            sheetTempPdf.delete()
            throw t
        }
    }

    private fun displayWordAsMovil(local: LocalDocument, revision: Int, docTitle: String, stateToRestore: Bundle?) {
        val text = currentWordText ?: DocumentReader.readWord(local).also { currentWordText = it }
        runOnUiThread {
            if (generation.get() == revision && !isDestroyed) {
                title.text = docTitle
                modeBar.visibility = View.VISIBLE
                updateModeButtons()
                content.removeAllViews()
                wordView = WordZoomView(this@MainActivity, text, stateToRestore)
                content.addView(wordView, LinearLayout.LayoutParams(-1, -1))
            }
        }
    }

    private fun switchWordMode(targetMode: WordViewMode) {
        if (wordMode == targetMode) return
        val doc = currentDocument ?: return
        val revision = generation.incrementAndGet()
        frameVersion++
        loading?.cancel(true)
        rendering?.cancel(true)
        worker.purge()

        if (wordMode == WordViewMode.HOJA) {
            savedHojaState = pdfView?.savePosition()
        } else {
            savedMovilState = wordView?.savePosition()
        }
        wordMode = targetMode
        updateModeButtons()

        showMessage(if (targetMode == WordViewMode.HOJA) "Generando vista de hoja…" else "Cargando texto…")
        loading = worker.submit {
            try {
                if (targetMode == WordViewMode.HOJA) {
                    closeSession()
                    displayWordAsHoja(doc, page, revision, doc.name, savedHojaState)
                } else {
                    displayWordAsMovil(doc, revision, doc.name, savedMovilState)
                }
            } catch (failure: Exception) {
                reportFailure(failure, revision)
            }
        }
    }

    private fun updateModeButtons() {
        if (sourceIsPdf) {
            btnHoja.text = "Lectura"; btnMovil.text = "Editar PDF"
            val selected = if (pdfEditMode) btnMovil else btnHoja
            val other = if (pdfEditMode) btnHoja else btnMovil
            selected.setTextColor(Color.WHITE); selected.setBackgroundColor(Color.rgb(35, 75, 140)); selected.setTypeface(null, Typeface.BOLD)
            other.setTextColor(Color.DKGRAY); other.setBackgroundColor(Color.rgb(220, 224, 232)); other.setTypeface(null, Typeface.NORMAL)
            return
        }
        btnHoja.text = "Hoja (Diseño)"; btnMovil.text = "Móvil (Texto)"
        if (wordMode == WordViewMode.HOJA) {
            btnHoja.setTextColor(Color.WHITE)
            btnHoja.setBackgroundColor(Color.rgb(35, 75, 140))
            btnHoja.setTypeface(null, Typeface.BOLD)
            btnMovil.setTextColor(Color.DKGRAY)
            btnMovil.setBackgroundColor(Color.rgb(220, 224, 232))
            btnMovil.setTypeface(null, Typeface.NORMAL)
        } else {
            btnMovil.setTextColor(Color.WHITE)
            btnMovil.setBackgroundColor(Color.rgb(35, 75, 140))
            btnMovil.setTypeface(null, Typeface.BOLD)
            btnHoja.setTextColor(Color.DKGRAY)
            btnHoja.setBackgroundColor(Color.rgb(220, 224, 232))
            btnHoja.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun showPdf(size: Pair<Int, Int>, restoreState: Bundle? = null) {
        content.removeAllViews()
        val controls = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        previous = Button(this).apply { text = "Anterior"; setOnClickListener { changePage(page - 1) } }
        next = Button(this).apply { text = "Siguiente"; setOnClickListener { changePage(page + 1) } }
        pageLabel = TextView(this).apply { gravity = Gravity.CENTER; setTextColor(Color.DKGRAY); textSize = 14f }
        controls.addView(previous, LinearLayout.LayoutParams(0, -2, 1f))
        controls.addView(pageLabel, LinearLayout.LayoutParams(0, -2, 1f))
        controls.addView(next, LinearLayout.LayoutParams(0, -2, 1f))
        pdfView = PdfZoomView(this, ::renderFrame)
        pdfView?.documentEdits = pdfEdits
        pdfView?.editMode = pdfEditMode
        pdfView?.onExistingText = { extractedPdfText.lineSequence().filter { it.isNotBlank() }.take(1).joinToString(" ") }
        pdfView?.onEditText = { bounds, background, text -> showTextEditor(bounds, background, text, true) }
        pdfView?.onEditTap = { tool, x, y -> if (tool == PdfTool.ADD_TEXT) showTextEditor(RectF(x, y, x + 190f, y + 48f), Color.WHITE, "", false) }
        content.addView(pdfView, LinearLayout.LayoutParams(-1, 0, 1f))
        if (sourceIsPdf) content.addView(buildPdfEditToolbar(), LinearLayout.LayoutParams(-1, -2))
        content.addView(controls, LinearLayout.LayoutParams(-1, -2))
        pdfView?.showPage(page, size, restoreState)
        updateControls()
    }

    private fun buildPdfEditToolbar(): View {
        val scroll = HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false }
        val bar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; visibility = if (pdfEditMode) View.VISIBLE else View.GONE }
        fun tool(label: String, tool: PdfTool) = Button(this).apply { text = label; setOnClickListener { pdfView?.activeTool = tool } }
        bar.addView(tool("Editar texto", PdfTool.REPLACE_TEXT)); bar.addView(tool("+ Texto", PdfTool.ADD_TEXT)); bar.addView(tool("Mover", PdfTool.MOVE))
        bar.addView(tool("Firmar", PdfTool.INK)); bar.addView(tool("Resaltar", PdfTool.HIGHLIGHT))
        bar.addView(Button(this).apply { text = "Borrar"; setOnClickListener { pdfEdits.clearPage(page); pdfView?.invalidate() } })
        bar.addView(Button(this).apply { text = "Deshacer"; setOnClickListener { pdfEdits.undo(); pdfView?.invalidate() } })
        bar.addView(Button(this).apply { text = "Guardar PDF"; setOnClickListener { saveEditedPdf() } })
        scroll.addView(bar); return scroll
    }

    private fun setPdfEditMode(enabled: Boolean) {
        pdfEditMode = enabled
        updateModeButtons()
        pdfView?.editMode = enabled
        (content.getChildAt(1) as? HorizontalScrollView)?.getChildAt(0)?.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    private fun showTextEditor(bounds: RectF, background: Int, initial: String, cover: Boolean) {
        val input = EditText(this).apply { setText(initial); minLines = 1; maxLines = 6; textSize = editTextSize }
        val coverCheck = CheckBox(this).apply { text = "Cubrir el texto original"; isChecked = cover }
        val sizeRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val preview = TextView(this).apply { text = "${editTextSize.toInt()} sp"; setPadding(dp(12), 0, dp(12), 0) }
        sizeRow.addView(Button(this).apply { text = "A−"; setOnClickListener { editTextSize = (editTextSize - 1).coerceAtLeast(6f); preview.text = "${editTextSize.toInt()} sp"; input.textSize = editTextSize } })
        sizeRow.addView(preview); sizeRow.addView(Button(this).apply { text = "A+"; setOnClickListener { editTextSize = (editTextSize + 1).coerceAtMost(72f); preview.text = "${editTextSize.toInt()} sp"; input.textSize = editTextSize } })
        val colors = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        listOf("Negro" to Color.BLACK, "Azul" to Color.BLUE, "Rojo" to Color.RED).forEach { (label, color) -> colors.addView(Button(this).apply { text = label; setOnClickListener { editColor = color } }) }
        val body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), dp(4)); addView(input); addView(sizeRow); addView(colors); if (cover) addView(coverCheck) }
        android.app.AlertDialog.Builder(this).setTitle(if (cover) "Editar texto" else "Añadir texto").setView(body)
            .setNegativeButton("Cancelar", null).setPositiveButton("Aplicar") { _, _ ->
                if (cover && !coverCheck.isChecked) return@setPositiveButton
                pdfEdits.add(PdfTextEdit(System.nanoTime(), page, RectF(bounds), input.text.toString(), editTextSize, editColor, background, coverCheck.isChecked))
                pdfView?.invalidate()
            }.show()
    }

    private fun saveEditedPdf() {
        val active = session ?: return
        val temp = File.createTempFile("reader-edited-", ".pdf", cacheDir)
        val revision = generation.get()
        showMessage("Guardando PDF editado…")
        worker.execute {
            try {
                PdfSaver.saveEditedPdf(active, pdfEdits, temp)
                val source = uri
                val savedDirectly = try {
                    if (source == null) false else contentResolver.openOutputStream(source, "wt")?.use { output -> temp.inputStream().use { it.copyTo(output) }; true } ?: false
                } catch (_: Exception) { false }
                if (savedDirectly) { temp.delete(); runOnUiThread { if (revision == generation.get()) { showMessage("PDF editado guardado en el documento original."); showPdfAgain() } } }
                else runOnUiThread {
                    if (revision != generation.get() || isDestroyed) { temp.delete(); return@runOnUiThread }
                    pendingEditedFile = temp
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply { addCategory(Intent.CATEGORY_OPENABLE); type = "application/pdf"; putExtra(Intent.EXTRA_TITLE, "documento-editado.pdf") }
                    startActivityForResult(intent, REQUEST_SAVE)
                }
            } catch (failure: Exception) { temp.delete(); runOnUiThread { if (revision == generation.get()) { showMessage("No se pudo guardar el PDF editado."); showPdfAgain() } } }
        }
    }

    private fun showPdfAgain() {
        val active = session ?: return
        worker.execute { try { val size = active.pageSize(page); runOnUiThread { if (!isDestroyed) showPdf(size, pdfView?.savePosition()) } } catch (_: Exception) {} }
    }

    private fun changePage(index: Int) {
        if (index !in 0 until pageCount) return
        page = index
        frameVersion++
        rendering?.cancel(true)
        previous.isEnabled = false
        next.isEnabled = false
        val revision = generation.get()
        val version = frameVersion
        worker.submit {
            try {
                val size = session?.pageSize(index) ?: return@submit
                runOnUiThread {
                    if (revision == generation.get() && version == frameVersion && !isDestroyed) {
                        pdfView?.showPage(index, size)
                        updateControls()
                    }
                }
            } catch (failure: Exception) { reportFailure(failure, revision) }
        }
    }

    private fun updateControls() {
        if (::previous.isInitialized && ::next.isInitialized && ::pageLabel.isInitialized) {
            previous.isEnabled = page > 0
            next.isEnabled = page < pageCount - 1
            pageLabel.text = "${page + 1} / $pageCount"
        }
    }

    private fun renderFrame(viewport: PdfViewport) {
        // Ignore delayed draws from the previous page while the worker loads the next one.
        if (viewport.page != page) return
        val revision = generation.get()
        val version = frameVersion
        val targetView = pdfView ?: return
        rendering?.cancel(true)
        worker.purge()
        rendering = worker.submit {
            try {
                session?.render(viewport) { image ->
                    try {
                        pendingBitmaps.acquire()
                    } catch (interrupted: InterruptedException) {
                        image.bitmap.recycle()
                        throw InterruptedIOException()
                    }
                    runOnUiThread {
                        try {
                            if (revision == generation.get() && version == frameVersion && !isDestroyed && pdfView === targetView) {
                                targetView.displayFrame(image)
                            } else image.bitmap.recycle()
                        } finally { pendingBitmaps.release() }
                    }
                }
            } catch (failure: Exception) {
                if (failure !is InterruptedIOException && !Thread.currentThread().isInterrupted) reportFailure(failure, revision, version)
            } catch (_: OutOfMemoryError) {
                reportFailure(ReadingException("No hay suficiente memoria para mostrar esta página."), revision, version)
            }
        }
    }

    private fun reportFailure(failure: Exception, revision: Int, version: Int? = null) {
        if (failure is InterruptedIOException || Thread.currentThread().isInterrupted) return
        val message = when {
            failure is ReadingException -> failure.message!!
            failure is SecurityException || failure.javaClass.simpleName.contains("Encrypted") -> "No se pudo abrir el documento. Comprueba sus permisos o si está protegido con contraseña."
            else -> "No se pudo abrir el documento. Puede estar dañado o usar un formato no compatible."
        }
        runOnUiThread {
            if (revision == generation.get() && !isDestroyed && (version == null || version == frameVersion)) {
                generation.incrementAndGet()
                frameVersion++
                rendering?.cancel(true)
                worker.execute {
                    closeSession()
                    closeCurrentDocument()
                }
                pdfView = null
                wordView = null
                modeBar.visibility = View.GONE
                showMessage(message)
            }
        }
    }

    private fun showMessage(message: String) {
        content.removeAllViews()
        content.addView(TextView(this).apply {
            text = message
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(Color.DKGRAY)
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }, LinearLayout.LayoutParams(-1, -1))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("uri", uri?.toString())
        outState.putInt("page", page)
        outState.putString("word_mode", wordMode.name)
        val currentPosition = if (pdfView != null && ::previous.isInitialized && !previous.isEnabled && !next.isEnabled && pageCount > 1)
            null else pdfView?.savePosition() ?: wordView?.savePosition() ?: pendingState
        outState.putBundle("position", currentPosition)
        outState.putBundle("hoja_position", if (wordMode == WordViewMode.HOJA) pdfView?.savePosition() else savedHojaState)
        outState.putBundle("movil_position", if (wordMode == WordViewMode.MOVIL) wordView?.savePosition() else savedMovilState)
    }

    private fun closeSession() {
        val closing = session
        session = null
        closing?.close()
    }

    private fun closeCurrentDocument() {
        val doc = currentDocument
        currentDocument = null
        doc?.file?.delete()
    }

    override fun onDestroy() {
        generation.incrementAndGet()
        loading?.cancel(true)
        rendering?.cancel(true)
        worker.purge()
        worker.execute {
            closeSession()
            closeCurrentDocument()
        }
        super.onDestroy()
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    companion object { private const val REQUEST_OPEN = 1001; private const val REQUEST_SAVE = 1002 }
}

````

### `app/src/main/java/com/example/lectorsimple/PdfEditModel.kt`

````kotlin
package com.example.lectorsimple

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.InflaterInputStream
import kotlin.math.abs

enum class PdfTool { REPLACE_TEXT, ADD_TEXT, MOVE, INK, HIGHLIGHT }

data class PdfTextEdit(
    val id: Long,
    val page: Int,
    val bounds: RectF,
    var text: String,
    var size: Float = 12f,
    var color: Int = Color.BLACK,
    var background: Int = Color.WHITE,
    var coverOriginal: Boolean = false
)

data class PdfStrokeEdit(val page: Int, val points: List<PointF>, val color: Int, val width: Float, val highlight: Boolean)
data class PdfPageEdit(val page: Int, val texts: MutableList<PdfTextEdit> = mutableListOf(), val strokes: MutableList<PdfStrokeEdit> = mutableListOf())

class PdfDocumentEdits {
    private val pages = linkedMapOf<Int, PdfPageEdit>()
    private val history = ArrayDeque<() -> Unit>()
    private fun page(index: Int) = pages.getOrPut(index) { PdfPageEdit(index) }
    fun texts(index: Int): List<PdfTextEdit> = pages[index]?.texts.orEmpty()
    fun strokes(index: Int): List<PdfStrokeEdit> = pages[index]?.strokes.orEmpty()
    fun add(edit: PdfTextEdit) { page(edit.page).texts.add(edit); history.addLast { page(edit.page).texts.remove(edit) } }
    fun add(edit: PdfStrokeEdit) { page(edit.page).strokes.add(edit); history.addLast { page(edit.page).strokes.remove(edit) } }
    fun remove(edit: PdfTextEdit) { val p = page(edit.page); val i = p.texts.indexOf(edit); if (i >= 0) { p.texts.removeAt(i); history.addLast { p.texts.add(i.coerceAtMost(p.texts.size), edit) } } }
    fun move(edit: PdfTextEdit, bounds: RectF) { val old = RectF(edit.bounds); edit.bounds.set(bounds); history.addLast { edit.bounds.set(old) } }
    fun recordMove(edit: PdfTextEdit, oldBounds: RectF) { val old = RectF(oldBounds); history.addLast { edit.bounds.set(old) } }
    fun clearPage(index: Int) { val old = pages.remove(index) ?: return; history.addLast { pages[index] = old } }
    fun clearAll() { val old = LinkedHashMap(pages); pages.clear(); history.addLast { pages.putAll(old) } }
    fun reset() { pages.clear(); history.clear() }
    fun undo(): Boolean { if (history.isEmpty()) return false; history.removeLast().invoke(); return true }
    fun hasEdits() = pages.values.any { it.texts.isNotEmpty() || it.strokes.isNotEmpty() }
}

object PdfEditRenderer {
    fun drawPageEdits(canvas: Canvas, edits: PdfDocumentEdits, page: Int) {
        edits.strokes(page).forEach { stroke ->
            if (stroke.points.isEmpty()) return@forEach
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = if (stroke.highlight) (stroke.color and 0x00ffffff) or 0x55000000 else stroke.color
                style = Paint.Style.STROKE; strokeWidth = stroke.width; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND
            }
            val path = Path().apply { moveTo(stroke.points.first().x, stroke.points.first().y); stroke.points.drop(1).forEach { lineTo(it.x, it.y) } }
            canvas.drawPath(path, paint)
        }
        edits.texts(page).forEach { edit ->
            if (edit.coverOriginal) canvas.drawRect(edit.bounds, Paint().apply { color = edit.background; style = Paint.Style.FILL })
            val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
                color = edit.color; textSize = edit.size; isSubpixelText = true; isLinearText = true
            }
            val width = edit.bounds.width().toInt().coerceAtLeast(1)
            val layout = StaticLayout.Builder.obtain(edit.text, 0, edit.text.length, textPaint, width)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL).setIncludePad(false).setLineSpacing(0f, 1f).build()
            canvas.save(); canvas.translate(edit.bounds.left, edit.bounds.top); layout.draw(canvas); canvas.restore()
        }
    }
}

object VisualLineDetector {
    data class Result(val bounds: RectF, val background: Int)

    fun detect(bitmap: Bitmap, pageWidth: Int, pageHeight: Int, x: Float, y: Float): Result? {
        if (bitmap.width < 3 || bitmap.height < 3) return null
        val sx = bitmap.width.toFloat() / pageWidth; val sy = bitmap.height.toFloat() / pageHeight
        val px = (x * sx).toInt().coerceIn(1, bitmap.width - 2); val py = (y * sy).toInt().coerceIn(1, bitmap.height - 2)
        val paper = medianColor(intArrayOf(bitmap.getPixel(1, 1), bitmap.getPixel(bitmap.width - 2, 1), bitmap.getPixel(1, bitmap.height - 2), bitmap.getPixel(bitmap.width - 2, bitmap.height - 2)))
        fun ink(row: Int): Boolean {
            for (xx in maxOf(0, px - 3)..minOf(bitmap.width - 1, px + 3)) if (colorDistance(bitmap.getPixel(xx, row), paper) > 55) return true
            return false
        }
        if (!ink(py) && !ink(py - 1) && !ink(py + 1)) return null
        var top = py; var bottom = py
        while (top > 0 && ink(top - 1) && py - top < bitmap.height / 30) top--
        while (bottom < bitmap.height - 1 && ink(bottom + 1) && bottom - py < bitmap.height / 30) bottom++
        top = maxOf(0, top - 2); bottom = minOf(bitmap.height - 1, bottom + 2)
        var left = bitmap.width - 1; var right = 0
        for (row in top..bottom) for (xx in 0 until bitmap.width) if (colorDistance(bitmap.getPixel(xx, row), paper) > 55) { left = minOf(left, xx); right = maxOf(right, xx) }
        if (right <= left) return null
        return Result(RectF(left / sx, top / sy, (right + 1) / sx, (bottom + 1) / sy), paper)
    }

    private fun medianColor(colors: IntArray): Int {
        val r = colors.map { Color.red(it) }.sorted()[colors.size / 2]; val g = colors.map { Color.green(it) }.sorted()[colors.size / 2]; val b = colors.map { Color.blue(it) }.sorted()[colors.size / 2]
        return Color.rgb(r, g, b)
    }
    private fun colorDistance(a: Int, b: Int) = abs(Color.red(a) - Color.red(b)) + abs(Color.green(a) - Color.green(b)) + abs(Color.blue(a) - Color.blue(b))
}

/** Lightweight extraction of standard PDF text operators for line prefill. */
object PdfTextExtractor {
    fun extract(file: File): String {
        val bytes = file.readBytes()
        val result = StringBuilder()
        val source = bytes.toString(Charsets.ISO_8859_1)
        val streamPattern = Regex("(?s)(<<.*?>>)\\s*stream\\r?\\n(.*?)\\r?\\nendstream")
        for (match in streamPattern.findAll(source)) {
            val raw = match.groupValues[2].toByteArray(Charsets.ISO_8859_1)
            val decoded = try { if (match.groupValues[1].contains("FlateDecode")) InflaterInputStream(raw.inputStream()).use { it.readBytes() } else raw } catch (_: Exception) { continue }
            val content = decoded.toString(Charsets.ISO_8859_1)
            for (block in Regex("(?s)BT(.*?)ET").findAll(content)) {
                val operators = block.groupValues[1]
                val strings = Regex("\\((?:\\\\.|[^\\\\)])*\\)\\s*Tj|\\[(.*?)\\]\\s*TJ|\\((?:\\\\.|[^\\\\)])*\\)\\s*'|\\((?:\\\\.|[^\\\\)])*\\)\\s*\\\"")
                for (op in strings.findAll(operators)) {
                    val tokens = Regex("\\((?:\\\\.|[^\\\\)])*\\)|<([0-9A-Fa-f]+)>").findAll(op.value)
                    for (token in tokens) result.append(decodePdfString(token.value)).append(' ')
                }
                result.append('\n')
            }
        }
        return result.toString().trim()
    }

    private fun decodePdfString(token: String): String {
        if (token.startsWith("<")) {
            val bytes = token.trim('<', '>').chunked(2).mapNotNull { it.toIntOrNull(16)?.toByte() }.toByteArray()
            return if (bytes.size >= 2 && bytes[0] == 0xfe.toByte() && bytes[1] == 0xff.toByte()) bytes.copyOfRange(2, bytes.size).toString(Charsets.UTF_16BE) else bytes.toString(Charsets.ISO_8859_1)
        }
        val body = token.substring(1, token.lastIndex)
        return Regex("\\\\([nrtbf()\\\\]|[0-7]{1,3}|\\r?\\n)").replace(body) { m ->
            when (val c = m.groupValues[1]) { "n" -> "\n"; "r" -> "\r"; "t" -> "\t"; "b" -> "\b"; "f" -> "\u000c"; "\n", "\r\n" -> ""; else -> if (c.firstOrNull()?.isDigit() == true) c.toInt(8).toChar().toString() else c }
        }
    }
}

object PdfSaver {
    fun saveEditedPdf(session: PdfSession, edits: PdfDocumentEdits, outputFile: File) {
        val document = PdfDocument()
        try {
            for (index in 0 until session.pageCount) {
                DocumentReader.checkCancelled()
                val (width, height) = session.pageSize(index)
                val page = document.startPage(PdfDocument.PageInfo.Builder(width, height, index + 1).create())
                val bitmap = session.renderPageBitmap(index, 2f)
                try {
                    page.canvas.drawBitmap(bitmap, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), Paint(Paint.FILTER_BITMAP_FLAG))
                } finally { bitmap.recycle() }
                PdfEditRenderer.drawPageEdits(page.canvas, edits, index)
                document.finishPage(page)
            }
            FileOutputStream(outputFile).use { document.writeTo(it) }
        } catch (failure: Throwable) {
            outputFile.delete()
            throw failure
        } finally { document.close() }
    }
}

````

### `app/src/main/java/com/example/lectorsimple/PdfSession.kt`

````kotlin
package com.example.lectorsimple

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.Closeable
import java.io.File
import kotlin.math.ceil

internal const val PDF_TILE_SIZE = 512
data class PdfTileKey(val level: Int, val column: Int, val row: Int, val viewWidth: Int)
data class PdfViewport(val page: Int, val scale: Float, val tiles: List<PdfTileKey>, val preview: Boolean)
data class PdfFrame(val page: Int, val key: PdfTileKey?, val bitmap: Bitmap, val bounds: android.graphics.RectF)

/** All access is serialized by MainActivity's worker, including close. */
class PdfSession(internal val file: File) : Closeable {
    private val renderer: PdfRenderer
    val pageCount: Int get() = renderer.pageCount

    init {
        val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        try {
            renderer = PdfRenderer(descriptor)
            if (renderer.pageCount == 0) {
                renderer.close()
                throw ReadingException("El PDF no contiene páginas.")
            }
        } catch (failure: Throwable) {
            descriptor.close()
            file.delete()
            throw failure
        }
    }

    fun pageSize(index: Int): Pair<Int, Int> = renderer.openPage(index).use { it.width to it.height }

    fun render(viewport: PdfViewport, deliver: (PdfFrame) -> Unit) {
        DocumentReader.checkCancelled()
        renderer.openPage(viewport.page).use { page ->
            if (viewport.preview) {
                // Match the visual weight of the first detail tiles while limiting memory use.
                val scale = minOf(1.5, kotlin.math.sqrt(786_432.0 / (page.width.toDouble() * page.height))).toFloat()
                val bounds = android.graphics.RectF(0f, 0f, page.width.toFloat(), page.height.toFloat())
                deliver(rasterize(page, null, bounds, scale))
            }
            for (key in viewport.tiles) {
                DocumentReader.checkCancelled()
                val left = key.column * PDF_TILE_SIZE / viewport.scale
                val top = key.row * PDF_TILE_SIZE / viewport.scale
                val right = minOf(page.width.toFloat(), (key.column + 1) * PDF_TILE_SIZE / viewport.scale)
                val bottom = minOf(page.height.toFloat(), (key.row + 1) * PDF_TILE_SIZE / viewport.scale)
                if (right > left && bottom > top) {
                    deliver(rasterize(page, key, android.graphics.RectF(left, top, right, bottom), viewport.scale))
                }
            }
        }
    }

    private fun rasterize(page: PdfRenderer.Page, key: PdfTileKey?, bounds: android.graphics.RectF, scale: Float): PdfFrame {
        DocumentReader.checkCancelled()
        val width = ceil(bounds.width() * scale).toInt().coerceAtLeast(1)
        val height = ceil(bounds.height() * scale).toInt().coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        try {
            bitmap.eraseColor(Color.WHITE)
            val matrix = Matrix().apply {
                setValues(floatArrayOf(
                    scale, 0f, -bounds.left * scale,
                    0f, scale, -bounds.top * scale,
                    0f, 0f, 1f
                ))
            }
            page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            val exactBounds = if (key == null) bounds else android.graphics.RectF(
                bounds.left, bounds.top, bounds.left + width / scale, bounds.top + height / scale
            )
            return PdfFrame(page.index, key, bitmap, exactBounds)
        } catch (failure: Throwable) {
            bitmap.recycle()
            throw failure
        }
    }

    fun renderPageBitmap(index: Int, scale: Float = 2f): Bitmap {
        DocumentReader.checkCancelled()
        return renderer.openPage(index).use { page ->
            val bounds = android.graphics.RectF(0f, 0f, page.width.toFloat(), page.height.toFloat())
            rasterize(page, null, bounds, scale).bitmap
        }
    }

    override fun close() {
        try { renderer.close() } finally { file.delete() }
    }
}

````

### `app/src/main/java/com/example/lectorsimple/PdfZoomView.kt`

````kotlin
package com.example.lectorsimple

import android.content.Context
import android.graphics.Canvas
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.OverScroller
import kotlin.math.max
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/** Page preview plus reusable tiles. All bitmap/cache ownership stays on the UI thread. */
class PdfZoomView(context: Context, private val requestFrame: (PdfViewport) -> Unit) : View(context) {
    var editMode = false
        set(value) { field = value; invalidate() }
    var activeTool = PdfTool.REPLACE_TEXT
    var documentEdits: PdfDocumentEdits? = null
    var onEditText: ((RectF, Int, String) -> Unit)? = null
    var onEditTap: ((PdfTool, Float, Float) -> Unit)? = null
    var onExistingText: (String) -> String = { "" }
    private val strokePoints = mutableListOf<PointF>()
    private var movingEdit: PdfTextEdit? = null
    private var moveOffsetX = 0f
    private var moveOffsetY = 0f
    private var moveOriginal: RectF? = null
    private var page = -1
    private var pageWidth = 0
    private var pageHeight = 0
    private var zoom = 1f
    private var x = 0f
    private var y = 0f
    private var preview: PdfFrame? = null
    private val tiles = LinkedHashMap<PdfTileKey, PdfFrame>(24, 0.75f, true)
    private var tileBytes = 0L
    private val pageRect = RectF()
    private var wanted = emptySet<PdfTileKey>()
    private var restore: Bundle? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val scroller = OverScroller(context)
    private var renderPosted = false
    private val render = Runnable {
        renderPosted = false
        viewport()?.let(requestFrame)
    }
    private val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            scroller.forceFinished(true)
            return true
        }
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            changeZoom(zoom * detector.scaleFactor, detector.focusX, detector.focusY)
            return true
        }
        override fun onScaleEnd(detector: ScaleGestureDetector) { scheduleRender() }
    })
    private val gestures = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            scroller.forceFinished(true)
            return true
        }
        override fun onScroll(first: MotionEvent?, event: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (!scaleDetector.isInProgress && event.pointerCount == 1) {
                x -= distanceX
                y -= distanceY
                constrain()
                invalidate()
                scheduleRender()
            }
            return true
        }
        override fun onDoubleTap(event: MotionEvent): Boolean {
            changeZoom(if (zoom > 1.05f) 1f else 2.5f, event.x, event.y)
            return true
        }
        override fun onSingleTapConfirmed(event: MotionEvent): Boolean { performClick(); return true }
        override fun onFling(first: MotionEvent?, event: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (scaleDetector.isInProgress || pageWidth == 0) return false
            val scale = baseScale() * zoom
            val minX = if (pageWidth * scale > width) (width - pageWidth * scale).toInt() else x.toInt()
            val maxX = if (pageWidth * scale > width) 0 else x.toInt()
            val minY = if (pageHeight * scale > height) (height - pageHeight * scale).toInt() else y.toInt()
            val maxY = if (pageHeight * scale > height) 0 else y.toInt()
            scroller.fling(x.toInt(), y.toInt(), velocityX.toInt(), velocityY.toInt(), minX, maxX, minY, maxY)
            postInvalidateOnAnimation()
            return true
        }
    })

    init {
        setBackgroundColor(Color.BLACK)
        contentDescription = "Página PDF. Pellizca para ampliar y desliza para desplazarte."
        isFocusable = true
    }

    fun showPage(index: Int, dimensions: Pair<Int, Int>, state: Bundle? = null) {
        releaseFrame()
        scroller.forceFinished(true)
        page = index
        pageWidth = dimensions.first
        pageHeight = dimensions.second
        zoom = 1f
        x = 0f
        y = 0f
        restore = state
        applyRestore()
        constrain()
        invalidate()
        scheduleRender()
    }

    private fun applyRestore() {
        if (width == 0) return
        restore?.let {
            zoom = it.getFloat("zoom", 1f).coerceIn(1f, 6f)
            x = it.getFloat("x", 0f) * width
            y = it.getFloat("y", 0f) * width
        }
        restore = null
    }

    fun savePosition(): Bundle = restore ?: Bundle().apply {
        putFloat("zoom", zoom)
        putFloat("x", x / max(1, width))
        putFloat("y", y / max(1, width))
    }

    private fun viewport(): PdfViewport? {
        if (width <= 0 || height <= 0 || pageWidth <= 0) return null
        if (scaleDetector.isInProgress && preview != null) return null
        val displayScale = baseScale() * zoom
        // Discrete resolution levels let tiles survive pans and small zoom changes.
        // Cap visible detail at 2 million pixels on exceptionally large screens.
        val factor = minOf(1.0, sqrt(2_000_000.0 / (width.toDouble() * height))).toFloat()
        val level = floor(ln((zoom * factor).toDouble()) / ln(2.0) * 2).toInt()
        val scale = baseScale() * 2.0.pow(level / 2.0).toFloat()
        val columns = ceil(pageWidth * scale / PDF_TILE_SIZE).toInt().coerceAtLeast(1)
        val rows = ceil(pageHeight * scale / PDF_TILE_SIZE).toInt().coerceAtLeast(1)
        val left = floor(-x / displayScale * scale / PDF_TILE_SIZE).toInt().coerceIn(0, columns - 1)
        val right = floor((width - x) / displayScale * scale / PDF_TILE_SIZE).toInt().coerceIn(0, columns - 1)
        val top = floor(-y / displayScale * scale / PDF_TILE_SIZE).toInt().coerceIn(0, rows - 1)
        val bottom = floor((height - y) / displayScale * scale / PDF_TILE_SIZE).toInt().coerceIn(0, rows - 1)
        val keys = ArrayList<PdfTileKey>()
        for (row in top..bottom) for (column in left..right) keys.add(PdfTileKey(level, column, row, width))
        wanted = keys.toSet()
        val missing = keys.filter { tiles[it] == null }.sortedBy {
            val dx = it.column - (left + right) / 2f
            val dy = it.row - (top + bottom) / 2f
            dx * dx + dy * dy
        }
        if (missing.isEmpty() && preview != null) return null
        return PdfViewport(page, scale, missing, preview == null)
    }

    fun displayFrame(value: PdfFrame) {
        if (value.page != page || !isAttachedToWindow) { value.bitmap.recycle(); return }
        val key = value.key
        if (key != null && key.viewWidth != width) { value.bitmap.recycle(); return }
        if (key == null) {
            if (preview == null) preview = value else value.bitmap.recycle()
        } else {
            tiles.put(key, value)?.let { tileBytes -= it.bitmap.allocationByteCount; it.bitmap.recycle() }
            tileBytes += value.bitmap.allocationByteCount
            while (tileBytes > 16L * 1024 * 1024 && tiles.size > 1) {
                // Evict old/offscreen detail before currently visible tiles.
                val evictedKey = tiles.keys.firstOrNull { it !in wanted } ?: tiles.keys.first()
                val evicted = tiles.remove(evictedKey)!!
                tileBytes -= evicted.bitmap.allocationByteCount
                evicted.bitmap.recycle()
            }
        }
        invalidate()
    }

    private fun changeZoom(value: Float, focusX: Float, focusY: Float) {
        val next = value.coerceIn(1f, 6f)
        val ratio = next / zoom
        x = focusX - (focusX - x) * ratio
        y = focusY - (focusY - y) * ratio
        zoom = next
        constrain()
        invalidate()
        scheduleRender()
    }

    private fun baseScale(): Float = width.toFloat() / pageWidth

    private fun pagePoint(screenX: Float, screenY: Float): PointF {
        val scale = baseScale() * zoom
        return PointF((screenX - x) / scale, (screenY - y) / scale)
    }

    private fun constrain() {
        if (pageWidth <= 0 || width <= 0) return
        val scale = baseScale() * zoom
        val scaledWidth = pageWidth * scale
        val scaledHeight = pageHeight * scale
        x = if (scaledWidth <= width) (width - scaledWidth) / 2 else x.coerceIn(width - scaledWidth, 0f)
        y = if (scaledHeight <= height) (height - scaledHeight) / 2 else y.coerceIn(height - scaledHeight, 0f)
    }

    private fun scheduleRender() {
        if (!renderPosted) { renderPosted = true; postDelayed(render, 32) }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // Tile levels depend on the view width; the full-page preview is reusable.
        tiles.values.forEach { it.bitmap.recycle() }
        tiles.clear()
        tileBytes = 0
        if (oldw > 0) { x *= w.toFloat() / oldw; y *= w.toFloat() / oldw }
        applyRestore()
        constrain()
        scheduleRender()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (pageWidth <= 0 || width <= 0) return
        val scale = baseScale() * zoom
        canvas.save()
        canvas.translate(x, y)
        canvas.scale(scale, scale)
        pageRect.set(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat())
        canvas.clipRect(pageRect)
        preview?.let { canvas.drawBitmap(it.bitmap, null, pageRect, paint) }
        // Draw older levels first so current detail covers them without holes.
        val level = wanted.firstOrNull()?.level
        for ((key, image) in tiles) if (key.level != level && visible(image.bounds, scale)) {
            canvas.drawBitmap(image.bitmap, null, image.bounds, paint)
        }
        for ((key, image) in tiles) if (key.level == level && visible(image.bounds, scale)) {
            canvas.drawBitmap(image.bitmap, null, image.bounds, paint)
        }
        documentEdits?.let { PdfEditRenderer.drawPageEdits(canvas, it, page) }
        canvas.restore()
        val border = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(150, 255, 255, 255); style = Paint.Style.STROKE; strokeWidth = 1f }
        canvas.drawRect(x, y, x + pageWidth * scale, y + pageHeight * scale, border)
    }

    private fun visible(bounds: RectF, scale: Float) =
        bounds.right * scale + x > 0 && bounds.left * scale + x < width &&
            bounds.bottom * scale + y > 0 && bounds.top * scale + y < height

    override fun onTouchEvent(event: MotionEvent): Boolean {
        parent?.requestDisallowInterceptTouchEvent(true)
        scaleDetector.onTouchEvent(event)
        if (editMode && pageWidth > 0) {
            val point = pagePoint(event.x, event.y)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (event.pointerCount == 1) {
                        when (activeTool) {
                            PdfTool.INK, PdfTool.HIGHLIGHT -> { strokePoints.clear(); strokePoints.add(point); return true }
                            PdfTool.MOVE -> {
                                val found = documentEdits?.texts(page)?.lastOrNull { it.bounds.contains(point.x, point.y) }
                                if (found != null) { movingEdit = found; moveOriginal = RectF(found.bounds); moveOffsetX = point.x - found.bounds.left; moveOffsetY = point.y - found.bounds.top; return true }
                                onEditTap?.invoke(activeTool, point.x, point.y); return true
                            }
                            PdfTool.ADD_TEXT -> { onEditTap?.invoke(activeTool, point.x, point.y); return true }
                            PdfTool.REPLACE_TEXT -> {
                                val bitmap = preview?.bitmap
                                val bounds = bitmap?.let { VisualLineDetector.detect(it, pageWidth, pageHeight, point.x, point.y)?.bounds }
                                if (bounds != null) onEditText?.invoke(bounds, previewBackground(bitmap, point.x, point.y), onExistingText(""))
                                else onEditTap?.invoke(activeTool, point.x, point.y)
                                return true
                            }
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount > 1) { movingEdit = null; strokePoints.clear(); return true }
                    movingEdit?.let { edit -> edit.bounds.offsetTo(point.x - moveOffsetX, point.y - moveOffsetY); invalidate(); return true }
                    if (strokePoints.isNotEmpty()) { strokePoints.add(point); invalidate(); return true }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    movingEdit?.let { edit -> moveOriginal?.let { documentEdits?.recordMove(edit, it) } }
                    movingEdit = null; moveOriginal = null
                    if (strokePoints.size > 1) {
                        documentEdits?.add(PdfStrokeEdit(page, strokePoints.toList(), if (activeTool == PdfTool.HIGHLIGHT) Color.YELLOW else Color.RED, if (activeTool == PdfTool.HIGHLIGHT) 14f else 2.5f, activeTool == PdfTool.HIGHLIGHT))
                        strokePoints.clear(); invalidate(); return true
                    }
                }
            }
        }
        gestures.onTouchEvent(event)
        if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) scheduleRender()
        return true
    }

    private fun previewBackground(bitmap: Bitmap, px: Float, py: Float): Int {
        val x = (px * bitmap.width / pageWidth).toInt().coerceIn(0, bitmap.width - 1)
        val y = (py * bitmap.height / pageHeight).toInt().coerceIn(0, bitmap.height - 1)
        return bitmap.getPixel(x, y)
    }

    override fun performClick(): Boolean { super.performClick(); return true }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            x = scroller.currX.toFloat()
            y = scroller.currY.toFloat()
            constrain()
            scheduleRender()
            postInvalidateOnAnimation()
        }
    }

    private fun releaseFrame() {
        preview?.bitmap?.recycle()
        preview = null
        tiles.values.forEach { it.bitmap.recycle() }
        tiles.clear()
        tileBytes = 0
        wanted = emptySet()
    }
    override fun onDetachedFromWindow() {
        removeCallbacks(render)
        renderPosted = false
        scroller.forceFinished(true)
        releaseFrame()
        super.onDetachedFromWindow()
    }
}

````

### `app/src/main/java/com/example/lectorsimple/ReaderSafety.kt`

````kotlin
package com.example.lectorsimple

import java.io.InputStream

enum class DocumentKind { PDF, DOCX, DOC }
class ReadingException(message: String) : Exception(message)

internal object DocumentSignature {
    fun identify(header: ByteArray): DocumentKind {
        val ole = byteArrayOf(0xd0.toByte(), 0xcf.toByte(), 0x11, 0xe0.toByte(), 0xa1.toByte(), 0xb1.toByte(), 0x1a, 0xe1.toByte())
        if (header.size >= 8 && header.copyOfRange(0, 8).contentEquals(ole)) return DocumentKind.DOC
        if (header.size >= 4 && header[0] == 0x50.toByte() && header[1] == 0x4b.toByte() &&
            header[2] == 3.toByte() && header[3] == 4.toByte()) return DocumentKind.DOCX
        if (header.toString(Charsets.ISO_8859_1).contains("%PDF-")) return DocumentKind.PDF
        throw ReadingException("Elige un PDF o un archivo Word .docx o .doc.")
    }
}

internal class LimitedInputStream(private val input: InputStream, private val limit: Long) : InputStream() {
    private var consumed = 0L
    private fun count(amount: Int): Int {
        if (amount > 0) {
            consumed += amount
            if (consumed > limit) throw ReadingException("El contenido Word es demasiado grande.")
        }
        return amount
    }
    override fun read(): Int {
        val value = input.read()
        if (value >= 0) count(1)
        return value
    }
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int = count(input.read(buffer, offset, length))
    override fun close() = input.close()
}

internal object WordText {
    fun clean(text: String): String = text.replace("\r\n", "\n")
        .replace('\r', '\n').replace('\u0007', '\t').replace('\u000b', '\n')
        .replace('\u000c', '\n').filter { it >= ' ' || it == '\n' || it == '\t' }.trim()

    /** Prefer paragraph boundaries and keep UTF-16 surrogate pairs together. */
    fun splitBlocks(text: String): List<String> {
        if (text.isEmpty()) return listOf("")
        val result = ArrayList<String>()
        var start = 0
        while (start < text.length) {
            var end = (start + 3000).coerceAtMost(text.length)
            if (end < text.length) {
                val paragraph = text.lastIndexOf('\n', end - 1)
                if (paragraph > start + 1500) end = paragraph + 1
                if (end > start && text[end - 1].isHighSurrogate()) end--
            }
            result.add(text.substring(start, end))
            start = end
        }
        return result
    }
}

````

### `app/src/main/java/com/example/lectorsimple/ReaderWorker.kt`

````kotlin
package com.example.lectorsimple

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/** Shared across rotations so two activities never extract/render documents concurrently. */
internal object ReaderWorker {
    val executor = ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS, LinkedBlockingQueue()) {
        task -> Thread(task, "document-reader")
    }.apply { allowCoreThreadTimeOut(true) }
}

````

### `app/src/main/java/com/example/lectorsimple/WordZoomView.kt`

````kotlin
package com.example.lectorsimple

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView

/** Recycles small text blocks instead of laying out an entire Word document at once. */
class WordZoomView(context: Context, text: String, state: Bundle?) : ListView(context) {
    private val blocks = WordText.splitBlocks(text)
    private var zoom = state?.getFloat("zoom", 1f)?.coerceIn(0.75f, 3f) ?: 1f
    private var anchor = 0
    private var anchorOffset = 0
    private var startingZoom = 1f
    private var restorePosition = state
    private val textAdapter = object : BaseAdapter() {
        override fun getCount() = blocks.size
        override fun getItem(position: Int) = blocks[position]
        override fun getItemId(position: Int) = position.toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val row = convertView as? TextView ?: TextView(context).apply {
                setTextColor(Color.rgb(35, 39, 45))
                setLineSpacing(0f, 1.2f)
                val padding = (16 * resources.displayMetrics.density).toInt()
                setPadding(padding, padding / 2, padding, padding / 2)
            }
            row.text = blocks[position]
            row.textSize = 18f * zoom
            return row
        }
    }
    private val detector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            anchor = firstVisiblePosition
            anchorOffset = getChildAt(0)?.top ?: 0
            startingZoom = zoom
            return true
        }
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val next = (zoom * detector.scaleFactor).coerceIn(0.75f, 3f)
            if (next != zoom) {
                zoom = next
                textAdapter.notifyDataSetChanged()
                setSelectionFromTop(anchor, (anchorOffset * zoom / startingZoom).toInt())
            }
            return true
        }
    })

    init {
        setBackgroundColor(Color.WHITE)
        divider = null
        dividerHeight = 0
        adapter = textAdapter
        state?.let { setSelection(it.getInt("block", 0).coerceIn(blocks.indices)) }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        detector.onTouchEvent(event)
        if (event.pointerCount > 1 || detector.isInProgress) {
            val cancel = MotionEvent.obtain(event).apply { action = MotionEvent.ACTION_CANCEL }
            super.dispatchTouchEvent(cancel)
            cancel.recycle()
            return true
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val state = restorePosition ?: return
        val first = getChildAt(0) ?: return
        restorePosition = null
        setSelectionFromTop(state.getInt("block", 0).coerceIn(blocks.indices), (state.getFloat("offset", 0f) * first.height).toInt())
    }

    fun savePosition(): Bundle = restorePosition ?: Bundle().apply {
        putFloat("zoom", zoom)
        putInt("block", firstVisiblePosition)
        val first = getChildAt(0)
        putFloat("offset", (first?.top ?: 0).toFloat() / (first?.height ?: 1).coerceAtLeast(1))
    }

}

````

### `app/src/main/java/org/libreoffice/kit/DirectBufferAllocator.java`

````java
/* -*- Mode: Java; c-basic-offset: 4; tab-width: 20; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.libreoffice.kit;

import java.nio.ByteBuffer;

/**
 * This is the common code for allocation and freeing of memory. For this direct ByteBuffer is used but
 * in the past it was possible to use a JNI version of allocation because of a bug in old Android version.
 */
public final class DirectBufferAllocator {

    private static final String LOGTAG = DirectBufferAllocator.class.getSimpleName();

    private DirectBufferAllocator() {
    }

    public static ByteBuffer allocate(int size) {
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(size);
        if (directBuffer == null) {
            if (size <= 0) {
                throw new IllegalArgumentException("Invalid allocation size: " + size);
            } else {
                throw new OutOfMemoryError("allocateDirectBuffer() returned null");
            }
        } else if (!directBuffer.isDirect()) {
            throw new AssertionError("allocateDirectBuffer() did not return a direct buffer");
        }

        return directBuffer;
    }

    public static ByteBuffer free(ByteBuffer buffer) {
        if (buffer == null) {
            return null;
        }

        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("ByteBuffer must be direct");
        }
        // can't free buffer - leave this to the VM
        return null;
    }

    public static ByteBuffer guardedAllocate(int size) {
        ByteBuffer buffer = null;
        try {
            buffer = allocate(size);
        } catch (OutOfMemoryError oomException) {
            return null;
        }
        return buffer;
    }
}

````

### `app/src/main/java/org/libreoffice/kit/Document.java`

````java
/* -*- Mode: Java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*- */
/*
 * This file is part of the LibreOffice project.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.libreoffice.kit;

import java.nio.ByteBuffer;

public class Document {
    public static final int PART_MODE_SLIDE = 0;
    public static final int PART_MODE_NOTES = 1;

    /**
     * Document types
     */
    public static final int DOCTYPE_TEXT = 0;
    public static final int DOCTYPE_SPREADSHEET = 1;
    public static final int DOCTYPE_PRESENTATION = 2;
    public static final int DOCTYPE_DRAWING = 3;
    public static final int DOCTYPE_OTHER = 4;

    /**
     * Mouse event types
     */
    public static final int MOUSE_EVENT_BUTTON_DOWN = 0;
    public static final int MOUSE_EVENT_BUTTON_UP = 1;
    public static final int MOUSE_EVENT_MOVE = 2;

    /**
     * Key event types
     */
    public static final int KEY_EVENT_PRESS = 0;
    public static final int KEY_EVENT_RELEASE = 1;

    /**
     * State change types
     */
    public static final int BOLD = 0;
    public static final int ITALIC = 1;
    public static final int UNDERLINE = 2;
    public static final int STRIKETHROUGH = 3;
    public static final int ALIGN_LEFT = 4;
    public static final int ALIGN_CENTER = 5;
    public static final int ALIGN_RIGHT = 6;
    public static final int ALIGN_BLOCK = 7;
    public static final int NUMBERED_LIST = 8;
    public static final int BULLET_LIST= 9;

    /**
     * Callback message types
     * Refer to https://opengrok.libreoffice.org/xref/core/include/LibreOfficeKit/LibreOfficeKitEnums.h
     * for more details about each callback.
     */
    public static final int CALLBACK_INVALIDATE_TILES = 0;
    public static final int CALLBACK_INVALIDATE_VISIBLE_CURSOR = 1;
    public static final int CALLBACK_TEXT_SELECTION = 2;
    public static final int CALLBACK_TEXT_SELECTION_START = 3;
    public static final int CALLBACK_TEXT_SELECTION_END = 4;
    public static final int CALLBACK_CURSOR_VISIBLE = 5;
    public static final int CALLBACK_GRAPHIC_SELECTION = 6;
    public static final int CALLBACK_HYPERLINK_CLICKED = 7;
    public static final int CALLBACK_STATE_CHANGED = 8;
    public static final int CALLBACK_STATUS_INDICATOR_START = 9;
    public static final int CALLBACK_STATUS_INDICATOR_SET_VALUE = 10;
    public static final int CALLBACK_STATUS_INDICATOR_FINISH = 11;
    public static final int CALLBACK_SEARCH_NOT_FOUND = 12;
    public static final int CALLBACK_DOCUMENT_SIZE_CHANGED = 13;
    public static final int CALLBACK_SET_PART = 14;
    public static final int CALLBACK_SEARCH_RESULT_SELECTION = 15;
    public static final int CALLBACK_UNO_COMMAND_RESULT = 16;
    public static final int CALLBACK_CELL_CURSOR = 17;
    public static final int CALLBACK_MOUSE_POINTER = 18;
    public static final int CALLBACK_CELL_FORMULA = 19;
    public static final int CALLBACK_DOCUMENT_PASSWORD = 20;
    public static final int CALLBACK_DOCUMENT_PASSWORD_TO_MODIFY = 21;
    public static final int CALLBACK_ERROR = 22;
    public static final int CALLBACK_CONTEXT_MENU = 23;
    public static final int CALLBACK_INVALIDATE_VIEW_CURSOR = 24;
    public static final int CALLBACK_TEXT_VIEW_SELECTION = 25;
    public static final int CALLBACK_CELL_VIEW_CURSOR = 26;
    public static final int CALLBACK_GRAPHIC_VIEW_SELECTION = 27;
    public static final int CALLBACK_VIEW_CURSOR_VISIBLE = 28;
    public static final int CALLBACK_VIEW_LOCK = 29;
    public static final int CALLBACK_REDLINE_TABLE_SIZE_CHANGED = 30;
    public static final int CALLBACK_REDLINE_TABLE_ENTRY_MODIFIED = 31;
    public static final int CALLBACK_COMMENT = 32;
    public static final int CALLBACK_INVALIDATE_HEADER = 33;
    public static final int CALLBACK_CELL_ADDRESS = 34;
    public static final int CALLBACK_SC_FOLLOW_JUMP = 54;

    /**
     * Set text selection types
     */
    public static final int SET_TEXT_SELECTION_START = 0;
    public static final int SET_TEXT_SELECTION_END = 1;
    public static final int SET_TEXT_SELECTION_RESET = 2;

    /**
     * Set graphic selection types
     */
    public static final int SET_GRAPHIC_SELECTION_START = 0;
    public static final int SET_GRAPHIC_SELECTION_END = 1;

    /**
     * Mouse button type
     */
    public static final int MOUSE_BUTTON_LEFT = 1;
    public static final int MOUSE_BUTTON_MIDDLE = 2;
    public static final int MOUSE_BUTTON_RIGHT = 4;

    public static final int KEYBOARD_MODIFIER_NONE = 0x0000;
    public static final int KEYBOARD_MODIFIER_SHIFT = 0x1000;
    public static final int KEYBOARD_MODIFIER_MOD1 = 0x2000;
    public static final int KEYBOARD_MODIFIER_MOD2 = 0x4000;
    public static final int KEYBOARD_MODIFIER_MOD3 = 0x8000;

    /** Optional features of LibreOfficeKit, in particular callbacks that block
     *  LibreOfficeKit until the corresponding reply is received, which would
     *  deadlock if the client does not support the feature.
     */
    public static final long LOK_FEATURE_DOCUMENT_PASSWORD = 1;
    public static final long LOK_FEATURE_DOCUMENT_PASSWORD_TO_MODIFY = (1 << 1);
    public static final long LOK_FEATURE_PART_IN_INVALIDATION_CALLBACK = (1 << 2);
    public static final long LOK_FEATURE_NO_TILED_ANNOTATIONS = (1 << 3);

    private final ByteBuffer handle;
    private MessageCallback messageCallback = null;

    public Document(ByteBuffer handle) {
        this.handle = handle;
        bindMessageCallback();
    }

    public void setMessageCallback(MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    /**
     * Callback triggered through JNI to indicate that a new signal
     * from LibreOfficeKit was retrieved.
     */
    private void messageRetrieved(int signalNumber, String payload) {
        if (messageCallback != null) {
            messageCallback.messageRetrieved(signalNumber, payload);
        }
    }

    /**
     * Bind the signal callback in LOK.
     */
    private native void bindMessageCallback();

    public native void destroy();

    public native int getPart();

    public native void setPart(int partIndex);

    public native int getParts();

    public native String getPartName(int partIndex);

    public native void setPartMode(int partMode);

    public native String getPartPageRectangles();

    public native long getDocumentHeight();

    public native long getDocumentWidth();

    private native int getDocumentTypeNative();

    public native void setClientZoom(int nTilePixelWidth, int nTilePixelHeight, int nTileTwipWidth, int nTileTwipHeight);

    public native void saveAs(String url, String format, String options);

    private native void paintTileNative(ByteBuffer buffer, int canvasWidth, int canvasHeight, int tilePositionX, int tilePositionY, int tileWidth, int tileHeight);

    public int getDocumentType() {
        return getDocumentTypeNative();
    }

    public void paintTile(ByteBuffer buffer, int canvasWidth, int canvasHeight, int tilePositionX, int tilePositionY, int tileWidth, int tileHeight) {
        paintTileNative(buffer, canvasWidth, canvasHeight, tilePositionX, tilePositionY, tileWidth, tileHeight);
    }

    public native void initializeForRendering();

    /**
     * Post a key event to LibreOffice.
     * @param type - type of key event
     * @param charCode - the Unicode character generated by this event or 0.
     * @param keyCode - the integer code representing the key of the event (non-zero for control keys).
     */
    public native void postKeyEvent(int type, int charCode, int keyCode);

    /**
     * Post a mouse event to LOK
     * @param type - mouse event type
     * @param x - x coordinate
     * @param y - y coordinate
     * @param count - number of events
     */
    public native void postMouseEvent(int type, int x, int y, int count, int button, int modifier);

    /**
     * Post a .uno: command to LOK
     * @param command - the command, like ".uno:Bold"
     * @param arguments
     */
    public native void postUnoCommand(String command, String arguments, boolean notifyWhenFinished);

    /**
     * Change text selection.
     * @param type - text selection type
     * @param x - x coordinate
     * @param y - y coordinate
     */
    public native void setTextSelection(int type, int x, int y);

    /**
     * Change graphic selection.
     * @param type - graphic selection type
     * @param x - x coordinate
     * @param y - y coordinate
     */
    public native void setGraphicSelection(int type, int x, int y);

    /**
     * Get selected text
     * @param mimeType
     * @return
     */
    public native String getTextSelection(String mimeType);

    /**
     * paste
     * @param mimeType
     * @param data
     * @return
     */
    public native boolean paste(String mimeType, String data);

    /**
     * Reset current (any kind of) selection.
     */
    public native void resetSelection();

    public native String getCommandValues(String command);

    /**
     * Callback to retrieve messages from LOK
     */
    public interface MessageCallback {
        /**
         * Invoked when a message is retrieved from LOK
         * @param signalNumber - signal type / number
         * @param payload - retrieved for the signal
         */
        void messageRetrieved(int signalNumber, String payload);
    }
}

````

### `app/src/main/java/org/libreoffice/kit/LibreOfficeKit.java`

````java
/* -*- Mode: Java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*- */
/*
 * This file is part of the LibreOffice project.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.libreoffice.kit;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.util.Log;

import java.nio.ByteBuffer;

// Native methods in this class are all implemented in
// sal/android/lo-bootstrap.c as the lo-bootstrap library is loaded with
// System.loadLibrary() and Android's JNI works only to such libraries, it
// seems.
public final class LibreOfficeKit
{
    private static String LOGTAG = LibreOfficeKit.class.getSimpleName();
    private static AssetManager mgr;

    // private constructor because instantiating would be meaningless
    private LibreOfficeKit() {
    }

    // Trigger library initialization - as this is done automatically by executing the "static" block, this method remains empty. However we need this to manually (at the right time) can force library initialization.
    public static void initializeLibrary() {
    }

    // Trigger initialization on the JNI - LOKit side.
    private static native boolean initializeNative(String dataDir, String cacheDir, String apkFile, AssetManager mgr);

    public static native ByteBuffer getLibreOfficeKitHandle();

    // Wrapper for putenv()
    public static native void putenv(String string);

    // A method that starts a thread to redirect stdout and stderr writes to
    // the Android logging mechanism, or stops the redirection.
    public static native void redirectStdio(boolean state);

    static boolean initializeDone = false;

    // This init() method should be called from the upper Java level of
    // LO-based apps.
    public static synchronized void init(Activity activity)
    {
        if (initializeDone) {
            return;
        }

        mgr = activity.getResources().getAssets();

        ApplicationInfo applicationInfo = activity.getApplicationInfo();
        String dataDir = applicationInfo.dataDir;
        Log.i(LOGTAG, String.format("Initializing LibreOfficeKit, dataDir=%s\n", dataDir));

        redirectStdio(true);

        String cacheDir = activity.getApplication().getCacheDir().getAbsolutePath();
        String apkFile = activity.getApplication().getPackageResourcePath();

        if (!initializeNative(dataDir, cacheDir, apkFile, mgr)) {
            Log.e(LOGTAG, "Initialize native failed!");
            // FreeDocs modification: report failure instead of silently marking the engine ready.
            throw new IllegalStateException("LibreOfficeKit native initialization failed");
        }
        initializeDone = true;
    }

    // Now with static loading we always have all native code in one native
    // library which we always call liblo-native-code.so, regardless of the
    // app. The library has already been unpacked into /data/data/<app
    // name>/lib at installation time by the package manager.
    static {
        NativeLibLoader.load();
    }
}

class NativeLibLoader {
        private static boolean done = false;

        protected static synchronized void load() {
            if (done)
                return;
            System.loadLibrary("nspr4");
            System.loadLibrary("plds4");
            System.loadLibrary("plc4");
            System.loadLibrary("nssutil3");
            System.loadLibrary("freebl3");
            System.loadLibrary("sqlite3");
            System.loadLibrary("softokn3");
            System.loadLibrary("nss3");
            System.loadLibrary("nssckbi");
            System.loadLibrary("nssdbm3");
            System.loadLibrary("smime3");
            System.loadLibrary("ssl3");

            System.loadLibrary("c++_shared");
            System.loadLibrary("lo-native-code");
            done = true;
        }
}

````

### `app/src/main/java/org/libreoffice/kit/Office.java`

````java
/* -*- Mode: Java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*- */
/*
 * This file is part of the LibreOffice project.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.libreoffice.kit;

import java.nio.ByteBuffer;

public class Office {
    private ByteBuffer handle;
    private MessageCallback messageCallback = null;

    public Office(ByteBuffer handle) {
        this.handle = handle;
        bindMessageCallback();
    }

    /**
     * Bind the signal callback in LOK.
     */
    private native void bindMessageCallback();

    public native String getError();

    private native ByteBuffer documentLoadNative(String url);

    public Document documentLoad(String url) {
        ByteBuffer documentHandle = documentLoadNative(url);
        Document document = null;
        if (documentHandle != null) {
            document = new Document(documentHandle);
        }
        return document;
    }

    public native void destroy();
    public native void destroyAndExit();
    public native void setDocumentPassword(String url, String pwd);
    public native void setOptionalFeatures(long options);

    public void setMessageCallback(MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    /**
     * Callback triggered through JNI to indicate that a new signal
     * from LibreOfficeKit was retrieved.
     */
    private void messageRetrievedLOKit(int signalNumber, String payload) {
        if (messageCallback != null) {
            messageCallback.messageRetrieved(signalNumber, payload);
        }

    }

    /**
     * Callback to retrieve messages from LOK
     */
    public interface MessageCallback {
        /**
         * Invoked when a message is retrieved from LOK
         * @param signalNumber - signal type / number
         * @param payload - retrieved for the signal
         */
        void messageRetrieved(int signalNumber, String payload);
    }
}

````

## 4. Layouts y recursos web
