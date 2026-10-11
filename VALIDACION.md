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

---

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

