package com.topcoder.common.model;

public class CategoryRank {

    private String name;
    private int rank;

    public CategoryRank(String name, int rank) {
        this.name = name;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public int getRank() {
        return rank;
    }
}
