package nougattechnologies.com.instagramclone.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import nougattechnologies.com.instagramclone.Fragment.ProfileFragment;
import nougattechnologies.com.instagramclone.Model.User;
import nougattechnologies.com.instagramclone.R;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.viewHolder>  {
private Context mContext;
private List<User>mUsers;
private FirebaseUser firebaseUser;

public UserAdapter(Context mContext,List<User>mUsers){
    this.mContext=mContext;
    this.mUsers=mUsers;


}
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view =LayoutInflater.from(mContext).inflate(R.layout.user_item,viewGroup,false);
        return new UserAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, int i) {

    firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
    final User user =  mUsers.get(i);
    viewHolder.btn_follow.setVisibility(View.VISIBLE);

    viewHolder.username.setText(user.getUsername());
    viewHolder.fullname.setText(user.getFullname());
        Glide.with(mContext).load(user.getImageurl()).into(viewHolder.image_profile);
        isFollowing(user.getId(),viewHolder.btn_follow);


        if (user.getId().equals(firebaseUser.getUid())){
            viewHolder.btn_follow.setVisibility(View.GONE);
        }

viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor =mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
        editor.putString("profileid",user.getId());
        editor.apply();

        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
               new ProfileFragment()).commit();

    }
});

        viewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.btn_follow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
    public TextView username;
    public TextView fullname;
    public CircleImageView image_profile;
    public Button btn_follow;



        public viewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.username);
            fullname=itemView.findViewById(R.id.fullname);
            image_profile=itemView.findViewById(R.id.image_profile);
            btn_follow=itemView.findViewById(R.id.btn_follow);


        }
    }
    private void isFollowing(final String userid, final Button button){
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
if (dataSnapshot.child(userid).exists()){
    button.setText("following");
}else {
    button.setText("follow");
}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
