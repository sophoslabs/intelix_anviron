package com.sophos.anviron.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;
import com.sophos.anviron.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class DeepScanFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private PageViewModel pageViewModel;
    private final String rootPath = Environment.getRootDirectory().getAbsolutePath();

    public static DeepScanFragment newInstance(int index) {
        DeepScanFragment fragment = new DeepScanFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

/*    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 9999:
                Log.i("Test", "Result URI " + data.toString());
                break;
        }
    }*/

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        int current_page_index  = pageViewModel.getMIndex();
        Log.i("current_page_index:", Integer.toString(current_page_index));


/*
        final Button btnSelectFilesOrFolders = root.findViewById(R.id.btnSelectFilesOrFolders);

        btnSelectFilesOrFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext().getApplicationContext(), R.string.app_name, Toast.LENGTH_LONG).show();
                Intent i = new Intent(getActivity().getApplication().getApplicationContext(), FileChooser.class);
                i.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.MULTIPLE_SELECTION.ordinal());
                startActivityForResult(i, 9999);
            }
        });
*/

        final View root = inflater.inflate(R.layout.deep_scan_fragment, container, false);
        final TextView textView = root.findViewById(R.id.section_label);
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }
}