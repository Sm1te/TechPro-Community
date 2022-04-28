package com.robin.community.entity;


//used for allocating pages
public class Page {
    // right now
    private int current = 1;
    // limit
    private int limit = 10;
    // count(calculate all how many pages we need to have)
    private int rows;
    // find path(reusable)
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1)
            this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100)
            this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0)
            this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    // get the offset of current page
    public int getOffset(){
        // current & limit - limit
        return (current - 1) * limit;
    }

    // Total pages
    public int getTotal(){
        // rows / limit + 1
        if(rows % limit == 0)
            return rows/limit;
        else
            return rows/limit + 1;
    }

    // get the starting page number
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    //get the end page number
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
