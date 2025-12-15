# Chess Game Deployment Guide

## Overview
This Java Swing chess game is deployed as a web application using noVNC, allowing browser-based access to the desktop GUI.

## Architecture
- **Base Application**: Java Swing GUI chess game
- **Web Interface**: noVNC (VNC over WebSocket)
- **Container**: Ubuntu with Xvfb (virtual display), x11vnc, and noVNC
- **Orchestration**: Kubernetes via Helm charts
- **CI/CD**: Jenkins with Kaniko and Trivy

## Deployment Methods

### 1. Local Testing
```bash
# Build the Docker image
make build

# Run locally (access at http://localhost:6080/vnc.html)
make run
```

### 2. Jenkins Pipeline
Push to GitHub and Jenkins will automatically:
1. Build the Docker image using Kaniko
2. Scan for vulnerabilities with Trivy
3. Deploy to Kubernetes using Helm
4. Accessible at: https://chess-game.atarnet.org

### 3. Manual Kubernetes Deployment
```bash
# Build and push image
docker build -t tinorodney/chess-game:v1.0.0 .
docker push tinorodney/chess-game:v1.0.0

# Deploy with Helm
kubectl create namespace chess-game-ns
helm upgrade --install chess-game ./charts/app \
  --namespace chess-game-ns \
  --set image.repository=tinorodney/chess-game \
  --set image.tag=v1.0.0
```

## Accessing the Application

### Via Browser (noVNC)
1. Navigate to `https://chess-game.atarnet.org` (production)
2. Or `http://localhost:6080/vnc.html` (local)
3. Click "Connect" to access the chess game

### Direct VNC
- Port 5900 is exposed for native VNC clients
- Example: `vncviewer chess-game.atarnet.org:5900`

## Configuration

### Environment Variables
- `VNC_RESOLUTION`: Screen resolution (default: 1280x720)
- `DISPLAY`: X display number (default: :99)
- `VNC_PORT`: VNC server port (default: 5900)
- `NOVNC_PORT`: noVNC web port (default: 6080)

### Helm Values
Edit `charts/app/values.yaml` to customize:
- Resource limits (CPU/Memory)
- Ingress hostname
- Number of replicas
- Image repository and tag

## Troubleshooting

### Container Issues
```bash
# Check logs
kubectl logs -n chess-game-ns deployment/chess-game

# Access container shell
kubectl exec -it -n chess-game-ns deployment/chess-game -- /bin/bash

# Local testing
docker logs chess-test
docker exec -it chess-test /bin/bash
```

### Build Issues
- **Test files excluded**: The Dockerfile only compiles `src/com/**/*.java` to avoid JUnit dependency issues
- Ensure Guava JAR is downloaded correctly
- Verify all Java source files compile
- Check art/ directory is copied

### Display Issues
- Verify Xvfb is running
- Check x11vnc connection
- Ensure noVNC websocket is active

## Notes
- The application runs in a virtual X server (Xvfb)
- Multiple users can connect simultaneously
- Each connection shares the same game instance
- For production, consider adding authentication to noVNC
