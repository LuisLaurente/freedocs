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

