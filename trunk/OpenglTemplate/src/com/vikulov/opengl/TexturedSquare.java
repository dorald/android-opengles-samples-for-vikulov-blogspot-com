package com.vikulov.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class TexturedSquare {

	// private float[] vertices=new float[4];
	
	float vertices[] = { -1.0f, -1.0f, 0.0f,0.0f,0.0f, // 0, Top Left  //x,y,z,u,v
			1.0f, -1.0f, 0.0f,0.0f,1.0f, // 1, Bottom Left
			1.0f, 1.0f, 0.0f,1.0f,1.0f, // 2, Bottom Right
			-1.0f, 1.0f, 0.0f,1.0f,0.0f, // 3, Top Right
	};
	
	public static int VERT_OFFSET=0;
	public static int TEXT_OFFSET=3;

	short[] indices = { 0, 1, 2, 2, 3, 0 };;
	
	// Our vertex buffer.
	private FloatBuffer vertexBuffer;

	// Our index buffer.
	private ShortBuffer indexBuffer;

	public TexturedSquare()
	{
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
		
	}
	
	FloatBuffer getVertexBuffer(){
		return vertexBuffer;
	}
	
	ShortBuffer getIndexBuffer(){
		return indexBuffer;
	}
	
	
	
}
