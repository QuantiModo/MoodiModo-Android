package com.moodimodo;

import com.moodimodo.sync.SyncService;
import com.quantimodo.android.sdk.SdkDefs;
import com.quantimodo.tools.models.*;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

@Module(
        complete = false,
        library = true
)
public class ConfigModule {
    private final Variable mOutcomeVariable;
    private final Unit mOutcomeUnit;

    public ConfigModule(DaoSession daoSession){

        Unit unit;
        unit = daoSession.getUnitDao().queryBuilder().where(UnitDao.Properties.AbbreviatedName.eq(Global.DEFAULT_UNIT)).unique();
        if (unit == null){
            unit = new Unit();
            unit.setAbbreviatedName(Global.DEFAULT_UNIT);
            unit.setMin(0d);
            unit.setMax(5d);
            unit.setName("1 to 5");
            daoSession.getUnitDao().insertOrReplace(unit);
        }
        mOutcomeUnit = unit;

        Variable variable = daoSession.getVariableDao().queryBuilder().where(VariableDao.Properties.Name.eq(BuildConfig.OUTCOME_VARIABLE)).unique();
        if (variable == null){
            Category category = new Category(null,BuildConfig.OUTCOME_CATEGORY);
            category.setId((long)category.getName().hashCode());
            daoSession.getCategoryDao().insertOrReplace(category);

            variable = new Variable();
            variable.setName(BuildConfig.OUTCOME_VARIABLE);
            variable.setUnit(unit);
            variable.setCategory(category);
            variable.setCombOperation((short) 0);
            daoSession.getVariableDao().insertOrReplace(variable);
        }
        mOutcomeVariable = variable;
    }


    @Provides @Named("outcome")
    public Unit getOutComeUnit(){
        return mOutcomeUnit;
    }

    @Provides @Named("outcome")
    public Variable getOutcomeVariable(){
        return mOutcomeVariable;
    }
}
