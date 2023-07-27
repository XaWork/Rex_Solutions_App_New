package com.innomalist.taxi.rider.databinding;

import android.databinding.Bindable;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.innomalist.taxi.common.models.Service;

public abstract class ItemServiceBinding extends ViewDataBinding {
  @NonNull
  public final ImageView image;

  @NonNull
  public final TextView textCost;

  @NonNull
  public final TextView textTitle;

  @Bindable
  protected Service mItem;

  protected ItemServiceBinding(DataBindingComponent _bindingComponent, View _root,
      int _localFieldCount, ImageView image, TextView textCost, TextView textTitle) {
    super(_bindingComponent, _root, _localFieldCount);
    this.image = image;
    this.textCost = textCost;
    this.textTitle = textTitle;
  }

  public abstract void setItem(@Nullable Service item);

  @Nullable
  public Service getItem() {
    return mItem;
  }

  @NonNull
  public static ItemServiceBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static ItemServiceBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable DataBindingComponent component) {
    return DataBindingUtil.<ItemServiceBinding>inflate(inflater, com.innomalist.taxi.rider.R.layout.item_service, root, attachToRoot, component);
  }

  @NonNull
  public static ItemServiceBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static ItemServiceBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable DataBindingComponent component) {
    return DataBindingUtil.<ItemServiceBinding>inflate(inflater, com.innomalist.taxi.rider.R.layout.item_service, null, false, component);
  }

  public static ItemServiceBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  public static ItemServiceBinding bind(@NonNull View view,
      @Nullable DataBindingComponent component) {
    return (ItemServiceBinding)bind(component, view, com.innomalist.taxi.rider.R.layout.item_service);
  }
}
