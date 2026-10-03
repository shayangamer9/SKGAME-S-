package com.shadowplay.launcher;

import android.app.Activity;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.json.*;

public class MainActivity extends Activity {
    private static final String PREFS="shadowplay_prefs";
    private static final String DEFAULT_GAME_PACKAGE="com.shadowplay.game";
    private LinearLayout content;
    private TextView status;
    private SharedPreferences prefs;
    private final ArrayList<Server> servers=new ArrayList<>();
    private int selected=0;
    private final int BG=Color.rgb(7,5,13), PANEL=Color.rgb(21,16,31), PANEL2=Color.rgb(33,21,50), PURPLE=Color.rgb(168,85,247), CYAN=Color.rgb(34,211,238), TEXT=Color.rgb(248,247,255), MUTED=Color.rgb(169,161,184), GREEN=Color.rgb(70,230,160), RED=Color.rgb(255,102,122);

    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);prefs=getSharedPreferences(PREFS,0);buildShell();loadServers();}
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    private TextView tv(String s,float size,int color,boolean bold){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setTypeface(bold?Typeface.DEFAULT_BOLD:Typeface.DEFAULT);v.setPadding(dp(3),dp(2),dp(3),dp(2));return v;}
    private GradientDrawable box(int color,float radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp((int)radius));return g;}
    private Button btn(String s){Button b=new Button(this);b.setText(s);b.setTextColor(TEXT);b.setTextSize(13);b.setAllCaps(false);b.setTypeface(Typeface.DEFAULT_BOLD);b.setBackground(box(PURPLE,14));b.setPadding(dp(12),0,dp(12),0);return b;}

    private void buildShell(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(BG);
        LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);header.setPadding(dp(18),dp(10),dp(18),dp(6));
        TextView brand=tv("✦ SHADOWPLAY  RP",20,TEXT,true);header.addView(brand,new LinearLayout.LayoutParams(0,dp(48),1));
        status=tv("● OFFLINE",11,MUTED,true);status.setGravity(Gravity.CENTER);header.addView(status,new LinearLayout.LayoutParams(dp(95),dp(40)));root.addView(header);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(dp(14),0,dp(14),dp(10));scroll.addView(content);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout nav=new LinearLayout(this);nav.setPadding(dp(8),dp(8),dp(8),dp(10));nav.setBackgroundColor(PANEL);
        String[] names={"SERVERS","NEWS","UPDATES","SETTINGS"};for(String name:names){Button b=btn(name);b.setTextSize(10);b.setBackground(box(PANEL2,12));nav.addView(b,new LinearLayout.LayoutParams(0,dp(48),1));if(name.equals("SERVERS"))b.setOnClickListener(v->showServers());if(name.equals("NEWS"))b.setOnClickListener(v->showNews());if(name.equals("UPDATES"))b.setOnClickListener(v->showUpdates());if(name.equals("SETTINGS"))b.setOnClickListener(v->showSettings());}root.addView(nav,new LinearLayout.LayoutParams(-1,dp(66)));setContentView(root);
    }
    private void clear(String h,String sub){content.removeAllViews();TextView t=tv(h,20,TEXT,true);content.addView(t);content.addView(tv(sub,12,MUTED,false));Space sp=new Space(this);content.addView(sp,new LinearLayout.LayoutParams(1,dp(10)));}
    private LinearLayout card(){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(16),dp(14),dp(16),dp(14));c.setBackground(box(PANEL,18));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,0,0,dp(12));c.setLayoutParams(p);return c;}

    private void showServers(){
        clear("Servers","Choose a server. The selected server is saved on this device.");
        if(servers.isEmpty()){content.addView(tv("No server configuration available.",14,TEXT,false));return;}
        for(int i=0;i<servers.size();i++){
            final int idx=i;Server s=servers.get(i);LinearLayout c=card();LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout info=new LinearLayout(this);info.setOrientation(LinearLayout.VERTICAL);info.addView(tv(s.name,17,TEXT,true));info.addView(tv(s.host+":"+s.port,12,MUTED,false));info.addView(tv(s.online+" / "+s.max+" online  •  "+s.status,12,s.isOnline()?GREEN:RED,true));row.addView(info,new LinearLayout.LayoutParams(0,-2,1));
            Button select=btn(i==selected?"SELECTED":"SELECT");select.setBackground(box(i==selected?PANEL2:PURPLE,12));select.setOnClickListener(v->{selected=idx;prefs.edit().putString("selected_server",s.key).apply();showServers();});row.addView(select,new LinearLayout.LayoutParams(dp(105),dp(46)));c.addView(row);
            Button play=btn("PLAY");play.setOnClickListener(v->launchGame(s));c.addView(play,new LinearLayout.LayoutParams(-1,dp(46)));content.addView(c);
        }
        Button refresh=btn("↻  REFRESH SERVER LIST");refresh.setOnClickListener(v->loadServers());content.addView(refresh,new LinearLayout.LayoutParams(-1,dp(48)));updateNetworkStatus();
    }
    private void showNews(){
        clear("News","Shadowplay announcements.");
        try{JSONArray a=new JSONArray(readAsset("news.json"));for(int i=0;i<a.length();i++){JSONObject o=a.getJSONObject(i);LinearLayout c=card();c.addView(tv(o.optString("title","News"),16,TEXT,true));c.addView(tv(o.optString("date",""),11,CYAN,true));c.addView(tv(o.optString("body",""),13,MUTED,false));content.addView(c);}}catch(Exception e){content.addView(tv("News configuration is unavailable.",14,TEXT,false));}
    }
    private void showUpdates(){
        clear("Updates","Update support is ready for your own HTTPS manifest.");LinearLayout c=card();c.addView(tv("Launcher 1.0.0",17,TEXT,true));c.addView(tv("Remote manifest format: version, versionCode, apkUrl, notes, sha256.",13,MUTED,false));Button b=btn("CHECK FOR UPDATE");b.setOnClickListener(v->toast("No update URL configured yet."));c.addView(b);content.addView(c);
        LinearLayout c2=card();c2.addView(tv("Game files",16,TEXT,true));c2.addView(tv("The launcher can be extended with a patch/download worker once the final Shadowplay client package and CDN are ready.",13,MUTED,false));content.addView(c2);
    }
    private void showSettings(){
        clear("Settings","Configure the final Shadowplay client package and server feed.");
        LinearLayout c=card();c.addView(tv("Game package",15,TEXT,true));EditText pkg=new EditText(this);pkg.setText(prefs.getString("game_package",DEFAULT_GAME_PACKAGE));pkg.setTextColor(TEXT);pkg.setHintTextColor(MUTED);pkg.setHint("com.shadowplay.game");pkg.setSingleLine(true);c.addView(pkg);Button save=btn("SAVE GAME PACKAGE");save.setOnClickListener(v->{prefs.edit().putString("game_package",pkg.getText().toString().trim()).apply();toast("Saved.");});c.addView(save);content.addView(c);
        LinearLayout c2=card();c2.addView(tv("Server JSON URL",15,TEXT,true));EditText url=new EditText(this);url.setText(prefs.getString("server_url",""));url.setTextColor(TEXT);url.setHintTextColor(MUTED);url.setHint("https://your-domain/servers.json");url.setSingleLine(true);c2.addView(url);Button save2=btn("SAVE SERVER URL");save2.setOnClickListener(v->{prefs.edit().putString("server_url",url.getText().toString().trim()).apply();toast("Saved. Refresh Servers to load it.");});c2.addView(save2);content.addView(c2);
        LinearLayout c3=card();c3.addView(tv("Selected server",15,TEXT,true));c3.addView(tv(servers.isEmpty()?"None":servers.get(selected).name,13,MUTED,false));Button reset=btn("RESET SETTINGS");reset.setOnClickListener(v->{prefs.edit().clear().apply();toast("Settings reset.");showSettings();});c3.addView(reset);content.addView(c3);
    }
    private void loadServers(){new Thread(()->{String raw=null;String url=prefs.getString("server_url","");if(!url.isEmpty())raw=http(url);if(raw==null)raw=readAsset("servers.json");String data=raw;runOnUiThread(()->parseServers(data));}).start();}
    private void parseServers(String raw){try{JSONObject r=new JSONObject(raw);JSONArray a=r.optJSONArray("servers");if(a==null)throw new Exception();servers.clear();for(int i=0;i<a.length();i++){JSONObject o=a.getJSONObject(i);Server s=new Server(o.optString("key","server"+i),o.optString("name","Server"),o.optString("host","127.0.0.1"),o.optInt("port",7777),o.optInt("online",0),o.optInt("max",0),o.optString("status","online"));s.color=o.optString("color","#A855F7");s.x2=o.optBoolean("x2",false);servers.add(s);}String key=prefs.getString("selected_server",r.optString("defaultServer",""));selected=0;for(int i=0;i<servers.size();i++)if(servers.get(i).key.equals(key)){selected=i;break;}showServers();}catch(Exception e){servers.clear();showServers();toast("Invalid server JSON. Local list is unavailable.");}}
    private String readAsset(String name){try(InputStream in=getAssets().open(name);ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] b=new byte[4096];int n;while((n=in.read(b))>0)out.write(b,0,n);return out.toString(StandardCharsets.UTF_8.name());}catch(Exception e){return "{}";}}
    private String http(String u){HttpURLConnection c=null;try{URL url=new URL(u);c=(HttpURLConnection)url.openConnection();c.setConnectTimeout(7000);c.setReadTimeout(7000);c.setRequestMethod("GET");if(c.getResponseCode()<200||c.getResponseCode()>=300)return null;try(InputStream in=c.getInputStream();ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] b=new byte[4096];int n;while((n=in.read(b))>0)out.write(b,0,n);return out.toString(StandardCharsets.UTF_8.name());}}catch(Exception e){return null;}finally{if(c!=null)c.disconnect();}}
    private void launchGame(Server s){String pkg=prefs.getString("game_package",DEFAULT_GAME_PACKAGE).trim();if(pkg.isEmpty()){toast("Set the game package in Settings.");return;}try{Intent i=getPackageManager().getLaunchIntentForPackage(pkg);if(i==null){toast("Shadowplay game is not installed: "+pkg);return;}i.putExtra("shadowplay_server_key",s.key);i.putExtra("shadowplay_server_name",s.name);i.putExtra("shadowplay_server_host",s.host);i.putExtra("shadowplay_server_port",s.port);i.putExtra("shadowplay_server_color",s.color);i.putExtra("shadowplay_server_x2",s.x2);i.putExtra("shadowplay_launcher_version","1.0.0");i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i);}catch(Exception e){toast("Could not launch Shadowplay client.");}}
    private void updateNetworkStatus(){ConnectivityManager cm=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);boolean ok=false;if(cm!=null){NetworkCapabilities n=cm.getNetworkCapabilities(cm.getActiveNetwork());ok=n!=null&&n.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);}status.setText(ok?"● ONLINE":"● OFFLINE");status.setTextColor(ok?GREEN:RED);}
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
    private static class Server{String key,name,host,status,color;int port,online,max;boolean x2;Server(String k,String n,String h,int p,int o,int m,String st){key=k;name=n;host=h;port=p;online=o;max=m;status=st;color="#A855F7";}boolean isOnline(){return !"offline".equalsIgnoreCase(status);}}
}
