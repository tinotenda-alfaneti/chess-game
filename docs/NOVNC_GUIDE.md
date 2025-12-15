# noVNC Guide for Java Desktop Applications

## What is noVNC?

**noVNC** is a browser-based VNC (Virtual Network Computing) client that allows you to access graphical desktop applications through a web browser without requiring any client-side software installation. It uses WebSockets to provide VNC connectivity directly in the browser.

### Key Features:
- **Zero Client Installation** - Users only need a modern web browser
- **Cross-Platform** - Works on Windows, Mac, Linux, mobile devices
- **HTML5 Canvas** - Renders the remote desktop using standard web technologies
- **WebSocket Protocol** - Efficient real-time bidirectional communication
- **Secure** - Can be served over HTTPS/WSS for encrypted connections

## Why Use noVNC for Java Swing Applications?

Traditional Java Swing applications are desktop GUI applications that require:
1. Java Runtime Environment (JRE) installed on the client
2. Direct access to the application JAR file
3. Local execution on the user's machine

By using noVNC, we can:
- ✅ **Deploy Java GUI apps to Kubernetes** like web applications
- ✅ **Access via web browser** - No Java installation required on client
- ✅ **Centralized execution** - App runs on server, reducing client requirements
- ✅ **Remote access** - Users can access from anywhere
- ✅ **Consistent environment** - Same Java version and dependencies for all users

## Architecture Overview

```
┌─────────────────┐
│   Web Browser   │
│  (Any Device)   │
└────────┬────────┘
         │ HTTP/WebSocket
         │ Port 6080
┌────────▼────────┐
│     noVNC       │ ◄─── Browser-based VNC client
│  (WebSockify)   │      Converts WebSocket ↔ VNC
└────────┬────────┘
         │ VNC Protocol
         │ Port 5900
┌────────▼────────┐
│     x11vnc      │ ◄─── VNC server
│  (VNC Server)   │      Shares X display
└────────┬────────┘
         │
┌────────▼────────┐
│      Xvfb       │ ◄─── Virtual X server
│ (Virtual Frame  │      Headless X11 display
│     Buffer)     │
└────────┬────────┘
         │
┌────────▼────────┐
│  Java Swing App │ ◄─── Your chess game
│  (GUI Process)  │      Runs normally
└─────────────────┘
```

## How Our Implementation Works

### 1. Virtual Display (Xvfb)
```bash
Xvfb :99 -screen 0 1280x720x24 &
```
- **Xvfb** = X Virtual Framebuffer
- Creates a virtual display `:99` with resolution 1280x720, 24-bit color
- Runs in memory without physical monitor
- Allows GUI apps to run in headless environments (like containers)

### 2. Window Manager (Fluxbox)
```bash
fluxbox &
```
- Lightweight X11 window manager
- Handles window decorations, focus, resizing
- Essential for proper Java Swing window behavior
- Low resource usage compared to full desktop environments

### 3. VNC Server (x11vnc)
```bash
x11vnc -display :99 -nopw -listen 0.0.0.0 -xkb -forever -shared &
```
- **-display :99** - Connect to virtual display
- **-nopw** - No password (fine for containerized deployment)
- **-listen 0.0.0.0** - Accept connections from any IP
- **-xkb** - Enable keyboard extensions
- **-forever** - Keep running after client disconnects
- **-shared** - Allow multiple simultaneous connections

### 4. WebSocket Proxy (websockify)
```bash
websockify --web=/usr/share/novnc/ 6080 localhost:5900 &
```
- Converts WebSocket connections (port 6080) to VNC protocol (port 5900)
- Serves noVNC HTML/JavaScript files from `/usr/share/novnc/`
- Acts as a bridge between browser and VNC server

### 5. Java Application
```bash
export DISPLAY=:99
java -cp "chess-game.jar:lib/*" com.chess.jChess &
```
- Set `DISPLAY` environment variable to point to virtual display
- Run Java Swing application normally
- App renders to Xvfb, which is captured by x11vnc

### 6. Window Automation (xdotool)
```bash
xdotool search --name "Atarist Chess" | head -1
xdotool windowsize $WINDOW_ID 1200 900
xdotool windowmove $WINDOW_ID 40 40
xdotool windowactivate $WINDOW_ID
```
- Automatically find the Java window
- Resize to optimal viewing size
- Position prominently on screen
- Bring to foreground

## Setting Up noVNC for Your Own Java Application

### Step 1: Create Dockerfile

```dockerfile
FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive \
    DISPLAY=:99 \
    VNC_RESOLUTION=1280x720

# Install dependencies
RUN apt-get update && apt-get install -y \
    openjdk-17-jre \
    x11vnc \
    xvfb \
    fluxbox \
    novnc \
    websockify \
    xdotool \
    && apt-get clean

# Copy your Java application
COPY your-app.jar /app/
COPY lib /app/lib

# Startup script
RUN echo '#!/bin/bash\n\
Xvfb :99 -screen 0 ${VNC_RESOLUTION}x24 &\n\
sleep 2\n\
fluxbox &\n\
sleep 1\n\
x11vnc -display :99 -nopw -listen 0.0.0.0 -xkb -forever -shared &\n\
sleep 1\n\
websockify --web=/usr/share/novnc/ 6080 localhost:5900 &\n\
sleep 2\n\
export DISPLAY=:99\n\
cd /app\n\
java -jar your-app.jar\n\
' > /start.sh && chmod +x /start.sh

EXPOSE 6080 5900

CMD ["/start.sh"]
```

### Step 2: Build and Run

```bash
# Build image
docker build -t my-java-gui-app .

# Run container
docker run -d -p 6080:6080 -p 5900:5900 my-java-gui-app

# Access via browser
open http://localhost:6080/vnc.html
```

### Step 3: Click "Connect" in noVNC Interface

The noVNC web interface will appear with a "Connect" button. Click it to access your Java GUI application.

## Configuration Options

### Display Resolution
Adjust `VNC_RESOLUTION` environment variable:
```dockerfile
ENV VNC_RESOLUTION=1920x1080  # For larger displays
ENV VNC_RESOLUTION=1024x768   # For smaller displays
```

### VNC Password Protection
Add password to x11vnc:
```bash
x11vnc -display :99 -passwd mypassword -listen 0.0.0.0 -xkb -forever -shared &
```

### Multiple Users
The `-shared` flag allows multiple users to connect simultaneously. Remove it for exclusive access:
```bash
x11vnc -display :99 -nopw -listen 0.0.0.0 -xkb -forever &
```

### Performance Tuning
```bash
# Faster screen updates
x11vnc -display :99 -noxdamage -nopw -listen 0.0.0.0

# Lower quality for better performance
x11vnc -display :99 -scale 0.75 -quality 5 -nopw
```

## Kubernetes Deployment

### Service Configuration
```yaml
apiVersion: v1
kind: Service
metadata:
  name: java-gui-app
spec:
  ports:
    - name: novnc
      port: 80
      targetPort: 6080    # noVNC web interface
    - name: vnc
      port: 5900
      targetPort: 5900    # Direct VNC (optional)
```

### Ingress Configuration
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: java-gui-app
  annotations:
    nginx.ingress.kubernetes.io/proxy-read-timeout: "3600"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "3600"
spec:
  rules:
    - host: my-app.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: java-gui-app
                port:
                  number: 80
```

## Security Considerations

### Production Recommendations:

1. **Enable TLS/SSL**
   - Use HTTPS for noVNC connection
   - Kubernetes Ingress with TLS certificates

2. **Add Authentication**
   - VNC password protection
   - HTTP basic auth at Ingress level
   - OAuth2 proxy for enterprise SSO

3. **Network Isolation**
   - Don't expose port 5900 externally
   - Only expose port 6080 through Ingress
   - Use NetworkPolicies to restrict access

4. **Resource Limits**
   ```yaml
   resources:
     limits:
       cpu: 1000m
       memory: 1Gi
     requests:
       cpu: 500m
       memory: 512Mi
   ```

## Troubleshooting

### Black Screen in noVNC
```bash
# Check if Xvfb is running
ps aux | grep Xvfb

# Check if x11vnc is connected to display
x11vnc -display :99 -bg -nopw -listen localhost -xkb
```

### Java Window Not Visible
```bash
# List all X windows
export DISPLAY=:99
xwininfo -root -tree

# Check Java process
ps aux | grep java
```

### Performance Issues
- Reduce screen resolution
- Lower VNC quality settings
- Use `-noxdamage` flag with x11vnc
- Ensure adequate CPU/memory resources

### Connection Refused
```bash
# Check if websockify is running
netstat -tlnp | grep 6080

# Check container logs
docker logs <container-name>
```

## Alternatives to noVNC

### For Production Environments:

1. **Apache Guacamole**
   - Full remote desktop gateway
   - Supports VNC, RDP, SSH
   - Built-in authentication and user management
   - More complex setup

2. **X2Go**
   - Remote desktop solution for X11
   - Better performance than VNC
   - Requires client installation

3. **XPRA**
   - "Screen for X11"
   - Seamless window forwarding
   - Better than VNC for individual apps

### For Modern Web Deployment:

Consider rewriting the GUI using:
- **JavaFX** with Gluon for web deployment
- **Vaadin** - Java framework for web UIs
- **React/Vue + REST API** - Modern web stack with Java backend

## Resources

- [noVNC Official Site](https://novnc.com/)
- [noVNC GitHub](https://github.com/novnc/noVNC)
- [x11vnc Documentation](http://www.karlrunge.com/x11vnc/)
- [Xvfb Manual](https://www.x.org/releases/X11R7.6/doc/man/man1/Xvfb.1.xhtml)
- [Docker + VNC Best Practices](https://github.com/ConSol/docker-headless-vnc-container)

## Summary

noVNC provides an elegant solution for deploying Java Swing desktop applications as cloud-native services. While it adds some complexity compared to true web applications, it enables:

✅ Legacy Java GUI apps in Kubernetes  
✅ Zero client-side installation  
✅ Browser-based access from any device  
✅ Centralized deployment and updates  

For the Atarist Chess Game, this approach allows players to enjoy the classic Java Swing interface directly in their browser, making the game accessible to anyone with a modern web browser.
