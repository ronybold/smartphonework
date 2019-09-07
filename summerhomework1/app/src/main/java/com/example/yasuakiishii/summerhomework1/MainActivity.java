package com.example.yasuakiishii.summerhomework1;


import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.DenseInstance;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSource;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

public class MainActivity extends Activity implements Runnable, SensorEventListener {
    SensorManager sm;
    TextView tv;
    TextView tv2, tv3;
    Handler h;
    float gx, gy, gz;
    double ca;
    int flag = 0, counter = 0;
    ConverterUtils.DataSource  source;
    Instances instances;
    Classifier classifier;
    Evaluation eval;
    Attribute acceleration;
    Instance instance;
    //どの計測を行なっているのかと、何回計測を行なっているのかを
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* LinearLayout ll = new LinearLayout(this);
        setContentView(ll);*/

        tv = (TextView) (findViewById(R.id.sensor));
        //ll.addView(tv);

        h = new Handler();
        h.postDelayed(this, 500);
        /*try {
            //出力先を作成する
            FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory().getPath()+"/test.csv", true);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            //内容を指定する
            pw.print("あ");
            pw.print(",");
            pw.print("い");
            pw.println();

            pw.print("01");
            pw.print(",");
            pw.print("02");
            pw.println();

            pw.print(ca);
            //ファイルに書き出す


            //終了メッセージを画面に出力する
            System.out.println("出力が完了しました。");

        } catch (IOException ex) {
            //例外時処理
            ex.printStackTrace();
        }
*/

// クリックイベントを取得したいボタン
        Button button = (Button) findViewById(R.id.button);
// ボタンに OnClickListener インターフェースを実装する
        button.setOnClickListener(new View.OnClickListener() {

            // クリック時に呼ばれるメソッド
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "クリックされました！", Toast.LENGTH_LONG).show();
                tv2 = (TextView) (findViewById(R.id.inform));
                tv2.setText("止まっている状態の計測開始！");
                flag = 1;
                //止まっている時の状態の計測
            }
        });
        // クリックイベントを取得したいボタン
        Button button2 = (Button) findViewById(R.id.button2);
// ボタンに OnClickListener インターフェースを実装する
        button2.setOnClickListener(new View.OnClickListener() {

            // クリック時に呼ばれるメソッド
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "クリックされました！", Toast.LENGTH_LONG).show();
                tv2 = (TextView) (findViewById(R.id.inform));
                tv2.setText("歩いている状態の計測開始！");
                flag = 2;
                //歩いている時の状態の計測
            }
        });
        // クリックイベントを取得したいボタン
        Button button3 = (Button) findViewById(R.id.button3);
// ボタンに OnClickListener インターフェースを実装する
        button3.setOnClickListener(new View.OnClickListener() {

            // クリック時に呼ばれるメソッド
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "クリックされました！", Toast.LENGTH_LONG).show();
                tv2 = (TextView) (findViewById(R.id.inform));
                tv2.setText("走っている状態の計測開始！");
                flag = 3;
                //走っている時の状態の計測
            }
        });
        Button button4 = (Button) findViewById(R.id.button4);
// ボタンに OnClickListener インターフェースを実装する
        button4.setOnClickListener(new View.OnClickListener() {

            // クリック時に呼ばれるメソッド
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "クリックされました！", Toast.LENGTH_LONG).show();
                tv2 = (TextView) (findViewById(R.id.inform));
                tv2.setText("学習完了！");
                arffwriter();
                flag = 4;
                //走っている時の状態の計測
            }
        });

        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {

            // クリック時に呼ばれるメソッド
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "クリックされました！", Toast.LENGTH_LONG).show();
                tv2 = (TextView) (findViewById(R.id.inform));
                tv2.setText("学習結果をリセットしました！");
                arffDelete();
                flag = 5;
                //走っている時の状態の計測
            }
        });
        Button button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {

            // クリック時に呼ばれるメソッド
            @Override
            public void onClick(View view) {
                File recog = new File((Environment.getExternalStorageDirectory().getPath() + "/learning.arff"));
                if(recog.exists()){
                    //Toast.makeText(MainActivity.this, "クリックされました！", Toast.LENGTH_LONG).show();
                    tv2 = (TextView) (findViewById(R.id.inform));
                    //tv2.setText("歩きスマホ検知中");
                    flag = 6;
                }else{
                    tv2 = (TextView) (findViewById(R.id.inform));
                    tv2.setText("学習データがありません");
                }

                //走っている時の状態の計測
            }
        });
    }

    @Override
    public void run() {
        tv.setText("X-axis : " + gx + "\n"
                + "Y-axis : " + gy + "\n"
                + "Z-axis : " + gz + "\n"
                + "Composite Acceleration" + ca + "\n");
        h.postDelayed(this, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors =
                sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (0 < sensors.size()) {
            sm.registerListener(this, sensors.get(0),
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        h.removeCallbacks(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        gx = event.values[0];
        gy = event.values[1];
        gz = event.values[2];
        ca = Math.sqrt(gx * gx + gy * gy + gz * gz);
        //tv3 =  (TextView)(findViewById(R.id.counter));
        //String FILE = "/test.csv";
        if (flag == 1) {
            if (counter < 1000) {
                try {
                    FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/stand.csv", true);
                    PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
                    String write_int =
                            String.valueOf(ca) + "," + "stand" + "\n";
                    //ここ変えるかも
                    pw.write(write_int);
                    pw.flush();
                    pw.close();
                    counter++;
                    //回数表示
                    //tv3 =  (TextView)(findViewById(R.id.counter));
                    //tv3.setText(counter);
                } catch (IOException k) {
                    k.printStackTrace();
                }
            }
            /*else{
                return;
            }*/
        } else if (flag == 2) {
            if (counter < 1000) {
                try {
                    FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/walk.csv", true);
                    PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

                    String write_int =
                            String.valueOf(ca) + "," + "walk" + "\n";
                    //ここ変えるかも
                    pw.write(write_int);
                    pw.flush();
                    pw.close();
                    counter++;
                } catch (IOException k) {
                    k.printStackTrace();
                }
            }
        } else if (flag == 3) {
            if (counter < 1000) {
                try {
                    FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory().getPath() + "/run.csv", true);
                    PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

                    String write_int =
                            String.valueOf(ca) + "," + "run" + "\n";
                    //ここ変えるかも
                    pw.write(write_int);
                    pw.flush();
                    pw.close();
                    counter++;
                } catch (IOException k) {
                    k.printStackTrace();
                }
            }
        }else if(flag==6){
            //Timer timer = new Timer();
            //TimerTask task = new TimerTask(){
                //public void run() {
                    judge();
                //}
            //};
            //timer.scheduleAtFixedRate(task,0,2000);
            //judge();
        }
        if (counter >= 1000) {
            counter = 0;
            flag = 0;
            tv2.setText("計測終わり！");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void arffwriter() {
        int i = 0;
        int j = 0;
        int z = 0;
        String stand_t=null;
        String walk_t=null;
        String run_t=null;
        FileOutputStream Files;
        FileInputStream Filei;
        try {
            Files = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/learning.arff"));
            String write =
                    ("@relation file" + "\n"+"\n" + "@attribute acceleration real" + "\n" + "@attribute state{stand,walk,run}" +"\n"+
                            "\n" + "@data" + "\n");
            Files.write(write.getBytes());
            try {
                Filei = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/stand.csv"));
                byte[] readBytes = new byte[Filei.available()];
                Filei.read(readBytes);
                stand_t = new String(readBytes);
            }catch (IOException e) {
                e.printStackTrace();
            }
            Files.write(stand_t.getBytes());
            try {
                Filei = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/walk.csv"));
                byte[] readBytes = new byte[Filei.available()];
                Filei.read(readBytes);
                walk_t = new String(readBytes);
            }catch (IOException e) {
                e.printStackTrace();
            }
            Files.write(walk_t.getBytes());
            try {
                Filei = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath() + "/run.csv"));
                byte[] readBytes = new byte[Filei.available()];
                Filei.read(readBytes);
                run_t = new String(readBytes);
            }catch (IOException e) {
                e.printStackTrace();
            }
            Files.write(run_t.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        distinguish();
    }
    public void arffDelete(){
        File deletefile = new File((Environment.getExternalStorageDirectory().getPath() + "/learning.arff"));
        File deletestandfile = new File((Environment.getExternalStorageDirectory().getPath() + "/stand.csv"));
        File deletewalkfile = new File((Environment.getExternalStorageDirectory().getPath() + "/walk.csv"));
        File deleterunfile = new File((Environment.getExternalStorageDirectory().getPath() + "/run.csv"));
        if (deletefile.exists()){
            //System.out.println("ファイルは存在します");
            deletefile.delete();
            deletestandfile.delete();
            deletewalkfile.delete();
            deleterunfile.delete();
        }/*else{
            //System.out.println("ファイルは存在しません");
        }*/
    }
    public void distinguish(){
        try {
            source = new DataSource((Environment.getExternalStorageDirectory().getPath() + "/learning.arff"));
            instances = source.getDataSet();
            instances.setClassIndex(1);
            classifier = new J48();
            classifier.buildClassifier(instances);

            eval = new Evaluation(instances);
            eval.evaluateModel(classifier, instances);
            //System.out.println(eval.toSummaryString());
            acceleration = new Attribute("acceleration", 0);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void judge(){
       // Timer timer = new Timer(); // 今回追加する処理
       // TimerTask task = new TimerTask() {
            //public void run() {
                try

                {
                    instance = new DenseInstance(30);
                    instance.setValue(acceleration, ca);
                    instance.setDataset(instances);

                    double result = classifier.classifyInstance(instance);
                    int results = (int) result;
                    if (results == 0) {
                        tv2.setText("止まりスマホ");
                    } else if (results == 1) {
                        tv2.setText("歩きスマホ");
                    } else if (results == 2) {
                        tv2.setText("走りスマホ");
                    }
                    //tv2.setText(String.valueOf(results));
                } catch (Exception e) {
                    e.printStackTrace();
                }
           // }
        //};
       // timer.scheduleAtFixedRate(task,0,2000); // 今回追加する処理
    }
}



