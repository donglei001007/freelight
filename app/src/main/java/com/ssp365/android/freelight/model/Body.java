package com.ssp365.android.freelight.model;

/**
 * 用于放置adapter数据的内部类
 *
 * @author donglei
 */
public class Body {// 放置adapter数据的类
    int no;
    String coin;
    Sporter sporter;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public Sporter getSporter() {
        return sporter;
    }

    public void setSporter(Sporter sporter) {
        this.sporter = sporter;
    }
}
