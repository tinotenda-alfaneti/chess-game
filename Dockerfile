# Multi-stage build for Java Chess Game with noVNC web access
# Stage 1: Build the Java application
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# Download Guava dependency
RUN mkdir -p lib && \
    curl -L -o lib/guava-31.1-jre.jar \
    https://repo1.maven.org/maven2/com/google/guava/guava/31.1-jre/guava-31.1-jre.jar

# Copy source code (exclude tests)
COPY src /build/src
COPY art /build/art

# Compile Java application (exclude test files)
RUN javac -cp "lib/*" -d out \
    $(find src/com -name "*.java")

# Create JAR with manifest
RUN echo "Main-Class: com.chess.jChess" > manifest.txt && \
    echo "Class-Path: lib/guava-31.1-jre.jar" >> manifest.txt && \
    jar cfm chess-game.jar manifest.txt -C out . && \
    mkdir -p /app/lib && \
    cp chess-game.jar /app/ && \
    cp lib/guava-31.1-jre.jar /app/lib/ && \
    cp -r art /app/

# Stage 2: Runtime with VNC and noVNC for web access
FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive \
    DISPLAY=:99 \
    VNC_PORT=5900 \
    NOVNC_PORT=6080 \
    VNC_RESOLUTION=1280x720

# Install dependencies
RUN apt-get update && apt-get install -y \
    openjdk-17-jre \
    x11vnc \
    xvfb \
    fluxbox \
    wget \
    net-tools \
    novnc \
    websockify \
    supervisor \
    xdotool \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Copy application from builder
COPY --from=builder /app /app

# Create chess game launcher script
RUN echo '#!/bin/bash\n\
export DISPLAY=:99\n\
cd /app\n\
exec java -cp "chess-game.jar:lib/*" com.chess.jChess\n\
' > /app/launch-chess.sh && chmod +x /app/launch-chess.sh

# Create Fluxbox menu with chess game launcher
RUN mkdir -p /root/.fluxbox && \
    echo '[begin] (Menu)\n\
  [exec] (Chess Game) {/app/launch-chess.sh}\n\
  [exec] (Terminal) {xterm}\n\
  [separator]\n\
  [restart] (Restart Fluxbox)\n\
[end]\n\
' > /root/.fluxbox/menu

# Create desktop shortcut
RUN mkdir -p /root/Desktop && \
    echo '#!/bin/bash\n\
/app/launch-chess.sh &\n\
' > /root/Desktop/chess-game.sh && \
    chmod +x /root/Desktop/chess-game.sh

# Create startup script
RUN echo '#!/bin/bash\n\
# Start virtual display\n\
Xvfb :99 -screen 0 ${VNC_RESOLUTION}x24 &\n\
sleep 2\n\
\n\
# Start window manager\n\
fluxbox &\n\
sleep 1\n\
\n\
# Start VNC server\n\
x11vnc -display :99 -nopw -listen 0.0.0.0 -xkb -forever -shared &\n\
sleep 1\n\
\n\
# Start noVNC web interface\n\
websockify --web=/usr/share/novnc/ ${NOVNC_PORT} localhost:${VNC_PORT} &\n\
sleep 2\n\
\n\
# Auto-restart chess game if closed\n\
export DISPLAY=:99\n\
while true; do\n\
  echo "Starting chess game..."\n\
  cd /app\n\
  java -cp "chess-game.jar:lib/*" com.chess.jChess\n\
  echo "Chess game closed. Restarting in 3 seconds..."\n\
  sleep 3\n\
done &\n\
\n\
# Wait for the window to appear and maximize it\n\
sleep 3\n\
WINDOW_ID=$(xdotool search --name "Atarist Chess" | head -1)\n\
if [ ! -z "$WINDOW_ID" ]; then\n\
  xdotool windowsize $WINDOW_ID 1200 900\n\
  xdotool windowmove $WINDOW_ID 40 40\n\
  xdotool windowactivate $WINDOW_ID\n\
  echo "Chess window maximized and positioned"\n\
else\n\
  echo "Chess window not found, it may take a moment to appear"\n\
fi\n\
\n\
# Keep container running and show logs\n\
echo "Chess game started. Access via http://localhost:6080/vnc.html"\n\
echo "If you close the game, it will automatically restart in 3 seconds"\n\
echo "You can also right-click on the desktop to access the Fluxbox menu and manually launch it"\n\
tail -f /dev/null\n\
' > /start.sh && chmod +x /start.sh

WORKDIR /app

EXPOSE 6080 5900

CMD ["/start.sh"]
