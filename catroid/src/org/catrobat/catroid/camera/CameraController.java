/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;


public class CameraController implements Camera.PreviewCallback{

	private byte[] image; //The image buffer that will hold the camera image when preview callback arrives
	private transient static CameraController instance;
	private transient boolean videoSwitchedOn = false;
	private transient String transparency = "0.5";
	private transient Camera camera;
	private int cameraID = 1;
	private transient boolean cameraStarted = false;
	//private static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

	private transient int width = 1280;
	private transient int height = 720;

	private transient boolean paused = false;

	private ByteBuffer yBuffer;
	private ByteBuffer uvBuffer;

	public ShaderProgram shader;
	private Texture yTexture;
	private Texture uvTexture;
	private Mesh mesh;
	private SurfaceTexture surface;

	public static CameraController getInstance() {
		if (instance == null) {
			instance = new CameraController();
		}
		return instance;
	}

	private CameraController() {
		Log.d("Lausi", "Construct CameraControl");
		//Our YUV image is 12 bits per pixel
		image = new byte[height*width/8*12];
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {

		Log.d("lausi", "onPreviewFRAME :D");
		camera.addCallbackBuffer(image);
	}

	public void init() {
		Log.d("Lausi", "INIT MESH");
		Texture.setEnforcePotImages(false);
		yTexture = new Texture(height, width, Format.Intensity); //A 8-bit per pixel format
		uvTexture = new Texture(height/2, width/2,Format.LuminanceAlpha); //A 16-bit per pixel format

		yBuffer = ByteBuffer.allocateDirect(width*height);
		uvBuffer = ByteBuffer.allocateDirect((width*height)/2); //We have (720/2*1280/2) pixels, each pixel is 2 bytes
		yBuffer.order(ByteOrder.nativeOrder());
		uvBuffer.order(ByteOrder.nativeOrder());

		String vertexShader =
				"attribute vec4 a_position;                         \n" +
				"attribute vec2 a_texCoord;                         \n" +
				"varying vec2 v_texCoord;                           \n" +

				"void main(){                                       \n" +
				"   gl_Position = a_position;                       \n" +
				"   v_texCoord = a_texCoord;                        \n" +
				"}                                                  \n";

		//Our fragment shader code; takes Y,U,V values for each pixel and calculates R,G,B colors,
		//Effectively making YUV to RGB conversion
		String fragmentShader =
				"#ifdef GL_ES                                       \n" +
				"precision highp float;                             \n" +
				"#endif                                             \n" +

				"varying vec2 v_texCoord;                           \n" +
				"uniform sampler2D yk_texture;                       \n" +
				"uniform sampler2D uv_texture;                      \n" +

				"void main (void){                                  \n" +
				"   float r, g, b, a, y, u, v;                      \n" +

				//We had put the Y values of each pixel to the R,G,B components by GL_LUMINANCE,
				//that's why we're pulling it from the R component, we could also use G or B
				"   y = texture2D(yk_texture, v_texCoord).r;         \n" +

				//We had put the U and V values of each pixel to the A and R,G,B components of the
				//texture respectively using GL_LUMINANCE_ALPHA. Since U,V bytes are interspread
				//in the texture, this is probably the fastest way to use them in the shader
				"   u = texture2D(uv_texture, v_texCoord).a - 0.5;  \n" +
				"   v = texture2D(uv_texture, v_texCoord).r - 0.5;  \n" +


				//The numbers are just YUV to RGB conversion constants
				"   r = y + 1.13983*v;                              \n" +
				"   g = y - 0.39465*u - 0.58060*v;                  \n" +
				"   b = y + 2.03211*u;                              \n" +
				"	a = "+transparency+";							\n" +

				"   gl_FragColor = vec4(r, g, b, 1.0) * a;          \n" +
				"}                                                  \n";

		shader = new ShaderProgram(vertexShader, fragmentShader);
		mesh = new Mesh(true, 4, 6,
				new VertexAttribute(Usage.Position, 2, "a_position"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord"));
		float[] vertices = {

				/*-1.0f,  1.0f,   // Position 0
				0.0f,   0.0f,   // TexCoord 0
				-1.0f,  -1.0f,  // Position 1
				0.0f,   1.0f,   // TexCoord 1
				1.0f,   -1.0f,  // Position 2
				1.0f,   1.0f,   // TexCoord 2
				1.0f,   1.0f,   // Position 3
				1.0f,   0.0f    // TexCoord 3*/
				1.0f,   1.0f,   // Position 3
				1.0f,   1.0f,   // TexCoord 2
				-1.0f,  1.0f,   // Position 0
				1.0f,   0.0f,    // TexCoord 3
				-1.0f,  -1.0f,  // Position 1
				0.0f,   0.0f,   // TexCoord 0
				1.0f,   -1.0f,  // Position 2
				0.0f,   1.0f   // TexCoord 1
		};

		short[] indices = {0, 1, 2, 0, 2, 3};

		mesh.setVertices(vertices);
		mesh.setIndices(indices);
		if(createCamera()) {
			initCam();
			startCam();
			camera.addCallbackBuffer(image);
		}

	}

	public boolean createCamera() {
		if (camera != null) {
			return false;
		}
		try {
			camera = Camera.open(cameraID);
		} catch (Exception exception) {
			Log.i("CameraController", "Can,t open camera: " + cameraID + exception.getMessage());
			return false;
		}
		//camera.setPreviewCallbackWithBuffer(this);
		return true;
	}

	public void initCam() {
		Log.d("Lausi", "INIT:CAM");
		if (camera != null) {
			Log.d("Lausi", "in if!");
			camera.setPreviewCallbackWithBuffer(this);
			Camera.Parameters params = camera.getParameters();
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			List<Camera.Size> bla = params.getSupportedPreviewSizes();
			Log.d("Lausi","height"+bla.get(1).height);
			Log.d("Lausi","width"+bla.get(1).width);
			params.setPreviewSize(width, height);
			params.set("orientation", "portrait");

			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
				try {
					camera.setParameters(params);
					surface = new SurfaceTexture(100);
					camera.setPreviewTexture(surface);
				} catch (Exception e) {
					Log.d("Lausi", "Could not create surface!" + e.getMessage());
				}
			}

		}
		else {
			Log.d("Lausi", "ich glaub es hackt!");
		}
	}
	public void renderBackground() {
		//Copy the Y channel of the image into its buffer, the first (720*1280) bytes are the Y channel
		yBuffer.put(image, 0, width*height);
		yBuffer.position(0);

		//Copy the UV channels of the image into their buffer, the following (720*1280/2) bytes are the UV channel; the U and V bytes are interspread
		uvBuffer.put(image, width*height, (width*height)/2);
		uvBuffer.position(0);

		//Set texture slot 0 as active and bind our texture object to it
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		yTexture.bind();

		//Y texture is (720*1280) in size and each pixel is one byte; by setting GL_LUMINANCE, OpenGL puts this byte into R,G and B components of the texture
		Gdx.gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_LUMINANCE, width, height, 0, GL20.GL_LUMINANCE, GL20.GL_UNSIGNED_BYTE, yBuffer);
		//Gdx.gl.glEnable(GL20.GL_ALPHA);

		//Use linear interpolation when magnifying/minifying the texture to areas larger/smaller than the texture size
		Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR);
		Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);
		Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);
		Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);

		//Set texture slot 1 as active and bind our texture object to it
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
		uvTexture.bind();

		//UV texture is (720/2*1280/2) in size (downsampled by 2 in both dimensions, each pixel corresponds to 4 pixels of the Y channel)
		//and each pixel is two bytes. By setting GL_LUMINANCE_ALPHA, OpenGL puts first byte (V) into R,G and B components and of the texture
		//and the second byte (U) into the A component of the texture. That's why we find U and V at A and R respectively in the fragment shader code.
		//Note that we could have also found V at G or B as well.
		Gdx.gl.glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_LUMINANCE_ALPHA, width/2, height/2, 0, GL20.GL_LUMINANCE_ALPHA, GL20.GL_UNSIGNED_BYTE, uvBuffer);

		//Use linear interpolation when magnifying/minifying the texture to areas larger/smaller than the texture size
		Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR);
		Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);
		Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);
		Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);

		//Log.d("Lausi", "CAMERACONTROL begin 1");
		shader.begin();

		//Set the uniform y_texture object to the texture at slot 0
		shader.setUniformi("yk_texture", 0);

		//Set the uniform uv_texture object to the texture at slot 1
		shader.setUniformi("uv_texture", 1);
		mesh.bind(shader);
		//Render our mesh using the shader, which in turn will use our textures to render their content on the mesh
		mesh.render(shader, GL20.GL_TRIANGLES);
		mesh.unbind(shader);
		shader.end();
		shader.dispose();

		//yTexture.dispose();
		//uvTexture.dispose();
		//Log.d("Lausi", "CAMERACONTROL end 1");
	}

	public boolean isVideoRunning() {
		return videoSwitchedOn;
	}

	public void setVideoRunning(boolean on) {
		this.videoSwitchedOn = on;
	}

	public void setTransparency(String transparency) {
		this.transparency = transparency;
	}

	public synchronized void destroy() {
		Log.d("Lausi", "DESTROY");
		if (camera != null) {
			stopCam();
			camera.release();
		}
		camera = null;
	}

	public void pause() {
		Log.d("Lausi", "PAUSE");
		paused = true;
		stopCam();
	}

	public void resume() {
		Log.d("Lausi", "RESUME");
		if (paused) {
			startCam();
			paused = false;
		}
	}

	public Camera getCamera() {
		return camera;
	}

	public void updateParamsForLed(boolean on) {
		stopCam();
		Camera.Parameters params = camera.getParameters();
		if (on) {
			params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		} else {
			params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		}

		camera.setParameters(params);
		startCam();
	}

	private synchronized void startCam() {
		Log.d("Lausi", "STARTCAM");
		//if (! cameraStarted && camera != null) {

			Log.d("Lausi", "SET CB");
			//camera.setPreviewCallbackWithBuffer(this);
		surface.detachFromGLContext();
			camera.startPreview();
			cameraStarted = true;
		//}
	}

	private synchronized void stopCam() {
		Log.d("Lausi", "STOPCAM");
		if (cameraStarted && camera != null) {
			camera.stopPreview();
			Log.d("Lausi", "NULL CB");
			//camera.setPreviewCallbackWithBuffer(null);
			cameraStarted = false;
		}
	}
}
