package functions;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewParent;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;

/*
 * to use this copy-pasted class with your project
 * be sure that you set android:label for each of your
 * navigation items
 */
public class MyNavigationUISetup {
    public static void setupWithNavController(@NonNull final NavigationView navigationView, @NonNull final NavController navController) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i("selectItem", navController.getCurrentDestination().getLabel().toString());
                boolean handled = onNavDestinationSelected(item, navController, true);
                if (handled) {
                    ViewParent parent = navigationView.getParent();
                    if (parent instanceof DrawerLayout) {
                        ((DrawerLayout)parent).closeDrawer(navigationView);
                    }
                }

                return handled;
            }
        });
        navController.addOnNavigatedListener(new NavController.OnNavigatedListener() {
            public void onNavigated(@NonNull NavController controller, @NonNull NavDestination destination) {
                int destinationId = destination.getId();
                Menu menu = navigationView.getMenu();
                int h = 0;

                for(int size = menu.size(); h < size; ++h) {
                    MenuItem item = menu.getItem(h);
                    item.setChecked(item.getItemId() == destinationId);
                }

            }
        });
    }

    private static boolean onNavDestinationSelected(@NonNull MenuItem item,
                                                    @NonNull NavController navController, boolean popUp) {
        NavOptions.Builder builder = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
                .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
                .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        if (popUp) {
            builder.setPopUpTo(navController.getGraph().getStartDestination(), false);
        }
        NavOptions options = builder.build();
        try {
            //TODO provide proper API instead of using Exceptions as Control-Flow.
            navController.navigate(item.getItemId(), null, options);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
