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

## Bloqueo observado

- El proyecto y la caché no contienen la biblioteca nativa de LibreOfficeKit.
- La descarga directa de una fuente desde GitHub falló con `No es posible conectar con el servidor remoto`.
- `wsl.exe --list --quiet` respondió `Wsl/EnumerateDistros/Service/E_ACCESSDENIED`.
- Hay Android NDK instalado, pero eso no proporciona el núcleo LibreOffice ni sus recursos.

No se añadió una dependencia ausente que rompa la compilación, un motor comercial ni una conversión parcial presentada como reproducción fiel de las hojas.
