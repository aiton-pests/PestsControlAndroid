package com.aiton.pestscontrolandroid.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.model.SettingModel;
import com.aiton.pestscontrolandroid.data.model.ShpFile;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.service.OssService;
import com.aiton.pestscontrolandroid.service.RetrofitUtil;
import com.aiton.pestscontrolandroid.ui.login.LoginActivity;
import com.aiton.pestscontrolandroid.ui.login.LoginViewModel;
import com.aiton.pestscontrolandroid.ui.me.MeActivity;
import com.aiton.pestscontrolandroid.ui.pests.PestsActivity;
import com.aiton.pestscontrolandroid.ui.pests.PestsViewModel;
import com.aiton.pestscontrolandroid.ui.setting.SettingActivity;
import com.aiton.pestscontrolandroid.ui.setting.SettingViewModel;
import com.aiton.pestscontrolandroid.utils.AMapTiledLayerClass;
import com.aiton.pestscontrolandroid.utils.GoogleMapLayer;
import com.aiton.pestscontrolandroid.utils.SPUtil;
import com.aiton.pestscontrolandroid.utils.TianDiTuTiledLayerClass;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.Geodatabase;
import com.esri.arcgisruntime.data.GeodatabaseFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.layers.OpenStreetMapLayer;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchStyle;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.util.ListenableList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.huawei.agconnect.crash.AGConnectCrash;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import cn.com.qiter.pests.PestsModel;
import cn.com.qiter.pests.Result;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PestsControl";

    private LocationDisplay mLocationDisplay;
    private Spinner mSpinner;
    private final int requestCode4Arcgis = 2;
    private final String[] reqPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
            .ACCESS_COARSE_LOCATION};

    ///////////华为 SCAN KIT ////////////// 开始
    public static final int DEFAULT_VIEW = 0x22;

    private static final int REQUEST_CODE_SCAN = 0X01;
    ///////////华为 SCAN KIT //////////////  结束

    //********* SettingViewModel ***********
    private SettingViewModel settingViewModel;
    private PestsViewModel pestsViewModel;
    private MainViewModel mainViewModel;
    LoginViewModel loginViewModel;
    //################# ArcGIS 相关#####################
    private MapView mMapView;
    private LocationManager mLocationManager;
    private FeatureLayer mFeatureLayer;
    private SketchEditor mainSketchEditor;
    private SketchStyle mainSketchStyle;
    ArcGISMap arcGISMap;
    ShapefileFeatureTable shapefileFeatureTable;
    private GraphicsOverlay mGraphicsOverlay;
    private GraphicsOverlay mLocationGraphicsOverlay;
    //底图
    private String url = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qiter/qiter.tpk";

    //shp文件  /storage/sdcard0/qiter/qiter.shp
    private String shpPath = "/storage/3346-1DEA/qiter/tc.shp";
    ZoomControls zoomControls;
    private boolean isSelect = false;

    /**
     * 增加图层
     *
     * @param mapView
     * @return
     */
    private GraphicsOverlay addGraphicsOverlay(MapView mapView) {
        //create the graphics overlay
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mLocationGraphicsOverlay = new GraphicsOverlay();
        //add the overlay to the map view
        mapView.getGraphicsOverlays().add(graphicsOverlay);
        mapView.getGraphicsOverlays().add(mLocationGraphicsOverlay);
        return graphicsOverlay;
    }


    /**
     * 初始化地图 放大 和缩小
     */
    private void initToolbarZoom() {
        zoomControls = findViewById(R.id.zoomCtrl);
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //放大地图zoomin
                mMapView.setViewpointScaleAsync(mMapView.getMapScale() * 0.5);
            }
        });
        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //缩小地图zoomout
                mMapView.setViewpointScaleAsync(mMapView.getMapScale() * 2);
            }
        });
    }

    //加载shp方式一
    private void openShp() {
        //"/storage/sdcard0/Android/data/com.aiton.pestscontrolandroid/files/qiter/qiter.shp"
        // instantiate shapefile feature table with the path to the .shp file
        shapefileFeatureTable = new ShapefileFeatureTable(shpPath);

        shapefileFeatureTable.loadAsync();
        shapefileFeatureTable.addDoneLoadingListener(() -> {
            //  Log.e(TAG, "openShp: " + shapefileFeatureTable.getInfo().toString() );
            Log.e(TAG, "openShp: " + shapefileFeatureTable.getLoadStatus().toString());
            if (shapefileFeatureTable.getLoadStatus() == LoadStatus.LOADED) {

                //create a feature layer for the shapefile feature table
                FeatureLayer shapefileLayer = new FeatureLayer(shapefileFeatureTable);
                mFeatureLayer = new FeatureLayer(shapefileFeatureTable);
                if (mFeatureLayer.getFullExtent() != null) {
                    mMapView.setViewpointGeometryAsync(mFeatureLayer.getFullExtent());
                } else {
                    mFeatureLayer.addDoneLoadingListener(new Runnable() {
                        @Override
                        public void run() {
                            mMapView.setViewpointGeometryAsync(mFeatureLayer.getFullExtent());
                        }
                    });

                }
                //add the layer to the map.

                SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 5);
                mFeatureLayer.setRenderer(new SimpleRenderer(simpleMarkerSymbol));
                // 添加到地图的业务图层组中

                mMapView.getMap().getOperationalLayers().add(shapefileLayer);
            }
        });
        //queryBySelectFeaturesAsync();
    }

    private void showGeoDatabase() {
        List<ShpFile> shpFiles = settingViewModel.getShpFile4SP();
        ShpFile shpFile = null;
        for (ShpFile sf :
                shpFiles) {
            if (sf.isSelected()) {
                shpFile = sf;
            }
        }
        if (shpFile == null) {
            Toast.makeText(this, getResources().getString(R.string.cant_load_geodatabase_file), Toast.LENGTH_SHORT).show();
            mainViewModel.setLoadedShp(false);
            return;
        }
        //"/sdcard/Android/data/com.aiton.pestscontrolandroid/files/geodatabase/XiangAn/data/xa.geodatabase"
        final Geodatabase geodatabase = new Geodatabase(shpFile.getUrl());
        geodatabase.loadAsync();
        geodatabase.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
//                QueryParameters queryParameters=new QueryParameters();
//                queryParameters.setWhereClause(sql);

                mMapView.getMap().getOperationalLayers().clear();

                List<GeodatabaseFeatureTable> geodatabaseFeatureTables = geodatabase.getGeodatabaseFeatureTables();
                if (geodatabaseFeatureTables == null || geodatabaseFeatureTables.size() == 0) {
                    return;
                }
//                 mFeatureLayer=new FeatureLayer(geodatabaseFeatureTables.get(code));
                for (GeodatabaseFeatureTable gdbTable : geodatabaseFeatureTables) {
                    mFeatureLayer = new FeatureLayer(gdbTable);
                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 1);
                    SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
                    mFeatureLayer.setRenderer(new SimpleRenderer(fillSymbol));
                    mFeatureLayer.setOpacity(0.2f);
                    mFeatureLayer.setVisible(true);// 隐藏
                    mMapView.getMap().getOperationalLayers().add(mFeatureLayer);
                }
                queryByIdentify();

                mainViewModel.setLoadedShp(true);
            }
        });

    }

    private void showServiceFeatureTable() {
        String sample_service_url = "http://192.168.0.55:6080/arcgis/rest/services/XAMapService/MapServer/0";
        ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable(sample_service_url);
        mFeatureLayer = new FeatureLayer(serviceFeatureTable);
        mFeatureLayer.setOpacity(0.8f);
        //重写renderer渲染器
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 1);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
        mFeatureLayer.setRenderer(new SimpleRenderer(fillSymbol));
        mMapView.getMap().getOperationalLayers().add(mFeatureLayer);

        // queryByIdentify();

    }

    private void showShapefile() {
        // shp文件存在本地的路径
        String shpPath = getApplicationContext().getExternalFilesDir("/qiter/tc.shp").getAbsolutePath();
        //String shpPath = context.getExternalFilesDir("/shp").getPath()+ File.separator + "检查标注.shp";/sdcard/Android/data/com.aiton.pestscontrolandroid/files/qiter/翔安区矢量图.shp
///sdcard/Android/data/com.aiton.pestscontrolandroid/files/qiter/Subdivisions.shp
        mMapView.setAttributionTextVisible(false);

        File shpFile = new File(shpPath);
        if (shpFile.exists()) {// 判断是否存储这个文件
            shapefileFeatureTable = new ShapefileFeatureTable(shpPath);
            shapefileFeatureTable.loadAsync();

            shapefileFeatureTable.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    GeometryType geoType = shapefileFeatureTable.getGeometryType();
                    String name = shapefileFeatureTable.getTableName();
                    Log.e(TAG, "run: " + shapefileFeatureTable.getPath());
                    Log.e(TAG, "run: " + shapefileFeatureTable.getLoadStatus().toString());
                    mFeatureLayer = new FeatureLayer(shapefileFeatureTable);
                    if (mFeatureLayer.getFullExtent() != null) {
                        mMapView.setViewpointGeometryAsync(mFeatureLayer.getFullExtent());
                    } else {
                        mFeatureLayer.addDoneLoadingListener(new Runnable() {
                            @Override
                            public void run() {
                                mMapView.setViewpointGeometryAsync(mFeatureLayer.getFullExtent());
                            }
                        });

                    }
                    // create the Symbol
                    SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
                    SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);

                    // create the Renderer
                    SimpleRenderer renderer = new SimpleRenderer(fillSymbol);

                    // set the Renderer on the Layer
                    mFeatureLayer.setRenderer(renderer);

                    // add the feature layer to the map
                    // 设置渲染图层添加到mapView
                    mMapView.getMap().getOperationalLayers().add(mFeatureLayer);

                }
            });

        } else {
            // 创建
            shpFile.mkdirs();
        }
        // 设置Shapefile文件的渲染方式
        // 点
        // SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 5);
        // 线
        // SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
        // 面
        // SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
        // 渲染线和面
        // SimpleRenderer renderer = new SimpleRenderer(fillSymbol);

        // SimpleRenderer renderer = new SimpleRenderer(simpleMarkerSymbol);

        // mFeatureLayer.setRenderer(renderer);
        //queryByIdentify();

    }

    /**
     * 查询shp方式mFeatureLayer
     */
    public void queryByIdentify() {

        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
//                mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
//                if (!mLocationDisplay.isStarted())
//                    mLocationDisplay.startAsync();
                LocationDataSource.Location location = mLocationDisplay.getLocation();
                mFeatureLayer.clearSelection();
                Point screenPoint = new Point(Math.round(e.getX()), Math.round(e.getY()));
                int tolerance = 10;

                ListenableFuture<IdentifyLayerResult> identifyLayerResultFuture = mMapView
                        .identifyLayerAsync(mFeatureLayer, screenPoint, tolerance, false, -1);

                identifyLayerResultFuture.addDoneListener(() -> {
                    try {
                        IdentifyLayerResult identifyLayerResult = identifyLayerResultFuture.get();
                        int counter = 0;
                        List<Map<String, String>> maps = new ArrayList<>();
                        for (GeoElement element : identifyLayerResult.getElements()) {
                            if (element instanceof Feature) {
                                Feature feature = (Feature) element;


                                Map<String, Object> attributes = feature.getAttributes();
                                Log.e(TAG, "run: " + attributes.toString());
                                Map<String, Object> hm = (Map<String, Object>) attributes;
                                Map<String, String> map = new HashMap<>();
                                Object jyxzcname = hm.get(AppConstance.JYXZCNAME);
                                Object cgqname = hm.get(AppConstance.CGQNAME);
                                Object dbh = hm.get(AppConstance.DBH);
                                Object xbh = hm.get(AppConstance.XBH);
                                map.put(AppConstance.JYXZCNAME, jyxzcname.toString());
                                map.put(AppConstance.CGQNAME, cgqname.toString());
                                map.put(AppConstance.DBH, dbh.toString());
                                map.put(AppConstance.XBH, xbh.toString());
                                map.put(AppConstance.LONGITUDE, String.valueOf(location.getPosition().getX()));
                                map.put(AppConstance.LATIDUTE, String.valueOf(location.getPosition().getY()));
                                maps.add(map);
                                mFeatureLayer.selectFeature(feature);
                                counter++;
                                Log.d(TAG, "Selection #: " + counter + " Table name: " + feature.getFeatureTable().getFields().toString());
                                List<Field> fieldList = feature.getFeatureTable().getFields();
                                Log.e(TAG, "onSingleTapConfirmed: " + feature.getAttributes().toString());
                                for (Field f :
                                        fieldList) {
                                    //  Log.e(TAG, "onSingleTapConfirmed: " + f.toString() );
                                    // Log.e(TAG, "onSingleTapConfirmed: " + f.getName());
//                                    Log.e(TAG, "onSingleTapConfirmed: " + f.getDomain().toString() );
//                                    Log.e(TAG, "onSingleTapConfirmed: " + f.getAlias() );
//                                    Log.e(TAG, "onSingleTapConfirmed: " + f.getLength() );
                                }
                            }
                        }
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.feature_selected) + counter + ";" + maps.toString(), Toast.LENGTH_LONG).show();
                    } catch (Exception ex) {
                        Log.e(TAG, "Select feature failed: " + ex.getMessage());
                    }
                });

                return true;
            }
        });
    }


    /**
     * 查询shp方式1:selectFeaturesAsync（查询不出数据）
     */
    public void queryBySelectFeaturesAsync() {
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                shapefileFeatureTable.getFeatureLayer().clearSelection();

                final com.esri.arcgisruntime.geometry.Point clickPoint = mMapView.screenToLocation(new Point(Math.round(e.getX()), Math.round(e.getY())));
                int tolerance = 10;
                double mapTolerance = tolerance * mMapView.getUnitsPerDensityIndependentPixel();
                SpatialReference spatialReference = mMapView.getSpatialReference();
                Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance,
                        clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, spatialReference);
                QueryParameters query = new QueryParameters();
                query.setGeometry(envelope);
                query.setSpatialRelationship(QueryParameters.SpatialRelationship.WITHIN);
                final ListenableFuture<FeatureQueryResult> future = mFeatureLayer.selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);
                future.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FeatureQueryResult result = future.get();
                            //mFeatureLayer.getFeatureTable().deleteFeaturesAsync(result);
                            Iterator<Feature> iterator = result.iterator();
                            int counter = 0;
                            while (iterator.hasNext()) {
                                counter++;
                                Feature feature = iterator.next();

                                Map<String, Object> attributes = feature.getAttributes();
                                for (String key : attributes.keySet()) {
                                    Log.e("xyh" + key, String.valueOf(attributes.get(key)));
                                }

                                //高亮显示选中区域
                                mFeatureLayer.selectFeature(feature);
                                Geometry geometry = feature.getGeometry();
                                mMapView.setViewpointGeometryAsync(geometry.getExtent());

                                //也可以通过添加graphic高亮显示选中区域
                                //
                                //                                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
                                //                                SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.RED, lineSymbol);
                                //
                                //                                if (mGraphicsOverlay != null) {
                                //                                    ListenableList<Graphic> graphics = mGraphicsOverlay.getGraphics();
                                //                                    if (graphics.size() > 0) {
                                //                                        graphics.removeAll(graphics);
                                //                                    }
                                //                                }
                                //                                Graphic graphic = new Graphic(geometry, fillSymbol);
                                //                                mGraphicsOverlay.getGraphics().add(graphic);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    /**
     * 调用 根据经纬度点，查询要素数据。
     * 再调用 SCAN KIT
     *
     * @param location
     */
    public void queryByPointloglat(LocationDataSource.Location location) {
        //android.graphics.Point screenPoint = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
        //com.esri.arcgisruntime.geometry.Point clickPoint = mMapView.screenToLocation(screenPoint);

        com.esri.arcgisruntime.geometry.Point clickPoint = location.getPosition();
        int tolerance = 1;
        double mapTolerance = tolerance * mMapView.getUnitsPerDensityIndependentPixel();
        SpatialReference spatialReference = mMapView.getSpatialReference();
        Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance,
                clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, spatialReference);
//投影坐标转换成经纬度
        QueryParameters query = new QueryParameters();
        query.setGeometry(envelope);// 设置空间几何对象
        query.setReturnGeometry(true);
        query.setSpatialRelationship(QueryParameters.SpatialRelationship.WITHIN);
        final ListenableFuture<FeatureQueryResult> future = mFeatureLayer.selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);
        future.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = future.get();
                    //mFeatureLayer.getFeatureTable().deleteFeaturesAsync(result);
                    Iterator<Feature> iterator = result.iterator();
                    Integer test = SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.ISTEST, Integer.class);
                    // 1 表示生产环境（发布） ； 0 表示测试环境
                    if (test.compareTo(0) != 0) {
                        int counter = 0;
                        while (iterator.hasNext()) {
                            counter++;
                            mainViewModel.setSelectedFeature(counter);
                            Feature feature = iterator.next();

                            Map<String, Object> attributes = feature.getAttributes();
                            Log.e(TAG, "run: " + attributes.toString());
                            Map<String, Object> hm = (Map<String, Object>) attributes;
                            Object jyxzcname = hm.get(AppConstance.JYXZCNAME);
                            Object cgqname = hm.get(AppConstance.CGQNAME);
                            Object dbh = hm.get(AppConstance.DBH);
                            Object xbh = hm.get(AppConstance.XBH);
                            Map<String, String> map = new HashMap<>();
                            map.put(AppConstance.JYXZCNAME, jyxzcname.toString());
                            map.put(AppConstance.CGQNAME, cgqname.toString());
                            map.put(AppConstance.DBH, dbh.toString());
                            map.put(AppConstance.XBH, xbh.toString());
                            map.put(AppConstance.LONGITUDE, String.valueOf(location.getPosition().getX()));
                            map.put(AppConstance.LATIDUTE, String.valueOf(location.getPosition().getY()));
                            mainViewModel.setMap(map);
                            Log.e(TAG, "run: " + mainViewModel.getMap().toString());
                            for (String key : attributes.keySet()) {
                                Log.e("xyh" + key, String.valueOf(attributes.get(key)));
                            }
                            if (mainViewModel.getMap().size() > 0) {
                                /////////////////华为SCAN KIT ////////////////// 调用扫码功能
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                                            DEFAULT_VIEW);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.point_message), Toast.LENGTH_SHORT).show();
                            }
                            //高亮显示选中区域
                            mFeatureLayer.selectFeature(feature);
                            Geometry geometry = feature.getGeometry();
                            mMapView.setViewpointGeometryAsync(geometry.getExtent());

                        }

                    } else {
                        Map<String, String> map = new HashMap<>();
                        map.put(AppConstance.JYXZCNAME, "新店镇");
                        map.put(AppConstance.CGQNAME, "新店社区居委会");
                        map.put(AppConstance.DBH, "01");
                        map.put(AppConstance.XBH, "811");
                        map.put(AppConstance.LONGITUDE, "118.23904583");
                        map.put(AppConstance.LATIDUTE, "24.60177837");
                        mainViewModel.setMap(map);
                        /////////////////华为SCAN KIT ////////////////// 调用扫码功能
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                                    DEFAULT_VIEW);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 查询shp方式2:queryFeaturesAsync(查询不出数据)
     */
    public void queryByQueryFeaturesAsync() {
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                android.graphics.Point screenPoint = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
                com.esri.arcgisruntime.geometry.Point clickPoint = mMapView.screenToLocation(screenPoint);

                QueryParameters query = new QueryParameters();
                query.setGeometry(clickPoint);// 设置空间几何对象
                FeatureTable mTable = mFeatureLayer.getFeatureTable();//得到查询属性表
                final ListenableFuture<FeatureQueryResult> featureQueryResult = mTable.queryFeaturesAsync(query);
                featureQueryResult.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FeatureQueryResult result = featureQueryResult.get();
                            Iterator<Feature> iterator = result.iterator();

                            int counter = 0;
                            while (iterator.hasNext()) {
                                counter++;
                                Feature feature = iterator.next();

                                Map<String, Object> attributes = feature.getAttributes();
                                for (String key : attributes.keySet()) {
                                    Log.e("xyh" + key, String.valueOf(attributes.get(key)));
                                }


                                //高亮显示选中区域
                                Geometry geometry = feature.getGeometry();
                                mMapView.setViewpointGeometryAsync(geometry.getExtent());

                                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
                                SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.RED, lineSymbol);

                                if (mGraphicsOverlay != null) {
                                    ListenableList<Graphic> graphics = mGraphicsOverlay.getGraphics();
                                    if (graphics.size() > 0) {
                                        graphics.removeAll(graphics);
                                    }
                                }
                                Graphic graphic = new Graphic(geometry, fillSymbol);
                                mGraphicsOverlay.getGraphics().add(graphic);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return super.onSingleTapConfirmed(e);
            }
        });
    }


    public void startDrawing() {
        try {
            mainSketchEditor = new SketchEditor();
            mainSketchStyle = new SketchStyle();
            mainSketchEditor.setSketchStyle(mainSketchStyle);
            mMapView.setSketchEditor(mainSketchEditor);

            mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (isSelect == false) {
                        com.esri.arcgisruntime.geometry.Point clickPoint = mMapView.screenToLocation(new Point(Math.round(e.getX()), Math.round(e.getY())));
                        int tolerance = 1;
                        double mapTolerance = tolerance * mMapView.getUnitsPerDensityIndependentPixel();
                        Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance,
                                clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, mMapView.getSpatialReference());
                        QueryParameters query = new QueryParameters();
                        query.setGeometry(envelope);
                        query.setSpatialRelationship(QueryParameters.SpatialRelationship.WITHIN);
                        final ListenableFuture<FeatureQueryResult> future = mFeatureLayer.selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);

                        future.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    FeatureQueryResult result = future.get();
                                    //mainShapefileLayer.getFeatureTable().deleteFeaturesAsync(result);
                                    Iterator<Feature> iterator = result.iterator();
                                    Feature feature;

                                    int counter = 0;
                                    while (iterator.hasNext()) {
                                        feature = iterator.next();

                                        Geometry geometry = feature.getGeometry();
                                        mMapView.setViewpointGeometryAsync(geometry.getExtent());

                                        counter++;

                                    }
                                } catch (Exception e) {
                                    e.getCause();
                                }
                            }
                        });
                    }

                    return super.onSingleTapConfirmed(e);
                }
            });
        } catch (Exception e) {
            e.getCause();
        }
    }

    private LocationListener locationListener = new LocationListener() {
        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            markLocation(location);
        }

        /**
         * 状态改变时调用
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    showToast("当前GPS状态为可见状态");
                    Log.i("TAG", "当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    showToast("当前GPS状态为服务区外状态");
                    Log.i("TAG", "当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    showToast("当前GPS状态为暂停服务状态");
                    Log.i("TAG", "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            showToast("GPS打开");
            int requestPermissionsCode = 2;
            String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(MainActivity.this, requestPermissions, requestPermissionsCode);
                return;
            }
            Location location = mLocationManager.getLastKnownLocation(provider);
            markLocation(location);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            showToast("GPS已关闭");
        }
    };

    private void markLocation(Location location) {
        if (location != null) {
            Log.i("TAG", "时间：" + location.getTime());
            Log.i("TAG", "经度：" + location.getLongitude());
            Log.i("TAG", "纬度：" + location.getLatitude());
            Log.i("TAG", "海拔：" + location.getAltitude());
            double locx = location.getLongitude();
            double locy = location.getLatitude();
            setExtentMap(location);
            ShowPointOnMap(locx, locy);
        }
    }

    private void ShowPointOnMap(double locx, double locy) {
        mLocationGraphicsOverlay.getGraphics().clear();
        Graphic graphic = new Graphic(locy, locx);
        com.esri.arcgisruntime.geometry.Point point = new com.esri.arcgisruntime.geometry.Point(locx, locy, SpatialReferences.getWgs84());
        SimpleMarkerSymbol pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.rgb(226, 119, 40), 10.0f);
        pointSymbol.setOutline(new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLUE, 2.0f));
        Graphic pointGraphic = new Graphic(point, pointSymbol);
        mLocationGraphicsOverlay.getGraphics().add(pointGraphic);
    }

    private void setExtentMap(Location location) {

        mMapView.setViewpoint(new Viewpoint(location.getLatitude(), location.getLongitude(), 100000));
//        mMapView.setViewpointCenterAsync(new com.esri.arcgisruntime.geometry.Point(location.getLongitude(), location.getLatitude()), 90000);
    }


    // %%%%%%%%%%%%    Android 主界面按钮 %%%%%%%%%%%%%%%%%
    private FloatingActionButton fabPests, fabLocation;
    Toast toast;


    ////////////////////////华为 SCAN KIT         /////////////////// 权限 开始
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || grantResults == null || grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
// If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == requestCode4Arcgis) {
            // Location permission was granted. This would have been triggered in response to failing to start the
            // LocationDisplay, so try starting this again.
            mLocationDisplay.startAsync();
        } else {
            // If permission was denied, show toast to inform user what was chosen. If LocationDisplay is started again,
            // request permission UX will be shown again, option should be shown to allow never showing the UX again.
            // Alternative would be to disable functionality so request is not shown again.
            //Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();

            // Update UI to reflect that the location display did not actually start
            //mSpinner.setSelection(0, true);
        }
        if (requestCode == DEFAULT_VIEW) {
            //start ScankitActivity for scanning barcode
            ScanUtil.startScan(MainActivity.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());
        }

    }

    /**
     * 华为 SCAN 完成后 返回内容
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //receive result after your activity finished scanning
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        // Obtain the return value of HmsScan from the value returned by the onActivityResult method by using ScanUtil.RESULT as the key value.
        if (requestCode == REQUEST_CODE_SCAN) {
            Object obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj instanceof HmsScan) {
                if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {
                    Toast.makeText(this, ((HmsScan) obj).getOriginalValue(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, PestsActivity.class);
                    PestsModel pests = new PestsModel();
                    String qrcode = ((HmsScan) obj).getOriginalValue();
                    String resultQrcode = StrUtil.subAfter(qrcode, "=", true);
                    pests.setQrcode(resultQrcode);
                    HashMap<String, String> map = (HashMap<String, String>) mainViewModel.getMap();
//                    Bundle nBundle = new Bundle();
                    intent.putExtra(AppConstance.PESTSMODEL, pests);
                    intent.putExtra(AppConstance.FEATURE_ATTRIBUTE_MAP, map);
                    startActivity(intent);
                }
                return;
            }
        }
    }

    ////////////////////////华为 SCAN KIT         /////////////////// 权限 结束
    // Basemap basemap, aMapBasemap, googleBasemap, arcgisBasemap, osmBasemap, geoBasemap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).setData(AppConstance.ISTEST, 1);
        ArcGISRuntimeEnvironment.setLicense(AppConstance.API_KEY);
        settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        pestsViewModel = new ViewModelProvider(this).get(PestsViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        mMapView = findViewById(R.id.mapView);
        fabPests = findViewById(R.id.fab_pests);
        fabLocation = findViewById(R.id.fab_location);
//要定位在地图中的位置，需要知道当前位置，而当前位置有Location对象决定，
        //但是，Location对象又需要LocationManager对象来创建。
        //创建LocationManager的唯一方法
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mGraphicsOverlay = addGraphicsOverlay(mMapView);
        initToolbarZoom();
        initFAB();
        // set the map to the map view

        if (arcGISMap == null) {
            setMap();
        }
        // get the MapView's LocationDisplay
        mLocationDisplay = mMapView.getLocationDisplay();
        // Listen to changes in the status of the location data source.
        mLocationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {

            // If LocationDisplay started OK, then continue.
            if (dataSourceStatusChangedEvent.isStarted())
                return;

            // No error is reported, then continue.
            if (dataSourceStatusChangedEvent.getError() == null)
                return;

            // If an error is found, handle the failure to start.
            // Check permissions to see if failure may be due to lack of permissions.
            boolean permissionCheck1 = ContextCompat.checkSelfPermission(this, reqPermissions[0]) ==
                    PackageManager.PERMISSION_GRANTED;
            boolean permissionCheck2 = ContextCompat.checkSelfPermission(this, reqPermissions[1]) ==
                    PackageManager.PERMISSION_GRANTED;

            if (!(permissionCheck1 && permissionCheck2)) {
                // If permissions are not already granted, request permission from the user.
                ActivityCompat.requestPermissions(this, reqPermissions, requestCode4Arcgis);
            } else {
                // Report other unknown failure types to the user - for example, location services may not
                // be enabled on the device.
                String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                        .getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                // Update UI to reflect that the location display did not actually start
                mSpinner.setSelection(0, true);
            }
        });

        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        if (!mLocationDisplay.isStarted())
            mLocationDisplay.startAsync();


        showGeoDatabase();
        pestsViewModel.findAll().observe(this, new Observer<List<Pests>>() {
            @Override
            public void onChanged(List<Pests> pests) {
                Log.e(TAG, "pestsViewModel.findAll().observe: " + pests.toString());
            }
        });
        /**
         * 观察防治数据，上传到服务器的方法
         */
        pestsViewModel.findAll(true).observe(this, new Observer<List<Pests>>() {
            @Override
            public void onChanged(List<Pests> pests) {

            }
        });
        pestsViewModel.findAll(false).observe(this, new Observer<List<Pests>>() {
            @Override
            public void onChanged(List<Pests> pests) {
                if (pests != null && !pests.isEmpty())
                    Log.e(TAG, "手机端数据新增加一条: " + pests.toString());
            }
        });

    }

    public String ossUpload(File file) {
        //初始化OssService类，参数分别是Content，accessKeyId，accessKeySecret，endpoint，bucketName（后4个参数是您自己阿里云Oss中参数）
        OssService ossService = new OssService(getApplication(), AppConstance.ACCESS_KEY_ID, AppConstance.ACCESS_KEY_SECRET, AppConstance.ENDPOINT, AppConstance.BUCKETNAME);
//初始化OSSClient
        ossService.initOSSClient();
        String filePath = new DateTime().toString("yyyy/MM/dd");
        String appPath = "app/" + filePath;
        String appFilePath = appPath + "/" + file.getName();
//开始上传，参数分别为content，上传的文件名filename，上传的文件路径filePath
        ossService.beginupload(getApplication().getApplicationContext(), appFilePath, file.getAbsolutePath());
//上传的进度回调
        ossService.setProgressCallback(new OssService.ProgressCallback() {
            @Override
            public void onProgressCallback(double progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "run: " + progress);
                    }
                });
            }
        });
        return appFilePath;
    }

    private void setMap() {
        arcGISMap = new ArcGISMap();
//        SettingModel model = settingViewModel.getSettingModel4SP();
        if (true) {
            Basemap basemap = new Basemap();
            basemap.setName(AppConstance.TIANDI_MAP);
            WebTiledLayer webTiledLayer = TianDiTuTiledLayerClass.CreateTianDiTuTiledLayer(TianDiTuTiledLayerClass.LayerType.TIANDITU_IMAGE_2000);
            webTiledLayer.loadAsync();
            WebTiledLayer webTiledLayer1 = TianDiTuTiledLayerClass.CreateTianDiTuTiledLayer(TianDiTuTiledLayerClass.LayerType.TIANDITU_IMAGE_2000_LABLE);
            webTiledLayer1.loadAsync();
            basemap.getBaseLayers().add(webTiledLayer);
            basemap.getBaseLayers().add(webTiledLayer1);
            arcGISMap.setBasemap(basemap);
        }
//        if (model.getGeoShow()) {
//            Basemap geoBasemap = new Basemap();
//            geoBasemap.setName(AppConstance.GEO_MAP);
//            String theURLString = "http://map.geoq.cn/arcgis/rest/services/ChinaOnlineCommunity/MapServer";
//            ArcGISTiledLayer mainArcGISTiledLayer = new ArcGISTiledLayer(theURLString);
//            geoBasemap.getBaseLayers().add(mainArcGISTiledLayer);
//            arcGISMap.setBasemap(geoBasemap);
//        }
//
//        if (model.getGoogleShow()) {
//            Basemap googleBasemap = new Basemap();
//            googleBasemap.setName(AppConstance.GOOGLE_MAP);
//            WebTiledLayer googleLayer = null;
//            try {
//                googleLayer = GoogleMapLayer.CreateGoogleLayer(GoogleMapLayer.MapType.IMAGE);
//                googleLayer.loadAsync();
//
//            } catch (Exception e) {
//                Toast.makeText(this, "Google地图加载失败，请关闭", Toast.LENGTH_SHORT).show();
//            }
//            // 加载谷歌地图
//            try {
//
//                googleBasemap.getBaseLayers().add(googleLayer);
////                boolean contains = arcGISMap.getBasemap().getName().equals(AppConstance.GOOGLE_MAP);
////                if (!contains){
//                arcGISMap.setBasemap(googleBasemap);
////                }
//            } catch (Exception e) {
//                Toast.makeText(this, "Google地图加载失败，请关闭", Toast.LENGTH_SHORT).show();
//            }
//        }
//        if (model.getaMapShow()) {
//            // 加载高德地图
//            Basemap aMapBasemap = new Basemap();
//            aMapBasemap.setName(AppConstance.AMAP_MAP);
//            WebTiledLayer aMap = AMapTiledLayerClass.CreateAMapTiledLayer(AMapTiledLayerClass.LayerType.AMAP_IMAGE);
//            aMap.loadAsync();
//            aMapBasemap.getBaseLayers().add(aMap);
//            arcGISMap.setBasemap(aMapBasemap);
//        }
//        if (model.getOsmShow()) {
//            //加载OpenStreetMap底图
//            Basemap osmBasemap = new Basemap();
//            osmBasemap.setName(AppConstance.OSM_MAP);
//            OpenStreetMapLayer streetlayer = new OpenStreetMapLayer();
//            osmBasemap.getBaseLayers().add(streetlayer);
//            arcGISMap.setBasemap(osmBasemap);
//        }
//        if (model.getArcgisShow()) {
//            Basemap arcgisBasemap = new Basemap();
//            arcgisBasemap.setName(AppConstance.ARCGIS_MAP);
//            String arcurl = "http://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer";
//            ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer(arcurl);
//            arcgisBasemap.getBaseLayers().add(arcGISTiledLayer);
//            arcGISMap.setBasemap(arcgisBasemap);
//        }
        mMapView.setMap(arcGISMap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_search:
                break;
            case R.id.menu_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_our:
                Intent intent1 = new Intent(this, MeActivity.class);
                startActivity(intent1);
                break;
            case R.id.menu_updateserver:
                updateServer(false);
                break;
//            case R.id.menu_loadbasemap:
//                setMap();
//                break;
            case R.id.menu_loadshp:
                showGeoDatabase();
                break;
            case R.id.menu_update_againr:
                updateServer(true);
                break;
//            case R.id.menu_logout:
//                //logout();
//                break;
            case R.id.menu_delete:
                //                showShapefile();
//                openShp();
//                showServiceFeatureTable();
                deletePestsByUpdate(true);
                Basemap basemap = arcGISMap.getBasemap();
                LayerList sic = basemap.getBaseLayers();
                Log.e(TAG, "onOptionsItemSelected: " + sic.size());
                LayerList si = mMapView.getMap().getOperationalLayers();
                Log.e(TAG, "onOptionsItemSelected: " + si.size());
                //   Toast.makeText(this, mMapView.getSpatialReference().getWKText() + " --- " + mMapView.getSpatialReference().toString(), Toast.LENGTH_LONG).show();
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadServer() {
        RetrofitUtil.getInstance().getPestsService().findAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.rxjava3.core.Observer<Result>() {
                    private Disposable disposable;

                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Result result) {
                        if (result.getSuccess()) {
                            Log.e(TAG, "onNext: " + result.toString());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        disposable.dispose();//注销
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void deletePestsByUpdate(boolean isUpdate) {
        Pests[] pests = pestsViewModel.findAllObject(isUpdate);
        for (Pests p :
                pests) {
            pestsViewModel.delete(p);
        }
    }

    private void updateServer(boolean isUpdate) {
        Pests[] pests = pestsViewModel.findAllObject(isUpdate);
        for (Pests p :
                pests) {
            p.setUpdateServer(true);
            pestsViewModel.update(p);

            Log.e(TAG, "服务器数据上传一条: " + pests.toString());
            PestsModel pestsModel = new PestsModel();
            pestsModel.setQrcode(p.getQrcode());
            pestsModel.setAppId(p.getId());
            pestsModel.setBagNumber(p.getBagNumber());
            pestsModel.setDb(p.getDb());
            pestsModel.setXb(p.getXb());
            pestsModel.setDeviceId(p.getDeviceId());
            if (p.getFellPic() != null) {
                File fellpic = new File(p.getFellPic());
                String filepath = ossUpload(fellpic);
                pestsModel.setFellPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                pestsModel.setFellPic("");
            }

            if (p.getStumpPic() != null) {
                File stumpPic = new File(p.getStumpPic());
                String filepath = ossUpload(stumpPic);
                pestsModel.setStumpPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                pestsModel.setStumpPic("");
            }
            if (p.getFinishPic() != null) {
                File finishPic = new File(p.getFinishPic());
                String filepath = ossUpload(finishPic);
                pestsModel.setFinishPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                pestsModel.setFinishPic("");
            }
            pestsModel.setLatitude(p.getLatitude());
            pestsModel.setLongitude(p.getLongitude());
            pestsModel.setOperator(p.getOperator());
            pestsModel.setPestsType(p.getPestsType());
            pestsModel.setPositionError(p.getPositionError());
            pestsModel.setTown(p.getTown());
            pestsModel.setTreeWalk(p.getTreeWalk());
            pestsModel.setUserId(p.getUserId());
            pestsModel.setVillage(p.getVillage());
            pestsModel.setStime(p.getStime());
            mainViewModel.uploadServer(pestsModel);

            //pestsViewModel.deleteWithId(s.getId());

            Log.e(TAG, "updateServer: " + p.toString());
        }
    }

    private void setInitExtentMap() {
        //判断GPS是否正常启动
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            //返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }
        int requestPermissionsCode = 2;
        String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this, requestPermissions, requestPermissionsCode);
            return;
        }
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        markLocation(location);
    }

    private void initFAB() {
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // setInitExtentMap();
                mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
                if (!mLocationDisplay.isStarted())
                    mLocationDisplay.startAsync();

            }
        });


        fabPests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mainViewModel.isLoadedShp()) {
                    //判断GPS是否正常启动
//                    if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//
//                        //返回开启GPS导航设置界面
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivityForResult(intent, 0);
//                        return;
//                    }
//                    int requestPermissionsCode = 2;
//                    String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
//                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        ActivityCompat.requestPermissions(MainActivity.this, requestPermissions, requestPermissionsCode);
//                        return;
//                    }
//                    Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
                    if (!mLocationDisplay.isStarted())
                        mLocationDisplay.startAsync();
                    LocationDataSource.Location location = mLocationDisplay.getLocation();
                    //markLocation(location);
                    queryByPointloglat(location);
                    // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                    // AGConnectCrash.getInstance().testIt(MainActivity.this);

                }
            }
        });

    }

    private void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }


    @Override
    public void onPause() {
        mMapView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }

}