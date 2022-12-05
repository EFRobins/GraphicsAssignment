import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
  
public class M01_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
    
  public M01_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(4f,6f,15f));
    this.camera.setTarget(new Vec3(0f,5f,0f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime = getSeconds();
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory, if necessary */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    disposeModels(gl);
  }

  // ***************************************************
  /* THE SCENE
   * Now define all the methods to handle the scene.
   * This will be added to in later examples.
   */
   
  private Camera camera;
  private Model floor,leftWall, rightWall, sphere, 
   nearRightLeg, nearLeftLeg, farRightLeg, farLeftLeg, tableTop;
  private Light light;
  //private SGNode twoBranchRoot;

  private void disposeModels(GL3 gl) {
    floor.dispose(gl);
    nearRightLeg.dispose(gl);
    sphere.dispose(gl);
    light.dispose(gl);
    rightWall.dispose(gl);
    leftWall.dispose(gl);
    farRightLeg.dispose(gl);
    farLeftLeg.dispose(gl);
    tableTop.dispose(gl);

    //twoBranchRoot.dispose(gl);
  }
  
  public void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/brick_wall.jpg");

    light = new Light(gl);
    light.setCamera(camera);
    
    Mesh m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    Material material = new Material(new Vec3(0.0f, 0.5f, 0.81f), new Vec3(0.0f, 0.5f, 0.81f), new Vec3(1.0f, 1.0f, 1.0f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16,1f,16);
    floor = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId0);
  
    m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.7f, 0.4f, 0.4f), new Vec3(0.7f, 0.5f, 0.3f), new Vec3(0.7f, 0.5f, 0.3f), 16.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90f), Mat4Transform.scale(16,1,16));
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-8.0f,8.0f,0.0f),modelMatrix);
    leftWall = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId5);

    m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.7f, 0.4f, 0.4f), new Vec3(0.7f, 0.5f, 0.3f), new Vec3(0.7f, 0.5f, 0.3f), 16.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(90f), Mat4Transform.scale(16,1,16));
    modelMatrix = Mat4.multiply(Mat4Transform.translate(8.0f,8.0f,0.0f),modelMatrix);
    rightWall = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId5);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,3.7f,0.5f), Mat4Transform.translate(4f,0.5f,4f));
    nearRightLeg = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1, textureId2);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,3.7f,0.5f), Mat4Transform.translate(-4f,0.5f,4f));
    nearLeftLeg = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1, textureId2);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,3.7f,0.5f), Mat4Transform.translate(4f,0.5f,-4f));
    farRightLeg = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1, textureId2);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(0.5f,3.7f,0.5f), Mat4Transform.translate(-4f,0.5f,-4f));
    farLeftLeg = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1, textureId2);

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4.5f,0.3f,4.5f), Mat4Transform.translate(0.0f,12.3f,-0.0f));
    tableTop = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1, textureId2);

    m = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_sphere_04.txt", "fs_sphere_04.txt");
    
    // no texture version 
    //shader = new Shader(gl, "vs_sphere_04.txt", "fs_sphere_04_notex.txt");
    
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(3,4,3), Mat4Transform.translate(0,0.5f,0));
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,4,0), modelMatrix);
    
    sphere = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId3, textureId4);
    
    // no texture version
    // sphere = new Model(gl, camera, light, shader, material, modelMatrix, m); 
  }
 
  private void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    
    light.setPosition(getLightPosition());  // changing light position each frame
    light.render(gl);

    floor.render(gl);
    sphere.render(gl);
    rightWall.render(gl);
    leftWall.render(gl);
    nearRightLeg.render(gl);
    nearLeftLeg.render(gl);
    farRightLeg.render(gl);
    farLeftLeg.render(gl);
    tableTop.render(gl);
  }

  // The light's postion is continually being changed, so needs to be calculated for each frame.
  private Vec3 getLightPosition() {
    double elapsedTime = getSeconds()-startTime;
    float x = 5.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 2.7f;
    float z = 5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);
    
    //return new Vec3(5f,3.4f,5f);  // use to set in a specific position for testing
  }
  
    // ***************************************************
  /* TIME
   */ 
  
  private double startTime;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }

  // ***************************************************
  /* An array of random numbers
   */ 
  
  private int NUM_RANDOMS = 1000;
  private float[] randoms;
  
  private void createRandomNumbers() {
    randoms = new float[NUM_RANDOMS];
    for (int i=0; i<NUM_RANDOMS; ++i) {
      randoms[i] = (float)Math.random();
    }
  }
  
  
}