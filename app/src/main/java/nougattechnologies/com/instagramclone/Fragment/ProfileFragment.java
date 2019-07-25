package nougattechnologies.com.instagramclone.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import nougattechnologies.com.instagramclone.Adapter.MyPhotosAdapter;
import nougattechnologies.com.instagramclone.EditProfileActivity;
import nougattechnologies.com.instagramclone.FollowersActivity;
import nougattechnologies.com.instagramclone.Model.Post;
import nougattechnologies.com.instagramclone.Model.User;
import nougattechnologies.com.instagramclone.OptionsActivity;
import nougattechnologies.com.instagramclone.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
ImageView image_profile,options;
TextView posts,followers,following,fullname,bio,username;
Button edit_profile;

FirebaseUser firebaseUser;
String profileid;

ImageButton my_fotos,saved_fotos;
private List<String>mySaves;

RecyclerView recyclerView,recyclerView_save;
MyPhotosAdapter myPhotosAdapter,myPhotosAdapter_saves;
List<Post> postList;
List<Post> postList_saves;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view;
        view= inflater.inflate(R.layout.fragment_profile, container, false);
firebaseUser =FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs=getContext().getSharedPreferences("PREFS",MODE_PRIVATE);
        profileid=prefs.getString("profileid","none");

        image_profile=view.findViewById(R.id.image_profile);
        options=view.findViewById(R.id.options);
        posts=view.findViewById(R.id.posts);
        followers=view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        fullname=view.findViewById(R.id.fullname);
        bio=view.findViewById(R.id.bio);
        username=view.findViewById(R.id.username);
        edit_profile=view.findViewById(R.id.edit_profile);
        my_fotos=view.findViewById(R.id.my_fotos);
        saved_fotos=view.findViewById(R.id.my_save);

        recyclerView =view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myPhotosAdapter = new  MyPhotosAdapter(getContext(),postList);
        recyclerView.setAdapter(myPhotosAdapter);

         recyclerView_save =view.findViewById(R.id.recycler_view_save);
        recyclerView_save.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(),3);
        recyclerView_save.setLayoutManager(linearLayoutManager_saves);
        postList_saves = new ArrayList<>();
        myPhotosAdapter_saves = new  MyPhotosAdapter(getContext(),postList_saves);
        recyclerView_save.setAdapter(myPhotosAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_save.setVisibility(View.GONE);









        userInfo();
        getNrPosts();
        getFollowers();
               myPhotos();
               mySaves();
        if (profileid.equals(firebaseUser.getUid())){

            edit_profile.setText("Edit Profile");


        }else {
            checkFollow();
            saved_fotos.setVisibility(View.GONE);
        }


        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn =edit_profile.getText().toString();

                if (btn.equals("Edit Profile")){
                    //go to  EditProfile
startActivity(new Intent(getContext(),EditProfileActivity.class));


                }else if (btn.equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                 addNotifications();

                }else if (btn.equals("following")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }


            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(),OptionsActivity.class);
                startActivity(intent);

            }
        });

        my_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_save.setVisibility(View.GONE);

            }
        });




        saved_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_save.setVisibility(View.VISIBLE);

            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),FollowersActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title","followers");
                startActivity(intent);


            }
        });


        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),FollowersActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title","following");
                startActivity(intent);


            }
        });


        return view;

    }

    private void addNotifications() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);

    }
    private  void userInfo(){
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (getContext()==null){

                }

                User user =dataSnapshot.getValue(User.class);

                //SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE);
                //



               // String userpic=user.getImageurl();
//
//
//                SharedPreferences pref = getActivity().getSharedPreferences("PICSPREFS",Context.MODE_PRIVATE);
//                SharedPreferences.Editor edt = pref.edit();
//                edt.putString("facebook_id", userpic);
//                edt.apply();
//


                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private  void  checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileid).exists()){
                    edit_profile.setText("following");
                }else {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference referencel = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("following");

        referencel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getNrPosts(){

        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private  void myPhotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }

                }
                Collections.reverse(postList);
                myPhotosAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void mySaves(){
        mySaves =new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readSaves() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
postList_saves.clear();
for (DataSnapshot snapshot : dataSnapshot.getChildren()){
    Post post = snapshot.getValue(Post.class);

    for (String id : mySaves){
        if (post.getPostid().equals(id)){
            postList_saves.add(post);

        }
    }
    myPhotosAdapter_saves.notifyDataSetChanged();
}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
