/**
 * 
 */
package com.vikulov.opengl;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

/**
 * @author ёрий
 * 
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

	private final String TAG = "MyGLRenderer";
	private Context mContext = null;

	private float[] mMVPMatrix = new float[16];
	private float[] mProjMatrix = new float[16];
	private float[] mMMatrix = new float[16];
	private float[] mVMatrix = new float[16];

	Square sq = new Square();
	TexturedSquare sqTex = new TexturedSquare();
	int program;
	int programTextured;

	public MyGLRenderer(Context context) {
		mContext = context;
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {

		GLES20.glClearColor(0.9f, 0.9f, 0.9f, .5f);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glEnable( GLES20.GL_BLEND );
		GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		
		
		// Use the program object
		GLES20.glUseProgram(program);

		// Load the vertex data
		GLES20.glVertexAttribPointer(
				GLES20.glGetAttribLocation(program, "aPosition"), 3,
				GLES20.GL_FLOAT, false, 0, sq.getVertexBuffer());
		
		GLES20.glVertexAttrib4f(
				GLES20.glGetAttribLocation(program, "acolor"), 
				1f,1f,0f,0.5f);
		GLES20.glEnableVertexAttribArray(0);

		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

		GLES20.glUniformMatrix4fv(
				GLES20.glGetUniformLocation(program, "uMVPMatrix"), 1, false,
				mMVPMatrix, 0);

		// GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT,	sq.getIndexBuffer());
		// GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 4);
		
		Matrix.setIdentityM(mMMatrix, 0);
		Matrix.rotateM(mMMatrix, 0, 20.2f, 0, 0, 1);
		Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
		
		GLES20.glUseProgram(programTextured);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
		
		
		sqTex.getVertexBuffer().position(sqTex.VERT_OFFSET);
		GLES20.glVertexAttribPointer(
				GLES20.glGetAttribLocation(programTextured, "aPosition"), 3,
				GLES20.GL_FLOAT, false, 5 * 4, sqTex.getVertexBuffer());
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(programTextured, "aPosition"));
		
		sqTex.getVertexBuffer().position(sqTex.TEXT_OFFSET);
		GLES20.glVertexAttribPointer(
				GLES20.glGetAttribLocation(programTextured, "aTextureCoord"), 2,
				GLES20.GL_FLOAT, false, 5 * 4, sqTex.getVertexBuffer());
		GLES20.glEnableVertexAttribArray(GLES20.glGetAttribLocation(programTextured, "aTextureCoord"));
		
		
		GLES20.glUniformMatrix4fv(
				GLES20.glGetUniformLocation(programTextured, "uMVPMatrix"), 1, false,
				mMVPMatrix, 0);
		GLES20.glVertexAttrib4f(
				GLES20.glGetAttribLocation(programTextured, "acolor"), 
				.6f,0.3f,0.9f,.5f);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT,	sqTex.getIndexBuffer());
		
		GLES20.glDisableVertexAttribArray(GLES20.glGetAttribLocation(programTextured, "aTextureCoord"));
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		float ratio = (float) width / height;
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3f, 17);

	}

	int mTextureID;
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

		program = createProgram(mVertexShader, mFragmentShader);
		programTextured = createProgram(mVertexShaderTextured, mFragmentShaderTextured);
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
		
		int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);

        InputStream is = mContext.getResources()
            .openRawResource(R.drawable.ywemmo2);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                // Ignore.
            }
        }

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
	}

	// SaHDER
	private int loadShader(int shaderType, String source) {
		int shader = GLES20.glCreateShader(shaderType);
		if (shader != 0) {
			GLES20.glShaderSource(shader, source);
			GLES20.glCompileShader(shader);
			int[] compiled = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			if (compiled[0] == 0) {
				Log.e(TAG, "Could not compile shader " + shaderType + ":");
				Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
				GLES20.glDeleteShader(shader);
				shader = 0;
			}
		}
		return shader;
	}

	private int createProgram(String vertexSource, String fragmentSource) {
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
		if (vertexShader == 0) {
			return 0;
		}

		int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
		if (pixelShader == 0) {
			return 0;
		}

		int program = GLES20.glCreateProgram();
		if (program != 0) {
			GLES20.glAttachShader(program, vertexShader);
			checkGlError("glAttachShader");
			GLES20.glAttachShader(program, pixelShader);
			checkGlError("glAttachShader");
			GLES20.glLinkProgram(program);
			int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
			if (linkStatus[0] != GLES20.GL_TRUE) {
				Log.e(TAG, "Could not link program: ");
				Log.e(TAG, GLES20.glGetProgramInfoLog(program));
				GLES20.glDeleteProgram(program);
				program = 0;
			}
		}
		return program;
	}

	private void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}

	private final String mVertexShader = "uniform mat4 uMVPMatrix;\n"
			+ "attribute vec4 aPosition;\n attribute vec4 acolor;\n varying vec4 vcolor;\n" + "void main() {\n"
			+ "  gl_Position = uMVPMatrix * aPosition;\n vcolor=acolor;\n" + "}\n";

	private final String mFragmentShader = "precision mediump float;\n"+
			"varying vec4 vcolor;\n" +
			"void main() {\n" + "vec4 color = vcolor; \n"
			+ "  gl_FragColor = color;\n" + "}\n";
	
	private final String mVertexShaderTextured = "uniform mat4 uMVPMatrix;\n"
		+ "attribute vec4 aPosition;\n " +
		"attribute vec2 aTextureCoord;\n" +
        "varying vec2 vTextureCoord;\n" +
        "attribute vec4 acolor;\n varying vec4 vcolor;\n" + "void main() {\n"
		+ "  gl_Position = uMVPMatrix * aPosition;\n vcolor=acolor;\n" +
		"  vTextureCoord=aTextureCoord;"+
		"}\n";

private final String mFragmentShaderTextured = "precision mediump float;\n"+
		"varying vec4 vcolor;\n" +
		"varying vec2 vTextureCoord;\n" +
		"uniform sampler2D sTexture;\n" +
		"void main() {\n" + "vec4 color = vcolor; \n"+
		"vec3 t=texture2D(sTexture, vTextureCoord).xyz;"+
		"  gl_FragColor = vec4(t.x,t.y,t.z,0.8);\n" + "}\n";

}
