# Base image con Tomcat y JDK 11
FROM tomcat:9.0-jdk11-openjdk  

# Instalar fuentes
RUN apt-get update && apt-get install -y fonts-dejavu

# Opción 2: Descargar fuente manualmente
COPY ./arial-font/arial.ttf /usr/share/fonts/truetype/
RUN fc-cache -f -v

# Crear la carpeta para almacenar los archivos .jasper
RUN mkdir -p /app/jasper

# Cambiar los permisos de la carpeta
RUN chmod -R 777 /app/jasper

# Copia los reportes en el contenedor el path solo puede ser relativo
# COPY ./reportes /app/jasper

# Copia tu archivo WAR al directorio de webapps
COPY target/reportes.war /usr/local/tomcat/webapps/  
COPY server.xml /usr/local/tomcat/conf/

RUN echo 'CATALINA_OPTS="$CATALINA_OPTS -Djava.awt.headless=true -Dawt.toolkit=sun.awt.HeadlessToolkit -Dsun.java2d.cmm=sun.java2d.cmm.kcms.KcmsServiceProvider -Djava.library.path=/usr/lib/jni:/usr/lib/x86_64-linux-gnu/jni:/app/jasper/fonts"' >> /usr/local/tomcat/bin/setenv.sh

# Expón el puerto 8080 para acceso externo
EXPOSE 8080  

# Ejecuta Tomcat
CMD ["catalina.sh", "run"]  
