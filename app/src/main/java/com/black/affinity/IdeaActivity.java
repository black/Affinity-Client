package com.black.affinity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.black.affinity.Fire.FirebaseMethods;
import com.black.affinity.Ideas.IdeaRVAdapter;
import com.black.affinity.Ideas.SpacesItemDecoration;
import com.black.affinity.Pojos.Ideas;
import com.black.affinity.Pojos.Note;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IdeaActivity extends AppCompatActivity {
    private FirebaseMethods firebaseMethods;
    private String user;
    private String myAvatar;
    private String team;
    private String status;
    private EditText msgText;
    private MaterialButton submit;

    private RecyclerView recyclerView;
    private IdeaRVAdapter ideaRVAdapter;
    private List<Ideas> ideasList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea);
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseMethods = new FirebaseMethods(user);
        team = getIntent().getStringExtra("TEAM");
        status = getIntent().getStringExtra("STATUS");
        myAvatar= getIntent().getStringExtra("AVATAR");
        setTitle(team.toUpperCase());
        recyclerView = findViewById(R.id.ideasList);
        ideaRVAdapter = new IdeaRVAdapter(ideasList,myAvatar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(ideaRVAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(2,getResources().getDimensionPixelSize(R.dimen.spacing),true));

        msgText = findViewById(R.id.my_idea);
        submit = findViewById(R.id.submitIdea);

        submit.setOnClickListener(v -> {
            if(!team.isEmpty() && status.equals("accepted")) {
                Ideas idea = new Ideas();
                idea.setMsg(msgText.getText().toString());
                idea.setAvatar(myAvatar);
                idea.setUser(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                idea.setTime(String.valueOf(new Date().getTime()));
                addNote(team, idea);
            }
            msgText.setText("");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllNotes();
    }

    private void getAllNotes(){
        firebaseMethods.getNotes(team, new FirebaseMethods.ReadStatus() {
            @Override
            public void DataRead(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Ideas idea = snapshot.getValue(Ideas.class);
                    ideasList.add(idea);
                    ideaRVAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    private void addNote(String team, Ideas ideas){
        firebaseMethods.addNotes(team, ideas, new FirebaseMethods.WriteStatus() {
            @Override
            public void DataWrite(String msg) {
                Log.d("FireStatus",msg+" write");
            }
        });
    }


}