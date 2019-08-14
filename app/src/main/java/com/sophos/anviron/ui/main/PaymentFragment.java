package com.sophos.anviron.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.sophos.anviron.MainActivity;
import com.sophos.anviron.R;
import com.sophos.anviron.ReportsActivity;
import com.sophos.anviron.dao.FileScanMappingDAO;
import com.sophos.anviron.service.main.DatabaseService;
import com.sophos.anviron.util.main.CommonUtils;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class PaymentFragment extends DialogFragment {

    View view;
    String scanId;
    String scanCost;
    String scanType = null;
    int filesToScan = 0;
    TextView txtFilesToScan;
    TextView txtScanCost;
    TextView txtScanType;
    Button btnPay;
    Button btnCancel;
    Context context;

    //PayTM variables
    double usdToInr = 70.00;

    public static PaymentFragment newInstance(String scanId) {
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
        context = getActivity().getApplication().getApplicationContext();

        txtFilesToScan = view.findViewById(R.id.txtFilesToScan);
        txtScanType = view.findViewById(R.id.txtScanType);
        txtScanCost = view.findViewById(R.id.txtScanCost);

        btnPay = view.findViewById(R.id.btnPay);
        btnCancel = view.findViewById(R.id.btnCancel);

        scanId = getArguments().getString("scanId");

        DecimalFormat df_dollar = new DecimalFormat(context.getString(R.string.decimal_format_3_precision));

        Double quickCost = Double.parseDouble(context.getString(R.string.quick_cost_dollar));
        Double staticCost = Double.parseDouble(context.getString(R.string.static_cost_dollar));
        Double dynamicCost = Double.parseDouble(context.getString(R.string.dynamic_cost_dollar));

        final DatabaseService dbInstance = DatabaseService.getInstance(context);
        List<FileScanMappingDAO.CustomJoinFileScanMapping> customJoinFileScanMappings = dbInstance.getMappingDAO().getFilesByScanId(scanId);

        for (FileScanMappingDAO.CustomJoinFileScanMapping mapping : customJoinFileScanMappings) {

            if (scanType == null) {
                scanType = mapping.scanType;
            }

            filesToScan++;
        }

        txtScanType.setText(scanType);
        txtFilesToScan.setText(filesToScan + "");

        if (scanType.equalsIgnoreCase("quick")) {
            scanCost = df_dollar.format(filesToScan * quickCost);
            txtScanCost.setText("$ " + scanCost);
        } else if (scanType.equalsIgnoreCase("static")) {
            scanCost = df_dollar.format(filesToScan * staticCost);
            txtScanCost.setText("$ " + scanCost);
        } else if (scanType.equalsIgnoreCase("dynamic")) {
            scanCost = df_dollar.format(filesToScan * dynamicCost);
            txtScanCost.setText("$ " + scanCost);
        } else {
            scanCost = df_dollar.format(filesToScan * 0);
            txtScanCost.setText("$ " + scanCost);
        }

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(context, "Redirecting to payment gateway...please hold on...", Toast.LENGTH_LONG);
                toast.show();
                toast.show();
                toast.show();
                toast.show();
                toast.show();
                try {

                    // For staging service
                    PaytmPGService Service = PaytmPGService.getStagingService();

                    // For production service
                    // PaytmPGService Service = PaytmPGService.getProductionService();

                    HashMap<String, String> paramMap = new HashMap<>();

                    paramMap.put("MID", context.getString(R.string.paytm_mid));

                    // Key in your staging and production MID available in your dashboard
                    paramMap.put("ORDER_ID", scanId);

                    paramMap.put("CUST_ID", CommonUtils.getUniqueImeiId(context));

                    // paramMap.put("MOBILE_NO", "7777777777");
                    // paramMap.put("EMAIL", "dhwanitshah55@gmail.com");

                    paramMap.put("CHANNEL_ID", "WAP");

                    DecimalFormat df_inr = new DecimalFormat("#.##");

                    String scanCostInr = "0.00";

                    if ((Double.parseDouble(scanCost) * usdToInr) < 1.00) {
                        scanCostInr = "1.00";
                    } else {
                        scanCostInr = df_inr.format(Double.parseDouble(scanCost) * usdToInr);
                    }

                    paramMap.put("TXN_AMOUNT", scanCostInr);

                    paramMap.put("WEBSITE", "WEBSTAGING");

                    // This is the staging value. Production value is available in your dashboard
                    paramMap.put("INDUSTRY_TYPE_ID", "Retail");

                    // This is the staging value. Production value is available in your dashboard
                    paramMap.put("CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + scanId);

                    // Generating checksum hash remotely from anviron server (ec2)
                    String checksum = null;
                    try {
                        checksum = generateChecksum(scanId, CommonUtils.getUniqueImeiId(context), scanCostInr);
                    } catch (Exception e) {
                        Log.e("AnvironException occurred while generating checksum", e.getMessage());
                        e.printStackTrace();
                        handlePaymentFailure("Payment has failed due to due to payment gateway not reachable at the moment. Scan is aborted. Please try again after sometime.", dbInstance, scanId);
                    }

                    if(checksum=="" && checksum.length()==0){
                        handlePaymentFailure("Payment has failed due to payment gateway not reachable at the moment. Scan is aborted. Please try again after sometime.", dbInstance, scanId);
                        return;
                    }

                    paramMap.put("CHECKSUMHASH", checksum);

                    PaytmOrder order = new PaytmOrder(paramMap);

                    Service.initialize(order, null);

                    Service.startPaymentTransaction(getActivity(), true, true, new PaytmPaymentTransactionCallback() {

                        /*Call Backs*/
                        public void someUIErrorOccurred(String inErrorMessage) {
                        }

                        public void onTransactionResponse(Bundle inResponse) {

                            try {
                                if (inResponse.toString().contains("STATUS=TXN_SUCCESS")) {
                                    Toast.makeText(context, "Payment is successful. Scan is initiated.", Toast.LENGTH_LONG).show();
                                    dbInstance.getMappingDAO().updateStatus("in progress", scanId);
                                    Intent intent = new Intent(getActivity(), ReportsActivity.class);
                                    startActivity(intent);
                                } else {
                                    handlePaymentFailure("Payment has failed. Scan is aborted.", dbInstance, scanId);
                                }
                            } catch (Exception e) {
                                Log.e("AnvironException in transaction response", e.getMessage());
                            }
                        }

                        public void networkNotAvailable() {
                            handlePaymentFailure("Payment has failed due to network not available. Scan is aborted.", dbInstance, scanId);
                        }

                        public void clientAuthenticationFailed(String inErrorMessage) {
                            handlePaymentFailure("Payment has failed due to authentication failure. Scan is aborted.", dbInstance, scanId);
                        }

                        public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                            handlePaymentFailure("Payment has failed due to error loading page. Scan is aborted.", dbInstance, scanId);
                        }

                        public void onBackPressedCancelTransaction() {
                            handlePaymentFailure("Payment has failed. Scan is aborted.", dbInstance, scanId);
                        }

                        public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                            handlePaymentFailure("Payment has been cancelled. Scan is aborted.", dbInstance, scanId);
                        }
                    });


                } catch (Exception e) {
                    Log.e("AnvironException occurred while processing payment", e.getMessage());
                    e.printStackTrace();
                }
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePaymentFailure("Payment has been cancelled. Scan is aborted.", dbInstance, scanId);
            }
        });

        return view;
    }

    public void handlePaymentFailure(String message, DatabaseService dbInstance, String scanId) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
        toast.show();
        toast.show();
        toast.show();
        toast.show();
        dbInstance.getMappingDAO().updateStatus("payment failed", scanId);
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    public String generateChecksum(String orderId, String customerId, String scanCostInr) throws Exception {

        String data = "";
        HttpURLConnection http_con = null;

        try {

            URL url = new URL("http://63.32.249.122:3000/generate_checksum");
            // Proxy webproxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.labs.localsite.sophos", 8080));
            // HttpURLConnection http_con = (HttpURLConnection) url.openConnection(webproxy);
            http_con = (HttpURLConnection) url.openConnection();
            http_con.setDoOutput(true);
            http_con.setConnectTimeout(5000);
            http_con.setReadTimeout(5000);
            http_con.setRequestMethod("POST");
            http_con.setRequestProperty("Accept-Charset", "UTF-8");
            // http_con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            http_con.setRequestProperty("Content-Type", "application/json; utf-8");

            JSONObject postData = new JSONObject();
            postData.put("ORDER_ID", orderId);
            postData.put("CUST_ID", customerId);
            postData.put("TXN_AMOUNT", scanCostInr);

            //String postData = String.format("orderId=%s&customerId=%s", URLEncoder.encode(orderId, "UTF-8"), URLEncoder.encode(customerId, "UTF-8"));

            OutputStreamWriter wr = new OutputStreamWriter(http_con.getOutputStream());
            wr.write(postData.toString());
            wr.flush();

        /*try(DataOutputStream wr = new DataOutputStream(http_con.getOutputStream())) {
            wr.write(postData.getBytes("UTF-8"));
        }*/


            InputStream inputStream = http_con.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                data = data + line;
            }

        } catch (Exception e) {
            System.out.println(e.toString());
            data = "";

        } finally {
            if (http_con != null) {
                http_con.disconnect();
            }
        }

        //Object obj = new JSONParser().parse(data);
        //JSONObject jo = (JSONObject) obj;
        //String acc_token = String.valueOf(jo.get("access_token"));
        //setAccessToken(acc_token);
        //setTokenDateTime();

        return data;

    }


}
