package com.example.yummpies;

import java.io.Serializable;

class Data implements Serializable {
    public String name;
    public Double price;
    public int items;

    Data(String s,Double p, int item_count)
    {
        this.name =s;
        this.price=p;
        this.items=item_count;

    }
    Data()
    {
        this.name = "";
        this.price= 0.0;
        this.items = 0;
    }
}
