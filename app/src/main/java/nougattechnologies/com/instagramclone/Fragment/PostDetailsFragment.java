package nougattechnologies.com.instagramclone.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class PostDetailsFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    String postid;

    public PostDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_post_details, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE);
        postid =sharedPreferences.getString("postid","none");

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
 postLists = new ArrayList<>();
 postAdapter=new PostAdapter(getContext(),postLists);
 recyclerView.setAdapter(postAdapter);

 readPosts();



        return view;
    }

    private void readPosts() {
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Posts").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postLists
                        .clear();
                Post post = dataSnapshot.getValue(Post.class);
                postLists.add(post);

                postAdapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
