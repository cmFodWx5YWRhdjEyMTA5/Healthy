package com.amsu.healthy.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amsu.healthy.R;
import com.amsu.healthy.activity.ClubDynamicsDetialActivity;
import com.amsu.healthy.bean.Dynamics;
import com.amsu.healthy.view.CircleImageView;
import com.lidroid.xutils.BitmapUtils;

import java.util.List;

/**
 * Created by HP on 2017/1/16.
 */

public class DynamicsAdapter extends BaseAdapter {
    private List<Dynamics> dynamicsList;
    private Context context;
    private BitmapUtils bitmapUtils;

    public DynamicsAdapter(List<Dynamics> dynamicsList, Context context) {
        this.dynamicsList = dynamicsList;
        this.context = context;
        bitmapUtils = new BitmapUtils(context);
    }

    @Override
    public int getCount() {
        return dynamicsList.size();
        //return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.i("position",position+"");
        final Dynamics dynamics = dynamicsList.get(position);
        View inflate;
        final MyHolder myHolder;
        if (convertView!=null){
            inflate = convertView;
            myHolder = (MyHolder)inflate.getTag();
        }
        else {
            inflate = View.inflate(context, R.layout.view_item_dynamics, null);
            myHolder = new MyHolder();
            myHolder.cv_item_usericon = (CircleImageView) inflate.findViewById(R.id.cv_item_usericon);
            myHolder.tv_item_username = (TextView) inflate.findViewById(R.id.tv_item_username);
            myHolder.tv_item_time = (TextView) inflate.findViewById(R.id.tv_item_time);
            myHolder.tv_item_text = (TextView) inflate.findViewById(R.id.tv_item_text);
            myHolder.iv_item_image1 = (ImageView) inflate.findViewById(R.id.iv_item_image1);
            myHolder.iv_item_image2 = (ImageView) inflate.findViewById(R.id.iv_item_image2);
            myHolder.iv_item_image3 = (ImageView) inflate.findViewById(R.id.iv_item_image3);
            myHolder.iv_item_surname = (ImageView) inflate.findViewById(R.id.iv_item_surname);
            myHolder.iv_item_comment = (ImageView) inflate.findViewById(R.id.iv_item_comment);
            myHolder.iv_item_share = (ImageView) inflate.findViewById(R.id.iv_item_share);
            myHolder.iv_item_more = (ImageView) inflate.findViewById(R.id.iv_item_more);
            myHolder.iv_item_surnamecount = (TextView) inflate.findViewById(R.id.iv_item_surnamecount);
            myHolder.iv_item_commentcount = (TextView) inflate.findViewById(R.id.iv_item_commentcount);
            myHolder.ll_image = (LinearLayout) inflate.findViewById(R.id.ll_image);

            myHolder.iv_item_surname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myHolder.iv_item_surname.setImageResource(R.drawable.jlb_dzyz);
                    int surnamecount = Integer.parseInt(myHolder.iv_item_surnamecount.getText().toString())+1;
                    myHolder.iv_item_surnamecount.setText(surnamecount+"");
                }
            });
            myHolder.iv_item_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ClubDynamicsDetialActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("dynamics",dynamics);
                    intent.putExtra("bundle",bundle);
                    intent.putExtra("type",0);
                    context.startActivity(intent);
                    //跳到评论页
                    /*myHolder.iv_item_comment.setImageResource(R.drawable.jlb_plyp);
                    int commentcount = Integer.parseInt(myHolder.iv_item_commentcount.getText().toString())+1;
                    myHolder.iv_item_commentcount.setText(commentcount+"");*/
                }
            });
            myHolder.iv_item_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
            myHolder.iv_item_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            inflate.setTag(myHolder);
        }




        bitmapUtils.display( myHolder.cv_item_usericon,dynamics.getUserIconurl());
        myHolder.tv_item_username.setText(dynamics.getUsername());
        myHolder.tv_item_time.setText(dynamics.getTime());
        myHolder.tv_item_text.setText(dynamics.getText());
        myHolder.iv_item_surnamecount.setText(dynamics.getSurnameCount());
        myHolder.iv_item_commentcount.setText(dynamics.getCommentCount());
        String[] imageList = dynamics.getImageList();


        if (imageList.length==0){
            myHolder.ll_image.setVisibility(View.GONE);
        }
        else if (imageList.length==1){
            myHolder.iv_item_image2.setVisibility(View.INVISIBLE);
            myHolder.iv_item_image3.setVisibility(View.INVISIBLE);
            bitmapUtils.display(myHolder.iv_item_image1,imageList[0]);
        }
        else if (imageList.length==2){
            myHolder.iv_item_image3.setVisibility(View.INVISIBLE);
            bitmapUtils.display(myHolder.iv_item_image1,imageList[0]);
            bitmapUtils.display(myHolder.iv_item_image2,imageList[1]);
        }
        else if (imageList.length>2){
            bitmapUtils.display(myHolder.iv_item_image1,imageList[0]);
            bitmapUtils.display(myHolder.iv_item_image2,imageList[1]);
            bitmapUtils.display(myHolder.iv_item_image3,imageList[2]);
        }





        return inflate;
    }

    class MyHolder{
        CircleImageView cv_item_usericon;
        TextView tv_item_username;
        TextView tv_item_time;
        TextView tv_item_text;
        ImageView iv_item_image1 ;
        ImageView iv_item_image2 ;
        ImageView iv_item_image3 ;
        ImageView iv_item_surname;
        ImageView iv_item_comment;
        ImageView iv_item_share;
        ImageView iv_item_more ;
        TextView iv_item_surnamecount ;
        TextView iv_item_commentcount ;
        LinearLayout ll_image;
    }
}
