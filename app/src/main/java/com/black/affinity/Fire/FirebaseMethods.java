package com.black.affinity.Fire;

import android.util.Log;

import androidx.annotation.NonNull;

import com.black.affinity.Pojos.Ideas;
import com.black.affinity.Pojos.Note;
import com.black.affinity.Pojos.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class FirebaseMethods {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference inviteRef,notesRef,participantsRef;
    private String user;
    private String session;

    public interface ReadStatus {
        void DataRead(DataSnapshot dataSnapshot);
    }

    public interface WriteStatus {
        void DataWrite(String msg);
    }

    public FirebaseMethods(String user) {
        this.user = user;
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.inviteRef = firebaseDatabase.getReference("invite");
        this.notesRef = firebaseDatabase.getReference("notes");
        this.participantsRef = firebaseDatabase.getReference("participants");
//        this.devicesRef = firebaseDatabase.getReference("devices");
//        this.roomsRef = firebaseDatabase.getReference("chatrooms");
//        this.msgRef = firebaseDatabase.getReference("messages");
    }

    /* Get chat-room under caretaker node which indicated what are the chat-room a caretaker has joined*/
//    public void getTeam(final ReadStatus status) {
//        careRef.child("chatrooms").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                status.DataRead(snapshot);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
    /* Get messages from a given chat id under Messages node */
    public void addNotes(String team, Ideas ideas, final WriteStatus status) {
        notesRef.child(team).push().setValue(ideas)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        status.DataWrite("Done");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                status.DataWrite("Failed");
            }
        });
    }

    /* Get messages from a given chat id under Messages node */
    public void fetchInvite(String email,final ReadStatus status) {
        inviteRef.orderByChild("mail").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    status.DataRead(snapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateStatus(String key,String response){
        inviteRef.child(key).child("status").setValue(response);
    }

    public void joinTeam(String team, User user, WriteStatus writeStatus){
        participantsRef.child(team).push().setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        writeStatus.DataWrite("Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                writeStatus.DataWrite("Failed");
            }
        });
    }

    public void getNotes(String team,ReadStatus status){
        notesRef.child(team).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    status.DataRead(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    status.DataRead(null);
            }
        });
    }

    /* Get userslist from a given chatid under Chatroom Node */
//    public void getUsers(String chatId, final ReadStatus status) {
//        roomsRef.child(chatId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    status.DataRead(snapshot);
//                } else {
//                    Log.d("UsersFound", "Not Found");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

//    public void sendMessages(String chatRoomId, Chat chat, final WriteStatus status) {
//        msgRef.child(chatRoomId).child(dateStamp()).push().setValue(chat)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        status.DataWrite("Done");
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                status.DataWrite("Failed");
//            }
//        });
//    }


    /* Get device status of a give user id under User Node */
  /*  public void createDeviceStatus(String uid) {
//        devicesRef.child(uid).child(dateStamp()).push().setValue(chat)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        status.DataWrite("Done");
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                status.DataWrite("Failed");
//            }
//        });
    }*/

//    public void getDeviceStatus(final String uid, final ReadStatus readStatus) {
//        devicesRef.child(uid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    readStatus.DataRead(snapshot);
//                } else {
//                    Log.d("DevStatus", "Not Found");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

//    public void sendRequest(String data) {
//        FirebaseFunctions.getInstance()
//                .getHttpsCallable("addFriend")
//                .call(data)
//                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
//                    @Override
//                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
//                        Log.d("cloudFun", httpsCallableResult.toString());
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d("cloudFun", e.toString());
//            }
//        });
//    }

//    public String dateStamp() {
//        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//    }

}
