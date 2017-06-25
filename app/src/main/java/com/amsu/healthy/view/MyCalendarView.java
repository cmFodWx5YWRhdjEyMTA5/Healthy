package com.amsu.healthy.view;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.amsu.healthy.R;
import com.amsu.healthy.utils.MyUtil;

/**
 * 日历控件 功能：获得点选的日期区间
 * 
 */
public class MyCalendarView extends View implements View.OnTouchListener {
	private final static String TAG = "MyCalendarView";
	private Date selectedStartDate;
	private Date selectedEndDate;
	private Date curDate; // 当前日历显示的月
	private Date today; // 今天的日期文字显示红色
	private Date downDate; // 手指按下状态时临时日期
	private Date showFirstDate, showLastDate; // 日历显示的第一个日期和最后一个日期
	private int downIndex; // 按下的格子索引
	private Calendar calendar;
	private Surface surface;
	private int[] date = new int[42]; // 日历显示数字
	private int curStartIndex, curEndIndex; // 当前显示的日历起始的索引
	private boolean completed = false; // 为false表示只选择了开始日期，true表示结束日期也选择了
	private boolean isSelectMore = false;
	//给控件设置监听事件
	private OnItemClickListener onItemClickListener;
	private OnItemLongClickListener onItemLongClickListener;
	private float textSize;
	float testHeight;
	private int dayInWeek;
	private boolean isNeedDrawCircle;
	private boolean isNeedDrawBack = true;
	private int[] planDays;
	private boolean isCurrMouth = false;


	public MyCalendarView(Context context) {
		super(context);
		init();
	}

	public MyCalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyCalendarView);
		textSize = typedArray.getDimension(R.styleable.MyCalendarView_cellTextSize, 0);
		testHeight = typedArray.getDimension(R.styleable.MyCalendarView_cellHeight, 0);

		Log.i(TAG,"dimension:"+textSize);


		init();
	}

	private void init() {
		curDate = selectedStartDate = selectedEndDate = today = new Date();
		calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		surface = new Surface();
		surface.density = getResources().getDisplayMetrics().density;
		setBackgroundColor(surface.bgColor);
		setOnTouchListener(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		surface.width = getResources().getDisplayMetrics().widthPixels;
		surface.height = (int) (getResources().getDisplayMetrics().heightPixels*2/5);

		if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
			surface.width = MeasureSpec.getSize(widthMeasureSpec);
		}
		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
			surface.height = MeasureSpec.getSize(heightMeasureSpec);
		}
		widthMeasureSpec = MeasureSpec.makeMeasureSpec(surface.width, MeasureSpec.EXACTLY);
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(surface.height, MeasureSpec.EXACTLY);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		Log.d(TAG, "[onLayout] changed:" + (changed ? "new size" : "not change") + " left:" + left + " top:" + top + " right:" + right + " bottom:" + bottom);
		if (changed) {
			surface.init();
		}
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(TAG, "onDraw");
		// 画框
		//canvas.drawPath(surface.boxPath, surface.borderPaint);
		// 年月
		//String monthText = getYearAndmonth();
		//float textWidth = surface.monthPaint.measureText(monthText);
		//canvas.drawText(monthText, (surface.width - textWidth) / 2f,
		//		surface.monthHeight * 3 / 4f, surface.monthPaint);
		// 上一月/下一月
		//canvas.drawPath(surface.preMonthBtnPath, surface.monthChangeBtnPaint);
		//canvas.drawPath(surface.nextMonthBtnPath, surface.monthChangeBtnPaint);
		// 星期
		float weekTextY = surface.monthHeight + surface.weekHeight * 3 / 4f;
		// 星期背景
//		surface.cellBgPaint.setColor(surface.textColor);
//		canvas.drawRect(surface.weekHeight, surface.width, surface.weekHeight, surface.width, surface.cellBgPaint);
		for (int i = 0; i < surface.weekText.length; i++) {
			float weekTextX = i* surface.cellWidth+ (surface.cellWidth - surface.weekPaint.measureText(surface.weekText[i])) / 2f;
			if (i==0 || i==6){
				surface.weekPaint.setColor(surface.cellSelectedColor);
			}
			else {
				surface.weekPaint.setColor(surface.textColor);
			}
			canvas.drawText(surface.weekText[i], weekTextX, weekTextY,surface.weekPaint);
		}
		
		// 计算日期
		calculateDate();

		// write date number
		// today index
		int todayIndex = -1;
		calendar.setTime(curDate);
		String curYearAndMonth = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);
		calendar.setTime(today);
		String todayYearAndMonth = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);
		if (curYearAndMonth.equals(todayYearAndMonth)) {
			int todayNumber = calendar.get(Calendar.DAY_OF_MONTH);
			todayIndex = curStartIndex + todayNumber - 1;
		}
		// 按下状态，选择状态背景色
		drawDownOrSelectedBg(canvas);
		if (todayIndex!=-1){
			drawCellBg(canvas, todayIndex, surface.cellTodayColor);
		}


		for (int i = 0; i < 42; i++) {
			int color = surface.textColor;
			if (isLastMonth(i)) {
				color = surface.borderColor;
			} 
			else if (isNextMonth(i)) {
				color = surface.borderColor;
			}
			else if (todayIndex != -1 && i == todayIndex) {
				color = surface.cellSelectedTextColor;
				drawCellText(canvas, i, date[i] + "", color);
			}
			else {
				if (i%7==0 || (i+1)%7==0){
					color = surface.cellSelectedColor;
				}
				if (i==downIndex){
					color = surface.cellSelectedTextColor;
				}
				drawCellText(canvas, i, date[i] + "", color);

			}
		}
		super.onDraw(canvas);
	}

	private void calculateDate() {
		calendar.setTime(curDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
		Log.d(TAG, "day in week:" + dayInWeek);
		int monthStart = dayInWeek;

		/*if (monthStart == 1) {
			monthStart = 1;
		}*/

		monthStart -= 1;  //以日为开头-1，以星期一为开头-2
		curStartIndex = monthStart;
		if (planDays!=null){
			int temp = 0;
			if (!isCurrMouth){
				temp = monthStart-1;
			}
			for (int i=0;i<planDays.length;i++){
				planDays[i] = planDays[i]+temp;
			}
			isCurrMouth = true;
		}
		date[monthStart] = 1;
		// last month
		if (monthStart > 0) {
			calendar.set(Calendar.DAY_OF_MONTH, 0);
			int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
			for (int i = monthStart - 1; i >= 0; i--) {
				date[i] = dayInmonth;
				dayInmonth--;
			}
			calendar.set(Calendar.DAY_OF_MONTH, date[0]);
		}
		showFirstDate = calendar.getTime();
		// this month
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 0);
		// Log.d(TAG, "m:" + calendar.get(Calendar.MONTH) + " d:" +
		// calendar.get(Calendar.DAY_OF_MONTH));
		int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
		for (int i = 1; i < monthDay; i++) {
			date[monthStart + i] = i + 1;
		}
		curEndIndex = monthStart + monthDay;
		// next month
		for (int i = monthStart + monthDay; i < 42; i++) {
			date[i] = i - (monthStart + monthDay) + 1;
		}
		if (curEndIndex < 42) {
			// 显示了下一月的
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		calendar.set(Calendar.DAY_OF_MONTH, date[41]);
		showLastDate = calendar.getTime();
	}

	/**
	 * 
	 * @param canvas
	 * @param index
	 * @param text
	 */
	private void drawCellText(Canvas canvas, int index, String text, int color) {
		//Log.i(TAG,"index:"+index);
		int x = getXByIndex(index);
		int y = getYByIndex(index);
		surface.datePaint.setColor(color);
		float cellY = surface.monthHeight + surface.weekHeight + (y - 1) * surface.cellHeight + surface.cellHeight * 2 / 3f;
		float cellX = (surface.cellWidth * (x - 1)) + (surface.cellWidth - surface.datePaint.measureText(text)) / 2f;
		canvas.drawText(text, cellX, cellY, surface.datePaint);
		if (planDays!=null){
			for (int i=0;i<planDays.length;i++){
				if (index==planDays[i]){
					canvas.drawCircle(cellX+surface.cellWidth/4, cellY+surface.cellHeight/6,getResources().getDimension(R.dimen.x10), surface.mPointPaint);
				}
			}
		}
	}

	/**
	 *
	 * @param canvas
	 * @param index
	 * @param color
	 */
	private void drawCellBg(Canvas canvas, int index, int color) {
		int x = getXByIndex(index);
		int y = getYByIndex(index);
		surface.cellBgPaint.setColor(color);
		float left = surface.cellWidth * (x - 1) + surface.borderWidth;
		float top = surface.monthHeight + surface.weekHeight + (y - 1)* surface.cellHeight + surface.borderWidth;
		float cneterX =left + surface.cellWidth/2f;
		float cneterY =top + surface.cellHeight/2f;
		//canvas.drawRect(left, top, left + surface.cellWidth- surface.borderWidth, top + surface.cellHeight- surface.borderWidth, surface.cellBgPaint);
		float radius = testHeight/2f;
		canvas.drawCircle(cneterX, cneterY, radius, surface.cellBgPaint);
	}

	private void drawDownOrSelectedBg(Canvas canvas) {
		if (!isNeedDrawBack){
			isNeedDrawBack = true;
			return;
		}

		// down and not up
		if (downDate != null) {
			drawCellBg(canvas, downIndex, surface.cellSelectedColor);
		}
	}

	//设置当月有计划的日期列表，然后会在日期下面显示小圆点
	public void setPlanDays(int[] planDays){
		this.planDays = planDays;
		isCurrMouth = false;
		invalidate();
	}

	private void findSelectedIndex(int startIndex, int endIndex, Calendar calendar, int[] section) {
		for (int i = startIndex; i < endIndex; i++) {
			calendar.set(Calendar.DAY_OF_MONTH, date[i]);
			Date temp = calendar.getTime();
			// Log.d(TAG, "temp:" + temp.toLocaleString());
			if (temp.compareTo(selectedStartDate) == 0) {
				section[0] = i;
			}
			if (temp.compareTo(selectedEndDate) == 0) {
				section[1] = i;
				return;
			}
		}
	}

	public Date getSelectedStartDate() {
		return selectedStartDate;
	}

	public Date getSelectedEndDate() {
		return selectedEndDate;
	}

	private boolean isLastMonth(int i) {
		if (i < curStartIndex) {
			return true;
		}
		return false;
	}

	private boolean isNextMonth(int i) {
		if (i >= curEndIndex) {
			return true;
		}
		return false;
	}

	private int getXByIndex(int i) {
		return i % 7 + 1; // 1 2 3 4 5 6 7
	}

	private int getYByIndex(int i) {
		return i / 7 + 1; // 1 2 3 4 5 6
	}

	// 获得当前应该显示的年月
	public String getYearAndmonth() {
		calendar.setTime(curDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		return year + "-" + month;
	}
	
	//上一月
	public String clickLeftMonth(){
		downIndex = -1;
		isNeedDrawBack = false;
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, -1);
		curDate = calendar.getTime();
		invalidate();
		return getYearAndmonth();
	}
	//下一月
	public String clickRightMonth(){
		downIndex = -1;
		isNeedDrawBack = false;
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, 1);
		curDate = calendar.getTime();
		invalidate();
		return getYearAndmonth();
	}
	
	//设置日历时间
	public void setCalendarData(Date date){
		calendar.setTime(date);
		invalidate();
	}
	
	//获取日历时间
	public Date getCalendatData(){
		return calendar.getTime();
	}
	
	//设置是否多选
	public boolean isSelectMore() {
		return isSelectMore;
	}

	public void setSelectMore(boolean isSelectMore) {
		this.isSelectMore = isSelectMore;
	}

	private int setSelectedDateByCoor(float x, float y) {
		// change month
//		if (y < surface.monthHeight) {
//			// pre month
//			if (x < surface.monthChangeWidth) {
//				calendar.setTime(curDate);
//				calendar.add(Calendar.MONTH, -1);
//				curDate = calendar.getTime();
//			}
//			// next month
//			else if (x > surface.width - surface.monthChangeWidth) {
//				calendar.setTime(curDate);
//				calendar.add(Calendar.MONTH, 1);
//				curDate = calendar.getTime();
//			}
//		}
		// cell click down
		int dayInmonth = -1;
		if (y > surface.monthHeight + surface.weekHeight) {
			int m = (int) (Math.floor(x / surface.cellWidth) + 1);
			int n = (int) (Math.floor((y - (surface.monthHeight + surface.weekHeight)) / Float.valueOf(surface.cellHeight)) + 1);
			downIndex = (n - 1) * 7 + m - 1;
			Log.d(TAG, "downIndex:" + downIndex);
			Log.d(TAG, "dayInWeek:" + dayInWeek);

			calendar.setTime(curDate);
			if (isLastMonth(downIndex)) {
				calendar.add(Calendar.MONTH, -1);
			} else if (isNextMonth(downIndex)) {
				calendar.add(Calendar.MONTH, 1);
			}
			calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
			downDate = calendar.getTime();



			dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
			Log.i(TAG,"dayInmonth:"+dayInmonth);
			if (downIndex<dayInWeek-1 || downIndex>=dayInmonth+dayInWeek){
				isNeedDrawCircle = false;
				return -1;
			}
			else {
				isNeedDrawCircle = true;
			}
		}
		return dayInmonth;
		//invalidate();
	}

	Handler mHandler = new Handler();

	Runnable mRunnable = new Runnable(){
		@Override
		public void run(){
			int getnum = MyUtil.getIntValueFromSP("value");
			Log.i(TAG,"getnum:"+getnum);
			Log.i(TAG,"mDayInmonth:"+mDayInmonth);
			if(getnum == 1) {
				//此处根据判断可添加自己延迟后要执行的逻辑
				//MyUtil.showToask(getContext(),mDayInmonth+"");
				if (onItemClickListener!=null){
					onItemLongClickListener.OnItemLongClick(mDayInmonth);
				}

			}
		}
	};
	int mDayInmonth;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDayInmonth = setSelectedDateByCoor(event.getX(), event.getY());
			MyUtil.putIntValueFromSP("value",1);
			mHandler.postDelayed(mRunnable,1000);
			break;
		case MotionEvent.ACTION_UP:

			Log.i(TAG,"isNeedDrawCircle:"+isNeedDrawCircle);
			if (!isNeedDrawCircle){
				break;
			}
			if (downDate != null) {
				if(isSelectMore){
					if (!completed) {
						if (downDate.before(selectedStartDate)) {
							selectedEndDate = selectedStartDate;
							selectedStartDate = downDate;
						} else {
							selectedEndDate = downDate;
						}
						completed = true;
						//响应监听事件
						onItemClickListener.OnItemClick(selectedStartDate,selectedEndDate,downDate);
					} else {
						selectedStartDate = selectedEndDate = downDate;
						completed = false;
					}
				}else{
					selectedStartDate = selectedEndDate = downDate;
					//响应监听事件
					onItemClickListener.OnItemClick(selectedStartDate,selectedEndDate,downDate);
				}
				invalidate();
			}
			MyUtil.putIntValueFromSP("value",2);
			mHandler.removeCallbacks(mRunnable);
			break;
		}
		return true;
	}
	
	//给控件设置监听事件
	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		this.onItemClickListener =  onItemClickListener;
	}
	//监听接口
	public interface OnItemClickListener {
		void OnItemClick(Date selectedStartDate, Date selectedEndDate, Date downDate);
	}

	public interface OnItemLongClickListener{
		void OnItemLongClick(int dayInmonth);
	}

	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
		this.onItemLongClickListener = onItemLongClickListener;
	}

	/**
	 * 
	 * 1. 布局尺寸 2. 文字颜色，大小 3. 当前日期的颜色，选择的日期颜色
	 */
	private class Surface {
		public float density;
		public int width; // 整个控件的宽度
		public int height; // 整个控件的高度
		public float monthHeight; // 显示月的高度
		//public float monthChangeWidth; // 上一月、下一月按钮宽度
		public float weekHeight; // 显示星期的高度
		public float cellWidth; // 日期方框宽度
		public float cellHeight; // 日期方框高度	
		public float borderWidth;
		public int bgColor = Color.parseColor("#FFFFFF");
		private int textColor = Color.parseColor("#6d6d6d");
		//private int textColorUnimportant = Color.parseColor("#666666");
		private int btnColor = Color.parseColor("#666666");
		private int borderColor = Color.parseColor("#CCCCCC");
		public int todayNumberColor = Color.RED;
		public int cellDownColor = Color.parseColor("#CCFFFF");
		public int cellSelectedColor = Color.parseColor("#9e9e9e");  //选中背景色
		public int cellTodayColor = Color.parseColor("#ff3b30");  //选中背景色
		public int cellSelectedTextColor = Color.parseColor("#FFFFFF");  //选中背景色
		public Paint borderPaint;
		public Paint monthPaint;
		public Paint weekPaint;
		public Paint datePaint;
		public Paint monthChangeBtnPaint;
		public Paint cellBgPaint;
		public Path boxPath; // 边框路径
		//public Path preMonthBtnPath; // 上一月按钮三角形
		//public Path nextMonthBtnPath; // 下一月按钮三角形
		public String[] weekText = { "日","一", "二", "三", "四", "五", "六"};
		public Paint mPointPaint;
		   
		public void init() {
			float temp = height / 7f;
			monthHeight = 0;//(float) ((temp + temp * 0.3f) * 0.6);
			//monthChangeWidth = monthHeight * 1.5f;
			weekHeight = (float) ((temp + temp * 0.3f) * 0.7);
			cellHeight = (height - monthHeight - weekHeight) / 6f;
			cellWidth = width / 7f;
			borderPaint = new Paint();
			borderPaint.setColor(borderColor);
			borderPaint.setStyle(Paint.Style.STROKE);
			borderWidth = (float) (0.5 * density);
			// Log.d(TAG, "borderwidth:" + borderWidth);
			borderWidth = borderWidth < 1 ? 1 : borderWidth;
			borderPaint.setStrokeWidth(borderWidth);
			
			monthPaint = new Paint();
			monthPaint.setColor(textColor);
			monthPaint.setAntiAlias(true);
			float textSize = cellHeight * 0.4f;
			Log.d(TAG, "text size:" + textSize);
			monthPaint.setTextSize(textSize);
			monthPaint.setTypeface(Typeface.DEFAULT_BOLD);
			
			weekPaint = new Paint();
			weekPaint.setColor(textColor);
			weekPaint.setAntiAlias(true);
			float weekTextSize = weekHeight * 0.6f;
			weekPaint.setTextSize(textSize);
			//weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
			
			datePaint = new Paint();
			datePaint.setColor(textColor);
			datePaint.setAntiAlias(true);
			float cellTextSize = cellHeight * 0.5f;
			datePaint.setTextSize(textSize);
			//datePaint.setTypeface(Typeface.DEFAULT_BOLD);
			
			boxPath = new Path();
			/*//boxPath.addRect(0, 0, width, height, Direction.CW);
			//boxPath.moveTo(0, monthHeight);
			boxPath.rLineTo(width, 0);
			boxPath.moveTo(0, monthHeight + weekHeight);
			boxPath.rLineTo(width, 0);
			for (int i = 1; i < 6; i++) {
				boxPath.moveTo(0, monthHeight + weekHeight + i * cellHeight);
				boxPath.rLineTo(width, 0);
				boxPath.moveTo(i * cellWidth, monthHeight);
				boxPath.rLineTo(0, height - monthHeight);
			}
			boxPath.moveTo(6 * cellWidth, monthHeight);
			boxPath.rLineTo(0, height - monthHeight);*/
			
			//preMonthBtnPath = new Path();
			//int btnHeight = (int) (monthHeight * 0.6f);
			//preMonthBtnPath.moveTo(monthChangeWidth / 2f, monthHeight / 2f);
			//preMonthBtnPath.rLineTo(btnHeight / 2f, -btnHeight / 2f);
			//preMonthBtnPath.rLineTo(0, btnHeight);
			//preMonthBtnPath.close();
			//nextMonthBtnPath = new Path();
			//nextMonthBtnPath.moveTo(width - monthChangeWidth / 2f,
			//		monthHeight / 2f);
			//nextMonthBtnPath.rLineTo(-btnHeight / 2f, -btnHeight / 2f);
			//nextMonthBtnPath.rLineTo(0, btnHeight);
			//nextMonthBtnPath.close();
			monthChangeBtnPaint = new Paint();
			monthChangeBtnPaint.setAntiAlias(true);
			monthChangeBtnPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			monthChangeBtnPaint.setColor(btnColor);
			
			cellBgPaint = new Paint();
			cellBgPaint.setAntiAlias(true);
			cellBgPaint.setStyle(Paint.Style.FILL);
			cellBgPaint.setColor(cellSelectedColor);

			mPointPaint = new Paint();  //小红点
			mPointPaint.setColor(Color.RED);
            mPointPaint.setAntiAlias(true);

		}
	}
}
