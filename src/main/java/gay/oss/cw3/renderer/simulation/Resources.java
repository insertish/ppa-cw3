package gay.oss.cw3.renderer.simulation;

import java.util.HashMap;

import gay.oss.cw3.renderer.shaders.ShaderProgram;

public class Resources {
    private static HashMap<String, ShaderProgram> shaders = new HashMap<>();

    public static ShaderProgram getShader(String shader) throws Exception {
        if (shaders.containsKey(shader)) return shaders.get(shader);
        try {
            var program = ShaderProgram.fromName(shader);
            shaders.put(shader, program);
            return program;
        } catch (Exception e) {
            System.err.println("Failed to load shader: " + shader);
            throw e;
        }
    }
}
