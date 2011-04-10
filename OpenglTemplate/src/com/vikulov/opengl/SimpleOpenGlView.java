/**
 * 
 */
package com.vikulov.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * @author ёрий
 *
 */
public class SimpleOpenGlView extends GLSurfaceView {

	private MyGLRenderer mRenderer=null;
	
	public SimpleOpenGlView(Context context) {
		super(context);
		
		mRenderer = new MyGLRenderer(context);
		setEGLContextClientVersion(2);
		setRenderer(mRenderer);
	}

}
