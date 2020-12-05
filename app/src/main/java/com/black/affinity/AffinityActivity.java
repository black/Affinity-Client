package com.black.affinity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.black.affinity.Fire.FirebaseMethods;
import com.black.affinity.Pojos.Invite;
import com.black.affinity.Pojos.User;
import com.black.affinity.Teams.TeamRVAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AffinityActivity extends AppCompatActivity {

    private FirebaseMethods firebaseMethods;
    private RecyclerView recyclerView;
    private TeamRVAdapter teamRvAdapter;
    private List<Invite> teamList = new ArrayList<>();
    private String invetedBy;

    private User user;
    private String myAvatar="fox";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affinity);
        String participant = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setTitle("AFFINITY");
        user = new User();
        user.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        user.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        user.setProfile(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
        firebaseMethods = new FirebaseMethods(participant);

        recyclerView = findViewById(R.id.teamsList);
        teamRvAdapter = new TeamRVAdapter(teamList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(teamRvAdapter);
        teamRvAdapter.setOnItemClickListener(this::init);
    }

    private void init(int pos){
        String currentTeam = teamList.get(pos).getTeam();
        String cuurTeamName = teamList.get(pos).getInfo().get("name");
        String myKey = teamList.get(pos).getUniqueId();
        String status = teamList.get(pos).getStatus();
        if (status.equals("pending")){
            joinTeam(myKey, currentTeam,"accepted");
        }else{
            Intent intent = new Intent(getBaseContext(), IdeaActivity.class);
            intent.putExtra("TEAM_ID", currentTeam);
            intent.putExtra("TEAM_NAME", cuurTeamName);
            intent.putExtra("STATUS", status);
            intent.putExtra("AVATAR", myAvatar);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTeamName(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    private void getTeamName(String email){
        firebaseMethods.fetchInvite(email,new FirebaseMethods.ReadStatus() {
            @Override
            public void DataRead(DataSnapshot dataSnapshot) {
                teamList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Invite invite = snapshot.getValue(Invite.class);
                    invite.setUniqueId(snapshot.getKey());
                    teamList.add(invite);
                    Log.d("LogInvite",invite.getInfo().get("name"));
                    teamRvAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void joinTeam(String key,String team,String response){
        firebaseMethods.updateStatus(key,response);
        firebaseMethods.joinTeam(team,user, new FirebaseMethods.WriteStatus() {
            @Override
            public void DataWrite(String msg) {
                Log.d("FireStatus","Joining.." + msg);
                teamRvAdapter.notifyDataSetChanged();
            }
        });
    }

}