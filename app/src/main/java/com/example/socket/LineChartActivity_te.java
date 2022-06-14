package com.example.socket;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class LineChartActivity_te extends AppCompatActivity {

    private LineChart chart;
    private Thread thread;
    NetworkActivity na;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.line_chart);
        na = new NetworkActivity();
        textView = findViewById(R.id.tv_graph);
        textView.setText("온도");

        chart = (LineChart) findViewById(R.id.chart);

        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setLabelCount(15, true); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.animateXY(2000, 2000);
        chart.invalidate();

        LineData data = new LineData();
        chart.setData(data);

        feedMultiple();
    }

    private void addEntry(int num) {
        LineData data = chart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            //그래프 데이터 x축과 y축 추가
            data.addEntry(new Entry(set.getEntryCount(), (float) num), 0);
            //데이터 변경 알림
            data.notifyDataChanged();

            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(10);
            chart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setFillAlpha(110);
        set.setFillColor(Color.parseColor("#d7e7fa"));
        set.setColor(Color.parseColor("#0B80C9"));
        set.setCircleColor(Color.parseColor("#FFA1B4DC"));
        set.setCircleColorHole(Color.BLUE);
        set.setValueTextColor(Color.WHITE);
        set.setDrawValues(false);
        set.setLineWidth(2);
        set.setCircleRadius(6);
        set.setDrawCircleHole(false);
        set.setDrawCircles(false);
        set.setValueTextSize(9f);
        set.setDrawFilled(true);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setHighLightColor(Color.rgb(244, 117, 117));

        return set;
    }

    private void feedMultiple() {
        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                addEntry(na.getTe_line());
                Log.d("te_line", ""+na.te_line);
            }
        };

        // 데이터를 받고 그리기 위해 thread(스레드) 안에서 사용하였다면
        // 차트 그리는 것도 UI변경이므로 runOnUIThread를 사용
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(runnable);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null)
            thread.interrupt();
    }
}
