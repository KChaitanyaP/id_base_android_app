package com.example.id_chat_firebase.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.id_chat_firebase.Adapter.MessageAdapter;
import com.example.id_chat_firebase.Model.Chat;
import com.example.id_chat_firebase.Model.User;
import com.example.id_chat_firebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DialogueFragment extends Fragment {
    String LOGTAG = "DialogueFragment_ACTIVITY";
    String curUsername;
    String IDUserid;
            //= "ruyLmyYIYYZ7YRhuJ9LZ4cFEGyg2";
    FirebaseUser firebaseUser;
    DatabaseReference reference, referenceID;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialogue, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        btn_send = view.findViewById(R.id.btn_send);
        text_send = view.findViewById(R.id.text_send);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference curUserRef=FirebaseDatabase.getInstance()
                .getReference().child("Users").child(firebaseUser.getUid());
        curUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User curUser = dataSnapshot.getValue(User.class);
                assert curUser != null;
                curUsername = curUser.getUsername() ;
                Log.i(LOGTAG, "curUsername: " + curUsername);

                referenceID = FirebaseDatabase.getInstance().getReference("Users");
                referenceID.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // update not to use for loop!
                        for (DataSnapshot aSnapshot: snapshot.getChildren()){
                            User user = aSnapshot.getValue(User.class);
                            assert user != null;
                            if (user.getUsername().equals("ID_"+curUsername)){
                                IDUserid = user.getId();
                                Log.i(LOGTAG, "Obtained IDUserid:" + IDUserid);
                            }
                            reference = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(IDUserid);
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    assert user != null;
                                    readMessages(firebaseUser.getUid(), IDUserid, user.getImageURL());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_send.setOnClickListener(view1 ->
        {
            String msg = text_send.getText().toString();
            if (!msg.equals("")) {
                sendMessage(firebaseUser.getUid(), IDUserid, msg);
            }else{
                Toast.makeText(view.getContext(), "Cannot send empty message",
                        Toast.LENGTH_LONG).show();
            }
            text_send.setText("");
        });


        return view;
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("sender", sender);
        hashmap.put("receiver", receiver);
        hashmap.put("message", message);

        reference.child("Chats").push().setValue(hashmap);
    }

    private void readMessages(final String myID, final String userID, final String imageURL){
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(myID) && chat.getSender().equals(userID) ||
                            chat.getReceiver().equals(userID) && chat.getSender().equals(myID)){
                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(getContext(), mChat, imageURL);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}