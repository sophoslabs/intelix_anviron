/*
 * #  Copyright (c) 2019. Sophos Limited
 * #
 * #  Licensed under the Apache License, Version 2.0 (the "License");
 * #  you may not use this file except in compliance with the License.
 * #  You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * #  Unless required by applicable law or agreed to in writing, software
 * #  distributed under the License is distributed on an "AS IS" BASIS,
 * #  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #  See the License for the specific language governing permissions and
 * #  limitations under the License.
 */

package com.sophos.anviron.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sophos.anviron.R;
import com.aditya.filebrowser.Constants;
import com.aditya.filebrowser.FileChooser;
import com.sophos.anviron.ReportsActivity;
import com.sophos.anviron.database.DatabaseRepository;
import com.sophos.anviron.models.FileScanMapping;
import com.sophos.anviron.models.Scan;
import com.sophos.anviron.service.main.DatabaseService;
import com.sophos.anviron.util.main.CommonUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import at.markushi.ui.CircleButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuickScanFragment extends Fragment {

    ArrayList<File> filesToScan = null;
    View root = null;
    DatabaseRepository repository = null;

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


        root = inflater.inflate(R.layout.quick_scan_fragment, container, false);
        repository = new DatabaseRepository(getActivity().getApplication());
        ImageButton infoButton = root.findViewById(R.id.infoBtn);
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

        DecimalFormat df1 = new DecimalFormat(root.getContext().getString(R.string.decimal_format_1_precision));
        DecimalFormat df3 = new DecimalFormat(root.getContext().getString(R.string.decimal_format_3_precision));

        TextView txtTotalScannedFiles = root.findViewById(R.id.txtTotalScannedFilesQuick);
        txtTotalScannedFiles.setText(repository.getTotalScannedFilesQuick().toString());

        TextView txtTotalDetections = root.findViewById(R.id.txtTotalDetectionsQuick);
        txtTotalDetections.setText(repository.getTotalDetectionsQuick().toString());

        TextView txtTotalCost = root.findViewById(R.id.txtTotalCostQuick);
        txtTotalCost.setText(df3.format(repository.getTotalCostQuick()));

        TextView txtScanCoverage = root.findViewById(R.id.txtScanCoverageQuick);
        txtScanCoverage.setText("0 %"); //default we think that scan coverage is 0 % until below code updates it with actual value

        List<String> filesScannedQuickWithDuplicates = repository.getFilesScannedQuick();
        Set<String> set = new LinkedHashSet<>(filesScannedQuickWithDuplicates);
        ArrayList<String> filesScannedQuickUnique = new ArrayList<>(set);

        ArrayList<File> allFilesInUserSpace = CommonUtils.getAllFilesInUserSpace();
        ArrayList<String> allFilesPathsInUserSpace = new ArrayList<>();

        if (allFilesInUserSpace.size() > 0) {

            for (File file : allFilesInUserSpace) {
                allFilesPathsInUserSpace.add(file.toString());
            }

            Integer unScannedFiles = CommonUtils.listItemsDiffsCount(allFilesPathsInUserSpace, filesScannedQuickUnique);
            Double scanCoverage = 100.0 - ((unScannedFiles.doubleValue() / allFilesInUserSpace.size()) * 100.0);

            txtScanCoverage.setText(df1.format(scanCoverage) + " %");
        }
        return root;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        filesToScan = new ArrayList<>();

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9999 && data != null) {
            if (resultCode == Activity.RESULT_OK) {

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

                    fileScanMapping.setStatus("waiting for payment");

                    dbInstance.getMappingDAO().insert(fileScanMapping);
                }

                DialogFragment paymentFragment = PaymentFragment.newInstance(scanId);
                paymentFragment.show(getFragmentManager(), "paymentDialog");

            }
//            Intent intent = new Intent(getActivity(), ReportsActivity.class);
//            startActivity(intent);
        }
    }

}