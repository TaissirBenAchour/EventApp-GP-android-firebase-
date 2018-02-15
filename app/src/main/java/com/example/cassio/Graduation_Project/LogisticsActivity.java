package com.example.cassio.Graduation_Project;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassio.Graduation_Project.models.Sponsors;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LogisticsActivity extends Fragment {

    DatabaseReference sponsorsRef, userRef ;
    RecyclerView sponsor_list;

    View mView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.activity_logistics, container, false);

         sponsorsRef = FirebaseDatabase.getInstance().getReference().child("Sponsors");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        sponsor_list = (RecyclerView)mView.findViewById(R.id.sponsorlist_id);
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        sponsor_list.setLayoutManager(layoutManager);
        sponsor_list.setHasFixedSize(true);


return mView;
    }
    public void onStart() {

        super.onStart();
        FirebaseRecyclerAdapter<Sponsors, SponsorsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Sponsors, SponsorsViewHolder>
                (
                        Sponsors.class,
                        R.layout.item_sponsor,
                        SponsorsViewHolder.class,
                        sponsorsRef
                ) {
            @Override
            protected void populateViewHolder(final SponsorsViewHolder viewHolder, Sponsors model, int position) {
                final String sponsor_id = getRef(position).getKey();
                userRef.child(sponsor_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("userName").getValue().toString();
                        viewHolder.setBusinessType(name);



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        };
        sponsor_list.setAdapter(firebaseRecyclerAdapter);
    }


            static class SponsorsViewHolder extends RecyclerView.ViewHolder{
        View mView;

                public SponsorsViewHolder(View itemView) {
                    super(itemView);
                    mView=itemView;
                }
                public void setBusinessType(String businessType){
                   TextView sponsor = (TextView) mView.findViewById(R.id.sponsor_id);
                   sponsor.setText(businessType);


                }
            }
}
