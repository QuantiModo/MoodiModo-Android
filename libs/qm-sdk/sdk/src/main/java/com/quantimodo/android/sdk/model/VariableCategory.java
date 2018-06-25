package com.quantimodo.android.sdk.model;

/*
{
	"name":"Mood"
}
*/

public class VariableCategory {
    @Deprecated
    public final String name;

    public VariableCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
