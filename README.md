<!-- PROJECT LOGO -->
<br />
<p align="center">

  <h1 align="center">ATARIST CHESS GAME</h1>

  <p align="center"> 
    <br />
    <br />
    <a href="https://chessgame.atarnet.org">View Demo</a>
    ·
    <a href="https://github.com/tinotenda-alfaneti/chess-game/issues/new">Report Bug</a>
    ·
    <a href="https://github.com/tinotenda-alfaneti/chess-game/issues">Request Feature</a>
    ·
    <a href="https://github.com/tinotenda-alfaneti/chess-game/pulls">Send a Pull Request</a>
  </p>
</p>

<!-- ABOUT THE PROJECT -->
## ABOUT THE PROJECT

According to [Baeldung](https://www.baeldung.com/cs/ai-chess), The idea of having a machine play chess against a human has been around for a long time and could have started in the late 18th century from an elaborate hoax. 
   The first computer chess program was written by Alan Turing in 1950.
This was soon after the second World War. At that time the computer was not
invented so he had to run the program using pencil and paper and acting as a
human CPU and each move took him between 15 to 30 minutes (not sure about this though ;-)). Enough of about the history. [Atarist Chess Game](https://github.com/tinotenda-alfaneti/chess-game) is an AI based chess game. The game has 
option for 2 player, 1 player or 0 player(watching computer play computer). The AI algorithm used was the [Minimax Algorithm](https://en.wikipedia.org/wiki/Minimax). However, because of the size of the game tree of the chess game, 
I am still limiting the depth to four but very soon it will be more than that after I implement [alpha-beta pruning](https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning). I used [Java Swing package](https://www.javatpoint.com/java-swing) for the graphical user interface. 


DISCLAIMER: At this point, this project is just for learning purposes only.

### BUILT WITH
Here are the main modules and tool I used for the project
* [Java](https://www.java.com/en/) - Java is a high-level, class-based, object-oriented programming language that is designed to have as few implementation dependencies as possible.
* [Guava](https://github.com/google/guava) - Guava is an open source, Java-based library developed by Google. It facilitates best coding practices and helps reduce coding errors.
* [JUnit5 Jupiter](https://junit.org/junit5/docs/current/user-guide/) - JUnit Jupiter is the combination of the programming model and extension model for writing tests and extensions in JUnit 5. The JUnit Platform serves as a foundation for launching testing frameworks on the JVM
* [Java Swing](https://en.wikipedia.org/wiki/Swing_(Java)) - The Swing framework in Java is built on top of the AWT framework and can be used to create GUI applications just like AWT.
* [IntelliJ](https://www.jetbrains.com/idea/) - IntelliJ IDEA is undoubtedly the top-choice IDE for software developers. It makes Java and Kotlin development a more productive and enjoyable experience
* [noVNC](https://novnc.com/) - Browser-based VNC client for web-based access to the chess game


## GETTING STARTED

### Prerequisites
- [Docker](https://www.docker.com/get-started) installed on your machine
- Modern web browser (Chrome, Firefox, Safari, Edge)

### Running Locally with Docker

1. **Clone the repository**
   ```bash
   git clone https://github.com/tinotenda-alfaneti/chess-game.git
   cd chess-game
   ```

2. **Build the Docker image**
   ```bash
   docker build -t chess-game:latest .
   ```
   Or using make:
   ```bash
   make build
   ```

3. **Run the container**
   ```bash
   docker run -d -p 6080:6080 -p 5900:5900 --name chess-game chess-game:latest
   ```
   Or using make:
   ```bash
   make run
   ```

4. **Access the game**
   - Open your browser and navigate to: **http://localhost:6080/vnc.html**
   - Click the **Connect** button
   - The chess game window will appear - start playing!

5. **Stop the game**
   ```bash
   docker stop chess-game
   docker rm chess-game
   ```

### Running with Make Commands

```bash
# Build the image
make build

# Run locally
make run

# Build and push to Docker Hub
make push

# Deploy to Kubernetes
make deploy

# Run local test
make test-local
```

### Traditional Desktop Mode (Requires Java)

If you prefer to run the chess game as a traditional desktop application:

1. **Prerequisites**
   - Java 17 or higher installed
   - Download Guava library

2. **Compile and run**
   ```bash
   # Compile
   javac -cp "lib/guava-31.1-jre.jar" -d out $(find src/com -name "*.java")
   
   # Run
   java -cp "out:lib/guava-31.1-jre.jar" com.chess.jChess
   ```

### Deployment to Kubernetes

For production deployment to Kubernetes, see [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md)

For details on how noVNC works with this application, see [docs/NOVNC_GUIDE.md](docs/NOVNC_GUIDE.md)


<!-- ROADMAP -->
## ROADMAP
Feel free to contribute on any of the [TODOs](https://github.com/tinotenda-alfaneti/chess-game/blob/main/TODO.md) that are not yet accomplished.



<!-- CONTRIBUTING -->
## CONTRIBUTING

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **extremely appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/fantasticfeature`)
3. Commit your Changes (`git commit -m 'Add some improvements'`)
4. Push to the Branch (`git push origin feature/fantasticfeature`)
5. Open a Pull Request



<!-- CONTACT -->
## CONTACT

Tinotenda Rodney Alfaneti - [@Linkedin](https://www.linkedin.com/in/tinotenda-rodney-alfaneti/)



<!-- ACKNOWLEDGEMENTS -->
## ACKNOWLEDGEMENTS
* [Amir650](https://github.com/amir650)





<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[forks-shield]: https://img.shields.io/github/forks/roshanlam/ReadMeTemplate?style=for-the-badge
[forks-url]: https://github.com/roshanlam/ReadMeTemplate/network/members
[stars-shield]: https://img.shields.io/github/stars/roshanlam/ReadMeTemplate?style=for-the-badge
[stars-url]: https://github.com/roshanlam/ReadMeTemplate/stargazers
[issues-shield]: https://img.shields.io/github/issues/roshanlam/ReadMeTemplate?style=for-the-badge
[issues-url]: https://github.com/roshanlam/ReadMeTemplate/issues
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/roshan-lamichhane 


