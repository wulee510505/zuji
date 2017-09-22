package com.wulee.administrator.zuji.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.entity.PublishPicture;
import com.wulee.administrator.zuji.utils.ImageUtil;
import com.wulee.administrator.zuji.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wulee on 2017/8/22 13:50
 */

public class PublishPicGridAdapter extends BaseAdapter {
    private Context context;
    private List<PublishPicture> picList =new ArrayList<>();

    public PublishPicGridAdapter(List<PublishPicture> picList, Context context) {
        super();
        this.context = context;
        this.picList = picList;
    }


    public  void setSelPic(List<PublishPicture> picList){
        this.picList = picList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        if (null != picList) {
            return picList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return picList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.context).inflate(R.layout.circle_publish_grid_item, parent,false);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.grid_item_img);

            int screenWidth = UIUtils.getScreenWidthAndHeight(context)[0];
            int gridItemWidth = (screenWidth - UIUtils.dip2px(60))/3;

            AbsListView.LayoutParams rlp =  new AbsListView.LayoutParams(gridItemWidth, gridItemWidth);
            convertView.setLayoutParams(rlp);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PublishPicture picture = picList.get(position);
        if(picture.getId() == -1){
            ImageUtil.setDefaultImageView(viewHolder.image,"",R.mipmap.icon_add_pic,context);
        }else{
            String imgPath =  picture.getPath();
            byte[] data = ImageUtil.getSmallBitmap(imgPath);
            if(data != null && data.length>0){
                Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length);
                viewHolder.image.setImageBitmap(bmp);
            }
        }
        return convertView;
    }

    class ViewHolder {
        public ImageView image;
    }

}
