package com.example.arne.translogistics_app;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.example.arne.translogistics_app.Model.DataRecording;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arne on 29-03-2018.
 */

public class MyDataRecAdapter extends ArrayAdapter<DataRecording> {
    private ArrayList<DataRecording> dataSet;

    public MyDataRecAdapter(@NonNull Context context, int resource, List<DataRecording> objects) {
        super(context, resource, objects);
        dataSet = (ArrayList<DataRecording>) objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       // return super.getView(position, convertView, parent);
        ViewHolder viewHolder;
        DataRecording dataRecording = getItem(position);
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.datarec_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            viewHolder.setBtnViewDataListener(dataRecording.getId());
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtPackageId.setText(String.valueOf(dataRecording.pack.getId()));
        viewHolder.txtRecId.setText(String.valueOf(dataRecording.getId()));
        viewHolder.txtCompany.setText(dataRecording.pack.getCompany());

        return convertView;
    }

    private class ViewHolder{
        TextView txtPackageId;
        TextView txtRecId;
        TextView txtCompany;
        Button btnViewData;
        Button btnSendData;

        public ViewHolder(View v){
            txtPackageId = v.findViewById(R.id.txtPackageId);
            txtRecId = v.findViewById(R.id.txtRecId);
            txtCompany = v.findViewById(R.id.txtCompany);
            btnViewData = v.findViewById(R.id.btnViewData);


        }
        public void setBtnViewDataListener(final int recId){
            btnViewData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DisplayDataActivity.class);
                    intent.putExtra("recId", recId);
                    getContext().startActivity(intent);
                }
            });
        }


    }

    @Nullable
    @Override
    public DataRecording getItem(int position) {
        return dataSet.get(position);
    }


}
