package functions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.asus_user.labs.R;
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
                boolean handled = onNavDestinationSelected(item, navController, true, navigationView);
                hideKeyboard((Activity) navigationView.getContext());
                if (handled) {
                    ViewParent parent = navigationView.getParent();
                    if (parent instanceof DrawerLayout) {
                        ((DrawerLayout)parent).closeDrawer(navigationView);
                    }
                }
                else {
                    Toast.makeText(navigationView.getContext(), "ain't allowed to do this now", Toast.LENGTH_LONG).show();
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

    private static boolean onNavDestinationSelected(@NonNull final MenuItem item,
                                                    @NonNull final NavController navController,
                                                    boolean popUp,
                                                    NavigationView navView) {
        NavOptions.Builder builder = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
                .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
                .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
                .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
        if (popUp) {
            builder.setPopUpTo(navController.getGraph().getStartDestination(), false);
        }
        final NavOptions options = builder.build();
        try {
            //TODO provide proper API instead of using Exceptions as Control-Flow.
            boolean goOut = false;

            Log.i("selectItem", navController.getCurrentDestination().getLabel().toString());
            if(navController.getCurrentDestination().getLabel().toString().equals("fragment_edit_user")){
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(navView.getContext());
                dialogBuilder.setMessage(R.string.non_saved_warning).setCancelable(false);
                dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navController.navigate(item.getItemId(), null, options);
                        dialog.dismiss();
                    }
                });
                dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                return true;
            }
            else {
                navController.navigate(item.getItemId(), null, options);
                return true;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
