package com.xtreme.jx.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "searched_comics")
public class SearchHistory {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String searchedText;

    @DatabaseField
    private String time;

    public SearchHistory() {
    }

    public SearchHistory(String searchedText, String time) {
        this.searchedText = searchedText;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSearchedText() {
        return searchedText;
    }

    public void setSearchedText(String searchedText) {
        this.searchedText = searchedText;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
