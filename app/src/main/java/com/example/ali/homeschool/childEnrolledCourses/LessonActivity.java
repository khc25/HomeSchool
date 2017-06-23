package com.example.ali.homeschool.childEnrolledCourses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.ali.homeschool.InstructorHome.CourseCreated;
import com.example.ali.homeschool.InstructorLessons.LessonModel;
import com.example.ali.homeschool.R;
import com.example.ali.homeschool.childClass.ClassActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LessonActivity extends AppCompatActivity {

    List<CourseCreated> enrolledLessonList;
    RecyclerView enrolledRecyclerView;
    Toolbar toolbar;
    CourseCreated course;
    ValueEventListener listener;
    List<LessonModel> lessonModelList;
    DatabaseReference db;
    ValueEventListener queryListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lesson);
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        enrolledRecyclerView = (RecyclerView) findViewById(R.id.lessonsRV2);
        enrolledRecyclerView.setHasFixedSize(true);
//        LinearLayoutManager categoryLayoutManger = new LinearLayoutManager(getApplicationContext(),
//                LinearLayoutManager.VERTICAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        enrolledRecyclerView.setLayoutManager(gridLayoutManager);
        Log.e("Test", "myLessonActivity");
        db = FirebaseDatabase.getInstance().getReference();
        lessonModelList = new ArrayList<LessonModel>();

        Intent intent = getIntent();
        course = intent.getParcelableExtra("course");

        Log.e("courseinLessonActivity", course.toString());

    }
    @Override
    protected void onPause(){
        if (listener != null)
            db.removeEventListener(listener);
        super.onPause();
    }
    @Override
    protected void onStart() {
        super.onStart();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("Lesson","Datasnapshot "+ dataSnapshot);
                lessonModelList = new ArrayList<LessonModel>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    LessonModel lessonModel = dataSnapshot1.getValue(LessonModel.class);
                    Log.v("Lesson " , lessonModel.getName());
                    lessonModelList.add(lessonModel);
                }

                StudentLessonAdapter studentLessonAdapter = new StudentLessonAdapter(lessonModelList,
                        new StudentLessonAdapter.OnClickHandler() {
                            @Override
                            public void onClick(LessonModel test) {
                                Intent intent = new Intent(getApplicationContext(),
                                        ClassActivity.class);
                                intent.putExtra("courseId", course.getCourse_id());
                                intent.putExtra("lessonid", test.getId());

                                startActivity(intent);
                            }
                        }, LessonActivity.this, course.getCourse_id());
                studentLessonAdapter.setCourseId(course.getCourse_id());
                enrolledRecyclerView.setAdapter(studentLessonAdapter);


                enrolledRecyclerView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {


                        return false;
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        Log.v("Lesson","Course id : "+ course.getCourse_id());
        db.child("courses").child(course.getCourse_id()).child("lessons").addValueEventListener(listener);

    }
}
