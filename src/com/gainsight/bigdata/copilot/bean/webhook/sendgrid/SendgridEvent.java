package com.gainsight.bigdata.copilot.bean.webhook.sendgrid;

/**
 * Created by gainsight on 17/12/15.
 */
public enum SendgridEvent {
    Processed,
    Dropped,
    Deferred,
    Delivered,
    Bounce,
    Click,
    Open,
    Unsubscribe,
    SpamReport;
}
