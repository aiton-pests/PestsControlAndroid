package com.aiton.pestscontrolandroid.ui.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.widget.ZoomControls;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.model.ShpFile;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.data.persistence.Trap;
import com.aiton.pestscontrolandroid.service.AutoUpdater;
import com.aiton.pestscontrolandroid.service.PestsWork;
import com.aiton.pestscontrolandroid.service.TrapWork;
import com.aiton.pestscontrolandroid.ui.login.LoginViewModel;
import com.aiton.pestscontrolandroid.ui.me.MeActivity;
import com.aiton.pestscontrolandroid.ui.me.MeViewModel;
import com.aiton.pestscontrolandroid.ui.myjob.MyJobActivity;
import com.aiton.pestscontrolandroid.ui.pests.PestsActivity;
import com.aiton.pestscontrolandroid.ui.pests.PestsViewModel;
import com.aiton.pestscontrolandroid.ui.setting.SettingActivity;
import com.aiton.pestscontrolandroid.ui.setting.SettingViewModel;
import com.aiton.pestscontrolandroid.ui.trap.TrapActivity;
import com.aiton.pestscontrolandroid.ui.trap.TrapViewModel;
import com.aiton.pestscontrolandroid.ui.trap.TrayJobActivity;
import com.aiton.pestscontrolandroid.utils.LocationUtils;
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
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchStyle;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.util.ListenableList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.com.qiter.common.vo.PestsControlModel;
import cn.com.qiter.common.vo.PestsTrapModel;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private LocationDisplay mLocationDisplay;
    private final int requestCode4Arcgis = 2;

    ///////////?????? SCAN KIT ////////////// ??????
    public static final int DEFAULT_VIEW = 0x22;

    private static final int REQUEST_CODE_SCAN = 0X01;
    ///////////?????? SCAN KIT //////////////  ??????

    //********* SettingViewModel ***********
    private SettingViewModel settingViewModel;
    private PestsViewModel pestsViewModel;
    private TrapViewModel trapViewModel;
    private MainViewModel mainViewModel;
    LoginViewModel loginViewModel;
    private MeViewModel meViewModel;
    //################# ArcGIS ??????#####################
    private MapView mMapView;
    private FeatureLayer mFeatureLayer;
    private SketchEditor mainSketchEditor;
    private SketchStyle mainSketchStyle;
    ArcGISMap arcGISMap;
    ShapefileFeatureTable shapefileFeatureTable;
    private GraphicsOverlay mGraphicsOverlay;
    private GraphicsOverlay mLocationGraphicsOverlay;
    //??????
    private String url = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qiter/qiter.tpk";

    ZoomControls zoomControls;
    private boolean isSelect = false;


    WorkManager workmanager;
    /**
     * ????????????
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
     * ??????????????? ?????? ?????????
     */
    private void initToolbarZoom() {
        zoomControls = findViewById(R.id.zoomCtrl);
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //????????????zoomin
                mMapView.setViewpointScaleAsync(mMapView.getMapScale() * 0.5);
            }
        });
        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //????????????zoomout
                mMapView.setViewpointScaleAsync(mMapView.getMapScale() * 2);
            }
        });
    }

    //??????shp?????????
    private void openShp() {
        //"/storage/sdcard0/Android/data/com.aiton.pestscontrolandroid/files/qiter/qiter.shp"
        // instantiate shapefile feature table with the path to the .shp file

        //shp??????  /storage/sdcard0/qiter/qiter.shp
        String shpPath = "/storage/3346-1DEA/qiter/tc.shp";
        shapefileFeatureTable = new ShapefileFeatureTable(shpPath);

        shapefileFeatureTable.loadAsync();
        shapefileFeatureTable.addDoneLoadingListener(() -> {
            //  Log.e(TAG, "openShp: " + shapefileFeatureTable.getInfo().toString() );
//            Log.e(TAG, "openShp: " + shapefileFeatureTable.getLoadStatus().toString());
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
                // ????????????????????????????????????

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
                    mFeatureLayer.setVisible(true);// ??????
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
        //??????renderer?????????
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK, 1);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
        mFeatureLayer.setRenderer(new SimpleRenderer(fillSymbol));
        mMapView.getMap().getOperationalLayers().add(mFeatureLayer);

        // queryByIdentify();

    }

    private void showShapefile() {
        // shp???????????????????????????
        String shpPath = getApplicationContext().getExternalFilesDir("/qiter/tc.shp").getAbsolutePath();
        //String shpPath = context.getExternalFilesDir("/shp").getPath()+ File.separator + "????????????.shp";/sdcard/Android/data/com.aiton.pestscontrolandroid/files/qiter/??????????????????.shp
///sdcard/Android/data/com.aiton.pestscontrolandroid/files/qiter/Subdivisions.shp
        mMapView.setAttributionTextVisible(false);

        File shpFile = new File(shpPath);
        if (shpFile.exists()) {// ??????????????????????????????
            shapefileFeatureTable = new ShapefileFeatureTable(shpPath);
            shapefileFeatureTable.loadAsync();

            shapefileFeatureTable.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    GeometryType geoType = shapefileFeatureTable.getGeometryType();
                    String name = shapefileFeatureTable.getTableName();
//                    Log.e(TAG, "run: " + shapefileFeatureTable.getPath());
//                    Log.e(TAG, "run: " + shapefileFeatureTable.getLoadStatus().toString());
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
                    // ???????????????????????????mapView
                    mMapView.getMap().getOperationalLayers().add(mFeatureLayer);

                }
            });

        } else {
            // ??????
            shpFile.mkdirs();
        }
        // ??????Shapefile?????????????????????
        // ???
        // SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.GREEN, 5);
        // ???
        // SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
        // ???
        // SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
        // ???????????????
        // SimpleRenderer renderer = new SimpleRenderer(fillSymbol);

        // SimpleRenderer renderer = new SimpleRenderer(simpleMarkerSymbol);

        // mFeatureLayer.setRenderer(renderer);
        //queryByIdentify();

    }

    /**
     * ??????shp??????mFeatureLayer
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
//                                Log.e(TAG, "run: " + attributes.toString());
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
//                                Log.d(TAG, "Selection #: " + counter + " Table name: " + feature.getFeatureTable().getFields().toString());
                                List<Field> fieldList = feature.getFeatureTable().getFields();
//                                Log.e(TAG, "onSingleTapConfirmed: " + feature.getAttributes().toString());
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
     * ??????shp??????1:selectFeaturesAsync????????????????????????
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

                                //????????????????????????
                                mFeatureLayer.selectFeature(feature);
                                Geometry geometry = feature.getGeometry();
                                mMapView.setViewpointGeometryAsync(geometry.getExtent());

                                //?????????????????????graphic????????????????????????
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
     * ?????? ??????????????????????????????????????????
     * ????????? SCAN KIT
     *
     * @param clickPoint
     */
    public void queryByPointloglat(com.esri.arcgisruntime.geometry.Point clickPoint) {
        int tolerance = 1;
        double mapTolerance = tolerance * mMapView.getUnitsPerDensityIndependentPixel();
        SpatialReference spatialReference = mMapView.getSpatialReference();
        Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance,
                clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, spatialReference);
//??????????????????????????????
        QueryParameters query = new QueryParameters();
        query.setGeometry(envelope);// ????????????????????????
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
                    Boolean test = SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.ISTEST, Boolean.class);
                    // 1 ?????????????????????????????? ??? 0 ??????????????????
                    if (!test) {
                        int counter = 0;
                        while (iterator.hasNext()) {
                            counter++;
                            mainViewModel.setSelectedFeature(counter);
                            Feature feature = iterator.next();
                            //TODO ??????????????????????????????????????????????????????????????????????????????????????????????????????
                            Map<String, Object> attributes = feature.getAttributes();
//                            Log.e(TAG, "run: " + attributes.toString());
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
                            map.put(AppConstance.LONGITUDE, String.valueOf(clickPoint.getX()));
                            map.put(AppConstance.LATIDUTE, String.valueOf(clickPoint.getY()));
                            mainViewModel.setMap(map);
//                            Log.e(TAG, "run: " + mainViewModel.getMap().toString());
                            for (String key : attributes.keySet()) {
                                Log.e("xyh" + key, String.valueOf(attributes.get(key)));
                            }
                            if (mainViewModel.getMap().size() > 0) {
                                /////////////////??????SCAN KIT ////////////////// ??????????????????
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                                            DEFAULT_VIEW);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.point_message), Toast.LENGTH_SHORT).show();
                            }
                            //????????????????????????
                            mFeatureLayer.selectFeature(feature);
                            Geometry geometry = feature.getGeometry();
                            mMapView.setViewpointGeometryAsync(geometry.getExtent());

                        }
                        if (counter == 0){
                            Toast.makeText(MainActivity.this, "??????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Map<String, String> map = new HashMap<>();
                        map.put(AppConstance.JYXZCNAME, "?????????");
                        map.put(AppConstance.CGQNAME, "?????????????????????");
                        map.put(AppConstance.DBH, "01");
                        map.put(AppConstance.XBH, "811");
                        map.put(AppConstance.LONGITUDE, "118.23904583");
                        map.put(AppConstance.LATIDUTE, "24.60177837");
                        mainViewModel.setMap(map);
                        /////////////////??????SCAN KIT ////////////////// ??????????????????
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
     * ??????shp??????2:queryFeaturesAsync(??????????????????)
     */
    public void queryByQueryFeaturesAsync() {
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                android.graphics.Point screenPoint = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
                com.esri.arcgisruntime.geometry.Point clickPoint = mMapView.screenToLocation(screenPoint);

                QueryParameters query = new QueryParameters();
                query.setGeometry(clickPoint);// ????????????????????????
                FeatureTable mTable = mFeatureLayer.getFeatureTable();//?????????????????????
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


                                //????????????????????????
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


    // %%%%%%%%%%%%    Android ??????????????? %%%%%%%%%%%%%%%%%
    private FloatingActionButton fabPests, fabLocation;
    Toast toast;


    ////////////////////////?????? SCAN KIT         /////////////////// ?????? ??????
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
        boolean haspermission = false;
        if (100 == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    haspermission = true;
                }
            }
            if (haspermission) {
                //????????????????????????????????????????????????????????????????????????????????????
                permissionDialog();
            } else {
                //????????????????????????????????????????????????
                AutoUpdater manager = new AutoUpdater(MainActivity.this);
                manager.CheckUpdate();
            }
        }
    }

    /**
     * ?????? SCAN ????????? ????????????
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //receive result after your activity finished scanning
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstance.STARTACTIVITY_MAINACTIVITY_PESTSACTIVITY && resultCode == RESULT_OK) {
            // SearchAddressInfo info = (SearchAddressInfo) data.getParcelableExtra("position");
            Pests pp = (Pests) data.getSerializableExtra("data");
//            Log.e(TAG, "MainActivity - onActivityResult: " + pp.toString());
            Pests p = pestsViewModel.findByLatLonAndUserIdAndStime(pp.getLatitude(),pp.getLongitude(),pp.getStime(),pp.getUserId(),pp.getQrcode());

            if (p != null){
//                Log.e(TAG, "MainActivity - onActivityResult: " + p.toString());
                Toast.makeText(this, "????????? ["+p.getCodeInt()+"] ?????????????????????", Toast.LENGTH_SHORT).show();
            }
//            mTvClockInAddress.setText(position);
        }
        if (requestCode == AppConstance.STARTACTIVITY_MAINACTIVITY_TRAPACTIVITY && resultCode == RESULT_OK) {
            // SearchAddressInfo info = (SearchAddressInfo) data.getParcelableExtra("position");
            Trap pp = (Trap) data.getSerializableExtra("data");
//            Log.e(TAG, "MainActivity - onActivityResult: " + pp.toString());
            Trap p = trapViewModel.findByLatLonAndUserIdAndStime(pp.getLatitude(),pp.getLongitude(),pp.getStime(),pp.getUserId(),pp.getQrcode());

            if (p != null){
//                Log.e(TAG, "MainActivity - onActivityResult: " + p.toString());
                Toast.makeText(this, "????????? ["+p.getCodeInt()+"] ?????????????????????", Toast.LENGTH_SHORT).show();
            }
//            mTvClockInAddress.setText(position);
        }
        // Obtain the return value of HmsScan from the value returned by the onActivityResult method by using ScanUtil.RESULT as the key value.
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK && data != null) {
            Object obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj instanceof HmsScan) {
                if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {
                    Toast.makeText(this, ((HmsScan) obj).getOriginalValue(), Toast.LENGTH_SHORT).show();
                    String qrcode = ((HmsScan) obj).getOriginalValue();
                    if (StrUtil.contains(qrcode,"qiter.com.cn")){
                        if (StrUtil.contains(qrcode,"trap")){
                            Intent intent = new Intent(MainActivity.this, TrapActivity.class);
                            PestsTrapModel trap = new PestsTrapModel();
                            String resultQrcode = StrUtil.subAfter(qrcode, "=", true);
                            trap.setQrcode(resultQrcode);
                            HashMap<String, String> map = (HashMap<String, String>) mainViewModel.getMap();
                            intent.putExtra(AppConstance.TRAPMODEL, trap);
                            intent.putExtra(AppConstance.FEATURE_ATTRIBUTE_MAP, map);
                            startActivityForResult(intent,AppConstance.STARTACTIVITY_MAINACTIVITY_TRAPACTIVITY);
                        } else {
                            Intent intent = new Intent(MainActivity.this, PestsActivity.class);
                            PestsControlModel pests = new PestsControlModel();
                            String resultQrcode = StrUtil.subAfter(qrcode, "=", true);
                            pests.setQrcode(resultQrcode);
                            HashMap<String, String> map = (HashMap<String, String>) mainViewModel.getMap();
                            intent.putExtra(AppConstance.PESTSMODEL, pests);
                            intent.putExtra(AppConstance.FEATURE_ATTRIBUTE_MAP, map);
                            startActivityForResult(intent,AppConstance.STARTACTIVITY_MAINACTIVITY_PESTSACTIVITY);
                        }
                    }else{
                        Toast.makeText(this, "??????????????????????????????!", Toast.LENGTH_LONG).show();
                    }
                }
                return;
            }
        }
    }

    ////////////////////////?????? SCAN KIT         /////////////////// ?????? ??????
    // Basemap basemap, aMapBasemap, googleBasemap, arcgisBasemap, osmBasemap, geoBasemap;
    private void initWorkManagerForAutoUpload(){
        Boolean test = SPUtil.builder(getApplication(), AppConstance.APP_SP).getData(AppConstance.AUTO_UPLOAD, Boolean.class);
        if (test == null || test == false){
            Log.e(TAG, "initWorkManagerForAutoUpload: ???????????????????????????");
            workmanager.cancelAllWork();
        }else{
            workmanager.cancelAllWork();

            // ??????
            Data data = new Data.Builder().putString(AppConstance.WORKMANAGER_KEY, "????????????").build();
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
                    .Builder(PestsWork.class,16, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .setInputData(data)
                    .build();

// ???????????????  ????????????????????? ENQUEUE????????? ?????????????????????????????????????????? SUCCESS     [???????????????????????????????????????SUCCESS]
            // ????????????
            workmanager.getWorkInfoByIdLiveData(periodicWorkRequest.getId())
                    .observe(MainActivity.this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            Log.d(AppConstance.TAG, "?????????" + workInfo.getState().name() + DateUtil.now()); // ENQUEEN   SUCCESS
                            if (workInfo.getState().isFinished()) {
                                Log.d(AppConstance.TAG, "?????????isFinished=true ????????????????????????????????????..."+ DateUtil.now());
                            }
                        }
                    });


            // ??????
            Data dataTrap = new Data.Builder().putString(AppConstance.WORKMANAGER_KEY, "????????????").build();
            Constraints constraintsTrap = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            PeriodicWorkRequest periodicWorkRequestTrap = new PeriodicWorkRequest
                    .Builder(TrapWork.class,16, TimeUnit.MINUTES)
                    .setConstraints(constraintsTrap)
                    .setInputData(dataTrap)
                    .build();
            workmanager.getWorkInfoByIdLiveData(periodicWorkRequestTrap.getId())
                    .observe(MainActivity.this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            Log.d(AppConstance.TAG, "?????????" + workInfo.getState().name()); // ENQUEEN   SUCCESS
                            if (workInfo.getState().isFinished()) {
                                Log.d(AppConstance.TAG, "?????????isFinished=true ????????????????????????????????????...");
                            }
                        }
                    });

            List<PeriodicWorkRequest> workRequests = new ArrayList<>();
            workRequests.add(periodicWorkRequest);
            workRequests.add(periodicWorkRequestTrap);
            workmanager.enqueue(workRequests);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workmanager = WorkManager.getInstance(this);
        LiveEventBus
                .get(AppConstance.WORK_NOTIFICATION_AUTO_UPLOAD, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Log.e(TAG, "onChanged: " + s);
                        //????????????????????????????????????
                        initWorkManagerForAutoUpload();
                    }
                });
//        SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).setData(AppConstance.ISTEST, 1);
        ArcGISRuntimeEnvironment.setLicense(AppConstance.API_KEY);
        settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        pestsViewModel = new ViewModelProvider(this).get(PestsViewModel.class);
        trapViewModel = new ViewModelProvider(this).get(TrapViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        meViewModel = new ViewModelProvider(this).get(MeViewModel.class);
        mMapView = findViewById(R.id.mapView);
        fabPests = findViewById(R.id.fab_pests);
        fabLocation = findViewById(R.id.fab_location);
//??????????????????????????????????????????????????????????????????????????????Location???????????????
        //?????????Location???????????????LocationManager??????????????????
        //??????LocationManager???????????????

        mGraphicsOverlay = addGraphicsOverlay(mMapView);
        initToolbarZoom();
        initFAB();
        // set the map to the map view

//        if (arcGISMap == null) {
            setMap();
//        }
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
            String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(MainActivity.this, requestPermissions, requestCode4Arcgis);
                //return;
            } else {
                // Report other unknown failure types to the user - for example, location services may not
                // be enabled on the device.
                String message = String.format("Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }

        });

        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        if (!mLocationDisplay.isStarted())
            mLocationDisplay.startAsync();

        showGeoDatabase();
        pestsViewModel.findAll().observe(this, new Observer<List<Pests>>() {
            @Override
            public void onChanged(List<Pests> pests) {
                // Log.e(TAG, "pestsViewModel.findAll().observe: " + pests.toString());
            }
        });
        /**
         * ????????????????????????????????????????????????
         */
        pestsViewModel.findAll(true).observe(this, new Observer<List<Pests>>() {
            @Override
            public void onChanged(List<Pests> pests) {
//                Log.e(TAG, "pestsViewModel.findAll().observe: " + pests.toString());
            }
        });
        pestsViewModel.findAll(false).observe(this, new Observer<List<Pests>>() {
            @Override
            public void onChanged(List<Pests> pests) {
//                if (pests != null && !pests.isEmpty())
//                    Log.e(TAG, "??????????????????????????????: " + pests.toString());
            }
        });
        LiveEventBus.get(AppConstance.WORK_NOTIFICATION_AUTO_UPLOAD).postDelay(AppConstance.OK,3000);
        //APP????????????
        checkUpdate();
    }


    private void setMap() {
        arcGISMap = new ArcGISMap();
        Boolean tiandi = SPUtil.builder(getApplication(), AppConstance.APP_SP).getData(AppConstance.TIANDIMAP, Boolean.class);
        if (tiandi) {
            Basemap basemap = new Basemap();
            basemap.setName(AppConstance.TIANDI_MAP);
            WebTiledLayer webTiledLayer = TianDiTuTiledLayerClass.CreateTianDiTuTiledLayer(TianDiTuTiledLayerClass.LayerType.TIANDITU_IMAGE_2000);
            webTiledLayer.loadAsync();
            WebTiledLayer webTiledLayer1 = TianDiTuTiledLayerClass.CreateTianDiTuTiledLayer(TianDiTuTiledLayerClass.LayerType.TIANDITU_IMAGE_2000_LABLE);
            webTiledLayer1.loadAsync();
            basemap.getBaseLayers().add(webTiledLayer);
            basemap.getBaseLayers().add(webTiledLayer1);
            arcGISMap.setBasemap(basemap);
        } else {
            Basemap arcgisBasemap = new Basemap();
            arcgisBasemap.setName(AppConstance.ARCGIS_MAP);
            String arcurl = "http://services.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer";
            ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer(arcurl);
            arcgisBasemap.getBaseLayers().add(arcGISTiledLayer);
            arcGISMap.setBasemap(arcgisBasemap);
        }

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
//            case R.id.menu_updateserver:
//                updateServer(false);
//                break;
            case R.id.menu_myjob:
                Intent inten = new Intent(this, MyJobActivity.class);
                startActivity(inten);
                break;
            case R.id.menu_trap:
                Intent trap = new Intent(this, TrayJobActivity.class);
                startActivity(trap);
                break;
            case R.id.menu_loadshp:
                showGeoDatabase();
                break;
//            case R.id.menu_update_againr:
//                updateServer(true);
//                break;
//            case R.id.menu_delete:
//
//                deletePestsByUpdate(true);
//                Basemap basemap = arcGISMap.getBasemap();
//                LayerList sic = basemap.getBaseLayers();
//                Log.e(TAG, "onOptionsItemSelected: " + sic.size());
//                LayerList si = mMapView.getMap().getOperationalLayers();
//                Log.e(TAG, "onOptionsItemSelected: " + si.size());
//                //   Toast.makeText(this, mMapView.getSpatialReference().getWKText() + " --- " + mMapView.getSpatialReference().toString(), Toast.LENGTH_LONG).show();
//                break;
            default:

        }
        return super.onOptionsItemSelected(item);
    }


    private void initFAB() {
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // setInitExtentMap();
                mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
                if (!mLocationDisplay.isStarted())
                    mLocationDisplay.startAsync();
//??????6.0??????????????????????????????????????????????????????
                LocationUtils.getInstance(getApplicationContext()).setAddressCallback(new LocationUtils.AddressCallback() {
                    @Override
                    public void onGetAddress(Address address) {
                        String countryName = address.getCountryName();//??????
                        String adminArea = address.getAdminArea();//???
                        String locality = address.getLocality();//???
                        String subLocality = address.getSubLocality();//???
                        String featureName = address.getFeatureName();//??????
                        Toast.makeText(getApplicationContext(), "????????????" + countryName + adminArea + locality + subLocality + featureName, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onGetLocation(double lat, double lng) {
                        Toast.makeText(getApplicationContext(), "????????????" + lat + ":" + lng, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        fabPests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean isSCAN = SPUtil.builder(getApplication(), AppConstance.APP_SP).getData(AppConstance.IS_SCAN, Boolean.class);
                if (isSCAN != null && isSCAN){
                    Boolean loadSHP = SPUtil.builder(getApplication(), AppConstance.APP_SP).getData(AppConstance.LOAD_SHP, Boolean.class);
                    if (loadSHP == null || loadSHP == false ){
                        Map<String, String> map = new HashMap<>();
                        map.put(AppConstance.JYXZCNAME, "???????????????");
                        map.put(AppConstance.CGQNAME, "???????????????");
                        map.put(AppConstance.DBH, "???????????????");
                        map.put(AppConstance.XBH, "???????????????");
                        map.put(AppConstance.LONGITUDE, "118.23904583");
                        map.put(AppConstance.LATIDUTE, "24.60177837");
                        mainViewModel.setMap(map);
                        /////////////////??????SCAN KIT ////////////////// ??????????????????
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                                    DEFAULT_VIEW);
                        }
                    }else {
                        if (mainViewModel.isLoadedShp()) {
                            //??????GPS??????????????????
                            mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
                            if (!mLocationDisplay.isStarted())
                                mLocationDisplay.startAsync();
                            LocationDataSource.Location arcgislocation = mLocationDisplay.getLocation();
                            if (arcgislocation != null) {
                                com.esri.arcgisruntime.geometry.Point point = arcgislocation.getPosition();
                                queryByPointloglat(point);
                            } else {
                                LocationUtils.getInstance(getApplicationContext()).setAddressCallback(new LocationUtils.AddressCallback() {
                                    @Override
                                    public void onGetAddress(Address address) {
                                        String countryName = address.getCountryName();//??????
                                        String adminArea = address.getAdminArea();//???
                                        String locality = address.getLocality();//???
                                        String subLocality = address.getSubLocality();//???
                                        String featureName = address.getFeatureName();//??????
                                        Toast.makeText(getApplicationContext(), "????????????" + countryName + adminArea + locality + subLocality + featureName, Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onGetLocation(double lat, double lng) {
                                        com.esri.arcgisruntime.geometry.Point point = new com.esri.arcgisruntime.geometry.Point(lng, lat);
                                        Toast.makeText(getApplicationContext(), "????????????" + lat + ":" + lng, Toast.LENGTH_LONG).show();
                                        queryByPointloglat(point);
                                    }
                                });
                            }

                            // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                            // AGConnectCrash.getInstance().testIt(MainActivity.this);

                        }else{
                            Toast.makeText(MainActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this, "???????????????????????????????????????", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(MainActivity.this, TrapActivity.class);
//                    HashMap<String, String> map = (HashMap<String, String>) mainViewModel.getMap();
//                    intent.putExtra(AppConstance.TRAPMODEL, trap);
//                    intent.putExtra(AppConstance.FEATURE_ATTRIBUTE_MAP, map);
//                    startActivityForResult(intent,AppConstance.STARTACTIVITY_MAINACTIVITY_TRAPACTIVITY);
                }
            }
        });

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
        setMap();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }

    /**
     * ???????????????
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //do something.
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }


    private void checkUpdate(){
        //????????????
        try {
            //6.0??????????????????
            if (Build.VERSION.SDK_INT >= 23) {
                String[] permissions = {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.INTERNET};
                List<String> permissionList = new ArrayList<>();
                for (int i = 0; i < permissions.length; i++) {
                    if (ActivityCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(permissions[i]);
                    }
                }
                if (permissionList.size() <= 0) {
                    //????????????????????????????????????????????????????????????
                    //????????????
                    AutoUpdater manager = new AutoUpdater(MainActivity.this);
                    manager.CheckUpdate();
                } else {
                    //????????????????????????
                    ActivityCompat.requestPermissions(this, permissions, 100);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "?????????????????????" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        boolean haspermission = false;
//        if (100 == requestCode) {
//            for (int i = 0; i < grantResults.length; i++) {
//                if (grantResults[i] == -1) {
//                    haspermission = true;
//                }
//            }
//            if (haspermission) {
//                //????????????????????????????????????????????????????????????????????????????????????
//                permissionDialog();
//            } else {
//                //????????????????????????????????????????????????
//                AutoUpdater manager = new AutoUpdater(MainActivity.this);
//                manager.CheckUpdate();
//            }
//        }
//    }

    AlertDialog alertDialog;

    //??????????????????????????????
    private void permissionDialog() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle("????????????")
                    .setMessage("????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????")
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                            Uri packageURI = Uri.parse("package:" + getPackageName());
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                        }
                    })
                    .create();
        }
        alertDialog.show();
    }

    private void cancelPermissionDialog() {
        alertDialog.cancel();
    }
}