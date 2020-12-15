package com.example.app_anima;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentMemo extends Fragment {
    ViewGroup viewGroup ;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup =  (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);


        return viewGroup;
    }
}
