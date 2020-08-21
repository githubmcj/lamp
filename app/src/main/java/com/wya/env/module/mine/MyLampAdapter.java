package com.wya.env.module.mine;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wya.env.R;
import com.wya.env.bean.doodle.LampSetting;
import com.wya.env.net.tpc.TaskCenter;
import com.wya.env.util.ByteUtil;
import com.wya.uikit.pickerview.CustomTimePicker;
import com.wya.utils.utils.LogUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.wya.env.common.CommonValue.TCP_PORT;

/**
 * @date: 2018/6/29 13:55
 * @author: Chunjiang Mao
 * @classname: DataAdapter
 * @describe: 我的设备
 */

public class MyLampAdapter extends BaseQuickAdapter<LampSetting, BaseViewHolder> {

    private Context context;
    private List<LampSetting> data;
    private CustomTimePicker mCustomTimePicker;


    public MyLampAdapter(Context context, int layoutResId, @Nullable List<LampSetting> data) {
        super(layoutResId, data);
        this.context = context;
        this.data = data;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void convert(BaseViewHolder helper, LampSetting item) {
        helper.setText(R.id.name, item.getName());
        if (item.isOpen()) {
            helper.getView(R.id.img_open).setBackground(context.getResources().getDrawable(R.drawable.dengguang));
        } else {
            helper.getView(R.id.img_open).setBackground(context.getResources().getDrawable(R.drawable.morenshebei));
        }

        if (item.isHasTimer()) {
            helper.getView(R.id.img_time_open).setBackground(context.getResources().getDrawable(R.drawable.dengguang));
            helper.setVisible(R.id.time, true);
            helper.setText(R.id.time, "定时开：" + item.getStartTime() + "    " + "定时关：" + item.getStopTime());
        } else {
            helper.getView(R.id.img_time_open).setBackground(context.getResources().getDrawable(R.drawable.morenshebei));
            helper.setVisible(R.id.time, false);
        }

        helper.getView(R.id.img_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setOpen(!item.isOpen());
                sendData(item.getIp(), getOpenLamp(item.isOpen()));
                MyLampAdapter.this.notifyDataSetChanged();
            }
        });

        helper.getView(R.id.img_time_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isHasTimer()) {
                    item.setHasTimer(!item.isHasTimer());
                    MyLampAdapter.this.notifyDataSetChanged();
                } else {
                    openChoseTime();
                }
            }
        });

        helper.getView(R.id.img_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(helper.getAdapterPosition());
                MyLampAdapter.this.notifyDataSetChanged();
            }
        });
    }


    private void openChoseTime() {
        mCustomTimePicker = new CustomTimePicker(context, new CustomTimePicker.OnTimePickerSelectedListener() {
            @Override
            public void selected(Date date) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                String format = dateFormat.format(date);
//                mYmdhmsText.setText(format);
                LogUtil.e(format);
            }
        });
        mCustomTimePicker.setType(new boolean[]{false, false, false, true, true, false})
                .show();
    }

    private void sendData(String ip, byte[] bodyData) {
        TaskCenter.sharedCenter().setDisconnectedCallback(new TaskCenter.OnServerDisconnectedCallbackBlock() {
            @Override
            public void callback(IOException e) {
                showShort("连接失败：" + e.getMessage());
            }
        });
        TaskCenter.sharedCenter().setConnectedCallback(new TaskCenter.OnServerConnectedCallbackBlock() {
            @Override
            public void callback() {
                LogUtil.e("连接成功， 打开开关");
                TaskCenter.sharedCenter().send(bodyData);
            }
        });
        TaskCenter.sharedCenter().setReceivedCallback(new TaskCenter.OnReceiveCallbackBlock() {
            @Override
            public void callback(byte[] receiceData) {
                showShort("返回数据：" + ByteUtil.byte2hex(receiceData));
                TaskCenter.sharedCenter().disconnect();
            }
        });
        //连接
        TaskCenter.sharedCenter().connect(ip, TCP_PORT);
    }

    int step = 0;

    private byte[] getOpenLamp(boolean open) {
        byte[] bodyData = new byte[4];
        bodyData[0] = 0x01;
        bodyData[1] = (byte) (0xff & step);
        bodyData[2] = (byte) 0x81;
        if (open) {
            bodyData[3] = 0x01;
        } else {
            bodyData[3] = 0x00;
        }
        byte[] send_head_data = ByteUtil.getHeadByteData(bodyData);
        byte[] openFileData = ByteUtil.byteMerger(send_head_data, bodyData);
        LogUtil.e("openFileData:" + ByteUtil.byte2hex(openFileData));
        return openFileData;
    }

    public void showShort(String msg) {
        try {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //解决在子线程中调用Toast的异常情况处理
            Looper.prepare();
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }

    }

}
