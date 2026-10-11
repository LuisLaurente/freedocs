# Word por hojas, gratis y sin conexión

## Requisito

Abrir DOC y DOCX dentro de la app con tamaños de hoja, márgenes, estilos, imágenes y tablas. El zoom debe ampliar la hoja sin reorganizar el texto. No usar servicios remotos ni una biblioteca comercial en modo de evaluación.

## Motor candidato

LibreOfficeKit (LOK), el núcleo que utiliza LibreOffice Viewer para Android. La documentación del proyecto describe su biblioteca `liblo-native-code.so`, sus clases JNI y su renderizado por bloques.

Fuente: https://github.com/LibreOffice/core/blob/master/android/README.md

El candidato necesita evaluación de compatibilidad y rendimiento; todavía no se incorporó ni se verificó en esta app. No es una dependencia Maven pequeña que se pueda añadir y utilizar directamente.

## Integración prevista

1. Obtener o compilar el núcleo Android y sus recursos desde una revisión fija de LibreOffice. Usar clases JNI, biblioteca, registro de servicios y fuentes de esa misma revisión, con sus licencias y avisos correspondientes.
2. Empaquetar las arquitecturas necesarias para celulares y emuladores. Confirmar compatibilidad con el Android mínimo del proyecto y los dispositivos previstos.
3. Inicializar LOK en un trabajador dedicado; abrir la copia privada seleccionada por el usuario, sin habilitar edición ni acceso remoto.
4. Convertir localmente DOC/DOCX a un PDF temporal y abrirlo con el visor por bloques ya implementado. Mantener el archivo original intacto y eliminar el PDF al cerrar. Evaluar después el renderizado directo por bloques si la conversión completa tarda demasiado en documentos largos.
5. Retirar la ruta de Word como texto cuando la nueva ruta esté operativa. Conservar selección, restauración de página y zoom, límites de memoria y cancelación.
6. Si se autoriza la validación, comparar documentos reales con el escritorio: fuentes, tablas, imágenes, encabezados, saltos y orientación; medir apertura, memoria y gestos en un celular modesto. La sustitución de fuentes puede cambiar la paginación incluso con un motor completo.

## Estado de la Integración (Rama feature/word-sheet-libreoffice)

El motor LibreOfficeKit fue integrado con éxito a partir de fuentes y componentes oficiales de The Document Foundation / F-Droid de una misma revisión (`libreoffice-26.2.6.3`, commit `8221e31b3ac356a1623c672912a3d2b492f7e3d1`):

1. **Bibliotecas nativas:** `liblo-native-code.so`, `libc++_shared.so` y dependencias NSS/NSPR empaquetadas para `arm64-v8a` (teléfonos modernos) y `x86_64` (emulador). Compatibilidad verificada desde Android 5.0 (minSdk 21) hasta Android 15 (API 35).
2. **Recursos y fuentes:** Recursos UNO (`assets/program/`, `assets/share/`) y fuentes tipográficas métricamente compatibles (`assets/unpack/user/fonts/`: Liberation, Carlito, Caladea) integrados en el paquete.
3. **Flujo de visualización:**
   - Para **"Hoja (Diseño)"**: Convierte localmente el documento Word en un PDF temporal en `cacheDir` usando `doc.saveAs(path, "pdf", null)` y lo abre con el visor por bloques `PdfSession` y `PdfZoomView` existente, conservando paginación, márgenes, tablas, imágenes y zoom de hasta 6x.
   - El PDF temporal se elimina automáticamente al cerrar o cambiar de documento (`PdfSession.close()`).
   - El archivo original del usuario nunca se modifica.
4. **Vistas conmutables:** Se implementó el control **"Hoja | Móvil"** para Word:
   - "Hoja": vista fiel por páginas.
   - "Móvil": vista de texto ajustado a la pantalla (`WordZoomView`).
   - El cambio entre ambas vistas conserva la posición y el nivel de zoom de cada modo.
5. **Seguridad y privacidad:** Operación 100% offline, sin permisos de Internet, ejecución en hilo secundario `ReaderWorker`, con control de cancelación y respeto de los límites de memoria.
