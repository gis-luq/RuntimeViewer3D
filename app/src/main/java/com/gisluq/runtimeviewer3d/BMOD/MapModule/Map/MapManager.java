package com.gisluq.runtimeviewer3d.BMOD.MapModule.Map;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedListener;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedEvent;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedListener;
import com.gisluq.runtimeviewer3d.Config.Entity.ConfigEntity;
import com.gisluq.runtimeviewer3d.BMOD.MapModule.Location.DMUserLocationManager;
import com.gisluq.runtimeviewer3d.BMOD.MapModule.Resource.ResourceConfig;
import com.gisluq.runtimeviewer3d.BMOD.MapModule.Running.MapConfigInfo;
import com.gisluq.runtimeviewer3d.R;

import java.text.DecimalFormat;

import gisluq.lib.Util.ToastUtils;


/**
 * 地图组件管理类
 * Created by gis-luq on 2018/4/10.
 */

public class MapManager {

    private static String TAG = "MapManager";

    private Context context;
    private ResourceConfig resourceConfig;

    private ConfigEntity configEntity;

    private String projectPath;

    private ArcGISScene scene;//地图容器

    public MapManager(Context context, ResourceConfig resourceConfig, ConfigEntity ce, String dirPath){
        this.context = context;
        this.resourceConfig = resourceConfig;
        this.configEntity = ce;
        this.projectPath = dirPath;

        this.scene = new ArcGISScene();//初始化
        resourceConfig.sceneView.setScene(scene);

        initMapResource();//初始化配置
    }


    /**
     * 初始化地图资源
     */
    private void initMapResource() {
        try {
            /**设置许可**/
            ArcGISRuntimeEnvironment.setLicense(configEntity.getRuntimrKey());
            String version =ArcGISRuntimeEnvironment.getAPIVersion();
            String lic =ArcGISRuntimeEnvironment.getLicense().getLicenseLevel().name();
            ToastUtils.showShort(context,"ArcGIS Runtime版本:"+version +"; 许可信息:"+lic);
        }catch (Exception e){
            ToastUtils.showShort(context,"ArcGIS Runtime 许可设置异常:"+e.getMessage());
        }

        /**显示高程*/
        final DecimalFormat df = new DecimalFormat("###.#");

        /***不显示Esri LOGO*/
        resourceConfig.sceneView.setAttributionTextVisible(false);

        //设置高程
        String elevat = df.format(resourceConfig.sceneView.getElevation());
        resourceConfig.txtMapScale.setText("高程:"+elevat);

        //根据缩放设置比例尺级别
        resourceConfig.sceneView.addViewpointChangedListener(new ViewpointChangedListener() {
            @Override
            public void viewpointChanged(ViewpointChangedEvent viewpointChangedEvent) {
                String elevat = df.format(resourceConfig.sceneView.getElevation());
                resourceConfig.txtMapScale.setText("高程:"+elevat);
            }
        });


        /**
         * 设置定位相关事件
         */
        MapConfigInfo.dmUserLocationManager = new DMUserLocationManager(context,resourceConfig.sceneView);
        resourceConfig.togbtnLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ToastUtils.showShort(context,"定位开");
                    resourceConfig.togbtnLocation.setBackgroundResource(R.drawable.ic_location_btn_on);
                    MapConfigInfo.dmUserLocationManager.start();
                }else{
                    ToastUtils.showShort(context,"定位关");
                    resourceConfig.togbtnLocation.setBackgroundResource(R.drawable.ic_location_btn_off);
                    MapConfigInfo.dmUserLocationManager.stop();
                    resourceConfig.txtLocation.setText(context.getString(R.string.txt_location_info));
                }
            }
        });

    }

}
