package com.tur.bumerang.Global.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iamhabib.easy_preference.EasyPreference;
import com.tur.bumerang.Base.BaseActivity;
import com.tur.bumerang.Base.Common;
import com.tur.bumerang.Base.Constants;
import com.tur.bumerang.Base.ReqConst;
import com.tur.bumerang.Business.Activity.UserProfileBusinessActivity;
import com.tur.bumerang.Global.CustomeView.CustomMapView;
import com.tur.bumerang.Global.Fragment.SliderImageFragment;
import com.tur.bumerang.R;
import com.tur.bumerang.Standard.Activity.RentPageActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tur.bumerang.Global.Activity.DetailbikeActivity.GetStringArray;

public class DetailCarActivity extends BaseActivity implements OnMapReadyCallback {

    String userInfo;
    @BindView(R.id.btn_rent_car) Button editandmessage;
    @BindView(R.id.mapView) CustomMapView mapView;
    @BindView(R.id.detail_car_title) TextView detail_car_title;
    @BindView(R.id.detail_car_fuel) TextView detail_car_fuel;
    @BindView(R.id.detail_car_gear) TextView detail_car_gear;
    @BindView(R.id.detail_car_doornumber) TextView detail_car_doornumber;
    @BindView(R.id.detail_car_type) TextView detail_car_type;
    @BindView(R.id.detail_car_price) TextView detail_car_price;
    @BindView(R.id.detail_car_deposit) TextView detail_car_deposit;
    @BindView(R.id.detail_car_des) TextView detail_car_des;
    @BindView(R.id.detail_car_address) TextView detail_car_address;
    @BindView(R.id.detail_owner_avatar) ImageView detail_car_ownerimv;
    @BindView(R.id.detail_owner_name) TextView detail_car_ownername;
    @BindView(R.id.detail_owner_products) Button detail_owner_products;
    @BindView(R.id.deletePro) ImageView deletePro;

    GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailcar);

        ButterKnife.bind(this);

        detail_car_title.setText(Constants.itemModel.title);
        List<String> strPath = Constants.itemModel.image_url;
        String[]strPaths = GetStringArray((ArrayList<String>) strPath);

        SliderImageFragment sliderImageFragment = new SliderImageFragment(this, strPaths);
        getFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(R.animator.fragment_animation_fade_in, R.animator.fragment_animation_fade_out)
                .replace(R.id.viewPage, sliderImageFragment)
                .commit();

        detail_car_address.setText(Constants.itemModel.address);
        detail_car_fuel.setText(Constants.itemModel.fuel_type);
        detail_car_gear.setText(Constants.itemModel.gear_type);
        detail_car_doornumber.setText(String.valueOf(Constants.itemModel.door_number));
        detail_car_type.setText(Constants.itemModel.car_type);
        detail_car_price.setText(String.valueOf(Constants.itemModel.price) + "₺" + "\n" + Constants.itemModel.date_unit);
        detail_car_deposit.setText(Constants.itemModel.deposit);
        detail_car_des.setText(Constants.itemModel.description);
        Glide
                .with(this)
                .load(Constants.itemModel.owner_info.avatarUrl)
                .centerCrop()
                .placeholder(R.mipmap.ic_avatar)
                .into(detail_car_ownerimv);
        detail_car_ownername.setText(Constants.itemModel.owner_info.firstName + " " + Constants.itemModel.owner_info.lastName);

//        detail_car_ownername.setText(Constants.itemModel.owner_info.firstName);
//        detail_car_rating.setText(Constants.itemModel.score);

        //TODO;edit detail
        String owner_id = EasyPreference.with(this).getString("easyUserId", "");
        if(owner_id.equals(Constants.itemModel.owner_id)){
            editandmessage.setText("Düzenle");
            deletePro.setVisibility(View.VISIBLE);
            detail_car_des.setClickable(false);
            detail_car_des.setFocusable(false);
            detail_car_des.setFocusableInTouchMode(false);
            editandmessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editandmessage.getText().toString().equals("Düzenle")) {
                        detail_car_des.setFocusable(true);
                        detail_car_des.setFocusableInTouchMode(true);
                        detail_car_des.setClickable(true);
                        detail_car_des.requestFocus();

                        editandmessage.setText("Kayıt etmek");
                    } else if (editandmessage.getText().toString().equals("Kayıt etmek")) {
                        DatabaseReference des = FirebaseDatabase.getInstance().getReference().child("product").child(Constants.itemModel.id).child("description");
                        des.setValue(detail_car_des.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DetailCarActivity.this, "Başarıyı kaydedin!", Toast.LENGTH_SHORT).show();
                                detail_car_des.setFocusable(false);
                                detail_car_des.setFocusableInTouchMode(false);
                                detail_car_des.setClickable(false);
                                editandmessage.setText("Düzenle");
                            }
                        });
                    }

                }
            });
        }
        else{
            detail_car_des.setClickable(false);
            detail_car_des.setFocusable(false);
            detail_car_des.setFocusableInTouchMode(false);
            editandmessage.setText(R.string.alread_rent);
            editandmessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Constants.itemModel.rental_state != null && Constants.itemModel.rental_state.equals("Yes")) {
                        //send message
                    } else {
                        if (EasyPreference.with(DetailCarActivity.this).getInt("easyRegistStatus", 0) == 1) {
                            Intent intent = new Intent(DetailCarActivity.this, ChattingActivity.class);
                            intent.putExtra("owner_id", Constants.itemModel.owner_id);
                            startActivity(intent);
//            startActivityForResult(intent,1);
                        } else {
                            showToast("You can't rent product.\n Please register as user. ");
                            return;
                        }
                    }
                }
            });

        }

        //TODO; delete owner products from firebase;
        deletePro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.itemModel.owner_id != FirebaseAuth.getInstance().getCurrentUser().getUid()) {
                    showAlertDialog("You can't delete this product!");
                }
                else {
                    new AlertDialog.Builder(DetailCarActivity.this)
                            .setTitle("Delete entry")
                            .setMessage("Are you sure you want to delete this product?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    Log.v("product id ", String.valueOf(Constants.itemModel.id));

                                    DatabaseReference deleting = FirebaseDatabase.getInstance().getReference().child("product");
                                    deleting.child(String.valueOf(Constants.itemModel.id)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(DetailCarActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });


                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

            }
        });
        //TODO; goto owner profile page
        detail_owner_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfo = Constants.itemModel.owner_info.membership;
//                Intent intent=new Intent(DetailhomeActivity.this,UserProfileBusinessActivity.class);
//                intent.putExtra("owner_id", Constants.itemModel.owner_id);
//                startActivity(intent);
                if(userInfo.length() ==0) {
                    Intent intent = new Intent(DetailCarActivity.this, UserProfileActivity.class);
                    intent.putExtra("owner_id", Constants.itemModel.owner_id);
                    startActivity(intent);
                }else {
                    Intent intent =  new Intent(DetailCarActivity.this, UserProfileBusinessActivity.class);
                    intent.putExtra("owner_id", Constants.itemModel.owner_id);
                    startActivity( intent);
                }
            }
        });
//        detail_owner_products.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(DetailCarActivity.this, UserProfileBusinessActivity.class);
//                startActivity(intent);
//            }
//        });

        RatingBar ratingBar = new RatingBar(this);
        ratingBar.setRating(0);
//        ratingBar.setSpacing(10);
        ratingBar.setIsIndicator(false);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(0.5F);
//        ratingBar.setProgress(null);
 //       ratingBar.setProgressed(null);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
//        String owner_id = EasyPreference.with(this).getString("easyUserId", "");
//        if(owner_id.equals(Constants.itemModel.owner_id)){
//            findViewById(R.id.btn_rent_car).setVisibility(View.GONE);
////          findViewById(R.id.lyt_card).setVisibility(View.GONE);
//        }
    }

    @OnClick(R.id.btn_rent_car)
    void goRentPage(){
        if (EasyPreference.with(this).getInt("easyRegistStatus",0) == 1){
            Intent intent = new Intent(this, RentPageActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, ChattingActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng myLocation = new LatLng(Double.valueOf(Constants.itemModel.lat), Double.valueOf(Constants.itemModel.lng));

        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.addMarker(new MarkerOptions().position(myLocation));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 5));
        mGoogleMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
//
//    @OnClick(R.id.imv_detail_chat)
//    void goChatting(){
//        Intent intent = new Intent(this, ChattingActivity.class);
//        startActivity(intent);
//    }
//
//    @OnClick(R.id.imv_share_btn)
//    void onSharing(){
//        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//        sharingIntent.setType("text/plain");
//        String shareBody = "Here is the share content body";
//        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
//        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
//        startActivity(Intent.createChooser(sharingIntent, "Share via"));
//    }

}