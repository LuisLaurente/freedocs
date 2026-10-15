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
