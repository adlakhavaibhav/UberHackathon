package com.proptiger.uberhackathon.util;

import com.squareup.otto.Bus;

/**
 * Created by rohitgarg on 1/15/16.
 */
public class AppBus {

    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }
}
