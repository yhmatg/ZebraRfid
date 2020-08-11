package com.zebra.rfidreader.nonghang.nongshanghang;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.multilevel.treelist.Node;
import com.multilevel.treelist.TreeRecyclerAdapter;
import com.zebra.rfidreader.nonghang.R;
import com.zebra.rfidreader.nonghang.application.Application;
import com.zebra.rfidreader.nonghang.common.ResponseHandlerInterfaces;
import com.zebra.rfidreader.nonghang.home.MainActivity;
import com.zebra.rfidreader.nonghang.inventory.InventoryListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SHInvPointFragment extends Fragment implements ResponseHandlerInterfaces.ResponseTagHandler, ResponseHandlerInterfaces.TriggerEventHandler, FilePointBeanAdapter.OnItemClickListener, EpcPointBeanAdapter.OnItemClickListener {
    private FloatingActionButton inventoryButton;
    private TextView fileNum;
    private TextView bookNum;
    private TextView extraNum;
    private Button clearButton;
    private RecyclerView filePointRecycle;
    private FilePointBeanAdapter filePointAdapter;
    private List<FilePointBean> filePointBeans = new ArrayList<>();
    private RecyclerView epcPointRecycle;
    private EpcPointBeanAdapter epcPointAdapter;
    private List<String> epcBeans = new ArrayList<>();
    private View contentView;
    private TextView tvBagCode;
    //多余的epc标签
    private ArrayList<String> extraEpcs = new ArrayList<>();
    //盘到的epc标签
    private ArrayList<String> currentEpcs = new ArrayList<>();
    //封袋编号和对应的档案
    HashMap<String, FilePointBean> epcFileMap = new HashMap<>();
    private MaterialDialog updateDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initData() {
    }

    public static SHInvPointFragment newInstance() {
        return new SHInvPointFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_point, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inventoryButton = (FloatingActionButton) getActivity().findViewById(R.id.inventoryButton);
        fileNum = (TextView) getActivity().findViewById(R.id.file_num);
        bookNum = (TextView) getActivity().findViewById(R.id.book_num);
        extraNum = (TextView) getActivity().findViewById(R.id.extra_num);
        clearButton = (Button) getActivity().findViewById(R.id.bt_clear);
        filePointRecycle = (RecyclerView) getActivity().findViewById(R.id.file_recycle);
        filePointAdapter = new FilePointBeanAdapter(filePointBeans, getActivity());
        filePointAdapter.setOnItemClickListener(this);
        filePointRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        filePointRecycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        filePointRecycle.setAdapter(filePointAdapter);
        //初始化epcdialog布局
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.file_epc_dialog, null);
        tvBagCode = (TextView) contentView.findViewById(R.id.tv_bagCode);
        //tvEpc = (TextView) contentView.findViewById(R.id.tv_epc);
        epcPointAdapter = new EpcPointBeanAdapter(epcBeans,getActivity());
        epcPointAdapter.setOnItemClickListener(this);
        epcPointRecycle = (RecyclerView) contentView.findViewById(R.id.epc_recycle);
        epcPointRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        epcPointRecycle.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        epcPointRecycle.setAdapter(epcPointAdapter);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraEpcs.clear();
                filePointBeans.clear();
                extraEpcs.clear();
                currentEpcs.clear();
                epcFileMap.clear();
                fileNum.setText("0");
                bookNum.setText("0");
                extraNum.setText("0");
                filePointAdapter.notifyDataSetChanged();
            }
        });
    }



    @Override
    public void handleTagResponse(InventoryListItem inventoryListItem, boolean isAddedToList) {
        //hex
        String hexEpc = inventoryListItem.getTagID();
        //ascii
        String epc = hex2ascii(hexEpc);
        /*String[] split = epc.split("\\|");
        String epcHead = split[0];*/
        if(epc.length() == 14 || epc.length() == 16){
            if(!currentEpcs.contains(epc)){
                currentEpcs.add(epc);
                bookNum.setText(String.valueOf(currentEpcs.size()));
            }
            String epcHead = epc.substring(0,14);
            if(epcFileMap.containsKey(epcHead)){
                FilePointBean filePointBean = epcFileMap.get(epcHead);
                List<String> epcs = filePointBean.getEpcs();
                if(!epcs.contains(epc)){
                    epcs.add(epc);
                    filePointAdapter.notifyDataSetChanged();
                }
            }else {
                FilePointBean filePointBean = new FilePointBean();
                filePointBean.setBagCode(epcHead);
                filePointBean.getEpcs().add(epc);
                filePointBeans.add(filePointBean);
                filePointAdapter.notifyDataSetChanged();
                epcFileMap.put(epcHead,filePointBean);
                fileNum.setText(String.valueOf(filePointBeans.size()));
            }

        }else {
            if(!extraEpcs.contains(epc)){
                extraEpcs.add(epc);
                extraNum.setText(String.valueOf(extraEpcs.size()));
            }

        }

    }

    private static String hex2ascii(String tagID) {
        if (tagID != null && !tagID.equals("")) {
            String hex = tagID;
            int n = hex.length();
            if (((n % 2) > 0)) {
                return tagID;
            }
            StringBuilder sb = new StringBuilder(n / 2);
            try {
                sb = new StringBuilder((n / 2) + 2);
                //prefexing the ascii representation with a single quote
                for (int i = 0; i < n; i += 2) {
                    char a = hex.charAt(i);
                    char b = hex.charAt(i + 1);
                    char c = (char) ((hexToInt(a) << 4) | hexToInt(b));
                    if (a == '0' && b == '0') {
                        continue;
                    }
                    if (hexToInt(a) <= 7 && hexToInt(b) <= 0xf && c >= 0x20 && c <= 0x7f)
                        sb.append(c);
                    else
                        return tagID;
                }
            } catch (IllegalArgumentException iae) {
                return tagID;
            }
            //prefexing the ascii representation with a single quote
            return sb.toString();
        } else
            return tagID;
    }

    private static int hexToInt(char ch) {
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        throw new IllegalArgumentException(String.valueOf(ch));
    }

    public static String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }
        return hex.toString();
    }

    @Override
    public void triggerPressEventRecieved() {
        if (!Application.mIsInventoryRunning && getActivity() != null) {
            Application.mInventoryStartPending = true;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity != null) {
                        activity.inventoryStartOrStop(inventoryButton);
                    }
                }
            });
        }
    }

    @Override
    public void triggerReleaseEventRecieved() {
        if (((Application.mIsInventoryRunning == true) || (Application.mInventoryStartPending == true)) && getActivity() != null) {
            Application.mInventoryStartPending = false;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity != null) {
                        activity.inventoryStartOrStop(inventoryButton);
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(FilePointBean fileBean) {
        tvBagCode.setText("封袋编号： " + fileBean.getBagCode());
        epcBeans.clear();
        epcBeans.addAll(fileBean.getEpcs());
        epcPointAdapter.notifyDataSetChanged();
        showEpcDialog();
    }

    public void showEpcDialog() {
        if (updateDialog != null) {
            updateDialog.show();
        } else {
            updateDialog = new MaterialDialog.Builder(getActivity())
                    .customView(contentView, false)
                    .show();
            Window window = updateDialog.getWindow();
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onEpcItemClick(String epc) {
        Application.locateTag = asciiToHex(epc);
        updateDialog.dismiss();
    }
}
