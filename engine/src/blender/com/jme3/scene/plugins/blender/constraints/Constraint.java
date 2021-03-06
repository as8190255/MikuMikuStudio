package com.jme3.scene.plugins.blender.constraints;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.scene.plugins.blender.BlenderContext;
import com.jme3.scene.plugins.blender.animations.Ipo;
import com.jme3.scene.plugins.blender.constraints.ConstraintHelper.Space;
import com.jme3.scene.plugins.blender.constraints.definitions.ConstraintDefinition;
import com.jme3.scene.plugins.blender.constraints.definitions.ConstraintDefinitionFactory;
import com.jme3.scene.plugins.blender.exceptions.BlenderFileException;
import com.jme3.scene.plugins.blender.file.Pointer;
import com.jme3.scene.plugins.blender.file.Structure;

/**
 * The implementation of a constraint.
 * 
 * @author Marcin Roguski (Kaelthas)
 */
public abstract class Constraint {
    private static final Logger          LOGGER = Logger.getLogger(Constraint.class.getName());

    /** The name of this constraint. */
    protected final String               name;
    /** Indicates if the constraint is already baked or not. */
    protected boolean                    baked;

    protected Space                      ownerSpace;
    protected final ConstraintDefinition constraintDefinition;
    protected Long                       ownerOMA;

    protected Long                       targetOMA;
    protected Space                      targetSpace;
    protected String                     subtargetName;

    /** The ipo object defining influence. */
    protected final Ipo                  ipo;
    /** The blender context. */
    protected final BlenderContext       blenderContext;
    protected final ConstraintHelper     constraintHelper;

    /**
     * This constructor creates the constraint instance.
     * 
     * @param constraintStructure
     *            the constraint's structure (bConstraint clss in blender 2.49).
     * @param ownerOMA
     *            the old memory address of the constraint owner
     * @param ownerType
     *            the type of the constraint owner
     * @param influenceIpo
     *            the ipo curve of the influence factor
     * @param blenderContext
     *            the blender context
     * @throws BlenderFileException
     *             this exception is thrown when the blender file is somehow
     *             corrupted
     */
    public Constraint(Structure constraintStructure, Long ownerOMA, Ipo influenceIpo, BlenderContext blenderContext) throws BlenderFileException {
        this.blenderContext = blenderContext;
        this.name = constraintStructure.getFieldValue("name").toString();
        Pointer pData = (Pointer) constraintStructure.getFieldValue("data");
        if (pData.isNotNull()) {
            Structure data = pData.fetchData(blenderContext.getInputStream()).get(0);
            constraintDefinition = ConstraintDefinitionFactory.createConstraintDefinition(data, ownerOMA, blenderContext);
            Pointer pTar = (Pointer) data.getFieldValue("tar");
            if (pTar != null && pTar.isNotNull()) {
                this.targetOMA = pTar.getOldMemoryAddress();
                this.targetSpace = Space.valueOf(((Number) constraintStructure.getFieldValue("tarspace")).byteValue());
                Object subtargetValue = data.getFieldValue("subtarget");
                if (subtargetValue != null) {// not all constraint data have the
                                             // subtarget field
                    subtargetName = subtargetValue.toString();
                }
            }
        } else {
            // Null constraint has no data, so create it here
            constraintDefinition = ConstraintDefinitionFactory.createConstraintDefinition(null, null, blenderContext);
        }
        this.ownerSpace = Space.valueOf(((Number) constraintStructure.getFieldValue("ownspace")).byteValue());
        this.ipo = influenceIpo;
        this.ownerOMA = ownerOMA;
        this.constraintHelper = blenderContext.getHelper(ConstraintHelper.class);
        LOGGER.log(Level.INFO, "Created constraint: {0} with definition: {1}", new Object[] { name, constraintDefinition });
    }

    /**
     * @return <b>true</b> if the constraint is implemented and <b>false</b>
     *         otherwise
     */
    public boolean isImplemented() {
        return constraintDefinition == null ? true : constraintDefinition.isImplemented();
    }

    /**
     * @return the name of the constraint type, similar to the constraint name
     *         used in Blender
     */
    public String getConstraintTypeName() {
        return constraintDefinition.getConstraintTypeName();
    }

    /**
     * Performs validation before baking. Checks factors that can prevent
     * constraint from baking that could not be checked during constraint
     * loading.
     */
    public abstract boolean validate();

    public abstract void apply(int frame);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((ownerOMA == null) ? 0 : ownerOMA.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Constraint other = (Constraint) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (ownerOMA == null) {
            if (other.ownerOMA != null) {
                return false;
            }
        } else if (!ownerOMA.equals(other.ownerOMA)) {
            return false;
        }
        return true;
    }
}