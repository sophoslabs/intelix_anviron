package com.sophos.anviron.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import com.sophos.anviron.R;
import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;
import com.sophos.anviron.service.main.ScanService;
import com.sophos.anviron.util.main.CommonUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuickScanFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
//    private PageViewModel pageViewModel;
    private final String rootPath = Environment.getRootDirectory().getAbsolutePath();
    ArrayList<File> filesToScan = null;

    public static QuickScanFragment newInstance(int index) {
        QuickScanFragment fragment = new QuickScanFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
//        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

//        int current_page_index = pageViewModel.getMIndex();
//        Log.i("current_page_index:", Integer.toString(current_page_index));

        final View root = inflater.inflate(R.layout.quick_scan_fragment, container, false);

        final Button btnSelectFilesOrFolders = root.findViewById(R.id.btnSelectFilesOrFolders);

        btnSelectFilesOrFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Toast.makeText(getContext().getApplicationContext(), R.string.app_name, Toast.LENGTH_LONG).show();*/
                Intent i = new Intent(getActivity().getApplication().getApplicationContext(), FileChooser.class);
                i.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.MULTIPLE_SELECTION.ordinal());
                startActivityForResult(i, 9999);
            }
        });
        return root;
    }


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        filesToScan = new ArrayList<>();

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 9999 && data!=null){
            if (resultCode == -1){
                ArrayList<Uri> selectedFiles  = data.getParcelableArrayListExtra(Constants.SELECTED_ITEMS);
                int i = 0;
                while(i<selectedFiles.size()){
                    String path = selectedFiles.get(i++).getPath();
                    File file = new File(path);
                    if (file.isDirectory()) {
                        filesToScan = CommonUtils.getAllNestedFilesRecursively(filesToScan, file);
                    }
                    else
                        filesToScan.add(file);
                }
                Log.i("files", filesToScan.toString());
            }
        }

        ScanService scanService = new ScanService(filesToScan);
        scanService.scanFiles();

    }

}