package kwa.pravaah;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import kwa.pravaah.database.DbManager;

public class AddAlarm extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String POWERON = "ON";
    private String PUMPOFF = "OFF";
    private String intent_off = "000";
    private String time_off = "000";
    public static String PhoneNo, Name;
    public static String Gtime, GAlarm_on;

    private static final int CONTACT_PICK = 1;
    TimePicker setTime;
    Button bt_ON, bt_OFF;
    EditText Phone;
    private DbManager db;
    boolean mFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        db=new DbManager(this);
        final int mValue = db.numOfRows();
        if (mValue == 0) {
            mFlag = true;
        } else {
            mFlag = false;
        }


        bt_ON=findViewById(R.id.ON);
        bt_OFF=findViewById(R.id.OFF);

        final Calendar now=Calendar.getInstance();
        setTime=findViewById(R.id.PickTime);
        setTime.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        setTime.setCurrentMinute(now.get(Calendar.MINUTE));

        Phone = findViewById(R.id.Phone);

        try {
            bt_ON.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Calendar cal = Calendar.getInstance();

                    cal.set(Calendar.HOUR_OF_DAY, setTime.getCurrentHour());
                    cal.set(Calendar.MINUTE, setTime.getCurrentMinute());
                    cal.set(Calendar.SECOND,00);


                    if (cal.compareTo(now) <= 0) {
                        //Today Set time passed, count to tomorrow
                        cal.add(Calendar.DATE, 1);
                    }
                    String time=cal.getTime().toString();
                    time=time.substring(11,19) ;

                    Toast.makeText(AddAlarm.this, "Alarm is set @" + time, Toast.LENGTH_SHORT).show();
                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


                    Intent myIntent = new Intent(AddAlarm.this, AlarmReceiver.class);

                    String num = Phone.getText().toString();
                    String PhNo = num+",1";
                    myIntent.putExtra("Number", PhNo);

                    int alarmID = (int) cal.getTimeInMillis();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(AddAlarm.this, alarmID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                    assert manager != null;
                    //manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

                    manager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);

                    Toast.makeText(AddAlarm.this, "Shift set", Toast.LENGTH_SHORT).show();
                    String alarmID_to_on= String.valueOf(alarmID);

                    GAlarm_on = alarmID_to_on;
                    if(mFlag) {


                        db.insertUserDetails(num,Name,POWERON , PUMPOFF, alarmID_to_on,"", "", "", "", "", intent_off,time,time_off);

                    }
                    else
                    {
                      /*  if (db.getnumber(num) == true) {
                            db.addPendingIntent_ON(num, alarmID_to_on);
                            db.addTime_ON(num,time);
                        } else {*/
                        db.insertUserDetails(num,Name, POWERON, PUMPOFF, alarmID_to_on,"", "", "", "", "", intent_off,time,time_off);
                        // }

                    }
                }
            });

            bt_OFF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {


                    String num=Phone.getText().toString();
                    Calendar cal = Calendar.getInstance();


                        cal.set(Calendar.HOUR_OF_DAY, setTime.getCurrentHour());
                        cal.set(Calendar.MINUTE, setTime.getCurrentMinute());
                        cal.set(Calendar.SECOND,00);

                        if (cal.compareTo(now) <= 0) {
                            //Today Set time passed, count to tomorrow
                            cal.add(Calendar.DATE, 1);
                        }

                        String time=cal.getTime().toString();
                        time=time.substring(11,19) ;
                        Toast.makeText(AddAlarm.this, "Alarm is set @" + time, Toast.LENGTH_SHORT).show();


                        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


                        Intent myIntent = new Intent(AddAlarm.this, AlarmReceiver.class);


                        String PhNo = num+",2";
                        myIntent.putExtra("Number", PhNo);

                        int alarmID = (int) cal.getTimeInMillis();
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddAlarm.this, alarmID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                        assert manager != null;
                        manager.setRepeating(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);


                        Toast.makeText(AddAlarm.this, "Shift set", Toast.LENGTH_SHORT).show();

                        String alarmID_to_off= String.valueOf(alarmID);
                        int numrows = db.numOfRows();
                        Cursor cursor = db.viewData();

                       /* if (numrows != 0) {
                            try {
                                if (cursor.getCount() > 0) {
                                    while (cursor.moveToNext()) {

                                                db.addPendingIntent_OFF(num, alarmID_to_off);


                                    }
                                }
                            } catch (Exception e)
                            {
                            }
                        }
                        else {*/
                            db.addPendingIntent_OFF(GAlarm_on, alarmID_to_off);
                            db.addTime_OFF(GAlarm_on, time);
                      //  }
                    }


            });
        }
        catch (NullPointerException e) {
            Toast.makeText(this, "Null value", Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent i2 = new Intent(AddAlarm.this, Home.class);
            startActivity(i2);
        } else if (id == R.id.nav_setAlarm) {
            Intent i2 = new Intent(AddAlarm.this, AddAlarm.class);
            startActivity(i2);

        } else if (id == R.id.nav_Cancel) {
            Intent i2 = new Intent(AddAlarm.this, CancelAlarm.class);
            startActivity(i2);

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void contactPickerOnClick(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICK);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case CONTACT_PICK:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }


    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            PhoneNo = cursor.getString(phoneIndex);
            Name = cursor.getString(nameIndex);
            // Set the value to the textviews
            //textView1.setText(name);
            if (PhoneNo.length() == 13)
                PhoneNo = PhoneNo.substring(3, 13);
            Phone.setText(PhoneNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
