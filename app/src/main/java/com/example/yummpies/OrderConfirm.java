package com.example.yummpies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OrderConfirm extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Current_Orders");
    private DocumentReference documentReference;



    List<String> itemlist = new ArrayList<>();

    private String EATERY_ID = "eatery_id";
    private String EATERY_NAME = "eatery_name";
    private String ORDER_TIME = "order_time";
    private String TOTAL = "total";

    private Location currentLocation;
    String order_id;
    private String TAG = "OrderConfirm";
    TextView tv, tv1, tv2, tv3, tv4;
    ListView ls;

    FirebaseUser user;

    boolean temp = true;
    ProgressBar spin;




    private FusedLocationProviderClient fusedLocationProviderClient;




    CoordinatorLayout coordinatorLayout;



    public void showSnackBar(String s)
    {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, s, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);


        tv = findViewById(R.id.textView5);
        tv1 = findViewById(R.id.textView15);
        tv2 = findViewById(R.id.textView13);
        tv3 = findViewById(R.id.textView14);
        tv4 = findViewById(R.id.textView16);
        ls = findViewById(R.id.listView22);
        spin=findViewById(R.id.spinner);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        spin.setVisibility(View.VISIBLE);
        getLocation();
        user = getIntent().getParcelableExtra("user");

        order_id = getIntent().getExtras().getString("order_id");
        tv.append(order_id);
        documentReference = collectionReference.document(order_id);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tv1.setText("Hotel: " + documentSnapshot.get(EATERY_NAME).toString());
                tv2.setText("Total: "+documentSnapshot.getDouble(TOTAL).toString());

                Timestamp timestamp = (Timestamp) documentSnapshot.get(ORDER_TIME);
                tv3.append(timestamp.toDate().toString());
                Map<String, Double> map = (Map<String, Double>) documentSnapshot.get("order");
                String name;
                int count;
                double price;
                int iend;
                for(String s:map.keySet())
                {


                    iend = s.indexOf("$");
                    name = s.substring(0,iend);
                    price = Double.parseDouble(s.substring(iend+1));
                    count = (int)Double.parseDouble(map.get(s).toString());
                    itemlist.add("Item name: "+name+"\n"+"Price: "+price+"\n" +"Quantity: "+count+"\n"+ "Sub-amount: "+price*count+"\n"+"\n");


                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(OrderConfirm.this, android.R.layout.simple_list_item_1, itemlist);
                 ls.setAdapter(adapter);








            }
        });
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("email",user.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                   final String s=documentSnapshot.getString("fName");
                   final String k=documentSnapshot.getString("phone");
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map a = new HashMap();
                            a.put("user_name",s);
                            a.put("user_phone",k);
                            documentReference.set(a,SetOptions.merge());
                        }
                    });
                    break;
                }


            }
        });



//
//        FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if(documentSnapshot.getTimestamp("first_order_time")==null)
//                {
//                    HashMap<String, Object> timestampNow = new HashMap<>();
//                    timestampNow.put("first_order_time", Timestamp.now());
//                    FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).set(timestampNow, SetOptions.merge());
//                }
//                else
//                {
//                    FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            Timestamp tt = documentSnapshot.getTimestamp("first_order_time");
//
//                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
//
//                            Date t1 = tt.toDate();
//
//                            Date t2 = Timestamp.now().toDate();
//
//                            long diff = t2.getTime() - t1.getTime();
//
//                            int daysBetween = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
//
////                            //int order_count = Integer.parseInt(documentSnapshot.get("order_count").toString());
////                            Map a = new HashMap();
////                            if(order_count>=3 && daysBetween<7)
////                            {
////
////                                a.put("discount", true);
////
////                            }
////                            else
////                            {
////                                a.put("discount", false);
////
////                            }
////                            FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).set(a, SetOptions.merge());
//
//
//
//                        }
//                    });
//                }
//            }
//        });




        FirebaseFirestore.getInstance().collection("Current_Orders").document(order_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null)
                {
                    Log.e(TAG, "OnEvent", e);
                    return;
                }
                if(documentSnapshot!=null)
                {

                    if(documentSnapshot.get("confirm").toString()=="true")
                    {
                        spin.setVisibility(View.GONE);
                        tv4.setText("Order Confirmed!");
                        /*funcAct();*/
                    }


                }
                else {
                    Log.e(TAG, "onEvent:NULL");
                }
            }
        });

    }

   /* public void funcAct()
    {
        if(temp)
        {
            Intent intent = new Intent(OrderConfirm.this, Deliver_Ordered.class);
            intent.putExtra("user", user);
            intent.putExtra("order_id",order_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
            temp=false;
        }

    } */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        return;
    }


    void getLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            final Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        currentLocation = (Location) task.getResult();

                        if(currentLocation==null)
                        {
                            showSnackBar("Location Not Found");
                            return;
                        }

                        GeoPoint mypos = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                        final Map user_instance = new HashMap<>();

                        user_instance.put("location",mypos);
                        user_instance.put("initial_user_location", mypos);

                        FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).set(user_instance, SetOptions.merge());

                    }
                }
            });
        }
        catch(Exception e) {
            e.printStackTrace();

        }
    }


}
