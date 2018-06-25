package com.quantimodo.tools.generator;

import de.greenrobot.daogenerator.*;

import java.io.File;

public class QmDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1006, "com.quantimodo.tools.models");
        schema.enableKeepSectionsByDefault();
        schema.enableActiveEntitiesByDefault();


        Entity category = addCategory(schema);
        Entity unit = addUnit(schema, category);
        Entity variable = addVariable(schema,unit,category);
        addMeasurement(schema, unit, variable);

        System.out.println(new File(".").getAbsolutePath());
        new DaoGenerator().generateAll(schema, "../quantimodo-sdk-tools/src/main/java/");
    }

    private static Entity addVariable(Schema schema, Entity unit, Entity category) {
        Entity variable = schema.addEntity("Variable");
        variable.implementsSerializable();
        variable.addLongProperty("id").primaryKey().autoincrement();
        variable.addStringProperty("name").unique().notNull();
        variable.addStringProperty("originalName");

        Property vp = variable.addLongProperty("parentVariable").getProperty();
        variable.addToOne(variable,vp,"parent");

        Property vu = variable.addLongProperty("unitId").getProperty();
        variable.addToOne(unit,vu,"unit");

        Property vc = variable.addLongProperty("categoryId").getProperty();
        variable.addToOne(category, vc, "category");

        variable.addShortProperty("combOperation").notNull();
        variable.addDateProperty("updated");
        variable.addDateProperty("latestMeasurementTime");
        variable.addLongProperty("lastMeasurementSync");

        return variable;
    }

    private static Entity addUnit(Schema schema, Entity category){
        Entity unit = schema.addEntity("Unit");
        unit.implementsSerializable();
        unit.addLongProperty("id").primaryKey().autoincrement();
        unit.addStringProperty("name").notNull();
        unit.addDoubleProperty("min");
        unit.addDoubleProperty("max");

        unit.addStringProperty("category");
        unit.addStringProperty("abbreviatedName").columnName("ABBR").notNull().unique();
        return unit;
    }

    private static Entity addCategory(Schema schema){
        Entity category = schema.addEntity("Category");
        category.implementsSerializable();
        category.addProperty(PropertyType.Long,"id").primaryKey().autoincrement();
        category.addStringProperty("name").notNull().unique();
        return category;
    }

    private static void addMeasurement(Schema schema, Entity unit, Entity variable){
        Entity measurement = schema.addEntity("Measurement");
        measurement.implementsSerializable();
        measurement.addLongProperty("id").primaryKey().autoincrement();
        Property timestamp = measurement.addDateProperty("timestamp").notNull().getProperty();

        Property vm = measurement.addLongProperty("variableId").getProperty();
        measurement.addToOne(variable,vm,"variable");



        Property vu = measurement.addLongProperty("unitId").getProperty();
        measurement.addToOne(unit,vu,"unit");

        measurement.addDoubleProperty("value").notNull();
        measurement.addStringProperty("source");
        measurement.addBooleanProperty("needUpdate");
        measurement.addStringProperty("note");
        vm =  measurement.addStringProperty("variableName").notNull().getProperty();
        measurement.addStringProperty("unitName").notNull();

        Index index = new Index();
        index.makeUnique();
        index.setName("pk");
        index.addProperty(timestamp);
        index.addProperty(vm);
        measurement.addIndex(index);
    }

}
