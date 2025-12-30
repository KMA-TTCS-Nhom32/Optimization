package com.example.optimization;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    // Số lượng tam giác (2000)
    private static final int OBJECT_COUNT = 2000;

    // --- ĐỊNH NGHĨA CÁC CHẾ ĐỘ (MODE) ---
    // Bài 1: CPU
    public static final int MODE_CPU_LOOP = 0;
    public static final int MODE_CPU_BATCH = 1;

    // Bài 2: GPU / Vertex Shader
    public static final int MODE_GPU_ANIMATION = 2; // Hiệu ứng biến dạng (Bad/Heavy)
    public static final int MODE_GPU_NORMAL = 3;    // Tĩnh (Good/Light)

    private int currentMode = MODE_CPU_LOOP;

    // Các biến Ma trận & OpenGL Handles
    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];
    private final float[] scratch = new float[16];

    private int positionHandle;
    private int colorHandle;
    private int vPMatrixHandle;
    private int timeHandle;

    // Shader Programs
    private int mProgramNormal;
    private int mProgramWobble;

    // Buffers
    private FloatBuffer singleTriangleBuffer;
    private FloatBuffer batchedTrianglesBuffer;

    // Random Positions
    private float[] randomPositionsX;
    private float[] randomPositionsY;

    // FPS
    private long lastTime = System.currentTimeMillis();
    private int frames = 0;
    private String fpsString = "FPS: ...";

    // --- SHADER CODE ---
    // 1. Vertex Shader THƯỜNG
    private final String vertexShaderNormalCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    // 2. Vertex Shader BIẾN DẠNG (Animation)
    private final String vertexShaderWobbleCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "uniform float uTime;" +
                    "void main() {" +
                    "  vec4 newPos = vPosition;" +
                    "  newPos.x += sin(uTime + newPos.y * 10.0) * 0.1;" +
                    "  gl_Position = uMVPMatrix * newPos;" +
                    "}";

    // 3. Fragment Shader (Chung)
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public MyGLRenderer(Context context) { }

    public void setDemoMode(int mode) {
        this.currentMode = mode;
    }

    public String getFPS() {
        return fpsString;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        mProgramNormal = createProgram(vertexShaderNormalCode, fragmentShaderCode);
        mProgramWobble = createProgram(vertexShaderWobbleCode, fragmentShaderCode);
        prepareGeometryData();
    }

    private int createProgram(String vertexCode, String fragmentCode) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        return program;
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private void prepareGeometryData() {
        float scale = 0.05f;
        float[] triangleCoords = {
                0.0f,  scale, 0.0f,
                -scale, -scale, 0.0f,
                scale, -scale, 0.0f
        };

        // Loop Mode Data
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        singleTriangleBuffer = bb.asFloatBuffer();
        singleTriangleBuffer.put(triangleCoords);
        singleTriangleBuffer.position(0);

        // Random positions
        randomPositionsX = new float[OBJECT_COUNT];
        randomPositionsY = new float[OBJECT_COUNT];
        Random rand = new Random();
        for(int i=0; i<OBJECT_COUNT; i++) {
            randomPositionsX[i] = (rand.nextFloat() * 2 - 1) * 2.5f;
            randomPositionsY[i] = (rand.nextFloat() * 2 - 1) * 4.0f;
        }

        // Batch Mode Data
        float[] allCoords = new float[OBJECT_COUNT * 3 * 3];
        int index = 0;
        for(int i=0; i<OBJECT_COUNT; i++) {
            float dx = randomPositionsX[i];
            float dy = randomPositionsY[i];
            allCoords[index++] = triangleCoords[0] + dx; allCoords[index++] = triangleCoords[1] + dy; allCoords[index++] = triangleCoords[2];
            allCoords[index++] = triangleCoords[3] + dx; allCoords[index++] = triangleCoords[4] + dy; allCoords[index++] = triangleCoords[5];
            allCoords[index++] = triangleCoords[6] + dx; allCoords[index++] = triangleCoords[7] + dy; allCoords[index++] = triangleCoords[8];
        }

        ByteBuffer bb2 = ByteBuffer.allocateDirect(allCoords.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        batchedTrianglesBuffer = bb2.asFloatBuffer();
        batchedTrianglesBuffer.put(allCoords);
        batchedTrianglesBuffer.position(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        countFPS();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        int programToUse;
        if (currentMode == MODE_GPU_ANIMATION) {
            programToUse = mProgramWobble;
        } else {
            programToUse = mProgramNormal;
        }

        GLES20.glUseProgram(programToUse);

        positionHandle = GLES20.glGetAttribLocation(programToUse, "vPosition");
        colorHandle = GLES20.glGetUniformLocation(programToUse, "vColor");
        vPMatrixHandle = GLES20.glGetUniformLocation(programToUse, "uMVPMatrix");

        if (currentMode == MODE_GPU_ANIMATION) {
            timeHandle = GLES20.glGetUniformLocation(programToUse, "uTime");
            float time = (float) (System.currentTimeMillis() % 100000L) / 500.0f;
            GLES20.glUniform1f(timeHandle, time);
        }

        GLES20.glEnableVertexAttribArray(positionHandle);

        if (currentMode == MODE_CPU_LOOP) {
            // Bad CPU
            GLES20.glUniform4f(colorHandle, 1.0f, 0.3f, 0.3f, 1.0f);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, singleTriangleBuffer);
            for (int i = 0; i < OBJECT_COUNT; i++) {
                Matrix.setIdentityM(mModelMatrix, 0);
                Matrix.translateM(mModelMatrix, 0, randomPositionsX[i], randomPositionsY[i], 0);
                Matrix.multiplyMM(scratch, 0, vPMatrix, 0, mModelMatrix, 0);
                GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, scratch, 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
            }
        } else if (currentMode == MODE_CPU_BATCH) {
            // Good CPU
            GLES20.glUniform4f(colorHandle, 0.3f, 1.0f, 0.3f, 1.0f);
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, vPMatrix, 0);
            batchedTrianglesBuffer.position(0);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, batchedTrianglesBuffer);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, OBJECT_COUNT * 3);
        } else if (currentMode == MODE_GPU_ANIMATION) {
            // Animation
            GLES20.glUniform4f(colorHandle, 1.0f, 0.6f, 0.0f, 1.0f);
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, vPMatrix, 0);
            batchedTrianglesBuffer.position(0);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, batchedTrianglesBuffer);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, OBJECT_COUNT * 3);
        } else if (currentMode == MODE_GPU_NORMAL) {
            // Static
            GLES20.glUniform4f(colorHandle, 0.0f, 0.8f, 1.0f, 1.0f);
            GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, vPMatrix, 0);
            batchedTrianglesBuffer.position(0);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, batchedTrianglesBuffer);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, OBJECT_COUNT * 3);
        }

        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private void countFPS() {
        frames++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= 1000) {
            fpsString = frames + " FPS";
            frames = 0;
            lastTime = currentTime;
            Log.d("OpenGLDemo", "FPS: " + fpsString);
        }
    }
}