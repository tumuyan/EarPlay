package com.oboard.ts;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.oboard.ts.R;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements SensorEventListener {
    AudioManager audioManager;
    SensorManager sensorManager;
    PowerManager.WakeLock mWakeLock;
    PowerManager mPowerManager;

    CheckBox cb;//传感开关
    Timer mTimer;//timer
    TimerTask mTimerTask;//timertask
    int mMode;
    String mName = "";
    View ii;
    SeekBar mVolumeBar;
    Sensor mSensor;//传感器


    RadioButton RadioButton1;
    RadioButton RadioButton2;
    RadioButton RadioButton3;
    SeekBar CloseSeekBar0;
    SeekBar CloseSeekBar1;
    TextView CloseText0;
    TextView CloseText1;
    TextView CloseText2;
    TextView VolueText;
    int close1=70;
    float close2=0;
    int close0=40;

    CheckBox CloseEar0;
    Boolean ear0=true;
    CheckBox CloseEar1;
    Boolean ear1=true;
    CheckBox CloseEar2;
    Boolean ear2=false;
    CheckBox CloseEar3;
    Boolean ear3=true;



    int maxVolume, systemVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        RadioButton1=(RadioButton)findViewById(R.id.radioButton1);
        RadioButton2=(RadioButton)findViewById(R.id.radioButton2);
        RadioButton3=(RadioButton)findViewById(R.id.radioButton3);
        CloseSeekBar0=(SeekBar)findViewById(R.id.closeSeekBar0);
        CloseSeekBar1=(SeekBar)findViewById(R.id.closeSeekBar1);
        CloseText0=(TextView)findViewById(R.id.closeText0);
        CloseText1=(TextView)findViewById(R.id.closeText1);
        CloseText2=(TextView)findViewById(R.id.closeText2);
        VolueText=(TextView)findViewById(R.id.volueText);
        CloseEar0=(CheckBox)findViewById(R.id.closeEar0);
        CloseEar1=(CheckBox)findViewById(R.id.closeEar1);
        CloseEar2=(CheckBox)findViewById(R.id.closeEar2);
        CloseEar3=(CheckBox)findViewById(R.id.closeEar3);
        //    ii = findViewById(R.id.i);
        cb = (CheckBox)findViewById(R.id.mainCheckBox1);
        mVolumeBar = (SeekBar)findViewById(R.id.mainSeekBar1);

        CloseEar0.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton view, boolean state) {
                ear0=state;
            }
        });
        CloseEar1.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton view, boolean state) {
                ear1=state;
            }
        });
        CloseEar2.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton view, boolean state) {
                ear2=state;
            }
        });
        CloseEar3.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton view, boolean state) {
                ear3=state;
            }
        });

        cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton view, boolean state) {
                    if (state) {
                        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);


                        //息屏设置
                        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "");

                        //注册传感器,先判断有没有传感器
                        if (mSensor != null)
                            sensorManager.registerListener(MainActivity.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
                    } else {
                        //传感器取消监听
                        sensorManager.unregisterListener(MainActivity.this);
                        //释放息屏
                        if (mWakeLock.isHeld())
                            mWakeLock.release();
                        mWakeLock = null;
                        mPowerManager = null;
                    }
                }
            });

        audioManager = (AudioManager)this.getSystemService("audio");
        //获取系统的最大声音
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //获取系统当前的声音
        systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        //设置最大值
        mVolumeBar.setMax(maxVolume);
        //设置为系统现在的音量
        mVolumeBar.setProgress(systemVolume);
        VolueText.setText(""+systemVolume);
        mVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar sb, int p, boolean b) {
                    systemVolume = p;
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, systemVolume, 0);
                    VolueText.setText(""+p);
                }
                public void onStartTrackingTouch(SeekBar sb) {}
                public void onStopTrackingTouch(SeekBar sb) {}
            });

        mTimer = new Timer();
        mTimerTask = new TimerTask(){
            public void run() {
                audioManager.setMode(mMode);
                audioManager.setSpeakerphoneOn(false);
                // 设置为通话状态
                setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

            }
        };
        mTimer.schedule(this.mTimerTask, 0, 1000);

        mName = getResources().getString(R.string.a);
        setNotification();


        CloseSeekBar0.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CloseText0.setText(""+i/10.0);
                close0=i;
                if(close1<close0){
                    CloseSeekBar1.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        CloseSeekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CloseText1.setText(""+i/10.0);
                close1=i;
                if(close1<close0){
                    CloseSeekBar0.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 传感器变化
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        CloseText2.setText(""+event.values[0]);
        close2=event.values[0];
        Log.i("close2",""+close0/event.values[0]);
        if (close0/event.values[0]  >= 10 ) {
            if(ear0){
                if(ear2){
                    onModeChange(2);
                }else {
                    onModeChange(3);}
            }
            //贴近手机
            //关闭屏幕
            if (!mWakeLock.isHeld() && ear3)
                mWakeLock.acquire();

        } else if(close1/event.values[0]<10) {
            //离开手机
            //唤醒设备
            if(ear1){

                onModeChange(0);
            }
            if (mWakeLock.isHeld() && ear3)
                mWakeLock.release();
        }
    }

    int num = -1;
    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getStringExtra("i") != null) {
            ((View)mVolumeBar.getParent()).setVisibility(View.GONE);
            String[] n = new String[] {
                getResources().getString(R.string.a),
                getResources().getString(R.string.b),
                getResources().getString(R.string.c)
            };
            
            new AlertDialog.Builder(this)
                .setTitle(getString(R.string.name))
                .setIcon(R.mipmap.i)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (num >= 0)
                            onModeChange(new int[] {0, 3, 2, 1}[num]);
                        onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                    }
                })
                .setSingleChoiceItems(n, new int[] {0, 3, 2, 1}[mMode], new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        num = which;
                    }
                })
                .create().show();
            
        } else {
            ((View)mVolumeBar.getParent()).setVisibility(View.VISIBLE);
        }

        super.onNewIntent(intent);
    }



    @Override
    public void onAccuracyChanged(Sensor p1, int p2) {

    }

    @Override
    protected void onDestroy() {
        cancelNotification();
        super.onDestroy();
    }

    @Override

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            moveTaskToBack(true);
            return true;
        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
            if(systemVolume>0){
                systemVolume--;
      mVolumeBar.setProgress(systemVolume);
            }


            return true;
        }
        else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP)
        {
            if(systemVolume<maxVolume){
                systemVolume++;
                mVolumeBar.setProgress(systemVolume);
            }
            return true;
        }
        else return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
            Log.i("按键松开","+");
            return true;
        }
        else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP)
        {
            Log.i("按键松开","-");
            return true;
        }
        else return super.onKeyUp(keyCode, event);
    }

    public void onModeChange(View v) {
        onModeChange(Integer.parseInt(v.getTag().toString()));
    }

    public void onModeChange(int i) {
        if (mMode == i)
            return;
        mMode = i;//储存模式
        int[] y = new int[] {0, 3, 2, 1};
        mName = new String[] {
            getResources().getString(R.string.a),
            getResources().getString(R.string.b),
            getResources().getString(R.string.c)
        }[y[i]];

        Log.i("onChange",mName);

        switch (i){
            case 0:
                RadioButton1.toggle();
                Log.i("onChange",""+i);
                break;
            case 3:
                RadioButton2.toggle();
                Log.i("onChange",""+i);
                break;
            case 2:
                RadioButton3.toggle();
                Log.i("onChange",""+i);
                break;

        }


    //    mbar(ii.getHeight() * y[i]);//指示条
        cancelNotification();//删除通知
        setNotification();//显示通知
    }


    public boolean onCreateOptionsMenu(Menu menu) { 
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true; 
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.gy:
                new AlertDialog.Builder(this)
                    .setTitle("关于『听筒播放』")
                    .setMessage("我即使是死了,钉在在棺材里了,也要在墓里,用这腐朽的声带喊出：“我爱你。” \n\n作品本人仅发布于酷安（coolapk）。\n基于EarPlay(一块小板子 2232442466)，目前还在施工中。\n\n\n\ntumuyan，2018.9\n")
                    .setPositiveButton("确定", null)
                    .setNegativeButton("不确定", new DialogInterface.OnClickListener() { 
                        @Override 
                        public void onClick(DialogInterface dialog, int which) { 
                            finish();
                        } 
                    })
                    .create().show(); 
                break;
                //case R.id.jz:

                //  break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

/*    public void mbar(float y) {
        //1.设置属性的初始值和结束值
        final ValueAnimator mAnimator = ValueAnimator.ofFloat(ii.getY(), y);
        //2.为目标对象的属性变化设置监听器
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    ii.setY((float) animation.getAnimatedValue());
                }
            });
        //3.设置动画的持续时间
        mAnimator.setDuration(250)
            .start();
    }*/

    // 添加常驻通知
    public void setNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("i", "i");
        PendingIntent contextIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification.Builder nb = new Notification.Builder(this);
        nb.setSmallIcon(R.mipmap.i)
            .setOngoing(true)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.i))
            .setContentIntent(contextIntent)
            .setContentTitle("切换声音输出")
            .setContentText("当前"  + mName)
//         .setVisibility(Notification.VISIBILITY_PUBLIC)
        ;

        //设置消息属性
        //必须设置的属性：小图标 标题 内容
        notificationManager.notify(0, nb.build());

    }


    // 取消通知
    public void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }




}
