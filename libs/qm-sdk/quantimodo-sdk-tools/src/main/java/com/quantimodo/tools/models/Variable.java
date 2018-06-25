package com.quantimodo.tools.models;

import com.quantimodo.tools.models.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import com.quantimodo.android.sdk.SdkDefs;
// KEEP INCLUDES END
/**
 * Entity mapped to table "VARIABLE".
 */
public class Variable implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String name;
    private String originalName;
    private Long parentVariable;
    private Long unitId;
    private Long categoryId;
    private short combOperation;
    private java.util.Date updated;
    private java.util.Date latestMeasurementTime;
    private Long lastMeasurementSync;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient VariableDao myDao;

    private Variable parent;
    private Long parent__resolvedKey;

    private Unit unit;
    private Long unit__resolvedKey;

    private Category category;
    private Long category__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    public enum CombinationType{
        MEAN(SdkDefs.COMBINE_MEAN),
        SUM(SdkDefs.COMBINE_SUM);

        private final String value;

        CombinationType(String value) {
            this.value = value;
        }

        public static CombinationType fromString(String value){
            switch (value){
                case SdkDefs.COMBINE_MEAN:
                    return MEAN;
                case SdkDefs.COMBINE_SUM:
                    return SUM;
            }
            return MEAN;
        };

        public String getValue() {
            return value;
        }
    }
    // KEEP FIELDS END

    public Variable() {
    }

    public Variable(Long id) {
        this.id = id;
    }

    public Variable(Long id, String name, String originalName, Long parentVariable, Long unitId, Long categoryId, short combOperation, java.util.Date updated, java.util.Date latestMeasurementTime, Long lastMeasurementSync) {
        this.id = id;
        this.name = name;
        this.originalName = originalName;
        this.parentVariable = parentVariable;
        this.unitId = unitId;
        this.categoryId = categoryId;
        this.combOperation = combOperation;
        this.updated = updated;
        this.latestMeasurementTime = latestMeasurementTime;
        this.lastMeasurementSync = lastMeasurementSync;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getVariableDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Long getParentVariable() {
        return parentVariable;
    }

    public void setParentVariable(Long parentVariable) {
        this.parentVariable = parentVariable;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public short getCombOperation() {
        return combOperation;
    }

    public void setCombOperation(short combOperation) {
        this.combOperation = combOperation;
    }

    public java.util.Date getUpdated() {
        return updated;
    }

    public void setUpdated(java.util.Date updated) {
        this.updated = updated;
    }

    public java.util.Date getLatestMeasurementTime() {
        return latestMeasurementTime;
    }

    public void setLatestMeasurementTime(java.util.Date latestMeasurementTime) {
        this.latestMeasurementTime = latestMeasurementTime;
    }

    public Long getLastMeasurementSync() {
        return lastMeasurementSync;
    }

    public void setLastMeasurementSync(Long lastMeasurementSync) {
        this.lastMeasurementSync = lastMeasurementSync;
    }

    /** To-one relationship, resolved on first access. */
    public Variable getParent() {
        Long __key = this.parentVariable;
        if (parent__resolvedKey == null || !parent__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            VariableDao targetDao = daoSession.getVariableDao();
            Variable parentNew = targetDao.load(__key);
            synchronized (this) {
                parent = parentNew;
            	parent__resolvedKey = __key;
            }
        }
        return parent;
    }

    public void setParent(Variable parent) {
        synchronized (this) {
            this.parent = parent;
            parentVariable = parent == null ? null : parent.getId();
            parent__resolvedKey = parentVariable;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Unit getUnit() {
        Long __key = this.unitId;
        if (unit__resolvedKey == null || !unit__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UnitDao targetDao = daoSession.getUnitDao();
            Unit unitNew = targetDao.load(__key);
            synchronized (this) {
                unit = unitNew;
            	unit__resolvedKey = __key;
            }
        }
        return unit;
    }

    public void setUnit(Unit unit) {
        synchronized (this) {
            this.unit = unit;
            unitId = unit == null ? null : unit.getId();
            unit__resolvedKey = unitId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Category getCategory() {
        Long __key = this.categoryId;
        if (category__resolvedKey == null || !category__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CategoryDao targetDao = daoSession.getCategoryDao();
            Category categoryNew = targetDao.load(__key);
            synchronized (this) {
                category = categoryNew;
            	category__resolvedKey = __key;
            }
        }
        return category;
    }

    public void setCategory(Category category) {
        synchronized (this) {
            this.category = category;
            categoryId = category == null ? null : category.getId();
            category__resolvedKey = categoryId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

    // KEEP METHODS - put your custom methods here
    public static Variable fromVariable(com.quantimodo.android.sdk.model.Variable v){
        Variable variable = new Variable();
        variable.setId(v.getId());
        variable.setName(v.getName());
        variable.setOriginalName(v.getOriginalName());
        variable.setUpdated(v.getUpdated());
        variable.setLatestMeasurementTime(v.getLatestMeasurementTime());
        //Unit
        //Parent Variable
        //Set category
        return variable;
    }

    public com.quantimodo.android.sdk.model.Variable toVariable() {
        return new com.quantimodo.android.sdk.model.Variable(
                getId(),
                getOriginalName(),
                getParent() != null ? getParent().getName() : null,
                getCategory().getName(),
                getUnit().getAbbreviatedName(),
                CombinationType.values()[getCombOperation()].getValue()
        );
    }
    // KEEP METHODS END

}