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
    }

    /* Get messages from a given chat id under Messages node */
    public void addNotes(String team, Ideas ideas, final WriteStatus status) {
        notesRef.child(team).push().setValue(ideas)
                .addOnSuccessListener(aVoid -> status.DataWrite("Done")).addOnFailureListener(new OnFailureListener() {
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

}
