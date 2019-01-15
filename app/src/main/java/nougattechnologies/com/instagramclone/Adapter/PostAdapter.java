package nougattechnologies.com.instagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import nougattechnologies.com.instagramclone.CommentsActivity;
import nougattechnologies.com.instagramclone.FollowersActivity;
import nougattechnologies.com.instagramclone.Fragment.PostDetailsFragment;
import nougattechnologies.com.instagramclone.Fragment.ProfileFragment;
import nougattechnologies.com.instagramclone.Model.Post;
import nougattechnologies.com.instagramclone.Model.User;
import nougattechnologies.com.instagramclone.R;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context mContext;
    public List<Post>mPost;

    //co

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =LayoutInflater.from(mContext).inflate(R.layout.post_item,viewGroup,false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post =mPost.get(i);
        Glide.with(mContext).load(post.getPostimage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(viewHolder.post_image);

        if (post.getDescription().equals("")){
            viewHolder.description.setVisibility(View.GONE);

        }else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(post.getDescription());
        }

        publisherInfo(viewHolder.image_profile,viewHolder.username,viewHolder.publisher,post.getPublisher());
isLikes(post.getPostid(),viewHolder.like);
nrLikes(viewHolder.likes,post.getPostid());
getComments(post.getPostid(),viewHolder.comments);
isSaved(post.getPostid(),viewHolder.save);

        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();


            }
        });

        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();


            }
        });

        viewHolder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();


            }
        });

        viewHolder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",post.getPostid());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailsFragment()).commit();


            }
        });

viewHolder.save.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (viewHolder.save.getTag().equals("save")){
            FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                    .child(post.getPostid()).setValue(true);


        }else {
            FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                    .child(post.getPostid()).removeValue();
        }
    }
});



viewHolder.like.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (viewHolder.like.getTag().equals("like")){
            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                    .child(firebaseUser.getUid()).setValue(true);
addNotifications(post.getPublisher(),post.getPostid());



        }else {
            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                    .child(firebaseUser.getUid()).removeValue();
        }
    }
});
viewHolder.comment.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent =new Intent(mContext,CommentsActivity.class);
        intent.putExtra("postid",post.getPostid());
        intent.putExtra("publisherid",post.getPublisher());
        mContext.startActivity(intent);



    }
});
        viewHolder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(mContext,CommentsActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisherid",post.getPublisher());
                mContext.startActivity(intent);



            }
        });

        viewHolder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,FollowersActivity.class);
                intent.putExtra("id",post.getPostid());
                intent.putExtra("title","likes");
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
public ImageView image_profile,post_image,like,comment,save;
public TextView username,likes,publisher,description,comments;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile=itemView.findViewById(R.id.image_profile);
            post_image=itemView.findViewById(R.id.post_image);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            save=itemView.findViewById(R.id.save);
            username=itemView.findViewById(R.id.username);
            likes=itemView.findViewById(R.id.likes);
            publisher=itemView.findViewById(R.id.publisher);
            description=itemView.findViewById(R.id.description);
            comments=itemView.findViewById(R.id.comments);
        }
    }
   private void getComments(String postid, final TextView comments){
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.setText("view All "+dataSnapshot.getChildrenCount() + " Comments");
//yes
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


   }


private void isLikes(String postid, final ImageView imageView){
        final FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference =FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {

                    imageView.setImageResource(R.drawable.ic_liked);

                    imageView.setTag("liked");

                }else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

}

private void addNotifications(String userid,String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

    HashMap<String,Object> hashMap = new HashMap<>();
    hashMap.put("userid",firebaseUser.getUid());
    hashMap.put("text","liked your post");
    hashMap.put("postid",postid);
    hashMap.put("ispost",true);

    reference.push().setValue(hashMap);

}



public void nrLikes(final TextView likes,String postid){
        DatabaseReference reference = FirebaseDatabase
                .getInstance().getReference().child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

}

    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void isSaved(final String postid, final ImageView imageView) {
        FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_savee_black);
                    imageView.setTag("saved");

                }else {

                    imageView.setImageResource(R.drawable.ic_savee);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
