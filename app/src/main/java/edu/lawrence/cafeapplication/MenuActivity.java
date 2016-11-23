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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

public class MenuActivity extends AppCompatActivity {

    private static int ORDER_NUM = 0;
    public static final String orderNumber = "edu.lawrence.cafeapplication.orderNumber";
    private ListView menu;
    private TextView testTextView;
    private int selected_handle = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        menu = (ListView) findViewById(R.id.menuList);
        testTextView = (TextView) findViewById(R.id.menuTextView);
        loadMenu();
    }

    public void viewOrder(View view) {
        Intent intent = new Intent(this, ViewOrderActivity.class);
        intent.putExtra(orderNumber, Integer.toString(ORDER_NUM));
        startActivity(intent);
    }

    public void addToOrder(View view) {
        // Create order if not already created.
        if (ORDER_NUM == 0) {
            JSONObject unknownOrder = new JSONObject();
            try {
                unknownOrder.put("customer", "unknown");
                unknownOrder.put("phone", "unknown");
                new CreateOrderTask(unknownOrder).execute();
            } catch (JSONException ex) {
                Log.d("CafeApp", "Exception in addToOrder: " + ex.getMessage());
            }
        } // If order has already been created, just add the item to the order.
        else {
            JSONObject selectedItem = new JSONObject();
            try {
                selectedItem.put("ordernumber", Integer.toString(ORDER_NUM));
                selectedItem.put("product", Integer.toString(selected_handle + 1));
                new addItemToOrderTask(selectedItem).execute();
            } catch (JSONException ex) {
                Log.d("CafeApp", "Exception in addToOrder: " + ex.getMessage());
            }
        }
    }

    private class CreateOrderTask extends AsyncTask<String, Void, String> {
        private String uri;
        private JSONObject unknownOrder;

        CreateOrderTask(JSONObject json) {
            uri = "http://" + URIHandler.hostName + "/RESTCafe/api/invoice/";
            this.unknownOrder = json;
        }

        @Override
        protected String doInBackground(String... urls) {
            return URIHandler.doPost(uri, unknownOrder.toString());
        }

        @Override
        protected void onPostExecute(String idOrder) {
            ORDER_NUM = Integer.parseInt(idOrder);
            testTextView.setText("Menu, Order ID: " + idOrder);

            Context context = getApplicationContext();
            CharSequence text = "Order created!";
            int duration = Toast.LENGTH_SHORT;

            Toast orderCreatedToast = Toast.makeText(context, text, duration);
            orderCreatedToast.show();

            // Add selected item to order after creating the order.
            JSONObject selectedItem = new JSONObject();
            try {
                selectedItem.put("ordernumber", Integer.toString(ORDER_NUM));
                selectedItem.put("product", Integer.toString(selected_handle + 1));
                new addItemToOrderTask(selectedItem).execute();
            } catch (JSONException ex) {
                Log.d("CafeApp", "Exception in addToOrder: " + ex.getMessage());
            }
        }
    }

    private class addItemToOrderTask extends AsyncTask<String, Void, String> {
        private String uri;
        private JSONObject unknownOrder;

        addItemToOrderTask(JSONObject json) {
            uri = "http://" + URIHandler.hostName + "/RESTCafe/api/item/";
            this.unknownOrder = json;
        }

        @Override
        protected String doInBackground(String... urls) {
            return URIHandler.doPost(uri, unknownOrder.toString());
        }

        @Override
        protected void onPostExecute(String idOrder) {
            Context context = getApplicationContext();
            CharSequence text = "Item added to order!";
            int duration = Toast.LENGTH_SHORT;

            Toast itemAddedToast = Toast.makeText(context, text, duration);
            itemAddedToast.show();
        }
    }

    public void loadMenu() {
        new loadMenuTask().execute();
    }

    private class loadMenuTask extends AsyncTask<String, Void, String> {
        private String uri;

        loadMenuTask() {
            uri = "http://" + URIHandler.hostName + "/RESTCafe/api/product";
        }

        @Override
        protected String doInBackground(String... urls) {
            return URIHandler.doGet(uri, "0");
        }

        @Override
        protected void onPostExecute(String result) {
            // Populate the ListView with product results from query.
            populateMenu(result);
        }

    }

    private void populateMenu(String json) {
        JSONArray menuItems = null;
        String[] handleStrs = null;

        try {
            menuItems = new JSONArray(json);
            handleStrs = new String[menuItems.length()];
            for (int n = 0; n < handleStrs.length; n++) {
                JSONObject handle = menuItems.getJSONObject(n);
                handleStrs[n] = handle.getString("name") + "\t\t" + handle.getString("cost");
            }
        } catch (JSONException ex) {
            Log.d("CafeApp", "Exception in populateMenu: " + ex.getMessage());
            handleStrs = new String[0];
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, handleStrs);
        menu.setAdapter(adapter);

        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int i, long l) {
                // remember the selection
                selected_handle = i;
            }
        });
    }


}


