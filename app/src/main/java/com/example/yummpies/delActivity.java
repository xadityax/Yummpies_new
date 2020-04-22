package com.example.yummpies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class delActivity extends AppCompatActivity {
    private ListView mListView;
    private List<String> name= new ArrayList<>();
    private List<String> orderID=new ArrayList<>();
    private List<String> total=new ArrayList<>();
    private List<String> phone=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del);
        mListView=findViewById(R.id.list);
        FirebaseFirestore.getInstance().collection("Current_Orders").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    name.add(documentSnapshot.getString("user_name"));
                    orderID.add(documentSnapshot.getId());
                    total.add(String.valueOf(documentSnapshot.getDouble("total")));
                    phone.add(documentSnapshot.getString("user_phone"));
                }
                MyCustomAdapter adapter = new MyCustomAdapter(name,orderID,total,phone,delActivity.this);
                mListView.setAdapter(adapter);
            }
        });


    }
}
