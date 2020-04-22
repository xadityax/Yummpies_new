package com.example.yummpies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
    private List<String> name;
    private List<String> orderid;
    private List<String> total;
    private List<String> phone;
    private Context context;
    private DocumentReference documentReference;

    public MyCustomAdapter(List name,List orderid,List total,List phone,Context context ){
        this.name=name;
        this.orderid=orderid;
        this.total=total;
        this.phone=phone;
        this.context=context;
    }


    @Override
    public int getCount() {
        return this.name.size();
    }

    @Override
    public Object getItem(int position) {
        return this.name.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.customlayout, null);

        TextView name1=view.findViewById(R.id.textView);
        TextView orderid1=view.findViewById(R.id.textView2);
        TextView total1=view.findViewById(R.id.textView8);
        TextView phone1=view.findViewById(R.id.textView9);
        Button confirm=view.findViewById(R.id.button2);
        if (position<name.size()) {
       name1.setText("Name: " + name.get(position));
       orderid1.setText("OrderID: " + orderid.get(position));
       total1.setText("Total: " + total.get(position));
       phone1.setText("Phone: " + phone.get(position));

       confirm.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FirebaseFirestore.getInstance().collection("Current_Orders").document(orderid.get(position)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                       documentReference = FirebaseFirestore.getInstance().collection("Current_Orders").document(documentSnapshot.getId());
                       Map a = new HashMap();
                       a.put("confirm", true);
                       documentReference.set(a, SetOptions.merge());
                       Toast.makeText(context, "delivery confirmed", Toast.LENGTH_LONG).show();
                   }
               });
//             // FirebaseFirestore.getInstance().collection("Current_Orders").whereEqualTo("user_name", name).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                   @Override
//                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                       for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                           boolean yes = documentSnapshot.getBoolean("confirm");
//                           if (!yes) {
//                               documentReference = FirebaseFirestore.getInstance().collection("Current_Orders").document(documentSnapshot.getId());
//                               documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                   @Override
//                                   public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                       Map a = new HashMap();
//                                       a.put("confirm", true);
//                                       documentReference.set(a, SetOptions.merge());
//                                       Toast.makeText(context, "delivery confirmed", Toast.LENGTH_SHORT).show();
//                                   }
//                               });
//
//                           }
//                           break;
//                       }
//
//                   }
//               });

           }
       });
   }



        return view;
    }
}
