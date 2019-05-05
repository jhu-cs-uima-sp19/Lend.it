package com.example.lendit;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class Chat extends AppCompatActivity { //TODO: combine this with chatpage
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    String username;
    String theirusername;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String TAG = "ChatActivity";
    DocumentReference reference1, reference2;

    //frontend variables
    private ListView listView;
    private View btnSend;
    private EditText editText;
    boolean myMessage = true;
    boolean justStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        theirusername = bundle.getString("postuser");
        justStarted = true;

        //frontend message bubble code
//        ChatBubbles = new ArrayList<>();
//
//        listView = (ListView) findViewById(R.id.list_msg);
//        btnSend = findViewById(R.id.btn_chat_send);
//        editText = (EditText) findViewById(R.id.msg_type);
//
//        //set ListView adapter first
//        adapter = new MessageAdapter(this, R.layout.left_chat_bubble, ChatBubbles);
//        listView.setAdapter(adapter);
//
//        //event for button SEND
//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (editText.getText().toString().trim().equals("")) {
//                    Toast.makeText(ChatOutdated.this, "Please input some text...", Toast.LENGTH_SHORT).show();
//                } else {
//                    //add message to list
//                    ChatBubble ChatBubble = new ChatBubble(editText.getText().toString(), myMessage);
//                    ChatBubbles.add(ChatBubble);
//                    adapter.notifyDataSetChanged();
//                    editText.setText("");
//                    if (myMessage) {
//                        myMessage = false;
//                    } else {
//                        myMessage = true;
//                    }
//                }
//            }
//        });


        //backend/database code
        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        reference1 =  db.collection("messages").document(username + "_" + theirusername);
        reference2 =  db.collection("messages").document(theirusername + "_" + username);

        Log.d(TAG, "username: " + username);
        Log.d(TAG, "their username " + theirusername);

        // prepopulate chat
        reference1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<Map<String, Object>> m = (ArrayList<Map<String, Object>>) documentSnapshot.get("messages");
                if (m != null && m.size() > 0) {
                    for (int i = 0; i < m.size() - 1; i++) {
                        Map<String, Object> lastChat = m.get(i);
                        String name = "";
                        if ((boolean) lastChat.get("myMessage") == true) {
                            name = username;
                            addMessageBox("You:-\n" + lastChat.get("content").toString(), 2);
                        } else {
                            name = theirusername;
                            addMessageBox(theirusername + ":-\n" + lastChat.get("content").toString(), 1);
                        }
                    }
                }
                justStarted = false;
            }
        });

        reference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    Map doc = snapshot.getData();;
                    ArrayList<Map<String, Object>> m = (ArrayList<Map<String, Object>>) doc.get("messages");
                    if (m.size() > 0 && !justStarted) {
                        Map<String, Object> lastChat = m.get(m.size() - 1);
                        String name = "";
                        if ((boolean)lastChat.get("myMessage") == true) {
                            name = username;
                            addMessageBox("You:-\n" + lastChat.get("content").toString(), 2);
                        } else {
                            name = theirusername;
                            addMessageBox(theirusername + ":-\n" + lastChat.get("content").toString(), 1);
                        }
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                    Map<String, Object> initialize1 = new HashMap<String, Object>();
                    ArrayList<Map<String, Object>> m = new ArrayList<Map<String, Object>>();
                    initialize1.put("messages", m);
                    initialize1.put("user1", username);
                    initialize1.put("user2", theirusername);

                    db.collection("messages").document(username + "_" + theirusername).set(initialize1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });

                    Map<String, Object> initialize2 = new HashMap<String, Object>();
                    initialize2.put("messages", m);
                    initialize2.put("user1", theirusername);
                    initialize2.put("user2", username);
                    db.collection("messages").document(theirusername + "_" + username).set(initialize2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                }
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){

                    reference1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ArrayList<Map<String, Object>> messages1 = (ArrayList<Map<String, Object>>) documentSnapshot.getData().get("messages");
                            Log.d(TAG, "message text" + messageText);
                            Map<String, Object> chatBubble1 = new HashMap<String, Object>();
                            chatBubble1.put("content", messageText);
                            chatBubble1.put("myMessage", true);
                            messages1.add(chatBubble1);
                            reference1.update("messages", messages1);
                        }
                    });

                    reference2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            ArrayList<Map<String, Object>> messages2 = (ArrayList<Map<String, Object>>) documentSnapshot.getData().get("messages");
                            Log.d(TAG, "message text" + messageText);
                            Map<String, Object> chatBubble2 = new HashMap<String, Object>();
                            chatBubble2.put("content", messageText);
                            chatBubble2.put("myMessage", false);
                            messages2.add(chatBubble2);
                            reference2.update("messages", messages2);
                        }
                    });
                    messageArea.setText("");
                }

            }
        });
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;
        //lp2.setMargins(24, 0, 24, 0);

        if(type == 1) {
            lp2.gravity = Gravity.LEFT;
            //textView.setBackgroundColor(getResources().getColor(R.color.green));
            textView.setPadding(70,40,50,40);
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        else{
            lp2.gravity = Gravity.RIGHT;
            //textView.setBackgroundColor(getResources().getColor(R.color.gray));
            textView.setBackgroundResource(R.drawable.bubble_in);
            textView.setPadding(50,40,60,40);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_mode_close_button) {
            Chat.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}