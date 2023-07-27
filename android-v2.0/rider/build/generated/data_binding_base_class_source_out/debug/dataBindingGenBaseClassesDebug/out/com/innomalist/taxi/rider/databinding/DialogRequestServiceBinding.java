package com.innomalist.taxi.rider.databinding;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import link.fls.swipestack.SwipeStack;

public abstract class DialogRequestServiceBinding extends ViewDataBinding {
  @NonNull
  public final CardView driversAcceptedCard;

  @NonNull
  public final LottieAnimationView driversAcceptedLoadingAnimation;

  @NonNull
  public final ConstraintLayout frameLayout;

  @NonNull
  public final SwipeStack swipeStack;

  @NonNull
  public final TextView textLoading;

  protected DialogRequestServiceBinding(DataBindingComponent _bindingComponent, View _root,
      int _localFieldCount, CardView driversAcceptedCard,
      LottieAnimationView driversAcceptedLoadingAnimation, ConstraintLayout frameLayout,
      SwipeStack swipeStack, TextView textLoading) {
    super(_bindingComponent, _root, _localFieldCount);
    this.driversAcceptedCard = driversAcceptedCard;
    this.driversAcceptedLoadingAnimation = driversAcceptedLoadingAnimation;
    this.frameLayout = frameLayout;
    this.swipeStack = swipeStack;
    this.textLoading = textLoading;
  }

  @NonNull
  public static DialogRequestServiceBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static DialogRequestServiceBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable DataBindingComponent component) {
    return DataBindingUtil.<DialogRequestServiceBinding>inflate(inflater, com.innomalist.taxi.rider.R.layout.dialog_request_service, root, attachToRoot, component);
  }

  @NonNull
  public static DialogRequestServiceBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static DialogRequestServiceBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable DataBindingComponent component) {
    return DataBindingUtil.<DialogRequestServiceBinding>inflate(inflater, com.innomalist.taxi.rider.R.layout.dialog_request_service, null, false, component);
  }

  public static DialogRequestServiceBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  public static DialogRequestServiceBinding bind(@NonNull View view,
      @Nullable DataBindingComponent component) {
    return (DialogRequestServiceBinding)bind(component, view, com.innomalist.taxi.rider.R.layout.dialog_request_service);
  }
}
