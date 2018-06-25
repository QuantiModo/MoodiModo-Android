package com.quantimodo.tools.models;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.quantimodo.tools.models.Category;
import com.quantimodo.tools.models.Unit;
import com.quantimodo.tools.models.Variable;
import com.quantimodo.tools.models.Measurement;

import com.quantimodo.tools.models.CategoryDao;
import com.quantimodo.tools.models.UnitDao;
import com.quantimodo.tools.models.VariableDao;
import com.quantimodo.tools.models.MeasurementDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig categoryDaoConfig;
    private final DaoConfig unitDaoConfig;
    private final DaoConfig variableDaoConfig;
    private final DaoConfig measurementDaoConfig;

    private final CategoryDao categoryDao;
    private final UnitDao unitDao;
    private final VariableDao variableDao;
    private final MeasurementDao measurementDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        categoryDaoConfig = daoConfigMap.get(CategoryDao.class).clone();
        categoryDaoConfig.initIdentityScope(type);

        unitDaoConfig = daoConfigMap.get(UnitDao.class).clone();
        unitDaoConfig.initIdentityScope(type);

        variableDaoConfig = daoConfigMap.get(VariableDao.class).clone();
        variableDaoConfig.initIdentityScope(type);

        measurementDaoConfig = daoConfigMap.get(MeasurementDao.class).clone();
        measurementDaoConfig.initIdentityScope(type);

        categoryDao = new CategoryDao(categoryDaoConfig, this);
        unitDao = new UnitDao(unitDaoConfig, this);
        variableDao = new VariableDao(variableDaoConfig, this);
        measurementDao = new MeasurementDao(measurementDaoConfig, this);

        registerDao(Category.class, categoryDao);
        registerDao(Unit.class, unitDao);
        registerDao(Variable.class, variableDao);
        registerDao(Measurement.class, measurementDao);
    }
    
    public void clear() {
        categoryDaoConfig.getIdentityScope().clear();
        unitDaoConfig.getIdentityScope().clear();
        variableDaoConfig.getIdentityScope().clear();
        measurementDaoConfig.getIdentityScope().clear();
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public UnitDao getUnitDao() {
        return unitDao;
    }

    public VariableDao getVariableDao() {
        return variableDao;
    }

    public MeasurementDao getMeasurementDao() {
        return measurementDao;
    }

}
