package com.sweak.smartalarm.features.menu;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.sweak.smartalarm.R;
import com.sweak.smartalarm.databinding.FragmentAboutDialogBinding;

public class AboutDialogFragment extends DialogFragment {

    private FragmentAboutDialogBinding mBinding;

    public AboutDialogFragment() {
    }

    public static AboutDialogFragment newInstance() {
        return new AboutDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.about)
                .setPositiveButton(R.string.alert_dialog_ok, null);

        mBinding = FragmentAboutDialogBinding.inflate(requireActivity().getLayoutInflater());

        dialogBuilder.setView(mBinding.getRoot());
        return dialogBuilder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}