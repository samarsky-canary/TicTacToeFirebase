package com.example.tictacfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.example.tictactoe.TicTac
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var game: TicTac
    private var YourTurn: Boolean = false;
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance();
    private var myRef = db.reference;
    private lateinit var MyEmail: String;
    private var mFirebaseAnalytics: FirebaseAnalytics? = null;
    var tictacButtons = ArrayList<Int>(9);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        game = TicTac();
        Init();
        var bundle: Bundle = intent.extras!!;
        MyEmail = bundle.getString("email").toString();

        ListenIncomingRequests();
    }


    fun Init() {
        for (i in 0..8) {
            tictacButtons.add(R.id.TT1 + i);
            Log.d("button", tictacButtons[i].toString());
        }
    }

    fun buttonTicTacClick(view: View) {
        if (sessionID == null)
            return;

        if (!YourTurn){
            Toast.makeText(this,"Not your turn",Toast.LENGTH_LONG).show();
        }
        playGame(tictacButtons.indexOf(view.id), view as ImageButton);
    }


    fun ResetGame() {
        for (buttonId in tictacButtons) {
            var button = findViewById<ImageButton>(buttonId);
            button.setImageResource(0);
        }
        game = TicTac();
    }

    fun Reset(view: View) {
        myRef.child("games").child(sessionID!!).setValue("false");
        ResetGame();
    }

    fun playGame(cellid: Int, button: ImageButton) {
        if (game.isGameWinned) {
            Toast.makeText(
                this,
                "Winner is player ${if (FirstPlayer!!) 1 else 2}",
                Toast.LENGTH_LONG
            ).show();
            return;
        }

        if (YourTurn){
            myRef.child("games").child(sessionID!!).child(cellid.toString())
                .setValue(FirstPlayer.toString());
            YourTurn = false;

        }
    }

    fun HandleButtonPress(cellid: Int, firstPlayer: Boolean, button: ImageButton) {

        if (game.tictacButtonPressed(cellid, firstPlayer!!)) {
            SetTicTacButtonImage(button, firstPlayer)
        }
        if (game.isGameWinned) {
            Toast.makeText(
                this,
                "Winner is player ${if (firstPlayer!!) 1 else 2}",
                Toast.LENGTH_LONG
            ).show();
        }

    }

    fun SetTicTacButtonImage(button: ImageButton, firstPlayer: Boolean) {
        val tic = R.drawable.tic;
        val tac = R.drawable.tac;
        val tictacImage = if (firstPlayer) tac else tic;
        button.setImageResource(tictacImage);
    }

    var FirstPlayer: Boolean? = null;
    fun btAcceptEvent(view: View) {
        var friendName = etFriendName.text.toString();
        FirstPlayer = false;

        myRef.child("users").child(SplitString(friendName)).child("Request").push()
            .setValue(MyEmail);
        InstanceGameSession(SplitString(friendName) + SplitString(MyEmail));
    }

    fun btRequestEvent(view: View) {
        var friendName = etFriendName.text.toString();
        FirstPlayer = true;

        myRef.child("users").child(SplitString(friendName)).child("Request").push()
            .setValue(MyEmail);
        InstanceGameSession(SplitString(MyEmail) + SplitString(friendName));
    }

    var sessionID: String? = null;
    fun InstanceGameSession(sessionId: String) {
        sessionID = sessionId;
        YourTurn = FirstPlayer!!;
//         clear data of the session
        myRef.child("games").child(sessionID!!).setValue("false");
        ResetGame();
        ListenGameButtonsPressed();
    }

    fun SplitString(str: String): String {
        var result = str.split("@");
        return result[0];
    }


    fun ListenGameButtonsPressed() {
        myRef.child("games").child(sessionID!!)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    var key = snapshot.key?.toInt();
                    var player = snapshot.value?.toString()!!.toBoolean();
                    if (player != FirstPlayer)
                        YourTurn = true;
                    HandleButtonPress(key!!,player,findViewById(tictacButtons[key]));
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    ResetGame();
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    var number = 0;
    fun ListenIncomingRequests() {
        myRef.child("users").child(SplitString(MyEmail)).child("Request")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        var td = snapshot.value as HashMap<String, Any>;
                        if (td != null) {
                            var value: String;
                            for (key in td.keys) {
                                value = td[key].toString();
                                etFriendName.setText(value);

                                val notifyMe = Notification();
                                notifyMe.Notify(applicationContext,"New Request income","$value want to play with you", number);
                                number++;
                                myRef.child("users").child(SplitString(MyEmail)).child("Request")
                                    .setValue(false);
                                break;
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("Error", e.message!!);
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            });
    }
}