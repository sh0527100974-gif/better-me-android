package com.betterme.app;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import android.text.InputType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int INK=Color.rgb(35,53,47), MUTED=Color.rgb(123,139,131),
        GREEN=Color.rgb(53,139,105), PALE=Color.rgb(232,244,237),
        BG=Color.rgb(246,249,247), WHITE=Color.WHITE, LINE=Color.rgb(231,237,233);
    private android.content.SharedPreferences prefs;
    private LinearLayout root, content;
    private TextView caloriesValue, waterValue, weightValue, stepsValue, mealList;
    private ProgressBar calorieBar, waterBar, stepBar;
    private int calories, water, steps, goalCalories=2000, waterGoal=8;
    private float weight;
    private String dayKey;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs=getSharedPreferences("betterme_v2",0);
        dayKey=new SimpleDateFormat("yyyyMMdd",Locale.US).format(new Date());
        String savedDay=prefs.getString("day","");
        if(!dayKey.equals(savedDay)){
            prefs.edit().putString("day",dayKey).putInt("calories",0).putInt("water",0)
                .putInt("steps",0).putString("meals","").apply();
        }
        calories=prefs.getInt("calories",0);
        water=prefs.getInt("water",0);
        steps=prefs.getInt("steps",0);
        weight=prefs.getFloat("weight",78f);
        buildScreen();
        refresh();
    }

    private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
    private GradientDrawable shape(int color,int radius){
        GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }
    private TextView text(String s,int size,int color,boolean bold){
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        t.setGravity(Gravity.RIGHT); t.setTextDirection(View.TEXT_DIRECTION_RTL);
        return t;
    }
    private LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
    private LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(0);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    private void pad(View v,int a,int b,int c,int d){v.setPadding(dp(a),dp(b),dp(c),dp(d));}
    private void gap(LinearLayout l,int h){View v=new View(this);l.addView(v,new LinearLayout.LayoutParams(1,dp(h)));}
    private LinearLayout.LayoutParams lp(int w,int h){return new LinearLayout.LayoutParams(w<0?w:dp(w),h<0?h:dp(h));}
    private void card(LinearLayout parent,View child){
        LinearLayout.LayoutParams p=lp(-1,-2);p.bottomMargin=dp(13);
        child.setBackground(shape(WHITE,20));child.setElevation(dp(2));pad(child,17,16,17,16);parent.addView(child,p);
    }
    private void buildScreen(){
        root=column();root.setBackgroundColor(BG);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
        content=column();pad(content,18,18,18,24);scroll.addView(content);
        LinearLayout header=row();
        LinearLayout titleBox=column();
        titleBox.addView(text("BETTER ME",13,GREEN,true));
        TextView hello=text("בוא נתקדם היום 🌿",25,INK,true);titleBox.addView(hello);
        header.addView(titleBox,new LinearLayout.LayoutParams(0,-2,1));
        TextView avatar=text("ב",20,GREEN,true);avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(shape(PALE,30));header.addView(avatar,lp(46,46));
        content.addView(header);gap(content,20);

        LinearLayout dateRow=row();
        dateRow.addView(text("המעקב היומי שלך",16,INK,true),new LinearLayout.LayoutParams(0,-2,1));
        dateRow.addView(text(new SimpleDateFormat("EEEE, d בMMMM",new Locale("he")).format(new Date()),12,MUTED,false));
        content.addView(dateRow);gap(content,12);

        LinearLayout main=column();main.setBackground(shape(Color.rgb(31,76,59),22));pad(main,19,19,19,19);
        TextView cap=text("מאזן קלורי יומי",14,Color.rgb(211,232,219),true);main.addView(cap);
        LinearLayout kcalRow=row();
        caloriesValue=text("0",36,WHITE,true);kcalRow.addView(caloriesValue,new LinearLayout.LayoutParams(0,-2,1));
        TextView unit=text("קלוריות",13,Color.rgb(211,232,219),false);kcalRow.addView(unit);
        main.addView(kcalRow);
        TextView target=text("מתוך "+goalCalories+" קלוריות ליום",12,Color.rgb(211,232,219),false);main.addView(target);
        calorieBar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        calorieBar.setMax(goalCalories);calorieBar.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.rgb(158,211,176)));
        calorieBar.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(74,111,91)));
        LinearLayout.LayoutParams bp=lp(-1,8);bp.topMargin=dp(13);main.addView(calorieBar,bp);
        LinearLayout actions=row();actions.setGravity(Gravity.RIGHT);gap(main,14);
        Button addMeal=button("＋ הוסף ארוחה",WHITE,GREEN);actions.addView(addMeal,lp(-2,42));
        addMeal.setOnClickListener(v->mealDialog());
        main.addView(actions);card(content,main);

        LinearLayout waterCard=column();waterCard.addView(sectionTitle("שתיית מים","💧"));
        LinearLayout wr=row();waterValue=text("0 / 8 כוסות",18,INK,true);wr.addView(waterValue,new LinearLayout.LayoutParams(0,-2,1));
        Button addWater=button("+ כוס מים",PALE,GREEN);wr.addView(addWater,lp(-2,40));addWater.setOnClickListener(v->{water++;save();refresh();});
        waterCard.addView(wr);
        waterBar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);waterBar.setMax(waterGoal);waterBar.setProgressTintList(android.content.res.ColorStateList.valueOf(GREEN));
        waterBar.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(LINE));
        LinearLayout.LayoutParams wp=lp(-1,7);wp.topMargin=dp(12);waterCard.addView(waterBar,wp);card(content,waterCard);

        LinearLayout macro=column();macro.addView(sectionTitle("ההרכב התזונתי","🥗"));
        LinearLayout mr=row();mr.addView(macroItem("חלבון","0 גרם",Color.rgb(72,133,190)),new LinearLayout.LayoutParams(0,-2,1));
        mr.addView(macroItem("פחמימות","0 גרם",Color.rgb(221,163,77)),new LinearLayout.LayoutParams(0,-2,1));
        mr.addView(macroItem("שומן","0 גרם",Color.rgb(177,118,164)),new LinearLayout.LayoutParams(0,-2,1));
        macro.addView(mr);card(content,macro);

        LinearLayout activity=column();activity.addView(sectionTitle("תנועה ופעילות","🏃"));
        LinearLayout sr=row();stepsValue=text("0 צעדים",17,INK,true);sr.addView(stepsValue,new LinearLayout.LayoutParams(0,-2,1));
        Button setSteps=button("עדכון צעדים",PALE,GREEN);sr.addView(setSteps,lp(-2,38));setSteps.setOnClickListener(v->numberDialog("כמה צעדים עשית היום?","steps",steps));
        activity.addView(sr);
        stepBar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);stepBar.setMax(8000);stepBar.setProgressTintList(android.content.res.ColorStateList.valueOf(GREEN));stepBar.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(LINE));
        LinearLayout.LayoutParams sp=lp(-1,7);sp.topMargin=dp(10);activity.addView(stepBar,sp);
        gap(activity,12);
        Button workout=button("＋ הוסף אימון",PALE,GREEN);activity.addView(workout,lp(-1,42));workout.setOnClickListener(v->workoutDialog());card(content,activity);

        LinearLayout weigh=column();weigh.addView(sectionTitle("המשקל שלי","⚖️"));
        LinearLayout weightRow=row();weightValue=text(String.format(Locale.US,"%.1f ק״ג",weight),22,INK,true);
        weightRow.addView(weightValue,new LinearLayout.LayoutParams(0,-2,1));
        Button updateWeight=button("עדכון משקל",PALE,GREEN);weightRow.addView(updateWeight,lp(-2,40));updateWeight.setOnClickListener(v->weightDialog());weigh.addView(weightRow);
        weigh.addView(text("התמדה קטנה בכל יום יוצרת שינוי גדול.",12,MUTED,false));gap(weigh,8);
        card(content,weigh);

        LinearLayout meals=column();meals.addView(sectionTitle("מה אכלתי היום","🍽️"));
        mealList=text("עדיין לא הוספת ארוחות",13,MUTED,false);meals.addView(mealList);card(content,meals);

        TextView footer=text("כל יום הוא הזדמנות להתחלה טובה 💚",13,MUTED,true);footer.setGravity(Gravity.CENTER);content.addView(footer);
        setContentView(scroll);
    }

    private TextView sectionTitle(String title,String emoji){
        TextView t=text(emoji+"   "+title,16,INK,true);LinearLayout.LayoutParams p=lp(-1,-2);p.bottomMargin=dp(13);t.setLayoutParams(p);return t;
    }
    private View macroItem(String name,String value,int color){
        LinearLayout box=column();box.setGravity(Gravity.CENTER);box.setBackground(shape(BG,13));pad(box,5,12,5,12);
        TextView v=text(value,15,color,true);v.setGravity(Gravity.CENTER);box.addView(v);
        TextView n=text(name,11,MUTED,false);n.setGravity(Gravity.CENTER);box.addView(n);return box;
    }
    private Button button(String label,int bg,int fg){
        Button b=new Button(this);b.setText(label);b.setTextSize(12);b.setTextColor(fg);b.setAllCaps(false);
        b.setBackground(shape(bg,13));b.setPadding(dp(12),0,dp(12),0);return b;
    }
    private void refresh(){
        if(caloriesValue==null)return;
        caloriesValue.setText(String.valueOf(calories));
        calorieBar.setProgress(Math.min(calories,goalCalories));
        waterValue.setText(water+" / "+waterGoal+" כוסות");waterBar.setProgress(Math.min(water,waterGoal));
        stepsValue.setText(String.format(Locale.US,"%,d צעדים",steps));stepBar.setProgress(Math.min(steps,8000));
        weightValue.setText(String.format(Locale.US,"%.1f ק״ג",weight));
        String meals=prefs.getString("meals","");
        mealList.setText(meals.isEmpty()?"עדיין לא הוספת ארוחות":meals);
    }
    private void save(){
        prefs.edit().putInt("calories",calories).putInt("water",water).putInt("steps",steps).putFloat("weight",weight).apply();
    }
    private EditText edit(String hint,boolean decimal){
        EditText e=new EditText(this);e.setSingleLine(true);e.setHint(hint);
        e.setInputType(decimal?8194:2);e.setTextSize(16);return e;
    }
    private void mealDialog(){
        LinearLayout box=column();EditText name=edit("מה אכלת?",false);EditText kcal=edit("קלוריות",false);
        EditText protein=edit("חלבון (גרם)",false);EditText carbs=edit("פחמימות (גרם)",false);EditText fat=edit("שומן (גרם)",false);
        box.addView(name);box.addView(kcal);box.addView(protein);box.addView(carbs);box.addView(fat);
        new AlertDialog.Builder(this).setTitle("הוספת ארוחה").setView(box)
            .setNegativeButton("ביטול",null).setPositiveButton("הוסף",(d,w)->{
                try{
                    int c=Integer.parseInt(kcal.getText().toString());
                    calories+=Math.max(0,c);save();
                    String old=prefs.getString("meals","");
                    String entry="• "+name.getText().toString()+" — "+c+" קל׳";
                    prefs.edit().putString("meals",old.isEmpty()?entry:old+"\n"+entry).apply();
                    refresh();
                }catch(Exception ex){Toast.makeText(this,"נא להזין מספר קלוריות תקין",Toast.LENGTH_SHORT).show();}
            }).show();
    }
    private void numberDialog(String title,String key,int current){
        EditText e=edit("הזן מספר",false);e.setText(String.valueOf(current));
        new AlertDialog.Builder(this).setTitle(title).setView(e).setNegativeButton("ביטול",null)
            .setPositiveButton("שמירה",(d,w)->{try{int n=Math.max(0,Integer.parseInt(e.getText().toString()));
                if(key.equals("steps"))steps=n;save();refresh();}catch(Exception ex){} }).show();
    }
    private void weightDialog(){
        EditText e=edit("משקל בק״ג",true);e.setText(String.format(Locale.US,"%.1f",weight));
        new AlertDialog.Builder(this).setTitle("עדכון משקל").setView(e).setNegativeButton("ביטול",null)
            .setPositiveButton("שמירה",(d,w)->{try{weight=Float.parseFloat(e.getText().toString());save();refresh();}catch(Exception ex){} }).show();
    }
    private void workoutDialog(){
        EditText name=edit("סוג האימון",false);EditText mins=edit("משך בדקות",false);
        LinearLayout box=column();box.addView(name);box.addView(mins);
        new AlertDialog.Builder(this).setTitle("רישום אימון").setView(box).setNegativeButton("ביטול",null)
            .setPositiveButton("שמירה",(d,w)->{String s=prefs.getString("workouts","");String item="• "+name.getText()+" — "+mins.getText()+" דקות";
                prefs.edit().putString("workouts",s.isEmpty()?item:s+"\n"+item).apply();
                Toast.makeText(this,"האימון נשמר 💪",Toast.LENGTH_SHORT).show();}).show();
    }
}
