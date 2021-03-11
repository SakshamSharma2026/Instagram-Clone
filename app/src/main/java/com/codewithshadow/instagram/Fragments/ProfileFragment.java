package com.codewithshadow.instagram.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.AppSharedPreferences;
import com.codewithshadow.instagram.DrawerActivity;
import com.codewithshadow.instagram.EditProfileActivity;
import com.codewithshadow.instagram.HighLightStory.HighlightAdapter;
import com.codewithshadow.instagram.PostImages.PostAdapter;
import com.codewithshadow.instagram.PostImages.PostModel;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.Story.StoryActivity;
import com.codewithshadow.instagram.Story.StoryModel;
import com.codewithshadow.instagram.newAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    CircleImageView profileimg;
    DatabaseReference ref,storyref;
    FirebaseUser user;
    TextView fullname,bio,website,idusername,textpost,textfollowing,textfollowers;
    RelativeLayout btneditprofile;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;
    RecyclerView recyclerView,highlight_recycler;
    AppSharedPreferences appSharedPreferences;
    private int totalpost = 0,totalfollowing=0,totalfollowers = 0;
   List<PostModel> imageUrlList;
    com.codewithshadow.instagram.newAdapter newAdapter;
    ImageView navigationdrawer;
    CardView profilecard;
    RelativeLayout backLayout;
    LinearLayoutManager manager2;
    HighlightAdapter storyAdapter;
    List<StoryModel> storyModelList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        appSharedPreferences = new AppSharedPreferences(getContext());
        recyclerView = view.findViewById(R.id.grid_view);
        highlight_recycler = view.findViewById(R.id.highlights_view);
        imageUrlList = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        storyref = FirebaseDatabase.getInstance().getReference().child("Story");
        profileimg = view.findViewById(R.id.button_image);
        fullname = view.findViewById(R.id.item_text_1);
        bio = view.findViewById(R.id.item_text_3);
        website = view.findViewById(R.id.item_text_4);
        idusername = view.findViewById(R.id.idusername);
        progressBar = view.findViewById(R.id.progress_bar);
        relativeLayout = view.findViewById(R.id.profilelayout);
        textpost = view.findViewById(R.id.item_1_value);
        textfollowing = view.findViewById(R.id.item_3_value);
        textfollowers = view.findViewById(R.id.item_2_value);
        navigationdrawer = view.findViewById(R.id.design_navigation_view);
        profilecard = view.findViewById(R.id.button_head_card_parent);
        backLayout = view.findViewById(R.id.button_click_parent);
        documentReference = db.collection("Users").document(user.getUid());

        navigationdrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DrawerActivity.class);
                startActivity(intent);


            }
        });

        btneditprofile = view.findViewById(R.id.item_button_1_click_parent);
        btneditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new GridLayoutManager(getContext(), 3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);


        storyref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(user.getUid()).exists())
                {
                    StoryRef();
                }
                else
                {
                    backLayout.setBackgroundColor(Color.TRANSPARENT);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        storyModelList = new ArrayList<>();
        manager2 = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        highlight_recycler.setHasFixedSize(true);
        highlight_recycler.setLayoutManager(manager2);
        storyAdapter = new HighlightAdapter(getActivity(),storyModelList);
        highlight_recycler.setAdapter(storyAdapter);

        readStory();







        return  view;
    }

    private void readPosts() {
//        CollectionReference collectionReference = db.collection("AllPosts");
//        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error!=null)
//                {
//                    return;
//                }
//                imageUrlList.clear();
//                PostModel model = null;
//                int postCount = 0;
//                for(DocumentSnapshot documentSnapshot : value)
//                {
//                    model = documentSnapshot.toObject(PostModel.class);
//                    if (model.getPublisherid().equals(user.getUid()))
//                    {
//                        postCount++;
//                        textpost.setText(Integer.toString(postCount));
//                        imageUrlList.add(model);
//                    }
//                    Collections.reverse(imageUrlList);
//
//                }
//
//                newAdapter=new newAdapter(getContext(),imageUrlList);
//                recyclerView.setAdapter(newAdapter);
//                newAdapter.notifyDataSetChanged();
//            }
//        });

        ref.child("AllPosts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageUrlList.clear();
                PostModel model = null;

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    model = dataSnapshot.child("Info").getValue(PostModel.class);

                    if (model.getPublisherid().equals(user.getUid()))
                    {
                        imageUrlList.add(model);
                    }
                    Collections.reverse(imageUrlList);
                }
                newAdapter=new newAdapter(getContext(),imageUrlList);
                recyclerView.setAdapter(newAdapter);
                newAdapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void StoryRef() {
        storyref.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    StoryModel model = dataSnapshot.getValue(StoryModel.class);

                    if (timecurrent > model.getTimestart() && timecurrent < model.getTimeend())
                    {
                        count++;
                        backLayout.setBackground(getResources().getDrawable(R.drawable.uigitdev_elements_profile_picture_gradient));
                    }
                }


                int finalCount = count;
                profilecard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (finalCount > 0)
                        {
                            Intent intent = new Intent(getContext(), StoryActivity.class);
                            intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            startActivity(intent);
                        }
                        else
                        {
                            backLayout.setBackgroundColor(Color.TRANSPARENT);

                        }


                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readStory()
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("HighLights");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyModelList.clear();

                    StoryModel storyModel = null;
                    for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                        storyModel = snapshot2.getValue(StoryModel.class);
                        storyModelList.add(storyModel);


                    }


                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void RetreivingData() {

        ref.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                String stringbio = snapshot.child("Info").child("bio").getValue(String.class);
                String stringwebsite = snapshot.child("Info").child("website").getValue(String.class);

                totalpost = (int)snapshot.child("Posts").getChildrenCount();
                totalfollowing = (int)snapshot.child("Following").getChildrenCount();
                totalfollowers = (int)snapshot.child("Followers").getChildrenCount();

                textpost.setText(Integer.toString(totalpost));
                textfollowing.setText(Integer.toString(totalfollowing));
                textfollowers.setText(Integer.toString(totalfollowers));

                String name = snapshot.child("Info").child("name").getValue(String.class);
                if (getActivity()==null)
                {return;}
                Glide.with(getActivity()).load(appSharedPreferences.getImgUrl()).into(profileimg);
                fullname.setText(name);
                bio.setText(stringbio);
                website.setText(stringwebsite);
                idusername.setText(appSharedPreferences.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        readPosts();
        RetreivingData();
    }
}