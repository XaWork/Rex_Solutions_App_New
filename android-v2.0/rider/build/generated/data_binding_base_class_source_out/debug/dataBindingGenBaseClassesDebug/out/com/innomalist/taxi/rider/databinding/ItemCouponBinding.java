package com.innomalist.taxi.rider.databinding;

import android.databinding.Bindable;
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
import android.widget.Button;
import android.widget.TextView;
import com.innomalist.taxi.common.models.Coupon;

public abstract class ItemCouponBinding extends ViewDataBinding {
  @NonNull
  public final Button buttonSelect;

  @NonNull
  public final CardView cardTransaction;

  @NonNull
  public final ConstraintLayout constraintHeader;

  @NonNull
  public final TextView textDay;

  @NonNull
  public final TextView textLeft;

  @NonNull
  public final TextView textTime;

  @NonNull
  public final TextView textTitle;

  @Bindable
  protected Coupon mItem;

  protected ItemCouponBinding(DataBindingComponent _bindingComponent, View _root,
      int _localFieldCount, Button buttonSelect, CardView cardTransaction,
      ConstraintLayout constraintHeader, TextView textDay, TextView textLeft, TextView textTime,
      TextView textTitle) {
    super(_bindingComponent, _root, _localFieldCount);
    this.buttonSelect = buttonSelect;
    this.cardTransaction = cardTransaction;
    this.constraintHeader = constraintHeader;
    this.textDay = textDay;
    this.textLeft = textLeft;
    this.textTime = textTime;
    this.textTitle = textTitle;
  }

  public abstract void setItem(@Nullable Coupon item);

  @Nullable
  public Coupon getItem() {
    return mItem;
  }

  @NonNull
  public static ItemCouponBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static ItemCouponBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable DataBindingComponent component) {
    return DataBindingUtil.<ItemCouponBinding>inflate(inflater, com.innomalist.taxi.rider.R.layout.item_coupon, root, attachToRoot, component);
  }

  @NonNull
  public static ItemCouponBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static ItemCouponBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable DataBindingComponent component) {
    return DataBindingUtil.<ItemCouponBinding>inflate(inflater, com.innomalist.taxi.rider.R.layout.item_coupon, null, false, component);
  }

  public static ItemCouponBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  public static ItemCouponBinding bind(@NonNull View view,
      @Nullable DataBindingComponent component) {
    return (ItemCouponBinding)bind(component, view, com.innomalist.taxi.rider.R.layout.item_coupon);
  }
}
