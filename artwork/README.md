# Logo FreeDocs

`freedocs-logo-transparent.png` es la imagen PNG con transparencia proporcionada por el usuario. Se usa como fuente para el icono de la aplicación y la pantalla de inicio.

`tools/prepare-icons.ps1` centra y redimensiona la imagen para generar los recursos Android por densidad, la capa del icono adaptativo y el logo de inicio. Conserva la transparencia del archivo original.

La imagen maestra no se empaqueta en el APK; solo se incluyen sus versiones reducidas dentro de `app/src/main/res`.
