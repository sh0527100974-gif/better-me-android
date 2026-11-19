package com.betterme.app;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import android.content.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private final int ink=Color.rgb(30,48,40), muted=Color.rgb(119,136,126), green=Color.rgb(45,126,91),
        pale=Color.rgb(231,243,235), bg=Color.rgb(247,249,247), white=Color.WHITE, line=Color.rgb(229,235,230);
    private SharedPreferences prefs;
    private LinearLayout page, body, bottom;
    private TextView caloriesTxt, waterTxt, stepsTxt, weightTxt, mealsTxt, proteinTxt, carbsTxt, fatTxt;
    private ProgressBar calorieBar, waterBar, stepsBar;
    private int calories, water, steps, protein, carbs, fat, goalCalories=2000, waterGoal=8;
    private float weight;
    private String today;
    private int screen=0;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(bg); getWindow().setNavigationBarColor(white);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        prefs=getSharedPreferences("betterme_v2",0);
        today=new SimpleDateFormat("yyyyMMdd",Locale.US).format(new Date());
        if(!today.equals(prefs.getString("day",""))) {
            prefs.edit().putString("day",today).putInt("calories",0).putInt("water",0).putInt("steps",0)
                .putInt("protein",0).putInt("carbs",0).putInt("fat",0).putString("meals","").apply();
        }
        calories=prefs.getInt("calories",0); water=prefs.getInt("water",0); steps=prefs.getInt("steps",0);
        protein=prefs.getInt("protein",0); carbs=prefs.getInt("carbs",0); fat=prefs.getInt("fat",0);
        weight=prefs.getFloat("weight",78f);
        render();
    }
    private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    private GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    private LinearLayout col(){LinearLayout x=new LinearLayout(this);x.setOrientation(1);return x;}
    private LinearLayout row(){LinearLayout x=new LinearLayout(this);x.setOrientation(0);x.setGravity(Gravity.CENTER_VERTICAL);return x;}
    private LinearLayout.LayoutParams lp(int w,int h){return new LinearLayout.LayoutParams(w<0?w:dp(w),h<0?h:dp(h));}
    private void pad(View v,int a,int b,int c,int d){v.setPadding(dp(a),dp(b),dp(c),dp(d));}
    private TextView txt(String s,int size,int color,boolean bold){
        TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);
        t.setGravity(Gravity.RIGHT);t.setTextDirection(View.TEXT_DIRECTION_RTL);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;
    }
    private void space(LinearLayout p,int h){View v=new View(this);p.addView(v,lp(1,h));}
    private void card(LinearLayout parent,View child){
        child.setBackground(shape(white,20));child.setElevation(dp(1));pad(child,17,16,17,16);
        LinearLayout.LayoutParams p=lp(-1,-2);p.bottomMargin=dp(12);parent.addView(child,p);
    }
    private Button btn(String s,boolean primary){
        Button b=new Button(this);b.setText(s);b.setTextSize(13);b.setAllCaps(false);b.setTypeface(null,Typeface.BOLD);
        b.setTextColor(primary?white:green);b.setBackground(shape(primary?green:pale,14));return b;
    }
    private ProgressBar bar(int max){
        ProgressBar p=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);p.setMax(max);
        p.setProgressTintList(android.content.res.ColorStateList.valueOf(green));
        p.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(line));return p;
    }
    private void render(){
        page=col();page.setBackgroundColor(bg);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(false);scroll.setClipToPadding(false);
        body=col();pad(body,18,13,18,22);scroll.addView(body);
        page.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        buildBottom();page.addView(bottom,lp(-1,66));
        setContentView(page);showScreen();
    }
    private void buildBottom(){
        bottom=row();bottom.setBackgroundColor(white);bottom.setElevation(dp(12));bottom.setGravity(Gravity.CENTER);
        String[] names={"היום","התקדמות","היעדים"};String[] icons={"⌂","↗","◎"};
        for(int i=0;i<3;i++){
            final int index=i;LinearLayout item=col();item.setGravity(Gravity.CENTER);
            TextView ic=txt(icons[i],22,i==screen?green:muted,true);ic.setGravity(Gravity.CENTER);
            TextView label=txt(names[i],11,i==screen?green:muted,i==screen);label.setGravity(Gravity.CENTER);
            item.addView(ic);item.addView(label);item.setOnClickListener(v->{screen=index;render();});
            bottom.addView(item,new LinearLayout.LayoutParams(0,-1,1));
        }
    }
    private void showScreen(){body.removeAllViews();if(screen==0)home();else if(screen==1)progress();else goals();}
    private void header(String eyebrow,String title){
        TextView e=txt(eyebrow.toUpperCase(Locale.ROOT),11,green,true);body.addView(e);
        TextView t=txt(title,27,ink,true);LinearLayout.LayoutParams p=lp(-1,-2);p.topMargin=dp(3);p.bottomMargin=dp(16);body.addView(t,p);
    }
    private TextView heading(String s){
        TextView t=txt(s,17,ink,true);LinearLayout.LayoutParams p=lp(-1,-2);p.bottomMargin=dp(13);t.setLayoutParams(p);return t;
    }
    private void home(){
        String date=new SimpleDateFormat("EEEE, d בMMMM",new Locale("he")).format(new Date());
        LinearLayout top=row();LinearLayout titles=col();
        titles.addView(txt("BETTER ME  •  המסע שלך",11,green,true));
        titles.addView(txt("היי, בוא נתקדם 🌱",24,ink,true));
        top.addView(titles,new LinearLayout.LayoutParams(0,-2,1));
        TextView badge=txt("BM",14,green,true);badge.setGravity(Gravity.CENTER);badge.setBackground(shape(pale,50));top.addView(badge,lp(44,44));
        body.addView(top);space(body,8);body.addView(txt(date,12,muted,false));space(body,17);

        LinearLayout hero=col();hero.setBackground(shape(Color.rgb(31,72,54),24));pad(hero,19,19,19,19);
        TextView label=txt("הקלוריות שלך היום",14,Color.rgb(211,231,218),true);hero.addView(label);
        LinearLayout num=row();caloriesTxt=txt(String.valueOf(calories),42,white,true);num.addView(caloriesTxt,new LinearLayout.LayoutParams(0,-2,1));
        num.addView(txt("קק״ל",14,Color.rgb(211,231,218),false));hero.addView(num);
        LinearLayout remain=row();remain.addView(txt("מתוך "+goalCalories+" קק״ל",12,Color.rgb(211,231,218),false),new LinearLayout.LayoutParams(0,-2,1));
        remain.addView(txt((goalCalories-calories>=0?"נותרו ":"חריגה ")+Math.abs(goalCalories-calories),12,white,true));hero.addView(remain);
        calorieBar=bar(goalCalories);calorieBar.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.rgb(147,207,166)));
        LinearLayout.LayoutParams cp=lp(-1,9);cp.topMargin=dp(12);hero.addView(calorieBar,cp);
        Button add=btn("＋  הוספת ארוחה",false);LinearLayout.LayoutParams ap=lp(-1,46);ap.topMargin=dp(16);hero.addView(add,ap);add.setOnClickListener(v->mealDialog());
        body.addView(hero);space(body,17);

        body.addView(heading("היום שלך במספרים"));
        LinearLayout stats=row();stats.addView(statCard("💧","מים",water+" / "+waterGoal,"כוסות"),new LinearLayout.LayoutParams(0,dp(111),1));
        View gap=new View(this);stats.addView(gap,lp(10,1));
        stats.addView(statCard("👟","צעדים",String.format(Locale.US,"%,d",steps),"צעדים"),new LinearLayout.LayoutParams(0,dp(111),1));
        body.addView(stats);space(body,10);
        LinearLayout nutrition=col();nutrition.addView(heading("המאקרו שלך"));
        LinearLayout macros=row();
        macros.addView(macro("חלבון",protein,"גרם",Color.rgb(71,132,186)),new LinearLayout.LayoutParams(0,-2,1));
        macros.addView(macro("פחמימות",carbs,"גרם",Color.rgb(204,151,67)),new LinearLayout.LayoutParams(0,-2,1));
        macros.addView(macro("שומן",fat,"גרם",Color.rgb(164,112,157)),new LinearLayout.LayoutParams(0,-2,1));
        nutrition.addView(macros);card(body,nutrition);

        LinearLayout waterCard=col();waterCard.addView(heading("שתיית מים"));
        LinearLayout wr=row();waterTxt=txt(water+" מתוך "+waterGoal+" כוסות",14,ink,true);wr.addView(waterTxt,new LinearLayout.LayoutParams(0,-2,1));
        Button waterAdd=btn("+ כוס מים",false);wr.addView(waterAdd,lp(-2,40));waterAdd.setOnClickListener(v->{water++;save();render();});waterCard.addView(wr);
        waterBar=bar(waterGoal);waterBar.setProgress(water);LinearLayout.LayoutParams wp=lp(-1,7);wp.topMargin=dp(13);waterCard.addView(waterBar,wp);card(body,waterCard);

        LinearLayout meals=col();meals.addView(heading("הארוחות שלי"));
        String savedMeals=prefs.getString("meals","");\n        mealsTxt=txt(savedMeals.isEmpty()?"עדיין לא הוספת ארוחות":savedMeals,13,muted,false);meals.addView(mealsTxt);
        Button mealBtn=btn("＋  הוספת מה שאכלתי",false);LinearLayout.LayoutParams mp=lp(-1,42);mp.topMargin=dp(12);meals.addView(mealBtn,mp);mealBtn.setOnClickListener(v->mealDialog());card(body,meals);
    }
    private View statCard(String icon,String title,String value,String unit){
        LinearLayout c=col();c.setBackground(shape(white,18));c.setElevation(dp(1));pad(c,14,13,14,13);
        c.addView(txt(icon+"  "+title,12,muted,true));space(c,8);c.addView(txt(value,23,ink,true));c.addView(txt(unit,11,muted,false));return c;
    }
    private View macro(String name,int value,String unit,int color){
        LinearLayout c=col();c.setGravity(Gravity.CENTER);c.setBackground(shape(bg,14));pad(c,8,13,8,13);
        c.addView(txt(String.valueOf(value),22,color,true));c.addView(txt(unit,10,muted,false));c.addView(txt(name,12,ink,true));return c;
    }
    private void progress(){
        header("YOUR PROGRESS","ההתקדמות שלי");
        LinearLayout w=col();w.addView(heading("המשקל הנוכחי"));
        LinearLayout r=row();weightTxt=txt(String.format(Locale.US,"%.1f ק״ג",weight),30,ink,true);r.addView(weightTxt,new LinearLayout.LayoutParams(0,-2,1));
        Button edit=btn("עדכון משקל",false);r.addView(edit,lp(-2,42));edit.setOnClickListener(v->weightDialog());w.addView(r);
        w.addView(txt("כאן נרכז את המעקב שלך לאורך זמן.",12,muted,false));card(body,w);
        LinearLayout activity=col();activity.addView(heading("פעילות היום"));
        stepsTxt=txt(String.format(Locale.US,"%,d צעדים",steps),25,ink,true);activity.addView(stepsTxt);
        stepsBar=bar(8000);stepsBar.setProgress(Math.min(steps,8000));LinearLayout.LayoutParams sp=lp(-1,8);sp.topMargin=dp(12);activity.addView(stepsBar,sp);
        Button set=btn("עדכון צעדים",false);LinearLayout.LayoutParams st=lp(-1,42);st.topMargin=dp(13);activity.addView(set,st);
        set.setOnClickListener(v->numberDialog("כמה צעדים עשית היום?",steps));card(body,activity);
        LinearLayout workouts=col();workouts.addView(heading("האימונים שלי"));
        String history=prefs.getString("workouts","");workouts.addView(txt(history.isEmpty()?"עדיין לא נרשמו אימונים":history,13,muted,false));
        Button workout=btn("＋  רישום אימון",false);LinearLayout.LayoutParams ap=lp(-1,42);ap.topMargin=dp(12);workouts.addView(workout,ap);workout.setOnClickListener(v->workoutDialog());card(body,workouts);
    }
    private void goals(){
        header("YOUR DAILY PLAN","היעדים שלי");
        LinearLayout c=col();c.addView(heading("יעד קלוריות יומי"));
        c.addView(txt(goalCalories+" קק״ל",25,ink,true));
        c.addView(txt("היעד הנוכחי שלך. אפשר להתאים אותו בכל עת.",12,muted,false));
        Button caloriesEdit=btn("שינוי יעד קלוריות",false);LinearLayout.LayoutParams p=lp(-1,44);p.topMargin=dp(12);c.addView(caloriesEdit,p);
        caloriesEdit.setOnClickListener(v->numberDialog("יעד קלוריות יומי",goalCalories));
        card(body,c);
        LinearLayout waterCard=col();waterCard.addView(heading("יעד שתיית מים"));waterCard.addView(txt(waterGoal+" כוסות ביום",23,ink,true));
        Button waterEdit=btn("שינוי יעד מים",false);LinearLayout.LayoutParams wp=lp(-1,44);wp.topMargin=dp(12);waterCard.addView(waterEdit,wp);
        waterEdit.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("יעד כוסות מים").setItems(new String[]{"6 כוסות","8 כוסות","10 כוסות","12 כוסות"},(d,n)->{waterGoal=new int[]{6,8,10,12}[n];prefs.edit().putInt("waterGoal",waterGoal).apply();render();}).show());
        card(body,waterCard);
        LinearLayout note=col();note.addView(heading("מתקדמים בקצב שלך"));
        note.addView(txt("לא צריך להיות מושלם. חשוב לבנות הרגלים קטנים שאפשר להתמיד בהם.",14,muted,false));card(body,note);
    }
    private void save(){
        prefs.edit().putInt("calories",calories).putInt("water",water).putInt("steps",steps)
            .putInt("protein",protein).putInt("carbs",carbs).putInt("fat",fat).putFloat("weight",weight)
            .putInt("waterGoal",waterGoal).putInt("goalCalories",goalCalories).apply();
    }
    private EditText field(String hint,boolean decimal){
        EditText e=new EditText(this);e.setSingleLine(true);e.setTextSize(16);e.setHint(hint);
        e.setInputType(decimal?8194:2);return e;
    }
    private void mealDialog(){
        LinearLayout box=col();pad(box,8,4,8,4);
        EditText name=field("מה אכלת? (למשל: חביתה וסלט)",false);
        EditText kcal=field("קלוריות",false),pr=field("חלבון בגרם",false),ca=field("פחמימות בגרם",false),fa=field("שומן בגרם",false);
        box.addView(name);box.addView(kcal);box.addView(pr);box.addView(ca);box.addView(fa);
        new AlertDialog.Builder(this).setTitle("הוספת ארוחה").setView(box).setNegativeButton("ביטול",null)
            .setPositiveButton("שמירה",(d,w)->{try{
                int k=Math.max(0,Integer.parseInt(kcal.getText().toString()));
                calories+=k;protein+=parse(pr);carbs+=parse(ca);fat+=parse(fa);
                String old=prefs.getString("meals","");String entry=(name.getText().length()==0?"ארוחה":name.getText().toString())+"  ·  "+k+" קק״ל";
                prefs.edit().putString("meals",old.isEmpty()?entry:old+"\n"+entry).apply();save();render();
            }catch(Exception e){Toast.makeText(this,"צריך להזין מספר קלוריות תקין",Toast.LENGTH_SHORT).show();}}).show();
    }
    private int parse(EditText e){try{return Math.max(0,Integer.parseInt(e.getText().toString()));}catch(Exception x){return 0;}}
    private void numberDialog(String title,int current){
        EditText e=field("הזן מספר",false);e.setText(String.valueOf(current));
        new AlertDialog.Builder(this).setTitle(title).setView(e).setNegativeButton("ביטול",null)
            .setPositiveButton("שמירה",(d,w)->{try{int n=Math.max(0,Integer.parseInt(e.getText().toString()));
                if(title.contains("צעדים"))steps=n;else if(title.contains("קלוריות"))goalCalories=n;
                save();render();}catch(Exception x){}}).show();
    }
    private void weightDialog(){
        EditText e=field("משקל בק״ג",true);e.setText(String.format(Locale.US,"%.1f",weight));
        new AlertDialog.Builder(this).setTitle("עדכון משקל").setView(e).setNegativeButton("ביטול",null)
            .setPositiveButton("שמירה",(d,w)->{try{weight=Float.parseFloat(e.getText().toString());save();render();}catch(Exception x){}}).show();
    }
    private void workoutDialog(){
        LinearLayout box=col();EditText n=field("סוג האימון",false),m=field("משך בדקות",false);box.addView(n);box.addView(m);
        new AlertDialog.Builder(this).setTitle("רישום אימון").setView(box).setNegativeButton("ביטול",null)
            .setPositiveButton("שמירה",(d,w)->{String old=prefs.getString("workouts","");String entry=n.getText()+" · "+m.getText()+" דקות";
                prefs.edit().putString("workouts",old.isEmpty()?entry:old+"\n"+entry).apply();render();}).show();
    }
}