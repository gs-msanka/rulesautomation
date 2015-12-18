package com.gainsight.bigdata.copilot.bean.webhook.mandrill;

/**
 * Created by gainsight on 17/12/15.
 */
public enum MandrillEvent {
    send,
    open,
    hard_bounce,
    soft_bounce,
    reject,
    click,
    spam,
    delay,
    unsubscribe;

}
