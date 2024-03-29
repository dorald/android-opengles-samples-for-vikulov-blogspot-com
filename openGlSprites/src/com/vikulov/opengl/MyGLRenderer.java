/**
 * 
 */
package com.vikulov.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

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
 * @author ����
 * 
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

	///
    // Constructor
    //
    public MyGLRenderer(Context context)
    {
        mContext = context;
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
        mIndices = ByteBuffer.allocateDirect(mIndicesData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mIndices.put(mIndicesData).position(0);
    }

    ///
    //  Load texture from resource
    //
    private int loadTexture ( InputStream is )
    {
        int[] textureId = new int[1];
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeStream(is);
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                // Ignore.
            }
        }

        
       
            
        GLES20.glGenTextures ( 1, textureId, 0 );
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_2D, textureId[0] );

        //GLES20.glTexImage2D ( GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, bitmap.getWidth(), bitmap.getHeight(), 0, 
        //                      GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, byteBuffer );
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE );
        GLES20.glTexParameteri ( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE );
        
        return textureId[0];
    }
 
 
    ///
    // Initialize the shader and program object
    //
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        String vShaderStr =
             "attribute vec4 a_position;   \n" +
             "attribute vec2 a_texCoord;   \n" +
             "varying vec2 v_texCoord;     \n" +
             "void main()                  \n" +
             "{                            \n" +
             "   gl_Position = a_position; \n" +
             "  gl_PointSize=45.0;"+
             "   v_texCoord = a_texCoord;  \n" +
             "}                            \n";

        String fShaderStr =
            "precision mediump float;                            \n" +
            "varying vec2 v_texCoord;                            \n" +
            "uniform sampler2D s_baseMap;                        \n" +
            "uniform sampler2D s_lightMap;                       \n" +
            "void main()                                         \n" +
            "{                                                   \n" +
            "  vec4 baseColor;                                   \n" +
            "  vec4 lightColor;                                  \n" +
            "                                                    \n" +
            "  baseColor = texture2D( s_baseMap, gl_PointCoord );   \n" +
            "  lightColor = texture2D( s_lightMap, gl_PointCoord ); \n" +
            "  gl_FragColor = baseColor * (lightColor + 0.25);   \n" +
            //"  gl_FragColor = vec4(1,0,0,1);   \n" +
            "}                                                   \n";                                                 
                
        // Load the shaders and get a linked program object
        mProgramObject = ESShader.loadProgram(vShaderStr, fShaderStr);

        // Get the attribute locations
        mPositionLoc = GLES20.glGetAttribLocation(mProgramObject, "a_position");
        mTexCoordLoc = GLES20.glGetAttribLocation(mProgramObject, "a_texCoord" );
        
        // Get the sampler locations
        mBaseMapLoc = GLES20.glGetUniformLocation ( mProgramObject, "s_baseMap" );
        mLightMapLoc = GLES20.glGetUniformLocation ( mProgramObject, "s_lightMap" );

        // Load the texture
        mBaseMapTexId = loadTexture(mContext.getResources().openRawResource(R.drawable.ywemmo2));
        mLightMapTexId = loadTexture(mContext.getResources().openRawResource(R.drawable.diffuse));

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    // /
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    public void onDrawFrame(GL10 glUnused)
    {
        // Set the viewport
        GLES20.glViewport(0, 0, mWidth, mHeight);

        // Clear the color buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.8f, 0.8f,0.8f, 0.8f);
        // Use the program object
        GLES20.glUseProgram(mProgramObject);

        // Load the vertex position
        mVertices.position(0);
        GLES20.glVertexAttribPointer ( mPositionLoc, 3, GLES20.GL_FLOAT, 
                                       false, 
                                       5 * 4, mVertices );
        // Load the texture coordinate
        mVertices.position(3);
        GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT,
                                       false, 
                                       5 * 4, 
                                       mVertices );

        GLES20.glEnableVertexAttribArray ( mPositionLoc );
        GLES20.glEnableVertexAttribArray ( mTexCoordLoc );


        // Bind the base map
        GLES20.glActiveTexture ( GLES20.GL_TEXTURE0 );
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_2D, mBaseMapTexId );

        // Set the base map sampler to texture unit to 0
        GLES20.glUniform1i ( mBaseMapLoc, 0 );

        // Bind the light map
        GLES20.glActiveTexture ( GLES20.GL_TEXTURE1 );
        GLES20.glBindTexture ( GLES20.GL_TEXTURE_2D, mLightMapTexId );
        
        // Set the light map sampler to texture unit 1
        GLES20.glUniform1i ( mLightMapLoc, 1 );

        GLES20.glDrawElements ( GLES20.GL_POINTS, 6, GLES20.GL_UNSIGNED_SHORT, mIndices );
    }

    ///
    // Handle surface changes
    //
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        mWidth = width;
        mHeight = height;
    }

    
    // Handle to a program object
    private int mProgramObject;
    
    // Attribute locations
    private int mPositionLoc;
    private int mTexCoordLoc;
    
    // Sampler location
    private int mBaseMapLoc;
    private int mLightMapLoc;
    
    // Texture handle
    private int mBaseMapTexId;
    private int mLightMapTexId;
    
    // Additional member variables
    private int mWidth;
    private int mHeight;
    private FloatBuffer mVertices;
    private ShortBuffer mIndices;
    private Context mContext;
    
    private final float[] mVerticesData =
    { 
            -0.5f, 0.5f, 0.0f, // Position 0
            0.0f, 0.0f, // TexCoord 0
            -0.5f, -0.5f, 0.0f, // Position 1
            0.0f, 1.0f, // TexCoord 1
            0.5f, -0.5f, 0.0f, // Position 2
            1.0f, 1.0f, // TexCoord 2
            0.5f, 0.5f, 0.0f, // Position 3
            1.0f, 0.0f // TexCoord 3
    };

    private final short[] mIndicesData =
    { 
            0, 1, 2, 0, 2, 3 
    };
    
    
}
