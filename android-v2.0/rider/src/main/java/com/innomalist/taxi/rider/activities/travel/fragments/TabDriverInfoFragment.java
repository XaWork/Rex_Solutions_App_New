package com.innomalist.taxi.rider.activities.travel.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.innomalist.taxi.common.components.BaseFragment;
import com.innomalist.taxi.common.models.DriverInfo;
import com.innomalist.taxi.common.models.Travel;
import com.innomalist.taxi.common.utils.CommonUtils;
import com.innomalist.taxi.common.utils.DataBinder;
import com.innomalist.taxi.rider.R;
import com.innomalist.taxi.rider.databinding.FragmentTravelDriverBinding;
import com.innomalist.taxi.rider.events.GetTravelInfoResultEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

public class TabDriverInfoFragment extends BaseFragment {
    private static final String ARG_TRAVEL = "travel";
    FragmentTravelDriverBinding binding;
    Travel travel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRegisterEventBus(false);
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            travel = Travel.fromJson(getArguments().getString(ARG_TRAVEL));
    }

    public static TabDriverInfoFragment newInstance(Travel travel) {
        TabDriverInfoFragment tabDriverInfoFragment = new TabDriverInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRAVEL, travel.toJson());
        tabDriverInfoFragment.setArguments(args);
        return tabDriverInfoFragment;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_travel_driver,container,false);
        binding.textPickup.setSelected(true);
        binding.textDestination.setSelected(true);
        binding.textPickup.setText(travel.getPickupAddress());
        binding.textDestination.setText(travel.getDestinationAddress());
        if(!travel.getDriver().getFirstName().isEmpty() || !travel.getDriver().getLastName().isEmpty())
            binding.textDriverName.setText(travel.getDriver().getFirstName() + " " + travel.getDriver().getLastName());
        String carName = "-";
        if(travel.getDriver().getCar() != null && travel.getDriver().getCar().getTitle() != null)
            carName = travel.getDriver().getCar().getTitle();
        if(travel.getDriver().getCarColor() != null)
            carName += " " + travel.getDriver().getCarColor();
        if(travel.getDriver().getCarPlate() != null)
            carName += ", " + travel.getDriver().getCarPlate();
        binding.textCarName.setText(carName);
        DataBinder.setMedia(binding.imageDriver,travel.getDriver().getCarMedia());
        return binding.getRoot();
    }
}
