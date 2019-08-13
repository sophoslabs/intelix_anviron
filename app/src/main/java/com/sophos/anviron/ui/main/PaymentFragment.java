package com.sophos.anviron.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sophos.anviron.R;
import com.sophos.anviron.dao.FileScanMappingDAO;
import com.sophos.anviron.service.main.DatabaseService;

import java.text.DecimalFormat;
import java.util.List;

public class PaymentFragment extends DialogFragment{

    View view;
    String scanId;
    String scanType = null;
    int filesToScan = 0;
    TextView txtFilesToScan;
    TextView txtScanCost;
    TextView txtScanType;



    public static PaymentFragment newInstance(String scanId){
        PaymentFragment paymentFragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putString("scanId", scanId);
        paymentFragment.setArguments(args);
        return paymentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.payment_fragment, container, false);
        Context context = getActivity().getApplication().getApplicationContext();

        txtFilesToScan = view.findViewById(R.id.txtFilesToScan);
        txtScanType = view.findViewById(R.id.txtScanType);
        txtScanCost = view.findViewById(R.id.txtScanCost);

        scanId = getArguments().getString("scanId");


        DecimalFormat df= new DecimalFormat(context.getString(R.string.decimal_format_3_precision));
        Double quickCost = Double.parseDouble(context.getString(R.string.quick_cost_dollar));
        Double staticCost = Double.parseDouble(context.getString(R.string.static_cost_dollar));
        Double dynamicCost = Double.parseDouble(context.getString(R.string.dynamic_cost_dollar));

        DatabaseService dbInstance = DatabaseService.getInstance(context);
        List<FileScanMappingDAO.CustomJoinFileScanMapping> customJoinFileScanMappings = dbInstance.getMappingDAO().getFilesByScanId(scanId);

        for (FileScanMappingDAO.CustomJoinFileScanMapping mapping : customJoinFileScanMappings) {

            if(scanType==null){
                scanType = mapping.scanType;
            }

            filesToScan++;
        }

        txtScanType.setText(scanType);
        txtFilesToScan.setText(filesToScan + "");

        if(scanType.equalsIgnoreCase("quick")){
            txtScanCost.setText(df.format(filesToScan * quickCost));
        }
        else if(scanType.equalsIgnoreCase("static")){
            txtScanCost.setText(df.format(filesToScan * staticCost));
        }
        else if (scanType.equalsIgnoreCase("dynamic")){
            txtScanCost.setText(df.format(filesToScan * dynamicCost));
        }
        else{
            txtScanCost.setText(df.format(filesToScan * 0));
        }

        return view;
    }

}
