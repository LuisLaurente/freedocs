# Licencias y Avisos de Distribución - FreeDocs

FreeDocs integra componentes de código abierto para permitir la lectura y visualización sin conexión de documentos PDF y Microsoft Word (.doc / .docx).

A continuación se detallan las licencias, procedencias y obligaciones de distribución de cada componente incluido.

---

## 1. LibreOfficeKit y Núcleo LibreOffice para Android

- **Licencia:** Mozilla Public License Versión 2.0 (MPL-2.0).
- **Entidad de origen:** The Document Foundation (TDF).
- **Procedencia y versión:**
  - Repositorio oficial: `https://git.libreoffice.org/core`
  - Tag: `libreoffice-26.2.6.3`
  - Commit completo: `8221e31b3ac356a1623c672912a3d2b492f7e3d1`
  - Canal de compilación reproducible verificado: F-Droid Build Server (receta `metadata/org.documentfoundation.libreoffice.yml`).
- **Componentes integrados:**
  - Clases JNI / Java: `org.libreoffice.kit.LibreOfficeKit`, `Office`, `Document`, `DirectBufferAllocator`.
  - Biblioteca nativa consolidada: `liblo-native-code.so` (arquitecturas `arm64-v8a` y `x86_64`).
  - Recursos y registros UNO: `assets/program/` (`services.rdb`, `bootstraprc`, etc.), `assets/share/` (`writer.xcd`, `main.xcd`, etc.), `assets/unpack/` (`offapi.rdb`, `sofficerc`, fuentes Liberation/Carlito/Caladea).
- **Obligaciones MPL-2.0:**
  - Los archivos fuente cubiertos por la MPL 2.0 y cualquier modificación que se les realice deben ponerse a disposición del público bajo los términos de la MPL 2.0.
  - Se debe conservar el aviso de derechos de autor y la referencia al código fuente disponible públicamente en `https://git.libreoffice.org/core`.
  - El texto completo de la MPL 2.0 puede consultarse en https://www.mozilla.org/MPL/2.0/.

---

## 2. Bibliotecas Criptográficas y de Seguridad (NSS / NSPR)

- **Bibliotecas:** `libnss3.so`, `libnspr4.so`, `libnssutil3.so`, `libsoftokn3.so`, `libfreebl3.so`, `libnssckbi.so`, `libnssdbm3.so`, `libsmime3.so`, `libssl3.so`, `libplc4.so`, `libplds4.so`.
- **Licencia:** Mozilla Public License Versión 2.0 (MPL-2.0).
- **Origen:** Proyecto Mozilla / NSS (empaquetado junto con LibreOffice core).

---

## 3. SQLite

- **Biblioteca:** `libsqlite3.so`
- **Licencia:** Dominio Público (Public Domain).
- **Origen:** SQLite Development Team (https://www.sqlite.org/).

---

## 4. Biblioteca Estándar C++ de Android (LLVM libc++)

- **Biblioteca:** `libc++_shared.so`
- **Licencia:** Apache License 2.0 con excepción LLVM (LLVM Exceptions).
- **Origen:** Android NDK / LLVM Project.

---

## 5. Tipografías de Sustitución Métrica (Assets)

- **Fuentes incluidas:**
  - Liberation Sans, Liberation Serif, Liberation Mono (compatibles con Arial, Times New Roman, Courier New): Licencia SIL Open Font License 1.1.
  - Carlito y Caladea (compatibles con Calibri y Cambria de MS Office): Licencia SIL Open Font License 1.1.
- **Origen:** Proyectos GNU / Red Hat / Google / The Document Foundation.

---

## 6. Apache POI Scratchpad

- **Uso:** Extracción de texto plano como fallback para la vista "Móvil" en formato .doc legacy (Word 97-2003).
- **Licencia:** Apache License, Versión 2.0.
- **Origen:** Apache Software Foundation (https://poi.apache.org/).
