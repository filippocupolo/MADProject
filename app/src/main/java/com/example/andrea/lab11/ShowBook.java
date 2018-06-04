package com.example.andrea.lab11;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ShowBook extends AppCompatActivity {

    private String deBugTag;
    private Context context;
    private TextView title;
    private TextView author;
    private TextView editionYear;
    private TextView publisher;
    private TextView ISBN;
    private TextView owner;
    private TextView cityOwner;
    private TextView statusTextView;
    private TextView request_list_title;
    private TextView lendingMessage;
    private Button sendRequestButton;
    private Button endLendingButton;
    private BookInfo book;
    private ConstraintLayout container;
    private ConstraintLayout containerListRequest;
    private ConstraintLayout containerRequestButton;
    private ImageButton goToProfileButton;
    private ImageButton send_message_button;
    private RecyclerView requestRecycleListView;
    private CopyOnWriteArrayList<Drawable> imagesList;
    private FirebaseRecyclerAdapter<UserModel,BookRequest> requestAdapter = null;

    //todo stampa le condizioni del libro

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deBugTag = this.getClass().getName();
        imagesList = new CopyOnWriteArrayList<>();
        context = getApplicationContext();
        MyUser myUser = new MyUser(context);

        //set layout
        setContentView(R.layout.book_result_show_details);

        //get elements
        title = findViewById(R.id.bookTitle);
        author = findViewById(R.id.bookAuthor);
        editionYear = findViewById(R.id.bookEdition);
        publisher = findViewById(R.id.bookPublisher);
        ISBN = findViewById(R.id.bookISBN);
        owner = findViewById(R.id.bookOwner);
        cityOwner = findViewById(R.id.ownerCity);
        statusTextView = findViewById(R.id.status);
        sendRequestButton = findViewById(R.id.sendRequest);
        container = findViewById(R.id.constrTop);
        containerListRequest=findViewById(R.id.requestList);
        containerRequestButton = findViewById(R.id.requestBookButton);
        goToProfileButton = findViewById(R.id.gotoProfileButton);
        endLendingButton = findViewById(R.id.lending_end_button);
        lendingMessage = findViewById(R.id.bookAlreadyLend);
        send_message_button = findViewById(R.id.send_message_button);
        requestRecycleListView = findViewById(R.id.requestRecycleListView);
        request_list_title = findViewById(R.id.relist);
        GridView gridView = findViewById(R.id.imageBook);

        //set toolbar
        TextView toolbarTitle = findViewById(R.id.back_toolbar_text);
        findViewById(R.id.imageButton).setOnClickListener(v->onBackPressed());
        toolbarTitle.setText(R.string.book_info);

        //hide container until information are got
        container.setVisibility(View.GONE);

        //set gridView adapter
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return imagesList.size();
            }

            @Override
            public Object getItem(int position) {
                return imagesList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null)
                {
                    LayoutInflater li=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView=li.inflate(R.layout.book_image_adapter_layout, parent,false);

                }

                ImageButton bookPhoto = convertView.findViewById(R.id.bookPhoto);
                ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);
                deleteButton.setVisibility(View.GONE);
                bookPhoto.setClickable(false);
                bookPhoto.setImageDrawable(imagesList.get(position));
                bookPhoto.setScaleType(ImageButton.ScaleType.FIT_XY);
                return convertView;
            }
        };
        gridView.setAdapter(adapter);

        //get bookId and showProfile from intent
        String bookId = getIntent().getStringExtra("bookId");
        Boolean showProfile = getIntent().getBooleanExtra("showProfile",true);

        //set database Ref
        DatabaseReference dbRef =  FirebaseDatabase.getInstance().getReference();

        //make FireBase request for book
        Query bookQuery = dbRef.child("books").orderByKey().equalTo(bookId);
        //todo rilasciare questo listener
        bookQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get book
                book = ResultsList.parseDataSnapshotBook(dataSnapshot.getChildren().iterator().next());
                if(book == null){
                    errorMethod(R.string.network_problem);
                }

                //make FireBase request for book Owner
                Query userQuery = dbRef.child("users").orderByKey().equalTo(book.getOwner());
                userQuery.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot == null){
                            errorMethod(R.string.not_existing_user);
                        }
                        if(!dataSnapshot.exists()){
                            errorMethod(R.string.not_existing_user);
                        }

                        dataSnapshot = dataSnapshot.getChildren().iterator().next();

                        String name_surname = null;
                        String city = null;

                        //get owner
                        for(DataSnapshot child : dataSnapshot.getChildren()){

                            switch (child.getKey()){
                                case "name":
                                    name_surname = (String) child.getValue();
                                    break;
                                case "surname":
                                    name_surname = name_surname + " ";
                                    name_surname = name_surname + (String) child.getValue();
                                    break;
                                case "city":
                                    city = (String) child.getValue();
                                    break;
                            }
                        }

                        //set layout with the results
                        container.setVisibility(View.VISIBLE);
                        title.setText(book.getBookTitle());
                        author.setText(book.getAuthor());
                        editionYear.setText(book.getEditionYear());
                        publisher.setText(book.getPublisher());
                        ISBN.setText(book.get_ISBN());
                        owner.setText(name_surname);
                        cityOwner.setText(city);

                        //check if the user is the owner of the book
                        String owner = name_surname;
                        if(!book.getOwner().equals(myUser.getUserID())){

                            //chat button
                            send_message_button.setOnClickListener(v->{
                                Intent chatIntent = new Intent(getApplicationContext(),PersonalChat.class);
                                chatIntent.putExtra("userId",book.getOwner());
                                Log.d(deBugTag,book.getOwner());
                                chatIntent.putExtra("userName",owner);
                                startActivity(chatIntent);
                            });

                            //set visible requestButton
                            containerRequestButton.setVisibility(View.VISIBLE);

                            if(book.getStatus()==0){
                                //libro libero

                                //aggiorna view se ho già richiesto il prestito
                                bookRequested(dbRef, book, myUser.getUserID());

                                sendRequestButton.setVisibility(View.VISIBLE);
                                statusTextView.setText(R.string.book_free);
                            }
                            else if(book.getStatus() == 1 && !book.getBorrower().equals(myUser.getUserID())){
                                //il libro è in prestito e non a me
                                statusTextView.setText(R.string.book_borrowed);
                                sendRequestButton.setText(getString(R.string.add_to_wishlist));
                            }else{
                                //libro in prestito a me
                                statusTextView.setText(R.string.book_mine);
                                sendRequestButton.setVisibility(View.GONE);
                            }


                            sendRequestButton.setOnClickListener( v ->{

                                sendRequestButton.setVisibility(View.GONE);
                                statusTextView.setText(R.string.lending_req_sent);

                                dbRef.child("bookRequests").child(book.getBookID()).child(myUser.getUserID()).setValue(myUser.getName() + " " + myUser.getSurname());
                                Toast.makeText(context,"la tua richiesta è stata effettuata",Toast.LENGTH_SHORT).show();
                            });


                        }else{

                            send_message_button.setVisibility(View.GONE);
                            containerListRequest.setVisibility(View.VISIBLE);

                            if(book.getStatus() == 1){ //libro in prestito

                                String borrowerName = getString(R.string.lent) + " <b>"+ book.getBorrowerName() + "</b>";

                                endLendingButton.setVisibility(View.VISIBLE);
                                lendingMessage.setVisibility(View.VISIBLE);
                                lendingMessage.setText(Html.fromHtml(borrowerName));
                                request_list_title.setVisibility(View.GONE);
                                requestRecycleListView.setVisibility(View.GONE);

                                endLendingButton.setOnClickListener(v->{
                                    dbRef.child("books").child(bookId).child("status").setValue(0);
                                    lendingMessage.setVisibility(View.GONE);
                                    endLendingButton.setVisibility(View.GONE);
                                });
                            }
                            else {  //non ancora in prestito

                                requestRecycleListView.setVisibility(View.VISIBLE);
                                getRequestList(dbRef, book, myUser.getUserID());
                                if(requestAdapter.getItemCount() > 0) {
                                    //almeno una richiesta
                                    request_list_title.setVisibility(View.VISIBLE);
                                }else{
                                    //nessuna richiesta
                                    containerListRequest.setVisibility(View.GONE);
                                }
                            }

                        }
                        if(showProfile){
                            goToProfileButton.setOnClickListener(v->{
                                Intent showProfileIntent = new Intent(getApplicationContext(),showProfile.class);
                                showProfileIntent.putExtra("userId",book.getOwner());
                                startActivity(showProfileIntent);
                            });
                        }else{
                            goToProfileButton.setVisibility(View.GONE);
                        }

                        for (int i = 0; i<4; i++){

                            //request for Images
                            StorageReference ref = FirebaseStorage.getInstance().getReference().child("bookImages/"+ bookId + "/" + i);

                            //todo ref.getBytes lancia degli errori cercare di capire cosa sono
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString()).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {

                                        @Override
                                        public void onSuccess(byte[] bytes) {

                                            imagesList.add(new BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));
                                            adapter.notifyDataSetChanged();
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
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        errorMethod(R.string.network_problem);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                errorMethod(R.string.network_problem);
            }
        });

    }

    private class UserModel{

        private String userId;
        private String nameSurname;

        public UserModel(String userId, String nameSurname){
            this.userId = userId;
            this.nameSurname = nameSurname;
        }

        public String getNameSurname(){
            return  nameSurname;
        }

        public String getUserId(){
            return  userId;
        }
    }

    private void bookRequested(DatabaseReference dbRef, BookInfo book, String user){

        Query query = dbRef.child("bookRequests").child(book.getBookID()).child(user);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    sendRequestButton.setVisibility(View.GONE);
                    statusTextView.setText(R.string.lending_req_sent);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getRequestList(DatabaseReference dbRef, BookInfo book, String myUserId){

        //set query
        Query query = dbRef.child("bookRequests").child(book.getBookID());

        //get and populate list
        FirebaseRecyclerOptions<UserModel> options = new FirebaseRecyclerOptions.Builder<UserModel>()
                .setQuery(query, new SnapshotParser<UserModel>() {
                    @NonNull
                    @Override
                    public UserModel parseSnapshot(@NonNull DataSnapshot snapshot) {

                        if(snapshot==null && snapshot.getValue()==null)
                            return null;

                        return new UserModel(snapshot.getKey(),snapshot.getValue().toString());
                    }
                })
                .setLifecycleOwner(this)
                .build();

        requestAdapter = new FirebaseRecyclerAdapter<UserModel,BookRequest>(options) {

            @NonNull
            @Override
            public  BookRequest onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_holder_book_request, parent, false);

                return new BookRequest(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull BookRequest holder, int position, @NonNull UserModel model) {
                holder.bindData(model.userId, book.getBookID(), myUserId , model.nameSurname, book.getStatus(), book.getBorrower());
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                containerListRequest.setVisibility(getItemCount() == 0 && containerListRequest.getVisibility() == View.GONE? View.GONE : View.VISIBLE);
            }
        };

        requestRecycleListView.setLayoutManager(new LinearLayoutManager(context));
        requestRecycleListView.setAdapter(requestAdapter);

    }

    private void errorMethod(int txt){

        Toast.makeText(context,txt,Toast.LENGTH_SHORT).show();
        Log.e(deBugTag,"dataSnapshot non esiste");
        onBackPressed();
    }
}
