import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;


/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain extends GameObject{

    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private float[] mySunlight;
    private MyTexture[] myTextures;
    
    private String textureFileName1 = "src/textures/grass.bmp";
    private String bmpExt = "bmp";
    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth, GameObject parent) {
    	super(parent);
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        mySunlight = new float[3];
    }
    
    public Terrain(int width, int depth) {
    	super(GameObject.ROOT);
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        mySunlight = new float[3];
    }
    public Terrain(Dimension size, GameObject parent) {
        this(size.width, size.height, parent);
    }

    public Dimension size() {
        return mySize;
    }

    public List<Tree> trees() {
        return myTrees;
    }

    public List<Road> roads() {
        return myRoads;
    }

    public float[] getSunlight() {
        return mySunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = dx;
        mySunlight[1] = dy;
        mySunlight[2] = dz;        
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * TO BE COMPLETED
     * 
     * Uses Bilinear Interpolation
     * Reference: http://supercomputingblog.com/graphics/coding-bilinear-interpolation/
     * 
     * 
     * @param x
     * @param z
     * @return
     */
    public double altitude(double x, double z) {
        double altitude = 0;
        int xInt = (int) x;
        int zInt = (int) z;
        if(xInt == x && zInt == z) {
        	altitude = getGridAltitude(xInt, zInt);
        }
        else {
	        int x1 = (int) Math.floor(x);
	        int x2 = (int) Math.ceil(x);
	        int z1 = (int) Math.floor(z);
	        int z2 = (int) Math.ceil(z);
	        
	        double r1 = (((x2 - x) / (x2 - x1)) * getGridAltitude(x1, z1));
	        r1 += (((x - x1) / (x2 - x1)) * getGridAltitude(x2, z1));
	        double r2 = (((x2 - x) / (x2 - x1)) * getGridAltitude(x1, z2));
	        r2 += (((x - x1) / (x2 - x1)) * getGridAltitude(x2, z2));
	        
	        double p = (((z2 - z) / (z2 - z1)) * r1);
	        p += (((z - z1) / (z2 - z1)) * r2);
	        
	        altitude = p;
        }
        return altitude;
    }
    public void initAll(GL2 gl) {
    	init(gl);
    	for(Tree t : this.myTrees) {
    		t.init(gl);
    	}
    	for(Road r : this.myRoads) {
    		r.init(gl);
    	}
    }
    public void init(GL2 gl) {
    	myTextures = new MyTexture[1];
    	myTextures[0] = new MyTexture(gl, textureFileName1, bmpExt,true);
    	
    }
    
    public void draw(GL2 gl) {
    	drawTerrain(gl);
    	drawTrees(gl);
    	drawRoads(gl);
    }
    public void drawTrees(GL2 gl) {
    	for(Tree t : this.myTrees) {
    		gl.glPushMatrix();
    		gl.glTranslated(t.getPosition().x,  t.getPosition().y,  t.getPosition().z);
    		t.draw(gl);
    		gl.glPopMatrix();
    	}
    }
    public void drawRoads(GL2 gl) {
    	for(Road r : this.myRoads) {
    		gl.glPushMatrix();
    		r.draw(gl);
    		gl.glPopMatrix();
    	}
    }
    public void drawTerrain(GL2 gl) {
    	 gl.glEnable(GL2.GL_TEXTURE_2D);
		 gl.glPushMatrix();
		 float matAmbAndDif1[] = {0.0f, 0.0f, 0.0f, 1.0f};
		 float matAmbAndDif2[] = {0.0f, 0.9f, 0.0f, 1.0f};
		 float matSpec[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		 float matShine[] = { 20.0f };
		
		 // Material properties
		 gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif2,0);
		 gl.glMaterialfv(GL2.GL_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, matAmbAndDif1,0);
		 gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, matSpec,0);
		 gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, matShine,0);
		 
		 float width = mySize.width;
		 float height = mySize.height;
		 float i,j; 
         //a grimy calculation for triangle mesh that works
        for (int x = 0; x+1 < width; x+=1.0) {
	        for (int z = 0; z+1 < height; z+=1.0) {
	        	//Corners for top left triangle
	            double[] botLeft = {x, myAltitude[x][z], z};
	            double[] topRight = {x+1, myAltitude[x+1][z+1], z+1};
	            double[] topLeft = {x+1, myAltitude[x+1][z], z};
	            double[] normals = getNormal(botLeft, topRight, topLeft);
		        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());
	            gl.glBegin(GL2.GL_TRIANGLES);{
	            	gl.glNormal3d(normals[0], normals[1], normals[2]);
	            	gl.glTexCoord2d(botLeft[0],botLeft[2]);
		        	gl.glVertex3d(botLeft[0],botLeft[1],botLeft[2]);
		        	gl.glNormal3d(normals[0], normals[1], normals[2]);
		        	gl.glTexCoord2d(topRight[0], topRight[2]);
		        	gl.glVertex3d(topRight[0],topRight[1],topRight[2]);
		        	gl.glNormal3d(normals[0], normals[1], normals[2]);
		        	gl.glTexCoord2d(topLeft[0], topLeft[2]);
		        	gl.glVertex3d(topLeft[0],topLeft[1],topLeft[2]);
	        	}gl.glEnd();   		
		        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());
		        //Corner for bottom right triangle
		        double[] botRight = {x, myAltitude[x][z+1],z+1};
		        normals = getNormal(botLeft, botRight, topRight);
		        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());
		        gl.glBegin(GL2.GL_TRIANGLES);{
		        	gl.glNormal3d(normals[0], normals[1], normals[2]);
		        	gl.glTexCoord2d(botLeft[0], botLeft[2]);
		        	gl.glVertex3d(botLeft[0], botLeft[1], botLeft[2]);
		        	gl.glNormal3d(normals[0], normals[1], normals[2]);
		        	gl.glTexCoord2d(botRight[0], botRight[2]);
		        	gl.glVertex3d(botRight[0], botRight[1], botRight[2]);
		        	gl.glNormal3d(normals[0], normals[1], normals[2]);
		        	gl.glTexCoord2d(topRight[0], topRight[2]);
		        	gl.glVertex3d(topRight[0], topRight[1], topRight[2]);
		        }
		        gl.glEnd();
		            	
		     }
                 
	     } 
         gl.glPopMatrix();
         gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL2.GL_FILL);	
    }
    
    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = altitude(x, z);
        Tree tree = new Tree(x, y, z, this);
        myTrees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        myRoads.add(road);        
    }
    /*Copied from week 4 Exercises */
    double [] cross(double u [], double v[]){
    	double crossProduct[] = new double[3];
    	crossProduct[0] = u[1]*v[2] - u[2]*v[1];
    	crossProduct[1] = u[2]*v[0] - u[0]*v[2];
    	crossProduct[2] = u[0]*v[1] - u[1]*v[0];
    	
    	return crossProduct;
    }
    
    //Assumes correct winding order
    double [] getNormal(double[] p0, double[] p1, double[] p2){
    	double u[] = {p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2]};
    	double v[] = {p2[0] - p0[0], p2[1] - p0[1], p2[2] - p0[2]}; 	
    	return cross(u,v);   	
    }

}
