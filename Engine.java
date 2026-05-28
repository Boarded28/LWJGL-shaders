import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL40.*;

public class Engine {
    
    private static long window;
    private static Engine inst;
    private int vao, vbo, positionLocation, vertexColorLoc;
    private float pos[];    
    private float 
    dt = 0.0f, last = 0.0f, 
    xPos = 0.0f, yPos = 0.0f, speed = 0.6f,
    objWidthRight = 0.1f, objWidthLeft = 0.1f,
    objHeightTop = 0.15f, objHeightBottom = 0.1f;
    private double angle = Math.toRadians(35.0);
    private float 
    xVel = (float) (Math.cos(angle) * speed),
    yVel = (float) (Math.sin(angle) * speed);

    private String 
    vertexPath = "shaders/vertex.glsl",
    fragmentPath = "shaders/fragment.glsl";

    private Shader shader;

    public Engine() {}

    public static Engine get() {
        if (inst == null) {
            inst = new Engine();
        }

        return inst;
    }

    public void run() {
        init();
        loop();
        stop();
    }

    private void init() {
        glfwInit(); // initialize opengl
        // use opengl version 3.3
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        // use the core profile (modern opengl)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        // minor setups
        glfwWindowHint(GLFW_RESIZABLE, 0);
        glfwWindowHint(GLFW_VISIBLE, 0);
        // create the window and set it as the current context
        window = glfwCreateWindow(640, 400, "OpenGL - \"Shaders\"", 0, 0);
        glfwMakeContextCurrent(window);
        // Initialize OpenGL capabilities for the current context
        GL.createCapabilities();

        // other stuff ------------------------------------------------------        
        // the coordinates for the vertex
        float vertices[] = {
            // positions         // colors
            -0.1f, -0.1f,  0.0f,  0.95f, 0.95f, 1.0f,  // bottom-left
             0.0f,  0.15f, 0.0f,   0.0f, 0.90f, 1.0f,  // top-middle
             0.1f, -0.1f,  0.0f,   1.0f, 0.15f, 0.55f, // bottom-right
        };

        // send the data (vertices) to memory
        // (1) Allocate rooms to memory, store the ID of the memory -----
        vao = glGenVertexArrays(); // vertex array object, we configure this on how to read the data (vbo)
        vbo = glGenBuffers(); // vertex buffer object, the area we store the data to be read (vao)
        // (2) Bind all the following instructions to our 'vao' -----
        glBindVertexArray(vao);
        // (3) Bind all operations to our 'vbo' then allocate VRAM, send the data from CPU to GPU -----
        glBindBuffer(GL_ARRAY_BUFFER, vbo); // designate vbo as an array buffer
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW); // our vbo now contains all the vertices
        // (4) The instructions to read the data, written to out 'vao' -----
        // position data
        int stride = 6 * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0); // the instructions on reading the data
        glEnableVertexAttribArray(0); // enable slot 0 [location = 0]
        // color data
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES); // offset to the 3rd index of the array where rgb starts
        glEnableVertexAttribArray(1); // enable slot 1 [location = 1]

        glBindVertexArray(0); // stops it from listening to instructions

        shader = new Shader(vertexPath, fragmentPath);
        positionLocation = shader.getLocation("uPos");
        vertexColorLoc = shader.getLocation("VertexColor");

        // Finally, make everything visible
        glfwShowWindow(window);
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {

            // a simple delta time system
            float current = (float) glfwGetTime(); // grabs the current system time
            dt = current - last; // subtracts the previous time and the current time
            last = current; // resets
           
            processInput(window, dt);

            glClearColor(0.090f, 0.098f, 0.114f, 1.0f); // sets the color to clear the screen with
            glClear(GL_COLOR_BUFFER_BIT); // actual clear

            shader.use(); // my shader :3

            // uniform vec4, basically accessible colors to our fragment
            double timeValue = glfwGetTime(); // current time
            // a neon effect I found online
            double redValue   = (Math.sin(timeValue) * 0.5) + 0.5; 
            double greenValue = (Math.sin(timeValue + 2.0944) * 0.5) + 0.5; 
            double blueValue  = (Math.sin(timeValue + 4.1888) * 0.5) + 0.5;
            // accesses the uniform variable and updates its values
            glUniform4f(vertexColorLoc, (float) redValue, (float) greenValue, (float) blueValue, 1.0f);

            // using the variable xPos will make it move to the right
            // you can also set it with key inputs (by default I disabled it, go enable it yourself)
            pos = bounceAlgorithm(dt);
            glUniform3f(positionLocation, pos[0], pos[1], pos[2]); // same as the color but for coordinates

            glBindVertexArray(vao); // uses the instructions I made up top
            glDrawArrays(GL_TRIANGLES, 0, 3); // tells openGL what to draw

            glfwSwapBuffers(window); // a double buffer, buffer 1 draws the next frame while buffer 2 shows the current
            glfwPollEvents(); // allow checking for input events such as key inputs
        }

        // this is a good boi behavior, always clean up after your mess!
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        shader.stop();
    }

    // stops openGL
    private void stop() {
        glfwDestroyWindow(window);
        glfwTerminate();
    }
        
    private float[] bounceAlgorithm(float dt) {
        // simple application of velocity with delta time
        xPos += xVel * dt;
        yPos += yVel * dt;

        // checks for bounce duhh
        boolean bounced = false;

        // simple boundary detection
        // switches direction if it hits the boundary (edges of the viewport)
        if (xPos + objWidthRight >= 1.0f) {
            xPos = 1.0f - objWidthRight;
            xVel = -xVel;
            bounced = true;
        } else if (xPos - objWidthLeft <= -1.0f) {
            xPos = -1.0f + objWidthLeft;
            xVel = -xVel;
            bounced = true;
        }

        if (yPos + objHeightTop >= 1.0f) {
            yPos = 1.0f - objHeightTop;
            yVel = -yVel;
            bounced = true;
        } else if (yPos - objHeightBottom <= -1.0f) {
            yPos = -1.0f + objHeightBottom;
            yVel = -yVel;
            bounced = true;
        }

        // the bounce algorithm
        if (bounced) {
            // takes the current angle using an inverse function called arctan2
            angle = Math.atan2(yVel, xVel);
            // adds randomness to the next angle direction, from -3 to 3 degrees
            double variation = Math.toRadians((Math.random() * 6.0) - 3.0);
            // adds the random angle to the current angle
            angle += variation;
            // updates the velocity
            xVel = (float) (Math.cos(angle) * speed);
            yVel = (float) (Math.sin(angle) * speed);
        }

        return new float[]{xPos, yPos, 0.0f};
    }

    private void processInput(long ID, float dt) {
        if (glfwGetKey(ID, GLFW_KEY_ESCAPE) == 1) {
            glfwSetWindowShouldClose(ID, true);
        }

        // enable this for movement through user input
        // Note: won't work unless you set it in the loop()
        /*
        if (glfwGetKey(ID, GLFW_KEY_LEFT) == 1) {
            xPos -= 0.2 * dt;
        }

        if (glfwGetKey(ID, GLFW_KEY_RIGHT) == 1) {
            xPos += 0.2 * dt;
        }
        */
    }

}
