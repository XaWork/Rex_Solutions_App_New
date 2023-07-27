package com.innomalist.taxi.common.databinding;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public abstract class DialogWriteComplaintBinding extends ViewDataBinding {
  @NonNull
  public final LinearLayout customDialogLayoutDesignUserInput;

  @NonNull
  public final TextInputEditText textContent;

  @NonNull
  public final TextInputLayout textLayoutContent;

  @NonNull
  public final TextInputLayout textLayoutSubject;

  @NonNull
  public final TextInputEditText textSubject;

  protected DialogWriteComplaintBinding(DataBindingComponent _bindingComponent, View _root,
      int _localFieldCount, LinearLayout customDialogLayoutDesignUserInput,
      TextInputEditText textContent, TextInputLayout textLayoutContent,
      TextInputLayout textLayoutSubject, TextInputEditText textSubject) {
    super(_bindingComponent, _root, _localFieldCount);
    this.customDialogLayoutDesignUserInput = customDialogLayoutDesignUserInput;
    this.textContent = textContent;
    this.textLayoutContent = textLayoutContent;
    this.textLayoutSubject = textLayoutSubject;
    this.textSubject = textSubject;
  }

  @NonNull
  public static DialogWriteComplaintBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static DialogWriteComplaintBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable DataBindingComponent component) {
    return DataBindingUtil.<DialogWriteComplaintBinding>inflate(inflater, com.innomalist.taxi.common.R.layout.dialog_write_complaint, root, attachToRoot, component);
  }

  @NonNull
  public static DialogWriteComplaintBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  @NonNull
  public static DialogWriteComplaintBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable DataBindingComponent component) {
    return DataBindingUtil.<DialogWriteComplaintBinding>inflate(inflater, com.innomalist.taxi.common.R.layout.dialog_write_complaint, null, false, component);
  }

  public static DialogWriteComplaintBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  public static DialogWriteComplaintBinding bind(@NonNull View view,
      @Nullable DataBindingComponent component) {
    return (DialogWriteComplaintBinding)bind(component, view, com.innomalist.taxi.common.R.layout.dialog_write_complaint);
  }
}
