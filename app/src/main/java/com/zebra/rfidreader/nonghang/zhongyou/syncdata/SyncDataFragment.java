package com.zebra.rfidreader.nonghang.zhongyou.syncdata;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.zebra.rfidreader.nonghang.R;
import com.zebra.rfidreader.nonghang.nongshanghang.datebase.DemoDatabase;
import com.zebra.rfidreader.nonghang.zhongyou.model.AreaBean;
import com.zebra.rfidreader.nonghang.zhongyou.model.CityModel;
import com.zebra.rfidreader.nonghang.zhongyou.model.DistrictModel;
import com.zebra.rfidreader.nonghang.zhongyou.model.ProvinceModel;
import com.zebra.rfidreader.nonghang.zhongyou.model.XmlParserHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SyncDataFragment extends Fragment {
    private Button btRead;
    private Button btReduce;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hn_data, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btRead = (Button) getActivity().findViewById(R.id.bt_data_in);
        btReduce = (Button) getActivity().findViewById(R.id.bt_data_reduce);
        btRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseXml();
            }
        });
        btReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void parseXml() {
        AssetManager assets = getActivity().getAssets();
        List<ProvinceModel> provinceList = new ArrayList<>();
        ArrayList<AreaBean> areaBeans = new ArrayList<>();
        try {
            InputStream e = assets.open("zhongyou.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(e, handler);
            e.close();
            provinceList = handler.getDataList();
            if(provinceList.size() > 0){
                for (ProvinceModel provinceModel : provinceList) {
                    AreaBean province = new AreaBean(provinceModel.getProvinceID(), provinceModel.getProvince(), "-1");
                    areaBeans.add(province);
                    List<CityModel> cityList = provinceModel.getCityList();
                    for (CityModel cityModel : cityList) {
                        AreaBean city = new AreaBean(cityModel.getCityID(), cityModel.getCity(), provinceModel.getProvinceID());
                        if(!areaBeans.contains(city)){
                            areaBeans.add(city);
                        }
                        List<DistrictModel> districtList = cityModel.getDistrictList();
                        for (DistrictModel districtModel : districtList) {
                            AreaBean district = new AreaBean(districtModel.getPieceareaID(), districtModel.getPiecearea(), cityModel.getCityID());
                            if(!areaBeans.contains(district)){
                                areaBeans.add(district);
                            }
                        }
                    }
                }
            }
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    DemoDatabase.getInstance().getAreaBeanDao().deleteAllData();
                    DemoDatabase.getInstance().getAreaBeanDao().insertItems(areaBeans);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if(getActivity() != null){
                        Toast.makeText(getActivity(),"数据导入成功", Toast.LENGTH_SHORT).show();
                    }

                }
            }.execute();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    public static SyncDataFragment newInstance() {
        return new SyncDataFragment();
    }
}
