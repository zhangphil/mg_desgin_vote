package migu.demo;


import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindCallback;

public class MyDialogFragment extends DialogFragment {

	private Activity activity;
	private	BarChart mBarChart;

	private	int msg_what=0x0a2;

	private	ArrayList<PlanResult> voteResult;

	private	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			//super.handleMessage(msg);
			Log.d("绘图", "开始设置数据");
			setData(mBarChart,voteResult, Utils.readAssetsFileCount(activity));
			//mBarChart.notifyDataSetChanged();
			mBarChart.invalidate();

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this.getActivity();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = activity.getLayoutInflater();
		View view = inflater.inflate(R.layout.results, null);
		mBarChart = (BarChart) view.findViewById(R.id.bar_chart);

		setBarChartStyle(mBarChart);

		//new MyAsyncTask().execute();
		queryData();
		// 制作数据点。
		//setData(mBarChart, Utils.readAssetsFileCount(activity));

		builder.setView(view).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		});

		return builder.create();
	}

	private void setBarChartStyle(BarChart mBarChart) {
		mBarChart.setDrawBarShadow(false);
		mBarChart.setDrawValueAboveBar(true);
		mBarChart.setDescription("");
		mBarChart.setMaxVisibleValueCount(60);
		mBarChart.setPinchZoom(false);
		mBarChart.setDrawGridBackground(false);

		XAxis xAxis = mBarChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(false);
		xAxis.setSpaceBetweenLabels(2);

		YAxis leftAxis = mBarChart.getAxisLeft();
		// leftAxis.setLabelCount(5, false);
		// leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
		// leftAxis.setSpaceTop(15f);
		// leftAxis.setTextColor(Color.BLUE);
		leftAxis.setEnabled(false);

		YAxis rightAxis = mBarChart.getAxisRight();
		// rightAxis.setDrawGridLines(false);
		// rightAxis.setLabelCount(5, false);
		// rightAxis.setSpaceTop(15f);
		// rightAxis.setTextColor(Color.GREEN);
		rightAxis.setEnabled(false);

		Legend mLegend = mBarChart.getLegend();
		mLegend.setPosition(LegendPosition.BELOW_CHART_CENTER);
		mLegend.setForm(LegendForm.SQUARE);
		mLegend.setFormSize(15f);
		mLegend.setTextSize(12f);
		mLegend.setXEntrySpace(5f);
	}

	private void queryData() {
		BmobQuery query = new BmobQuery("Vote");
		query.findObjects(activity, new FindCallback() {

			@Override
			public void onSuccess(JSONArray ja) {
				try {
					voteResult=readJSON(ja);
					handler.sendEmptyMessage(msg_what);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int code, String msg) {

			}
		});
	}

	private	class	PlanResult{
		public	int vote_count=0;
		public	int plan_seq;
	}

	private	boolean	hasPlanSeq(ArrayList<PlanResult> lists,int seq){
		for(int i=0;i<lists.size();i++){
			PlanResult pr=lists.get(i);
			if(pr.plan_seq==seq){
				return	true;
			}
		}

		return	false;
	}

	private ArrayList<PlanResult> readJSON(JSONArray jsonArray) throws Exception {
		Log.d("json", jsonArray.toString());

		ArrayList<PlanResult> results = new ArrayList<PlanResult>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jo = jsonArray.getJSONObject(i);
			//JSONArray ja = jo.getJSONArray("results");
			String s=jo.getString("results");

			//for (int j = 0; j < ja.length(); j++) {
			//String s = ja.getString(j);

			int num = Integer.parseInt(s);
			//Log.d("投票", num + "");
			boolean b=hasPlanSeq(results, num);

			if(!b){
				PlanResult pr=new PlanResult();
				pr.plan_seq=num;
				pr.vote_count=1;
				results.add(pr);
				//Log.d("新加方案"+pr.plan_seq, pr.count+"");
			}
			else{
				for(int k=0;k<results.size();k++){
					PlanResult pr=results.get(k);
					if(pr.plan_seq==num){
						PlanResult pr_new=new PlanResult();
						pr_new.plan_seq=num;
						pr_new.vote_count=pr.vote_count+1;
						results.remove(k);
						results.add(pr_new);

						//Log.d("调整方案"+pr_new.plan_seq, pr_new.count+"");
						break;
					}
				}
			}
			//}
		}

		/*
		for(int i=0;i<results.size();i++){
			PlanResult pr= results.get(i);
			Log.d("方案"+pr.plan_seq, pr.count+"");
		}
		*/

		return results;
	}

	private void setData(BarChart mBarChart,ArrayList<PlanResult> results, int count) {

		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			xVals.add(i, "方案" + (i + 1));
		}

		ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();

		for (int i = 0; i < count; i++) {
			//float val = (float) (Math.random() * 10000);
			for(int j=0;j<results.size();j++){
				PlanResult pr=results.get(j);
				if(pr.plan_seq==(i+1)){
					yVals.add(new BarEntry(pr.vote_count, i));

					break;
				}
			}

			//yVals.add(new BarEntry(val, i));
		}

		BarDataSet mBarDataSet = new BarDataSet(yVals, "投票结果");
		// mBarDataSet.setValueFormatter(new ValueFormatter() {
		//
		// @Override
		// public String getFormattedValue(float value) {
		// DecimalFormat decimalFormat = new DecimalFormat(".0");
		// String s = decimalFormat.format(value) + "毫米";
		//
		// return s;
		// }
		// });
		mBarDataSet.setValueFormatter(new ValueFormatter() {

			@Override
			public String getFormattedValue(float value, Entry entry, int dataSetIndex,
											ViewPortHandler viewPortHandler) {

				// DecimalFormat decimalFormat = new DecimalFormat(".0");
				String s = ((int)value)+"票";
				return s;
			}

		});

		// 如果是0f，那么柱状图之间将紧密无空隙的拼接在一起形成一片。
		mBarDataSet.setBarSpacePercent(30f);

		// 柱状图柱的颜色
		mBarDataSet.setColor(Color.RED);

		// 当柱状图某一柱被选中时候的颜色
		mBarDataSet.setHighLightColor(Color.YELLOW);

		ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
		dataSets.add(mBarDataSet);

		BarData mBarData = new BarData(xVals, dataSets);
		mBarData.setValueTextSize(12f);

		mBarChart.setData(mBarData);
	}

	/*
	private	class	MyAsyncTask	extends	AsyncTask{

		@Override
		protected Object doInBackground(Object... params) {

			//queryData();

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {

		}
	}
	*/
}