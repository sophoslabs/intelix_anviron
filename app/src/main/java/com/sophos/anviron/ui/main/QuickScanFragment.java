package com.sophos.anviron.ui.main;

import android.app.AlertDialog;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.sophos.anviron.R;
import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;
import com.sophos.anviron.database.DatabaseRepository;
import com.sophos.anviron.models.FileScanMapping;
import com.sophos.anviron.models.Scan;
import com.sophos.anviron.service.main.DatabaseService;
import com.sophos.anviron.util.main.CommonUtils;

import java.io.File;
import java.util.ArrayList;

import at.markushi.ui.CircleButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuickScanFragment extends Fragment {

    private final String rootPath = Environment.getRootDirectory().getAbsolutePath();
    ArrayList<File> filesToScan = null;

    public static QuickScanFragment newInstance(int index) {
        QuickScanFragment fragment = new QuickScanFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {


        final View root = inflater.inflate(R.layout.quick_scan_fragment, container, false);
        DatabaseRepository repository = new DatabaseRepository(getActivity().getApplication());
        ImageButton infoButton = (ImageButton) root.findViewById(R.id.infoBtn);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                alertDialogBuilder.setTitle(R.string.tab_text_1);
                alertDialogBuilder.setMessage(R.string.quick_description).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        final CircleButton btnSelectFilesOrFolders = root.findViewById(R.id.btnQuickScan);

        btnSelectFilesOrFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Toast.makeText(getContext().getApplicationContext(), R.string.app_name, Toast.LENGTH_LONG).show();*/
                Intent i = new Intent(getActivity().getApplication().getApplicationContext(), FileChooser.class);
                i.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.MULTIPLE_SELECTION.ordinal());
                // i.putExtra(Constants.INITIAL_DIRECTORY, rootPath);
                startActivityForResult(i, 9999);
            }
        });

        TextView txtTotalScannedFiles = root.findViewById(R.id.txtTotalScannedFilesQuick);
        txtTotalScannedFiles.setText(repository.getTotalScannedFilesQuick().toString());

        TextView txtTotalDetections = root.findViewById(R.id.txtTotalDetectionsQuick);
        txtTotalDetections.setText(repository.getTotalDetectionsQuick().toString());
        return root;
    }


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        filesToScan = new ArrayList<>();

        super.onActivityResult(requestCode, resultCode, data);

        //see request code for deep scan
        if (requestCode == 9999 && data != null) {
            if (resultCode == -1) {
                ArrayList<Uri> selectedFiles = data.getParcelableArrayListExtra(Constants.SELECTED_ITEMS);
                int i = 0;
                while (i < selectedFiles.size()) {
                    String path = selectedFiles.get(i++).getPath();
                    File file = new File(path);
                    if (file.isDirectory()) {
                        filesToScan = CommonUtils.getAllNestedFilesRecursively(filesToScan, file);
                    } else
                        filesToScan.add(file);
                }
                Log.i("files", filesToScan.toString());

                DatabaseService dbInstance = DatabaseService.getInstance(getActivity().getApplication().getApplicationContext());

                Scan scan = new Scan();
                String scanId = CommonUtils.generateUUID();
                scan.setScan_id(scanId);
                scan.setType("quick");
                scan.setIs_file_uploaded(false);
                scan.setSubmission_time(CommonUtils.getCurrentDateTime());
                scan.setCompletion_time(null);
                dbInstance.getScanDAO().insert(scan);

                for (File file : filesToScan) {

                    String fileId = dbInstance.getFileDAO().getFileIdByFilePath(file.toString());

                    if (fileId == null || fileId == "") {
                        fileId = CommonUtils.generateUUID();
                        com.sophos.anviron.models.File fileObj = new com.sophos.anviron.models.File();
                        fileObj.setFile_id(fileId);
                        fileObj.setFile_path(file.toString());
                        dbInstance.getFileDAO().insert(fileObj);
                    }

                    //Add file-scan mapping
                    FileScanMapping fileScanMapping = new FileScanMapping();
                    fileScanMapping.setFile_id(fileId);
                    fileScanMapping.setScan_id(scanId);
                    fileScanMapping.setStatus("in progress");

                    dbInstance.getMappingDAO().insert(fileScanMapping);

                }
            }
        }
    }

}