package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Comment;

import java.io.File;

import kotlin.collections.SlidingWindowKt;

import static android.graphics.drawable.Drawable.createFromPath;


public class showProfile extends AppCompatActivity{

    private Context context;
    private String deBugTag;
    private AppCompatActivity activity;
    private FirebaseRecyclerAdapter<BookInfo, CardViewBook> adapter;
    private FirebaseRecyclerAdapter<CommentModel, CardViewComment> commentAdapter;
    private String name = null;
    private String surname = null;
    private String email = null;
    private String biography = null;
    private String city = null;
    private Boolean noBook = false;
    private Boolean noComments = false;
    private String userId;
    private Float vote = -1.f;
    private Button canCommentButton;
    private Button canChatButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deBugTag = this.getClass().getName();
        activity = this;
        context = getApplicationContext();

        MyUser myUser = new MyUser(context);

        //+++++++++++++set fields+++++++++++++
        setContentView(R.layout.show_profile);

        userId = getIntent().getStringExtra("userId");

        //expandableView data
        String groups[] = {getString(R.string.bio_label_show), getString(R.string.my_books), getString(R.string.ratings)};

        //get elements
        //TextView nameSurnameView = findViewById(R.id.nameSurnameShow);
        //TextView emailView = findViewById(R.id.emailShow);
        ImageView profileView = findViewById(R.id.imageViewShow);
        //RecyclerView list = findViewById(R.id.published_books_rv);
        ExpandableListView aboutMeList = findViewById(R.id.aboutMe);
        canCommentButton = findViewById(R.id.send_comment_button);
        canChatButton = findViewById(R.id.send_message_button);



        if(userId.equals(myUser.getUserID())){
            canCommentButton.setVisibility(View.GONE);
            canChatButton.setVisibility(View.GONE);
        }

        RatingBar ratings = findViewById(R.id.comment_stars);


        //set toolbar
        TextView toolbarTitle = findViewById(R.id.back_toolbar_text);
        findViewById(R.id.imageButton).setOnClickListener(v -> onBackPressed());
        toolbarTitle.setText(R.string.user_info);

        //set FireBaseReference
        DatabaseReference fireBaseRef = FirebaseDatabase.getInstance().getReference();

        //aboutMeList expandable adapter
        BaseExpandableListAdapter aboutMeAdapter = new BaseExpandableListAdapter() {

            private int lastExpandedGroupPosition;
            @Override
            public int getGroupCount() {
                return groups.length;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return 1;
            }

            @Override
            public Object getGroup(int groupPosition) {
                return groups[groupPosition];
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return null;
            }

            @Override
            public long getGroupId(int groupPosition) {
                return 0;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                if(convertView==null){
                    LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = li.inflate(R.layout.show_profile_groups, null);
                }

                TextView title = convertView.findViewById(R.id.heading);
                title.setText(groups[groupPosition]);

                return convertView;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

                switch (groupPosition){
                    case 0:
                        if(convertView==null || convertView.getId() != R.id.about_me_childs){
                            LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            convertView = li.inflate(R.layout.about_me_childs, null);
                        }

                        TextView cityView = convertView.findViewById(R.id.showProfileCity);
                        cityView.setText(city);

                        TextView bioView = convertView.findViewById(R.id.showProfileBio);
                        bioView.setText(biography);

                        TextView nameView = convertView.findViewById(R.id.showProfileNameSurname);
                        nameView.setText(name+" "+surname);

                        TextView emailView = convertView.findViewById(R.id.showProfileEmail);
                        emailView.setText(email);


                        break;

                    case 1:
                        if(convertView==null || convertView.getId() != R.id.book_list_childs){
                            LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            convertView = li.inflate(R.layout.book_list_childs, null);
                        }

                        //convertView.setLayoutParams(new ConstraintLayout.LayoutParams("300dp", MA));
                        RecyclerView list = convertView.findViewById(R.id.published_books_rv);
                        TextView noBooksMessage = convertView.findViewById(R.id.noBookMessage);


                        if(noBook)
                            noBooksMessage.setVisibility(View.VISIBLE);
                        else
                            noBooksMessage.setVisibility(View.GONE);

                        list.setItemAnimator(new DefaultItemAnimator());
                        list.setLayoutManager(new LinearLayoutManager(context));
                        list.setAdapter(adapter);

                        break;

                    case 2:
                        if(convertView==null || convertView.getId() != R.id.comment_list_childs){
                            LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            convertView = li.inflate(R.layout.comment_list_childs, null);
                        }

                        RecyclerView commentList = convertView.findViewById(R.id.comments_list_rv);
                        TextView noCommentsMessage = convertView.findViewById(R.id.noCommentMessage);

                        if(noComments)
                            noCommentsMessage.setVisibility(View.VISIBLE);
                        else
                            noCommentsMessage.setVisibility(View.GONE);

                        commentList.setItemAnimator(new DefaultItemAnimator());
                        commentList.setLayoutManager(new LinearLayoutManager(context));
                        commentList.setAdapter(commentAdapter);

                        break;
                }

                return convertView;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return false;
            }

            @Override
            public void onGroupExpanded(int groupPosition){
                super.onGroupExpanded(groupPosition);
                //collapse the old expanded group, if not the same
                //as new group to expand
                if(groupPosition != lastExpandedGroupPosition){
                    aboutMeList.collapseGroup(lastExpandedGroupPosition);
                }

                lastExpandedGroupPosition = groupPosition;
            }
        };

        aboutMeList.setAdapter(aboutMeAdapter);
        aboutMeAdapter.getChildView(0,0,true,null, null);
        aboutMeList.expandGroup(0);

        //check if the user has to leave comment
        Query leaveCommentQuery = fireBaseRef.child("commentsDB").child(userId).child("can_comment").orderByKey().equalTo(myUser.getUserID());

        leaveCommentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                canCommentButton.setClickable(true);
                canCommentButton.setOnClickListener(v->{
                    if(dataSnapshot!=null && dataSnapshot.getValue()!=null){
                        Log.d(deBugTag,dataSnapshot.getValue().toString());
                        Intent commentIntent = new Intent(
                                getApplicationContext(),
                                CommentActivity.class
                        );
                        commentIntent.putExtra("userId", userId);
                        startActivity(commentIntent);
                    }else{
                        //todo fai stringa
                        Toast.makeText(context,"Non puoi scrivere un commento se non hai scambiato un libro con questo utente",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Get information from FireBase
        fireBaseRef.child("users").orderByKey().equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot = dataSnapshot.getChildren().iterator().next();

                for(DataSnapshot child : dataSnapshot.getChildren()){

                    switch (child.getKey()){
                        case "name":
                            name = (String)child.getValue();
                            break;
                        case "surname":
                            surname = (String)child.getValue();
                            break;
                        case "email":
                            email = (String)child.getValue();
                            break;
                        case "biography":
                            biography = child.getValue().toString();
                            break;
                        case "city":
                            city = (String) child.getValue();
                            break;
                        case "avgVotes":
                            vote = Float.parseFloat(child.child("vote").getValue().toString());
                            break;
                    }
                }

                if(vote>0){
                    ratings.setRating(vote);
                }

                findViewById(R.id.send_message_button).setOnClickListener(v->{
                    Intent chatIntent = new Intent(getApplicationContext(),PersonalChat.class);
                    chatIntent.putExtra("userId",userId);
                    chatIntent.putExtra("userName",name + " " + surname);
                    startActivity(chatIntent);
                });

                //nameSurnameView.setText(getString(R.string.nameSurname,name,surname));
                //emailView.setText(email);
                //biographyView.setText(biography);
                //cityView.setText(city);


                aboutMeAdapter.notifyDataSetChanged();

                //set default image
                profileView.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_black_40dp));

                //set container visible
                //findViewById(R.id.show_form_wrapper).setVisibility(View.VISIBLE);

                //check if the profile image is available and load it
                StorageReference ref = FirebaseStorage.getInstance().getReference().child("profileImages/"+ userId);

                //todo ref.getBytes lancia degli errori cercare di capire cosa sono
                //todo ridurre la dimensione del file ma per fare questo bisogna comprimere tutte le immagini e forse è meglio sostituite bitmap con drawable per migliorare le prestazioni
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //create File
                        File file = new File(getFilesDir(), "profile.jpg");

                        FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString()).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                profileView.setImageDrawable(Drawable.createFromPath(file.getPath()));
                                profileView.setOnClickListener(v -> {
                                    Intent fullImageIntent = new Intent(
                                            getApplicationContext(),
                                            fullScreenImage.class
                                    );
                                    fullImageIntent.putExtra("path", file.getPath());
                                    startActivity(fullImageIntent);

                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(deBugTag,e.getMessage());

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(deBugTag,e.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                networkProblem();
            }
        });

        //fill up the published book list
        Log.d(deBugTag,"userId: "+ userId);
        Query publishedBookQuery = fireBaseRef.child("books").orderByChild("owner").equalTo(userId);

        FirebaseRecyclerOptions<BookInfo> options = new FirebaseRecyclerOptions.Builder<BookInfo>()
                .setQuery(publishedBookQuery, new SnapshotParser<BookInfo>() {
                    @NonNull
                    @Override
                    public BookInfo parseSnapshot(@NonNull DataSnapshot snapshot) {

                        if(snapshot==null){
                            Log.d(deBugTag,"snapshot null");
                        }
                        if(!snapshot.exists()){
                            Log.d(deBugTag,"snapshot non esiste");
                        }else{
                            Log.d(deBugTag,"snapshot: " + snapshot.toString());
                        }

                        return ResultsList.parseDataSnapshotBook(snapshot);

                    }
                })
                .setLifecycleOwner(this)
                .build();

        adapter = new FirebaseRecyclerAdapter<BookInfo, CardViewBook>(options) {

            @NonNull
            @Override
            public CardViewBook onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(activity)
                        .inflate(R.layout.card_view_search_results_list, parent, false);

                return new CardViewBook(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CardViewBook holder, int position, @NonNull BookInfo model) {

                holder.bindData(model.getBookTitle(),model.getAuthor(),model.get_ISBN(), model.getEditionYear(), model.getBookID(),false,false);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount()==0)
                    noBook = true;
                Log.d(deBugTag,"ondatachanged");
            }
        };

        //comment list
        Query commentQuery = fireBaseRef.child("commentsDB").child(userId).child("comments");

        FirebaseRecyclerOptions<CommentModel> commentOptions = new FirebaseRecyclerOptions.Builder<CommentModel>()
                .setQuery(commentQuery, CommentModel.class).setLifecycleOwner(this).build();

        commentAdapter = new FirebaseRecyclerAdapter<CommentModel, CardViewComment>(commentOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CardViewComment holder, int position, @NonNull CommentModel model) {
                String data = model.getDay() + "/" + model.getMonth() + "/" + model.getYear();
                holder.bindData(model.getUserNameSurname(), model.getRating(), model.getText(), data);
            }

            @NonNull
            @Override
            public CardViewComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(activity)
                        .inflate(R.layout.cardview_comment, parent, false);

                return new CardViewComment(v);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount()==0)
                    noComments = true;
                Log.d(deBugTag,"ondatachanged");
            }
        };

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
        commentAdapter.stopListening();
    }

    private void networkProblem(){
        Toast.makeText(getApplicationContext(),R.string.network_problem,Toast.LENGTH_SHORT).show();
        onBackPressed();
    }


}
