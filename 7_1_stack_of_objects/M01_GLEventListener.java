import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;
import java.io.File;
import java.io.FileInputStream;
  
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
    //gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    //gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled'
    //gl.glCullFace(GL.GL_BACK);     // default is 'back', assuming CCW 
    gl.glDisable(GL.GL_CULL_FACE);
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
  private Model floor,leftWall, rightWall, backWall, sphere, nearRightLeg, nearLeftLeg, farRightLeg, farLeftLeg, tableTop, skybox,base, bottomLong,joint, topLong, lampTop ;
  private Light light, lamp1;
  
  private SGNode twoBranchRoot;
  private TransformNode translateAll, rotateAll,translateFirstBranch, rotateFirstBranch, translateSecondBranch, 
          rotateSecondBranch, translateThirdBranch, rotateThirdBranch, translateFourthBranch, 
          rotateFourthBranch,translateFifthBranch, rotateFifthBranch;  
  
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
    backWall.dispose(gl);
  }
  /* 
  public int[] loadCubemap(GL3 gl){
    //IntBuffer[] textureID = new IntBuffer[0];
    int[] textureID = new int[1];
    ByteBuffer data;
    gl.glGenTextures(1, textureID, 0);
    gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, textureID[0]);

    int width, height, nrChannels;
    for (int i = 0; i < 6; i++)
    {
        String[] files = {"textures/skybox0.jpg","textures/skybox1.jpg","textures/skybox2.jpg","textures/skybox3.jpg","textures/skybox4.jpg","textures/skybox5.jpg"};
        File f = new File("textures/skybox0.jpg");
        FileInputStream F = new FileInputStream(f);      
        JPEGImage img = JPEGImage.read(F);
        data = img.getData();
        gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL.GL_RGB, img.getWidth(), img.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, data);
    }
    gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
    gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
    gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
    gl.glTexParameteri(GL.GL_TEXTURE_CUBE_MAP, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);

    return textureID;
}  

  
*/
    /*  
  int *data;
  public void drawSkybox(GL3 gl){
  int[][] skyboxTextures; 
  int width,height;
  String data;
    for(int i=0; i<6; i++){
      skyboxTextures[i] = TextureLibrary.loadTexture(gl, "textures/skybox"+ Integer.toString(i) + ".jpg");};

      for(int i=0; i<6; i++){
        System.out.println(skyboxTextures[i]);
        System.out.println(skyboxTextures[i]);
        gl.glTexImage2D(GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, width,height,0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,(double)skyboxTextures[i]);
      };
    gl.glGenTextures(1, skyboxTextures);
    gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, skyboxTextures);

    int width, height, nrChannels;
    char data;  
    for(int i = 0; i < textures_faces.size(); i++)
    {
        data = stbi_load(Integer.toString(skyboxTextures[i]), width, height, nrChannels, 0);
        gl.glTexImage2D(
            GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 
            0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data
        );
  }
*/


  public void initialise(GL3 gl) {
    createRandomNumbers();
    int[] textureId0 = TextureLibrary.loadTexture(gl, "textures/chequerboard.jpg");
    int[] textureId1 = TextureLibrary.loadTexture(gl, "textures/container2.jpg");
    int[] textureId2 = TextureLibrary.loadTexture(gl, "textures/container2_specular.jpg");
    int[] textureId3 = TextureLibrary.loadTexture(gl, "textures/jade.jpg");
    int[] textureId4 = TextureLibrary.loadTexture(gl, "textures/jade_specular.jpg");
    int[] textureId5 = TextureLibrary.loadTexture(gl, "textures/brick_wall.jpg");
    int[] textureId6 = TextureLibrary.loadTexture(gl, "textures/skybox.jpg");

    light = new Light(gl);
    light.setCamera(camera);
    
    lamp1 = new Light(gl);
    lamp1.setCamera(camera);

    Mesh m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    Material material = new Material(new Vec3(1f, 1f, 1f), new Vec3(1f, 1f, 1f), new Vec3(1.0f, 1.0f, 1.0f), 32.0f);
    Mat4 modelMatrix = Mat4Transform.scale(16,1f,32);
    floor = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId0);
     
    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4Transform.scale(100f,100f,100f);
    skybox = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId6);
    
    m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.7f, 0.4f, 0.4f), new Vec3(0.7f, 0.5f, 0.3f), new Vec3(0.7f, 0.5f, 0.3f), 8.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90f), Mat4Transform.scale(32,1,16));
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-8.0f,0.0f,-8.0f),modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90),modelMatrix);
    leftWall = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId5);

    m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.7f, 0.4f, 0.4f), new Vec3(0.7f, 0.5f, 0.3f), new Vec3(0.7f, 0.5f, 0.3f), 8.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(90f), Mat4Transform.scale(32,1,16));
    modelMatrix = Mat4.multiply(Mat4Transform.translate(8.0f,0.0f,8.0f),modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(-90),modelMatrix);
    rightWall = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId5);
    
    m = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    shader = new Shader(gl, "vs_tt_05.txt", "fs_tt_05.txt");
    material = new Material(new Vec3(0.7f, 0.4f, 0.4f), new Vec3(0.7f, 0.5f, 0.3f), new Vec3(0.7f, 0.5f, 0.3f), 8.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90f), Mat4Transform.scale(16,1,16));
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(180),modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0.0f,8.0f,16.0f),modelMatrix);
    backWall = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId5);

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
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(3,4,3), Mat4Transform.translate(0,0.5f,0));
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,4,0), modelMatrix);
    sphere = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId3, textureId4);
    // no texture version 
    //shader = new Shader(gl, "vs_sphere_04.txt", "fs_sphere_04_notex.txt");
    modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4),Mat4Transform.translate(0,0.5f,0));    

    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    base = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1, textureId2);

    m = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_sphere_04.txt", "fs_sphere_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    bottomLong = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId3, textureId4);

    m = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_sphere_04.txt", "fs_sphere_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    joint = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId3, textureId4);
    
    m = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
    shader = new Shader(gl, "vs_sphere_04.txt", "fs_sphere_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    topLong = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId3, textureId4);
     
    m = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
    shader = new Shader(gl, "vs_cube_04.txt", "fs_cube_04.txt");
    material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
    lampTop = new Model(gl, camera, light, shader, material, modelMatrix, m, textureId1, textureId2);

    int rotateAllAngle = 0;
    int angle = 45;
    twoBranchRoot = new SGNode("two-branch structure");
    
    //mat = Mat4.multiply(mat, ); 
      translateAll = new TransformNode("translate(3,0,6)", Mat4Transform.translate(3.0f,0.0f,6.0f));
      rotateAll = new TransformNode("rotateAroundZ("+45+")", Mat4Transform.rotateAroundX(0));
      
        //First Branch Base
        NameNode FirstBranch = new NameNode("First");
        Mat4 mat = Mat4Transform.translate(0,0.05f,0.0f); 
        mat = Mat4.multiply(mat, Mat4Transform.scale(2.5f,0.1f,2.5f));
        translateFirstBranch = new TransformNode("translate(0,0,0)",mat);
        rotateFirstBranch = new TransformNode("rotateAroundX("+0+")", Mat4Transform.rotateAroundY(30.0f));
        ModelNode baseModel = new ModelNode("Base", base);

        //Second Branch 1st Long sphere
        NameNode SecondBranch = new NameNode("Second");
        mat = Mat4Transform.translate(0.0f,2.5f,0.0f);
        mat = Mat4.multiply(mat,Mat4Transform.scale(0.3f,5.0f,0.30f));
        translateSecondBranch = new TransformNode("translate(0,0.3,0)",mat);
        rotateSecondBranch = new TransformNode("rotateAroundX("+35+")", Mat4Transform.rotateAroundX(15.0f));
        ModelNode bottomModel = new ModelNode("Bottom Long", bottomLong);

        //Third Branch
        NameNode ThirdBranch = new NameNode("Third");
        mat = Mat4Transform.translate(0.0f,4.7f,0.0f);
        mat = Mat4.multiply(mat,Mat4Transform.scale(0.6f,0.6f,0.6f) );
        translateThirdBranch = new TransformNode("translate(0.5,0.5,0.5)",mat);
        rotateThirdBranch = new TransformNode("rotateAroundX("+0+")", Mat4Transform.rotateAroundX(0.0f));
        ModelNode jointModel = new ModelNode("Joint", joint);

        //Fourth Branch
        NameNode FourthBranch = new NameNode("Fourth");
        mat = Mat4Transform.translate(0.0f,5.3f,3.6f);
        mat = Mat4.multiply(mat,Mat4Transform.scale(0.3f,5.0f,0.3f));
        translateFourthBranch = new TransformNode("translate(0,0,0)", mat);
        rotateFourthBranch = new TransformNode("rotateAroundX("+0+")", Mat4Transform.rotateAroundX(-50.0f));
        ModelNode topModel = new ModelNode("TopLong", topLong);
        
        //Fifth Branch
        NameNode FifthBranch = new NameNode("Fifth");
        mat = Mat4Transform.translate(0.0f,7.7f,2.5f);
        mat = Mat4.multiply(mat,Mat4Transform.scale(1.0f,0.3f,2.5f));
        translateFifthBranch = new TransformNode("translate(0,0,0)",mat);
        rotateFifthBranch = new TransformNode("rotateAroundZ("+0+")", Mat4Transform.rotateAroundZ(rotateAllAngle));
        ModelNode lampTopModel = new ModelNode("Lamp Top", lampTop);

    twoBranchRoot.addChild(rotateAll);
    rotateAll.addChild(translateAll);
    translateAll.addChild(FirstBranch);

    FirstBranch.addChild(rotateFirstBranch);
    rotateFirstBranch.addChild(translateFirstBranch);
    translateFirstBranch.addChild(baseModel);
    rotateFirstBranch.addChild(SecondBranch);

    SecondBranch.addChild(rotateSecondBranch);
    rotateSecondBranch.addChild(translateSecondBranch);
    translateSecondBranch.addChild(bottomModel);
    rotateSecondBranch.addChild(ThirdBranch);
     
    ThirdBranch.addChild(rotateThirdBranch);
    rotateThirdBranch.addChild(translateThirdBranch);
    translateThirdBranch.addChild(jointModel);
    rotateThirdBranch.addChild(FourthBranch);
    
    FourthBranch.addChild(rotateFourthBranch);
    rotateFourthBranch.addChild(translateFourthBranch);
    translateFourthBranch.addChild(topModel);
    rotateFourthBranch.addChild(FifthBranch);
  
    rotateFifthBranch.addChild(translateFifthBranch);
    translateFifthBranch.addChild(lampTopModel);
    FifthBranch.addChild(rotateFifthBranch);
    


    twoBranchRoot.update();
    twoBranchRoot.print(0,false);
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
    backWall.render(gl);
    nearRightLeg.render(gl);
    nearLeftLeg.render(gl);
    farRightLeg.render(gl);
    farLeftLeg.render(gl);
    tableTop.render(gl);
    skybox.render(gl);
    twoBranchRoot.draw(gl);
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