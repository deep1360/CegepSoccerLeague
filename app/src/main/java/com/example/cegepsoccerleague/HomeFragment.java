package com.example.cegepsoccerleague;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener{

    private BottomNavigationView home_bottom_bar;
    private FragmentManager homeFragManager;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initializing default Fragment
        homeFragManager = getChildFragmentManager();
        homeFragManager.beginTransaction().add(R.id.fragment_container, new ScoreboardFragment()).commit();

        //Referencing bottom bar Class variable to XML Layout
        home_bottom_bar = view.findViewById(R.id.home_bottom_bar);
        //Referencing bottom item with click listener
        home_bottom_bar.setOnNavigationItemSelectedListener(this);

    }

    //Changeing Fragment on Clicking Bottom Bar Item
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.scoreboard_item:
                homeFragManager.beginTransaction().replace(R.id.fragment_container, new ScoreboardFragment(), "ScoreboardFragment").commit();
                break;
            case R.id.leagues_item:
                homeFragManager.beginTransaction().replace(R.id.fragment_container, new ListOfLeaguesFragment(), "LeaguesFragment").commit();
                break;
            case R.id.up_match_item:
                homeFragManager.beginTransaction().replace(R.id.fragment_container, new ListOfSchedulesFragment(), "UpcomingFragment").commit();
                break;
        }
        return true;
    }
}
