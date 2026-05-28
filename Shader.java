import static org.lwjgl.opengl.GL40.*;

import java.io.BufferedReader;
import java.io.FileReader;

public class Shader {

    private int ID;

    public Shader(String vertexPath, String fragmentPath) {
        String vertexSource = readFile(vertexPath);
        String fragmentSource = readFile(fragmentPath);

        int vertex = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertex, vertexSource);
        glCompileShader(vertex);

        int fragment = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragment, fragmentSource);
        glCompileShader(fragment);

        ID = glCreateProgram();
        glAttachShader(ID, vertex);
        glAttachShader(ID, fragment);
        glLinkProgram(ID);

        glDeleteShader(vertex);
        glDeleteShader(fragment);
    }

    public void setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(ID, name), value);
    }

    public int getLocation(String name) {
        return glGetUniformLocation(ID, name);
    }

    public void use() {
        glUseProgram(ID);        
    }

    public int get() {
        return ID;
    }

    public void stop() {
        glDeleteProgram(ID);
    }

    private String readFile(String path) {
        StringBuilder source = new StringBuilder();
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            while ((line = reader.readLine()) != null) {
                source.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Couldn't read file path: " + path);
        }
        return source.toString();
    }
}
