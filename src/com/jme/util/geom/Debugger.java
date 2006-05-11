/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.util.geom;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.OrientedBox;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;

/**
 * <code>Debugger</code> provides tools for viewing scene data such as
 * boundings and normals.
 * 
 * @author Joshua Slack
 * @author Emond Papegaaij (normals ideas and previous normal tool)
 * @version $Id: Debugger.java,v 1.19 2006-05-11 19:39:40 nca Exp $
 */
public final class Debugger {

    // -- **** METHODS FOR DRAWING BOUNDING VOLUMES **** -- //
    
    private static final Sphere boundingSphere = new Sphere("bsphere", 10, 10, 1);
    static { 
        boundingSphere.setRenderQueueMode(Renderer.QUEUE_SKIP);
    }
    private static final Box boundingBox = new Box("bbox", new Vector3f(), 1, 1, 1);
    static { 
        boundingBox.setRenderQueueMode(Renderer.QUEUE_SKIP);
    }
    private static final OrientedBox boundingOB = new OrientedBox("bobox");
    static { 
        boundingOB.setRenderQueueMode(Renderer.QUEUE_SKIP);
    }
    
    private static WireframeState boundsWireState;
    private static ZBufferState boundsZState;

    /**
     * <code>drawBounds</code> draws the bounding volume for a given Spatial
     * and its children.
     * 
     * @param spat
     *            the Spatial to draw boundings for.
     * @param r
     *            the Renderer to use to draw the bounding.
     */
    public static void drawBounds(Spatial spat, Renderer r) {
        drawBounds(spat, r, true);
    }
    
    /**
     * <code>drawBounds</code> draws the bounding volume for a given Spatial
     * and optionally its children.
     * 
     * @param spat
     *            the Spatial to draw boundings for.
     * @param r
     *            the Renderer to use to draw the bounding.
     * @param doChildren
     *            if true, boundings for any children will also be drawn
     */
    public static void drawBounds(Spatial spat, Renderer r, boolean doChildren) {
        if (spat == null) return;

        if (boundsWireState == null) {
            boundsWireState = r.createWireframeState();
            boundsZState = r.createZBufferState();
            boundingBox.setRenderState(boundsWireState);
            boundingBox.setRenderState(boundsZState);
            boundingBox.updateRenderState();
            boundingOB.setRenderState(boundsWireState);
            boundingOB.setRenderState(boundsZState);
            boundingOB.updateRenderState();
            boundingSphere.setRenderState(boundsWireState);
            boundingSphere.setRenderState(boundsZState);
            boundingSphere.updateRenderState();
        }
        
        if (spat.getWorldBound() != null && spat.getCullMode() != Spatial.CULL_ALWAYS) {
            int state = r.getCamera().getPlaneState();
            if (r.getCamera().contains(spat.getWorldBound()) != Camera.OUTSIDE_FRUSTUM)
                drawBounds(spat.getWorldBound(), r);
            else
                doChildren = false;
            r.getCamera().setPlaneState(state);
        }
        if (doChildren && (spat.getType() & Spatial.NODE) != 0) {
            Node n = (Node)spat;
            if (n.getChildren() != null) {
                for (int i = n.getChildren().size(); --i >= 0; )
                    drawBounds(n.getChild(i), r, true);
            }
        }
    }
    
    private static void drawBounds(BoundingVolume bv, Renderer r) {

        switch (bv.getType()) {
            case BoundingVolume.BOUNDING_BOX:
                drawBoundingBox((BoundingBox) bv, r);
                break;
            case BoundingVolume.BOUNDING_SPHERE:
                drawBoundingSphere((BoundingSphere) bv, r);
                break;
            case BoundingVolume.BOUNDING_OBB:
                drawOBB((OrientedBoundingBox) bv, r);
                break;
            default:
                break;
        }
    }

    public static void setBoundsColor(ColorRGBA color) {
        boundingBox.setSolidColor(color);
        boundingOB.setSolidColor(color);
        boundingSphere.setSolidColor(color);
    }
    
    private static void drawBoundingSphere(BoundingSphere sphere, Renderer r) {
        boundingSphere.getCenter().set(sphere.getCenter());
        boundingSphere.setData(boundingSphere.getCenter(), 10, 10, sphere.getRadius()); // pass back bs center to prevent accidently data access.
        boundingSphere.draw(r);
    }

    private static void drawBoundingBox(BoundingBox box, Renderer r) {
        boundingBox.getCenter().set(box.getCenter());
        boundingBox.setData(boundingBox.getCenter(), box.xExtent, box.yExtent, box.zExtent); 
        boundingBox.draw(r);
    }

    private static void drawOBB(OrientedBoundingBox box, Renderer r) {
        boundingOB.getCenter().set(box.getCenter());
        boundingOB.getxAxis().set(box.getXAxis());
        boundingOB.getyAxis().set(box.getYAxis());
        boundingOB.getzAxis().set(box.getZAxis());
        boundingOB.getExtent().set(box.getExtent());
        boundingOB.computeInformation();
        boundingOB.draw(r);
    }
    
    // -- **** METHODS FOR DRAWING NORMALS **** -- //
    

    private static final Line normalLines = new Line("normLine");
    static {
        normalLines.setLineWidth(3.0f);
        normalLines.setMode(Line.SEGMENTS);
        normalLines.setVertexBuffer(0, BufferUtils.createVector3Buffer(500));
    }
    private static final Vector3f _normalVect = new Vector3f();
    private static ZBufferState normZState;
    public static ColorRGBA NORMAL_COLOR = ColorRGBA.red;
    public static BoundingBox measureBox = new BoundingBox();
    public static float AUTO_NORMAL_RATIO = .05f;
    
    /**
     * <code>drawNormals</code> draws lines representing normals for a given Spatial
     * and its children.
     * 
     * @param spat
     *            the Spatial to draw normals for.
     * @param r
     *            the Renderer to use to draw the normals.
     */
    public static void drawNormals(Spatial spat, Renderer r) {
        drawNormals(spat, r, -1f, true);
    }

    /**
     * <code>drawNormals</code> draws the normals for a given Spatial
     * and optionally its children.
     * 
     * @param spat
     *            the Spatial to draw normals for.
     * @param r
     *            the Renderer to use to draw the normals.
     * @param size
     *            the length of the drawn normal (default is -1.0f which means autocalc based on boundings - if any).
     * @param doChildren
     *            if true, normals for any children will also be drawn
     */
    public static void drawNormals(Spatial spat, Renderer r, float size, boolean doChildren) {
        if (spat == null) return;

        if (normZState == null) {
            normZState = r.createZBufferState();
            normalLines.setRenderState(normZState); 
            normalLines.updateRenderState();
        }
        
        int state = r.getCamera().getPlaneState();
        if (spat.getWorldBound() != null && r.getCamera().contains(spat.getWorldBound()) == Camera.OUTSIDE_FRUSTUM) {
            r.getCamera().setPlaneState(state);
            return;
        }
        float rSize = size;
        if (rSize == -1) {
            BoundingVolume vol = spat.getWorldBound(); 
            if (vol != null) {
                measureBox.setCenter(vol.getCenter());
                measureBox.xExtent = 0;
                measureBox.yExtent = 0;
                measureBox.zExtent = 0;
                measureBox.mergeLocal(vol);
                rSize = AUTO_NORMAL_RATIO * ((measureBox.xExtent + measureBox.yExtent + measureBox.zExtent) / 3f);
            } else
                rSize = 1.0f;
        }
        r.getCamera().setPlaneState(state);
        if ((spat.getType() & Spatial.GEOMETRY) != 0 && spat.getCullMode() != Spatial.CULL_ALWAYS) {
            Geometry g = (Geometry)spat;
            for(int i = 0; i < g.getBatchCount(); i++) {
                GeomBatch batch = g.getBatch(i);

                FloatBuffer norms = batch.getNormalBuffer();
                FloatBuffer verts = batch.getVertexBuffer();
                if (norms != null && verts != null  && norms.capacity() == verts.capacity()) {
                    FloatBuffer lineVerts = normalLines.getVertexBuffer(0);
                    if (lineVerts.capacity() < (3 * (2 * batch.getVertexCount()))) {
                        normalLines.setVertexBuffer(0, null);
                        System.gc();
                        lineVerts = BufferUtils.createVector3Buffer(batch.getVertexCount() * 2);
                        normalLines.setVertexBuffer(0, lineVerts);
                    } else {
                        normalLines.getBatch(0).setVertexCount(2 * batch.getVertexCount());
                        lineVerts.clear();
                    }
                    IntBuffer lineInds = normalLines.getIndexBuffer();
                    if (lineInds == null || lineInds.capacity() < (normalLines.getBatch(0).getVertexCount())) {
                        normalLines.setIndexBuffer(null);
                        System.gc();
                        lineInds = BufferUtils.createIntBuffer(batch.getVertexCount() * 2);
                        normalLines.setIndexBuffer(lineInds);
                    } else {
                        lineInds.clear();
                        lineInds.limit(normalLines.getBatch(0).getVertexCount());
                    }
                    
                    verts.rewind();
                    norms.rewind();
                    lineVerts.rewind();
                    lineInds.rewind();
                    
                    for (int x = 0; x < batch.getVertexCount(); x++ ) {
                        _normalVect.set(verts.get(), verts.get(), verts.get());
                        lineVerts.put(_normalVect.x);
                        lineVerts.put(_normalVect.y);
                        lineVerts.put(_normalVect.z);
                        lineInds.put(x*2);
                        
                        _normalVect.addLocal(norms.get()*rSize, norms.get()*rSize, norms.get()*rSize);
                        lineVerts.put(_normalVect.x);
                        lineVerts.put(_normalVect.y);
                        lineVerts.put(_normalVect.z);
                        lineInds.put((x*2)+1);
                    }
                    
                    normalLines.setDefaultColor(NORMAL_COLOR);
                    normalLines.setLocalTranslation(batch.getWorldTranslation());
                    normalLines.setLocalScale(batch.getWorldScale());
                    normalLines.setLocalRotation(batch.getWorldRotation());
                    normalLines.onDraw(r);
                }
            }
            
        }
        
        if (doChildren && (spat.getType() & Spatial.NODE) != 0) {
            Node n = (Node)spat;
            if (n.getChildren() != null) {
                for (int i = n.getChildren().size(); --i >= 0; )
                    drawNormals(n.getChild(i), r, size, true);
            }
        }
    }

    // -- **** METHODS FOR DISPLAYING BUFFERS **** -- //
    public static final int NORTHWEST = 0;
    public static final int NORTHEAST = 1;
    public static final int SOUTHEAST = 2;
    public static final int SOUTHWEST = 3;
    
    private static final Quad bQuad = new Quad("", 128, 128);
    private static Texture bufTexture;
    private static TextureRenderer bufTexRend;
    
    static {
        bQuad.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        bQuad.setCullMode(Spatial.CULL_NEVER);
    }
    
    public static void drawBuffer(int rttSource, int location, Renderer r) {
        drawBuffer(rttSource, location, r, r.getWidth() / 6.25f);
    }


    public static void drawBuffer(int rttSource, int location, Renderer r, float size) {
        r.flush();
        float locationX = r.getWidth(), locationY = r.getHeight();
        bQuad.resize(size, (r.getHeight() / (float)r.getWidth()) * size);
        if (bQuad.getRenderState(RenderState.RS_TEXTURE) == null) {
            TextureState ts = r.createTextureState();
            bufTexture = new Texture();
            ts.setTexture(bufTexture);
            bQuad.setRenderState(ts);
            bQuad.updateRenderState();
        }
        
        bufTexture.setRTTSource(rttSource);
        
        if (bufTexRend == null) {
            bufTexRend = DisplaySystem.getDisplaySystem()
                    .createTextureRenderer(256, 256, false,
                            true, false, false,
                            TextureRenderer.RENDER_TEXTURE_2D, 0);
            bufTexRend.setupTexture(bufTexture);
        }
        int width = r.getWidth();
        if (!FastMath.isPowerOfTwo(width)) {
            int newWidth = 2;
            do {
                newWidth <<= 1;

            } while (newWidth < width);
            bQuad.getTextureBuffer(0, 0).put(4, width / (float)newWidth);
            bQuad.getTextureBuffer(0, 0).put(6, width / (float)newWidth);
            width = newWidth;
        }

        int height = r.getHeight();
        if (!FastMath.isPowerOfTwo(height)) {
            int newHeight = 2;
            do {
                newHeight <<= 1;

            } while (newHeight < height);
            bQuad.getTextureBuffer(0, 0).put(1, height / (float)newHeight);
            bQuad.getTextureBuffer(0, 0).put(7, height / (float)newHeight);
            height = newHeight;
        }

        bufTexRend.copyToTexture(bufTexture, width, height);

        float loc = size*.75f;
        switch (location) {
        case NORTHWEST:
            locationX = loc;
            locationY -= loc;
            break;
        case NORTHEAST:
            locationX -= loc;
            locationY -= loc;
            break;
        case SOUTHEAST:
            locationX -= loc;
            locationY = loc;
            break;
        case SOUTHWEST:
        default:
            locationX = loc;
            locationY = loc;
            break;
        }

        bQuad.getWorldTranslation().set(locationX, locationY, 0);
        
        bQuad.onDraw(r);
        r.flush();
    }
}
