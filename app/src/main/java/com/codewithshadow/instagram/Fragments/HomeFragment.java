package com.codewithshadow.instagram.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.codewithshadow.instagram.AppSharedPreferences;
import com.codewithshadow.instagram.LoginPage.UserModel;
import com.codewithshadow.instagram.PostImages.PostAdapter;
import com.codewithshadow.instagram.PostImages.PostModel;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.SearchActivity.SearchAdapter;
import com.codewithshadow.instagram.SearchActivity.SearchModel;
import com.codewithshadow.instagram.SendMessages.MessageUser.MessagesUserActivity;
import com.codewithshadow.instagram.Story.StoryAdapter;
import com.codewithshadow.instagram.Story.StoryModel;
import com.codewithshadow.instagram.UserRecomAdapter;
import com.codewithshadow.instagram.Utils.UniversalImageLoderClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    DatabaseReference ref,userref;
    RecyclerView recyclerView,mystoryrecycler;
    List<PostModel> list;
    LinearLayoutManager manager ,manager2,manager3;
    PostAdapter adapter;
    StoryAdapter storyAdapter;
    List<StoryModel> storyModelList;
    FirebaseUser user;
    ImageView btn_message;
    private List<String> followingList;
    private List<String> followersList;
    private List<String> newList;
    AppSharedPreferences appSharedPreferences;
    LinearLayout linearLayout;
    ScrollView scrollView;
    UserRecomAdapter recomAdapter;
    List<UserModel> searchModelList;
    DiscreteScrollView disscrollView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        appSharedPreferences = new AppSharedPreferences(getContext());
        recyclerView = view.findViewById(R.id.recycler);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mystoryrecycler = view.findViewById(R.id.mystoryrecycler);
        UniversalImageLoderClass universalImageLoderClass = new UniversalImageLoderClass(getActivity());
        ImageLoader.getInstance().init(universalImageLoderClass.getConfig());
        ref = FirebaseDatabase.getInstance().getReference().child("AllPosts");
        userref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        linearLayout = view.findViewById(R.id.linearLayout);
        scrollView = view.findViewById(R.id.scrollView);
        disscrollView  = view.findViewById(R.id.picker);
        list = new ArrayList<>();
        storyModelList = new ArrayList<>();
        manager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        recyclerView.setLayoutManager(manager);
        recyclerView.setNestedScrollingEnabled(false);

        manager2 = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        mystoryrecycler.setHasFixedSize(true);
        mystoryrecycler.setLayoutManager(manager2);
        mystoryrecycler.setNestedScrollingEnabled(false);


        searchModelList = new ArrayList<>();
        recomAdapter = new UserRecomAdapter(getContext(),searchModelList);



        disscrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                .build());



        storyAdapter = new StoryAdapter(getContext(),storyModelList);
        mystoryrecycler.setAdapter(storyAdapter);
        btn_message = view.findViewById(R.id.messagebtn);
        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MessagesUserActivity.class);
                startActivity(intent);
            }
        });






        readUsers();


        return  view;
    }


    private void checkFollowers() {
        followersList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followersList.clear();
                if (!snapshot.child("Following").exists())
                {
                    linearLayout.setVisibility(View.VISIBLE);
                }



                for (DataSnapshot snapshot1 : snapshot.child("Followers").getChildren())
                {
                    
                    followersList.add(snapshot1.getKey());

                }
                List<String> myid = new ArrayList<>();
                myid.add(user.getUid());
                newList = new ArrayList<String>(myid);
                newList.addAll(followersList);

                readPost();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel searchModel =null;
                searchModelList.clear();
                for(DataSnapshot snap : snapshot.getChildren())
                {
                    if (!snap.getKey().equals(user.getUid()))
                    {
                        searchModel = snap.child("Info").getValue(UserModel.class);
                        searchModelList.add(searchModel);
                    }


                }
                disscrollView.setAdapter(recomAdapter);
                recomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void checkFollowing() {
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(user.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    followingList.add(snapshot1.getKey());
                }
                readStory();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void readPost() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                PostModel model = null;
                for (DataSnapshot snap : snapshot.getChildren())
                {
                    model = snap.child("Info").getValue(PostModel.class);

                    if (followingList.contains(model.getPublisherid()) || model.getPublisherid().equals(user.getUid()) )
                    {
                        scrollView.setVisibility(View.VISIBLE);
                        linearLayout.setVisibility(View.GONE);
                        list.add(model);
                    }

                }
                adapter = new PostAdapter(getActivity(),list);
                recyclerView.setAdapter(adapter);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void readStory()
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Story");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurrent = System.currentTimeMillis();
                storyModelList.clear();
                storyModelList.add(new StoryModel("",0,0,FirebaseAuth.getInstance().getCurrentUser().getUid(),"",""));

                for (String id : followingList)
                {
                    int countStory = 0;
                    StoryModel storyModel = null;
                        for (DataSnapshot snapshot2 : snapshot.child(id).getChildren()) {
                            storyModel = snapshot2.getValue(StoryModel.class);
                            if (timecurrent > storyModel.getTimestart() && timecurrent < storyModel.getTimeend()) {
                                countStory++;
                            }
                        }
                    if (countStory > 0)
                    {
                        storyModelList.add(storyModel);
                    }
                }

                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        checkFollowing();
        checkFollowers();
//        if (appSharedPreferences.getStatus().equals("Active"))
//        {
//            scrollView2.setVisibility(View.VISIBLE);
//            linearLayout.setVisibility(View.GONE);
//        }

        super.onStart();
    }
}