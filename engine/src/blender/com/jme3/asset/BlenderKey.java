/*
 * Copyright (c) 2009-2010 jMonkeyEngine
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
package com.jme3.asset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.plugins.ogre.AnimData;
import com.jme3.texture.Texture;

/**
 * Blender key. Contains path of the blender file and its loading properties.
 * @author Marcin Roguski
 */
public class BlenderKey extends ModelKey {
	protected static final int					DEFAULT_FPS				= 25;

	/**
	 * Animation definitions. The key is the object name that owns the animation. The value is a map between animation
	 * name and its start and stop frames. Blender stores a pointer for animation within object. Therefore one object
	 * can only have one animation at the time. We want to be able to switch between animations for one object so we
	 * need to map the object name to animation names the object will use.
	 */
	protected Map<String, Map<String, int[]>>	animations;
	/**
	 * FramesPerSecond parameter describe how many frames there are in each second. It allows to calculate the time
	 * between the frames.
	 */
	protected int								fps						= DEFAULT_FPS;
	/** Width of generated textures (in pixels). Blender uses 140x140 by default. */
	protected int								generatedTextureWidth	= 140;
	/** Height of generated textures (in pixels). Blender uses 140x140 by default. */
	protected int								generatedTextureHeight	= 140;
	/**
	 * This variable is a bitwise flag of FeatureToLoad interface values; By default everything is being loaded.
	 */
	protected int								featuresToLoad			= FeaturesToLoad.ALL;
	/** The root path for all the assets. */
	protected String							assetRootPath;
	/** This variable indicate if Y axis is UP axis. If not then Z is up. By default set to true. */
	protected boolean							fixUpAxis				= true;
	/**
	 * The name of world settings that the importer will use. If not set or specified name does not occur in the file
	 * then the first world settings in the file will be used.
	 */
	protected String							usedWorld;
	/**
	 * User's default material that is set fo objects that have no material definition in blender. The default value is
	 * null. If the value is null the importer will use its own default material (gray color - like in blender).
	 */
	protected Material							defaultMaterial;
	/** Face cull mode. By default it is disabled. */
	protected FaceCullMode						faceCullMode			= FaceCullMode.Off;

	/**
	 * Constructor used by serialization mechanisms.
	 */
	public BlenderKey() {}

	/**
	 * Constructor. Creates a key for the given file name.
	 * @param name
	 *        the name (path) of a file
	 */
	public BlenderKey(String name) {
		super(name);
	}

	/**
	 * This method adds an animation definition. If a definition already eixists in the key then it is replaced.
	 * @param objectName
	 *        the name of animation's owner
	 * @param name
	 *        the name of the animation
	 * @param start
	 *        the start frame of the animation
	 * @param stop
	 *        the stop frame of the animation
	 */
	public synchronized void addAnimation(String objectName, String name, int start, int stop) {
		if(objectName == null) {
			throw new IllegalArgumentException("Object name cannot be null!");
		}
		if(name == null) {
			throw new IllegalArgumentException("Animation name cannot be null!");
		}
		if(start > stop) {
			throw new IllegalArgumentException("Start frame cannot be greater than stop frame!");
		}
		if(animations == null) {
			animations = new HashMap<String, Map<String, int[]>>();
			animations.put(objectName, new HashMap<String, int[]>());
		}
		Map<String, int[]> objectAnimations = animations.get(objectName);
		if(objectAnimations == null) {
			objectAnimations = new HashMap<String, int[]>();
			animations.put(objectName, objectAnimations);
		}
		objectAnimations.put(name, new int[] {start, stop});
	}

	/**
	 * This method returns the animation frames boundaries.
	 * @param objectName
	 *        the name of animation's owner
	 * @param name
	 *        animation name
	 * @return animation frame boundaries in a table [start, stop] or null if animation of the given name does not
	 *         exists
	 */
	public int[] getAnimationFrames(String objectName, String name) {
		Map<String, int[]> objectAnimations = animations == null ? null : animations.get(objectName);
		int[] frames = objectAnimations == null ? null : objectAnimations.get(name);
		return frames == null ? null : frames.clone();
	}

	/**
	 * This method returns the animation names for the given object name.
	 * @param objectName
	 *        the name of the object
	 * @return an array of animations for this object
	 */
	public Set<String> getAnimationNames(String objectName) {
		Map<String, int[]> objectAnimations = animations == null ? null : animations.get(objectName);
		return objectAnimations == null ? null : objectAnimations.keySet();
	}

	/**
	 * This method returns the animations map.
	 * @return the animations map
	 */
	public Map<String, Map<String, int[]>> getAnimations() {
		return animations;
	}

	/**
	 * This method returns frames per second amount. The default value is BlenderKey.DEFAULT_FPS = 25.
	 * @return the frames per second amount
	 */
	public int getFps() {
		return fps;
	}

	/**
	 * This method sets frames per second amount.
	 * @param fps
	 *        the frames per second amount
	 */
	public void setFps(int fps) {
		this.fps = fps;
	}

	/**
	 * This method sets the width of generated texture (in pixels). By default the value is 140 px.
	 * @param generatedTextureWidth
	 *        the width of generated texture
	 */
	public void setGeneratedTextureWidth(int generatedTextureWidth) {
		this.generatedTextureWidth = generatedTextureWidth;
	}

	/**
	 * This method returns the width of generated texture (in pixels). By default the value is 140 px.
	 * @return the width of generated texture
	 */
	public int getGeneratedTextureWidth() {
		return generatedTextureWidth;
	}

	/**
	 * This method sets the height of generated texture (in pixels). By default the value is 140 px.
	 * @param generatedTextureHeight
	 *        the height of generated texture
	 */
	public void setGeneratedTextureHeight(int generatedTextureHeight) {
		this.generatedTextureHeight = generatedTextureHeight;
	}

	/**
	 * This method returns the height of generated texture (in pixels). By default the value is 140 px.
	 * @return the height of generated texture
	 */
	public int getGeneratedTextureHeight() {
		return generatedTextureHeight;
	}

	/**
	 * This method returns the face cull mode.
	 * @return the face cull mode
	 */
	public FaceCullMode getFaceCullMode() {
		return faceCullMode;
	}

	/**
	 * This method sets the face cull mode.
	 * @param faceCullMode
	 *        the face cull mode
	 */
	public void setFaceCullMode(FaceCullMode faceCullMode) {
		this.faceCullMode = faceCullMode;
	}

	/**
	 * This method sets the asset root path.
	 * @param assetRootPath
	 *        the assets root path
	 */
	public void setAssetRootPath(String assetRootPath) {
		this.assetRootPath = assetRootPath;
	}

	/**
	 * This method returns the asset root path.
	 * @return the asset root path
	 */
	public String getAssetRootPath() {
		return assetRootPath;
	}

	/**
	 * This method adds features to be loaded.
	 * @param featuresToLoad
	 *        bitwise flag of FeaturesToLoad interface values
	 */
	public void includeInLoading(int featuresToLoad) {
		this.featuresToLoad |= featuresToLoad;
	}

	/**
	 * This method removes features from being loaded.
	 * @param featuresToLoad
	 *        bitwise flag of FeaturesToLoad interface values
	 */
	public void excludeFromLoading(int featuresNotToLoad) {
		this.featuresToLoad &= ~featuresNotToLoad;
	}

	/**
	 * This method returns bitwise value of FeaturesToLoad interface value. It describes features that will be loaded by
	 * the blender file loader.
	 * @return features that will be loaded by the blender file loader
	 */
	public int getFeaturesToLoad() {
		return featuresToLoad;
	}

	/**
	 * This method creates an object where loading results will be stores. Only those features will be allowed to store
	 * that were specified by features-to-load flag.
	 * @return an object to store loading results
	 */
	public LoadingResults prepareLoadingResults() {
		return new LoadingResults(featuresToLoad);
	}

	/**
	 * This method sets the fix up axis state. If set to true then Y is up axis. Otherwise the up i Z axis. By default Y
	 * is up axis.
	 * @param fixUpAxis
	 *        the up axis state variable
	 */
	public void setFixUpAxis(boolean fixUpAxis) {
		this.fixUpAxis = fixUpAxis;
	}

	/**
	 * This method returns the fix up axis state. If set to true then Y is up axis. Otherwise the up i Z axis. By
	 * default Y is up axis.
	 * @return the up axis state variable
	 */
	public boolean isFixUpAxis() {
		return fixUpAxis;
	}

	/**
	 * This mehtod sets the name of the WORLD data block taht should be used during file loading. By default the name is
	 * not set. If no name is set or the given name does not occur in the file - the first WORLD data block will be used
	 * during loading (assumin any exists in the file).
	 * @param usedWorld
	 *        the name of the WORLD block used during loading
	 */
	public void setUsedWorld(String usedWorld) {
		this.usedWorld = usedWorld;
	}

	/**
	 * This mehtod returns the name of the WORLD data block taht should be used during file loading.
	 * @return the name of the WORLD block used during loading
	 */
	public String getUsedWorld() {
		return usedWorld;
	}

	/**
	 * This method sets the default material for objects.
	 * @param defaultMaterial
	 *        the default material
	 */
	public void setDefaultMaterial(Material defaultMaterial) {
		this.defaultMaterial = defaultMaterial;
	}

	/**
	 * This method returns the default material.
	 * @return the default material
	 */
	public Material getDefaultMaterial() {
		return defaultMaterial;
	}

	@Override
	public void write(JmeExporter e) throws IOException {
		super.write(e);
		OutputCapsule oc = e.getCapsule(this);
		//saving animations
		oc.write(animations == null ? 0 : animations.size(), "anim-size", 0);
		if(animations != null) {
			int objectCounter = 0;
			for(Entry<String, Map<String, int[]>> animEntry : animations.entrySet()) {
				oc.write(animEntry.getKey(), "animated-object-" + objectCounter, null);
				int animsAmount = animEntry.getValue().size();
				oc.write(animsAmount, "anims-amount-" + objectCounter, 0);
				for(Entry<String, int[]> animsEntry : animEntry.getValue().entrySet()) {
					oc.write(animsEntry.getKey(), "anim-name-" + objectCounter, null);
					oc.write(animsEntry.getValue(), "anim-frames-" + objectCounter, null);
				}
				++objectCounter;
			}
		}
		//saving the rest of the data
		oc.write(fps, "fps", DEFAULT_FPS);
		oc.write(featuresToLoad, "features-to-load", FeaturesToLoad.ALL);
		oc.write(assetRootPath, "asset-root-path", null);
		oc.write(fixUpAxis, "fix-up-axis", true);
		oc.write(usedWorld, "used-world", null);
		oc.write(defaultMaterial, "default-material", null);
		oc.write(faceCullMode, "face-cull-mode", FaceCullMode.Off);
	}

	@Override
	public void read(JmeImporter e) throws IOException {
		super.read(e);
		InputCapsule ic = e.getCapsule(this);
		//reading animations
		int animSize = ic.readInt("anim-size", 0);
		if(animSize > 0) {
			if(animations == null) {
				animations = new HashMap<String, Map<String, int[]>>(animSize);
			} else {
				animations.clear();
			}
			for(int i = 0; i < animSize; ++i) {
				String objectName = ic.readString("animated-object-" + i, null);
				int animationsAmount = ic.readInt("anims-amount-" + i, 0);
				Map<String, int[]> objectAnimations = new HashMap<String, int[]>(animationsAmount);
				for(int j = 0; j < animationsAmount; ++j) {
					String animName = ic.readString("anim-name-" + i, null);
					int[] animFrames = ic.readIntArray("anim-frames-" + i, null);
					objectAnimations.put(animName, animFrames);
				}
				animations.put(objectName, objectAnimations);
			}
		}

		//reading the rest of the data
		fps = ic.readInt("fps", DEFAULT_FPS);
		featuresToLoad = ic.readInt("features-to-load", FeaturesToLoad.ALL);
		assetRootPath = ic.readString("asset-root-path", null);
		fixUpAxis = ic.readBoolean("fix-up-axis", true);
		usedWorld = ic.readString("used-world", null);
		defaultMaterial = (Material)ic.readSavable("default-material", null);
		faceCullMode = ic.readEnum("face-cull-mode", FaceCullMode.class, FaceCullMode.Off);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (animations == null ? 0 : animations.hashCode());
		result = prime * result + (assetRootPath == null ? 0 : assetRootPath.hashCode());
		result = prime * result + (defaultMaterial == null ? 0 : defaultMaterial.hashCode());
		result = prime * result + (faceCullMode == null ? 0 : faceCullMode.hashCode());
		result = prime * result + featuresToLoad;
		result = prime * result + (fixUpAxis ? 1231 : 1237);
		result = prime * result + fps;
		result = prime * result + (usedWorld == null ? 0 : usedWorld.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(!super.equals(obj)) {
			return false;
		}
		if(this.getClass() != obj.getClass()) {
			return false;
		}
		BlenderKey other = (BlenderKey)obj;
		if(animations == null) {
			if(other.animations != null) {
				return false;
			}
		} else if(!animations.equals(other.animations)) {
			return false;
		}
		if(assetRootPath == null) {
			if(other.assetRootPath != null) {
				return false;
			}
		} else if(!assetRootPath.equals(other.assetRootPath)) {
			return false;
		}
		if(defaultMaterial == null) {
			if(other.defaultMaterial != null) {
				return false;
			}
		} else if(!defaultMaterial.equals(other.defaultMaterial)) {
			return false;
		}
		if(faceCullMode != other.faceCullMode) {
			return false;
		}
		if(featuresToLoad != other.featuresToLoad) {
			return false;
		}
		if(fixUpAxis != other.fixUpAxis) {
			return false;
		}
		if(fps != other.fps) {
			return false;
		}
		if(usedWorld == null) {
			if(other.usedWorld != null) {
				return false;
			}
		} else if(!usedWorld.equals(other.usedWorld)) {
			return false;
		}
		return true;
	}

	/**
	 * This interface describes the features of the scene that are to be loaded.
	 * @author Marcin Roguski
	 */
	public static interface FeaturesToLoad {
		int	SCENES		= 0x0000FFFF;
		int	OBJECTS		= 0x0000000B;
		int	ANIMATIONS	= 0x00000004;
		int	MATERIALS	= 0x00000003;
		int	TEXTURES	= 0x00000001;
		int	CAMERAS		= 0x00000020;
		int	LIGHTS		= 0x00000010;
		int	ALL			= 0xFFFFFFFF;
	}

	/**
	 * This class holds the loading results according to the given loading flag.
	 * @author Marcin Roguski
	 */
	public static class LoadingResults extends Spatial {
		/** Bitwise mask of features that are to be loaded. */
		private final int		featuresToLoad;
		/** The scenes from the file. */
		private List<Node>		scenes;
		/** Objects from all scenes. */
		private List<Node>		objects;
		/** Materials from all objects. */
		private List<Material>	materials;
		/** Textures from all objects. */
		private List<Texture>	textures;
		/** Animations of all objects. */
		private List<AnimData>	animations;
		/** All cameras from the file. */
		private List<Camera>	cameras;
		/** All lights from the file. */
		private List<Light>		lights;

		/**
		 * Private constructor prevents users to create an instance of this class from outside the
		 * @param featuresToLoad
		 *        bitwise mask of features that are to be loaded
		 * @see FeaturesToLoad FeaturesToLoad
		 */
		private LoadingResults(int featuresToLoad) {
			this.featuresToLoad = featuresToLoad;
			if((featuresToLoad & FeaturesToLoad.SCENES) != 0) {
				scenes = new ArrayList<Node>();
			}
			if((featuresToLoad & FeaturesToLoad.OBJECTS) != 0) {
				objects = new ArrayList<Node>();
				if((featuresToLoad & FeaturesToLoad.MATERIALS) != 0) {
					materials = new ArrayList<Material>();
					if((featuresToLoad & FeaturesToLoad.TEXTURES) != 0) {
						textures = new ArrayList<Texture>();
					}
				}
				if((featuresToLoad & FeaturesToLoad.ANIMATIONS) != 0) {
					animations = new ArrayList<AnimData>();
				}
			}
			if((featuresToLoad & FeaturesToLoad.CAMERAS) != 0) {
				cameras = new ArrayList<Camera>();
			}
			if((featuresToLoad & FeaturesToLoad.LIGHTS) != 0) {
				lights = new ArrayList<Light>();
			}
		}

		/**
		 * This method returns a bitwise flag describing what features of the blend file will be included in the result.
		 * @return bitwise mask of features that are to be loaded
		 * @see FeaturesToLoad FeaturesToLoad
		 */
		public int getLoadedFeatures() {
			return featuresToLoad;
		}

		/**
		 * This method adds a scene to the result set.
		 * @param scene
		 *        scene to be added to the result set
		 */
		public void addScene(Node scene) {
			if(scenes != null) {
				scenes.add(scene);
			}
		}

		/**
		 * This method adds an object to the result set.
		 * @param object
		 *        object to be added to the result set
		 */
		public void addObject(Node object) {
			if(objects != null) {
				objects.add(object);
			}
		}

		/**
		 * This method adds a material to the result set.
		 * @param material
		 *        material to be added to the result set
		 */
		public void addMaterial(Material material) {
			if(materials != null) {
				materials.add(material);
			}
		}

		/**
		 * This method adds a texture to the result set.
		 * @param texture
		 *        texture to be added to the result set
		 */
		public void addTexture(Texture texture) {
			if(textures != null) {
				textures.add(texture);
			}
		}

		/**
		 * This method adds a camera to the result set.
		 * @param camera
		 *        camera to be added to the result set
		 */
		public void addCamera(Camera camera) {
			if(cameras != null) {
				cameras.add(camera);
			}
		}

		/**
		 * This method adds a light to the result set.
		 * @param light
		 *        light to be added to the result set
		 */
		@Override
		public void addLight(Light light) {
			if(lights != null) {
				lights.add(light);
			}
		}

		/**
		 * This method returns all loaded scenes.
		 * @return all loaded scenes
		 */
		public List<Node> getScenes() {
			return scenes;
		}

		/**
		 * This method returns all loaded objects.
		 * @return all loaded objects
		 */
		public List<Node> getObjects() {
			return objects;
		}

		/**
		 * This method returns all loaded materials.
		 * @return all loaded materials
		 */
		public List<Material> getMaterials() {
			return materials;
		}

		/**
		 * This method returns all loaded textures.
		 * @return all loaded textures
		 */
		public List<Texture> getTextures() {
			return textures;
		}

		/**
		 * This method returns all loaded animations.
		 * @return all loaded animations
		 */
		public List<AnimData> getAnimations() {
			return animations;
		}

		/**
		 * This method returns all loaded cameras.
		 * @return all loaded cameras
		 */
		public List<Camera> getCameras() {
			return cameras;
		}

		/**
		 * This method returns all loaded lights.
		 * @return all loaded lights
		 */
		public List<Light> getLights() {
			return lights;
		}

		@Override
		public int collideWith(Collidable other, CollisionResults results) throws UnsupportedCollisionException {
			return 0;
		}

		@Override
		public void updateModelBound() {}

		@Override
		public void setModelBound(BoundingVolume modelBound) {}

		@Override
		public int getVertexCount() {
			return 0;
		}

		@Override
		public int getTriangleCount() {
			return 0;
		}

		@Override
		public Spatial deepClone() {
			return null;
		}

		@Override
		public void depthFirstTraversal(SceneGraphVisitor visitor) {}

		@Override
		protected void breadthFirstTraversal(SceneGraphVisitor visitor, Queue<Spatial> queue) {}
	}

	/**
	 * The WORLD file block contains various data that could be added to the scene. The contained data includes: ambient
	 * light.
	 * @author Marcin Roguski
	 */
	public static class WorldData {
		/** The ambient light. */
		private AmbientLight	ambientLight;

		/**
		 * This method returns the world's ambient light.
		 * @return the world's ambient light
		 */
		public AmbientLight getAmbientLight() {
			return ambientLight;
		}

		/**
		 * This method sets the world's ambient light.
		 * @param ambientLight
		 *        the world's ambient light
		 */
		public void setAmbientLight(AmbientLight ambientLight) {
			this.ambientLight = ambientLight;
		}
	}
}
