# Music
#### Capturas de pantalla de la aplicación

|    |    |    |
| --- | --- | --- |
|   <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/main.png?raw=true" width="200" align="center">  | <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/detail.png?raw=true" width="200"  align="center">     |   <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/detail_lrc.png?raw=true" width="200"  align="center">   |

|                                                                                                                    |                                                                                                                    |                                                                                                                   |
| ------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/1.gif?raw=true" width="200"  align="center"> | <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/2.gif?raw=true" width="200"  align="center"> | <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/3.gif?raw=true" width="200" align="center"> |
| <img src="https://github.com/985211yygg/Music2/blob/master/screenshots/4.gif?raw=true" width="200" align="center">                                                                                                                    |                                                                                                                     |                                                                                     |

#### Introducción de la aplicación
Music es un reproductor de música que imita a QQ Music. La aplicación controla la reproducción de música en un proceso de servicio independiente. Actualmente, implementa las siguientes funciones:
- Control rápido de la reproducción de música a través de auriculares y la barra de notificaciones;

- Reproducción de música deslizando la barra de control inferior;

- El botón de reproducción de la barra de control inferior muestra el progreso de la reproducción;

- Visualización dinámica de letras, visualización de letras en una sola línea, ajuste del tamaño de fuente y color de las letras;

- Escaneo de canciones locales y canciones en la tarjeta SD, entre otras funciones.

Algunas funciones aún no están completas y existen algunos errores conocidos o desconocidos que se están corrigiendo y mejorando gradualmente.
**Se está intentando utilizar la herramienta de extracción de NetEase Cloud Music [Spider163](https://github.com/Chengyumeng/spider163), combinada con MySQL para obtener más información de las canciones, permitiendo la reproducción en línea, extracción de letras, listas de reproducción, rankings de popularidad, etc.**

#### Librerías utilizadas y Vistas personalizadas de código abierto
- linelrcview: vista personalizada para la visualización de letras en una sola línea (el plan original era mostrar el progreso de la canción palabra por palabra, pero aún no se ha implementado debido a limitaciones técnicas personales)

- lrcview: espacio personalizado para la visualización de letras, admite visualización dinámica, deslizamiento y reproducción rápida mediante deslizamiento

- QQPlayButton: botón de reproducción personalizado al estilo de QQ Music, alterna entre los estados de reproducción y pausa, y muestra el progreso de la reproducción

- MediaSeekBar: Seekbar personalizado sincronizado con el progreso de la reproducción, admite arrastre para adelantar y retroceder rápido

- CircleImageView: ImageView circular personalizado para mostrar la imagen del álbum, implementa un efecto de disco basado en el estado y el progreso de la reproducción

- Componentes de arquitectura de Android: Room, LiveData, ViewModel, Lifecycle
- RxJava

- Okhttp y Retrofit para solicitudes de red

- EventBus

- Butterknife

- Glide: librería de carga de imágenes

- EasyBlur: desenfoque gaussiano de imágenes

- MZBannerView: librería de carrusel de imágenes

- wavesidebar: librería de barra lateral de índice
