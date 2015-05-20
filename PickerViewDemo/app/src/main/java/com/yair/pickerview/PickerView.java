package com.yair.pickerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yair.pickerviewdemo.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by yair on 5/17/15.
 */
public class PickerView extends RelativeLayout {
    private LayoutInflater inflater;
    private ListView listView = null;
    private View chooser = null;
    private PickerListAdapter adapter = null;
    private Context context;

    private boolean setListView = false;
    private int showCells, middleCell, cellHeight, firstVisibleItem = 0;
    private View thisView;
    private int selectorColor = Color.parseColor("#116b2b66");

    private ArrayList<String> array = null;

    public PickerView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initView(context);
    }

    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PickerView);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.PickerView_selectorColor:
                    // parse the scolor string (hex)
                    selectorColor = Color.parseColor(a.getString(attr));
                    break;
                case R.styleable.PickerView_array:
                    String[] arr = getResources().getStringArray(a.getResourceId(attr, 0));
                    array = new ArrayList<String>(Arrays.asList(arr));

                    break;
                default:
                    break;
            }
        }
        a.recycle();

        initView(context);
    }

    public PickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        initView(context);
    }

    private void initView(Context context) {

        this.context = context;

//        View.inflate(context, R.layout.picker_view, this);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        thisView = inflater.inflate(R.layout.picker_view, this, true);
//        this.addView(thisView);


        listView = (ListView) findViewById(R.id.listview);
        chooser = findViewById(R.id.chooser);

        chooser.setBackgroundColor(selectorColor);

        if (array != null) {
            setList(array);
        }
    }

    public void setList(ArrayList<String> items) {
        adapter = new PickerListAdapter(context, R.layout.list_item, items);
//        listView.setAdapter(adapter);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//        //At this time we need to call setMeasuredDimensions(). Lets just
//        //call the parent View's method
//        //(see https://github.com/android/platform_frameworks_base/blob/master/core/java/android/view/View.java)
//        //that does:
//        //setMeasuredDimension(getDefaultSize(
//        //                       getSuggestedMinimumWidth(), widthMeasureSpec),
//        //                    getDefaultSize(
//        //                       getSuggestedMinimumHeight(), heightMeasureSpec));
//        //
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int wspec = MeasureSpec.makeMeasureSpec(
//                getMeasuredWidth() / getChildCount(), MeasureSpec.EXACTLY);
//        int hspec = MeasureSpec.makeMeasureSpec(
//                getMeasuredHeight(), MeasureSpec.EXACTLY);
//        for (int i = 0; i < getChildCount(); i++) {
//            View v = getChildAt(i);
//            v.measure(wspec, hspec);
//        }
//    }


//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        for (int i = 0; i < getChildCount(); i++) {
//            getChildAt(i).layout(l, t, r, b);
//        }
//
//    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        // we set the years listView here, because we need to calculate the cells size, only
        // after listView already has height
        if (!setListView && adapter != null) {
            showCells = 5; // (int)getResources().getInteger(R.integer.years_show_cells);
            setListView = true;


            int height = listView.getHeight();
            cellHeight = height / showCells;

            middleCell = showCells / 2;


            // set the chooser rect on the middle cell, by the top margin

            if (chooser.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) chooser.getLayoutParams();
                p.height = cellHeight;
                p.setMargins(0, cellHeight * middleCell, 0, 0);
                chooser.requestLayout();
            }

            // adding empty cells on top and bottom
            adapter.addEmpties(showCells / 2);


            listView.setAdapter(adapter);

            // scroll listview to the middle.
            listView.setSelection(adapter.getCount() / 2);
            firstVisibleItem = adapter.getCount() / 2;

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                    if (scrollState == SCROLL_STATE_IDLE) {

                        View child = view.getChildAt(0);    // first visible child

                        if (child != null) {
                            firstVisibleItem = listView.getFirstVisiblePosition();
                            Rect r = new Rect(0, 0, child.getWidth(), child.getHeight());     // set this initially, as required by the docs
                            double height = child.getHeight() * 1.0;

                            view.getChildVisibleRect(child, r, null);
                            if (Math.abs(r.height()) < (int) height / 2) {
                                // show next child
                                firstVisibleItem++;
                                listView.setSelection(firstVisibleItem);
                            } else {
                                // show this child
                                listView.setSelection(firstVisibleItem);
                            }
                        }
                    }

                }

                @Override
                public void onScroll(AbsListView view, int firstVisible, int visibleItemCount, int totalItemCount) {

                }
            });
        }
    }

    private class PickerListAdapter extends ArrayAdapter<String> {

        private ArrayList<String> items;

        public PickerListAdapter(Context context, int textViewResourceId,
                                 ArrayList<String> sList) {
            super(context, textViewResourceId, sList);


            items = sList;

        }

        public void addEmpties(int size) {
            for (int i = 0; i < size; i++) {
                items.add("");
                items.add(0, "");
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        private class ViewHolder {
            TextView text;

        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_item, null);

                holder = new ViewHolder();

                holder.text = (TextView) convertView
                        .findViewById(R.id.item_text);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();


            }


            if (holder != null) {
                holder.text.setText("" + items.get(position));
//                holder.text.setHeight(80);
            }


            if (holder.text.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.text.getLayoutParams();
                p.height = cellHeight;
                holder.text.setLayoutParams(p);
                holder.text.requestLayout();

            }

            return convertView;

        }

    }

}
