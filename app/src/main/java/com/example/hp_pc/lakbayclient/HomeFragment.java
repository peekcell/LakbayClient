package com.example.hp_pc.lakbayclient;


import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.hp_pc.lakbayclient.models.PlaceInfo;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener {




    private static final String TAG = "MapsActivity";

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15.5f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );

    //widgets

    private Fragment searchtext;
    private ImageView ngps;
    //vars
    private Boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedProviderClient;
    private PlaceAutocompleteAdapter PlaceAutocompleteAdapter;
    private GoogleApiClient GoogleApiClient;
    private GoogleApiClient pGoogleApiClient;
    private PlaceInfo nPlace;
    private LatLng pickupLocation;
    private LatLng destinationLatLng;
    private LatLng loc;
    private GoogleMap nmap;
    Location LastLocation;
    LocationRequest LocationRequest;
    private Boolean requestBol = false;
    private Marker pickupMarker, destinationMarker;
    public DatabaseReference userdata;
    private float rideDistance, ridePrice;


    Button ncancel;
    Button nrequest, toPaypal;
    ImageView single, family, barkada, ic_payment_method;
    String requestType = "single";
    String total, pmethod;
    TextView cap, cash, price, driverStatus;
    RelativeLayout requestLayout, cancelLayout;

    private RatingBar mRatingBar;

    private String destination;

    private String firstName, lastName;
    private TextView name, phone, car;
    private ImageView userImage;



    int flag = 0;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);


//        getAssignedDriverInfo();
//
//        if (driverFound == true){
//            updateUIThreads();
//            getAssignedDriverInfo();
//        }
//        else {
//            updateUIThreads();
//            getAssignedDriverInfo();
//        }


//        searchtext = v.findViewById(R.id.place_autocomplete_fragment);
        ngps = v.findViewById(R.id.ic_gps);

        ncancel = v.findViewById(R.id.cancel);
        nrequest = v.findViewById(R.id.request);
        toPaypal = v.findViewById(R.id.toPaypal);

        driverStatus = v.findViewById(R.id.driverStatus);
        cap = v.findViewById(R.id.capacity);
        cash = v.findViewById(R.id.cash);
        price = v.findViewById(R.id.price);

        ic_payment_method = v.findViewById(R.id.ic_payment_method);

        single = v.findViewById(R.id.single);
        family = v.findViewById(R.id.family);
        barkada = v.findViewById(R.id.barkada);
        single.setSelected(true);

        name = v.findViewById(R.id.tvName);
        phone = v.findViewById(R.id.tvPhone);
        car = v.findViewById(R.id.tvCar);
        userImage = v.findViewById(R.id.userImage);
        mRatingBar = v.findViewById(R.id.ratingBar);
        destinationLatLng = new LatLng(0.0, 0.0);

        requestLayout = v.findViewById(R.id.switch1);
        cancelLayout = v.findViewById(R.id.switch2);

        Bitmap abitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.singlee)).getBitmap();
        Bitmap aimagerounded = Bitmap.createBitmap(abitmap.getWidth(), abitmap.getHeight(), abitmap.getConfig());
        Canvas acanvas = new Canvas(aimagerounded);
        Paint apaint = new Paint();
        apaint.setAntiAlias(true);
        apaint.setShader(new BitmapShader(abitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        acanvas.drawRoundRect(new RectF(0,0,abitmap.getWidth(), abitmap.getHeight()), 1000, 1000, apaint);
        single.setImageBitmap(aimagerounded);

        Bitmap bbitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.family)).getBitmap();
        Bitmap bimagerounded = Bitmap.createBitmap(bbitmap.getWidth(), bbitmap.getHeight(), bbitmap.getConfig());
        Canvas bcanvas = new Canvas(bimagerounded);
        Paint bpaint = new Paint();
        bpaint.setAntiAlias(true);
        bpaint.setShader(new BitmapShader(bbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        bcanvas.drawRoundRect(new RectF(0,0,bbitmap.getWidth(), bbitmap.getHeight()), 1000, 1000, bpaint);
        family.setImageBitmap(bimagerounded);

        Bitmap cbitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.barkada)).getBitmap();
        Bitmap cimagerounded = Bitmap.createBitmap(cbitmap.getWidth(), cbitmap.getHeight(), cbitmap.getConfig());
        Canvas ccanvas = new Canvas(cimagerounded);
        Paint cpaint = new Paint();
        cpaint.setAntiAlias(true);
        cpaint.setShader(new BitmapShader(cbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        ccanvas.drawRoundRect(new RectF(0,0,cbitmap.getWidth(), cbitmap.getHeight()), 1000, 1000, cpaint);
        barkada.setImageBitmap(cimagerounded);
        //        single.setSelected(true);

        single.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                //Set the button's appearance
                button.setSelected(!button.isSelected());
                if (button.isSelected()) {
                    //Handle selected state change
                    family.setSelected(!button.isSelected());
                    barkada.setSelected(!button.isSelected());

                    Location loc11 = new Location("");
                    loc11.setLatitude(LastLocation.getLatitude());
                    loc11.setLongitude(LastLocation.getLongitude());

                    Location loc22 = new Location("");
                    loc22.setLatitude(destinationLatLng.latitude);
                    loc22.setLongitude(destinationLatLng.longitude);

                    if((loc22.getLatitude() == 0) && ((loc22.getLatitude() == 0))) {
//                        Toast.makeText(getContext(), "no location po", Toast.LENGTH_SHORT).show();

                        ridePrice = 0;
                        total = String.valueOf(0);
                        price.setText("----------------");
                    } else {

//                        float rideDistance = loc11.distanceTo(loc22) / 1000;
//
//                        float basePrice = 70;
//                        float perKm = 10;
//                        ridePrice = ((rideDistance * perKm) + basePrice);
//                        total = String.format("%.2f", ridePrice);
//                        price.setText(String.valueOf(total));
//
//                        cap.setText("1-4");
//                        requestType = "single";

                        computeFare("singleRide");

//                    setSingle();
                    }

                } else {
                    //Handle de-select state change

//                    ridePrice = 0;
//                    total = String.valueOf(0);
//                    price.setText("Select destination first");

                }
            }
        });

        family.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                //Set the button's appearance
                button.setSelected(!button.isSelected());
                if (button.isSelected()) {
                    //Handle selected state change
                    single.setSelected(!button.isSelected());
                    barkada.setSelected(!button.isSelected());

                    Location loc11 = new Location("");
                    loc11.setLatitude(LastLocation.getLatitude());
                    loc11.setLongitude(LastLocation.getLongitude());

                    Location loc22 = new Location("");
                    loc22.setLatitude(destinationLatLng.latitude);
                    loc22.setLongitude(destinationLatLng.longitude);

                    if((loc22.getLatitude() == 0) && ((loc22.getLatitude() == 0))) {
//                        Toast.makeText(getContext(), "no location po", Toast.LENGTH_SHORT).show();

                        ridePrice = 0;
                        total = String.valueOf(0);
                        price.setText("----------------");
                    } else {
                        computeFare("familyRide");
                    }

//                    float rideDistance = loc11.distanceTo(loc22)/1000;
//
//                    float basePrice = 110;
//                    float perKm = 15;
//                    ridePrice = ((rideDistance*perKm)+basePrice);
//                    total = String.format("%.2f", ridePrice);
//                    price.setText(String.valueOf(total));
//
//                    cap.setText("1-6");
//                    requestType = "family";
                } else {
                    //Handle de-select state change
                }
            }
        });

        barkada.setOnClickListener(new View.OnClickListener() {

            public void onClick(View button) {
                //Set the button's appearance
                button.setSelected(!button.isSelected());
                if (button.isSelected()) {
                    //Handle selected state change
                    single.setSelected(!button.isSelected());
                    family.setSelected(!button.isSelected());

                    Location loc11 = new Location("");
                    loc11.setLatitude(LastLocation.getLatitude());
                    loc11.setLongitude(LastLocation.getLongitude());

                    Location loc22 = new Location("");
                    loc22.setLatitude(destinationLatLng.latitude);
                    loc22.setLongitude(destinationLatLng.longitude);

                    if((loc22.getLatitude() == 0) && ((loc22.getLatitude() == 0))) {
//                        Toast.makeText(getContext(), "no location po", Toast.LENGTH_SHORT).show();

                        ridePrice = 0;
                        total = String.valueOf(0);
                        price.setText("----------------");
                    } else {
                        computeFare("barkadaRide");
                    }

//                    float rideDistance = loc11.distanceTo(loc22)/1000;
//
//                    float basePrice = 160;
//                    float perKm = 30;
//                    ridePrice = ((rideDistance*perKm)+basePrice);
//                    total = String.format("%.2f", ridePrice);
//                    price.setText(String.valueOf(total));
//
//                    cap.setText("1-12");
//                    requestType = "barkada";
                } else {
                    //Handle de-select state change
                }
            }

        });


        nrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



//                requestType = "single";

                if (destinationMarker != null){
                    destinationMarker.remove();
                    erasePolylines();
                }

                    if (requestType == null){
                        return;
                    }


                    requestBol = true;

                    if(flag == 0 ) {
                        Toast.makeText(getContext(), "please select payment method", Toast.LENGTH_SHORT).show();
                    } else if(flag == 1) {
                        Toast.makeText(getContext(), "cash payment selected", Toast.LENGTH_SHORT).show();

                    } else if(flag == 2) {
                        Toast.makeText(getContext(), "paypal payment selected", Toast.LENGTH_SHORT).show();
                    }


                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference userdata = FirebaseDatabase.getInstance().getReference("clients_request");
                    GeoFire geoFire = new GeoFire(userdata);
                    geoFire.setLocation(userId, new GeoLocation(LastLocation.getLatitude(), LastLocation.getLongitude()));

                    pickupLocation = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
                    pickupMarker = nmap.addMarker(new MarkerOptions()
                            .position(pickupLocation)
                            .title("Pickup Here")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickup)));

                    driverStatus.setText("Getting your Driver...");

                    getClosestDriver();




            }
        });

        ncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestLayout.setVisibility(View.VISIBLE);
                        cancelLayout.setVisibility(View.GONE);
                    }
                });

                if (requestBol){
                   endRide();
                }

            }
        });

        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] pmethods  = {"Cash", "Paypal"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Pick payment method")
                        .setItems(pmethods, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(i == 0) {
                                    Toast.makeText(getContext(), "Cash", Toast.LENGTH_SHORT).show();
                                    cash.setText("Cash");
                                    pmethod = "Cash";
                                    flag = 1;
                                    ic_payment_method.setImageResource(R.drawable.ic_action_name);
                                } else if(i == 1) {
                                    Toast.makeText(getContext(), "Paypal", Toast.LENGTH_SHORT).show();
                                    cash.setText("Paypal");
                                    pmethod = "Paypal";
                                    flag = 2;
                                    ic_payment_method.setImageResource(R.drawable.paypal_logo);
                                }
                            }
                        });
                builder.create();
                builder.show();
            }
        });


        ngps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        toPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PaypalActivity.class);
                intent.putExtra("totalPrice", String.valueOf(total));
                startActivity(intent);
            }
        });



        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("PH")
                .build();

        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destination = place.getName().toString();
                destinationLatLng = place.getLatLng();

                loc = (new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude()));
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(loc);
                    builder.include(destinationLatLng);
                    LatLngBounds bounds = builder.build();
                    int width = getResources().getDisplayMetrics().widthPixels;
                    int padding = (int) (width*0.21);

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    nmap.animateCamera(cameraUpdate);
                    getRouteToMarker();
                    if (destinationMarker != null){
                        destinationMarker.remove();
                        erasePolylines();
                        destinationMarker = nmap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));
                    } else {
                        destinationMarker = nmap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));
                    }

                //COUNTRY FILTER
//                final AutocompletePrediction item = PlaceAutocompleteAdapter.getItem(pl);
//                final String placeId = item.getPlaceId();
//
//            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                    .getPlaceById(GoogleApiClient, placeId);
//                placeResult.setResultCallback(nUpdatePlaceDetailsCallback);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        getLocationPermission();

//        init();

        return v;
    }

//    private ResultCallback<PlaceBuffer> nUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(@NonNull PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()){
//                Log.d(TAG, "onResult: Place query  did not complete succesfully" + places.getStatus().toString());
//                places.release();
//                return;
//            }
//            final Place place = places.get(0);
//
//
//            try{
//                nPlace = new PlaceInfo();
//                nPlace.setName(place.getName().toString());
//                nPlace.setAddress(place.getAddress().toString());
//                nPlace.setId(place.getId());
//                nPlace.setRating(place.getRating());
//                nPlace.setPhoneNumber(place.getPhoneNumber().toString());
//                nPlace.setWebsiteuri(place.getWebsiteUri());
//
//                Log.d(TAG, "onResult: Place:" );
//            }catch (NullPointerException e){
//                Log.e(TAG, "onResult: NullPointerException" + e.getMessage());
//            }
//
//            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
//                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, nPlace.getName());
//
//            places.release();
//        }
//    };



/////////////////////////////////////////////////////////AUTO-COMPLETE LISTENER
//
//    private void init(){
//        Log.d(TAG, "init: initializing");
//
//        pGoogleApiClient = new GoogleApiClient
//                .Builder(getActivity())
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(getActivity(), this)
//                .build();
//
//        nsearchtext.setOnItemClickListener(nAutocompleteClickListener);
//
//        //COUNTRY FILTER
//        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
//                .setTypeFilter(Place.TYPE_COUNTRY)
//                .setCountry("PH")
//                .build();
//
//        PlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(),
//                pGoogleApiClient, LAT_LNG_BOUNDS, autocompleteFilter);
//
//        nsearchtext.setAdapter(PlaceAutocompleteAdapter);
//
//        nsearchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
//
//                    //execute our method for searching
//
//                    geoLocate();
//                    hideSoftKeyboard();
//                }
//
//                return false;
//            }
//        });


//        hideSoftKeyboard();
//    }
//
//    private void geoLocate(){
//        Log.d(TAG, "geoLocate: geolocating");
//
//        String searchString = nsearchtext.getText().toString();
//
//        Geocoder geocoder = new Geocoder(getContext());
//        List<Address> list = new ArrayList<>();
//        try{
//            list = geocoder.getFromLocationName(searchString, 1);
//        }catch (IOException e){
//            Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
//        }
//        if (list.size() > 0){
//            Address address = list.get(0);
//
//            Log.d(TAG, "geoLocate: found a location: " + address.toString());
//            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
//            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
//                    address.getAddressLine(0));
//        }
//    }
//
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting the Device Current Location");

        mFusedProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionGranted) {

                Task location = mFusedProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: found location!");
                            final Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");


                        } else {
                            Log.d(TAG, "onComplete: Current Location is Null");
                            Toast.makeText(getContext(), "Unable to get Current Location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: Security Exception" + e.getMessage());
        }
    }
//
//
//    /////////////////////////////////////////////////////////AUTO-COMPLETE LISTENER

    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;

    GeoQuery geoQuery;
    private void getClosestDriver(){
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("drivers_available");

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestBol){
                    DatabaseReference carType = FirebaseDatabase.getInstance().getReference().child("drivers").child(key);
                    carType.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (driverFound){
                                    return;
                                }
                                if (driverMap.get("car_type").equals(requestType)){
//                                    Object carrrr = driverMap.get("car_type").toString();
//                                    Toast.makeText(getContext(), "cartype db" + carrrr , Toast.LENGTH_SHORT).show();
                                    driverFound = true;
                                    driverFoundID = dataSnapshot.getKey();

                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("drivers").child(driverFoundID).child("client_request");
                                    String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap map = new HashMap();
                                    map.put("customerRideID", customerID);
                                    map.put("destination", destination);
                                    map.put("destinationLat", destinationLatLng.latitude);
                                    map.put("destinationLng", destinationLatLng.longitude);
                                    driverRef.updateChildren(map);

                                    getDriverLocation();
                                    getHasRideEnded();
                                    driverStatus.setText("Looking for Driver");


                                } else{
//                                    Toast.makeText(getContext(), "No Driver Available", Toast.LENGTH_SHORT).show();

                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound){
                    radius++;
//                    Toast.makeText(getContext(), "radius" + radius , Toast.LENGTH_SHORT).show();
                    if (radius == 6 && !driverFound){
                        Toast.makeText(getContext(), "No Driver Available", Toast.LENGTH_SHORT).show();

                    }else {
                        getClosestDriver();
                    }
                }else {
//                    Toast.makeText(getContext(), "Driver Found", Toast.LENGTH_SHORT).show();
                    driverStatus.setText("Driver Found");
                    if (driverFoundID != null){
                        updateUIThreads();
                    }

                    getAssignedDriverInfo();

                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    public void updateUIThreads(){
//        final String foundHim = driverFoundID;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                requestLayout.setVisibility(View.GONE);
                cancelLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getAssignedDriverInfo(){
        userdata = FirebaseDatabase.getInstance().getReference().child("drivers").child(driverFoundID);
        userdata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                    pBar.setVisibility(View.GONE);
//                    Info.setVisibility(View.VISIBLE);
                    if (map.get("user_firstname") != null && map.get("user_lastname") != null){
                        firstName = map.get("user_firstname").toString();
                        lastName = map.get("user_lastname").toString();
                        name.setText(firstName + " " + lastName);
                    }
                    if (map.get("user_mobile") != null){
                        phone.setText(map.get("user_mobile").toString());
                    }
                    if (map.get("profile_image_url") != null){
                        Glide.with(getActivity().getApplication()).load(map.get("profile_image_url").toString()).into(userImage);
                    }
                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingAvg;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if (ratingsTotal != 0 ){
                        ratingAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingAvg);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;
    private void getHasRideEnded(){
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("drivers").child(driverFoundID).child("client_request").child("customerRideID");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
//
                } else {
                    endRide();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestLayout.setVisibility(View.VISIBLE);
                            cancelLayout.setVisibility(View.GONE);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void endRide(){
        requestBol = false;
        geoQuery.removeAllListeners();
        driverLocationRef.removeEventListener(driverLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);

        if (driverFoundID != null){
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("drivers").child(driverFoundID).child("client_request");
            driverRef.removeValue();
            driverFoundID = null;
        }
        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference userdata = FirebaseDatabase.getInstance().getReference("clients_request");
        GeoFire geoFire = new GeoFire(userdata);
        geoFire.removeLocation(userId);

        if (pickupMarker != null){
            pickupMarker.remove();
        }
        if (driverMarker != null){
            driverMarker.remove();
        }
        nrequest.setText("CONFIRM LAKBAY");

//                    Info.setVisibility(View.GONE);
        name.setText("");
        phone.setText("");
        car.setText("");
        userImage.setImageResource(R.mipmap.default_user);


//        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
//        String requestID = historyRef.getKey();
//        HashMap map = new HashMap();
//        map.put("timestamp", getCurrentTimestamp());
//
//
//        historyRef.child(requestID).updateChildren(map);

//        String userId1 = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history").child(userId1);
//        String requestID = historyRef.push().getKey();
//
//        HashMap map = new HashMap();
//        map.put("payment_method", userId);
////        map.put("price", ridePrice);
//
//
//        historyRef.child(requestID).updateChildren(map);


    }


//    private Long getCurrentTimestamp() {
//        Long timestamp = System.currentTimeMillis()/1000;
//        return timestamp;
//    }

    private Marker driverMarker;
    private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;
    private void getDriverLocation(){
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("drivers_working").child(driverFoundID).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    driverStatus.setText("Driver Found");
                    pickupMarker.remove();
                    if (map.get(0) != null){
                        Object latObject = map.get(0);
                        locationLat = Double.parseDouble(latObject.toString());
//                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null){
                        Object lngObject = map.get(1);
                        locationLng = Double.parseDouble(lngObject.toString());
//                        locationLng = Double.parseDouble(map.get(0).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat, locationLng);
                    if(driverMarker != null){
                        driverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatLng.latitude);
                    loc2.setLongitude(driverLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);
//                    distance = distance/1;

                    if (distance < 100){
                        driverStatus.setText("Driver Arrived at Pick up Location");
                    } else {
                        driverStatus.setText("Driver Found " + String.valueOf(distance) + " meters away");
                    }


                    driverMarker = nmap.addMarker(new MarkerOptions()
                            .position(driverLatLng)
                            .title("Your Driver")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // single car method //

    private void selectTo() {
        ridePrice = 0;
        total = String.valueOf(0);
        price.setText("----------------");
    }

    private void computeFare(String rideType) {

        if(rideType.equals("singleRide")) {

            Location loc11 = new Location("");
            loc11.setLatitude(LastLocation.getLatitude());
            loc11.setLongitude(LastLocation.getLongitude());

            Location loc22 = new Location("");
            loc22.setLatitude(destinationLatLng.latitude);
            loc22.setLongitude(destinationLatLng.longitude);

            float rideDistance = loc11.distanceTo(loc22) / 1000;

            float basePrice = 70;
            float perKm = 10;
            ridePrice = ((rideDistance * perKm) + basePrice);
            float r = Math.round(ridePrice)+2*3;
            total = String.format("%.2f", r);
            price.setText(String.valueOf(total));

            cap.setText("1-4");
            requestType = "single";

        } else if(rideType.equals("barkadaRide")) {
            Location loc11 = new Location("");
            loc11.setLatitude(LastLocation.getLatitude());
            loc11.setLongitude(LastLocation.getLongitude());

            Location loc22 = new Location("");
            loc22.setLatitude(destinationLatLng.latitude);
            loc22.setLongitude(destinationLatLng.longitude);

            float rideDistance = loc11.distanceTo(loc22)/1000;
//
            float basePrice = 160;
            float perKm = 30;
            ridePrice = ((rideDistance*perKm)+basePrice);
            float r = Math.round(ridePrice)+2*3;
            total = String.format("%.2f", r);
            price.setText(String.valueOf(total));

            cap.setText("1-12");
            requestType = "barkada";
        } else if(rideType.equals("familyRide")) {
            Location loc11 = new Location("");
            loc11.setLatitude(LastLocation.getLatitude());
            loc11.setLongitude(LastLocation.getLongitude());

            Location loc22 = new Location("");
            loc22.setLatitude(destinationLatLng.latitude);
            loc22.setLongitude(destinationLatLng.longitude);

            float rideDistance = loc11.distanceTo(loc22)/1000;

            float basePrice = 110;
            float perKm = 15;
            ridePrice = ((rideDistance*perKm)+basePrice);
            float r = Math.round(ridePrice)+2*3;
            total = String.format("%.2f", r);
            price.setText(String.valueOf(total));

            cap.setText("1-6");
            requestType = "family";

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        selectTo();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getContext(), "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is Ready");
        nmap = googleMap;

        if (mLocationPermissionGranted) {
//            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               return;
            }
            buildGoogleApiClient();
            nmap.setMyLocationEnabled(true);
            nmap.getUiSettings().setMyLocationButtonEnabled(false);
//            nmap.getUiSettings().setCompassEnabled(true);

//            init();

        }
    }

    protected synchronized void buildGoogleApiClient(){
        GoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        GoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        LastLocation = location;

//        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()),
                DEFAULT_ZOOM,
                "My Location");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest = new LocationRequest();
        LocationRequest.setInterval(10000);
        LocationRequest.setFastestInterval(10000);
        LocationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getContext(), FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(GoogleApiClient, LocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveCamera: Moving the Camera to Lat" + latLng.latitude + ", lng" + latLng.longitude);
//        nmap.setMinZoomPreference(5.5f);
        nmap.setMaxZoomPreference(18.0f);
        nmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
//        map.clear();
//        if (!title.equals("My Location")){
//            MarkerOptions options = new MarkerOptions()
//                    .position(latLng)
//                    .title(title)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_end));
//
//
//            nmap.addMarker(options);
//        }
//        hideSoftKeyboard();
    }

    private void hideSoftKeyboard(){
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initMap(){
        Log.d(TAG, "initMap: Initializing Map");
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: Getting Location Permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            if (ContextCompat.checkSelfPermission(this.getActivity(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Called.");
        mLocationPermissionGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: Permission Failed!");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }

    }





     /*
    ---------------------------------google places api autocmplete suggestions
     */

//    private AdapterView.OnItemClickListener nAutocompleteClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            hideSoftKeyboard();
//
//            final AutocompletePrediction item = PlaceAutocompleteAdapter.getItem(i);
//            final String placeId = item.getPlaceId();
//
//            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                    .getPlaceById(GoogleApiClient, placeId);
//            placeResult.setResultCallback(nUpdatePlaceDetailsCallback);
//        }
//    };
//
//    private ResultCallback<PlaceBuffer> nUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
//        @Override
//        public void onResult(@NonNull PlaceBuffer places) {
//            if (!places.getStatus().isSuccess()){
//                Log.d(TAG, "onResult: Place query  did not complete succesfully" + places.getStatus().toString());
//                places.release();
//                return;
//            }
//            final Place place = places.get(0);
//
//
//                try{
//                    nPlace = new PlaceInfo();
//                    nPlace.setName(place.getName().toString());
//                    nPlace.setAddress(place.getAddress().toString());
//                    nPlace.setId(place.getId());
//                    nPlace.setRating(place.getRating());
//                    nPlace.setPhoneNumber(place.getPhoneNumber().toString());
//                    nPlace.setWebsiteuri(place.getWebsiteUri());
//
//                    Log.d(TAG, "onResult: Place:" );
//                }catch (NullPointerException e){
//                    Log.e(TAG, "onResult: NullPointerException" + e.getMessage());
//                }
//
//            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
//                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, nPlace.getName());
//
//            places.release();
//        }
//    };

    private void getRouteToMarker() {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(loc, destinationLatLng)
                .build();
        routing.execute();
    }



    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.colorAccent};

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getContext(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int i) {



//        if(polylines.size()>0) {
//            for (Polyline poly : polylines) {
//                poly.remove();
//            }
//        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int e = 0; e <route.size(); e++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(15 + e * 3);
            polyOptions.addAll(route.get(e).getPoints());
            Polyline polyline = nmap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getContext(),"Route "+ (e+1) +": distance - "+ route.get(e).getDistanceValue()+": duration - "+ route.get(e).getDurationValue(),Toast.LENGTH_SHORT).show();

            computeFare("singleRide");
        }

    }

    @Override
    public void onRoutingCancelled() {

    }

    private void erasePolylines(){
        for (Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
//        GoogleApiClient.stopAutoManage(getActivity());
//        GoogleApiClient.disconnect();
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (GoogleApiClient != null && GoogleApiClient.isConnected()) {
//            GoogleApiClient.stopAutoManage(getActivity());
//            GoogleApiClient.disconnect();
//        }
    }



}














