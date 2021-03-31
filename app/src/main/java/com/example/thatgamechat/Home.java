package com.example.thatgamechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;


public class Home<T> extends AppCompatActivity {
    String refID;
    FirebaseAuth fAuth;

    public static class GridItem {
        public Bitmap image;
        public String title;
        public String date;
    }

    public class GridItemAdapter extends ArrayAdapter<T> {

        private final LayoutInflater mInflater;

        @Override
        public T getItem(int i) {
            return super.getItem(i);
        }

        public GridItemAdapter(Context context, int rid, List<T> list) {
            super(context, rid, list);
            mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //retrieve data
            GridItem item = (GridItem) getItem(position);

            //Use layout file to generate view
            View view = mInflater.inflate(R.layout.activity_list_item, null);

            //setting image
            ImageView image;
            image = view.findViewById(R.id.image);
            image.setImageBitmap(item.image);

            //setting title
            TextView title;
            title = view.findViewById(R.id.title);
            title.setText(item.title);

            //setting comment
            TextView date;
            date = view.findViewById(R.id.date);
            date.setText(item.date);


            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        Intent myIntent = getIntent();
        refID = myIntent.getStringExtra("refID");

        //create image & place it at /res/drawable
        Bitmap defaultImage;
        defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.image_profile);

        //get today's date and format for string output
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        //create testing data
        List<T> list = new ArrayList<>();

        GridItem item = new GridItem();
        item.image = defaultImage;
        item.title = "Chat room 1";
        item.date = "Online";
        list.add((T)item);

        GridItem item2 = new GridItem();
        item2.image = defaultImage;
        item2.title = "Chat room 2";
        item2.date = "Online";
        list.add((T)item2);

        GridItem item3 = new GridItem();
        item3.image = defaultImage;
        item3.title = "Chat room 3";
        item3.date = "Online";
        list.add((T)item3);




        //initialize shop button
        final Button shopButton;
        shopButton = findViewById(R.id.home_shop_button);
        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(shopButton.getContext(), Shop.class);
                myIntent.putExtra("refID", refID);
                startActivity(myIntent);

            }
        });

        //initialize tic tac toe button
        Button tttButton;
        tttButton = findViewById(R.id.home_ttt_button);
        tttButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(shopButton.getContext(), TicTacToe.class);
                myIntent.putExtra("refID", refID);
                startActivity(myIntent);
            }
        });


        //initializing adapter
        GridItemAdapter adapter;
        adapter = new GridItemAdapter(this, 0, list);


        //initializing gridView & setting adapter
        final GridView gridView = findViewById(R.id.GridView01);
        gridView.setAdapter(adapter);

        fAuth = FirebaseAuth.getInstance();

        //sign out button
        Button signOutButton;
        signOutButton = findViewById(R.id.home_signout_button);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        String username, email, uid;
        Uri photoUrl;
        boolean emailVerified;


        if (fAuth.getCurrentUser() != null) {
            username = fAuth.getCurrentUser().getDisplayName();
            email =  fAuth.getCurrentUser().getEmail();
            photoUrl =  fAuth.getCurrentUser().getPhotoUrl();
            emailVerified =  fAuth.getCurrentUser().isEmailVerified();
            uid =  fAuth.getCurrentUser().getUid();  // The user's ID, unique to the Firebase project. Do NOT use
            // this value to authenticate with your backend server, if
            // you have one. Use User.getToken() instead.
            TextView t1;
            t1 = findViewById(R.id.home_text1);
            t1.setText("Username: " + username);

            TextView t2;
            t2 = findViewById(R.id.home_text2);
            t2.setText("Account email: " + email);

            TextView t3;
            t3 = findViewById(R.id.home_text3);
            t3.setText("Email verified? " + emailVerified);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent myIntent = new Intent(gridView.getContext(), Messages.class);
                String channelId = "";
                //chat room 1
                //pass channel id
                if (id == 0){
                    myIntent.putExtra("firstKeyName","qAsa1lwae0kcHtyo");
                    Log.v("testtest", "qAsa1lwae0kcHtyo");
                    startActivity(myIntent);
                }
                //chat room 2
                //pass channel id
                if (id == 1){
                    myIntent.putExtra("firstKeyName","uYBPPresEC6GNq2r");
                    Log.v("testtest", "uYBPPresEC6GNq2r");
                    startActivity(myIntent);
                }
                //chat room 3
                //pass channel id
                if (id == 2){
                    myIntent.putExtra("firstKeyName","MiBNOgaJ9bXeMTcD");
                    Log.v("testtest", "MiBNOgaJ9bXeMTcD");
                    startActivity(myIntent);

                }

            }
        });
    }
    }
