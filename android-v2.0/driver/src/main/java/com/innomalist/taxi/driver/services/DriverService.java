package com.innomalist.taxi.driver.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.innomalist.taxi.common.MyTaxiApplication;
import com.innomalist.taxi.common.events.AcceptOrderEvent;
import com.innomalist.taxi.common.events.BackgroundServiceStartedEvent;
import com.innomalist.taxi.common.events.ChangeProfileImageEvent;
import com.innomalist.taxi.common.events.ChangeProfileImageResultEvent;
import com.innomalist.taxi.common.events.ChargeAccountEvent;
import com.innomalist.taxi.common.events.ChargeAccountResultEvent;
import com.innomalist.taxi.common.events.ConnectEvent;
import com.innomalist.taxi.common.events.ConnectResultEvent;
import com.innomalist.taxi.common.events.EditProfileInfoEvent;
import com.innomalist.taxi.common.events.EditProfileInfoResultEvent;
import com.innomalist.taxi.common.events.GetStatusEvent;
import com.innomalist.taxi.common.events.GetStatusResultEvent;
import com.innomalist.taxi.common.events.GetTransactionsRequestEvent;
import com.innomalist.taxi.common.events.GetTransactionsResultEvent;
import com.innomalist.taxi.common.events.GetTravelsEvent;
import com.innomalist.taxi.common.events.GetTravelsResultEvent;
import com.innomalist.taxi.common.events.HideTravelEvent;
import com.innomalist.taxi.common.events.HideTravelResultEvent;
import com.innomalist.taxi.common.events.LoginEvent;
import com.innomalist.taxi.common.events.ProfileInfoChangedEvent;
import com.innomalist.taxi.common.events.ServiceCallRequestEvent;
import com.innomalist.taxi.common.events.ServiceCallRequestResultEvent;
import com.innomalist.taxi.common.events.ServiceCancelEvent;
import com.innomalist.taxi.common.events.ServiceCancelResultEvent;
import com.innomalist.taxi.common.events.SocketConnectionEvent;
import com.innomalist.taxi.common.events.WriteComplaintEvent;
import com.innomalist.taxi.common.events.WriteComplaintResultEvent;
import com.innomalist.taxi.common.models.Driver;
import com.innomalist.taxi.common.utils.CommonUtils;
import com.innomalist.taxi.common.utils.MyPreferenceManager;
import com.innomalist.taxi.common.utils.ServerResponse;
import com.innomalist.taxi.driver.R;
import com.innomalist.taxi.driver.activities.main.MainActivity;
import com.innomalist.taxi.driver.events.CancelRequestEvent;
import com.innomalist.taxi.driver.events.ChangeHeaderImageEvent;
import com.innomalist.taxi.driver.events.ChangeHeaderImageResultEvent;
import com.innomalist.taxi.driver.events.ChangeStatusEvent;
import com.innomalist.taxi.driver.events.ChangeStatusResultEvent;
import com.innomalist.taxi.driver.events.GetRequestsRequestEvent;
import com.innomalist.taxi.driver.events.GetRequestsResultEvent;
import com.innomalist.taxi.driver.events.GetStatisticsEvent;
import com.innomalist.taxi.driver.events.GetStatisticsResultEvent;
import com.innomalist.taxi.driver.events.LocationChangedEvent;
import com.innomalist.taxi.driver.events.LoginResultEvent;
import com.innomalist.taxi.common.events.NotificationPlayerId;
import com.innomalist.taxi.driver.events.PaymentRequestEvent;
import com.innomalist.taxi.driver.events.PaymentRequestResultEvent;
import com.innomalist.taxi.driver.events.RequestReceivedEvent;
import com.innomalist.taxi.driver.events.RiderAcceptedEvent;
import com.innomalist.taxi.driver.events.SendTravelInfoEvent;
import com.innomalist.taxi.driver.events.ServiceFinishEvent;
import com.innomalist.taxi.driver.events.ServiceFinishResultEvent;
import com.innomalist.taxi.driver.events.ServiceInLocationEvent;
import com.innomalist.taxi.driver.events.ServiceStartEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;


public class DriverService extends Service {
    final static String DRIVER_ACCEPTED = "driverAccepted";
    final static String EDIT_PROFILE = "editProfile";
    final static String START_TRAVEL = "startTravel";
    final static String CANCEL_TRAVEL = "cancelTravel";
    Socket socket;
    Vibrator vibe;
    EventBus eventBus = EventBus.getDefault();
    public static Ringtone r;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    @Subscribe
    public void connectSocket(ConnectEvent event) {
        try {
            IO.Options options = new IO.Options();
            options.query = "token=" + event.token;
            socket = IO.socket(getString(R.string.server_address), options);
            socket.on(Socket.EVENT_CONNECT, args -> {
                eventBus.post(new ConnectResultEvent(ServerResponse.OK.getValue()));
                eventBus.post(new SocketConnectionEvent(Socket.EVENT_CONNECT));
            })
                    .on(Socket.EVENT_DISCONNECT, args -> eventBus.post(new SocketConnectionEvent(Socket.EVENT_DISCONNECT)))
                    .on(Socket.EVENT_CONNECTING, args -> eventBus.post(new SocketConnectionEvent(Socket.EVENT_CONNECTING)))
                    .on(Socket.EVENT_CONNECT_ERROR, args -> eventBus.post(new SocketConnectionEvent(Socket.EVENT_CONNECT_ERROR)))
                    .on(Socket.EVENT_CONNECT_TIMEOUT, args -> eventBus.post(new SocketConnectionEvent(Socket.EVENT_CONNECT_TIMEOUT)))
                    .on(Socket.EVENT_RECONNECT, args -> eventBus.post(new SocketConnectionEvent(Socket.EVENT_RECONNECT)))
                    .on(Socket.EVENT_RECONNECTING, args -> eventBus.post(new SocketConnectionEvent(Socket.EVENT_RECONNECTING)))
                    .on(Socket.EVENT_RECONNECT_ATTEMPT, args -> eventBus.post(new SocketConnectionEvent(Socket.EVENT_RECONNECT_ATTEMPT)))
                    .on(Socket.EVENT_RECONNECT_ERROR, args -> eventBus.post(new SocketConnectionEvent(Socket.EVENT_RECONNECT_ERROR)))
                    .on(Socket.EVENT_RECONNECT_FAILED, args -> eventBus.post(new SocketConnectionEvent(Socket.EVENT_RECONNECT_FAILED)))
                    .on("error", args -> {
                        try {
                            JSONObject obj = new JSONObject(args[0].toString());
                            eventBus.post(new ConnectResultEvent(ServerResponse.UNKNOWN_ERROR.getValue(), obj.getString("message")));
                        } catch (JSONException c) {
                            eventBus.post(new ConnectResultEvent(ServerResponse.UNKNOWN_ERROR.getValue(), args[0].toString()));
                        }
                    })
                    .on("requestReceived", args -> {
                try {
                    eventBus.post(new RequestReceivedEvent(args[0].toString(), (Integer) args[1], (Integer) args[2], Double.valueOf(args[3].toString())));
                    if(MainActivity.isInBackGround.get()){
                        sendNotification();
                    }
                } catch (Exception c) {
                    c.printStackTrace();
                }
            })
                    .on("riderAccepted", args -> {
                        try {
                            eventBus.post(new RiderAcceptedEvent(args));
                        } catch (Exception c) {
                            c.printStackTrace();
                        }
                    })
                    .on("cancelRequest", args -> eventBus.post(new CancelRequestEvent(args)))
                    .on("driverInfoChanged", args -> {
                        MyPreferenceManager SP = new MyPreferenceManager(getApplicationContext());
                        SP.putString("driver_user", args[0].toString());
                        CommonUtils.driver = new Gson().fromJson(args[0].toString(), Driver.class);
                        eventBus.postSticky(new ProfileInfoChangedEvent());
                    })
                    .on("cancelTravel", args -> eventBus.post(new ServiceCancelResultEvent(200)));
            socket.connect();
        } catch (Exception c) {
            Log.e("Connect Socket", c.getMessage());
        }

    }

    @Subscribe
    public void getStatus(GetStatusEvent event) {
        socket.emit("getStatus", (Ack) args -> eventBus.post(new GetStatusResultEvent(args)));
    }

    @Subscribe
    public void login(LoginEvent event) {
        new LoginRequest().execute(String.valueOf(event.userName), String.valueOf(event.versionNumber));
    }

    @Subscribe
    public void FinishTravel(final ServiceFinishEvent event) {
        socket.emit("finishedTaxi", event.cost, event.duration, event.distance, event.log, (Ack) args -> eventBus.post(new ServiceFinishResultEvent((int) args[0], (boolean) args[1], Float.parseFloat(args[2].toString()))));
    }

    @Subscribe
    public void PaymentRequested(final PaymentRequestEvent event) {
        socket.emit("requestPayment", (Ack) args -> eventBus.post(new PaymentRequestResultEvent((int) args[0])));
    }

    @Subscribe
    public void editProfile(final EditProfileInfoEvent event) {
        socket.emit(EDIT_PROFILE, event.userInfo, (Ack) args -> eventBus.post(new EditProfileInfoResultEvent((Integer) args[0])));
    }

    @Subscribe
    public void notificationPlayerId(NotificationPlayerId event) {
        socket.emit("notificationPlayerId", event.playerId);
    }

    @Subscribe
    public void acceptOrder(AcceptOrderEvent event) {
        socket.emit(DRIVER_ACCEPTED, event.travelId);
    }

    @Subscribe
    public void changeStatus(final ChangeStatusEvent event) {
        socket.emit("changeStatus", event.status.getValue(), (Ack) args -> eventBus.post(new ChangeStatusResultEvent((int) args[0])));
    }

    @Subscribe
    public void serviceInLocation(ServiceInLocationEvent event) {
        socket.emit("buzz");
    }

    @Subscribe
    public void startTaxi(ServiceStartEvent event) {
        socket.emit(START_TRAVEL);
    }

    @Subscribe
    public void callRequest(ServiceCallRequestEvent event) {
        socket.emit("callRequest", (Ack) args -> eventBus.post(new ServiceCallRequestResultEvent((int) args[0])));
    }

    @Subscribe
    public void cancelTaxi(ServiceCancelEvent event) {
        socket.emit(CANCEL_TRAVEL, (Ack) args -> eventBus.post(new ServiceCancelResultEvent((int) args[0])));
    }

    @Subscribe
    public void getTravels(final GetTravelsEvent event) {
        socket.emit("getTravels", (Ack) args -> eventBus.post(new GetTravelsResultEvent((int) args[0], args[1].toString())));
    }

    @Subscribe
    public void locationChanged(LocationChangedEvent event) {
        socket.emit("locationChanged", event.location.latitude, event.location.longitude);
    }

    @Subscribe
    public void chargeAccount(ChargeAccountEvent event) {
        socket.emit("chargeAccount", event.type, event.stripeToken, event.amount, (Ack) args -> eventBus.post(new ChargeAccountResultEvent(args)));
    }

    @Subscribe
    public void ChangeProfileImage(ChangeProfileImageEvent event) {
        File file = new File(event.path);
        byte[] data = new byte[(int) file.length()];
        try {
            int len = new FileInputStream(file).read(data);
            if (len > 0)
                socket.emit("changeProfileImage", data, (Ack) args -> eventBus.post(new ChangeProfileImageResultEvent((int) args[0], args[1].toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void HideTravel(final HideTravelEvent event) {
        socket.emit("hideTravel", event.travelId, (Ack) args -> eventBus.post(new HideTravelResultEvent(ServerResponse.OK.getValue())));
    }

    @Subscribe
    public void changeHeaderImage(ChangeHeaderImageEvent event) {
        File file = new File(event.path);
        byte[] data = new byte[(int) file.length()];
        try {
            int len = new FileInputStream(file).read(data);
            if (len > 0)
                socket.emit("changeHeaderImage", data, (Ack) args -> eventBus.post(new ChangeHeaderImageResultEvent((int) args[0], args[1].toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void getDriverStatistics(GetStatisticsEvent event) {
        socket.emit("getStats", event.queryTime.getValue(), (Ack) args -> eventBus.post(new GetStatisticsResultEvent((Integer) args[0], args[1] != null ? args[1].toString() : null, args[2] != null ? args[2].toString() : null)));
    }

    @Subscribe
    public void WriteComplaint(final WriteComplaintEvent event) {
        socket.emit("writeComplaint", event.travelId, event.subject, event.content, (Ack) args -> eventBus.post(new WriteComplaintResultEvent((int) args[0])));
    }

    @Subscribe
    public void sendTravelInfo(SendTravelInfoEvent event) {
        socket.emit("travelInfo", event.travel.getDistanceReal(), event.travel.getDurationReal(), event.travel.getCost());
    }

    @Subscribe
    public void getTransactions(GetTransactionsRequestEvent event) {
        socket.emit("getTransactions", (Ack) args -> eventBus.post(new GetTransactionsResultEvent(args)));
    }

    @Subscribe
    public void getRequests(GetRequestsRequestEvent event) {
        socket.emit("getRequests", (Ack) args -> eventBus.post(new GetRequestsResultEvent(args)));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        eventBus.post(new BackgroundServiceStartedEvent());
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class LoginRequest extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            try {
                URL url = new URL(getString(R.string.server_address) + "driver_login");
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");
                client.setDoOutput(true);
                client.setDoInput(true);
                DataOutputStream wr = new DataOutputStream(client.getOutputStream());

                HashMap<String, String> postDataParams = new HashMap<>();
                postDataParams.put("user_name", uri[0]);
                postDataParams.put("version", uri[1]);
                StringBuilder result = new StringBuilder();
                boolean first = true;
                for (Map.Entry<String, String> entry : postDataParams.entrySet()) {
                    if (first)
                        first = false;
                    else
                        result.append("&");
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
                wr.write(result.toString().getBytes());
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    sb.append(line);
                return sb.toString();
            } catch (Exception c) {
                c.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject obj = new JSONObject(result);
                int status = obj.getInt("status");
                if (status == 200)
                    eventBus.post(new LoginResultEvent(obj.getInt("status"), obj.getString("user"), obj.getString("token")));
                else
                    eventBus.post(new LoginResultEvent(status));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    void sendNotification(){
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(this , MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getPackageName() + "/"+R.raw.e);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("New Ride Request")
                .setContentText("You have a new travel request.")
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(resultPendingIntent);

        //r.play();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("1001", "QCAB_CHANNEL", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            notificationChannel.setSound(alarmSound, audioAttributes);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId("1001");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            mBuilder.setSound(alarmSound);
        }

        assert mNotificationManager != null;
        mNotificationManager.notify(new Random().nextInt()/* Request Code */, mBuilder.build());
    }
}