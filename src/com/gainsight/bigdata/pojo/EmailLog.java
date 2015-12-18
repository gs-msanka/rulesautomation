package com.gainsight.bigdata.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by Giribabu on 14/12/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailLog {

    private int nOpened;
    private int nClicked;
    private int nSent;
    private int nRejected;
    private int nSpam;
    private int nHb;
    private int nSb;
    private int nUsb;
    private long triggeredDate;
    private int nAccounts;

    public int getnOpened() {
        return nOpened;
    }

    public void setnOpened(int nOpened) {
        this.nOpened = nOpened;
    }

    public int getnClicked() {
        return nClicked;
    }

    public void setnClicked(int nClicked) {
        this.nClicked = nClicked;
    }

    public int getnSent() {
        return nSent;
    }

    public void setnSent(int nSent) {
        this.nSent = nSent;
    }

    public int getnRejected() {
        return nRejected;
    }

    public void setnRejected(int nRejected) {
        this.nRejected = nRejected;
    }

    public int getnSpam() {
        return nSpam;
    }

    public void setnSpam(int nSpam) {
        this.nSpam = nSpam;
    }

    public int getnHb() {
        return nHb;
    }

    public void setnHb(int nHb) {
        this.nHb = nHb;
    }

    public int getnSb() {
        return nSb;
    }

    public void setnSb(int nSb) {
        this.nSb = nSb;
    }

    public int getnUsb() {
        return nUsb;
    }

    public void setnUsb(int nUsb) {
        this.nUsb = nUsb;
    }

    public long getTriggeredDate() {
        return triggeredDate;
    }

    public void setTriggeredDate(long triggeredDate) {
        this.triggeredDate = triggeredDate;
    }

    public int getnAccounts() {
        return nAccounts;
    }

    public void setnAccounts(int nAccounts) {
        this.nAccounts = nAccounts;
    }
}
