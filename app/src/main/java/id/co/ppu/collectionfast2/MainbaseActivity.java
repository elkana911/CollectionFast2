package id.co.ppu.collectionfast2;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import butterknife.BindView;
import id.co.ppu.collectionfast2.component.BasicActivity;

/**
 * Created by Eric on 30-Dec-16.
 */

public abstract class MainbaseActivity extends BasicActivity {

    @BindView(R.id.fab)
    public FloatingActionButton fab;

    @BindView(R.id.coordinatorLayout)
    public View coordinatorLayout;

    @BindView(R.id.nav_view)
    public NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    public DrawerLayout drawer;

    protected abstract void changeFabIcon(int resId);
    protected abstract void displayView(int viewId);
}
