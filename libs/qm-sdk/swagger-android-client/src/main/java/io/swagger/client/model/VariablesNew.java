package io.swagger.client.model;

import io.swagger.client.model.VariableNew;
import java.util.*;
import java.util.ArrayList;

import io.swagger.annotations.*;
import com.google.gson.annotations.SerializedName;


/**
 * New variables
 **/
@ApiModel(description = "New variables")
public class VariablesNew extends ArrayList<VariableNew> {
  

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class VariablesNew {\n");
    sb.append("  " + super.toString()).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
