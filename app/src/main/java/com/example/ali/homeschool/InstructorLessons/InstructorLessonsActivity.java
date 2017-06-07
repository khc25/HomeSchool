package com.example.ali.homeschool.InstructorLessons;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.ali.homeschool.InstructorHome.CourseCreated;
import com.example.ali.homeschool.InstructorTopic.InstructorTopicActivity;
import com.example.ali.homeschool.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InstructorLessonsActivity extends AppCompatActivity {
    RecyclerView lessonsRV;
    String m_Text = "";
    DatabaseReference db;
    CourseCreated courseCreated;
    LessonModel lessonModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_lessons);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab3);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // this line supports the back button to go back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InstructorLessonsActivity.this);
                builder.setTitle("Title");

                // Set up the input
                final EditText input = new EditText(InstructorLessonsActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(
                        InputType.TYPE_CLASS_TEXT );
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
//                        Map<String,String> lesson = new HashMap<String, String>();
                        String key =db.child("courses").child(courseCreated.getCourse_id()).child("lessons").push().getKey();
//                        lesson.put("id",key);
//                        lesson.put("name",m_Text);
                        db.child("courses").child(courseCreated.getCourse_id()).child("lessons").child(key).child("id").setValue(key);
                        db.child("courses").child(courseCreated.getCourse_id()).child("lessons").child(key).child("name").setValue(m_Text);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
//        addTopicB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), InstructorTopicActivity.class);
//                intent.putExtra("lesson",lessonModel);
//                startActivity(intent);
//            }
//        });
        lessonsRV = (RecyclerView) findViewById(R.id.lessonsRV);
        lessonsRV.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        lessonsRV.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lessonsRV.getContext(),layoutManager.getOrientation());
        lessonsRV.addItemDecoration(dividerItemDecoration);

        db = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("course")){
            courseCreated = intent.getParcelableExtra("course");
            Log.v("Test","Course "+ courseCreated.getCourse_id());
            toolbar.setTitle(courseCreated.getName());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("Test ", ":------------"+courseCreated.getCourse_id());
        db.child("courses").child(String.valueOf(courseCreated.getCourse_id())).child("lessons").addValueEventListener(
                new ValueEventListener() {
                    List<LessonModel> lessonModelList;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        lessonModelList = new ArrayList<LessonModel>();
                        for (DataSnapshot d : dataSnapshot.getChildren()){
                            Log.v("Test","Lesson " + d.toString());
                            lessonModel = d.getValue(LessonModel.class);
                            lessonModelList.add(lessonModel);
                            Log.v("Test","LESSON __ "+ lessonModel.toString());
                        }
                        LessonAdapter lessonAdapter = new LessonAdapter(lessonModelList,
                                new LessonAdapter.OnClickHandler() {
                                    @Override
                                    public void onClick(LessonModel test) {
                                        Intent intent = new Intent(getApplicationContext(), InstructorTopicActivity.class);
                                        intent.putExtra("lesson",test.getId());
                                        intent.putExtra("courseID",courseCreated.getCourse_id());
                                        startActivity(intent);
                                    }
                                });
                        lessonsRV.setAdapter(lessonAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}