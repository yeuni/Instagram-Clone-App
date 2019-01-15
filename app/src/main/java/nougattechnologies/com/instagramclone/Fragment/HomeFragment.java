package nougattechnologies.com.instagramclone.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nougattechnologies.com.instagramclone.Adapter.PostAdapter;
import nougattechnologies.com.instagramclone.Model.Post;
import nougattechnologies.com.instagramclone.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
private RecyclerView recyclerView;
private PostAdapter postAdapter;
private List<Post>postLists;

private List<String>followingList;

ProgressBar progressBar;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postLists);
        recyclerView.setAdapter(postAdapter);

        progressBar = view.findViewById(R.id.progress_circular);


checkFollowing();
        return view;
    }

    private void checkFollowing(){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               followingList.clear();
               for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                   followingList.add(snapshot.getKey());
               }
               readPosts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void readPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
postLists.clear();
for (DataSnapshot snapshot : dataSnapshot.getChildren()){
    Post post =snapshot.getValue(Post.class);
    for (String id : followingList){
        if (post.getPublisher().equals(id)){
            postLists.add(post);
        }
    }
}
postAdapter.notifyDataSetChanged();
progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
