package com.reductivetech.taskclarity.page;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.reductivetech.taskclarity.R;

import java.util.List;

public class TaskListAdapter extends ArrayAdapter {

    public static final String TAG = TaskListAdapter.class.getSimpleName();

    private Context _context;
    private List<TaskListItem> _tasks;

    private View.OnClickListener _sliderClickListener;
    private View.OnLongClickListener _sliderLongClickListener;

    public TaskListAdapter(Context context, List<TaskListItem> tasks){
        super(context, R.layout.task_list_item, R.id.task_title, tasks);
        _context = context;
        _tasks = tasks;
    }

    @Override
    public int getCount() {
        return _tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return _tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task_list_item, null);
        }

        //TextView txtValue = (TextView) convertView.findViewById(R.id.task_value);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.task_title);
        View slider = convertView.findViewById(R.id.task_slider);
        slider.setOnClickListener(_sliderClickListener);
        slider.setOnLongClickListener(_sliderLongClickListener);

        //txtValue.setText(_tasks.get(position).getValue());
        txtTitle.setText(_tasks.get(position).getTitle());

        return convertView;
    }

    public void setSliderClickListener(View.OnClickListener listener) {
        _sliderClickListener = listener;
    }

    public void setSliderLongClickListener(View.OnLongClickListener listener) {
        _sliderLongClickListener = listener;
    }

}