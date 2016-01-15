package com.proptiger.uberhackathon.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.proptiger.uberhackathon.util.AppBus;
import com.proptiger.uberhackathon.util.AppUtils;

import butterknife.ButterKnife;

/**
 * Created by rohitgarg on 1/15/16.
 */
public abstract class HackBaseActivity extends AppCompatActivity{

    protected abstract int getContentViewId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        AppBus.getInstance().register(this);

        if (!AppUtils.haveNetworkConnection(this)) {
            showNoNetworkFound();
        }


    }

    private void showNoNetworkFound() {
        // TODO
    }

    protected void initFragment(int fragmentHolderId, Fragment fragment, boolean shouldAddToBackStack) {
        // reference fragment transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentHolderId, fragment, fragment.getClass().getName());
        // if need to be added to the backstack, then do so
        if (shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        // TODO
        // check if we this can be called from any background thread or after background to ui thread communication
        // then we need to make use of commitAllowingStateLoss()
        fragmentTransaction.commit();
    }

    protected void initFragments(int[] fragmentHolderId, Fragment[] fragment, boolean shouldAddToBackStack) {
        if (fragmentHolderId.length != fragment.length) {
            return;
        }
        // reference fragment transaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < fragmentHolderId.length; i++) {
            fragmentTransaction.replace(fragmentHolderId[i], fragment[i], fragment.getClass().getName());
        }
        // if need to be added to the backstack, then do so
        if (shouldAddToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        // TODO
        // check if we this can be called from any background thread or after background to ui thread communication
        // then we need to make use of commitAllowingStateLoss()
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        AppBus.getInstance().unregister(this);

    }
}
