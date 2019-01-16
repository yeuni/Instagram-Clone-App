package nougattechnologies.com.instagramclone.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import nougattechnologies.com.instagramclone.Model.Story;
import nougattechnologies.com.instagramclone.Model.User;
import nougattechnologies.com.instagramclone.R;

public class StoryAdapter  extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{
private Context mContext;
private List<Story>mStory;

    public StoryAdapter(Context mContext, List<Story> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i==0){
            View view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item,viewGroup,false);
return new StoryAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.story_item,viewGroup,false);
            return new StoryAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        Story story = mStory.get(i);
        userInfo(viewHolder,story.getUserid(),i);

if (viewHolder.getAdapterPosition()!=0){
    seenStory(viewHolder,story.getUserid());

}

if (viewHolder.getAdapterPosition() ==0){
    myStory(viewHolder.addstory_text,viewHolder.story_plus,false);

}
viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (viewHolder.getAdapterPosition()==0){
            myStory(viewHolder.addstory_text,viewHolder.story_plus,true);


        }else {
            //TODO:go to story


        }
    }
});


    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView story_photo,story_plus,story_photo_seen;
        public TextView story_username,addstory_text;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo=itemView.findViewById(R.id.story_photo);
            story_plus=itemView.findViewById(R.id.story_plus);
            story_photo_seen=itemView.findViewById(R.id.story_photo_seen);
            story_username=itemView.findViewById(R.id.story_username);
            addstory_text=itemView.findViewById(R.id.addstory_text);


        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){

            return 0;
        }
        return 1;

    }

    private  void userInfo(final ViewHolder viewHolder, String userid, final int pos){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(viewHolder.story_photo);
                if (pos!=0) {

                    Glide.with(mContext).load(user.getImageurl()).into(viewHolder.story_photo_seen);
                    viewHolder.story_username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void myStory(final TextView textView, final ImageView imageView, final boolean click){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                long timecurrent=System.currentTimeMillis();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Story story = snapshot.getValue(Story.class);
                    if (timecurrent >story.getTimestart()&& timecurrent < story.getTimeend()){
                       count++;

                    }

                }

                if (click){
                    //TODO:show alert dialog

                }else {

                    if (count>0){
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    }else {

                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void seenStory(final ViewHolder viewHolder, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (!snapshot.child("views")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .exists() && System.currentTimeMillis()<snapshot.getValue(Story.class).getTimeend()){
                        i++;

                    }
                }

                if (i>0){

                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);


                }else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
