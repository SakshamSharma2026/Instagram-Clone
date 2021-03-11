package com.codewithshadow.instagram.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.codewithshadow.instagram.AppSharedPreferences;
import com.codewithshadow.instagram.CommentActivity.CommentModel;
import com.codewithshadow.instagram.NotificationFragmentData.NotificationAdapter;
import com.codewithshadow.instagram.NotificationFragmentData.NotificationFragCommentAdapter;
import com.codewithshadow.instagram.NotificationFragmentData.NotificationModel;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.RequestFollower.RequestAdapter;
import com.codewithshadow.instagram.RequestFollower.RequestModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {
    RecyclerView recyclerView,followNotificationRecycler,commentRecycler;
    LinearLayoutManager manager;
    LinearLayoutManager manager2;
    LinearLayoutManager manager3;
    AppSharedPreferences appSharedPreferences;
    NotificationAdapter adapter;
    List<NotificationModel> list;
    List<CommentModel> commentModelList;
    List<RequestModel> requestModelList;
    FirebaseUser user;
    ScrollView scrollView;
    RelativeLayout relativeLayout;
    RequestAdapter requestAdapter;
    NotificationFragCommentAdapter notificationFragCommentAdapter;
    private List<String> commentList;
    private List<String> requestList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ValueEventListener dbListener;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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
        View vew = inflater.inflate(R.layout.fragment_notification, container, false);
        appSharedPreferences = new AppSharedPreferences(getContext());
        recyclerView = vew.findViewById(R.id.notification_recycler);
        followNotificationRecycler = vew.findViewById(R.id.follownotification_recycler);
        commentRecycler = vew.findViewById(R.id.notification_comment_recycler);
        user = FirebaseAuth.getInstance().getCurrentUser();
        scrollView = vew.findViewById(R.id.scrollView);
        relativeLayout = vew.findViewById(R.id.notilayout);

        manager2 = new LinearLayoutManager(getContext());
        manager2.setReverseLayout(true);
        manager2.setStackFromEnd(true);
        followNotificationRecycler.setLayoutManager(manager2);
        followNotificationRecycler.setNestedScrollingEnabled(false);
        followNotificationRecycler.setHasFixedSize(true);








        manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();




        manager3 = new LinearLayoutManager(getContext());
        manager3.setReverseLayout(true);
        manager3.setStackFromEnd(true);
        commentRecycler.setLayoutManager(manager3);
        commentRecycler.setNestedScrollingEnabled(false);
        commentRecycler.setHasFixedSize(true);
        commentModelList = new ArrayList<>();




        requestList();
        checkPosts();

        return  vew;
    }


    private void checkPosts() {
        commentList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(user.getUid()).child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    commentList.add(snapshot1.getKey());
                    scrollView.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.GONE);
                }

                readComments();
                readLikes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void requestList()
    {
        requestList = new ArrayList<>();
//        CollectionReference collectionReference = db.collection("Users").document(user.getUid()).collection("Request");
//        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                requestList.clear();
//                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
//                {
//                    requestList.add(documentSnapshot.getId());
//                }
//                checkRequest();
//            }
//        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(user.getUid()).child("Request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for(DataSnapshot snap : snapshot.getChildren())
                {
                    requestList.add(snap.getKey());
                }
                checkRequest();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void checkRequest() {
        requestModelList = new ArrayList<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users");
        for(String requestid : requestList)
        {
            requestModelList.clear();
            db.child(requestid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.child("Info").child("username").getValue(String.class);
                    String name = dataSnapshot.child("Info").child("name").getValue(String.class);
                    String imgurl = dataSnapshot.child("Info").child("imgurl").getValue(String.class);
                    String receiver = dataSnapshot.child("Info").child("username").getValue(String.class);
                    String sender = dataSnapshot.getKey();
                    RequestModel requestModel = new RequestModel(sender,receiver,username,name,imgurl);
                    requestModelList.add(requestModel);
                    scrollView.setVisibility(View.VISIBLE);
                    relativeLayout.setVisibility(View.GONE);
                    requestAdapter = new RequestAdapter(getActivity(),requestModelList);
                    followNotificationRecycler.setAdapter(requestAdapter);
                    requestAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
//        CollectionReference collectionReference = db.collection("Users");
//        for(String requestid : requestList)
//        {
//            requestModelList.clear();

//            collectionReference.document(requestid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot dataSnapshot) {
//                    String username = dataSnapshot.getString("username");
//                    String name = dataSnapshot.getString("name");
//                    String imgurl = dataSnapshot.getString("imgurl");
//                    String receiver = dataSnapshot.getString("username");
//                    String sender = dataSnapshot.getId();
//                    RequestModel requestModel = new RequestModel(sender,receiver,username,name,imgurl);
//                    requestModelList.add(requestModel);
//                    scrollView.setVisibility(View.VISIBLE);
//                    relativeLayout.setVisibility(View.GONE);
//                    requestAdapter = new RequestAdapter(getActivity(),requestModelList);
//                    followNotificationRecycler.setAdapter(requestAdapter);
//                    requestAdapter.notifyDataSetChanged();
//                }
//            });
//        }


    }




    private void readLikes()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("AllPosts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (String id : commentList)
                {
                    NotificationModel model = null;
                    for (DataSnapshot snapshot2 : snapshot.child(id).child("Likes").getChildren()) {
                        model = snapshot2.getValue(NotificationModel.class);
                        if (!model.getUsername().equals(appSharedPreferences.getUserName()))
                        {
                            list.add(model);
                        }
                    }

                }
                adapter = new NotificationAdapter(getActivity(),list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void readComments()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("AllPosts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentModelList.clear();
                for (String id : commentList)
                {
                    CommentModel commentModel = null;
                    for (DataSnapshot snapshot2 : snapshot.child(id).child("Comments").getChildren()) {
                        commentModel = snapshot2.getValue(CommentModel.class);
                        if (!commentModel.getUsername().equals(appSharedPreferences.getUserName()))
                        {
                            commentModelList.add(commentModel);
                        }
                    }

                }
                notificationFragCommentAdapter = new NotificationFragCommentAdapter(getActivity(),commentModelList);
                commentRecycler.setAdapter(notificationFragCommentAdapter);
                notificationFragCommentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}