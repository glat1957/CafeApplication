// Giorgio Latour
// Cafe Application
// IHRTLUHC

/*
Launcher Image: espresso cup by Symbolon from the Noun Project
 */

package edu.lawrence.cafeapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewOrderActivity extends AppCompatActivity {

    private int ORDER_NUM = 0;
    private ListView orderedItems;
    private JSONArray menuItems = null;
    private JSONObject handle = null;
    private int selected_handle = -1;
    private TextView testTextView;
    private EditText customerName;
    private EditText phoneNumber;
    public static final String orderNumber = "edu.lawrence.cafeapplication.orderNumber";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        orderedItems = (ListView) findViewById(R.id.itemsOrdered);
        testTextView = (TextView) findViewById(R.id.orderTextView);
        customerName = (EditText) findViewById(R.id.nameField);
        phoneNumber = (EditText) findViewById(R.id.phoneNumField);

        Intent intent = getIntent();
        String orderNum = intent.getStringExtra(MenuActivity.orderNumber);
        ORDER_NUM = Integer.parseInt(orderNum);

        loadOrder();
        testTextView.setText("Order for Order ID: " + Integer.toString(ORDER_NUM));
    }

    public void removeItem(View view) {
        // Get product ID based on selection number.
        try {
            JSONObject selectedProduct = menuItems.getJSONObject(selected_handle);
            int productID = Integer.parseInt(selectedProduct.getString("iditem"));

            new removeItemTask(productID).execute();
            loadOrder();
        } catch (JSONException ex) {
            Log.d("CafeApp", "Exception removing order: " + ex.getMessage());
        }
    }

    private class removeItemTask extends AsyncTask<String, Void, String> {
        private String uri;

        removeItemTask(int itemToRemove) {
            uri = "http://" + URIHandler.hostName + "/RESTCafe/api/item/" + itemToRemove;
        }

        @Override
        protected String doInBackground(String... urls) {
            URIHandler.doDelete(uri);
            return null;
        }

        @Override
        protected void onPostExecute(String idOrder) {
            Context deletedcontext = getApplicationContext();
            CharSequence deletedtext = "Item removed from order.";
            int deletedduration = Toast.LENGTH_SHORT;

            Toast itemdeletedToast = Toast.makeText(deletedcontext, deletedtext, deletedduration);
            itemdeletedToast.show();
        }
    }

    public void updateOrder(View view) {
        String customerNameStr = customerName.getText().toString();
        String phoneNumStr = phoneNumber.getText().toString();

        if(!customerNameStr.isEmpty() && !phoneNumStr.isEmpty()) {
            JSONObject updateOrder = new JSONObject();
            try {
                updateOrder.put("idorder", ORDER_NUM);
                updateOrder.put("customer", customerNameStr);
                updateOrder.put("phone", phoneNumStr);
                new updateOrderTask(updateOrder).execute();
            } catch (JSONException ex) {
                Log.d("CafeApp", "Exception in addToOrder: " + ex.getMessage());
            }

            // Set order number to zero to reset app then go to first activity.
            ORDER_NUM = 0;
            Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra(orderNumber, Integer.toString(ORDER_NUM));
            startActivity(intent);
        } else{
            Context errorcontext = getApplicationContext();
            CharSequence errortext = "Please enter a name and phone number.";
            int errorduration = Toast.LENGTH_SHORT;

            Toast errorToast = Toast.makeText(errorcontext, errortext, errorduration);
            errorToast.show();
        }

    }

    private class updateOrderTask extends AsyncTask<String, Void, String> {
        private String uri;
        private JSONObject updateOrder;

        updateOrderTask(JSONObject json) {
            uri = "http://" + URIHandler.hostName + "/RESTCafe/api/invoice/" + Integer.toString(ORDER_NUM);
            this.updateOrder = json;
        }

        @Override
        protected String doInBackground(String... urls) {
            return URIHandler.doPut(uri, updateOrder.toString());
        }

        @Override
        protected void onPostExecute(String idOrder) {
            Context sentcontext = getApplicationContext();
            CharSequence senttext = "Order sent!";
            int sentduration = Toast.LENGTH_SHORT;

            Toast orderSentToast = Toast.makeText(sentcontext, senttext, sentduration);
            orderSentToast.show();
        }
    }


    public void loadOrder() {
        new loadOrderTask().execute();
    }

    private class loadOrderTask extends AsyncTask<String, Void, String> {
        private String uri;

        loadOrderTask() {
            uri = "http://" + URIHandler.hostName + "/RESTCafe/api/handle?order=" + Integer.toString(ORDER_NUM);
        }

        @Override
        protected String doInBackground(String... urls) {
            return URIHandler.doGet(uri, "0");
        }

        @Override
        protected void onPostExecute(String result) {
            // Populate the ListView with product results from query.
            populateOrder(result);
        }

    }

    private void populateOrder(String json) {
        String[] handleStrs = null;

        try {
            menuItems = new JSONArray(json);
            handleStrs = new String[menuItems.length()];
            for (int n = 0; n < handleStrs.length; n++) {
                handle = menuItems.getJSONObject(n);
                handleStrs[n] = handle.getString("productname") + "\t\t" + handle.getString("productcost");
            }
        } catch (JSONException ex) {
            Log.d("CafeApp", "Exception in populateMenu: " + ex.getMessage());
            handleStrs = new String[0];
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, handleStrs);
        orderedItems.setAdapter(adapter);

        orderedItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int i, long l) {
                // remember the selection
                selected_handle = i;
            }
        });
    }
}
