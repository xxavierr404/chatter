package com.teamzero.chatter;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teamzero.chatter.databinding.ActivityMainBinding;
import com.teamzero.chatter.ui.fragments.auth.login.LoginFragment;
import com.teamzero.chatter.ui.fragments.main.ChatsFragment;
import com.teamzero.chatter.ui.fragments.main.FinderFragment;
import com.teamzero.chatter.ui.fragments.main.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private FirebaseAuth mAuth;

    private final ChatsFragment chatsFragment = new ChatsFragment();
    private final FinderFragment finderFragment = new FinderFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();
    private final FragmentManager manager = getSupportFragmentManager();
    private Fragment currentFragment = chatsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
            return;
        }

        profileFragment.setId(mAuth.getCurrentUser().getUid());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        manager.beginTransaction().add(R.id.nav_host_fragment_activity_main, finderFragment, "Finder").hide(finderFragment)
                .add(R.id.nav_host_fragment_activity_main, profileFragment, "Profile").hide(profileFragment)
                .add(R.id.nav_host_fragment_activity_main, chatsFragment, "Chats")
                .addToBackStack("Initial").commit();

        navView.setSelectedItemId(R.id.navigation_chats);

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.navigation_chats:
                        manager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                                .hide(currentFragment).show(chatsFragment).commit();
                        currentFragment = chatsFragment;
                        return true;
                    case R.id.navigation_finder:
                        manager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                                .hide(currentFragment).show(finderFragment).commit();
                        currentFragment = finderFragment;
                        return true;
                    case R.id.navigation_profile:
                        manager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                                .hide(currentFragment).show(profileFragment).commit();
                        currentFragment = profileFragment;
                        return true;
                    default:
                        return false;
                }
            }
        });
/*        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_finder, R.id.navigation_chats, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);*/
    }
}