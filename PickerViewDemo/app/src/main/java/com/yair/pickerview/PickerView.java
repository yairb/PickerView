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
    private ListView listView = null;
    private View selector = null;
    private PickerListAdapter adapter = null;
    private Context context;

    private boolean setListView = false;
    private int itemsToShow, middleCell, cellHeight, firstVisibleItem = 0;
    // set default selector color
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
                case R.styleable.PickerView_itemsToShow:
                    // how many items will be in seen in listView. should be define in @dimen
                    // to fit different devices.
                    itemsToShow = a.getInt(attr, 5);
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View thisView = inflater.inflate(R.layout.picker_view, this, true);
//        this.addView(thisView);


        listView = (ListView) findViewById(R.id.listview);
        selector = findViewById(R.id.chooser);

        selector.setBackgroundColor(selectorColor);

        // if we got the array from resources - set it now
        if (array != null) {
            setList(array);
        }
    }

    public void setList(ArrayList<String> items) {
        adapter = new PickerListAdapter(context, R.layout.list_item, items);
    }


    // get selected item
    public String getSelected(){
        if (adapter == null){
            return "";
        }else{
            return adapter.getItem(firstVisibleItem + itemsToShow / 2);
        }
    }

    // get selected item position
    public int getSelectedIndex(){
        if (adapter == null){
            return 0;
        }else{
            return firstVisibleItem + itemsToShow / 2;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        // we set the listView here, because we need to calculate the cells size, only
        // after listView already has height
        if (!setListView && adapter != null) {
            itemsToShow = 5; // (int)getResources().getInteger(R.integer.years_show_cells);
            setListView = true;


            int height = listView.getHeight();
            cellHeight = height / itemsToShow;

            middleCell = itemsToShow / 2;


            // set the selector rect on the middle cell, by the top margin

            if (selector.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) selector.getLayoutParams();
                p.height = cellHeight;
                p.setMargins(0, cellHeight * middleCell, 0, 0);
                selector.requestLayout();
            }

            // adding empty cells on top and bottom
            adapter.addEmpties(itemsToShow / 2);


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
        // to enable choosing of EVERY item, we need to add on top and bottom the list empty
        // items, so the user will be able scrolling there
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


                if (holder.text.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.text.getLayoutParams();
                    p.height = cellHeight;
                    holder.text.setLayoutParams(p);
                    holder.text.requestLayout();

                }
            }
            return convertView;

        }

    }

}
